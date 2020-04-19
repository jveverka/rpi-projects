package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.services.AAService;
import itx.rpi.powercontroller.services.TaskManagerService;

import java.io.InputStream;
import java.util.Optional;

public class SubmitTaskHandler implements HttpHandler {

    private final AAService aaService;
    private final ObjectMapper mapper;
    private final TaskManagerService taskManagerService;

    public SubmitTaskHandler(ObjectMapper mapper, AAService aaService, TaskManagerService taskManagerService) {
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
            JobId jobId = mapper.readValue(is, JobId.class);
            Optional<TaskId> taskInfo = taskManagerService.submitTask(jobId);
            if (taskInfo.isPresent()) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
                exchange.setStatusCode(HandlerUtils.OK);
                exchange.getResponseSender().send(mapper.writeValueAsString(taskInfo.get()));
            } else {
                exchange.setStatusCode(HandlerUtils.NOT_FOUND);
            }
        } else {
            exchange.setStatusCode(HandlerUtils.METHOD_NOT_ALLOWED);
        }
    }
}
