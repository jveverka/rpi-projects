package itx.rpi.powercontroller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Measurements {

    private final Date timeStamp;
    private final Float temperature;
    private final String temperatureUnit;
    private final Float relHumidity;
    private final String relHumidityUnit;
    private final Float pressure;
    private final String pressureUnit;

    @JsonCreator
    public Measurements(@JsonProperty("timeStamp") Date timeStamp,
                        @JsonProperty("temperature")  Float temperature,
                        @JsonProperty("temperatureUnit") String temperatureUnit,
                        @JsonProperty("relHumidity") Float relHumidity,
                        @JsonProperty("relHumidityUnit") String relHumidityUnit,
                        @JsonProperty("pressure") Float pressure,
                        @JsonProperty("pressureUnit") String pressureUnit) {
        this.timeStamp = timeStamp;
        this.temperature = temperature;
        this.temperatureUnit = temperatureUnit;
        this.relHumidity = relHumidity;
        this.relHumidityUnit = relHumidityUnit;
        this.pressure = pressure;
        this.pressureUnit = pressureUnit;
    }

    public Float getTemperature() {
        return temperature;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public Float getRelHumidity() {
        return relHumidity;
    }

    public String getRelHumidityUnit() {
        return relHumidityUnit;
    }

    public Float getPressure() {
        return pressure;
    }

    public String getPressureUnit() {
        return pressureUnit;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

}
