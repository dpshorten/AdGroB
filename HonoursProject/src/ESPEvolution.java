
import java.util.Collections;
import java.util.Vector;
import java.util.Random;

public class ESPEvolution {
	static final int numHiddenNodes = 10;
	static final int numPredators = 3;
	static final int subPopulationSize = 100;
	// Each trial set runs a number of trials equal to the sub-population size.
	static final int trialSetsPerGeneration = 10; // 10
	static final int trialsPerGeneration = trialSetsPerGeneration
			* subPopulationSize;
	static final int evaluationsPerTrial = 6; // 6
	static final int generations = 200;
	static final int boardSize = 100;
	static final double mutationProbability = 0.4;
	static final double earlyMutationStdDev = 0.05;
	static final double lateMutationStdDev = 0.01;
	static final double earlyBurstMutationAmountStdDev = 0.3;
	static final double lateBurstMutationAmountStdDev = 0.05;
	static final double newEpochBurstMutationAmountStdDev = 0.2;
	static final int burstMutationWaitBeforeRepeat = 20;
	static final int burstMutationWaitBeforeFirst = 10;
	static final int burstMutationWaitAfterMigration = 6;
	static final int burstMutationWaitAfterEpochChange = 6;
	static int burstMutationTestLookBackDistance = 5;
	static final double burstMutationTestRatioOfTrialsDifference = 0.01;
	static final int rootOfNumTests = 10;
	static final double ratioCapturesForNextEpoch = 0.7;
	static final double ratioCapturesForEnd = 0.8;
	static final int ratioHitsBeforeNextEpoch = 2;
	static final double[] preySpeeds = { 0.1, 0.95, 1 };

	static Integer[] predatorPositions = { 30, 30, boardSize - 30, 30, 30,
			boardSize - 30};

	// Variables for David's delta coding.
	static final int genotypesPerSubPopulation = 5;
	static final int testedPieceGroups = 100;
	static final int rootOfNumTestsDelta = 7;
	static final double stdDev = 0.1;
	static final double ratioDifference = 0.0;

	static Vector<ESPPopulation> agentPopulations = new Vector<ESPPopulation>();

	public static void main(String[] args) {
		run(false, false, false, true);
	}

	public static TrialResult run(boolean doMigration) {
		return run(doMigration, true, true, true);
	}

