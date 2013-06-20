import java.io.*;
import java.util.*;

public class Environment {
	// variable to set the Board size
	int boardSize;
	// 2D grid to represent the board
	int[][] board;
	
	private final int MAXMOVES = 100;

	boolean Gameover = false;

	Vector<Piece> predators = new Vector<Piece>();
	Vector<Piece> prey = new Vector<Piece>();
	Vector<Piece> pieces = new Vector<Piece>();

	File log = new File("log.txt");

	PrintWriter out;

	public Environment() {
		boardSize = 2;
		board = new int[boardSize][boardSize];
	}

	public Environment(int size) {
		boardSize = size;
		board = new int[boardSize][boardSize];
	}

	public void clearBoard(Piece p) {
		board[p.pos.x][p.pos.y] = 0;
	}

	public void updateBoard(Piece p) {
		if (p.isPrey)
			board[p.pos.x][p.pos.y] = 2;
		else
			board[p.pos.x][p.pos.y] = 1;
	}

	public void drawWorld() {
		// ********************* Loop throught board and draw elements in the
		// grid *************************
		try {
			out = new PrintWriter(new FileWriter(log, true));
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}

		for (int j = 0; j < boardSize; j++) {
			for (int k = 0; k < boardSize; k++) {
				if (board[j][k] == 1)
					out.print("1");

				else if (board[j][k] == 2)
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

	public int getBoardSize() {
		return boardSize;
	}

	/*public Piece getNextPiece() {
		Piece nextPiece = pieces.firstElement();

		pieces.add(pieces.firstElement());
		pieces.remove(0);

		return nextPiece;
	}*/

	public boolean isGameOver() {
		return !(prey.size() > 0);
	}


	public SimulationResult run(int numPred, int numPrey)
	{
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(log, false));
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
		out.write("");
		out.close();
		
		StochasticRunawayBehaviour runAway = new StochasticRunawayBehaviour(boardSize);
		
		for(int i = 0 ; i < numPred; i ++)
		{
			predators.add(new Piece(2,i,false,this,runAway));
			pieces.add(predators.elementAt(i));
		}

		for (int i = 0; i < numPrey; i++) {
			prey.add(new Piece(0, i, true, this, runAway));
			pieces.add(prey.elementAt(i));
		}
		for(Piece piece : pieces) {
			updateBoard(piece);
		}
		drawWorld();

		int preyCaught = 0;
		int i = 0;
		for (i = 0; i < MAXMOVES; i++) {
			Piece poppedPiece = pieces.remove(0);
			// Removes current piece from the board until position is updated.			
			clearBoard(poppedPiece); 
			// Gets a position update for a piece.
			try {
				poppedPiece.makeMove();
			} catch (Exception e) {
				System.out.println("Error : " + e);
			}
			pieces.add(poppedPiece);
			// We have to update the position of all pieces to cover the case 
			// where a piece moves off of a piece it was covering
			for (Piece piece : pieces) {
				updateBoard(piece); 
			}
			drawWorld();
			// Add caught prey to a list of to-be-removed pieces.
			Vector<Piece> removals = new Vector<Piece>();
			for (Piece pred : predators) {
				for (Piece aPrey : prey) {
					if (pred.getPosition().equals(aPrey.getPosition())) {
						preyCaught++;
						removals.add(aPrey);
						clearBoard(aPrey);
						// In case the prey was on top of the predator
						// the predator must be reprinted.
						updateBoard(pred);
						drawWorld();
					}
				}
			}
			for(Piece piece : removals) {
				prey.remove(piece);
				pieces.remove(piece);
			}
			if(isGameOver()) {
				break;
			}
		}
		return new SimulationResult(i + 1, preyCaught);
	}
}
