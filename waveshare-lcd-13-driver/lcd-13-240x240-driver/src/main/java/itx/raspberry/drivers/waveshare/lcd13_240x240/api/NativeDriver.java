package itx.raspberry.drivers.waveshare.lcd13_240x240.api;

public interface NativeDriver {

    void init(int channel, int speed) throws AlreadyInitializedException;

    void drawPixel(int x, int y, Color color);

    void drawLine(int xFrom, int yFrom, int xTo, int yTo, Color color);

    void drawString(int x, int y, Color color, FontSize fontSize, Color background, String string);

    void drawCircle(int x, int y, int radius, Color color, boolean fill);

    void drawRectangle(int xFrom, int yFrom, int xTo, int yTo, Color color, boolean fill);

    void lcdShow();

    void lcdShowArea(int xFrom, int yFrom, int xTo, int yTo);

    void clear(Color color);

}
