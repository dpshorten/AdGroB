import java.util.Vector;


public class ESPArtificialNeuralNetworkBehaviour extends Behaviour{
	ESPArtificialNeuralNetwork ANN;
	
	public ESPArtificialNeuralNetworkBehaviour(int boardsize, ESPArtificialNeuralNetwork ANN) {
		super(boardsize);
		this.ANN = ANN;
	}
	
	// For now it will go for the first prey in the prey vector
	public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) {
		Point offset = Point.getSmallestOffset(myPos, prey.get(0).getPosition(), boardSize);
		offsetHistory.elementAt(offset.x).set(offset.y, 
				offsetHistory.elementAt(offset.x).elementAt(offset.y) + 1);
		totalNumberOfDecisions++;
		double[] activations = ANN.run((double)offset.x(), (double)offset.y());
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
