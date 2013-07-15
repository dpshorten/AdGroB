import java.util.Random;
import java.util.Vector;


public class SocialEntropyBehaviourMeasurement {
	private static final int numberOfSimulations = 1000;
	
	// In the returned double[][]: [x][y] is the similarity of predators x and y.
	public static double[][] measureSimularity(Vector<Piece> predators, int boardSize, Environment env) {

		// Run tests in order to get the probabilities of the inputs.
		for (Piece predator : predators) {
			predator.getBehaviour().resetHistory();
		}

		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
						boardSize, 1);
		Random random = new Random();
		for (int i = 0; i < numberOfSimulations; i++) {
			Vector<Piece> testPreyPieces = new Vector<Piece>();
			testPreyPieces.add(new Piece(random.nextInt(boardSize), random
					.nextInt(boardSize), true, env, runAway));
			env.setPieces(predators, testPreyPieces);
			env.run(false, false);
		}
		
		// Compute probability values for each input.
		Vector<Vector<Vector<Double>>> predatorInputProbabilities = new Vector<Vector<Vector<Double>>>();
		for(Piece predator : predators) {
			Vector<Vector<Double>> thisPredatorsProbabilities = new Vector<Vector<Double>>(); 
			int sum = 0;
			for(Vector<Integer> row : predator.getBehaviour().offsetHistory) {
				for(Integer frequency : row) {
					sum += frequency;
				}
			}
			for(Vector<Integer> row : predator.getBehaviour().offsetHistory) {
				Vector<Double> thisPredatorsRow = new Vector<Double>();
				for(Integer frequency : row) {
					thisPredatorsRow.add(frequency/((double)sum));
				}
				thisPredatorsProbabilities.add(thisPredatorsRow);
			}
			predatorInputProbabilities.add(thisPredatorsProbabilities);
		}
		/*
		for(Vector<Double> row : predatorInputProbabilities.get(0)) {
			for(Double elem : row) {
				System.out.print(elem + " ");
			}
			System.out.println();
		}*/

		// Calculate all the predators' ANNs outputs.
		Vector<Vector<Vector<Integer>>> predatorOutputs = new Vector<Vector<Vector<Integer>>>();
		for(Piece predator : predators) {
			Vector<Vector<Integer>> thisPredatorsOutputs = new Vector<Vector<Integer>>(); 
			for(int i = 0; i < boardSize; i++) {
				for(int j = 0; j < boardSize; j++) {
					
				}
			}
		}
		return new double[10][10];
	}
}
