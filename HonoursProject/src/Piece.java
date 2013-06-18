public class Piece
{

	int positionX;
	int positionY;
	int boardSize = 5;

	boolean isPrey;


	public Piece()
	{

		positionX = 0;
		positionY = 0;

		isPrey = false;
	}

	public Piece(boolean prey)
	{

		positionX = 0;
		positionY = 0;

		isPrey = prey;
	}

	public Piece(int x, int y)
	{
		positionX = x;
		positionY = y;


		isPrey = false;
	}

	public Piece(int x, int y,boolean prey)
	{
		positionX = x;
		positionY = y;


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

	void makeMove()
	{
		positionX += 1;
		positionY += 1;

		handleMovement();

	}

	public void handleMovement ()
	{

		// ******************************** Movement Handling ******************************************************

		if(positionX >= boardSize)
			positionX = 0;                            // right hand side met, move to left



		if(positionX < 0)
			positionX = boardSize -1;				// left hand side met, move to the right


		// *******************    Y coordinate change **************************************************************

		if(positionY >= boardSize)
			positionY = 0;                            // bottom reached, move to top


		if(positionY < 0)
			positionY = boardSize-1;                  // top reached, move to bottom



		//***********************************************************************************************************

	}
}