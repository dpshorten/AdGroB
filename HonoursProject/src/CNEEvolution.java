import java.util.Collections;
import java.util.Random;
import java.util.Vector;


public class CNEEvolution {
	
	public static void main(String[] args) {
		(new CNEEvolution()).run();
	}
	
	public int run() {
		Random random = new Random();

		Vector<CNEGenotype> population = new Vector<CNEGenotype>();
		for (int i = 0; i < EvolutionParameters.CNEPopSize; i++) {
			population.add(new CNEGenotype());
		}

		Environment environment = null;
		int epochNumber = 0;
		int captureCount = 0;
		
		int generation = 1;
		for (generation = 1; generation <= EvolutionParameters.CNEMaxGenerations; generation++) {

			for (CNEGenotype genotype : population) {
				genotype.resetFitness();
			}

			environment = new Environment(EvolutionParameters.boardSize,
					EvolutionParameters.epochPreySpeeds[epochNumber],
					EvolutionParameters.numPredators);

			Vector<Piece> preyPieces = new Vector<Piece>();
			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
					EvolutionParameters.boardSize, 1);
			preyPieces.add(new Piece(0, 0, true, environment, runAway));

			captureCount = 0;

			// Build an ANN for each predator using a randomly chosen
			// node from each subpopulation
			for (int trial = 0; trial < EvolutionParameters.CNEPopSize; trial++) {
				CNEGenotype genotype = population.get(trial);
				Vector<Piece> predatorPieces = new Vector<Piece>();
				for (int pred = 0; pred < EvolutionParameters.numPredators; pred++) {
					ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(
							new Vector<Double>(
									genotype.getWeights()
											.subList(
													pred
															* 7
															* EvolutionParameters.numHiddenNodes,
													(pred + 1)
															* 7
															* EvolutionParameters.numHiddenNodes)),
							0);
					ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(
							EvolutionParameters.boardSize, ann);
					predatorPieces
							.add(new Piece(
									EvolutionParameters.predatorPositions[2 * pred],
									EvolutionParameters.predatorPositions[2 * pred + 1],
									false, environment, annBehaviour));
				}
				TrialResult result = trial(predatorPieces, preyPieces,
						environment, EvolutionParameters.CNEEvaluationsPerTrial);
				captureCount += result.captureCount;
				genotype.setFitness(result.avgEvalFitness);

			}// trial
			
			Collections.sort(population);
			Collections.reverse(population);

			int numberOfOffspring = EvolutionParameters.CNEPopSize / 2;
			int maxParentIndex = EvolutionParameters.CNEPopSize / 4;
			int replacementIndex = population.size() - 1;
			for (int i = 0; i < numberOfOffspring; i++) {
				CNEGenotype parent1 = population.elementAt(random
						.nextInt(maxParentIndex));
				CNEGenotype parent2 = population.elementAt(random
						.nextInt(maxParentIndex));
				CNEGenotype child = CNEGenotype.crossover(parent1, parent2);
				if (random.nextDouble() < EvolutionParameters.CNEMutationProbability) {
					child.mutate();
				}
				population.remove(replacementIndex);
				population.add(replacementIndex, child);
				replacementIndex--;
			}
			
			Vector<Piece> testPredatorPieces = new Vector<Piece>();
			for (int pred = 0; pred < EvolutionParameters.numPredators; pred++) {
				ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(
						new Vector<Double>(
								population.get(0).getWeights()
										.subList(
												pred
														* 7
														* EvolutionParameters.numHiddenNodes,
												(pred + 1)
														* 7
														* EvolutionParameters.numHiddenNodes)),
						0);
				ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(
						EvolutionParameters.boardSize, ann);
				testPredatorPieces
						.add(new Piece(
								EvolutionParameters.predatorPositions[2 * pred],
								EvolutionParameters.predatorPositions[2 * pred + 1],
								false, environment, annBehaviour));
			}
			int bestCaptureCount = ESPEvolution.testOnIncrementedPositions(EvolutionParameters.rootOfNumGridTests,
					environment, testPredatorPieces, runAway, new EvolutionParameters());
			
			System.out.println("Generation " + generation + " done, " + captureCount + 
						" captures, " + bestCaptureCount + " test.");
			
			if((captureCount / ((double) EvolutionParameters.CNEPopSize * EvolutionParameters.CNEEvaluationsPerTrial) 
					> EvolutionParameters.CNERatioCapturesForEpochChange) & 
					(epochNumber < EvolutionParameters.epochPreySpeeds.length)) {
				epochNumber++;
			} else if ((epochNumber == EvolutionParameters.epochPreySpeeds.length - 1) & 
					(bestCaptureCount/Math.pow(EvolutionParameters.rootOfNumGridTests, 2) 
					> EvolutionParameters.ratioCapturesForEnd)) {
				break;
			}
		}// generation
		
		return generation;
	}// run()

	// Run a set of evaluations on the predators to get fitness values for the
	// genotypes
	private static TrialResult trial(Vector<Piece> predatorPieces,
			Vector<Piece> preyPieces, Environment env, int evaluations) {
		double avgEvalFitness = 0;
		int captureCount = 0;
		Random random = new Random();
		for (int eval = 0; eval < evaluations; eval++) {
			// Randomize the prey positions so that they are not the
			// same as the previous evaluation.
			for (Piece prey : preyPieces) {
				prey.setPosition(random.nextInt(EvolutionParameters.boardSize),
						random.nextInt(EvolutionParameters.boardSize));
			}
			int k = 0;
			for (Piece predator : predatorPieces) {
				predator.setPosition(
						EvolutionParameters.predatorPositions[2 * k],
						EvolutionParameters.predatorPositions[2 * k + 1]);
				k++;
			}

			env.setPieces(predatorPieces, preyPieces);
			SimulationResult result = env.run(false, false);
			captureCount += result.preyCaught;
			for (int i = 0; i < EvolutionParameters.numPredators; i++) {
				if (result.preyCaught == 0) {
					/*
					 * double fitness = result.initialDistancesFromPrey
					 * .elementAt(i) -
					 * result.finalDistancesFromPrey.elementAt(i);
					 */
					double fitness = EvolutionParameters.boardSize
							- result.finalDistancesFromPrey.elementAt(i);
					avgEvalFitness += fitness;
				} else {
					// avgEvalFitnesses[i] += 2 * boardSize;
					avgEvalFitness += 2 * EvolutionParameters.boardSize
							- result.finalDistancesFromPrey.elementAt(i);
				}
			}
		}
		avgEvalFitness = avgEvalFitness
					/ EvolutionParameters.evaluationsPerTrial;

		return new TrialResult(avgEvalFitness, captureCount, 0);
	}
}
