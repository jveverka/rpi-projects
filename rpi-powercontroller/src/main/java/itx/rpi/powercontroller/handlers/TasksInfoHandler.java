package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.TaskManagerService;

import java.util.Collection;

public class TasksInfoHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final TaskManagerService taskManagerService;

    public TasksInfoHandler(ObjectMapper mapper, TaskManagerService taskManagerService) {
        this.mapper = mapper;
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString requestMethod = exchange.getRequestMethod();
        if (HandlerUtils.METHOD_GET.equals(requestMethod.toString())) {
            Collection<TaskInfo> tasks = taskManagerService.getTasks();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
            exchange.setStatusCode(200);
            exchange.getResponseSender().send(mapper.writeValueAsString(tasks));
        } else {
            exchange.setStatusCode(405);
        }
    }

}
