"""
 file: main.py
 
 author: David Shorten
 date: 19 June 2013
 
 Simple program which takes the file record of a simulation and produces a 
 visualisation of it.
"""

import sys
import pygame

# Pixel length of the square cells, this must always be even.
CELLLENGTH = 20
# Frames per second.
FRAMERATE = 4
BACKGROUND_COLOR = (255, 255, 255)
LINE_COLOR = (0, 0, 255)
PREY_COLOR = (0, 255, 0)
PREDATOR_COLOR = (255, 0, 0)

inputFile = open(sys.argv[1])

# Find out the length of the square grid.
length = 0;
for line in inputFile:
    if line[0] == "\n":
        break
    length += 1

screen = pygame.display.set_mode([CELLLENGTH * length, CELLLENGTH * length])
        
# Separate the file into its blocks.
turn = []
turns = []
for line in inputFile:
    if line[0] == "\n":
        turns.append(turn)
        turn = []
    else:
        turn.append(line)

clock = pygame.time.Clock()

for turn in turns:

    # Limits the number of loop iterations per second.
    clock.tick(FRAMERATE)

    # Make the background and grid.    
    screen.fill(BACKGROUND_COLOR)
    for row in xrange(length - 1):
        pygame.draw.line(screen, LINE_COLOR, [0, (row + 1) * CELLLENGTH], [length * CELLLENGTH, (row + 1) * CELLLENGTH])
    for col in xrange(length - 1):
        pygame.draw.line(screen, LINE_COLOR, [(col + 1) * CELLLENGTH, 0], [(col + 1) * CELLLENGTH, length * CELLLENGTH])
    
    # Print the pieces.    
    row = 0
    for line in turn:
        col = 0
        for char in line:
            if char == '1':
                pygame.draw.circle(screen, PREY_COLOR, [int((row + 0.5) * CELLLENGTH), int((col + 0.5) * CELLLENGTH)], int(CELLLENGTH / 2))
            elif char == '2':
                pygame.draw.circle(screen, PREDATOR_COLOR, [int((row + 0.5) * CELLLENGTH), int((col + 0.5) * CELLLENGTH)], int(CELLLENGTH / 2))
            col += 1
        row += 1    
            
    pygame.display.flip()