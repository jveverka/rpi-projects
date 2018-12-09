/*****************************************************************************
* | File      	:   .c
* | Author      :   Waveshare team
* | Function    :   Hardware underlying interface
* | Info        :
*                Used to shield the underlying layers of each master
*                and enhance portability
*----------------
* |	This version:   V1.0
* | Date        :   2018-01-11
* | Info        :   Basic version
*
******************************************************************************/
#include "KEY_APP.h"
#include "GUI_Paint.h"
#include "LCD_1in3.h"
#include "Debug.h"

void Draw_Init(void)
{
    GUI_Clear(WHITE);

    /* Press */
    GUI_DrawCircle(90, 120, 25, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
    GUI_DrawString_EN(82, 112, "P", &Font24, IMAGE_BACKGROUND, BLUE);

    /* Left */
    GUI_DrawRectangle(15, 95, 65, 145, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
    GUI_DrawString_EN(32, 112, "L", &Font24, IMAGE_BACKGROUND, BLUE);

    /* Down */
    GUI_DrawRectangle(65, 145, 115, 195, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
    GUI_DrawString_EN(82, 162, "D", &Font24, IMAGE_BACKGROUND, BLUE);

    /* Right */
    GUI_DrawRectangle(115, 95, 165, 145, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
    GUI_DrawString_EN(132, 112, "R", &Font24, IMAGE_BACKGROUND, BLUE);

    /* Up */
    GUI_DrawRectangle(65, 45, 115, 95, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
    GUI_DrawString_EN(82, 62, "U", &Font24, IMAGE_BACKGROUND, BLUE);

    /* Key1 */
    GUI_DrawRectangle(185, 35, 235, 85, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
    GUI_DrawString_EN(195, 52, "K1", &Font24, IMAGE_BACKGROUND, BLUE);

    /* Key2	*/
    GUI_DrawRectangle(185, 95, 235, 145, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
    GUI_DrawString_EN(195, 112, "K2", &Font24, IMAGE_BACKGROUND, BLUE);

    /* Key3 */
    GUI_DrawRectangle(185, 155, 235, 205, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
    GUI_DrawString_EN(195, 172, "K3", &Font24, IMAGE_BACKGROUND, BLUE);

    LCD_Display();
}

void KEY_Listen(void)
{
    Draw_Init();
    for(;;) {
        if(GET_KEY_UP == 0) {
            while(GET_KEY_UP == 0) {
                GUI_DrawRectangle(65, 45, 115, 95, RED, DRAW_FILL_FULL, DOT_PIXEL_DFT);
                GUI_DrawString_EN(82, 62, "U", &Font24, IMAGE_BACKGROUND, BLUE);
                LCD_DisplayWindows(65, 45, 115, 95);
            }
            GUI_DrawRectangle(65, 45, 115, 95, WHITE, DRAW_FILL_FULL, DOT_PIXEL_DFT);
            GUI_DrawRectangle(65, 45, 115, 95, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
            GUI_DrawString_EN(82, 62, "U", &Font24, IMAGE_BACKGROUND, BLUE);
            LCD_DisplayWindows(65, 45, 115, 95);
        }
        if(GET_KEY_DOWN == 0) {
            while(GET_KEY_DOWN == 0) {
                GUI_DrawRectangle(65, 145, 115, 195, RED, DRAW_FILL_FULL, DOT_PIXEL_DFT);
                GUI_DrawString_EN(82, 162, "D", &Font24, IMAGE_BACKGROUND, BLUE);
                LCD_DisplayWindows(65, 145, 115, 195);
            }
            GUI_ClearWindows(65, 145, 115, 195, WHITE);
            GUI_DrawRectangle(65, 145, 115, 195, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
            GUI_DrawString_EN(82, 162, "D", &Font24, IMAGE_BACKGROUND, BLUE);
            LCD_DisplayWindows(65, 145, 115, 195);
        }
        if(GET_KEY_LEFT == 0) {
            while(GET_KEY_LEFT == 0) {
                GUI_DrawRectangle(15, 95, 65, 145, RED, DRAW_FILL_FULL, DOT_PIXEL_DFT);
                GUI_DrawString_EN(32, 112, "L", &Font24, IMAGE_BACKGROUND, BLUE);
                LCD_DisplayWindows(15, 95, 65, 145);
            }
            GUI_DrawRectangle(15, 95, 65, 145, WHITE, DRAW_FILL_FULL, DOT_PIXEL_DFT);
            GUI_DrawRectangle(15, 95, 65, 145, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
            GUI_DrawString_EN(32, 112, "L", &Font24, IMAGE_BACKGROUND, BLUE);
            LCD_DisplayWindows(15, 95, 65, 145);
        }
        if(GET_KEY_RIGHT == 0) {
            while(GET_KEY_RIGHT == 0) {
                GUI_DrawRectangle(115, 95, 165, 145, RED, DRAW_FILL_FULL, DOT_PIXEL_DFT);
                GUI_DrawString_EN(132, 112, "R", &Font24, IMAGE_BACKGROUND, BLUE);
                LCD_DisplayWindows(115, 95, 165, 145);
            }
            GUI_DrawRectangle(115, 95, 165, 145, WHITE, DRAW_FILL_FULL, DOT_PIXEL_DFT);
            GUI_DrawRectangle(115, 95, 165, 145, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
            GUI_DrawString_EN(132, 112, "R", &Font24, IMAGE_BACKGROUND, BLUE);
            LCD_DisplayWindows(115, 95, 165, 145);
        }
        if(GET_KEY_PRESS == 0) {
            while(GET_KEY_PRESS == 0) {
                GUI_DrawCircle(90, 120, 25, RED, DRAW_FILL_FULL, DOT_PIXEL_DFT);
                GUI_DrawString_EN(82, 112, "P", &Font24, IMAGE_BACKGROUND, BLUE);
                LCD_DisplayWindows(65, 95, 115, 145);
            }
            GUI_DrawCircle(90, 120, 25, WHITE, DRAW_FILL_FULL, DOT_PIXEL_DFT);
            GUI_DrawCircle(90, 120, 25, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
            GUI_DrawString_EN(82, 112, "P", &Font24, IMAGE_BACKGROUND, BLUE);
            LCD_DisplayWindows(65, 95, 115, 145);
        }
        if(GET_KEY1 == 0) {
            while(GET_KEY1 == 0) {
                GUI_DrawRectangle(185, 35, 235, 85, RED, DRAW_FILL_FULL, DOT_PIXEL_DFT);
                GUI_DrawString_EN(195, 52, "K1", &Font24, IMAGE_BACKGROUND, BLUE);
                LCD_DisplayWindows(185, 35, 235, 85);
            }
            GUI_DrawRectangle(185, 35, 235, 85, WHITE, DRAW_FILL_FULL, DOT_PIXEL_DFT);
            GUI_DrawRectangle(185, 35, 235, 85, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
            GUI_DrawString_EN(195, 52, "K1", &Font24, IMAGE_BACKGROUND, BLUE);
            LCD_DisplayWindows(185, 35, 235, 85);
        }
        if(GET_KEY2 == 0) {
            while(GET_KEY2 == 0) {
                GUI_DrawRectangle(185, 95, 235, 145, RED, DRAW_FILL_FULL, DOT_PIXEL_DFT);
                GUI_DrawString_EN(195, 112, "K2", &Font24, IMAGE_BACKGROUND, BLUE);
                LCD_DisplayWindows(185, 95, 235, 145);
            }
            GUI_DrawRectangle(185, 95, 235, 145, WHITE, DRAW_FILL_FULL, DOT_PIXEL_DFT);
            GUI_DrawRectangle(185, 95, 235, 145, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
            GUI_DrawString_EN(195, 112, "K2", &Font24, IMAGE_BACKGROUND, BLUE);
            LCD_DisplayWindows(185, 95, 235, 145);
        }
        if(GET_KEY3 == 0) {
            while(GET_KEY3 == 0) {
                GUI_DrawRectangle(185, 155, 235, 205, RED, DRAW_FILL_FULL, DOT_PIXEL_DFT);
                GUI_DrawString_EN(195, 172, "K3", &Font24, IMAGE_BACKGROUND, BLUE);
                LCD_DisplayWindows(185, 155, 235, 205);
            }
            GUI_DrawRectangle(185, 155, 235, 205, WHITE, DRAW_FILL_FULL, DOT_PIXEL_DFT);
            GUI_DrawRectangle(185, 155, 235, 205, RED, DRAW_FILL_EMPTY, DOT_PIXEL_DFT);
            GUI_DrawString_EN(195, 172, "K3", &Font24, IMAGE_BACKGROUND, BLUE);
            LCD_DisplayWindows(185, 155, 235, 205);
        }
    }
}