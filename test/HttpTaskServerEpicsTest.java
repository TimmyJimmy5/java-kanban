import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import service.http.HttpTaskServer;
import utility.DurationAdapter;
import utility.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerEpicsTest {
    Managers managers = new Managers();
    TaskManager tm = managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(tm);
    protected Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW);
        String epicJson = gson.toJson(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode(), "Возвращаемый статус не 201.");

        List<Epic> epicsFromManager = tm.getAllEpics();

        Assertions.assertNotNull(epicsFromManager, "Epic не возвращаются");
        Assertions.assertEquals(1, epicsFromManager.size(), "Некорректное количество Epic");
        Assertions.assertEquals("Построить зиккурат.", epicsFromManager.getFirst().getName(), "Некорректное имя Epic");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW);
        tm.createEpic(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Возвращаемый статус не 200.");

        Task testEpic2 = gson.fromJson(response.body(), Task.class);

        Assertions.assertEquals(epic1.getName(), testEpic2.getName(), "Имена не совпадают.");
        Assertions.assertEquals(epic1.getDescription(), testEpic2.getDescription(), "Описание не совпадает.");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW);
        tm.createEpic(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .DELETE().header("Content-Type", "application/json").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Возвращаемый статус не 200.");

        List<Epic> epicsFromManager = tm.getAllEpics();

        Assertions.assertNull(epicsFromManager, "Epic не была удалена.");
    }
}
