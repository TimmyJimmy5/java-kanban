import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.Managers;
import service.TaskManager;
import service.http.HttpTaskServer;
import utility.DurationAdapter;
import utility.LocalDateTimeAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerSubtasksTest {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW);
        tm.createEpic(epic1);
        Subtask subtask1 = new Subtask("Нужно больше золота.", "Построить рудники.", TaskStatus.NEW, 0, 30, "12:00 23.07.2024");
        String subtaskJson = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode(), "Возвращаемый код не 201.");

        List<Subtask> subtasksFromManager = tm.getAllSubtasks();

        Assertions.assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Нужно больше золота.", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW);
        tm.createEpic(epic1);
        Subtask subtask1 = new Subtask("Нужно больше золота.", "Построить рудники.", TaskStatus.NEW, 0, 30, "12:00 23.07.2024");
        tm.createSubtask(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Возвращаемый код не 200.");
        Subtask subtask2 = gson.fromJson(response.body(), Subtask.class);
        Assertions.assertEquals(subtask1.getName(), subtask2.getName(), "Имена не совпадают.");
        Assertions.assertEquals(subtask1.getDescription(), subtask2.getDescription(), "Описание не совпадает.");
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW);
        tm.createEpic(epic1);
        Subtask subtask1 = new Subtask("Нужно больше золота.", "Построить рудники.", TaskStatus.NEW, 0, 30, "12:00 23.07.2024");
        tm.createSubtask(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .DELETE().header("Content-Type", "application/json").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Возвращаемый код не 200.");
        List<Subtask> subtasksFromManager = tm.getAllSubtasks();

        Assertions.assertNull(subtasksFromManager, "Задача не была удалена.");
    }
}