	public static TrialResult run(boolean doMigration,
			boolean useBehaviourDistance, boolean useGenotypeDistance,
			boolean doDavidsDeltaThings) {

		if (doMigration) {
			burstMutationTestLookBackDistance = 12;
		}

		// refresh the agent populations vector
		agentPopulations = new Vector<ESPPopulation>();

		int epochNumber = 0;
		int epochRatioHits = 0;

		int burstMutationTicker = burstMutationWaitBeforeFirst;

		// Initialise a population of genotypes for each predator
		for (int i = 0; i < numPredators; i++)
			agentPopulations.add(new ESPPopulation(numHiddenNodes,
					subPopulationSize));

		Random random = new Random();
		Environment env = null;

		Vector<Integer> capturesForEachGeneration = new Vector<Integer>(
				generations);

		int gen = 0;
		for (gen = 0; gen < generations; gen++) {

			for (ESPPopulation agentPopulation : agentPopulations) {
				agentPopulation.resetGenotypes();
			}

			env = new Environment(boardSize, preySpeeds[epochNumber],
					numPredators);

			Vector<Piece> preyPieces = new Vector<Piece>();
			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
					boardSize, 1);
			int preyX = random.nextInt(boardSize);
			int preyY = random.nextInt(boardSize);
			preyPieces.add(new Piece(preyX, preyY, true, env, runAway));

			// For each generation, a number of trials are run to get fitness
			// values for the genotypes
			int captureCount = 0;
			for (int trialSet = 0; trialSet < trialSetsPerGeneration; trialSet++) {
				for (ESPPopulation pop : agentPopulations) {
					pop.shuffleSubPopulations();
				}
				for (int trial = 0; trial < subPopulationSize; trial++) {
					Vector<Piece> predatorPieces = new Vector<Piece>();
					// usedGenotypes will contain a vector of active genotypes
					// for
					// each predator
					Vector<Vector<Genotype>> usedGenotypes = new Vector<Vector<Genotype>>();

					// Build an ANN for each predator using a randomly choosen
					// node
					// from each subpopulation
					for (int pred = 0; pred < numPredators; pred++) {
						Vector<Genotype> hiddenNodes = new Vector<Genotype>();
						for (int node = 0; node < numHiddenNodes; node++) {
							// Add a random genotype from the appropriate
							// subpopulation to the hidden nodes
							hiddenNodes.add(agentPopulations.elementAt(pred)
									.getSubPopulationForNode(node)
									.getFirstGenotypeAndSendItToBack());
						}

						ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(
								hiddenNodes);
						ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(
								boardSize, ann);
						predatorPieces.add(new Piece(
								predatorPositions[2 * pred],
								predatorPositions[2 * pred + 1], false, env,
								annBehaviour));
						usedGenotypes.add(hiddenNodes);
					}

					TrialResult result = trial(predatorPieces, preyPieces, env,
							evaluationsPerTrial);
					captureCount += result.captureCount;

					// Update the genotypes fitnesses with the average fitness
					// over
					// the evaluations
					for (int i = 0; i < numPredators; i++) {
						for (Genotype genotype : usedGenotypes.elementAt(i))
							genotype.updateFitness(result.avgEvalFitnesses[i]);
					}
				}// trial
			}// trialSets

			double mutationStdDev = 0;
			if (captureCount
					/ ((double) ((trialsPerGeneration * evaluationsPerTrial))) < 0.9) {
				mutationStdDev = earlyMutationStdDev;
			} else {
				mutationStdDev = lateMutationStdDev;
			}
			// Create offspring by applying crossover and mutation to the
			// genotype subpopulations
			for (int pred = 0; pred < numPredators; pred++) {
				for (int subpop = 0; subpop < numHiddenNodes; subpop++) {

					// Within each subpopulation, rank the genotypes by fitness
					// with element 0 being the highest fitness
					Vector<Genotype> genotypes = agentPopulations
							.elementAt(pred).getSubPopulationForNode(subpop)
							.getAllGenotypes();
					Collections.sort(genotypes);
					Collections.reverse(genotypes);

					// Replace the bottom ~50% of genotypes with offspring from
					// the top ~25%
					// Also mutate offspring with probability =
					// mutationProbability

					int numberOfOffspring = subPopulationSize / 2;
					int maxParentIndex = subPopulationSize / 4;
					int replacementIndex = genotypes.size() - 1;
					for (int i = 0; i < numberOfOffspring; i++) {
						Genotype parent1 = genotypes.elementAt(random
								.nextInt(maxParentIndex));
						Genotype parent2 = genotypes.elementAt(random
								.nextInt(maxParentIndex));
						Genotype child = Genotype.crossover(parent1, parent2);
						if (random.nextDouble() < mutationProbability) {
							child.mutate(mutationStdDev);
						}
						genotypes.remove(replacementIndex);
						genotypes.add(replacementIndex, child);
						replacementIndex--;
					}

					// Cloning for now
					/*
					 * int endOfElites = 10; int replacementIndex =
					 * genotypes.size() - 1; for (int i = 0; i < endOfElites;
					 * i++) { Genotype clone = genotypes.get(i).clone(); if
					 * (random.nextDouble() < mutationProbability) { double
					 * mutationStdDev = 0; if (captureCount / ((double)
					 * ((trialsPerGeneration * evaluationsPerTrial))) < 0.9) {
					 * mutationStdDev = earlyMutationStdDev; } else {
					 * mutationStdDev = lateMutationStdDev; }
					 * clone.mutate(mutationStdDev); }
					 * genotypes.remove(replacementIndex);
					 * genotypes.add(replacementIndex, clone);
					 * replacementIndex--; }
					 */

				}
			}// replacement

			capturesForEachGeneration.add(captureCount);

			// Construct the fittest predators for the n instance test.
			Vector<Piece> testPredatorPieces = new Vector<Piece>();

			// These 3 variables are for David's delta things.
			Vector<Piece> mostSuccessfulPieces = testPredatorPieces;
			Vector<Vector<Genotype>> mostSuccessfulGenotypes = new Vector<Vector<Genotype>>();
			int numCapturesOfMostSuccessfulPieces = 0;

			for (int pred = 0; pred < numPredators; pred++) {

				Vector<Genotype> hiddenNodes = new Vector<Genotype>();
				for (int node = 0; node < numHiddenNodes; node++) {
					hiddenNodes
							.add(agentPopulations.elementAt(pred)
									.getSubPopulationForNode(node)
									.getFittestGenotype());
				}
				mostSuccessfulGenotypes.add(hiddenNodes);
				ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(
						hiddenNodes);
				ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(
						boardSize, ann);
				testPredatorPieces.add(new Piece(predatorPositions[2 * pred],
						predatorPositions[2 * pred + 1], false, env,
						annBehaviour));
			}
			// Run the n instance test.
			int testCaptureCount = testOnIncrementedPositions(rootOfNumTests,
					env, testPredatorPieces, runAway);

			System.out.println("Generation " + gen + " done: " + captureCount
					+ " captures, " + testCaptureCount + "/" + rootOfNumTests
					* rootOfNumTests + " test score.");

			// Check if we can move onto the next epoch
			if (epochNumber >= (preySpeeds.length - 1) & testCaptureCount / Math.pow(rootOfNumTests, 2) > ratioCapturesForEnd) {
				break;
			} else if (captureCount / ((double) trialsPerGeneration * evaluationsPerTrial) > ratioCapturesForNextEpoch & 
					epochNumber < (preySpeeds.length - 1)) {
				epochRatioHits = 0;
				epochNumber++;
				System.out.println("Epoch Change to number "
						+ (epochNumber + 1));
				burstMutationTicker = burstMutationWaitAfterEpochChange;
			} else if (doDavidsDeltaThings) {
				boolean forceBurstMutation = false;
				numCapturesOfMostSuccessfulPieces = testOnIncrementedPositions(
						rootOfNumTestsDelta, env, mostSuccessfulPieces, runAway);
				for (int i = 0; i < testedPieceGroups; i++) {
					Vector<Piece> testPieces = new Vector<Piece>();
					int j = 0;
					Vector<Vector<Genotype>> thisPiecesGenotypes = new Vector<Vector<Genotype>>();
					for (ESPPopulation pop : agentPopulations) {
						Vector<Genotype> genotypes = new Vector<Genotype>();
						for (ESPSubPopulation subPop : pop.subPopulations) {
							genotypes.add(subPop.getNthFittestGenotype(random
									.nextInt(genotypesPerSubPopulation)));
						}
						thisPiecesGenotypes.add(genotypes);
						ESPArtificialNeuralNetwork network = new ESPArtificialNeuralNetwork(
								genotypes);
						ESPArtificialNeuralNetworkBehaviour behaviour = new ESPArtificialNeuralNetworkBehaviour(
								boardSize, network);
						testPieces.add(new Piece(predatorPositions[2 * j],
								predatorPositions[2 * j + 1], false, env,
								behaviour));
						j++;
					}
					int testResult = testOnIncrementedPositions(
							rootOfNumTestsDelta, env, testPieces, runAway);
					if (testResult > numCapturesOfMostSuccessfulPieces) {
						numCapturesOfMostSuccessfulPieces = testResult;
						mostSuccessfulPieces = testPieces;
						mostSuccessfulGenotypes = thisPiecesGenotypes;
					}
				}
				if (numCapturesOfMostSuccessfulPieces
						/ ((double) rootOfNumTestsDelta * rootOfNumTestsDelta) > ratioCapturesForNextEpoch) {
					forceBurstMutation = true;
					epochRatioHits = 0;
					epochNumber++;
					System.out.println("Epoch Change to number "
							+ (epochNumber + 1));
					burstMutationTicker = burstMutationWaitAfterEpochChange;
					if (epochNumber >= preySpeeds.length) {
						break;
					}
				}
				if ((numCapturesOfMostSuccessfulPieces
						/Math.pow(rootOfNumTestsDelta, 2)) > (captureCount
						/ ((double) trialsPerGeneration * evaluationsPerTrial) + ratioDifference)
						|| forceBurstMutation) {
					for (int i = 0; i < numPredators; i++) {
						agentPopulations
								.set(i, new ESPPopulation(subPopulationSize,
										mostSuccessfulGenotypes.get(i), stdDev));
					}
					System.out.println("Delta Done : "
							+ numCapturesOfMostSuccessfulPieces);
				}
			}

			// Migration
			final int startingGen = 1;
			final int genInterval = 2;
			final int lateGenInterval = 2;
			final int lateGenThreshold = 25;
			final int numMigrants = 2;
			final double behaviourSimilarityThreshhold = 0.0;
			final double genotypeDistanceThreshhold = 0.37;

			if (doMigration) {
				if ((gen % genInterval == 0 && gen > startingGen && gen < lateGenThreshold)
						|| (gen % lateGenInterval == 0 && gen > lateGenThreshold)) {
					burstMutationTicker = burstMutationWaitAfterMigration;
					if (useBehaviourDistance) {
						double[][] similarities = SocialEntropyBehaviourMeasurement
								.measureSimilarity(testPredatorPieces,
										boardSize, env);
						for (int i = 0; i < numPredators; i++) {
							for (int j = 0; j < numPredators; j++) {
								if (similarities[i][j] > behaviourSimilarityThreshhold) {
									// System.out.println("Migrating " + i +
									// " to " + j);
									if (useGenotypeDistance) {
										int migrantCount = 0;
										for (ESPSubPopulation subPopI : agentPopulations
												.get(i).subPopulations) {
											for (ESPSubPopulation subPopJ : agentPopulations
													.get(j).subPopulations) {
												if (subPopI
														.averageWeightDistance(subPopJ) < genotypeDistanceThreshhold) {
													subPopI.sendMigrants(
															subPopJ,
															numMigrants);
													migrantCount++;
												}
											}
										}
										System.out.println(migrantCount
												+ " migrations done");
									} else {
										// NB: Note the change to the spraying
										// method.
										agentPopulations
												.get(i)
												.sendSprayMigrants(
														agentPopulations.get(j),
														numMigrants);
									}
								}
							}
						}
					} else if (useGenotypeDistance) {
						for (int i = 0; i < numPredators; i++) {
							for (int j = 0; j < numPredators; j++) {
								int migrantCount = 0;
								for (ESPSubPopulation subPopI : agentPopulations
										.get(i).subPopulations) {
									for (ESPSubPopulation subPopJ : agentPopulations
											.get(j).subPopulations) {
										if (subPopI
												.averageWeightDistance(subPopJ) < genotypeDistanceThreshhold) {
											subPopI.sendMigrants(subPopJ,
													numMigrants);
											migrantCount++;
										}
									}
								}
								System.out.println(migrantCount
										+ " migrations done");
							}
						}
					} else {
						for (int i = 0; i < numPredators; i++) {
							for (int j = 0; j < numPredators; j++) {
								System.out.println("Migrating " + i + " to "
										+ j);
								agentPopulations.get(i).sendMigrants(
										agentPopulations.get(j), numMigrants);
							}
						}
					}
				}
			}
			// If the improvement is stagnating, burst mutation is run.
			if (gen >= burstMutationTestLookBackDistance) {
				if ((burstMutationTicker <= 0)
						& (capturesForEachGeneration.get(gen) < (capturesForEachGeneration
								.get(gen - burstMutationTestLookBackDistance) + (int) (Math
								.ceil(burstMutationTestRatioOfTrialsDifference
										* trialsPerGeneration
										* evaluationsPerTrial))))) {
					System.out.println("Burst Mutation!!");
					for (ESPPopulation pop : agentPopulations) {
						if (captureCount
								/ ((double) ((trialsPerGeneration * evaluationsPerTrial))) < 0.9) {
							pop.runBurstMutation(earlyBurstMutationAmountStdDev);
						} else {
							pop.runBurstMutation(lateBurstMutationAmountStdDev);
						}
					}
					burstMutationTicker = burstMutationWaitBeforeRepeat;
				} else {
					burstMutationTicker -= 1;
				}
			}

		}// generations

