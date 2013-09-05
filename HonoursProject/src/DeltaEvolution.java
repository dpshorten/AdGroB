import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class DeltaEvolution {

	public static final int deltaSubPopulationSize = 100;
	public static final int numGroupsFirstRound = 2000;
	public static final int rootNumEvalsFirstRound = 1;
	public static final int numGroupsSecondRound = 500;
	public static final int rootNumEvalsSecondRound = 2;
	public static final int numGroupsThirdRound = 50;
	public static final int rootNumEvalsThirdRound = 4;
	public static final int numGroupsFourthRound = 10;
	public static final int rootNumEvalsFourthRound = 10;
	
	//public static final double earlyLateRatioCutoff = 0.5;
	public static final double firstStdDev = 0.4;
	public static final double secondStdDev = 0.3;
	public static final double thirdStdDev = 0.2;
	public static final double fourthStdDev = 0.2;
	public static final double fifthStdDev = 0.2;
	public static final double sixthStdDev = 0.1;
	public static final double seventhStdDev = 0.1;
	public static final double eighthStdDev = 0.05;
	public static final int bigBurstMutationLookBackDistance = 3;
	public static final int bigBurstMutationWaitBeforeRepeat = 3;
	public static final double bigStdDev = 0.3;
	
	public static void main(String[] args) {
		run();
	}
	
	public static int run() {

		int bigBurstMutationTicker = bigBurstMutationLookBackDistance;
		
		Vector<ESPPopulation> agentPopulations = new Vector<ESPPopulation>();

		// Initialise a population of genotypes for each predator
		for (int i = 0; i < ESPEvolution.numPredators; i++) {
			agentPopulations.add(new ESPPopulation(ESPEvolution.numHiddenNodes,
					deltaSubPopulationSize));
		}
		

		Random random = new Random();
		int epochNumber = 0;

		int gen = 0;
		Vector<Double> previousBestCaptureRatios = new Vector<Double>();
		for (gen = 0; gen < ESPEvolution.generations; gen++) {
			
			Environment env = new Environment(ESPEvolution.boardSize, ESPEvolution.preySpeeds[epochNumber],
					ESPEvolution.numPredators);

			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
					ESPEvolution.boardSize, 1);
			
			Vector<PredatorGroup> predatorGroups = new Vector<PredatorGroup>();
			for (int i = 0; i < numGroupsFirstRound; i++) {
				Vector<Vector<Genotype>> groupsGenotypes = new Vector<Vector<Genotype>>();
				for (ESPPopulation pop : agentPopulations) {
					Vector<Genotype> predatorsGenotypes = new Vector<Genotype>();
					for (ESPSubPopulation subPop : pop.subPopulations) {
						predatorsGenotypes.add(subPop.getNthFittestGenotype(random
								.nextInt(deltaSubPopulationSize)));
					}
					groupsGenotypes.add(predatorsGenotypes);
					predatorGroups.add(new PredatorGroup(groupsGenotypes, env));
				}
			}
			
			predatorGroups = selectFromPredatorGroups(rootNumEvalsFirstRound, predatorGroups, env, runAway, numGroupsSecondRound);
			predatorGroups = selectFromPredatorGroups(rootNumEvalsSecondRound, predatorGroups, env, runAway, numGroupsThirdRound);
			predatorGroups = selectFromPredatorGroups(rootNumEvalsThirdRound, predatorGroups, env, runAway, numGroupsFourthRound);
			predatorGroups = selectFromPredatorGroups(rootNumEvalsFourthRound, predatorGroups, env, runAway, 1);
			
			double captureRatio = predatorGroups.get(0).getCaptureRatio();
			System.out.println("Generation " + (gen + 1) + " done, best ratio: " + captureRatio);
			
			
			double stdDev = 0;
			if(captureRatio <= 0.1) {
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
			
			previousBestCaptureRatios.add(predatorGroups.get(0).getCaptureRatio());
			if(bigBurstMutationTicker <= 0) {
				if(previousBestCaptureRatios.get(gen) < previousBestCaptureRatios.get(gen - bigBurstMutationLookBackDistance) + 0.03) {
					stdDev = bigStdDev;
					bigBurstMutationTicker = bigBurstMutationWaitBeforeRepeat + 1;
					System.out.println("Big burst mutation!");
				}
			}
			bigBurstMutationTicker--;
			
			for (int i = 0; i < ESPEvolution.numPredators; i++) { 
				agentPopulations
						.set(i, new ESPPopulation(deltaSubPopulationSize,
								predatorGroups.get(0).getGenotypes().get(i), stdDev));
			}
			if (predatorGroups.get(0).getCaptureRatio() > ESPEvolution.ratioCapturesForNextEpoch) {
				bigBurstMutationTicker = bigBurstMutationWaitBeforeRepeat + 1;
				epochNumber++;
				System.out.println("Epoch Change to number "
						+ (epochNumber + 1));
				if (epochNumber >= ESPEvolution.preySpeeds.length) {
					break;
				}
			}
		}
				
		
		return gen;
	}
	
	public static Vector<PredatorGroup> selectFromPredatorGroups(int rootOfNumTests, Vector<PredatorGroup> inputGroups,
			Environment env, Behaviour preyBehaviour, int numToSelect) {
		for(PredatorGroup group : inputGroups) {
			int captures = ESPEvolution.testOnIncrementedPositions(rootOfNumTests, env, group.getPieces(), preyBehaviour);
			group.updateCaptureRatio(captures / Math.pow(rootOfNumTests, 2), (int) Math.pow(rootOfNumTests, 2));
		}
		Collections.sort(inputGroups);
		Collections.reverse(inputGroups);
		return new Vector<PredatorGroup>(inputGroups.subList(0, numToSelect));
	}
}
