"""
Tetris-like game with LEDs for the SenseHat / UnicornHat / AstroPi!

Adapted from Stephen Blythe's Tetris clone for the Ciseco PiLite.
pygame used for colours, collision detection, timing and keyboard handling.

Controls (sticking with the Sense Hat joystick-to-keyboard mappings):
    Joystick left   /  Left Arrow  - Move left
    Joystick right  /  Right Arrow - Move right
    Joystick up     /  Up Arrow    - Rotate clockwise
    Joystick down   /  Down Arrow  - Drop current block
    Joystick press  /  Return key  - Not currently setup

Stephen Blythe 2014 (Originial PiLite implementation)
Andrew Richards 2015 (Sense Hat implementation)
--------------------------------------------------------------------------
"""

import sys, pygame, random, sense_hat, math
from pygame.color import Color

WIDTH = 8
WORKAREA_HEIGHT = 12  # ACTUAL_SIZE of display + max height of a block
WORKAREA_SIZE = WIDTH, WORKAREA_HEIGHT
ACTUAL_HEIGHT = 8  # The actual number of pixels vertically on the display
ACTUAL_SIZE = WIDTH, ACTUAL_HEIGHT
TOTAL_LEDS = WIDTH * ACTUAL_HEIGHT
CLEAR = (0, 0, 0, 0)
THRESHOLD = 10  # Colour intensity over which a pixel/LED counts as 'on'.
FRAME_RATE = 10  # Frames per second
BASE_FRAMES = 10  # Frames between drops, value reduces during game
sense = sense_hat.SenseHat()


def sensehat_display(surface):
    """Transfer a pygame Surface to the Sense Hat LEDs.

    This function builds a frame-buffer fb of (r,g,b) values suitable for the
    Hat's set_pixels method. It does this by extracting these values from
    pygame's (r,g,b,a) values, treating the final value as intensity.
    """
    fb = []
    w, h = surface.get_size()
    for y in range(h):
        row = ""
        for x in range(w):
            r, g, b, a = surface.get_at((x, y))
            red = int(r * a / 255)
            green = int(g * a / 255)
            blue = int(b * a / 255)
            fb.append((red, green, blue))
    # Display only the physical pixels that the Hat actually has,
    sense.set_pixels(fb[-TOTAL_LEDS:])


def blank_canvas(size=WORKAREA_SIZE):
    """Setup and return a pygame.Surface with all pixels turned off"""
    s = pygame.Surface(size, pygame.SRCALPHA)
    s.fill(CLEAR)
    return s


def surface_from_pattern(pattern, colour):
    """
    Returns a pygame.Surface object according to the supplied pattern and colour.
    The pattern is a list of rows, each row is a list of 1s and 0s.
    """
    height = len(pattern)  # Number of rows
    width = (max(len(row) for row in pattern))
    s = blank_canvas(size = ((width, height)))
    for y, row in enumerate(pattern):
        for x, element in enumerate(row):
            if element:
                s.set_at((x, y), colour)
    return s


def all_block_variants(pattern, colour):
    """
    Given a pattern, create the corresponding pygame.Surface for it with
    surface_from_pattern() above; also create all its possible
    variants if rotated.
    """
    block_surface = surface_from_pattern(pattern, colour)
    # Calculate number of pixels assuming 1s and 0s in pattern
    pixels = sum(pixel for row in pattern for pixel in row)
    block_mask = pygame.mask.from_surface(block_surface, THRESHOLD)
    list_of_variants = [block_surface, ]
    for angle in (-90, -180, -270):  # -90 etc: clockwise rotation in pygame.transform.rotate
        rotated_surface = pygame.transform.rotate(block_surface, angle)
        rotated_mask = pygame.mask.from_surface(rotated_surface, THRESHOLD)
        # Don't store any variants for this block that are already stored,
        if block_mask.overlap_area(rotated_mask, (0, 0)) == pixels:
            break
        list_of_variants.append(rotated_surface)
    return list_of_variants


