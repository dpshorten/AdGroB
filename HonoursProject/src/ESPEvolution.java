import java.util.Collections;
import java.util.Vector;
import java.util.Random;

@SuppressWarnings("static-access")
public class ESPEvolution {

	Vector<ESPPopulation> agentPopulations = new Vector<ESPPopulation>();


	public TrialResult run(boolean doMigration,
			boolean useBehaviourDistance, boolean useGenotypeDistance,
			boolean doDavidsDeltaThings){
		EvolutionParameters params = new EvolutionParameters();
		
		params.doMigration = doMigration;
		params.useBehaviourDistance = useBehaviourDistance;
		params.useGenotypeDistance = useGenotypeDistance;
		params.doDavidsDeltaThings = doDavidsDeltaThings;
		
		// refresh the agent populations vector
		agentPopulations = new Vector<ESPPopulation>();

		int epochNumber = 0;
		int burstMutationTicker = params.burstMutationWaitBeforeFirst;

		// Initialise a population of genotypes for each predator
		for (int i = 0; i < params.numPredators; i++)
			agentPopulations.add(new ESPPopulation(params.numHiddenNodes,
					params.subPopulationSize));

		Random random = new Random();
		Environment env = null;

		Vector<Integer> capturesForEachGeneration = new Vector<Integer>(
				params.generations);

		int gen = 0;
		for (gen = 0; gen < params.generations; gen++) {

			for (ESPPopulation agentPopulation : agentPopulations) {
				agentPopulation.resetGenotypes();
			}

			env = new Environment(params.boardSize,
					params.preySpeeds[epochNumber], params.numPredators);

			Vector<Piece> preyPieces = new Vector<Piece>();
			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
					params.boardSize, 1);
			int preyX = random.nextInt(params.boardSize);
			int preyY = random.nextInt(params.boardSize);
			preyPieces.add(new Piece(preyX, preyY, true, env, runAway));

			// For each generation, a number of trials are run to get fitness
			// values for the genotypes
			int captureCount = 0;
			for (int trialSet = 0; trialSet < params.trialSetsPerGeneration; trialSet++) {
				for (ESPPopulation pop : agentPopulations) {
					pop.shuffleSubPopulations();
				}
				for (int trial = 0; trial < params.subPopulationSize; trial++) {
					Vector<Piece> predatorPieces = new Vector<Piece>();
					// usedGenotypes will contain a vector of active genotypes
					// for each predator
					Vector<Vector<Genotype>> usedGenotypes = new Vector<Vector<Genotype>>();

					// Build an ANN for each predator using a randomly chosen
					// node from each subpopulation
					for (int pred = 0; pred < params.numPredators; pred++) {
						Vector<Genotype> hiddenNodes = new Vector<Genotype>();
						for (int node = 0; node < params.numHiddenNodes; node++) {
							// Add a random genotype from the appropriate
							// subpopulation to the hidden nodes
							hiddenNodes.add(agentPopulations.elementAt(pred)
									.getSubPopulationForNode(node)
									.getFirstGenotypeAndSendItToBack());
						}

						ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(
								hiddenNodes);
						ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(
								params.boardSize, ann);
						predatorPieces.add(new Piece(
								params.predatorPositions[2 * pred],
								params.predatorPositions[2 * pred + 1], false,
								env, annBehaviour));
						usedGenotypes.add(hiddenNodes);
					}

					TrialResult result = trial(predatorPieces, preyPieces, env,
							params.evaluationsPerTrial, params);
					captureCount += result.captureCount;

					// Update the genotypes fitnesses with the average fitness
					// over
					// the evaluations
					for (int i = 0; i < params.numPredators; i++) {
						for (Genotype genotype : usedGenotypes.elementAt(i))
							genotype.updateFitness(result.avgEvalFitnesses[i]);
					}
				}// trial
			}// trialSets

			double mutationStdDev = 0;
			if (captureCount
					/ ((double) ((params.trialsPerGeneration * params.evaluationsPerTrial))) < 0.9) {
				mutationStdDev = params.earlyMutationStdDev;
			} else {
				mutationStdDev = params.lateMutationStdDev;
			}
			// Create offspring by applying crossover and mutation to the
			// genotype subpopulations
			for (int pred = 0; pred < params.numPredators; pred++) {
				for (int subpop = 0; subpop < params.numHiddenNodes; subpop++) {

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

					int numberOfOffspring = params.subPopulationSize / 2;
					int maxParentIndex = params.subPopulationSize / 4;
					int replacementIndex = genotypes.size() - 1;
					for (int i = 0; i < numberOfOffspring; i++) {
						Genotype parent1 = genotypes.elementAt(random
								.nextInt(maxParentIndex));
						Genotype parent2 = genotypes.elementAt(random
								.nextInt(maxParentIndex));
						Genotype child = Genotype.crossover(parent1, parent2);
						if (random.nextDouble() < params.mutationProbability) {
							child.mutate(mutationStdDev);
						}
						genotypes.remove(replacementIndex);
						genotypes.add(replacementIndex, child);
						replacementIndex--;
					}

					// Cloning code, if needed.
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

			// The fittest predators for the n instance test.
			Vector<Piece> testPredatorPieces = new Vector<Piece>();

			// These 3 variables are for David's delta things.
			Vector<Piece> mostSuccessfulPieces = testPredatorPieces;
			Vector<Vector<Genotype>> mostSuccessfulGenotypes = new Vector<Vector<Genotype>>();
			int numCapturesOfMostSuccessfulPieces = 0;

			// Construct the fittest predators.
			for (int pred = 0; pred < params.numPredators; pred++) {

				Vector<Genotype> hiddenNodes = new Vector<Genotype>();
				for (int node = 0; node < params.numHiddenNodes; node++) {
					hiddenNodes
							.add(agentPopulations.elementAt(pred)
									.getSubPopulationForNode(node)
									.getFittestGenotype());
				}
				mostSuccessfulGenotypes.add(hiddenNodes);
				ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(
						hiddenNodes);
				ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(
						params.boardSize, ann);
				testPredatorPieces.add(new Piece(
						params.predatorPositions[2 * pred],
						params.predatorPositions[2 * pred + 1], false, env,
						annBehaviour));
			}
			// Run the n instance test.
			int testCaptureCount = testOnIncrementedPositions(
					params.rootOfNumTests, env, testPredatorPieces, runAway, params);

			System.out.println("Generation " + gen + " done: " + captureCount
					+ " captures, " + testCaptureCount + "/"
					+ params.rootOfNumTests * params.rootOfNumTests
					+ " test score, " + env.numRuns + " evaluations.");

			// Check if we can move onto the next epoch (because the population
			// capture rate is high enough).
			if (epochNumber >= (params.preySpeeds.length - 1)
					& testCaptureCount / Math.pow(params.rootOfNumTests, 2) > params.ratioCapturesForEnd) {
				break;
			} else if (captureCount
					/ ((double) params.trialsPerGeneration * params.evaluationsPerTrial) > params.ratioCapturesForNextEpoch
					& epochNumber < (params.preySpeeds.length - 1)) {
				epochNumber++;
				System.out.println("Epoch Change to number "
						+ (epochNumber + 1));
				burstMutationTicker = params.burstMutationWaitAfterEpochChange;
			}
			// If we the epoch cannot be changed, the delta things can be done.
			else if (params.doDavidsDeltaThings) {
				boolean forceBurstMutation = false;
				numCapturesOfMostSuccessfulPieces = testOnIncrementedPositions(
						params.rootOfNumTestsDelta, env, mostSuccessfulPieces,
						runAway, params);
				// Try a number of genotype combinations, 'saving' the fittest.
				for (int i = 0; i < params.testedPieceGroups; i++) {
					Vector<Piece> testPieces = new Vector<Piece>();
					int j = 0;
					Vector<Vector<Genotype>> thisPiecesGenotypes = new Vector<Vector<Genotype>>();
					for (ESPPopulation pop : agentPopulations) {
						Vector<Genotype> genotypes = new Vector<Genotype>();
						for (ESPSubPopulation subPop : pop.subPopulations) {
							genotypes
									.add(subPop.getNthFittestGenotype(random
											.nextInt(params.genotypesPerSubPopulation)));
						}
						thisPiecesGenotypes.add(genotypes);
						ESPArtificialNeuralNetwork network = new ESPArtificialNeuralNetwork(
								genotypes);
						ESPArtificialNeuralNetworkBehaviour behaviour = new ESPArtificialNeuralNetworkBehaviour(
								params.boardSize, network);
						testPieces.add(new Piece(
								params.predatorPositions[2 * j],
								params.predatorPositions[2 * j + 1], false,
								env, behaviour));
						j++;
					}
					int testResult = testOnIncrementedPositions(
							params.rootOfNumTestsDelta, env, testPieces,
							runAway, params);
					if (testResult > numCapturesOfMostSuccessfulPieces) {
						numCapturesOfMostSuccessfulPieces = testResult;
						mostSuccessfulPieces = testPieces;
						mostSuccessfulGenotypes = thisPiecesGenotypes;
					}
				}
				// If the fittest is fit enough, the epoch can be changed.
				/*if (numCapturesOfMostSuccessfulPieces
						/ Math.pow(params.rootOfNumTestsDelta, 2) > params.ratioCapturesForNextEpoch) {
					forceBurstMutation = true;
					epochNumber++;
					System.out.println("Epoch Change to number "
							+ (epochNumber + 1) + " with fittest " + numCapturesOfMostSuccessfulPieces);
					burstMutationTicker = params.burstMutationWaitAfterEpochChange;
					if (epochNumber >= params.preySpeeds.length) {
						break;
					}
				}*/
				// If the fittest is much fitter than the population, burst
				// mutation can still be applied.
				if ((numCapturesOfMostSuccessfulPieces / Math.pow(
						params.rootOfNumTestsDelta, 2)) > (captureCount
						/ ((double) params.trialsPerGeneration * params.evaluationsPerTrial) + params.ratioDifference)
						|| forceBurstMutation) {
					for (int i = 0; i < params.numPredators; i++) {
						agentPopulations.set(i, new ESPPopulation(
								params.subPopulationSize,
								mostSuccessfulGenotypes.get(i), params.stdDev));
					}
					System.out.println("Delta Done : "
							+ numCapturesOfMostSuccessfulPieces);
				}
			}

			//Migration
			if (params.doMigration) {
				if ((gen % params.migrationGenInterval == 0
						&& gen > params.migrationStartingGen && gen < params.migrationLateGenThreshold)
						|| (gen % params.migrationLateGenInterval == 0 && gen > params.migrationLateGenThreshold)) {
					burstMutationTicker = params.burstMutationWaitAfterMigration;
					if (params.useBehaviourDistance) {
						double[][] similarities = SocialEntropyBehaviourMeasurement
								.measureSimilarity(testPredatorPieces,
										params.boardSize, env);
						for (int i = 0; i < params.numPredators; i++) {
							for (int j = i + 1; j < params.numPredators; j++) {
								if (similarities[i][j] < params.migrationBehaviourSimilarityThreshhold) {
									if (params.useGenotypeDistance) {
										int migrantCount = 0;
										for (ESPSubPopulation subPopI : agentPopulations.get(i).subPopulations) {
											for (ESPSubPopulation subPopJ : agentPopulations.get(j).subPopulations) {
												if (subPopI.averageWeightDistance(subPopJ) < 
														params.migrationGenotypeDistanceThreshhold) {
													subPopI.sendMigrants(subPopJ, params.migrationNumMigrants);
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
														params.migrationNumMigrants);
									}
								}
							}
						}
					} else if (params.useGenotypeDistance) {
						for (int i = 0; i < params.numPredators; i++) {
							for (int j = 0; j < params.numPredators; j++) {
								int migrantCount = 0;
								for (ESPSubPopulation subPopI : agentPopulations
										.get(i).subPopulations) {
									for (ESPSubPopulation subPopJ : agentPopulations
											.get(j).subPopulations) {
										if (subPopI
												.averageWeightDistance(subPopJ) < params.migrationGenotypeDistanceThreshhold) {
											subPopI.sendMigrants(subPopJ,
													params.migrationNumMigrants);
											migrantCount++;
										}
									}
								}

								System.out.println(migrantCount
										+ " migrations done");
							}
						}
					} else {
						for (int i = 0; i < params.numPredators; i++) {
							for (int j = 0; j < params.numPredators; j++) {
								System.out.println("Migrating " + i + " to "
										+ j);
								agentPopulations.get(i).sendMigrants(
										agentPopulations.get(j),
										params.migrationNumMigrants);
							}
						}
					}
				}
			}//migration
			
			//Inter-population crossover
			if(params.doInterpopulationCrossover){
				if(gen % params.crossoverGenInterval == 0 && gen >= params.crossoverFirstGen){
					int crossovercount = 0;
					if(params.useBehaviourDistance){
						double[][] similarities = SocialEntropyBehaviourMeasurement.measureSimilarity(
									testPredatorPieces, params.boardSize, env);
						
						for (int i = 0; i < params.numPredators; i++) {
							for (int j = i + 1; j < params.numPredators; j++) {
								System.out.println("Behaviour distance "+i+":"+j+"= "+similarities[i][j]);
								if (similarities[i][j] < params.crossoverBehaviourSimilarityThreshhold) {
									for(ESPSubPopulation subPopI : agentPopulations.get(i).subPopulations){
										for(ESPSubPopulation subPopJ : agentPopulations.get(j).subPopulations){
											//System.out.println("\tGenotype distance: "+subPopI.averageWeightDistance(subPopJ));
											if((!params.useGenotypeDistance) || subPopI.averageWeightDistance(subPopJ) < params.crossoverGenotypeDistanceThreshhold){
												crossovercount++;
												
												Vector<Genotype> genotypesI = subPopI.getAllGenotypes();
												Vector<Genotype> genotypesJ = subPopJ.getAllGenotypes();
												
												//Sort by descending fitness (element 0 = fittest)
												Collections.sort(genotypesI);
												Collections.reverse(genotypesI);
												Collections.sort(genotypesJ);
												Collections.reverse(genotypesJ);
												
												int replacementIndex = params.subPopulationSize - 1;
												int numReplacements = (int)Math.round(params.crossoverPopulationPercentage * params.subPopulationSize);
												
												for(int k=0; k < numReplacements; k++){
													Genotype parentI = genotypesI.get(k);
													Genotype parentJ = genotypesJ.get(k);
													Genotype child = Genotype.crossover(parentI, parentJ);
													
													genotypesI.remove(replacementIndex);
													genotypesJ.remove(replacementIndex);
													genotypesI.add(replacementIndex, child);
													genotypesJ.add(replacementIndex, child);
													replacementIndex--;
												}
											}
										}
									}
								}
							}
						}
					}
					else if(params.useGenotypeDistance){
						for (int i = 0; i < params.numPredators; i++) {
							for (int j = 0; j < params.numPredators; j++) {
								for(ESPSubPopulation subPopI : agentPopulations.get(i).subPopulations){
									for(ESPSubPopulation subPopJ : agentPopulations.get(j).subPopulations){
										if(subPopI.averageWeightDistance(subPopJ) <	params.crossoverGenotypeDistanceThreshhold){
											crossovercount++;
											
											Vector<Genotype> genotypesI = subPopI.getAllGenotypes();
											Vector<Genotype> genotypesJ = subPopJ.getAllGenotypes();
											
											//Sort by descending fitness (element 0 = fittest)
											Collections.sort(genotypesI);
											Collections.reverse(genotypesI);
											Collections.sort(genotypesJ);
											Collections.reverse(genotypesJ);
											
											int replacementIndex = params.subPopulationSize;
											int numReplacements = (int)Math.round(params.crossoverPopulationPercentage * params.subPopulationSize);
											
											for(int k=0; k < numReplacements; k++){
												Genotype parentI = genotypesI.get(k);
												Genotype parentJ = genotypesJ.get(k);
												Genotype child = Genotype.crossover(parentI, parentJ);
												
												genotypesI.remove(replacementIndex);
												genotypesJ.remove(replacementIndex);
												genotypesI.add(replacementIndex, child);
												genotypesJ.add(replacementIndex, child);
												replacementIndex--;
											}
										}
									}
								}
							}
						}
					}
					
					System.out.println("Crossover occured between "+crossovercount+" subpopulations.");
				}
			}//Interpop crossover
		
			// If the improvement is stagnating, burst mutation is run.
			if (gen >= params.burstMutationTestLookBackDistance) {
				if ((burstMutationTicker <= 0)
						& (capturesForEachGeneration.get(gen) < (capturesForEachGeneration
								.get(gen
										- params.burstMutationTestLookBackDistance) + (int) (Math
								.ceil(params.burstMutationTestRatioOfTrialsDifference
										* params.trialsPerGeneration
										* params.evaluationsPerTrial))))) {
					System.out.println("Burst Mutation!!");
					for (ESPPopulation pop : agentPopulations) {
						if (captureCount
								/ ((double) ((params.trialsPerGeneration * params.evaluationsPerTrial))) < params.burstMutationEarlyLateFitnessCutoff) {
							pop.runBurstMutation(params.earlyBurstMutationAmountStdDev);
						} else {
							pop.runBurstMutation(params.lateBurstMutationAmountStdDev);
						}
					}
					burstMutationTicker = params.burstMutationWaitBeforeRepeat;
				} else {
					burstMutationTicker -= 1;
				}
			}

		}// generations

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
					params.boardSize, ann);
			fittestPredatorPieces.add(new Piece(
					params.predatorPositions[2 * j],
					params.predatorPositions[2 * j + 1], false, env,
					annBehaviour));
			j += 1;
		}

		// Run a trial on the fittest predators
		Vector<Piece> preyPieces = new Vector<Piece>();
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
				params.boardSize, 1);
		int preyX = random.nextInt(params.boardSize);
		int preyY = random.nextInt(params.boardSize);
		preyPieces.add(new Piece(preyX, preyY, true, env, runAway));
		TrialResult finalResult = trial(fittestPredatorPieces, preyPieces, env,
				params.FinalTestNumEvaluationsToRun, params);
		finalResult.generations = gen + 1;

		return finalResult;
	}

