package one.microproject.rpi.powercontroller.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import one.microproject.rpi.powercontroller.services.AAService;
import one.microproject.rpi.powercontroller.services.TaskManagerService;


public class CancelAllTasksHandler implements HttpHandler {

    private final AAService aaService;
    private final TaskManagerService taskManagerService;

    public CancelAllTasksHandler(AAService aaService, TaskManagerService taskManagerService) {
        this.aaService = aaService;
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
            taskManagerService.kilAllTasks();
            exchange.setStatusCode(HandlerUtils.OK);
        } else {
            exchange.setStatusCode(HandlerUtils.METHOD_NOT_ALLOWED);
        }
    }
}
