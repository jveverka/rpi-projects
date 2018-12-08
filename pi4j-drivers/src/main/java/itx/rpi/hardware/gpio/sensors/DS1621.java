package itx.rpi.hardware.gpio.sensors;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * DS1621 I2C 
 * 5V temperature sensor
 * not tested !
 * @author gergej
 *
 */
public class DS1621 {

    private static final byte START_CONVERT_CMD  = (byte)0xEE;
    private static final byte READ_TEMP_CMD      = (byte)0xAA;
    private static final byte COUNT_PER_C_CMD    = (byte)0xA9;
    private static final byte COUNT_REMAIN_CMD   = (byte)0xA8;
	private int address;
	
	public DS1621(int address) {
		this.address = address;
		getTemperature();
	}
	
	public Double getTemperature() {
		/*
        self.bus.transaction(i2c.writing_bytes(self.ADDR, self.START_CONVERT_CMD))
        time.sleep(0.01)
        self.bus.transaction(i2c.writing_bytes(self.ADDR, self.READ_TEMP_CMD))
        time.sleep(0.01)
        results = self.bus.transaction(i2c.reading(self.ADDR, 2))
        temperature = 0x7F & results[0][0]
        tempSign    = 0x80 & results[0][0]
        if (tempSign != 0):
            temperature = temperature - 128
        if (results[0][1] != 0):
            temperature = temperature + 0.5 
        if (temperature <= -55):
            return self.getTemperature()   
        return temperature
        */   		
		try {
			I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			I2CDevice i2cDS1621 = bus.getDevice(address);
			i2cDS1621.write(START_CONVERT_CMD);
			Thread.sleep(10);
			i2cDS1621.write(READ_TEMP_CMD);
			byte[] results = new byte[2];
			int bytesRead = i2cDS1621.read(results, 0, 2);
			if (bytesRead == 2) {
				double temp = 0;
		        int temperature = 0x7F & results[0];
		        int tempSign = 0x80 & results[0];
		        if (tempSign != 0) {
		        	temperature = temperature - 128;
		        }
		        if (results[1] != 0) {
		        	temp = temperature + 0.5; 
		        }
		        if (temp <= -55) {
		            return getTemperature();
		        }
		        return new Double(temp);
			}
		} catch (IOException | InterruptedException | I2CFactory.UnsupportedBusNumberException e) {
		      System.err.println(e.getMessage());
		}
		return null;
	}

}
