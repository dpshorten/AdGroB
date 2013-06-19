import sys
import pygame

CELLLENGTH = 20

inputFile = open(sys.argv[1])

length = 0;
for line in inputFile:
    if line[0] == "\n":
        break
    length += 1
        
screen = pygame.display.set_mode([CELLLENGTH * length, CELLLENGTH * length])

# Separate the file into its blocks
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

    clock.tick(4)
    
    screen.fill((255, 255, 255))
    
    for row in xrange(length - 1):
        pygame.draw.line(screen, (0, 0, 255), [0, (row + 1) * CELLLENGTH], [length * CELLLENGTH, (row + 1) * CELLLENGTH])
    for col in xrange(length - 1):
        pygame.draw.line(screen, (0, 0, 255), [(col + 1) * CELLLENGTH, 0], [(col + 1) * CELLLENGTH, length * CELLLENGTH])
        
    row = 0
    for line in turn:
        col = 0
        for char in line:
            if char == '1':
                pygame.draw.circle(screen, (0, 255, 0), [int((row + 0.5) * CELLLENGTH), int((col + 0.5) * CELLLENGTH)], int(CELLLENGTH / 2))
            elif char == '2':
                pygame.draw.circle(screen, (255, 0, 0), [int((row + 0.5) * CELLLENGTH), int((col + 0.5) * CELLLENGTH)], int(CELLLENGTH / 2))
            col += 1
        row += 1    
            
    pygame.display.flip()