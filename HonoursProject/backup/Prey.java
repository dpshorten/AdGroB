public class Prey extends Piece
{

	public Prey()
	{
		moveX = 0;
		moveY = 0;
		positionX = 1;
		positionY = 1;
	}
	
	public Prey(int x, int y)
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