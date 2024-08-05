package service.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import service.TaskManager;
import service.http.HttpMethods;
import model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] request = path.split("/");

            switch (HttpMethods.valueOf(method)) {
                case GET:
                    if (request.length == 3 && request[1].equals("tasks")) {
                        int id = Integer.parseInt(request[2]);
                        Task task = getTaskManager().searchTaskById(id);
                        if (task == null) {
                            sendNotFound(exchange);
                            break;
                        }
                        sendText(exchange, getGson().toJson(task), 200);
                        break;
                    } else {
                        List<Task> tasks = getTaskManager().getAllTasks();
                        if (tasks == null) {
                            sendNotFound(exchange);
                            break;
                        }
                        sendText(exchange, getGson().toJson(tasks), 200);
                        break;
                    }
                case POST:
                    Task newTask = getGson().fromJson(new InputStreamReader(exchange.getRequestBody(),
                            DEFAULT_CHARSET), Task.class);
                    if (getTaskManager().getAllTasks() == null) {
                        getTaskManager().createTask(newTask);
                        sendText(exchange, "", 201);
                        break;
                    } else if (request.length == 2 && request[1].equals("tasks")) {
                        getTaskManager().createTask(newTask);
                        sendText(exchange, "", 201);
                        break;
                    } else {
                        getTaskManager().changeTask(newTask.getId(), newTask);
                        sendText(exchange, "", 201);
                        break;
                    }
                case DELETE:
                    int id = Integer.parseInt(request[2]);
                    getTaskManager().removeTaskById(id);
                    sendText(exchange, "", 200);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "400 - Invalid JSON", 400);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }
}
