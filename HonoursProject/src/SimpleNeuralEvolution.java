import java.util.Collections;
import java.util.Vector;
import java.util.Random;

public class SimpleNeuralEvolution 
{
	static final int numHiddenNodes = 10;
	static final int numPredators = 3;
	static final int populationSize = 1000;
	static final int trialsPerGeneration = 10; // 1000
static final int evaluationsPerTrial = 6; // 6
static final int generations = 50;
static final int boardSize = 10;
static final double mutationProbability = 0.4;
static final double earlyMutationStdDev = 0.05;
static final double lateMutationStdDev = 0.01;
static final int rootOfNumTests = 10;

static Vector<Genotype> genotypesUsed = new Vector<Genotype>();
static Vector<Genotype> bestGenotypes = new Vector<Genotype>();

public static void main(String[] args) {
	System.out.println("running");
		run();
}

public static int run( ) {

	Random random = new Random();
	Environment env = new Environment(boardSize);
	SimpleNeuralNetwork network;
	SimpleNeuralNetworkBehaviour sNNB;
	
	Vector<Piece> preyPieces = new Vector<Piece>();
	Vector<Piece> predatorPieces;

	StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 1);
	
	int preyX = random.nextInt(boardSize);
	int preyY = random.nextInt(boardSize);
	
	preyPieces.add(new Piece(preyX, preyY, true, env, runAway));
	
	// Create random Genotypes for this generation	
for(int j = 0 ; j < populationSize; j ++)
{
	genotypesUsed.add(new Genotype());
}


	

