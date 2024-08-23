import com.google.gson.GsonBuilder;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;
import service.http.HttpTaskServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import utility.DurationAdapter;
import utility.LocalDateTimeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpTaskServerTest {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Task testTask1 = new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW, 30, "12:00 22.07.2024");
        Task testTask2 = new Task("Покормить собаку.", "Корм находится на средней полке", TaskStatus.NEW, 30, "12:00 24.07.2024");
        Task testTask3 = new Task("Покормить домашнего медведя.", "Корм находится на нижней полке", TaskStatus.NEW, 30, "12:00 26.07.2024");
        tm.createTask(testTask1);
        tm.createTask(testTask2);
        tm.createTask(testTask3);
        tm.searchTaskById(1);
        tm.searchTaskById(2);
        List<Task> assertionHistory = new ArrayList<>(Arrays.asList(testTask2, testTask3));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Возвращаемый код не 200.");

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> historyFromResponse = gson.fromJson(response.body(), taskListType);

        Assertions.assertEquals(assertionHistory, historyFromResponse, "Ответ не равен референсному списку.");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task testTask1 = new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW, 30, "12:00 22.07.2024");
        Task testTask2 = new Task("Покормить собаку.", "Корм находится на средней полке", TaskStatus.NEW, 30, "12:00 24.07.2024");
        Task testTask3 = new Task("Покормить домашнего медведя.", "Корм находится на нижней полке", TaskStatus.NEW, 30, "12:00 26.07.2024");
        tm.createTask(testTask1);
        tm.createTask(testTask2);
        tm.createTask(testTask3);

        List<Task> assertList = new ArrayList<>(Arrays.asList(testTask1, testTask2, testTask3));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Возвращаемый код не 200.");

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> prioritizedFromResponse = gson.fromJson(response.body(), taskListType);

        Assertions.assertEquals(assertList, prioritizedFromResponse, "Ответ не равен референсному списку.");
    }
}
