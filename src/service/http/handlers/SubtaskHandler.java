package service.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import model.Subtask;
import service.TaskManager;
import service.http.HttpMethods;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
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
                    if (request.length == 3 && request[1].equals("subtasks")) {
                        int id = Integer.parseInt(request[2]);
                        Subtask subtask = getTaskManager().searchSubtaskById(id);
                        if (subtask == null) {
                            sendNotFound(exchange);
                            break;
                        }
                        sendText(exchange, getGson().toJson(subtask), 200);
                        break;
                    } else {
                        List<Subtask> subtasks = getTaskManager().getAllSubtasks();
                        if (subtasks == null) {
                            sendNotFound(exchange);
                            break;
                        }
                        sendText(exchange, getGson().toJson(subtasks), 200);
                        break;
                    }
                case POST:
                    Subtask newSubtask = getGson().fromJson(new InputStreamReader(exchange.getRequestBody(),
                            defaultCharset), Subtask.class);
                    if (getTaskManager().getAllSubtasks() == null) {
                        getTaskManager().createSubtask(newSubtask);
                        sendText(exchange, "", 201);
                        break;
                    } else if (request.length == 2 && request[1].equals("subtasks")) {
                        getTaskManager().createSubtask(newSubtask);
                        sendText(exchange, "", 201);
                        break;
                    } else {
                        getTaskManager().changeSubtask(newSubtask.getId(), newSubtask);
                        sendText(exchange, "", 201);
                        break;
                    }
                case DELETE:
                    int id = Integer.parseInt(request[2]);
                    getTaskManager().removeSubtaskById(id);
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
