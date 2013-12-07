import java.util.Collections;
import java.util.Random;
import java.util.Vector;

@SuppressWarnings("static-access")
public class DeltaEvolution {

	EvolutionParameters params = new EvolutionParameters();
	
	static final double[] preySpeeds = { 1 };
	public static final int deltaSubPopulationSize = 100;
	public static final int[] numGroupsFirstRound = { 2000, 2000};
	public static final int[] rootNumEvalsFirstRound = { 1, 1 };
	public static final int[] numGroupsSecondRound = { 500, 500 };
	public static final int[] rootNumEvalsSecondRound = { 2, 2 };
	public static final int[] numGroupsThirdRound = { 50, 50 };
	public static final int[] rootNumEvalsThirdRound = { 4, 4 };
	public static final int[] numGroupsFourthRound = { 10, 10 };
	public static final int[] rootNumEvalsFourthRound = { 10, 10 };

	// public static final double earlyLateRatioCutoff = 0.5;
	public static final double firstStdDev = 0.9;
	public static final double secondStdDev = 0.7;
	public static final double thirdStdDev = 0.5;
	public static final double fourthStdDev = 0.4;
	public static final double fifthStdDev = 0.4;
	public static final double sixthStdDev = 0.3;
	public static final double seventhStdDev = 0.15;
	public static final double eighthStdDev = 0.15;
	public static final int bigBurstMutationLookBackDistance = 3;
	public static final int bigBurstMutationWaitBeforeRepeat = 4;
	public static final double bigStdDev = 0.6;

	public int run() {

		int bigBurstMutationTicker = bigBurstMutationLookBackDistance;

		Vector<ESPPopulation> agentPopulations = new Vector<ESPPopulation>();

		// Initialise a population of genotypes for each predator
		for (int i = 0; i < params.numPredators; i++) {
			agentPopulations.add(new ESPPopulation(params.numHiddenNodes,
					deltaSubPopulationSize));
		}

		Random random = new Random();
		int epochNumber = 0;
		int evaluationStructureIndex = 0;

		int gen = 0;
		Vector<Double> previousBestCaptureRatios = new Vector<Double>();
		PredatorGroup bestPredatorGroup = null;
		for (gen = 0; gen < params.generations; gen++) {

			Environment env = new Environment(params.boardSize,
					preySpeeds[epochNumber],
					params.numPredators);

			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
					params.boardSize, 1);

			Vector<PredatorGroup> predatorGroups = new Vector<PredatorGroup>();
			for (int i = 0; i < numGroupsFirstRound[evaluationStructureIndex]; i++) {
				Vector<Vector<Genotype>> groupsGenotypes = new Vector<Vector<Genotype>>();
				for (ESPPopulation pop : agentPopulations) {
					Vector<Genotype> predatorsGenotypes = new Vector<Genotype>();
					for (ESPSubPopulation subPop : pop.subPopulations) {
						predatorsGenotypes.add(subPop
								.getNthFittestGenotype(random
										.nextInt(deltaSubPopulationSize)));
					}
					groupsGenotypes.add(predatorsGenotypes);
				}
				predatorGroups.add(new PredatorGroup(groupsGenotypes, env));
			}

			predatorGroups = selectFromPredatorGroups(
					rootNumEvalsFirstRound[evaluationStructureIndex],
					predatorGroups, env, runAway,
					numGroupsSecondRound[evaluationStructureIndex]);
			predatorGroups = selectFromPredatorGroups(
					rootNumEvalsSecondRound[evaluationStructureIndex],
					predatorGroups, env, runAway,
					numGroupsThirdRound[evaluationStructureIndex]);
			predatorGroups = selectFromPredatorGroups(
					rootNumEvalsThirdRound[evaluationStructureIndex],
					predatorGroups, env, runAway,
					numGroupsFourthRound[evaluationStructureIndex]);
			predatorGroups = selectFromPredatorGroups(
					rootNumEvalsFourthRound[evaluationStructureIndex],
					predatorGroups, env, runAway, 1);

			double captureRatio = predatorGroups.get(0).getCaptureRatio();
			/*System.out.println("Generation " + (gen + 1)
					+ " done, best ratio: " + captureRatio + " " + env.numRuns);*/
			
			if(captureRatio > 0.6) {
				evaluationStructureIndex = 1;
			} else {
				evaluationStructureIndex = 0;
			}

			double stdDev = 0;
			if (captureRatio <= 0.1) {
				stdDev = firstStdDev;
			} else if (captureRatio <= 0.2) {
				stdDev = secondStdDev;
			} else if (captureRatio <= 0.3) {
				stdDev = thirdStdDev;
			} else if (captureRatio <= 0.4) {
				stdDev = fourthStdDev;
			} else if (captureRatio <= 0.5) {
				stdDev = fifthStdDev;
			} else if (captureRatio <= 0.6) {
				stdDev = sixthStdDev;
			} else if (captureRatio <= 0.7) {
				stdDev = seventhStdDev;
			} else {
				stdDev = eighthStdDev;
			}
			
			previousBestCaptureRatios.add(predatorGroups.get(0)
					.getCaptureRatio());
			if (bigBurstMutationTicker <= 0) {
				if (previousBestCaptureRatios.get(gen) < previousBestCaptureRatios
						.get(gen - bigBurstMutationLookBackDistance) + 0.20) {
					agentPopulations.clear();
					for (int i = 0; i < params.numPredators; i++) {
						agentPopulations.add(new ESPPopulation(params.numHiddenNodes,
								deltaSubPopulationSize));
					}
					bigBurstMutationTicker = bigBurstMutationWaitBeforeRepeat + 1;
					//System.out.println("restart!");
					continue;
				}
			}
			bigBurstMutationTicker--;

			for (int i = 0; i < params.numPredators; i++) {
				agentPopulations.set(i, new ESPPopulation(
						deltaSubPopulationSize, predatorGroups.get(0)
								.getGenotypes().get(i), stdDev));
			}
			if (predatorGroups.get(0).getCaptureRatio() > params.ratioCapturesForEnd) {
				bigBurstMutationTicker = bigBurstMutationWaitBeforeRepeat + 1;
				evaluationStructureIndex = 0;
				epochNumber++;
				/*System.out.println("Epoch Change to number "
						+ (epochNumber + 1));*/
				if (epochNumber >= preySpeeds.length) {
					if (predatorGroups.get(0).getMostRecentCaptureRatio() >= params.ratioCapturesForEnd) {
						bestPredatorGroup = predatorGroups.get(0);
						break;
					} else {
						epochNumber--;
					}
				}
			}
		}

		int j = 0;
		for(Vector<Genotype> hiddenNodes : bestPredatorGroup.getGenotypes()) {
			ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(hiddenNodes);
			ann.saveNetwork("PredatorBehaviour" + j);
			j++;
		}
		
		return gen + 1;
	}

	public Vector<PredatorGroup> selectFromPredatorGroups(
			int rootOfNumTests, Vector<PredatorGroup> inputGroups,
			Environment env, Behaviour preyBehaviour, int numToSelect) {
		for (PredatorGroup group : inputGroups) {
			int captures = ESPEvolution.testOnIncrementedPositions(
					rootOfNumTests, env, group.getPieces(), preyBehaviour, params);
			group.updateCaptureRatio(captures / Math.pow(rootOfNumTests, 2),
					(int) Math.pow(rootOfNumTests, 2));
		}
		Collections.sort(inputGroups);
		Collections.reverse(inputGroups);
		return new Vector<PredatorGroup>(inputGroups.subList(0, numToSelect));
	}
	
}
