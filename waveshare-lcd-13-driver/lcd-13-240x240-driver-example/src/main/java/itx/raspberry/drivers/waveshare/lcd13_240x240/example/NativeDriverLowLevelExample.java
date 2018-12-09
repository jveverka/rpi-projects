package itx.raspberry.drivers.waveshare.lcd13_240x240.example;

import itx.raspberry.drivers.waveshare.lcd13_240x240.NativeDriverLowLevel;

public class NativeDriverLowLevelExample {

    public static void main(String[] args) throws Exception {
        NativeDriverLowLevel nativeDriver = new NativeDriverLowLevel();
        nativeDriver.init(0, 300000000);
        System.out.println("waiting ...");
        Thread.sleep(1000);
        System.out.println("drawing ...");
        nativeDriver.drawPixel(  2,  2, NativeDriverLowLevel.BLACK);
        nativeDriver.drawPixel(  3,  3, NativeDriverLowLevel.GBLUE);
        nativeDriver.drawPixel(  4,  4, NativeDriverLowLevel.GREEN);
        nativeDriver.drawPixel(  5,  5, NativeDriverLowLevel.GREEN);
        nativeDriver.drawPixel(  6,  6, NativeDriverLowLevel.GREEN);
        nativeDriver.drawLine(0,239,239,0, NativeDriverLowLevel.RED);
        nativeDriver.drawString(10, 50, NativeDriverLowLevel.BROWN, 24, NativeDriverLowLevel.GBLUE, "hello xxx");
        nativeDriver.drawString(10,100, NativeDriverLowLevel.BROWN, 24, NativeDriverLowLevel.GBLUE, "hi hi yyy");
        nativeDriver.drawCircle(25, 50, 20, NativeDriverLowLevel.MAGENTA, true);
        nativeDriver.drawCircle(25,100, 20, NativeDriverLowLevel.YELLOW, false);
        nativeDriver.drawRectangle(200,100, 220, 120, NativeDriverLowLevel.GRAY, true);
        nativeDriver.drawRectangle(210,210, 230, 230, NativeDriverLowLevel.GRAY, false);
        nativeDriver.lcdShow();
        System.out.println("waiting ...");
        Thread.sleep(5_000);

        nativeDriver.drawCircle(30, 150, 20, NativeDriverLowLevel.YELLOW, true);
        nativeDriver.lcdShowArea(10, 130, 50, 170);
        Thread.sleep(1000);
        nativeDriver.drawCircle(30, 150, 20, NativeDriverLowLevel.BLACK, true);
        nativeDriver.lcdShowArea(10, 130, 50, 170);
        Thread.sleep(1000);
        nativeDriver.drawCircle(30, 150, 20, NativeDriverLowLevel.YELLOW, true);
        nativeDriver.lcdShowArea(10, 130, 50, 170);

        nativeDriver.clear(NativeDriverLowLevel.YELLOW);
        Thread.sleep(1000);
        nativeDriver.clear(NativeDriverLowLevel.BLACK);
    }

}
