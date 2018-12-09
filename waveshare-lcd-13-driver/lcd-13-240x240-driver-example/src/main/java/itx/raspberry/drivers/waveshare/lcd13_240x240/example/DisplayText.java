package itx.raspberry.drivers.waveshare.lcd13_240x240.example;

import itx.raspberry.drivers.waveshare.lcd13_240x240.api.Color;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.FontSize;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.NativeDriver;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.NativeDriverFactory;

public class DisplayText {

    public static void main(String[] args) throws Exception {
	String data = "...";
	int delay = 2000;
	if (args.length > 0) {
	   data = args[0];
	}
	if (args.length > 1) {
	   try {
               delay = Integer.parseInt(args[1]);
	   } catch (Exception e) {
	   }	   
	}
        NativeDriver nativeDriver = NativeDriverFactory.getNativeDriver(false);
        nativeDriver.init(0, 300000000);
        nativeDriver.drawString(10,100, Color.BROWN, FontSize.FONT_20, Color.GBLUE, data);
        nativeDriver.lcdShow();
        Thread.sleep(delay);
        nativeDriver.clear(Color.BLACK);
    }

}
