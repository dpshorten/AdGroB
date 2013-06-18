public class Predator extends Piece
{

	public Predator()
	{
		moveX = -1;
		moveY = -1;
		
		positionX = 0;
		positionY = 0;
	}
	
	public Predator(int x, int y)
	{
		positionX = x;
		positionY = y;
		moveX = -1;
		moveY = -1;
	}
	
	@Override
	int getPositionX() 
	{
		return positionX;
	}

	@Override
	int getPositionY() 
	{
		return positionY;
	}

	@Override
	void setPosition(int newX, int newY) 
	{
		positionX = newX;
		positionY = newY;
	}

	@Override
	int getMoveX() 
	{
		return moveX;
	}

	@Override
	int getMoveY() 
	{
		return moveY;
	}
	
}