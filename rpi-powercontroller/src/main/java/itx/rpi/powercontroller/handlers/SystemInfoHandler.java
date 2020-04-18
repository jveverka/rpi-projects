package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import itx.rpi.powercontroller.services.SystemInfoService;

public class SystemInfoHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final SystemInfoService systemInfoService;

    public SystemInfoHandler(ObjectMapper mapper, SystemInfoService systemInfoService) {
        this.mapper = mapper;
        this.systemInfoService = systemInfoService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
        exchange.getResponseSender().send(mapper.writeValueAsString(systemInfoService.getSystemInfo()));
    }

}
