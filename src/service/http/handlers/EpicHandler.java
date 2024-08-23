package service.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import model.Epic;
import service.TaskManager;
import service.http.HttpMethods;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
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
                    if (request.length == 3 && request[1].equals("epics")) {
                        int id = Integer.parseInt(request[2]);
                        Epic epic = getTaskManager().searchEpicById(id);
                        if (epic == null) {
                            sendNotFound(exchange);
                            break;
                        }
                        sendText(exchange, getGson().toJson(epic), 200);
                        break;
                    } else {
                        List<Epic> epics = getTaskManager().getAllEpics();
                        if (epics == null) {
                            sendNotFound(exchange);
                            break;
                        }
                        sendText(exchange, getGson().toJson(epics), 200);
                        break;
                    }
                case POST:
                    Epic epic = getGson().fromJson(new InputStreamReader(exchange.getRequestBody(),
                            defaultCharset), Epic.class);
                    System.out.println(epic);
                    if (getTaskManager().getAllEpics() == null) {
                        getTaskManager().createEpic(epic);
                        sendText(exchange, "", 201);
                        break;
                    } else if (request.length == 2 && request[1].equals("epics")) {
                        getTaskManager().createEpic(epic);
                        sendText(exchange, "", 201);
                        break;
                    } else {
                        getTaskManager().changeEpic(epic.getId(), epic);
                        sendText(exchange, "", 201);
                        break;
                    }
                case DELETE:
                    int id = Integer.parseInt(request[2]);
                    getTaskManager().removeEpicById(id);
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
