package one.microproject.devicecontroller.repository;

import one.microproject.devicecontroller.model.DeviceData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceDataRepository extends CrudRepository<DeviceData, String> {
}
