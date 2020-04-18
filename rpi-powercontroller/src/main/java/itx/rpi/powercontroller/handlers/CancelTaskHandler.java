package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.services.TaskManagerService;

import java.io.InputStream;

public class CancelTaskHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final TaskManagerService taskManagerService;

    public CancelTaskHandler(ObjectMapper mapper, TaskManagerService taskManagerService) {
        this.mapper = mapper;
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString requestMethod = exchange.getRequestMethod();
        if (HandlerUtils.METHOD_PUT.equals(requestMethod.toString())) {
            exchange.startBlocking();
            InputStream is = exchange.getInputStream();
            TaskId taskId = mapper.readValue(is, TaskId.class);
            boolean result = taskManagerService.cancelTask(taskId);
            if (result) {
                exchange.setStatusCode(200);
            } else {
                exchange.setStatusCode(404);
            }
        } else {
            exchange.setStatusCode(405);
        }
    }
}