def blockdata_list():
    """
    Create a surface for each block as well as all its variants if rotated,
    and put these in an array. Given small display, some smaller blocks used.
    """
    bl = []
    bl.append(all_block_variants([[1, 1], [1, 1]], Color('magenta')))
    bl.append(all_block_variants([[0, 1], [1, 1], [1, 0]], Color('blue')))
    bl.append(all_block_variants([[1, 0], [1, 1], [0, 1]], Color('darkorange4')))
    bl.append(all_block_variants([[1, 1, 1], [0, 1, 0]], Color('red')))
    bl.append(all_block_variants([[1, 1], [1, 0], [1, 0]], Color('cyan')))
    bl.append(all_block_variants([[1, 1], [0, 1], [0, 1]], Color('salmon')))
    bl.append(all_block_variants([[1,], [1,], [1,]], Color('yellow')))
    return bl


def block_mask(block, x, y):
    """Return a pygame.mask for the specified block and position"""
    block_canvas = blank_canvas()
    block_canvas.blit(block, [x, y], special_flags=pygame.BLEND_RGBA_ADD)
    return pygame.mask.from_surface(block_canvas, THRESHOLD)


def game_over(frames):
    """Print score etc, exit cleanly"""
    score = int(frames / 10)  # Tweak as desired
    print("Game over, score: {0}".format(score))
    # Draw a red cross on the Hat, wait a bit, then display the score there,
    canvas = blank_canvas(ACTUAL_SIZE)
    pygame.draw.line(canvas, Color('red'), (0, 0), (WIDTH - 1, ACTUAL_HEIGHT - 1))
    pygame.draw.line(canvas, Color('red'), (WIDTH - 1, 0), (0, ACTUAL_HEIGHT - 1))
    sensehat_display(canvas)
    pygame.time.wait(2000)
    sense.show_message("Score: " + str(score), text_colour=Color('navyblue')[:3])


class Block:
    """
    Class to hold block data: Shape of block, variants of this shape when
    rotated and associated helpful data to enable easy access of the next
    variant if rotated [clockise]. The variants are pre-generated
    which means that the work to compute the rotated variants doesn't need to
    happen during the game - although that's only a theoretical concern with
    the Pi having a huge amount of processing power compared to the
    transformations for the very small number of pixels in the shapes being
    computed.
    """
    blocks_data = blockdata_list()

    def __init__(self):
        """
        Create a new block (select the shape randomly) and populate
        related attributes like self.permutations, self.rotated etc.
        """
        self._block = self.blocks_data[random.randrange(0, len(self.blocks_data))]
        self.permutations = len(self._block)
        self.index = 0
        self.set_shape_attributes()

    @property
    def rotated_clockwise_index(self):
        return (self.index + 1) % self.permutations

    def set_shape_attributes(self):
        self.current_shape = self._block[self.index]
        self.rotated = self._block[self.rotated_clockwise_index]

    def rotate_clockwise(self):
        """Adjust Block +  attributes corresponding to clockwise rotation"""
        self.index = self.rotated_clockwise_index
        self.set_shape_attributes()