	// Run a set of evaluations on the predators to get fitness values for the
	// genotypes
	private static TrialResult trial(Vector<Piece> predatorPieces,
			Vector<Piece> preyPieces, Environment env, int evaluations
			, EvolutionParameters params) {
		double[] avgEvalFitnesses = new double[params.numPredators];
		int captureCount = 0;
		Random random = new Random();
		for (int eval = 0; eval < evaluations; eval++) {
			// Randomize the prey positions so that they are not the 
			// same as the previous evaluation.
			for (Piece prey : preyPieces) {
				prey.setPosition(random.nextInt(params.boardSize),
						random.nextInt(params.boardSize));
			}
			int k = 0;
			for (Piece predator : predatorPieces) {
				predator.setPosition(params.predatorPositions[2 * k],
						params.predatorPositions[2 * k + 1]);
				k++;
			}

			env.setPieces(predatorPieces, preyPieces);
			SimulationResult result = env.run(false, false);
			captureCount += result.preyCaught;
			for (int i = 0; i < params.numPredators; i++) {
				if (result.preyCaught == 0) {
					/*
					 * double fitness = result.initialDistancesFromPrey
					 * .elementAt(i) -
					 * result.finalDistancesFromPrey.elementAt(i);
					 */
					double fitness = params.boardSize
							- result.finalDistancesFromPrey.elementAt(i);
					avgEvalFitnesses[i] += fitness;
				} else {
					// avgEvalFitnesses[i] += 2 * boardSize;
					avgEvalFitnesses[i] += 2 * params.boardSize
							- result.finalDistancesFromPrey.elementAt(i);
				}
			}
		}
		for (int i = 0; i < params.numPredators; i++)
			avgEvalFitnesses[i] = avgEvalFitnesses[i]
					/ params.evaluationsPerTrial;

		return new TrialResult(avgEvalFitnesses, captureCount, 0);
	}

