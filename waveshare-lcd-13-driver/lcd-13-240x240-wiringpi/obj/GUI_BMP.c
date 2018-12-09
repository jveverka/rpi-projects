/*****************************************************************************
* | File      	:   BMP_APP.c
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
#include "GUI_BMP.h"
#include <stdio.h>	//fseek fread
#include <fcntl.h>
#include <unistd.h>
#include <stdint.h>
#include <stdlib.h>	//memset

#include "GUI_Paint.h"
// #include "GUI_Cache.h"

UBYTE GUI_ReadBmp(const char *path)
{
    FILE *fp;                     //Define a file pointer 
    BMPFILEHEADER bmpFileHeader;  //Define a bmp file header structure
    BMPINF bmpInfoHeader;         //Define a bmp bitmap header structure 

    // Binary file open
    if((fp = fopen(path, "rb")) == NULL) { // fp = 0x00426aa0
        DEBUG("Cann't open the file!\n");
        return 0;
    }

    // Set the file pointer from the beginning
    fseek(fp, 0, SEEK_SET);                            // fp = 0x00426aa0
    fread(&bmpFileHeader, sizeof(BMPFILEHEADER), 1, fp);//	sizeof(BMPFILEHEADER) must be 14,
	fread(&bmpInfoHeader, sizeof(BMPINF), 1, fp);

	int row, col;
    short data;
	RGBQUAD rgb;
	int len = bmpInfoHeader.bBitCount / 8;    //RGB888,one 3 byte = 1 bitbmp
	
	// get bmp data and show
	fseek(fp, bmpFileHeader.bOffset, SEEK_SET);
    for(row = 0; row < bmpInfoHeader.bHeight; row++) {
        for(col = 0; col < bmpInfoHeader.bWidth; col++) {
			if(fread((char *)&rgb, 1, len, fp) != len){
				perror("get bmpdata:\r\n");
				break;
			}            
            data = RGB((rgb.rgbRed), (rgb.rgbGreen), (rgb.rgbBlue));
            // ImageBuff[col + ((bmpInfoHeader.bHeight - row - 1) * bmpInfoHeader.bWidth)] = data;
            GUI_SetPixel(col, bmpInfoHeader.bHeight - row - 1, data);
        }
    }
	fclose(fp);
    return 0;
}