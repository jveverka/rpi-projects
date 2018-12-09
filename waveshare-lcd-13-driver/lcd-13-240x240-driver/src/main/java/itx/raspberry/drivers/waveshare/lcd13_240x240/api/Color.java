package itx.raspberry.drivers.waveshare.lcd13_240x240.api;

import itx.raspberry.drivers.waveshare.lcd13_240x240.NativeDriverLowLevel;

public final class Color {

    public static final Color WHITE   = new Color(NativeDriverLowLevel.WHITE);
    public static final Color BLACK   = new Color(NativeDriverLowLevel.BLACK);
    public static final Color BLUE    = new Color(NativeDriverLowLevel.BLUE);
    public static final Color BRED    = new Color(NativeDriverLowLevel.BRED);
    public static final Color GRED    = new Color(NativeDriverLowLevel.GRED);
    public static final Color GBLUE   = new Color(NativeDriverLowLevel.GBLUE);
    public static final Color RED     = new Color(NativeDriverLowLevel.RED);
    public static final Color MAGENTA = new Color(NativeDriverLowLevel.MAGENTA);
    public static final Color GREEN   = new Color(NativeDriverLowLevel.GREEN);
    public static final Color CYAN    = new Color(NativeDriverLowLevel.CYAN);
    public static final Color YELLOW  = new Color(NativeDriverLowLevel.YELLOW);
    public static final Color BROWN   = new Color(NativeDriverLowLevel.BROWN);
    public static final Color BRRED   = new Color(NativeDriverLowLevel.BRRED);
    public static final Color GRAY    = new Color(NativeDriverLowLevel.GRAY);

    private final int color;

    public Color(int color) {
        verifyColor(color);
        this.color = color;
    }

    public Color(int r, int g, int b) {
        verify(r);
        verifyGreen(g);
        verify(b);
        this.color = (b | (g<<5) | (r<<11));
    }

    public int getColor() {
        return color;
    }

    private void verify(int value) {
        if (value > 0x1F) {
            throw new UnsupportedOperationException("Color value must be < 0x1F");
        }
    }

    private void verifyGreen(int value) {
        if (value > 0x3F) {
            throw new UnsupportedOperationException("Green color value must be < 0x3F");
        }
    }

    private void verifyColor(int value) {
        if (value > 0xFFFF) {
            throw new UnsupportedOperationException("Total color value must be < 0xFFFF");
        }
    }

}
