import java.util.Random;
import java.util.Vector;


public class SocialEntropyBehaviourMeasurement {
	private static final int numberOfSimulations = 1000;
	
	// In the returned double[][]: [x][y] is the similarity of predators x and y.
	public static double[][] measureSimilarity(Vector<Piece> predators, int boardSize, Environment env) {

		int halfBoardSize = (int)Math.floor(boardSize/((double)2));
		for (Piece predator : predators) {
			predator.getBehaviour().resetHistory();
		}
		
		// Run tests in order to get the probabilities of the inputs.
		//VectorRunAwayBehaviour runAway = new VectorRunAwayBehaviour(boardSize);
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 1);
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
		

		// Calculate all the predators' ANNs outputs.
		Vector<Vector<Vector<Integer>>> predatorOutputs = new Vector<Vector<Vector<Integer>>>();
		for(Piece predator : predators) {
			Vector<Vector<Integer>> thisPredatorsOutputs = new Vector<Vector<Integer>>(); 
			for(int i = 0; i < boardSize; i++) {
				Vector<Integer> rowOfOutputs = new Vector<Integer>();
				for(int j = 0; j < boardSize; j++) {
					rowOfOutputs.add(((ESPArtificialNeuralNetworkBehaviour)predator.behaviour)
							.getIndexOfMaximumActivation(i - halfBoardSize,j - halfBoardSize));
				}
				thisPredatorsOutputs.add(rowOfOutputs);
			}
			predatorOutputs.add(thisPredatorsOutputs);
		}
		
		/*
		for(Vector<Integer> row : predatorOutputs.get(0)) {
			for(Integer elem : row) {
				System.out.print(elem + " ");
			}
			System.out.println();
		}
		*/
		
		double[][] returnedSimilarities = new double[predators.size()][predators.size()];
		Vector<Vector<Vector<Double>>> predatorInputProbabilitiesClone = 
				(Vector<Vector<Vector<Double>>>)predatorInputProbabilities.clone();
		Vector<Vector<Vector<Integer>>> predatorOutputsClone = 
				(Vector<Vector<Vector<Integer>>>)predatorOutputs.clone();
		for(int i = 0; i < predatorInputProbabilities.size(); i++){
			for(int j = 1; j < predatorInputProbabilitiesClone.size(); j++){
				double similarityMeasure = 0;
				for(int row = 0; row < boardSize; row++) {
					for(int col = 0; col < boardSize; col++) {
						if(predatorOutputs.get(i).get(row).get(col) != 
								predatorOutputsClone.get(j).get(row).get(col)) {
							similarityMeasure += (predatorInputProbabilities.get(i).get(row).get(col) + 
									predatorInputProbabilitiesClone.get(j).get(row).get(col))/2;
						}
					}
				}
				
				returnedSimilarities[i][j + i] += similarityMeasure;
				returnedSimilarities[j + i][i] += similarityMeasure;
			}
			predatorInputProbabilitiesClone.remove(0);
			predatorOutputsClone.remove(0);
		}
		return returnedSimilarities;
	}
}
