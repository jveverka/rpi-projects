/*****************************************************************************
* | File      	:   GUI_Cache.c
* | Author      :   Waveshare team
* | Function    :	When the controller's memory is sufficient, open up a part 
*                   of the memory for the image cache
*----------------
* |	This version:   V1.0
* | Date        :   2018-06-29
* | Info        :
*
******************************************************************************/
#include "GUI_Cache.h"

UWORD ImageBuff[LCD_HEIGHT * LCD_WIDTH];