class MyPlayarea:
    """Class to keep track of game 'Surface' and current falling block."""

    def __init__(self, size=WORKAREA_SIZE):
        pygame.init()  # In case not already called
        self.background = blank_canvas()  # For blocks that have landed
        self.width = size[0]
        self.height = size[1]
        self.setup_new_block()

    def setup_new_block(self):
        block = Block()
        new_x = 3
        new_y = WORKAREA_HEIGHT - ACTUAL_HEIGHT - block.current_shape.get_height()
        if self.can_place_block_here(block.current_shape, new_x, new_y):
            self.block = block
            self.block_x = new_x
            self.block_y = new_y
            return True
        else:
            return False

    def can_place_block_here(self, block, x, y):
        """
        Check if having block at (x,y) would exceed the borders of workarea.
        Check for no collision with the existing contents of workarea if block
        is placed at position (x,y).
        """
        border_violation = x < 0 or y < 0 or \
                           x + block.get_width() > self.width or \
                           y + block.get_height() > self.height
        background_mask = pygame.mask.from_surface(self.background, THRESHOLD)
        collision = background_mask.overlap(block_mask(block, x, y), (0, 0))
        return not (border_violation or collision)

    def block_move(self, dx, dy):
        """
        See if there is empty space available if the current falling block
        moves by (dx, dy) pixels.
        """
        if self.can_place_block_here(self.block.current_shape, self.block_x + dx,
                                     self.block_y + dy):
            self.block_x = self.block_x + dx
            self.block_y = self.block_y + dy
            return True
        else:
            return False

    def block_rotate(self):
        """
        See if there is empty space available if the current falling block
        is rotated clockwise by 90 degrees.
        """
        if self.can_place_block_here(self.block.rotated, self.block_x, self.block_y):
            self.block.rotate_clockwise()
            return True
        else:  # Try wiggling rotated block 1 pixel right or left before giving up,
            if self.can_place_block_here(self.block.rotated, self.block_x + 1, self.block_y):
                self.block.rotate_clockwise()
                return self.block_move(1, 0)  # True expected since can_place_block_here()
            if self.can_place_block_here(self.block.rotated, self.block_x - 1, self.block_y):
                self.block.rotate_clockwise()
                return self.block_move(-1, 0)  # True expected since can_place_block_here()
            return False

    def add_block_to_background(self):
        """
        Add the current falling block at its current position and orientation to the background
        """
        self.background.blit(self.block.current_shape, [self.block_x, self.block_y],
                             special_flags=pygame.BLEND_RGBA_ADD)

    def render(self):
        '''Renders background and block in current position'''
        screen = blank_canvas()
        screen.blit(self.background, (0, 0))
        screen.blit(self.block.current_shape, [self.block_x, self.block_y],
                    special_flags=pygame.BLEND_RGBA_ADD)
        sensehat_display(screen)

    def remove_full_lines(self):
        """
        Remove any complete horizontal lines in workarea; move content above the
        removed line down into the space created by the line removal.
        """
        background_mask = pygame.mask.from_surface(self.background, THRESHOLD)
        for row in range(self.height):
            check_area = blank_canvas()
            pygame.draw.line(check_area, Color('white'), (0, row), (self.width - 1, row))
            check_area_mask = pygame.mask.from_surface(check_area, THRESHOLD)
            if background_mask.overlap_area(check_area_mask, (0, 0)) == self.width:
                # Remove full lines by setting the clipping area to be a rectangle
                # from the top to the line to be removed, and scrolling down,
                self.background.set_clip(pygame.Rect((0, 0), (self.width, row + 1)))
                self.background.scroll(0, 1)
                self.background.set_clip(None)
                background_mask = pygame.mask.from_surface(self.background, THRESHOLD)


def run_game():
    sense.clear()
    pygame.init()

    # pygame.display not used, but needed to capture keyboard events
    pygame.display.set_mode((1, 1))

    clock = pygame.time.Clock()
    s = MyPlayarea()
    frames = 0
    frames_before_drop = BASE_FRAMES
    drop_block = False
    try:
        while True:
            frames += 1
            # One move event per frame simplifies collision detection and plays better
            moved = False
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    return
                if drop_block and event.type == pygame.KEYUP and event.key == pygame.K_DOWN:
                    drop_block = False
                if event.type == pygame.KEYDOWN:
                    if event.key in (pygame.K_ESCAPE, pygame.K_q):
                        return
                    if not drop_block and not moved:
                        if event.key == pygame.K_LEFT:
                            moved = s.block_move(-1, 0)
                        if event.key == pygame.K_RIGHT:
                            moved = s.block_move(1, 0)
                        if event.key == pygame.K_UP:
                            moved = s.block_rotate()
                        if event.key == pygame.K_DOWN:
                            drop_block = True
            frames_before_drop -= 1
            if frames_before_drop == 0:
                if s.block_move(0, 1):  # Move down
                    pass
                else:  # Collision downwards
                    s.add_block_to_background()
                    s.remove_full_lines()
                    if not s.setup_new_block():
                        break  # New block collides with existing blocks
                    drop_block = False
                # Progressively reduce to make game harder,
                frames_before_drop = BASE_FRAMES - int(math.log(frames, 5))
            s.render()
            if drop_block:
                clock.tick(FRAME_RATE * BASE_FRAMES)  # Fast descent
            else:
                clock.tick(FRAME_RATE)
    except KeyboardInterrupt:
        return
    game_over(frames)

def cleanup_game():
    sense.clear()
    pygame.quit()

if __name__ == '__main__':
    run_game()
    sense.clear()
    pygame.quit()
