import java.io.*;
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

	File log = new File("log.txt");
	
	PrintWriter out;

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

	public void handleMovement () throws Exception
	{
		// ******************************** Movement Handling ******************************************************

		for(Piece p : pieces)
		{
			clearBoard(p);              // removes current piece from the board until position is updated.
			p.makeMove();				// Gets a position update for a piece.
			updateBoard(p);				// Updates the board to include the new position of the piece.
		}
	}

	public void clearBoard(Piece p)
	{
		board[p.pos.x][p.pos.y] = 0;
	}

	public void updateBoard(Piece p)
	{
		if(p.isPrey)
			board[p.pos.x][p.pos.y] = 2;
		else
			board[p.pos.x][p.pos.y] = 1;

		drawWorld();
	}

	public void drawWorld()
	{
		// ********************* Loop throught board and draw elements in the grid *************************
		try
		{
			out = new PrintWriter(new FileWriter(log, true));
		}
		catch(Exception e)
		{
			System.out.println("Error : " + e);
		}

		for(int j = 0 ; j < boardSize ; j ++)
		{
			for (int k = 0 ; k < boardSize ; k ++)
			{
				if(board[j][k] == 1)
					out.print("1");

				else if(board[j][k] == 2)
					out.print("2");

				else
					out.print("0");
			}
			out.println();

		}
		out.println();
		
		out.close();

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
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize);
		
		for(int i = 0 ; i < pred; i ++)
		{
			predator.add(new Piece(2,2,false,this,runAway));
			pieces.add(predator.elementAt(i));
		}

		for(int i = 0 ; i < pry; i ++)
		{
			prey.add(new Piece(0,0,true,this,runAway));
			pieces.add(prey.elementAt(i));
		}

		int runs = 20;

		while (runs > 0)
		{
			try{
				handleMovement();
			}
			catch(Exception e){
				System.out.println("An error occured: "+e.getMessage());
				e.printStackTrace();
			}
			runs--;
		}

	}

}