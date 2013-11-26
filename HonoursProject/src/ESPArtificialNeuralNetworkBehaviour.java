import java.util.Vector;


public class ESPArtificialNeuralNetworkBehaviour extends Behaviour{
	ESPArtificialNeuralNetwork ANN;
	private final int NUMINPUTNODES = 10;
	
	public ESPArtificialNeuralNetworkBehaviour(int boardsize, ESPArtificialNeuralNetwork ANN) {
		super(boardsize);
		this.ANN = ANN;
	}
	
	// For now it will go for the first prey in the prey vector
	public int getMove(Point myPos, Vector<Piece> predatorPieces, Vector<Piece> preyPieces) {
		Vector<Integer> offsets = new Vector<Integer>();
		for(int i = 0; i < 10; i++) {
			offsets.add(0);
		}
		for(Piece preyPiece : preyPieces) {
			Point offset = Point.getSmallestOffset(myPos, preyPiece.getPosition(), boardSize);
			offsets.set(2*preyPiece.getID(), offset.x);
			offsets.set(2*preyPiece.getID() + 1, offset.y);
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
