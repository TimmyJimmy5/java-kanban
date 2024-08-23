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

public class HttpTaskServerTasksTest {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task testTask1 = new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW, 30, "12:00 22.07.2024");
        String taskJson = gson.toJson(testTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode(), "Возвращаемый статус не 201.");

        List<Task> tasksFromManager = tm.getAllTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Покормить кота.", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task testTask1 = new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW, 30, "12:00 22.07.2024");
        tm.createTask(testTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Возвращаемый статус не 200.");

        Task testTask2 = gson.fromJson(response.body(), Task.class);

        Assertions.assertEquals(testTask1.getName(), testTask2.getName(), "Имена не совпадают.");
        Assertions.assertEquals(testTask1.getDescription(), testTask2.getDescription(), "Описание не совпадает.");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task testTask1 = new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW, 30, "12:00 22.07.2024");
        tm.createTask(testTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .DELETE().header("Content-Type", "application/json").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Возвращаемый статус не 200.");

        List<Task> tasksFromManager = tm.getAllTasks();

        Assertions.assertNull(tasksFromManager, "Задача не была удалена.");
    }
}
