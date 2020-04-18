package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.dto.JobId;
import itx.rpi.powercontroller.dto.TaskId;
import itx.rpi.powercontroller.dto.TaskInfo;
import itx.rpi.powercontroller.services.TaskManagerService;

import java.io.InputStream;
import java.util.Optional;

public class SubmitTaskHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final TaskManagerService taskManagerService;

    public SubmitTaskHandler(ObjectMapper mapper, TaskManagerService taskManagerService) {
        this.mapper = mapper;
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString requestMethod = exchange.getRequestMethod();
        if (HandlerUtils.METHOD_PUT.equals(requestMethod.toString())) {
            exchange.startBlocking();
            InputStream is = exchange.getInputStream();
            JobId jobId = mapper.readValue(is, JobId.class);
            Optional<TaskId> taskInfo = taskManagerService.submitTask(jobId);
            if (taskInfo.isPresent()) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
                exchange.getResponseSender().send(mapper.writeValueAsString(taskInfo.get()));
                exchange.setStatusCode(200);
            } else {
                exchange.setStatusCode(404);
            }
        } else {
            exchange.setStatusCode(405);
        }
    }
}
