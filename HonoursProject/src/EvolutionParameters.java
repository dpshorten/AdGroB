
public class EvolutionParameters {
	public static int numHiddenNodes = 10;
	public static int numPredators = 3;
	public static int subPopulationSize = 100;
	// Each trial set runs a number of trials equal to the sub-population size.
	public static int trialSetsPerGeneration = 10; // 10
	public static int trialsPerGeneration = trialSetsPerGeneration
			* subPopulationSize;
	public static int evaluationsPerTrial = 6; // 6
	public static int generations = 200;
	public static int boardSize = 100;
	public static double mutationProbability = 0.4;
	public static double earlyMutationStdDev = 0.05;
	public static double lateMutationStdDev = 0.01;
	public static double earlyBurstMutationAmountStdDev = 0.3;
	public static double lateBurstMutationAmountStdDev = 0.05;
	public static double newEpochBurstMutationAmountStdDev = 0.2;
	public static int burstMutationWaitBeforeRepeat = 20;
	public static int burstMutationWaitBeforeFirst = 10;
	public static int burstMutationWaitAfterMigration = 6;
	public static int burstMutationWaitAfterEpochChange = 6;
	public static int burstMutationTestLookBackDistance = 5;
	public static double burstMutationTestRatioOfTrialsDifference = 0.01;
	public static int rootOfNumTests = 10;
	public static double ratioCapturesForNextEpoch = 0.7;
	public static double ratioCapturesForEnd = 0.8;
	public static int ratioHitsBeforeNextEpoch = 2;
	public static double[] preySpeeds = { 0.1, 0.95, 1 };

	public static Integer[] predatorPositions = { 30, 30, boardSize - 30, 30, 30,
			boardSize - 30};
	
	// Variables for David's delta coding.
	public static int genotypesPerSubPopulation = 5;
	public static int testedPieceGroups = 100;
	public static int rootOfNumTestsDelta = 7;
	public static double stdDev = 0.1;
	public static double ratioDifference = 0.0;
}
