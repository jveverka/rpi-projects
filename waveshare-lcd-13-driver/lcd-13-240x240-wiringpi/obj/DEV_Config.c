/*****************************************************************************
* | File      	:   DEV_Config.c
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
#include "DEV_Config.h"

/******************************************************************************
function:	Initialization pin
parameter:
Info:
******************************************************************************/
static void DEV_GPIOConfig(void)
{
    pinMode(LCD_RST, OUTPUT);
    pinMode(LCD_DC, OUTPUT);
    pinMode(LCD_CS, OUTPUT);

    pinMode(KEY_UP_PIN, INPUT);
    pinMode(KEY_DOWN_PIN, INPUT);
    pinMode(KEY_LEFT_PIN, INPUT);
    pinMode(KEY_RIGHT_PIN, INPUT);
    pinMode(KEY_PRESS_PIN, INPUT);
    pinMode(KEY1_PIN, INPUT);
    pinMode(KEY2_PIN, INPUT);
    pinMode(KEY3_PIN, INPUT);

    pullUpDnControl(KEY_UP_PIN, PUD_UP);
    pullUpDnControl(KEY_DOWN_PIN, PUD_UP);
    pullUpDnControl(KEY_LEFT_PIN, PUD_UP);
    pullUpDnControl(KEY_RIGHT_PIN, PUD_UP);
    pullUpDnControl(KEY_PRESS_PIN, PUD_UP);
    pullUpDnControl(KEY1_PIN, PUD_UP);
    pullUpDnControl(KEY2_PIN, PUD_UP);
    pullUpDnControl(KEY3_PIN, PUD_UP);
}

/******************************************************************************
function:	Module Initialize, the BCM2835 library and initialize the pins, SPI protocol
parameter:
Info:
******************************************************************************/
UBYTE DEV_ModuleInit(int channel, int speed)
{
    //1.wiringPiSetupGpio
    //if(wiringPiSetup() < 0)//use wiringpi Pin number table
    if(wiringPiSetupGpio() < 0) { //use BCM2835 Pin number table
        DEBUG("set wiringPi lib failed	!!! \r\n");
        return 1;
    } else {
        DEBUG("set wiringPi lib success  !!! \r\n");
    }

    //2.GPIO config
    DEV_GPIOConfig();
    DEBUG("DEV_GPIOConfig success  !!! \r\n");

    //3.spi init
    wiringPiSPISetup(channel, speed);
    //wiringPiSPISetup(0, 9000000, 0);
    
    DEBUG("DEV_ModuleInit done.\r\n");
    return 0;
}

void DEV_SPI_WriteByte(uint8_t value)
{
    int read_data;
    read_data = wiringPiSPIDataRW(0,&value,1);
    if(read_data < 0)
        perror("wiringPiSPIDataRW failed\r\n");
}

/******************************************************************************
function:	Module exits, closes SPI and BCM2835 library
parameter:
Info:
******************************************************************************/
void DEV_ModuleExit(void)
{
    LCD_RST_1;
}
