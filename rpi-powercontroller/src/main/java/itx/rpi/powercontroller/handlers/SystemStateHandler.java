package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.services.RPiService;

public class SystemStateHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final RPiService rPiService;

    public SystemStateHandler(ObjectMapper mapper, RPiService rPiService) {
        this.mapper = mapper;
        this.rPiService = rPiService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString requestMethod = exchange.getRequestMethod();
        if ("GET".equals(requestMethod.toString())) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
            exchange.setStatusCode(200);
            exchange.getResponseSender().send(mapper.writeValueAsString(rPiService.getSystemState()));
        } else {
            exchange.setStatusCode(405);
        }
    }

}
