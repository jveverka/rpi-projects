DIR_FONTS = ./Fonts
DIR_OBJ = ./obj
DIR_BIN = ./bin

OBJ_C = $(wildcard ${DIR_FONTS}/*.c ${DIR_OBJ}/*.c)
OBJ_O = $(patsubst %.c,${DIR_BIN}/%.o,$(notdir ${OBJ_C}))

TARGET = libLcd13Inch.so

CC = gcc
DEBUG = -g -O0 -Wall
CFLAGS += $(DEBUG)

LIB = -lwiringPi

${TARGET}:${OBJ_O}
	$(CC) $(CFLAGS) $(OBJ_O) -o $(DIR_BIN)/lcd13InchDemo.o $(LIB)

${DIR_BIN}/%.o : $(DIR_OBJ)/%.c
	$(CC) $(CFLAGS) -c  $< -o $@ $(LIB)

${DIR_BIN}/%.o:$(DIR_FONTS)/%.c
	$(CC) $(CFLAGS) -c  $< -o $@ 

buildlib:${OBJ_O}
	$(CC) $(CFLAGS) $(OBJ_O) -shared -o $(DIR_BIN)/$(TARGET) $(LIB)
	
clean:
	rm -f $(DIR_BIN)/*.o
	rm -f $(DIR_BIN)/*.so