	public static int testOnIncrementedPositions(int rootOfTotalNumTests,
			Environment env, Vector<Piece> testPredatorPieces,
			Behaviour preyBehaviour, EvolutionParameters params) {
		int preyPlacementIncrememnt = (int) Math.floor(params.boardSize
				/ ((double) rootOfTotalNumTests));
		int testCaptureCount = 0;
		for (int preyRow = 0; preyRow < rootOfTotalNumTests; preyRow++) {
			for (int preyCol = 0; preyCol < rootOfTotalNumTests; preyCol++) {
				Vector<Piece> testPreyPieces = new Vector<Piece>();
				testPreyPieces.add(new Piece((int) Math.round((preyRow + 0.5)
						* preyPlacementIncrememnt), (int) Math
						.round((preyCol + 0.5) * preyPlacementIncrememnt),
						true, env, preyBehaviour));
				/*
				 * I am not sure why there is the need to reset the position of
				 * the pieces (as in the trial() method), but it makes things
				 * work. - David
				 */
				for (Piece prey : testPreyPieces)
					prey.setPosition(
							(int) Math.round((preyRow + 0.5)
									* preyPlacementIncrememnt),
							(int) Math.round((preyCol + 0.5)
									* preyPlacementIncrememnt));
				int k = 0;
				for (Piece predator : testPredatorPieces) {
					predator.setPosition(params.predatorPositions[2 * k],
							params.predatorPositions[2 * k + 1]);
					k++;
				}
				env.setPieces(testPredatorPieces, testPreyPieces);
				SimulationResult result = env.run(false, false);
				testCaptureCount += result.preyCaught;
			}
		}
		return testCaptureCount;
	}
}
