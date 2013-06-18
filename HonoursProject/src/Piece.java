public class Piece
{

	int positionX;
	int positionY;

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

	public void handleMovement ()
	{

		// ******************************** Movement Handling ******************************************************
		if(predator.size() > 0)
		{

			for(Piece p : predator)
			{

				board [p.getPositionX()] [p.getPositionY()] = 0;

				// ********************  X coordinate Change ************************************************************

				if (p.getMoveX() == 1)                                                         // move right on board
				{
					if(p.getPositionX() +1 >= boardSize)
						p.setPosition(0, p.getPositionY());                            // right hand side met, move to left

					else
						p.setPosition(p.getPositionX()+1, p.getPositionY());   // just move one right
				}

				else if(p.getMoveX() == -1)                                                    // move left on board
				{
					if(p.getPositionX() -1 < 0)
						p.setPosition(boardSize -1, p.getPositionY());                            // right hand side met, move to left

					else
						p.setPosition(p.getPositionX()-1, p.getPositionY());   // just move one right
				}

				else {} // do nothing to X coordinate.

				// *******************    Y coordinate change **************************************************************

				if(p.getMoveY() == 1)                                                          // move down board
				{
					if(p.getPositionY() +1 >= boardSize)
						p.setPosition(p.getPositionX(), 0);                            // bottom reached, move to top

					else
						p.setPosition(p.getPositionX(), p.getPositionY() + 1); // just move one up
				}

				else if (p.getMoveY() == -1)                                                   // move up board
				{
					if(p.getPositionY() -1 < 0)
						p.setPosition(p.getPositionX(), boardSize-1);                  // top reached, move to bottom

					else
						p.setPosition(p.getPositionX(), p.getPositionY() - 1); // just move one down
				}

				else {} // do nothing to Y coordinate

				board [p.getPositionX()] [p.getPositionY()] = 1;

				for(Piece b : prey)
				{
					if((p.getPositionX() == b.getPositionX()) && (p.getPositionY() == b.getPositionY()))
					{
						prey.remove(b);

						if(prey.size() <= 0)
							Gameover = true;
					}

				}

				drawWorld( board );
			}
		}

		//***********************************************************************************************************
		if(prey.size() > 0)
		{
			for(Piece p : prey)
			{

				board [p.getPositionX()] [p.getPositionY()] = 0;

				// ********************  X coordinate Change ************************************************************

				if (p.getMoveX() == 1)                                                         // move right on board
				{
					if(p.getPositionX() +1 >= boardSize)
						p.setPosition(0, p.getPositionY());                            // right hand side met, move to left

					else
						p.setPosition(p.getPositionX()+1, p.getPositionY());   // just move one right
				}

				else if(p.getMoveX() == -1)                                                    // move left on board
				{
					if(p.getPositionX() -1 < 0)
						p.setPosition(boardSize -1, p.getPositionY());                            // right hand side met, move to left

					else
						p.setPosition(p.getPositionX()-1, p.getPositionY());   // just move one right
				}

				else {} // do nothing to X coordinate.

				// *******************    Y coordinate change **************************************************************

				if(p.getMoveY() == 1)                                                  // move down board
				{
					if(p.getPositionY() +1 >= boardSize)
						p.setPosition(p.getPositionX(), 0);                            // bottom reached, move to top

					else
						p.setPosition(p.getPositionX(), p.getPositionY() + 1);         // just move one up
				}

				else if (p.getMoveY() == -1)                                           // move up board
				{
					if(p.getPositionY() -1 < 0)
						p.setPosition(p.getPositionX(), boardSize-1);                  // top reached, move to bottom

					else
						p.setPosition(p.getPositionX(), p.getPositionY() - 1);         // just move one down
				}

				else {} // do nothing to Y coordinate

				board [p.getPositionX()] [p.getPositionY()] = 2;

				drawWorld( board );
			}
		}

		//***********************************************************************************************************

	}
}