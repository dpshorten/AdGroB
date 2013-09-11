import java.util.Collections;
import java.util.Vector;
import java.util.Random;

public class SimpleNeuralEvolution 
{
	static final int numHiddenNodes = 10;
	static final int numPredators = 3;
	static final int populationSize = 100;
	static final int numberOfBest = 100;
	static final int trialsPerGeneration = 100; // 1000
static final int evaluationsPerTrial = 6; // 6
static final int generations = 3;
static final int boardSize = 10;
static final double mutationProbability = 0.4;
static final double earlyMutationStdDev = 0.05;
static final double lateMutationStdDev = 0.01;
static final int rootOfNumTests = 10;
static int speed = 0;

static final double[] preySpeeds = { 0.01, 0.1, 0.15, 0.2, 0.3, 0.5, 0.7, 0.9, 1};

static int stagnator = 0;
static int Stagnatingcaptures = 0;
static Random random = new Random();


static Vector<Genotype> genotypesUsed = new Vector<Genotype>();
static Vector<Genotype> bestGenotypes = new Vector<Genotype>();

static Vector<Genotype> pred1 = new Vector<Genotype>();
static Vector<Genotype> pred2 = new Vector<Genotype>();
static Vector<Genotype> pred3 = new Vector<Genotype>();

static Vector<Genotype> best1 = new Vector<Genotype>();
static Vector<Genotype> best2 = new Vector<Genotype>();
static Vector<Genotype> best3 = new Vector<Genotype>();

public static void main(String[] args) 
{
	System.out.println("running");
	run();
	printPredatorFiles();
}

public static void printPredatorFiles()
{
	for (int predator = 0 ; predator < numPredators; predator++)
	{
		
		SimpleNeuralNetwork ann;
		
		if(predator == 0)
			ann = new SimpleNeuralNetwork(best1);
		else if(predator == 1)
			ann = new SimpleNeuralNetwork(best2);
		else
			ann = new SimpleNeuralNetwork(best3);
		
		
		ann.saveNetwork("SimplePredatorBehaviour" + predator);
	}
}

public static void buildGenotypeList(int i)
{
	int i1 = 0;
	while(genotypesUsed.size() < populationSize)
	{
		if(i == 0)
			genotypesUsed.add(best1.elementAt(i1));
		else if (i == 1)
			genotypesUsed.add(best2.elementAt(i1));
		else
			genotypesUsed.add(best3.elementAt(i1));
		
		
		if(i1 == 99)
			i1=0;
		else
			i1++;
							
	}
}

public static int run( ) 
{

	Random random = new Random();
	Environment env = new Environment(boardSize, preySpeeds[speed]);
	SimpleNeuralNetwork network;
	SimpleNeuralNetworkBehaviour sNNB;
	
	Vector<Piece> preyPieces = new Vector<Piece>();
	Vector<Piece> predatorPieces;

	StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 1);
	
	int preyX = random.nextInt(boardSize);
	int preyY = random.nextInt(boardSize);
	
	int preyX2 = random.nextInt(boardSize);
	int preyY2 = random.nextInt(boardSize);
	
	preyPieces.add(new Piece(preyX, preyY, true, env, runAway));
	preyPieces.add(new Piece(preyX2,preyY2,true, env, runAway));
	
	// Create random Genotypes for this generation	

	for(int i = 0 ; i < numPredators; i ++)
	{
		for(int j = 0 ; j < populationSize; j ++)
		{
			genotypesUsed.add(new Genotype(4.0f));
		}
		
		if(i == 0)
			pred1.addAll(genotypesUsed);
		else if (i == 1)
			pred2.addAll(genotypesUsed);
		else
			pred3.addAll(genotypesUsed);
	}


