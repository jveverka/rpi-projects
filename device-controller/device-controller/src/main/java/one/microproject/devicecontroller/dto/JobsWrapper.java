package one.microproject.devicecontroller.dto;

import one.microproject.rpi.powercontroller.dto.JobInfo;

import java.util.Collection;

public record JobsWrapper(Collection<JobInfo> jobs) {
}
