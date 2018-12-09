package itx.raspberry.drivers.waveshare.lcd13_240x240;

import itx.raspberry.drivers.waveshare.lcd13_240x240.api.AlreadyInitializedException;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.Color;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.FontSize;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.NativeDriver;

public class NativeDriverImpl implements NativeDriver {

    private boolean initialized;
    private final NativeDriverLowLevel driverLowLevel;

    public NativeDriverImpl(NativeDriverLowLevel driverLowLevel) {
        this.driverLowLevel = driverLowLevel;
        this.initialized = false;
    }

    @Override
    public void init(int channel, int speed) throws AlreadyInitializedException {
        if (initialized) {
            throw new AlreadyInitializedException();
        }
        driverLowLevel.init(channel, speed);
        initialized = true;
    }

    @Override
    public void drawPixel(int x, int y, Color color) {
        driverLowLevel.drawPixel(x, y, color.getColor());
    }

    @Override
    public void drawLine(int xFrom, int yFrom, int xTo, int yTo, Color color) {
        driverLowLevel.drawLine(xFrom, yFrom, xTo, yTo, color.getColor());
    }

    @Override
    public void drawString(int x, int y, Color color, FontSize fontSize, Color background, String string) {
        driverLowLevel.drawString(x, y, color.getColor(), fontSize.getSize(), background.getColor(), string);
    }

    @Override
    public void drawCircle(int x, int y, int radius, Color color, boolean fill) {
        driverLowLevel.drawCircle(x, y, radius, color.getColor(), fill);
    }

    @Override
    public void drawRectangle(int xFrom, int yFrom, int xTo, int yTo, Color color, boolean fill) {
        driverLowLevel.drawRectangle(xFrom, yFrom, xTo, yTo, color.getColor(), fill);
    }

    @Override
    public void lcdShow() {
        driverLowLevel.lcdShow();
    }

    @Override
    public void lcdShowArea(int xFrom, int yFrom, int xTo, int yTo) {
        driverLowLevel.lcdShowArea(xFrom, yFrom, xTo, yTo);
    }

    @Override
    public void clear(Color color) {
        driverLowLevel.clear(color.getColor());
    }

}