	for (int gen = 0; gen < generations; gen++) 
	{
		int captureCount = 0;
		
		/*if(bestGenotypes.size() > 0)
		System.out.println("Generation : " + gen + "   -   Genotypes Used: " + genotypesUsed.size() + "   -   With best Fitness: " + bestGenotypes.elementAt(0).getFitness());
	else*/
		System.out.println("Generation : " + gen);

	

	// For each generation, a number of trials are run to get fitness
	// values for the genotypes
	for(int node = 0 ; node < populationSize ; node ++)
	{
		predatorPieces = new Vector<Piece>();
		
		network = new SimpleNeuralNetwork(genotypesUsed.elementAt(node));
			
		sNNB = new SimpleNeuralNetworkBehaviour(boardSize,network);
					
		predatorPieces.add(new Piece(5, 5, false, env, sNNB));
		
		env.setPieces(predatorPieces, preyPieces);
		
		for (int trial = 0; trial < trialsPerGeneration; trial++) 
		{
			int captures = 0;
			TrialResult result = trial(predatorPieces, preyPieces, env,
					evaluationsPerTrial);
				captureCount += result.captureCount;
				captures += result.captureCount;
	
			double newFitness = 0;
			if (captures > 0)
					newFitness = (boardSize * (captureCount+1)) - 2*preyPieces.elementAt(0).getDistance(predatorPieces.elementAt(0));
			else
				if(preyPieces.elementAt(0).getDistance(predatorPieces.elementAt(0)) < 10)
					newFitness = boardSize - Math.pow((preyPieces.elementAt(0).getDistance(predatorPieces.elementAt(0))),2)/boardSize;
				
			// Update the genotypes fitnesses with the average fitness over
			// the evaluations
				genotypesUsed.elementAt(node).updateFitness(newFitness);
		}
	}
	
	// Replace the bottom ~50% of genotypes with offspring from
	// the top ~25%
	// Also mutate offspring with probability =
	// mutationProbability
	
	Collections.sort(genotypesUsed);
	Collections.reverse(genotypesUsed);
	
	for(int i = 0 ; i < 100 ; i ++)
	{
		bestGenotypes.add(genotypesUsed.elementAt(i));
	}
	
	/*for(int j = 0 ; j < genotypesUsed.size(); j++)
	{
		bestGenotypes.add(genotypesUsed.elementAt(j));
	}*/
	genotypesUsed.clear();
		
	/*Collections.sort(bestGenotypes);
	Collections.reverse(bestGenotypes);
	
	Vector<Genotype> temp1 = new Vector<Genotype>();
	for(int i = 0 ; i < 100 ; i ++)
	{
		temp1.add(bestGenotypes.elementAt(i));
	}
	bestGenotypes = temp1;*/


	double mutationStdDev = 0.2;
	System.out.println(bestGenotypes.size());
	int numberOfOffspring = bestGenotypes.size() / 2;
	int maxParentIndex = bestGenotypes.size() / 5;
	int replacementIndex = bestGenotypes.size() - 1;
	for (int i = 0; i < numberOfOffspring; i++) 
	{
		Genotype parent1 = bestGenotypes.elementAt(random
				.nextInt(maxParentIndex));
		Genotype parent2 = bestGenotypes.elementAt(random
				.nextInt(maxParentIndex));
		Genotype child = Genotype.crossover(parent1, parent2);
		if (random.nextDouble() < mutationProbability) {
			child.mutate(mutationStdDev);
		}
		bestGenotypes.remove(replacementIndex);
		bestGenotypes.add(replacementIndex, child);
		replacementIndex--;
	}
	
	
	// Construct the 3 fittest predators for the n instance test.
				Vector<Piece> testPredatorPieces = new Vector<Piece>();
				for (int pred = 0; pred < numPredators; pred++) 
				{
					Vector<Genotype> hiddenNodes = new Vector<Genotype>();
					
					for (int node = 0; node < numHiddenNodes; node++) 
					{
						hiddenNodes.add(bestGenotypes.elementAt(random.nextInt(bestGenotypes.size()/5)));
					}
		
					SimpleNeuralNetwork ann = new SimpleNeuralNetwork(hiddenNodes);
					
					SimpleNeuralNetworkBehaviour annBehaviour = new SimpleNeuralNetworkBehaviour(boardSize, ann);
					
					testPredatorPieces.add(new Piece(5, 5, false, env, annBehaviour));
				}
				
				// Run the n instance test.
				int testCaptureCount = 0;
				int preyPlacementIncrememnt = (int) Math.floor(boardSize
						/ ((double) rootOfNumTests));
				for (int preyRow = 0; preyRow < rootOfNumTests; preyRow++) {
					for (int preyCol = 0; preyCol < rootOfNumTests; preyCol++) {
						Vector<Piece> testPreyPieces = new Vector<Piece>();
						testPreyPieces.add(new Piece(preyRow
								* preyPlacementIncrememnt, preyCol
								* preyPlacementIncrememnt, true, env, runAway));
						env.setPieces(testPredatorPieces, testPreyPieces);
						SimulationResult result = env.run(false, false);
						testCaptureCount += result.preyCaught;
					}
			
				}
			
				System.out.println("Generation " + gen + " done: " + captureCount
						+ " captures, " + testCaptureCount + "/" + rootOfNumTests
						* rootOfNumTests + " test score.");// and best Fitness of : " + bestGenotypes.elementAt(0).getFitness());
		
				SimpleNeuralNetwork ann = new SimpleNeuralNetwork(bestGenotypes);
				ann.saveNetwork("SimplePredatorBehaviour");
					
					int i = 0;
					while(genotypesUsed.size() < populationSize)
					{
						genotypesUsed.add(bestGenotypes.elementAt(i));
						if(i == 99)
							i=0;
						else
							i++;
						
					}
					bestGenotypes.clear();
		
	}
	
	return 1;
}

//Run a set of evaluations on the predators to get fitness values for the
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
			for (Piece predator : predatorPieces)
				predator.setPosition(5, 5);

			env.setPieces(predatorPieces, preyPieces);
			SimulationResult result = env.run(false, false);
			captureCount += result.preyCaught;
			}

			for (int i = 0; i < numPredators; i++)
				avgEvalFitnesses[i] = avgEvalFitnesses[i] / evaluationsPerTrial;

			return new TrialResult(avgEvalFitnesses, captureCount);
		}
		
		private static class TrialResult 
		{
			public double[] avgEvalFitnesses;
			public int captureCount;

			public TrialResult(double[] avgEvalFitnesses, int captureCount) 
			{
				this.avgEvalFitnesses = avgEvalFitnesses;
				this.captureCount = captureCount;
			}
		}
	
}
