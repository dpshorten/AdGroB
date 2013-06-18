public class Piece
{

	int positionX;
	int positionY;

	int moveX;
	int moveY;

	int move [][] = new int [1][1];

	boolean isPrey;


	public Piece()
	{
		moveX = -1;
		moveY = -1;

		positionX = 0;
		positionY = 0;
		
		isPrey = false;
	}
	
	public Piece(boolean prey)
	{
		moveX = -1;
		moveY = -1;

		positionX = 0;
		positionY = 0;
		
		isPrey = prey;
	}

	public Piece(int x, int y)
	{
		positionX = x;
		positionY = y;
		moveX = -1;
		moveY = -1;
		
		isPrey = false;
	}
	
	public Piece(int x, int y,boolean prey)
	{
		positionX = x;
		positionY = y;
		moveX = -1;
		moveY = -1;
		
		isPrey = prey;
	}

	int getPositionX() 
	{
		return positionX;
	}

	int getPositionY() 
	{
		return positionY;
	}

	void setPosition(int newX, int newY) 
	{
		positionX = newX;
		positionY = newY;
	}

	int getMoveX() 
	{
		return moveX;
	}

	int getMoveY() 
	{
		return moveY;
	}
}