
#include "../../../../lcd-13-240x240-wiringpi/obj/DEV_Config.h"
#include "../../../../lcd-13-240x240-wiringpi/obj/GUI_Paint.h"
#include "../../../../lcd-13-240x240-wiringpi/obj/LCD_1in3.h"

#include <jni.h>       // JNI header provided by JDK
#include <stdio.h>     // C Standard IO Header
#include "itx_raspberry_drivers_waveshare_lcd13_240x240_NativeDriverLowLevel.h"   // Generated

JNIEXPORT void JNICALL JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_init(JNIEnv *env, jobject thisObj, jint channel, jint speed) {
   printf("LCD init ...\r\n");
   /* Module Init */
   //int channel = 0;
   //int speed = 9000000;
   //int speed = 300000000;
   if(DEV_ModuleInit(channel, speed) != 0){
      DEV_ModuleExit();
      return;
   }
   /* LCD Init */
   LCD_Init(HORIZONTAL);	
   GUI_NewImage(IMAGE_RGB, LCD_WIDTH, LCD_HEIGHT, IMAGE_ROTATE_0, IMAGE_COLOR_POSITIVE);
   printf("LCD init done.\r\n");
   return;
}

JNIEXPORT void JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_lcdShow(JNIEnv *env, jobject thisObj) {
   printf("lcdShow all");
   LCD_Display();
   return;
}

JNIEXPORT void JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_lcdShowArea(JNIEnv *env, jobject thisObj, jint xFrom, jint yFrom, jint xTo, jint yTo) {
   printf("lcdShowArea [%d,%d]->[%d,%d]\r\n", xFrom, yFrom, xTo, yTo); 
   LCD_DisplayWindows(xFrom, yFrom, xTo, yTo);
   return;
}

JNIEXPORT void JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_clear(JNIEnv *env, jobject thisObj, jint color) {
   printf("clear color=%d\r\n", color);
   GUI_Clear(color);
   LCD_Clear(color);
   return;
}
 
JNIEXPORT void JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_drawPixel(JNIEnv *env, jobject thisObj, jint x, jint y, jint color) {
   printf("drawing pixel [%d,%d] color=%d\r\n", x, y, color);
   GUI_DrawPoint(x, y, color, DOT_PIXEL_1X1, DOT_STYLE_DFT);
   return;
}

JNIEXPORT void JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_drawLine(JNIEnv *env, jobject thisObj, jint xFrom, jint yFrom, jint xTo, jint yTo, jint color) {
   printf("drawing line [%d,%d]->[%d,%d] color=%d\r\n", xFrom, yFrom, xTo, yTo, color);
   GUI_DrawLine(xFrom, yFrom, xTo, yTo, color, LINE_STYLE_SOLID, DOT_PIXEL_1X1);
   return;
}

JNIEXPORT void JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_drawCircle(JNIEnv *env, jobject thisObj, jint x, jint y, jint radius, jint color, jboolean fill) {
   printf("drawing circle [%d,%d] color=%d radius=%d fill=%d\r\n", x, y, color, fill);
   if (fill == 1) {
      GUI_DrawCircle(x, y, radius, color, DRAW_FILL_FULL, DOT_PIXEL_1X1);
   } else {
      GUI_DrawCircle(x, y, radius, color, DRAW_FILL_EMPTY, DOT_PIXEL_1X1);
   } 
   return;
}

JNIEXPORT void JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_drawRectangle(JNIEnv *env, jobject thisObj, jint xFrom, jint yFrom, jint xTo, jint yTo, jint color, jboolean fill) {
   printf("drawing rectangle [%d,%d]->[%d,%d] color=%d radius=%d fill=%d\r\n", xFrom, yFrom, xTo, yTo, color, fill);
   if (fill == 1) {
      GUI_DrawRectangle(xFrom, yFrom, xTo, yTo, color, DRAW_FILL_FULL, DOT_PIXEL_DFT);
   } else {
      GUI_DrawRectangle(xFrom, yFrom, xTo, yTo, color, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
   } 
   return;
}

JNIEXPORT void JNICALL Java_itx_raspberry_drivers_waveshare_lcd13_1240x240_NativeDriverLowLevel_drawString(JNIEnv *env, jobject thisObj, jint x, jint y, jint color, jint fontSize, jint background, jstring text) {
   const char *str = (*env)->GetStringUTFChars(env, text, 0);
   printf("drawing string [%d,%d] color=%d font=%d bckgrnd=%d text=%s\r\n", x, y, color, fontSize, background, str);
   if (fontSize == 8) {
      GUI_DrawString_EN(x, y, str, &Font8, color, background);
   } else if (fontSize == 12) {
      GUI_DrawString_EN(x, y, str, &Font12, color, background);
   } else if (fontSize == 16) {
      GUI_DrawString_EN(x, y, str, &Font16, color, background);
   } else if (fontSize == 20) {
      GUI_DrawString_EN(x, y, str, &Font20, color, background);
   } else if (fontSize == 24) {
      GUI_DrawString_EN(x, y, str, &Font24, color, background);
   } else {
      printf("ERROR: unsupported font size: %d\r\n", fontSize);
   }
   return;
}