	int captureCount = 0;
	int predator = 0;
	for (int gen = 0; gen < generations; gen++) 
	{
		captureCount = 0;
		if(gen%10 == 0)
		{
			speed++;
			if (speed >= preySpeeds.length)
				speed = preySpeeds.length;
			env = new Environment(boardSize, preySpeeds[speed]);
		
		}
		for(int i = 0 ; i < numPredators; i ++)
		{
			predator = i;
			genotypesUsed.clear();
			
			if(gen == 0)
			{
			
				if(i == 0)
					genotypesUsed.addAll(pred1);
				else if (i == 1)
					genotypesUsed.addAll(pred2);
				else
					genotypesUsed.addAll(pred3);
				
			}
			else
			{
				buildGenotypeList(i);
			}
		
			/*if(bestGenotypes.size() > 0)
			System.out.println("Generation : " + gen + "   -   Genotypes Used: " + genotypesUsed.size() + "   -   With best Fitness: " + bestGenotypes.elementAt(0).getFitness());
			else*/
			System.out.println("Generation : " + gen + "   Predator : " + i);
	
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
					TrialResult result = trial(predatorPieces, preyPieces, env, evaluationsPerTrial);
					captureCount += result.captureCount;
					captures += result.captureCount;
	
					double newFitness = 0;
					if (captures > 0)
						newFitness = (boardSize * (captureCount+1)) - 2*preyPieces.elementAt(0).getDistance(predatorPieces.elementAt(0));
					else
						if(preyPieces.elementAt(0).getDistance(predatorPieces.elementAt(0)) < (boardSize / 3))
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
			bestGenotypes.clear();
			
			for(int i1 = 0 ; i1 < numberOfBest ; i1 ++)
			{
				bestGenotypes.add(genotypesUsed.elementAt(i1));
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
			for (int i1 = 0; i1 < numberOfOffspring; i1++) 
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
			
			if(predator == 0)
			{
				best1.clear();
				best1.addAll(bestGenotypes);
			}
			else if (predator == 1)
			{
				best2.clear();
				best2.addAll(bestGenotypes);
			}
			else
			{
				best3.clear();
				best3.addAll(bestGenotypes);
			}
		}
	
	
		// Construct the 3 fittest predators for the n instance test.
		Vector<Piece> testPredatorPieces = new Vector<Piece>();
		for (int pred = 0; pred < numPredators; pred++) 
		{
				
			if(pred == 0)
				bestGenotypes.addAll(best1);
			else if (pred == 1)
				bestGenotypes.addAll(best2);
			else
				bestGenotypes.addAll(best3);
				
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
		for (int preyRow = 0; preyRow < rootOfNumTests; preyRow++) 
		{
			for (int preyCol = 0; preyCol < rootOfNumTests; preyCol++) 
			{
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
		* rootOfNumTests + " test score. Best Fitness of : " + bestGenotypes.elementAt(0).getFitness());
		
		System.out.println("Stagnator = " + stagnator + ", Stagnatingcaptures and testCaptureCount = " + Stagnatingcaptures + "," + testCaptureCount);
		
		if(Stagnatingcaptures == 0)
				Stagnatingcaptures = testCaptureCount;
		else if(Stagnatingcaptures == testCaptureCount)
		{
			stagnator++;
			System.out.println("Stagnating!!!" + stagnator);
		}
		else
		{
			Stagnatingcaptures = testCaptureCount;
			stagnator = 0;
		}
			
		if(stagnator > 2)
		{
			stagnator = 0;
			burstMutate(predator);
		}
//		else
//		{
//								
//			int i1 = 0;
//			while(genotypesUsed.size() < populationSize)
//			{
//				genotypesUsed.add(bestGenotypes.elementAt(i1));
//				if(i1 == 99)
//					i1=0;
//				else
//					i1++;
//									
//			}
//								
//		}
//		
////		if(predator == 0)
////			pred1.addAll(genotypesUsed);
////		else if (predator == 1)
////			pred2.addAll(genotypesUsed);
////		else
////			pred3.addAll(genotypesUsed);
////					
	}

return 1;
}

private static void burstMutate(int j) 
{
	
	if(j == 0)
	{
		best1.clear();
		bestGenotypes.addAll(best1);
	}
	else if (j == 1)
	{
		best2.clear();
		bestGenotypes.addAll(best2);
	}
	else
	{
		best3.clear();
		bestGenotypes.addAll(best3);
	}
	
	
	System.out.println("Running burst mutation");
	Vector<Genotype> newGenotypes = new Vector<Genotype>();
	
	for(int i = 0 ; i < 15 ; i++)
	{
		Collections.sort(bestGenotypes);
		Collections.reverse(bestGenotypes);
		newGenotypes.add(bestGenotypes.elementAt(i));
	}
	
	
	double mutationStdDev = 0.8;
	//System.out.println(bestGenotypes.size());
	int numberOfOffspring = bestGenotypes.size() - 15;
	for (int i = 0; i < numberOfOffspring; i++) 
	{
		Genotype parent1 = bestGenotypes.elementAt(random.nextInt(bestGenotypes.size()-1));
		Genotype parent2 = bestGenotypes.elementAt(random.nextInt(bestGenotypes.size()-1));
		Genotype child = Genotype.crossover(parent1, parent2);
		if (random.nextDouble() < mutationProbability) {
			child.mutate(mutationStdDev);
		}
		newGenotypes.add(child);
	}
	
	if(j == 0)
	{
		best1.clear();
		best1.addAll(newGenotypes);
	}
	else if (j == 1)
	{
		best2.clear();
		best2.addAll(newGenotypes);
	}
	else
	{
		best3.clear();
		best3.addAll(newGenotypes);
	}
}

//Run a set of evaluations on the predators to get fitness values for the
// genotypes
private static TrialResult trial(Vector<Piece> predatorPieces,
		Vector<Piece> preyPieces, Environment env, int evaluations)
{
	double[] avgEvalFitnesses = new double[numPredators];
	int captureCount = 0;
	Random random = new Random();
	for (int eval = 0; eval < evaluations; eval++)
	{
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
