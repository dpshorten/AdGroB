import java.io.*;
import java.util.*;

public class Environment {
	// variable to set the Board size
	int boardSize;
	// 2D grid to represent the board
	int[][] board;
	
	private int maxMoves;
	
	boolean Gameover = false;

	Vector<Piece> predators = new Vector<Piece>();
	Vector<Piece> prey = new Vector<Piece>();
	Vector<Piece> pieces = new Vector<Piece>();
	Vector<Piece> caughtPieces = new Vector<Piece>();

	File log = new File("log.txt");

	PrintWriter out;

	public Environment() {
		boardSize = 100;
		maxMoves = 8 * boardSize;
		board = new int[boardSize][boardSize];
	}

	public Environment(int size) {
		boardSize = size;
		maxMoves = 8 * boardSize;
		board = new int[boardSize][boardSize];
	}

	private void clearBoard() {
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
		// ********************* Loop through board and draw elements in the
		// grid *************************
		try {
			out = new PrintWriter(new FileWriter(log, true));
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}

		for (int j = 0; j < boardSize; j++) {
			for (int k = 0; k < boardSize; k++) {
				out.print(board[j][k]);
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

	public void setPieces(Vector<Piece> predatorPieces, Vector<Piece> preyPieces){
		pieces.clear();
		predators.clear();
		prey.clear();
		predators.addAll(predatorPieces);
		prey.addAll(preyPieces);
		pieces.addAll(predatorPieces);
		pieces.addAll(preyPieces);
	}

	public SimulationResult run(boolean shouldWriteToFile, boolean shouldAppendFile)
	{	
		clearBoard();
		// Clear the log file.
		if (shouldWriteToFile & (!shouldAppendFile)) {
			try {
				out = new PrintWriter(new FileWriter(log, false));
			} catch (Exception e) {
				System.out.println("Error : " + e);
			}
			out.write("");
			out.close();
		}		
		// Draw the first turn.
		for(Piece piece : pieces) {
			updateBoard(piece);
		}
		if (shouldWriteToFile) {
			drawWorld();
		}

		//Record the distances from each predator to the prey
		//For now only looks at closest prey
		Vector<Double> initialDistancesFromPrey = new Vector<Double>();
		for(Piece predator : predators){
			double minDist = Double.MAX_VALUE;
			for(Piece prey : this.caughtPieces){
				double dist = Point.getDistance(predator.getPosition(), prey.getPosition(), boardSize);
				if(dist < minDist)
					minDist = dist;
			}
			initialDistancesFromPrey.add(minDist);
		}
		
		int preyCaught = 0;
		int i = 0;
		for (i = 0; i < maxMoves; i++) {
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
			if (shouldWriteToFile) {
				drawWorld();
			}
			// Add caught prey to a list of to-be-removed pieces.
			Vector<Piece> removals = new Vector<Piece>();
			int j = 0;
			for (Piece pred : predators) {
				for (Piece aPrey : prey) {
					if (pred.getPosition().equals(aPrey.getPosition()) & !removals.contains(aPrey)) {
						j += 1;
						if(j > 1) {
							System.out.println("foo");
						}
						preyCaught++;
						removals.add(aPrey);
						clearBoard(aPrey);
						// In case the prey was on top of the predator
						// the predator must be reprinted.
						updateBoard(pred);
						if (shouldWriteToFile) {
							drawWorld();
						}
					}
				}
			}
			for(Piece piece : removals) {
				prey.remove(piece);
				pieces.remove(piece);
				caughtPieces.add(piece);
				
			}
			if(isGameOver()) {
				break;
			}
		}
		
		//Make a vector containing the predators distances from the prey
		Vector<Double> finalDistancesFromPrey = new Vector<Double>();
		for(Piece predator : predators){
			double minDist = Double.MAX_VALUE;
			for(Piece prey : this.caughtPieces){
				double dist = Point.getDistance(predator.getPosition(), prey.getPosition(), boardSize);
				if(dist < minDist)
					minDist = dist;
			}
			finalDistancesFromPrey.add(minDist);
		}
		return new SimulationResult(i + 1, preyCaught, finalDistancesFromPrey, initialDistancesFromPrey);
	}
}
