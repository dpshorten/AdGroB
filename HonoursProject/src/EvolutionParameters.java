
public class EvolutionParameters {
	
	//Control parameters
	boolean doMigration = true;
	boolean useBehaviourDistance = true;
	boolean useGenotypeDistance = true;
	boolean doDavidsDeltaThings = true;
	boolean doInterpopulationCrossover = true;
	
	public static int numHiddenNodes = 10;
	public static int numPredators = 3;
	public static int subPopulationSize = 100;
	// Each trial set runs a number of trials equal to the sub-population size.
	public static int trialSetsPerGeneration = 10; // 10
	public static int trialsPerGeneration = trialSetsPerGeneration * subPopulationSize;
	public static int evaluationsPerTrial = 6; // 6
	public static int generations = 200;
	public static int boardSize = 100;
	public static double mutationProbability = 0.4;
	public static double earlyMutationStdDev = 0.3;
	public static double lateMutationStdDev = 0.2;
	public static double earlyBurstMutationAmountStdDev = 0.7;
	public static double lateBurstMutationAmountStdDev = 0.05;
	public static double burstMutationEarlyLateFitnessCutoff = 0.9;
	public static int burstMutationWaitBeforeRepeat = 10;
	public static int burstMutationWaitBeforeFirst = 8;
	public static int burstMutationWaitAfterMigration = 6;
	public static int burstMutationWaitAfterEpochChange = 6;
	public static int burstMutationTestLookBackDistance = 5;
	public static double burstMutationTestRatioOfTrialsDifference = 0.01;
	public static int rootOfNumTests = 10;
	public static double ratioCapturesForNextEpoch = 0.6;
	public static double ratioCapturesForEnd = 0.8;
	public static int ratioHitsBeforeNextEpoch = 2;
	public static double[] preySpeeds = { 0.1, 1};
	public static Integer[] predatorPositions = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	public static final int FinalTestNumEvaluationsToRun = 100;
	
	// Migration
	public static int migrationStartingGen = 1;
	public static int migrationGenInterval = 2;
	public static int migrationLateGenInterval = 2;
	public static int migrationLateGenThreshold = 25;
	public static int migrationNumMigrants = 2;
	public static double migrationBehaviourSimilarityThreshhold = 0.7;
	public static double migrationGenotypeDistanceThreshhold = 5;
	
	//Crossover
	public int crossoverGenInterval = 2;
	public int crossoverFirstGen = 2;
	public double crossoverPopulationPercentage = 0.05;
	public double crossoverBehaviourSimilarityThreshhold = 0.6;
	public double crossoverGenotypeDistanceThreshhold = 5;
	
	// Variables for David's delta coding.
	public static int genotypesPerSubPopulation = 20;
	public static int testedPieceGroups = 20;
	public static int rootOfNumTestsDelta = 7;
	public static double stdDev = 0.3;
	public static double ratioDifference = 0.0;
}
