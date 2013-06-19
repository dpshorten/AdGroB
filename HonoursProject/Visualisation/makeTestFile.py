from random import randint

LENGTH = 20
TURNS = 100

prey = [10, 10];
pred1 = [0, 0];
pred2 = [0, 1];
pred3 = [1, 0];

def permutePos(pos):
    newPos = pos[:]
    rand = randint(0, 3)
    if rand == 0:
        newPos[0] = (newPos[0] + 1) % LENGTH
    elif rand == 1:
        newPos[0] = (newPos[0] - 1) % LENGTH
    elif rand == 2:
        newPos[1] = (newPos[1] + 1) % LENGTH
    elif rand == 3:
        newPos[1] = (newPos[1] - 1) % LENGTH
    if (not equals(newPos, prey)) & (not equals(newPos, pred1)) & (not equals(newPos, pred2)) & (not equals(newPos, pred3)):
        return newPos
    else:
        return permutePos(pos)
    
def equals(list1, list2):
    return (list1[0] == list2[0]) & (list1[1] == list2[1])
    
    
f = open("test", 'w+')


for turn in xrange(TURNS):
    prey = permutePos(prey)
    for row in xrange(LENGTH):
        for col in xrange(LENGTH):
            if equals(prey, [row, col]):
                f.write("1")
            elif equals(pred1, [row, col]) or equals(pred2, [row, col]) or equals(pred3, [row, col]):
                f.write("2")
            else:
                f.write("0")
        f.write("\n")
    f.write("\n")
    pred1 = permutePos(pred1)
    pred2 = permutePos(pred2)
    pred3 = permutePos(pred3)
    
f.close()
        

    
        
        

    