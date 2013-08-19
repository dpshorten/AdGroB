import java.util.Vector;


public class SimpleNeuralNetworkBehaviour extends Behaviour{
	SimpleNeuralNetwork ANN;
	
	public SimpleNeuralNetworkBehaviour(int boardsize, SimpleNeuralNetwork ANN) {
		super(boardsize);
		this.ANN = ANN;
	}
	
	// For now it will go for the first prey in the prey vector
	public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) {
		Point offset = Point.getSmallestOffset(myPos, prey.get(0).getPosition(), boardSize);
		offsetHistory.elementAt(offset.x + halfBoardSize).set(offset.y + halfBoardSize, 
				offsetHistory.elementAt(offset.x + halfBoardSize).elementAt(offset.y + halfBoardSize) + 1);
		totalNumberOfDecisions++;
		return getIndexOfMaximumActivation(offset.x, offset.y);
	}
	
	public int getIndexOfMaximumActivation(int xOffset, int yOffset) {
		double[] activations = ANN.run((double)xOffset, (double)yOffset);
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
