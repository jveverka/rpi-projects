#include "DEV_Config.h"
#include "LCD_1in3.h"
#include "GUI_Paint.h"
#include "GUI_BMP.h"
#include "KEY_APP.h"

#include <stdio.h>		//printf()
#include <stdlib.h>		//exit()
#include <time.h>  

int main(void)
{
    /* Module Init */
    if(DEV_ModuleInit(0, 9000000) != 0){
        DEV_ModuleExit();
        exit(0);
    }
	
    /* LCD Init */
    printf("1.3inch LCD demo...\r\n");
    LCD_Init(HORIZONTAL);	
    LCD_Clear(WHITE);
    
    /*1.Create a new image cache named IMAGE_RGB and fill it with white*/
    GUI_NewImage(IMAGE_RGB, LCD_WIDTH, LCD_HEIGHT, IMAGE_ROTATE_0, IMAGE_COLOR_POSITIVE);
    GUI_Clear(WHITE);
    
    /* GUI */
    printf("drawing...\r\n");
    /*2.Drawing on the image*/
    GUI_DrawPoint(5, 10, BLACK, DOT_PIXEL_1X1, DOT_STYLE_DFT);//240 240
    GUI_DrawPoint(5, 25, BLACK, DOT_PIXEL_2X2, DOT_STYLE_DFT);
    GUI_DrawPoint(5, 40, BLACK, DOT_PIXEL_3X3, DOT_STYLE_DFT);
    GUI_DrawPoint(5, 55, BLACK, DOT_PIXEL_4X4, DOT_STYLE_DFT);

    GUI_DrawLine(20, 10, 70, 60, RED, LINE_STYLE_SOLID, DOT_PIXEL_1X1);
    GUI_DrawLine(70, 10, 20, 60, RED, LINE_STYLE_SOLID, DOT_PIXEL_1X1);
    GUI_DrawLine(170, 15, 170, 55, RED, LINE_STYLE_DOTTED, DOT_PIXEL_1X1);
    GUI_DrawLine(150, 35, 190, 35, RED, LINE_STYLE_DOTTED, DOT_PIXEL_1X1);

    GUI_DrawRectangle(20, 10, 70, 60, BLUE, DRAW_FILL_EMPTY, DOT_PIXEL_1X1);
    GUI_DrawRectangle(85, 10, 130, 60, BLUE, DRAW_FILL_FULL, DOT_PIXEL_1X1);

    GUI_DrawCircle(170, 35, 20, GREEN, DRAW_FILL_EMPTY, DOT_PIXEL_1X1);
    GUI_DrawCircle(170, 85, 20, GREEN, DRAW_FILL_FULL, DOT_PIXEL_1X1);

    GUI_DrawString_EN(5, 70, "hello world", &Font16, WHITE, BLACK);
    GUI_DrawString_EN(5, 90, "waveshare", &Font20, RED, IMAGE_BACKGROUND);

    GUI_DrawNum(5, 120, 123456789, &Font20, BLUE, IMAGE_BACKGROUND);
        
    /*3.Refresh the picture in RAM to LCD*/
    LCD_Display();
    DEV_Delay_ms(2000);
    
    /* show bmp */
    printf("show bmp\r\n");
    GUI_ReadBmp("./pic/pic.bmp");
    
    LCD_Display();
    DEV_Delay_ms(2000);
    
    /* Monitor button */
    printf("Listening KEY\r\n");
    KEY_Listen();
    
    /* Module Exit */
    DEV_ModuleExit();
    return 0;	

}