		// Compute the genotypal euclidean distances between the predators.
		/*
		 * double[] distances1 =
		 * agentPopulations.get(0).maxAndMinEuclideanDistance
		 * (agentPopulations.get(1));
		 * System.out.println("Distances 0 - 1: min - " + distances1[0] +
		 * " max - " + distances1[1]); double[] distances2 =
		 * agentPopulations.get
		 * (0).maxAndMinEuclideanDistance(agentPopulations.get(2));
		 * System.out.println("Distances 0 - 2: min - " + distances2[0] +
		 * " max - " + distances2[1]); double[] distances3 =
		 * agentPopulations.get
		 * (1).maxAndMinEuclideanDistance(agentPopulations.get(2));
		 * System.out.println("Distances 1 - 2: min - " + distances3[0] +
		 * " max - " + distances3[1]);
		 */

		// Create the fittest predators and save them.
		int j = 0;
		Vector<Piece> fittestPredatorPieces = new Vector<Piece>();
		for (ESPPopulation agentPopulation : agentPopulations) {
			Vector<Genotype> hiddenNodes = agentPopulation
					.getFittestGenotypeInEachSubPopulation();
			ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(
					hiddenNodes);
			ann.saveNetwork("PredatorBehaviour" + j);
			ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(
					boardSize, ann);
			fittestPredatorPieces.add(new Piece(predatorPositions[2 * j],
					predatorPositions[2 * j + 1], false, env, annBehaviour));
			j += 1;
		}

