package itx.raspberry.drivers.waveshare.lcd13_240x240.example;

import itx.raspberry.drivers.waveshare.lcd13_240x240.api.Color;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.FontSize;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.NativeDriver;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.NativeDriverFactory;

public class NativeDriverExample {

    public static void main(String[] args) throws Exception {
        NativeDriver nativeDriver = NativeDriverFactory.getNativeDriver(false);
        nativeDriver.init(0, 300000000);
        System.out.println("waiting ...");
        Thread.sleep(1000);
        System.out.println("drawing ...");
        nativeDriver.drawPixel(  2,  2, Color.BLACK);
        nativeDriver.drawPixel(  3,  3, Color.GBLUE);
        nativeDriver.drawPixel(  4,  4, Color.GREEN);
        nativeDriver.drawPixel(  5,  5, Color.GREEN);
        nativeDriver.drawPixel(  6,  6, Color.GREEN);
        nativeDriver.drawLine(0,239,239,0, Color.RED);
        nativeDriver.drawString(10, 50, Color.BROWN, FontSize.FONT_24, Color.GBLUE, "hello xxx");
        nativeDriver.drawString(10,100, Color.BROWN, FontSize.FONT_24, Color.GBLUE, "hi hi yyy");
        nativeDriver.drawCircle(25, 50, 20, Color.MAGENTA, true);
        nativeDriver.drawCircle(25,100, 20, Color.YELLOW, false);
        nativeDriver.drawRectangle(200,100, 220, 120, Color.GRAY, true);
        nativeDriver.drawRectangle(210,210, 230, 230, Color.GRAY, false);
        nativeDriver.lcdShow();
        System.out.println("waiting ...");
        Thread.sleep(5_000);

        nativeDriver.drawCircle(30, 150, 20, Color.YELLOW, true);
        nativeDriver.lcdShowArea(10, 130, 50, 170);
        Thread.sleep(1000);
        nativeDriver.drawCircle(30, 150, 20, Color.BLACK, true);
        nativeDriver.lcdShowArea(10, 130, 50, 170);
        Thread.sleep(1000);
        nativeDriver.drawCircle(30, 150, 20, Color.YELLOW, true);
        nativeDriver.lcdShowArea(10, 130, 50, 170);

        nativeDriver.clear(Color.YELLOW);
        Thread.sleep(1000);
        nativeDriver.clear(Color.BLACK);
    }

}
