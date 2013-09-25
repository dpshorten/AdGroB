import java.util.Vector;


public class ESPArtificialNeuralNetworkBehaviour extends Behaviour{
	ESPArtificialNeuralNetwork ANN;
	
	public ESPArtificialNeuralNetworkBehaviour(int boardsize, ESPArtificialNeuralNetwork ANN) {
		super(boardsize);
		this.ANN = ANN;
	}
	
	// Will go for the closest prey
	public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) {
		
		double minDist = Double.MAX_VALUE;
		Piece closestPrey = null;
		for(Piece aPrey : prey){
			Point offset = Point.getSmallestOffset(myPos, aPrey.getPosition(), boardSize);
			double dist = Math.sqrt(offset.x * offset.x + offset.y * offset.y);
			if(dist < minDist){
				minDist = dist;
				closestPrey = aPrey;
			}
		}
		
		Point offset = Point.getSmallestOffset(myPos, closestPrey.getPosition(), boardSize);
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
