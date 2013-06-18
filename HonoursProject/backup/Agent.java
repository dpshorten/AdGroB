abstract class Agent
{
	
	int positionX;
	int positionY;
	
	int moveX;
	int moveY;
	
	int move [][] = new int [1][1];
	
	
	abstract int getPositionX();
	abstract int getPositionY();
	
	abstract void setPosition(int newX, int newY);
	
	abstract int getMoveX();
	abstract int getMoveY();
	
}