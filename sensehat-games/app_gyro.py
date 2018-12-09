
from sense_hat import SenseHat
from time import sleep
import math

sense = SenseHat()

is_running = True

#circle = [[3,0],[2,0],[1,1],[0,2],[0,3],[0,4],[0,5],[1,6],[2,7],[3,7],[4,7],[5,7],[6,6],[7,5],[7,4],[7,3],[7,2],[6,1],[5,0],[4,0]] #always down
circle = [[4,7],[5,7],[6,6],[7,5],[7,4],[7,3],[7,2],[6,1],[5,0],[4,0],[3,0],[2,0],[1,1],[0,2],[0,3],[0,4],[0,5],[1,6],[2,7],[3,7]] #always up

def exit_event(event):
    global is_running
    if event.action=='pressed' and is_running:
       print("standby exit")
       is_running = False

def draw_data(x,y,z):
    brightness = round(48 + (abs(z)*200))
    if brightness > 255:
       brightness = 255
    #print("brightness:" + str(brightness))
    for x in range(0,2):
        for y in range(0,2):
            if z > 0:
               sense.set_pixel(x+3, y+3, brightness, 0, 0)
            else: 
               sense.set_pixel(x+3, y+3, 0, brightness, 0)

def draw_pointer(index, brightness):
    sense.set_pixel(circle[index][0], circle[index][1], brightness, brightness, brightness)

def clear_circle():
    global circle
    for i in range(0, len(circle)):
        sense.set_pixel(circle[i][0], circle[i][1], 0, 0, 48)

def run_main():
    global circle
    global is_running
    sense.stick.direction_left  = exit_event
    sense.stick.direction_right = exit_event
    sense.stick.direction_up    = exit_event
    sense.stick.direction_down  = exit_event
    sense.clear()

    is_running = True
    clear_circle()

    while is_running:
       acceleration = sense.get_accelerometer_raw()
       x = acceleration['x']
       y = acceleration['y']
       z = acceleration['z']
       
       #angle is in range form 0 to 2*PI
       angle = math.atan2(x,y) + math.pi
       
       #index of circle coordinates to paint
       index = round((len(circle)-1) * ( angle / (2*math.pi))) 

       #distance from 0,0
       distance = math.sqrt(abs(x)*abs(x) + abs(y)*abs(y)) 
       brightness = round(40 + (distance*215))
       if brightness > 255:
          brightness = 255
       
       #print("x={0}, y={1}, z={2}".format(x, y, z))
       #print("angle: " + str(angle) + " " + str(len(circle)) + " i=" + str(index) + " distance=" + str(distance))
       draw_data(x,y,z)
       clear_circle()
       draw_pointer(index, brightness) 
       sleep(0.1)
    return
