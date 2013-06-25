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
		boardSize = 100;
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


	public SimulationResult run(int numPred, int numPrey)
	{
		// Clear the log file.
		try {
			out = new PrintWriter(new FileWriter(log, false));
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
		out.write("");
		out.close();
		
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize);
		Vector<Genotype> genotypes1 = new Vector<Genotype>();
		for(int i = 0; i < 10; i++) {
			genotypes1.add(new Genotype());
		}
		Vector<Genotype> genotypes2 = new Vector<Genotype>();
		for(int i = 0; i < 10; i++) {
			genotypes2.add(new Genotype());
		}
		Vector<Genotype> genotypes3 = new Vector<Genotype>();
		for(int i = 0; i < 10; i++) {
			genotypes3.add(new Genotype());
		}
		ESPArtificialNeuralNetwork ESPTestNetwork1 = new ESPArtificialNeuralNetwork(genotypes1);  
		ESPArtificialNeuralNetwork ESPTestNetwork2 = new ESPArtificialNeuralNetwork(genotypes2);  
		ESPArtificialNeuralNetwork ESPTestNetwork3 = new ESPArtificialNeuralNetwork(genotypes3);  
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour1 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork1);  
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour2 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork2);  
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour3 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork3);  
		
		predators.add(new Piece(2,3,false,this,ESPTestBehaviour1));
		predators.add(new Piece(2,7,false,this,ESPTestBehaviour2));
		predators.add(new Piece(2,9,false,this,ESPTestBehaviour3));
		pieces.add(predators.elementAt(0));
		pieces.add(predators.elementAt(1));
		pieces.add(predators.elementAt(2));
		for (int i = 0; i < numPrey; i++) {
			prey.add(new Piece(0, i, true, this, runAway));
			pieces.add(prey.elementAt(i));
		}
		// Draw the first turn.
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
