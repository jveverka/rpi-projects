package itx.raspberry.drivers.waveshare.lcd13_240x240;

/**
 * Low level JNI driver API for WaveShare 1.3" 240x240 LCD display.
 * @author juraj.veverka
 */
public final class NativeDriverLowLevel {

   static {
      System.out.println("Loading system library...");
      System.loadLibrary("WsLcd13NativeDriver");
      System.out.println("Library loaded.");
   }

   public static final int WHITE   = 0xFFFF;
   public static final int BLACK   = 0x0000;
   public static final int BLUE    = 0x001F;
   public static final int BRED    = 0XF81F;
   public static final int GRED    = 0XFFE0;
   public static final int GBLUE   = 0X07FF;
   public static final int RED     = 0xF800;
   public static final int MAGENTA = 0xF81F;
   public static final int GREEN   = 0x07E0;
   public static final int CYAN    = 0x7FFF;
   public static final int YELLOW  = 0xFFE0;
   public static final int BROWN   = 0XBC40;
   public static final int BRRED   = 0XFC07;
   public static final int GRAY    = 0X8430;

   public native void init(int channel, int speed);
   
   public native void drawPixel(int x, int y, int color);

   public native void drawLine(int xFrom, int yFrom, int xTo, int yTo, int color);

   public native void drawString(int x, int y, int color, int fontSize, int background, String string);

   public native void drawCircle(int x, int y, int radius, int color, boolean fill);

   public native void drawRectangle(int xFrom, int yFrom, int xTo, int yTo, int color, boolean fill);

   public native void lcdShow();

   public native void lcdShowArea(int xFrom, int yFrom, int xTo, int yTo);

   public native void clear(int color); 

}

