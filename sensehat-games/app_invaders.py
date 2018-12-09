
from sense_hat import SenseHat
from time import sleep
from random import randrange
from copy import copy

sense = SenseHat()
is_running = True

background = [
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
   [[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0],[0,0,0,]],
]

spaceship_position = 3
spaceship_color = [255,0,255]
max_block_count = 1
rock_color = [0, 255, 0]
delay = 1.2
counter = 0

def clear_background():
    global background
    sense.clear()
    for y in range(0,len(background)):
        for x in range(0,len(background[y])):
            background[y][x][0] = 0
            background[y][x][1] = 0
            background[y][x][2] = 0

def draw_background():
    global background
    sense.clear()
    for y in range(0,len(background)):
        for x in range(0,len(background[y])):
            sense.set_pixel(x, y,  background[y][x][0], background[y][x][1], background[y][x][2])

def draw_spaceship():
    global spaceship_position
    global spaceship_color
    sense.set_pixel(spaceship_position, 7,  spaceship_color[0], spaceship_color[1], spaceship_color[2])

def clear_spaceship():
    global spaceship_position
    sense.set_pixel(spaceship_position, 7,  0, 0, 0)

def clear_first_row():
    global background
    print("clear_first_row")
    for x in range(0,len(background[0])):
        background[0][x] = [0,0,0];

def generate_new_row():
    global background
    global max_block_count
    global rock_color
    print("generate_new_row [0]")
    for i in range(0, max_block_count):
        x = randrange(8)
        background[0][x] = copy(rock_color); 
        print("x=" + str(x) + ":" + str(rock_color))

def move_background_down():
    global background
    print("move_background_down")
    for y in range(len(background)-1):
        i = len(background) - y - 2
        print("  " + str(i) + " -> " + str(i+1))
        background[i + 1] = copy(background[i])
    clear_first_row()
    generate_new_row()
    draw_background()

def ship_has_collision():
    if background[7][spaceship_position][0] != 0 or background[7][spaceship_position][1] != 0 or background[7][spaceship_position][2] != 0:
       return True 
    return False

def left_event(event):
    global spaceship_position
    if event.action=='pressed':
       if spaceship_position<7:
          clear_spaceship()
          spaceship_position = spaceship_position + 1
          draw_spaceship()

def right_event(event):
    global spaceship_position
    if event.action=='pressed':
       if spaceship_position>0:
          clear_spaceship()
          spaceship_position = spaceship_position - 1
          draw_spaceship()

def middle_event(event):
    if event.action=='pressed':
       print("fire !")

def adjust_game_parameters():
    global delay
    global max_block_count
    global counter
    if counter>24:
       delay = delay - 0.3
       max_block_count = max_block_count + 1
       counter = 0
    
def run_main():
    global is_running
    global spaceship_position
    global delay
    global counter
    global max_block_count

    clear_background();
    sense.clear()
    sense.set_rotation(180)
    sense.stick.direction_left = left_event
    sense.stick.direction_right = right_event
    sense.stick.direction_middle = middle_event
    draw_background()
    draw_spaceship()

    is_running = True
    score = 0
    delay = 1.2
    spaceship_position = 3 
    counter = 0
    max_block_count = 1 

    while is_running:
       print("main cycle")
       sleep(delay)
       move_background_down()
       draw_spaceship()
       if ship_has_collision():
          is_running = False
       score = score + 1
       counter = counter + 1
       adjust_game_parameters()

    print("score: " + str(score))
    sense.show_message("Score: " + str(score), text_colour=(0, 0, 255)) 
    sense.clear()
    return 
