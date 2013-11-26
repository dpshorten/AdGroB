import java.util.Vector;


public class ESPArtificialNeuralNetworkBehaviour extends Behaviour{
	ESPArtificialNeuralNetwork ANN;
	private final int NUM_INPUT_NODES = 16;
	
	public ESPArtificialNeuralNetworkBehaviour(int boardsize, ESPArtificialNeuralNetwork ANN) {
		super(boardsize);
		this.ANN = ANN;
	}
	
	// For now it will go for the first prey in the prey vector
	public int getMove(Point myPos, Vector<Piece> predatorPieces, Vector<Piece> preyPieces) {
		Vector<Integer> offsets = new Vector<Integer>();
		for(int i = 0; i < NUM_INPUT_NODES; i++) {
			offsets.add(0);
		}
		for(Piece predatorPiece : predatorPieces) {
			Point offset = Point.getSmallestOffset(myPos, predatorPiece.getPosition(), boardSize);
			offsets.set(2*predatorPiece.getID(), offset.x);
			offsets.set(2*predatorPiece.getID() + 1, offset.y);
		}
		for(Piece preyPiece : preyPieces) {
			Point offset = Point.getSmallestOffset(myPos, preyPiece.getPosition(), boardSize);
			offsets.set(2*(predatorPieces.size() + preyPiece.getID()), offset.x);
			offsets.set(2*(predatorPieces.size() + preyPiece.getID()) + 1, offset.y);
		}
		totalNumberOfDecisions++;
		return getIndexOfMaximumActivation(offsets);
	}
	
	public int getIndexOfMaximumActivation(Vector<Integer> offsets) {
		double[] activations = ANN.run(offsets);
		int indexOfMaximumActivation = 0;
		double currentMax = 0;
		for(int i = 0; i < activations.length; i++) {
			if (activations[i] > currentMax) {
				currentMax = activations[i];
				indexOfMaximumActivation = i;
			}
		}		
		return indexOfMaximumActivation;
	}
}
