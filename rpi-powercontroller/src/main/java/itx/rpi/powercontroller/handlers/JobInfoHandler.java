package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.config.ActionConfiguration;
import itx.rpi.powercontroller.dto.ActionInfo;
import itx.rpi.powercontroller.dto.JobInfo;
import itx.rpi.powercontroller.services.AAService;
import itx.rpi.powercontroller.services.TaskManagerService;
import itx.rpi.powercontroller.services.jobs.Job;

import java.util.ArrayList;
import java.util.Collection;

public class JobInfoHandler implements HttpHandler {

    private final AAService aaService;
    private final ObjectMapper mapper;
    private final TaskManagerService taskManagerService;

    public JobInfoHandler(ObjectMapper mapper, AAService aaService, TaskManagerService taskManagerService) {
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
        if (HandlerUtils.METHOD_GET.equals(requestMethod.toString())) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
            Collection<JobInfo> jobInfos = new ArrayList<>();
            for (Job job: taskManagerService.getJobs()) {
                Collection<ActionInfo> actionInfos = new ArrayList<>();
                Integer ordinal = 0;
                for (ActionConfiguration actionConfiguration: job.getActions()) {
                    ActionInfo actionInfo = new ActionInfo(ordinal,
                            actionConfiguration.getType().getTypeName(), actionConfiguration.getDescription());
                    actionInfos.add(actionInfo);
                    ordinal = ordinal  + 1;
                }
                JobInfo jobInfo = new JobInfo(job.getId(), job.getName(), actionInfos);
                jobInfos.add(jobInfo);
            }
            exchange.setStatusCode(HandlerUtils.OK);
            exchange.getResponseSender().send(mapper.writeValueAsString(jobInfos));
        } else {
            exchange.setStatusCode(HandlerUtils.METHOD_NOT_ALLOWED);
        }
    }

}
