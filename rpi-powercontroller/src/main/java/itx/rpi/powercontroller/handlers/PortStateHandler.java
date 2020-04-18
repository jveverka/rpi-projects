package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.dto.SetPortRequest;
import itx.rpi.powercontroller.services.RPiService;

import java.io.InputStream;
import java.util.Optional;

public class PortStateHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final RPiService rPiService;

    public PortStateHandler(ObjectMapper mapper, RPiService rPiService) {
        this.mapper = mapper;
        this.rPiService = rPiService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString requestMethod = exchange.getRequestMethod();
        if (HandlerUtils.METHOD_PUT.equals(requestMethod.toString())) {
            exchange.startBlocking();
            InputStream is = exchange.getInputStream();
            SetPortRequest setPortRequest = mapper.readValue(is, SetPortRequest.class);
            Optional<Boolean> result = rPiService.setPortState(setPortRequest.getPort(), setPortRequest.getState());
            if (result.isPresent()) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
                exchange.setStatusCode(200);
            } else {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
                exchange.setStatusCode(400);
            }
        } else {
            exchange.setStatusCode(405);
        }
    }

}
