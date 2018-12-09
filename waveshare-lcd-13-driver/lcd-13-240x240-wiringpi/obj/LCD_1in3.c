/*****************************************************************************
* | File      	:   LCD_1in3.c
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
#include "LCD_1in3.h"
#include "DEV_Config.h"
#include "GUI_Cache.h"
#include "GUI_Paint.h"
#include <stdlib.h>		//itoa()
#include <stdio.h>

LCD_ATTRIBUTES LCD;

/******************************************************************************
function :	Hardware reset
parameter:
******************************************************************************/
static void LCD_Reset(void)
{
    LCD_RST_1;
    DEV_Delay_ms(100);
    LCD_RST_0;
    DEV_Delay_ms(100);
    LCD_RST_1;
    DEV_Delay_ms(100);
}

/******************************************************************************
function :	send command
parameter:
     Reg : Command register
******************************************************************************/
static void LCD_SendCommand(UBYTE Reg)
{
    LCD_DC_0;
    // LCD_CS_0;
    DEV_SPI_WriteByte(Reg);
    // LCD_CS_1;
}

/******************************************************************************
function :	send data
parameter:
    Data : Write data
******************************************************************************/
static void LCD_SendData_8Bit(UBYTE Data)
{
    LCD_DC_1;
    // LCD_CS_0;
    DEV_SPI_WriteByte(Data);
    // LCD_CS_1;
}

/******************************************************************************
function :	send data
parameter:
    Data : Write data
******************************************************************************/
static void LCD_SendData_16Bit(UWORD Data)
{
    LCD_DC_1;
    // LCD_CS_0;
    DEV_SPI_WriteByte((Data >> 8) & 0xFF);
    DEV_SPI_WriteByte(Data & 0xFF);
    // LCD_CS_1;
}

/******************************************************************************
function :	Initialize the lcd register
parameter:
******************************************************************************/
static void LCD_InitReg(void)
{
    LCD_SendCommand(0x3A);
    LCD_SendData_8Bit(0x05);

    LCD_SendCommand(0xB2);
    LCD_SendData_8Bit(0x0C);
    LCD_SendData_8Bit(0x0C);
    LCD_SendData_8Bit(0x00);
    LCD_SendData_8Bit(0x33);
    LCD_SendData_8Bit(0x33);

    LCD_SendCommand(0xB7);  //Gate Control
    LCD_SendData_8Bit(0x35);

    LCD_SendCommand(0xBB);  //VCOM Setting
    LCD_SendData_8Bit(0x19);

    LCD_SendCommand(0xC0); //LCM Control     
    LCD_SendData_8Bit(0x2C);

    LCD_SendCommand(0xC2);  //VDV and VRH Command Enable
    LCD_SendData_8Bit(0x01);
    LCD_SendCommand(0xC3);  //VRH Set
    LCD_SendData_8Bit(0x12);
    LCD_SendCommand(0xC4);  //VDV Set
    LCD_SendData_8Bit(0x20);

    LCD_SendCommand(0xC6);  //Frame Rate Control in Normal Mode
    LCD_SendData_8Bit(0x0F);
    
    LCD_SendCommand(0xD0);  // Power Control 1
    LCD_SendData_8Bit(0xA4);
    LCD_SendData_8Bit(0xA1);

    LCD_SendCommand(0xE0);  //Positive Voltage Gamma Control
    LCD_SendData_8Bit(0xD0);
    LCD_SendData_8Bit(0x04);
    LCD_SendData_8Bit(0x0D);
    LCD_SendData_8Bit(0x11);
    LCD_SendData_8Bit(0x13);
    LCD_SendData_8Bit(0x2B);
    LCD_SendData_8Bit(0x3F);
    LCD_SendData_8Bit(0x54);
    LCD_SendData_8Bit(0x4C);
    LCD_SendData_8Bit(0x18);
    LCD_SendData_8Bit(0x0D);
    LCD_SendData_8Bit(0x0B);
    LCD_SendData_8Bit(0x1F);
    LCD_SendData_8Bit(0x23);

    LCD_SendCommand(0xE1);  //Negative Voltage Gamma Control
    LCD_SendData_8Bit(0xD0);
    LCD_SendData_8Bit(0x04);
    LCD_SendData_8Bit(0x0C);
    LCD_SendData_8Bit(0x11);
    LCD_SendData_8Bit(0x13);
    LCD_SendData_8Bit(0x2C);
    LCD_SendData_8Bit(0x3F);
    LCD_SendData_8Bit(0x44);
    LCD_SendData_8Bit(0x51);
    LCD_SendData_8Bit(0x2F);
    LCD_SendData_8Bit(0x1F);
    LCD_SendData_8Bit(0x1F);
    LCD_SendData_8Bit(0x20);
    LCD_SendData_8Bit(0x23);

    LCD_SendCommand(0x21);  //Display Inversion On

    LCD_SendCommand(0x11);  //Sleep Out

    LCD_SendCommand(0x29);  //Display On
}

