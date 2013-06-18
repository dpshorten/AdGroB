import java.util.*;

public class Environment
{
	// variable to set the Board size
	int boardSize;
	// 2D grid to represent the board
	int[][] board;

	boolean Gameover = false;

	Vector<Piece> predator = new Vector<Piece>();
	Vector<Piece> prey = new Vector<Piece>();
	Vector<Piece> pieces = new Vector<Piece>();

	public Environment()
	{
		boardSize = 5;
		board = new int[boardSize][boardSize];
	}

	public Environment(int size)
	{
		boardSize = size;
		board = new int[boardSize][boardSize];
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

	public void drawWorld(int [][] board)
	{
		// ********************* Loop throught board and draw elements in the grid *************************

		for(int j = 0 ; j < boardSize ; j ++)
		{
			for (int k = 0 ; k < boardSize ; k ++)
			{
				if(board[j][k] == 1)
					System.out.print("|_x_|");

				else if(board[j][k] == 2)
					System.out.print("|_o_|");

				else
					System.out.print("|___|");
			}
			System.out.println();

		}	

		System.out.println();
		System.out.println();
		System.out.println();

		// ************************************************************************************************
	}

	public int getBoardSize()
	{
		return boardSize;
	}

	public Piece getNextPiece()
	{
		Piece nextPiece = pieces.firstElement();

		pieces.add(pieces.firstElement());
		pieces.remove(0);

		return nextPiece;
	}

	public boolean isGameover()
	{
		return Gameover;
	}

	public void run(int pred, int pry)
	{

		for(int i = 0 ; i < pred; i ++)
		{
			predator.add(new Piece(2,2));
		}

		for(int i = 0 ; i < pry; i ++)
		{
			prey.add(new Piece(0,0,true));
		}

		int runs = 20;
		
		while (runs > 0)
		{
			handleMovement();
			runs--;
		}

	}

}