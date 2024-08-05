package service.http;

import com.sun.net.httpserver.HttpServer;
import exceptions.ManagerSaveException;

import model.Task;
import model.TaskStatus;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;
import service.http.handlers.EpicHandler;
import service.http.handlers.HistoryHandler;
import service.http.handlers.SubtaskHandler;
import service.http.handlers.TaskHandler;
import service.http.handlers.PrioritizedHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final String TASKS_PATH = "/tasks";
    private static final String SUBTASKS_PATH = "/subtasks";
    private static final String EPICS_PATH = "/epics";
    private static final String HISTORY_PATH = "/history";
    private static final String PRIORITIZED_PATH = "/prioritized";
    private HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws ManagerSaveException, IOException {
        Managers managers = new Managers();
        File file = new File("./src/service/TaskSaveFile1.csv");
        TaskManager tm = managers.getDefaultFileBackedTaskManagerFromFile(file);
        HttpTaskServer httpTaskServer = new HttpTaskServer(tm);

        httpTaskServer.start();

        Task testTask1 = new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW, 30, "12:00 22.07.2024");
        Task testTask2 = new Task("Покормить собаку.", "Корм находится на средней полке", TaskStatus.NEW,  30, "12:00 24.07.2024");
        Task testTask3 = new Task("Покормить домашнего медведя.", "Корм находится на нижней полке", TaskStatus.NEW,  30, "12:00 26.07.2024");
        tm.createTask(testTask1);
        tm.createTask(testTask2);
        tm.createTask(testTask3);
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext(TASKS_PATH, new TaskHandler(taskManager));
        httpServer.createContext(SUBTASKS_PATH, new SubtaskHandler(taskManager));
        httpServer.createContext(EPICS_PATH, new EpicHandler(taskManager));
        httpServer.createContext(HISTORY_PATH, new HistoryHandler(taskManager));
        httpServer.createContext(PRIORITIZED_PATH, new PrioritizedHandler(taskManager));

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер на " + PORT + " порту остановлен!");
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }
}
