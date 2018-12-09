#!/usr/bin/python3

from sense_hat import SenseHat
from time import sleep
import app_pong
import app_snake
import app_tetripisense
import app_colors
import app_invaders
import app_standby
import app_gyro
import app_christmas

sense = SenseHat()

menu = [
         [[255,255,255],[255,255,255],[255,255,255],[255,255,255],[255,255,255]], 
         [[ 28, 28,255],[ 28, 28,255],[ 28, 28,255],[ 28, 28,255],[ 28, 28,255]], 
         [[255,255, 28],[255,255, 28],[255,255, 28],[255,255, 28],[255,255, 28]], 
         [[ 28,255, 28],[ 28,255, 28],[ 28,255, 28],[ 28,255, 28],[ 28,255, 28]], 
         [[255,255, 28],[255, 28,255],[ 28,255, 28],[255, 28, 28],[ 28, 28,255]], 
         [[200,200,200],[ 48, 48,200],[ 48, 48,200],[ 48, 48,200],[200,200,200]], 
         [[ 28,255, 28],[255, 28, 28],[ 28,255, 28],[255, 28, 28],[ 28,255, 28]], 
         [[255, 28, 28],[255, 28, 28],[255, 28, 25],[255, 28, 28],[255, 28, 28]], 
       ]

menu_position = 0

def draw_menu(menu):
    for y in range(len(menu)):
       for x in range(len(menu[y])):
           sense.set_pixel(x+2, y, menu[y][x][0], menu[y][x][1], menu[y][x][2])
    return

def draw_cursor(old_position, new_position):
    sense.set_pixel(0, old_position+0,  0,   0,   0)
    sense.set_pixel(0, new_position+0, 64, 255,  64)
    return

def up_event(event):
    global menu_position
    if event.action=='pressed':
       if menu_position>0:
          draw_cursor(menu_position, menu_position-1) 
          menu_position = menu_position-1;
       print("Up")
    return 

def down_event(event):
    global menu_position
    if event.action=='pressed':
       if menu_position<(len(menu)-1):
          draw_cursor(menu_position, menu_position+1) 
          menu_position = menu_position+1;
       print("Down")
    return 

def middle_event(event):
    global menu_position
    if event.action=='pressed':
       print("Middle")
       launch_action(menu_position)
    return 

def launch_action(menu_position):
    global menu
    global is_running
    if menu_position == 0:
       print("starting "+ str(menu_position) + " pong")
       app_pong.rum_main()
       start_launcher_menu(False)
    if menu_position == 1:
       print("starting "+ str(menu_position) + " snake")
       app_snake.rum_main()
       start_launcher_menu(False)
    if menu_position == 2:
       print("starting "+ str(menu_position) + " tertis")
       app_tetripisense.run_game()
       app_tetripisense.cleanup_game()
       start_launcher_menu(False)
    if menu_position == 3:
       print("starting "+ str(menu_position) + " invaders")
       app_invaders.run_main()
       start_launcher_menu(False)
    if menu_position == 4:
       print("starting "+ str(menu_position) + " colors")
       app_colors.run_main()
       start_launcher_menu(False)
    if menu_position == 5:
       print("starting "+ str(menu_position) + " gyro")
       app_gyro.run_main()
       start_launcher_menu(False) 
    if menu_position == 6:
       print("starting "+ str(menu_position) + " christmas")
       app_christmas.run_main()
       start_launcher_menu(False) 
    if menu_position == (len(menu)-1):
       print("standby "+ str(menu_position))
       app_standby.run_main()
       start_launcher_menu(False)
    return 

def start_launcher_menu(show_welcome):
    print("starting ...")
    sense.stick.direction_up = down_event
    sense.stick.direction_down = up_event
    sense.stick.direction_middle = middle_event
    sense.clear()
    sense.set_rotation(180)
    if show_welcome:
       sense.show_message("Welcome! ", text_colour=(255, 0, 0)) 
    print("ready ...")
    draw_menu(menu)
    draw_cursor(menu_position, menu_position)
    return

try:
   is_running = True
   start_launcher_menu(False)
   while is_running:
        sleep(0.25)
   print("bye ...\n")
   sense.clear()
except KeyboardInterrupt:
   print("killed by key interrupt") 
finally:
   print("killed !\n")
   sense.clear()

