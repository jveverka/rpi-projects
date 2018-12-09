
from sense_hat import SenseHat
from time import sleep
from random import randint

sense = SenseHat()
score = 0
inverted = True
speed = 0.50
snake_pos = [4, 3, 4, 4, 4, 5, 4, 6]
apple_pos = [randint(0, 7), randint(0, 7)]
snake_dir = 1
# The direction the snake is facing.
# 1 = up
# 2 = down
# 3 = left
# 4 = right
is_running = True

def draw_apple():
    sense.set_pixel(apple_pos[0], apple_pos[1], 255, 0, 0)
    # Lights up a pixel to represent the apple

def draw_snake(t):
    # This function is called with an argument of either 0 or 1. 
    # 0 means just draw the snake normally. 
    # 1 means add a segment
    global snake_pos, snake_dir

    if t==1:
            snake_pos.append(snake_pos[len(snake_pos)-2])
            snake_pos.append(snake_pos[len(snake_pos)-1])
	    # Duplicates the last segment by duplicating the last 2 values on the list.

    for i in range(1, len(snake_pos)-1):
        #global snake_pos
        snake_pos[len(snake_pos)-i]=snake_pos[len(snake_pos)-i-2]
	# Move each segment, except the head, to where the segment in front of it was.     
    if snake_dir == 1:
        snake_pos[1]-=1
    elif snake_dir == 2:
        snake_pos[1]+=1
    elif snake_dir == 3:
        snake_pos[0]-=1
    elif snake_dir == 4:
        snake_pos[0]+=1
    # Draw the head of the snake according to the direction of the snake

    if snake_pos[0]>7:
        snake_pos[0]=0
    if snake_pos[0]<0:
        snake_pos[0]=7
    if snake_pos[1]>7:
        snake_pos[1]=0
    if snake_pos[1]<0:
        snake_pos[1]=7
    # If the snake wants to go beyond the screen, make it reappear from the opposite edge.

    for i in range (0, int(len(snake_pos)/2)):
        if i==0:
	    # Draw the snake head as a bright blue
            sense.set_pixel(snake_pos[2*(i)], snake_pos[2*(i)+1], 0, 255, 255)
        elif i == 1:
	    # Draw the first segment as a darker blue
            sense.set_pixel(snake_pos[2*(i)], snake_pos[2*(i)+1], 0, 127, 255)
        else:
	    # Draw all remaining segments as the darkest blue
            sense.set_pixel(snake_pos[2*(i)], snake_pos[2*(i)+1], 0, 0, 255)

# Functions to change the direction of the snake
def move_up():
    global snake_dir
    if snake_dir != 2:
        snake_dir = 1
def move_down():
    global snake_dir
    if snake_dir != 1:
        snake_dir = 2
def move_left():
    global snake_dir
    if snake_dir != 4:
        snake_dir = 3
def move_right():
    global snake_dir
    if snake_dir != 3:
        snake_dir = 4



def rum_main():

    global inverted
    global speed 
    global snake_pos 
    global apple_pos 
    global snake_dir 
    global is_running 
    global score

    # Resets the game variables
    score = 0
    speed = 0.50
    snake_pos = [4, 3, 4, 4, 4, 5, 4, 6]
    apple_pos = [randint(0, 7), randint(0, 7)]
    snake_dir = 1

    if inverted:
        sense.set_rotation(180)
        # Inverts the screen of the Sense HAT (but annoyingly enough, not the joystick)

    # Mapping the joystick to the functions to change the snake direction, depending on the state
    # of 'inverted'
    if not inverted:
        sense.stick.direction_up = move_up
        sense.stick.direction_down = move_down
        sense.stick.direction_left = move_left
        sense.stick.direction_right = move_right
    else:
        sense.stick.direction_down = move_up
        sense.stick.direction_up = move_down
        sense.stick.direction_right = move_left
        sense.stick.direction_left = move_right

    # Main game loop
    while is_running:
        sense.clear()
    
        end = False
        for i in range (1, int(len(snake_pos)/2)-1):
            # Because this loop runs as many times as the snake has segments (to check if the
	    # snake is eating itself), the code inside is contained in an if, which evaluates
	    # to false once it is determined that the snake has eaten itself.
            if not end:
                # Checks if the snake's head is in the same position as this segment (in this iteration)
                if snake_pos[0] == snake_pos[2*i] and snake_pos[1] == snake_pos[2*i+1]:
                    sense.show_message("You scored " + str(score))
		    # Shows the score         
                    #score = 0
                    #speed = 0.50
                    #snake_pos = [4, 3, 4, 4, 4, 5, 4, 6]
                    #apple_pos = [randint(0, 7), randint(0, 7)]
                    #snake_dir = 1
		    # Resets the game variables
                    #end = True
                    return 
        draw_apple()
        # Draws the apple
    
        if apple_pos[0]==snake_pos[0] and apple_pos[1] == snake_pos[1]:
            # Checks if the snake head is in the same place as the apple (apple is eaten)
            draw_snake(1)
	    # Draws the snake while increasing its length.
            speed = speed * 0.9
            # Reduces the time between redraws, increasing game speed.
            apple_pos[0] = randint(0, 7)
            apple_pos[1] = randint(0, 7)
	    # Randomises the apple position
            score += 1
	    # Increases the score
        else:
            draw_snake(0)
	    # Draws the snake normally.
        sleep(speed)
        # Waits till the next redraw; this time is reduced as more apples are eaten.
