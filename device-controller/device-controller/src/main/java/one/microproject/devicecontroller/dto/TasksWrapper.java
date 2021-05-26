package one.microproject.devicecontroller.dto;

import one.microproject.rpi.powercontroller.dto.TaskInfo;

import java.util.Collection;

public record TasksWrapper(Collection<TaskInfo> jobs) {
}
