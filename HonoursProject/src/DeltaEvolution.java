import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class DeltaEvolution {

	public static final int deltaSubPopulationSize = 20;
	public static final int numGroupsFirstRound = 200;
	public static final int rootNumEvalsFirstRound = 3;
	public static final int numGroupsSecondRound = 50;
	public static final int rootNumEvalsSecondRound = 7;
	public static final int numGroupsThirdRound = 10;
	public static final int rootNumEvalsThirdRound = 10;
	public static final double earlyStdDev = 0.1;
	public static final double earlyLateRatioCutoff = 0.5;
	public static final double lateStdDev = 0.05;
	
	public static void main(String[] args) {
		run();
	}
	
	public static int run() {

		Vector<ESPPopulation> agentPopulations = new Vector<ESPPopulation>();

		// Initialise a population of genotypes for each predator
		for (int i = 0; i < ESPEvolution.numPredators; i++) {
			agentPopulations.add(new ESPPopulation(ESPEvolution.numHiddenNodes,
					deltaSubPopulationSize));
		}
		

		Random random = new Random();
		int epochNumber = 0;

		int gen = 0;
		for (gen = 0; gen < ESPEvolution.generations; gen++) {
			
			Environment env = new Environment(ESPEvolution.boardSize, ESPEvolution.preySpeeds[epochNumber],
					ESPEvolution.numPredators);

			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
					ESPEvolution.boardSize, 1);
			
			Vector<PredatorGroup> predatorGroups = new Vector<PredatorGroup>();
			for (int i = 0; i < numGroupsFirstRound; i++) {
				int j = 0;
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
			predatorGroups = selectFromPredatorGroups(rootNumEvalsThirdRound, predatorGroups, env, runAway, 1);
			System.out.println("Generation " + (gen + 1) + " done, best ratio: " + predatorGroups.get(0).getCaptureRatio());
			double stdDev = predatorGroups.get(0).getCaptureRatio() > earlyLateRatioCutoff ? earlyStdDev : lateStdDev; 
			for (int i = 0; i < ESPEvolution.numPredators; i++) { 
				agentPopulations
						.set(i, new ESPPopulation(deltaSubPopulationSize,
								predatorGroups.get(0).getGenotypes().get(i), stdDev));
			}
			if (predatorGroups.get(0).getCaptureRatio() > ESPEvolution.ratioCapturesForNextEpoch) {
				epochNumber++;
				System.out.println("Epoch Change to number "
						+ (epochNumber + 1));
				if (epochNumber >= ESPEvolution.preySpeeds.length) {
					break;
				}
			}
		}
				
		
		return 1;
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
