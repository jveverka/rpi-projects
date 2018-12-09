package itx.raspberry.drivers.waveshare.lcd13_240x240;

import itx.raspberry.drivers.waveshare.lcd13_240x240.api.AlreadyInitializedException;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.Color;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.FontSize;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.NativeDriver;

public class NativeDriverSimulatedImpl implements NativeDriver {

    private boolean initialized;

    public NativeDriverSimulatedImpl() {
        this.initialized = false;
    }

    @Override
    public void init(int channel, int speed) throws AlreadyInitializedException {
        if (initialized) {
            throw new AlreadyInitializedException();
        }
        initialized = true;
    }

    @Override
    public void drawPixel(int x, int y, Color color) {
    }

    @Override
    public void drawLine(int xFrom, int yFrom, int xTo, int yTo, Color color) {
    }

    @Override
    public void drawString(int x, int y, Color color, FontSize fontSize, Color background, String string) {
    }

    @Override
    public void drawCircle(int x, int y, int radius, Color color, boolean fill) {
    }

    @Override
    public void drawRectangle(int xFrom, int yFrom, int xTo, int yTo, Color color, boolean fill) {
    }

    @Override
    public void lcdShow() {
    }

    @Override
    public void lcdShowArea(int xFrom, int yFrom, int xTo, int yTo) {
    }

    @Override
    public void clear(Color color) {
    }

}
