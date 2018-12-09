
from sense_hat import SenseHat
from time import sleep

sense = SenseHat()

is_running = False

def exit_event(event):
    global is_running
    if event.action=='pressed' and is_running:
       print("standby exit")
       is_running = False

def paint_indicator(brightness):
    for x in range(0,2):
        for y in range(0,2):
            sense.set_pixel(x+3, y+3, brightness, brightness, brightness)

def run_main():
    global is_running
    print("entering standby")
    brightness = 48
    is_running = False
    increment = True
    sense.stick.direction_left  = exit_event
    sense.stick.direction_right = exit_event
    sense.stick.direction_up    = exit_event
    sense.stick.direction_down  = exit_event
    sense.clear()
    paint_indicator(brightness)
    sleep(1)
    is_running = True
    while is_running:
       sleep(0.05)
       paint_indicator(brightness)
       if increment:
          brightness = brightness + 4
       else:
          brightness = brightness - 4
       if increment and brightness > 110:
           increment = not increment
       if not increment and brightness <= 48:
           increment = not increment
    sleep(1)
    print("standby interrupted")
    sense.clear()
    return 
