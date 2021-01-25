package one.microproject.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import one.microproject.rpi.powercontroller.dto.CancelledTaskInfo;
import one.microproject.rpi.powercontroller.dto.TaskId;
import one.microproject.rpi.powercontroller.services.AAService;
import one.microproject.rpi.powercontroller.services.TaskManagerService;

import java.io.InputStream;
import java.util.Optional;

public class CancelTaskHandler implements HttpHandler {

    private final AAService aaService;
    private final ObjectMapper mapper;
    private final TaskManagerService taskManagerService;

    public CancelTaskHandler(ObjectMapper mapper, AAService aaService, TaskManagerService taskManagerService) {
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
        if (HandlerUtils.METHOD_PUT.equals(requestMethod.toString())) {
            exchange.startBlocking();
            InputStream is = exchange.getInputStream();
            TaskId taskId = mapper.readValue(is, TaskId.class);
            Optional<CancelledTaskInfo> result = taskManagerService.cancelTask(taskId);
            if (result.isPresent()) {
                exchange.setStatusCode(HandlerUtils.OK);
                exchange.getResponseSender().send(mapper.writeValueAsString(result.get()));
            } else {
                exchange.setStatusCode(HandlerUtils.NOT_FOUND);
            }
        } else {
            exchange.setStatusCode(HandlerUtils.METHOD_NOT_ALLOWED);
        }
    }
}
