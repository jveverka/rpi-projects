package one.microproject.clientsim;

import one.microproject.clientsim.dto.DataRequest;
import one.microproject.clientsim.dto.DataResponse;
import one.microproject.clientsim.dto.SystemInfo;

import java.io.InputStream;

public interface ClientSim {

    SystemInfo getSystemInfo();

    DataResponse getData(DataRequest request);

    InputStream download(DataRequest request);

}
