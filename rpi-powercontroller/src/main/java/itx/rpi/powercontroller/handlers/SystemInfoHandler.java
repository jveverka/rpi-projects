package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import itx.rpi.powercontroller.config.Configuration;
import itx.rpi.powercontroller.dto.SystemInfo;

public class SystemInfoHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final Configuration configuration;

    public SystemInfoHandler(ObjectMapper mapper, Configuration configuration) {
        this.mapper = mapper;
        this.configuration = configuration;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
        SystemInfo systemInfo = new SystemInfo(configuration.getId(), "power-controller",
                configuration.getName(), "1.0.0", configuration.isHardware());
        exchange.getResponseSender().send(mapper.writeValueAsString(systemInfo));
    }

}
