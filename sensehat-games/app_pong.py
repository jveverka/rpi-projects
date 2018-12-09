from sense_hat import SenseHat
from time import sleep

def draw_ball():
    global y
    global oy
    global t
    global is_running
    global ball_position
    global ball_velocity

    ball_position[0] += ball_velocity[0]
    ball_position[1] += ball_velocity[1]
    # Adds the ball velocity to the ball position, for both x and y axes.
    # This moves the ball in the right direction and speed.
    
    if ball_position[1] == 7 or ball_position[1] == 0:
        ball_velocity[1] = -ball_velocity[1]
        # If the ball hits the upper or lower boundary of the screen, it bounces
        # back. 
    if ball_position[0] == 0:
        sense.show_message("You lost! ", text_colour=(255, 0, 0))
        is_running = False
        reset_game()
        # If you miss the ball and it hits the left boundary, you lose, the Sense
        # HAT displays a message, and the game quits. 
        
    if ball_position[0] == 7:
        sense.show_message("You won! ", text_colour=(0, 255, 0))
        is_running = False
        reset_game()
        # If your opponent misses the ball, you win, the Sense
        # HAT displays a message, and the game quits. 

    # These conditions check which part of the paddle the ball is hitting, and
    # change the velocity accordingly - if it hits the top edge, the ball moves
    # diagonally upwards, if it hits the middle, it moves straight, and if it
    # hits the bottom edge, it moves diagonally downwards. Note that if you hit
    # the ball with the edge of the paddle at the edge of the screen, it doesn't
    # make it move off the screen - instead, the bouncing logic from above makes
    # it bounce back.
    if (ball_position[0] == 1 and y == ball_position[1]) or (ball_position[0] == 6 and oy == ball_position[1]):
        ball_velocity[0] = -ball_velocity[0]
        ball_velocity[1] = 0
    if ball_position[0] == 1 and y-1 == ball_position[1] or (ball_position[0] == 6 and oy-1 == ball_position[1]):
        ball_velocity[0] = -ball_velocity[0]
        if ball_position[1] != 0:
            ball_velocity[1] = -1
    if ball_position[0] == 1 and y+1 == ball_position[1] or (ball_position[0] == 6 and oy+1 == ball_position[1]):
        ball_velocity[0] = -ball_velocity[0]
        if ball_position[1] != 7:
            ball_velocity[1] = 1

    # Draws the ball in the position calculated.
    sense.set_pixel(ball_position[0], ball_position[1], 0, 0, 255)

# A very simple function to draw the bat for both the player and the opponent -
# lights up a pixel at the current y-position of the player and the opponent, as
# well as one above and one below. The player bat is white, and the opponent bat
# is yellow.
def draw_bat():
    global y
    global oy
    sense.set_pixel(0, y, 255, 255, 255)
    sense.set_pixel(0, y-1, 255, 255, 255)
    sense.set_pixel(0, y+1, 255, 255, 255)

    sense.set_pixel(7, oy, 255, 255, 0)
    sense.set_pixel(7, oy-1, 255, 255, 0)
    sense.set_pixel(7, oy+1, 255, 255, 0)

# These functions are called every time the joystick is moved up or down.
# If the paddle isn't already in the highest possible position, it moves up
# when the joystick is moved up and if it isn't already in the lowest possible
# position, it moves down when the joystick is moved down.

def move_up(event):
    global y
    if event.action=='pressed' and y>1:
        y-=1
def move_down(event):
    global y
    if event.action=='pressed' and y<6:
        y+=1

def reset_game():
    global y
    global oy
    global t
    global ball_position
    global ball_velocity
    y = 4
    oy = 4
    t = 0
    ball_position = [3, 3]
    ball_velocity = [1, 1]

sense = SenseHat()
inverted = True
is_running = True

def rum_main():
    global y
    global oy
    global t
    global inverted
    global is_running
    
    reset_game()
    is_running = True

    if inverted:
       sense.set_rotation(180)
    
    if not inverted:
       sense.stick.direction_up = move_up
       sense.stick.direction_down = move_down
    else:
       sense.stick.direction_down = move_up
       sense.stick.direction_up = move_down
    
    draw_bat()
    
    while is_running:
       global t
       sense.clear()
       
       # Every 2 out of 3 frames, move the opponent's paddle towards the ball.
       if ball_position[1]>oy and oy < 6 and t%3 != 0:
           oy+=1
       if ball_position[1]<oy and oy > 1 and t%3 != 0:
           oy-=1
       
       # Draw the 2 bats and the ball
       draw_bat()
       draw_ball()
       
       # Add 1 to the frame counter used to calculate whether or not the opponent moves.
       t += 1
       
       # Waits for a quarter of a second before drawing the next frame.
       sleep(0.25)
        