		// Run a trial on the fittest predators
		int evaluationsToRun = 100;
		Vector<Piece> preyPieces = new Vector<Piece>();
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
				boardSize, 1);
		int preyX = random.nextInt(boardSize);
		int preyY = random.nextInt(boardSize);
		preyPieces.add(new Piece(preyX, preyY, true, env, runAway));
		TrialResult result = trial(fittestPredatorPieces, preyPieces, env,
				evaluationsToRun);
		result.generations = gen;

		// Run the simulation with them to create a log file
		/*
		 * for (Piece prey : preyPieces)
		 * prey.setPosition(random.nextInt(boardSize),
		 * random.nextInt(boardSize)); for(Piece predator :
		 * fittestPredatorPieces) predator.setPosition(5, 5);
		 * preyPieces.get(0).setPosition(preyX, preyY);
		 * env.setPieces(fittestPredatorPieces, preyPieces); env.run(true,
		 * false);
		 */

		return result;
	}

	// Run a set of evaluations on the predators to get fitness values for the
	// genotypes
	private static TrialResult trial(Vector<Piece> predatorPieces,
			Vector<Piece> preyPieces, Environment env, int evaluations) {
		double[] avgEvalFitnesses = new double[numPredators];
		int captureCount = 0;
		Random random = new Random();
		for (int eval = 0; eval < evaluations; eval++) {
			// Randomize the prey position so that it is not the same as the
			// previous evaluation.
			for (Piece prey : preyPieces)
				prey.setPosition(random.nextInt(boardSize),
						random.nextInt(boardSize));
			int k = 0;
			// randomizePredatorPositions();
			for (Piece predator : predatorPieces) {
				predator.setPosition(predatorPositions[2 * k],
						predatorPositions[2 * k + 1]);
				k++;
			}

			env.setPieces(predatorPieces, preyPieces);
			SimulationResult result = env.run(false, false);
			captureCount += result.preyCaught;
			for (int i = 0; i < numPredators; i++) {
				if (result.preyCaught == 0) {
					// double fitness = boardSize -
					// result.finalDistancesFromPrey.elementAt(i);
					/*
					 * double fitness = result.initialDistancesFromPrey
					 * .elementAt(i) -
					 * result.finalDistancesFromPrey.elementAt(i);
					 */
					double fitness = boardSize
							- result.finalDistancesFromPrey.elementAt(i);
					avgEvalFitnesses[i] += fitness;
				} else {
					// avgEvalFitnesses[i] += 2 * boardSize;
					avgEvalFitnesses[i] += 2 * boardSize
							- result.finalDistancesFromPrey.elementAt(i);
				}
			}
		}

		for (int i = 0; i < numPredators; i++)
			avgEvalFitnesses[i] = avgEvalFitnesses[i] / evaluationsPerTrial;

		return new TrialResult(avgEvalFitnesses, captureCount, 0);
	}

	public static int testOnIncrementedPositions(int rootOfTotalNumTests,
			Environment env, Vector<Piece> testPredatorPieces,
			Behaviour preyBehaviour) {
		int preyPlacementIncrememnt = (int) Math.floor(boardSize
				/ ((double) rootOfTotalNumTests));
		int testCaptureCount = 0;
		for (int preyRow = 0; preyRow < rootOfTotalNumTests; preyRow++) {
			for (int preyCol = 0; preyCol < rootOfTotalNumTests; preyCol++) {
				Vector<Piece> testPreyPieces = new Vector<Piece>();
				testPreyPieces.add(new Piece(preyRow * preyPlacementIncrememnt,
						preyCol * preyPlacementIncrememnt, true, env,
						preyBehaviour));
				/*
				 * I am not sure why there is the need to reset the position of
				 * the pieces (as in the trial() method), but it makes things
				 * work. - David
				 */
				for (Piece prey : testPreyPieces)
					prey.setPosition((int)Math.round((preyRow + 0.5) * preyPlacementIncrememnt), (int)Math.round((preyCol + 0.5)
							* preyPlacementIncrememnt));
				int k = 0;
				// randomizePredatorPositions();
				for (Piece predator : testPredatorPieces) {
					//predator.setPosition(predatorPositions[2 * k],
					//		predatorPositions[2 * k + 1]);
					predator.setPosition(1, 1);
					k++;
				}
				env.setPieces(testPredatorPieces, testPreyPieces);
				SimulationResult result = env.run(false, false);
				testCaptureCount += result.preyCaught;
			}
		}
		return testCaptureCount;
	}

	/*
	 * public static void randomizePredatorPositions() { Random random = new
	 * Random(); Vector<Integer> predatorPositionsVector = new
	 * Vector<Integer>(Arrays.asList(predatorPositions)); int indecesMoved =
	 * (random.nextInt() % numPredators) * 2; for(int i = 0; i < indecesMoved;
	 * i++) { int temp = predatorPositionsVector.remove(0);
	 * predatorPositionsVector.add(temp); } predatorPositions = (Integer[])
	 * predatorPositionsVector.toArray(new Integer[0]); }
	 */
}
