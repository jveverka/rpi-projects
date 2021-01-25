package one.microproject.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import one.microproject.rpi.powercontroller.services.AAService;
import one.microproject.rpi.powercontroller.services.RPiService;

public class MeasurementsHandler implements HttpHandler {

    private final AAService aaService;
    private final ObjectMapper mapper;
    private final RPiService rPiService;

    public MeasurementsHandler(ObjectMapper mapper, AAService aaService, RPiService rPiService) {
        this.aaService = aaService;
        this.mapper = mapper;
        this.rPiService = rPiService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (!HandlerUtils.validateRequest(aaService, exchange)) {
            exchange.setStatusCode(HandlerUtils.FORBIDDEN);
            return;
        }
        HttpString requestMethod = exchange.getRequestMethod();
        if (HandlerUtils.METHOD_GET.equals(requestMethod.toString())) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
            exchange.setStatusCode(HandlerUtils.OK);
            exchange.getResponseSender().send(mapper.writeValueAsString(rPiService.getMeasurements()));
        } else {
            exchange.setStatusCode(HandlerUtils.METHOD_NOT_ALLOWED);
        }
    }

}
