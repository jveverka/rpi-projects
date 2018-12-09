
from sense_hat import SenseHat
from time import sleep
from random import randrange
from copy import copy

sense = SenseHat()

background = []

# juraj's
background.append([
         [[  0,  0,  0],[  0,  0,  0],[  0,  0,  0],[160, 28, 28],[160, 28, 28],[  0,  0,  0],[  0,  0,  0],[  0,  0,  0]], 
         [[  0,  0,  0],[  0,  0,  0],[ 28,160, 28],[ 28, 28,160],[ 28,160, 28],[ 28,160, 28],[  0,  0,  0],[  0,  0,  0]], 
         [[  0,  0,  0],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28],[ 28, 28,160],[ 28,160, 28],[ 28,160, 28],[  0,  0,  0]], 
         [[ 28,160, 28],[ 28,160, 28],[160, 28, 28],[ 28, 28,160],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28]], 
         [[  0,  0,  0],[ 28, 28,160],[ 28,160, 28],[ 28,160, 28],[160, 28, 28],[ 28,160, 28],[160, 28, 28],[  0,  0,  0]], 
         [[ 28,160, 28],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28]], 
         [[  0,  0,  0],[ 28,160, 28],[160, 28, 28],[ 28, 28,160],[ 28,160, 28],[ 28, 28,160],[ 28,160, 28],[  0,  0,  0]], 
         [[ 28,160, 28],[ 28, 28,160],[ 28,160, 28],[160, 28, 28],[ 28,160, 28],[ 28,160, 28],[160, 28, 28],[ 28,160, 28]], 
       ])

# samuel's
background.append([
         [[  0,  0,  0],[  0,  0,  0],[  0,  0,  0],[255,255,  0],[255,255,  0],[  0,  0,  0],[  0,  0,  0],[  0,  0,  0]], 
         [[  0,  0,  0],[  0,  0,  0],[  0,  0,  0],[  0,204,255],[ 28,160, 28],[  0,  0,  0],[  0,  0,  0],[  0,  0,  0]], 
         [[  0,  0,  0],[  0,  0,  0],[ 28,160, 28],[ 28,160, 28],[  0,204,255],[255,  0,  0],[  0,  0,  0],[  0,  0,  0]], 
         [[  0,  0,  0],[ 28,160, 28],[255,  0,  0],[ 28,160, 28],[ 28,160, 28],[  0,204,255],[ 28,160, 28],[  0,  0,  0]], 
         [[  0,  0,  0],[  0,  0,  0],[ 28,160, 28],[ 28,160, 28],[  0,204,255],[ 28,160, 28],[  0,  0,  0],[  0,  0,  0]], 
         [[  0,  0,  0],[255,  0,  0],[ 28,160, 28],[  0,204,255],[255,  0,  0],[ 28,160, 28],[ 28,160, 28],[  0,  0,  0]], 
         [[ 28,160, 28],[ 28,160, 28],[  0,204,255],[ 28,160, 28],[ 28,160, 28],[ 28,160, 28],[255,  0,  0],[ 28,160, 28]], 
         [[  0,  0,  0],[  0,  0,  0],[  0,  0,  0],[153,102, 51],[153,102, 51],[  0,  0,  0],[  0,  0,  0],[  0,  0,  0]], 
       ])

snow = [
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
]

last_line = [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]]

is_running = False
max_block_count = 1
snow_color = [160, 160, 160]
background_index=0


def clear_first_row():
    global snow
    print("clear_first_row")
    for x in range(0,len(snow[0])):
        snow[0][x] = [0,0,0];

def generate_new_row():
    global snow
    global max_block_count
    global snow_color
    print("generate_new_row [0]")
    for i in range(0, max_block_count):
        x = randrange(8)
        snow[0][x] = copy(snow_color); 
        print("x=" + str(x) + ":" + str(snow_color))

def move_snow_down():
    global snow
    print("move_snow_down")
    for y in range(len(snow)-1):
        i = len(snow) - y - 2
        print("  " + str(i) + " -> " + str(i+1))
        snow[i + 1] = copy(snow[i])
    clear_first_row()
    generate_new_row()
    draw_snow()



def exit_event(event):
    global is_running
    if event.action=='pressed' and is_running:
       print("standby exit")
       is_running = False

def draw_background(background):
    for y in range(len(background)):
       for x in range(len(background[y])):
           sense.set_pixel(x, y, background[y][x][0], background[y][x][1], background[y][x][2])
    return

def draw_snow():
    global snow
    for y in range(len(snow)):
       for x in range(len(snow[y])):
           if snow[y][x][0] != 0:  
              sense.set_pixel(x, y, snow[y][x][0], snow[y][x][1], snow[y][x][2])
    return

def increment_background_index(event):
    global background_index
    if event.action=='pressed':
       background_index = background_index + 1
    if background_index >= len(background):
       background_index = 0 

def decrement_background_index(event):
    global background_index
    if event.action=='pressed':
       background_index = background_index - 1
    if background_index < 0:
       background_index = len(background) - 1 

def draw_accumulated_snow(background):
    global snow
    global last_line
    for x in range(len(last_line)):
        if snow[7][x][0] != 0:
           last_line[x] = copy(snow[7][x])
    snow_accumulated = 0
    for x in range(len(last_line)):
        if last_line[x][0] != 0:
           sense.set_pixel(x, 7, last_line[x][0], last_line[x][1], last_line[x][2])
           snow_accumulated = snow_accumulated + 1
    if snow_accumulated == 8:
       #melt all accumulated snow
       for x in range(len(last_line)):
           last_line[x] = [0,0,0]
           sense.set_pixel(x, 7, background[7][x][0], background[7][x][1], background[7][x][2])
    return

def run_main():
    print("entering x-mas mode")
    global is_running
    is_running = True
    sense.stick.direction_left  = exit_event
    sense.stick.direction_right = exit_event
    sense.stick.direction_up    = increment_background_index
    sense.stick.direction_down  = decrement_background_index
    sense.clear()
    sense.set_rotation(180)
    while is_running:
       draw_background(background[background_index])
       move_snow_down()
       draw_accumulated_snow(background[background_index])
       sleep(1)
    sense.clear()
    sense.set_rotation(180)
    is_running = False
    return 

