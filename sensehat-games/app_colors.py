
from sense_hat import SenseHat
from time import sleep
from random import randrange

sense = SenseHat()

def run_main():
    sense.clear()
    for x in range(0,8):
        for y in range(0,8):
            r = randrange(256)
            g = randrange(256)
            b = randrange(256)
            sense.set_pixel(x, y, r, g, b)
            sleep(0.1)
    sense.clear()
    return 