/********************************************************************************
function:	Set the resolution and scanning method of the screen
parameter:
		Scan_dir:   Scan direction
********************************************************************************/
static void LCD_SetAttributes(UBYTE Scan_dir)
{
    //Get the screen scan direction
    LCD.SCAN_DIR = Scan_dir;
    UBYTE MemoryAccessReg = 0x00;

    //Get GRAM and LCD width and height
    if(Scan_dir == HORIZONTAL) {
        LCD.HEIGHT	= LCD_HEIGHT;
        LCD.WIDTH   = LCD_WIDTH;
        MemoryAccessReg = 0X70;
    } else {
        LCD.HEIGHT	= LCD_WIDTH;
        LCD.WIDTH   = LCD_HEIGHT;
        MemoryAccessReg = 0X00;
    }

    // Set the read / write scan direction of the frame memory
    LCD_SendCommand(0x36); //MX, MY, RGB mode
    LCD_SendData_8Bit(MemoryAccessReg);	//0x08 set RGB
}

/********************************************************************************
function :	Initialize the lcd
parameter:
********************************************************************************/
void LCD_Init(UBYTE Scan_dir)
{
    //Turn on the backlight
    LCD_BL_1;

    //Hardware reset
    LCD_Reset();

    //Set the resolution and scanning method of the screen
    LCD_SetAttributes(Scan_dir);
    
    //Set the initialization register
    LCD_InitReg();
}

/********************************************************************************
function:	Sets the start position and size of the display area
parameter:
		Xstart 	:   X direction Start coordinates
		Ystart  :   Y direction Start coordinates
		Xend    :   X direction end coordinates
		Yend    :   Y direction end coordinates
********************************************************************************/
void LCD_SetWindows(UWORD Xstart, UWORD Ystart, UWORD Xend, UWORD Yend)
{
    //set the X coordinates
    LCD_SendCommand(0x2A);
    LCD_SendData_8Bit((Xstart >> 8) & 0xFF);
    LCD_SendData_8Bit(Xstart & 0xFF);
    LCD_SendData_8Bit(((Xend  - 1) >> 8) & 0xFF);
    LCD_SendData_8Bit((Xend  - 1) & 0xFF);

    //set the Y coordinates
    LCD_SendCommand(0x2B);
    LCD_SendData_8Bit((Ystart >> 8) & 0xFF);
    LCD_SendData_8Bit(Ystart & 0xFF);
    LCD_SendData_8Bit(((Yend  - 1) >> 8) & 0xFF);
    LCD_SendData_8Bit((Yend  - 1) & 0xFF);

    LCD_SendCommand(0X2C);
}

/******************************************************************************
function :	Clear screen
parameter:
******************************************************************************/
void LCD_Clear(UWORD Color)
{
    UWORD i, j;
    LCD_SetWindows(0, 0, LCD_WIDTH, LCD_HEIGHT);
    for (j = 0; j < LCD_HEIGHT; j++) {
        for (i = 0; i < LCD_WIDTH; i++) {
            LCD_SendData_16Bit(Color);
        }
    }
}

/******************************************************************************
function :	Sends the image buffer in RAM to e-Paper and displays
parameter:
******************************************************************************/
void LCD_Display(void)
{
    UDOUBLE Addr = 0;
    UDOUBLE Offset = GUI_Image.Image_Offset;
    UWORD i, j;

    LCD_SetWindows(0, 0, LCD_WIDTH, LCD_HEIGHT);
    for (j = 0; j < LCD_HEIGHT; j++) {
        for (i = 0; i < LCD_WIDTH; i++) {
            Addr = i + j * LCD_WIDTH + Offset;
            LCD_SendData_16Bit(ImageBuff[Addr]);
        }
    }
}

void LCD_DisplayWindows(UWORD Xstart, UWORD Ystart, UWORD Xend, UWORD Yend)
{
    //Convert coordinates
    UWORD X0, Y0, X1, Y1;
    switch(GUI_Image.Image_Rotate) {
    case IMAGE_ROTATE_0:
        X0 = Xstart;
        Y0 = Ystart;
        X1 = Xend;
        Y1 = Yend;
        break;
    case IMAGE_ROTATE_90:
        X0 = Ystart;
        Y0 = GUI_Image.Image_Width - Xend ;
        X1 = Yend;
        Y1 = GUI_Image.Image_Width - Xstart ;
        break;
    case IMAGE_ROTATE_180:
        X0 = GUI_Image.Image_Width - Xend ;
        Y0 = GUI_Image.Image_Height - Yend ;
        X1 = GUI_Image.Image_Width - Xstart ;
        Y1 = GUI_Image.Image_Height - Ystart ;
        break;
    case IMAGE_ROTATE_270:
        X0 = GUI_Image.Image_Width - Xend ;
        Y0 = Xstart;
        X1 = GUI_Image.Image_Height - Ystart ;
        Y1 = GUI_Image.Image_Height - Yend ;
        break;
    }
    
    // display
    UDOUBLE Addr = 0;
    UDOUBLE Offset = GUI_Image.Image_Offset;

    UWORD i, j;
    LCD_SetWindows(X0- 1, Y0- 1, X1 + 1, Y1 + 1);
    for (j = Y0- 1; j < Y1 + 1; j++) {
        for (i = X0- 1; i < X1 + 1; i++) {
            Addr = i + j * LCD_WIDTH + Offset;
            LCD_SendData_16Bit(ImageBuff[Addr]);
        }
    }
}
