package one.microproject.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import one.microproject.rpi.powercontroller.dto.SetPortRequest;
import one.microproject.rpi.powercontroller.services.AAService;
import one.microproject.rpi.powercontroller.services.RPiService;

import java.io.InputStream;
import java.util.Optional;

public class PortStateHandler implements HttpHandler {

    private final AAService aaService;
    private final ObjectMapper mapper;
    private final RPiService rPiService;

    public PortStateHandler(ObjectMapper mapper, AAService aaService, RPiService rPiService) {
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
        if (HandlerUtils.METHOD_PUT.equals(requestMethod.toString())) {
            exchange.startBlocking();
            InputStream is = exchange.getInputStream();
            SetPortRequest setPortRequest = mapper.readValue(is, SetPortRequest.class);
            Optional<Boolean> result = rPiService.setPortState(setPortRequest.getPort(), setPortRequest.getState());
            if (result.isPresent()) {
                exchange.setStatusCode(HandlerUtils.OK);
            } else {
                exchange.setStatusCode(HandlerUtils.NOT_FOUND);
            }
        } else {
            exchange.setStatusCode(HandlerUtils.METHOD_NOT_ALLOWED);
        }
    }

}
