package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.dto.TaskFilter;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.AAService;
import itx.rpi.powercontroller.services.TaskManagerService;

import java.io.InputStream;
import java.util.Collection;

public class TasksInfoHandler implements HttpHandler {

    private final AAService aaService;
    private final ObjectMapper mapper;
    private final TaskManagerService taskManagerService;

    public TasksInfoHandler(ObjectMapper mapper, AAService aaService, TaskManagerService taskManagerService) {
        this.aaService = aaService;
        this.mapper = mapper;
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (!HandlerUtils.validateRequest(aaService, exchange)) {
            exchange.setStatusCode(HandlerUtils.FORBIDDEN);
            return;
        }
        HttpString requestMethod = exchange.getRequestMethod();
        if (HandlerUtils.METHOD_GET.equals(requestMethod.toString())) {
            Collection<TaskInfo> tasks = taskManagerService.getTasks();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
            exchange.setStatusCode(HandlerUtils.OK);
            exchange.getResponseSender().send(mapper.writeValueAsString(tasks));
        } else if (HandlerUtils.METHOD_PUT.equals(requestMethod.toString())) {
            exchange.startBlocking();
            InputStream is = exchange.getInputStream();
            TaskFilter taskFilter = mapper.readValue(is, TaskFilter.class);
            Collection<TaskInfo> tasks = taskManagerService.getTasks(taskFilter);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
            exchange.setStatusCode(HandlerUtils.OK);
            exchange.getResponseSender().send(mapper.writeValueAsString(tasks));
        } else {
            exchange.setStatusCode(HandlerUtils.METHOD_NOT_ALLOWED);
        }
    }

}
