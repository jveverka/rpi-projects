package itx.rpi.powercontroller.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import itx.rpi.powercontroller.dto.ActionInfo;
import itx.rpi.powercontroller.dto.JobInfo;
import itx.rpi.powercontroller.services.TaskManagerService;
import itx.rpi.powercontroller.services.jobs.Action;
import itx.rpi.powercontroller.services.jobs.Job;

import java.util.ArrayList;
import java.util.Collection;

public class JobInfoHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final TaskManagerService taskManagerService;

    public JobInfoHandler(ObjectMapper mapper, TaskManagerService taskManagerService) {
        this.mapper = mapper;
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString requestMethod = exchange.getRequestMethod();
        if (HandlerUtils.METHOD_GET.equals(requestMethod.toString())) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HandlerUtils.JSON_TYPE);
            Collection<JobInfo> jobInfos = new ArrayList<>();
            for (Job job: taskManagerService.getJobs()) {
                Collection<ActionInfo> actionInfos = new ArrayList<>();
                for (Action action: job.getActions()) {
                    ActionInfo actionInfo = new ActionInfo(action.getType(), action.getDescription());
                    actionInfos.add(actionInfo);
                }
                JobInfo jobInfo = new JobInfo(job.getId(), job.getName(), actionInfos);
                jobInfos.add(jobInfo);
            }
            exchange.setStatusCode(200);
            exchange.getResponseSender().send(mapper.writeValueAsString(jobInfos));
        } else {
            exchange.setStatusCode(405);
        }
    }

}
