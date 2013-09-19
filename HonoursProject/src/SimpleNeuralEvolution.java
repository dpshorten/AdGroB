import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;
import java.util.Random;

public class SimpleNeuralEvolution 
{
	static final int numHiddenNodes = 10;
	static final int numInputNodes = 2;
	static final int numOutputNodes = 4;
	static final int numPredators = 3;
	static final int weights = numInputNodes + numOutputNodes;
	
	static final int populationSize = 1000;
	static final int numberOfBest = 100;
	static final int trialsPerGeneration = 100; // 1000
static final int evaluationsPerTrial = 6; // 6
static final int generations = 30;
static final int boardSize = 10;

static final double mutationProbability = 0.4;
static final double mutationSTD = 0.2;
static final double crossOverProbability = 0.2;
static final int rootOfNumTests = 10;
static final double visionRange = boardSize;
static int speed = 0;
static int captureCount = 0;

static final double[] preySpeeds = { 0.0, 0.1, 0.2, 0.3, 0.5, 0.6, 0.8, 0.9, 1};

static int stagnator = 0;
static int Stagnatingcaptures = 0;
static Random random = new Random();


static Vector<Genotype> genotypesUsed = new Vector<Genotype>();
static Vector<SimpleGenotype> simpleGenotypesUsed = new Vector<SimpleGenotype>();
static Vector<SimpleGenotype> bestGenotypes = new Vector<SimpleGenotype>();

static Vector<SimpleGenotype> pred1 = new Vector<SimpleGenotype>();
static Vector<SimpleGenotype> pred2 = new Vector<SimpleGenotype>();
static Vector<SimpleGenotype> pred3 = new Vector<SimpleGenotype>();

static Vector<Genotype> simpleGenomes = new Vector<Genotype>();
static SimpleGenotype genomes = new SimpleGenotype();
static Vector<SimpleGenotype> genomeList = new Vector<SimpleGenotype>();

static Vector<SimpleGenotype> best1 = new Vector<SimpleGenotype>();
static Vector<SimpleGenotype> best2 = new Vector<SimpleGenotype>();
static Vector<SimpleGenotype> best3 = new Vector<SimpleGenotype>();

public static void main(String[] args) 
{
	System.out.println("running");
	//try
	//{
		run();
		printPredatorFiles();
	//}
	//catch(Exception e)
	//{
		//System.out.println("Error occured, attempting to do a rescue print : " + e);
		//try
	//	{
			//printPredatorFiles();
		//}
		//catch(Exception a)
		//{
		//	System.out.println("Error occured while attempting to do a rescue print : " + a);
		//}
	//}
	
}

public static void printPredatorFiles()
{
	for (int predator = 0 ; predator < numPredators; predator++)
	{
		
		SimpleNeuralNetwork ann;
		
		if(predator == 0)
			saveNetwork("SimplePredatorBehaviour" + predator , best1);
		else if(predator == 1)
			saveNetwork("SimplePredatorBehaviour" + predator , best2);
		else
			saveNetwork("SimplePredatorBehaviour" + predator , best3);
	}
}

public static void saveNetwork(String fileName, Vector<SimpleGenotype> genotypes)
{
	File outputFile = new File(fileName);
	PrintWriter out = null;
	// Clear the file of any existing data.
	try {
		out = new PrintWriter(new FileWriter(outputFile, false));
	} catch (Exception e) {
		System.out.println("Error : " + e);
	}
	out.write("");
	out.close();
	// Now instantiate out in appending mode.
	try {
		out = new PrintWriter(new FileWriter(outputFile, true));
	} catch (Exception e) {
		System.out.println("Error : (SaveNetwork) : " + e);
	}
	// Write the data.
	//for (SimpleGenotype simplegenotype : genotypes) 
	//{
		
		out.write(genotypes.elementAt(0).toString());
		//out.write("\n");
	//}

	out.close();
}

public static void buildGenotypeList(int i)
{
	int i1 = 0;
	simpleGenotypesUsed.clear();
	while(simpleGenotypesUsed.size() < populationSize)
	{
		if(i == 0)
			simpleGenotypesUsed.add(best1.elementAt(i1));
		else if (i == 1)
			simpleGenotypesUsed.add(best2.elementAt(i1));
		else
			simpleGenotypesUsed.add(best3.elementAt(i1));
		
		
		if(i1 == 99)
			i1=0;
		else
			i1++;
							
	}
}

public static int run( ) 
{

	Random random = new Random();
	Environment env = new Environment(boardSize, preySpeeds[speed], numPredators);
	SimpleNeuralNetwork network;
	SimpleNeuralNetworkBehaviour sNNB;
	
	Vector<Piece> preyPieces = new Vector<Piece>();
	Vector<Piece> predatorPieces = new Vector<Piece>();

	StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 1);
	
//	int preyX = random.nextInt(boardSize);
//	int preyY = random.nextInt(boardSize);
//	
//	int preyX2 = random.nextInt(boardSize);
//	int preyY2 = random.nextInt(boardSize);
	
	
	//preyPieces.add(new Piece(preyX2,preyY2,true, env, runAway));
	
	// Create random Genotypes for this generation	

	for(int i = 0 ; i < numPredators; i ++)
	{
		for(int j = 0 ; j < populationSize; j ++)
		{
			genotypesUsed.clear();
			for(int k = 0 ; k < numHiddenNodes ; k ++)
			{
				genotypesUsed.add(new Genotype(4.0f));
			}
			
			simpleGenotypesUsed.add(new SimpleGenotype(genotypesUsed, weights, numInputNodes, numOutputNodes));
		}
		
		if(i == 0)
			pred1.addAll(simpleGenotypesUsed);
		else if (i == 1)
			pred2.addAll(simpleGenotypesUsed);
		else
			pred3.addAll(simpleGenotypesUsed);
		
		
		genotypesUsed.clear();
	}

	int predator = 0;
	for (int gen = 0; gen < generations; gen++) 
	{
		captureCount = 0;
		if(gen%3 == 0)
		{
			speed++;
			if (speed >= preySpeeds.length)
				speed = preySpeeds.length -1;
			//env = new Environment(boardSize, preySpeeds[speed],numPredators);
		
		}
		
		for(int i = 0 ; i < numPredators; i ++)
		{
			predator = i;
			genotypesUsed.clear();
			
			if(gen == 0)
			{
			
				if(i == 0)
					simpleGenotypesUsed.addAll(pred1);
				else if (i == 1)
					simpleGenotypesUsed.addAll(pred2);
				else
					simpleGenotypesUsed.addAll(pred3);
				
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
			
			
			
			for(int genotype = 0 ; genotype < populationSize ; genotype ++)
			{
				
				//System.out.println("Running genotype " + genotype );
		
				network = new SimpleNeuralNetwork(simpleGenotypesUsed.elementAt(genotype).convertToGenotype());
			
				sNNB = new SimpleNeuralNetworkBehaviour(boardSize,network);
					
//				predatorPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), false, env, sNNB));
//				predatorPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), false, env, sNNB));
//				predatorPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), false, env, sNNB));
//				
//				preyPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), true, env, runAway));
//				
//				env.setPieces(predatorPieces, preyPieces);
		
				for (int trial = 0; trial < trialsPerGeneration; trial++) 
				{
					predatorPieces.clear();
					preyPieces.clear();
					
//					System.out.println("Running trial " + trial );
					predatorPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), false, env, sNNB));
					predatorPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), false, env, sNNB));
					predatorPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), false, env, sNNB));
					
					preyPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), true, env, runAway));
					
					env.setPieces(predatorPieces, preyPieces);
					
					double newFitness = trial(predatorPieces, preyPieces, env, evaluationsPerTrial);
	
//					double newFitness = 0;
//					
//					for(Piece aPrey: preyPieces)
//					{
//						for(Piece aPred: predatorPieces)
//						{
//							if (aPrey.getDistance(aPred) < (boardSize / 5))
//								newFitness += captures + (boardSize / (aPrey.getDistance(aPred)+boardSize));
//						}
//					}
//				
//					// Update the genotypes fitnesses with the average fitness over
//					// the evaluations
					//System.out.println("Updating fitness : " + simpleGenotypesUsed.elementAt(genotype).getFitness() + " with new fitness : " + newFitness);
					simpleGenotypesUsed.elementAt(genotype).updateFitness(newFitness);
					//System.out.println("Fitness Updated,  New Fitness = " + simpleGenotypesUsed.elementAt(genotype).getFitness() * 100);
				}
			}
	
			// Replace the bottom ~50% of genotypes with offspring from
			// the top ~25%
			// Also mutate offspring with probability =
			// mutationProbability
			
			Collections.sort(simpleGenotypesUsed);
			Collections.reverse(simpleGenotypesUsed);
			bestGenotypes.clear();
			
			for(int i1 = 0 ; i1 < numberOfBest ; i1 ++)
			{
				bestGenotypes.add(simpleGenotypesUsed.elementAt(i1));
			}
	
			/*for(int j = 0 ; j < genotypesUsed.size(); j++)
			{
				bestGenotypes.add(genotypesUsed.elementAt(j));
			}*/
			simpleGenotypesUsed.clear();
				
			/*Collections.sort(bestGenotypes);
			Collections.reverse(bestGenotypes);
			
			Vector<Genotype> temp1 = new Vector<Genotype>();
			for(int i = 0 ; i < 100 ; i ++)
			{
				temp1.add(bestGenotypes.elementAt(i));
			}
			bestGenotypes = temp1;*/
			
			
			//System.out.println(bestGenotypes.size());
			int numberOfOffspring = bestGenotypes.size() / 2;
			int maxParentIndex = bestGenotypes.size() / 5;
			int replacementIndex = bestGenotypes.size() - 1;
			for (int i1 = 0; i1 < numberOfOffspring; i1++) 
			{
				SimpleGenotype parent1 = bestGenotypes.elementAt(random
						.nextInt(maxParentIndex));
				SimpleGenotype parent2 = bestGenotypes.elementAt(random
						.nextInt(maxParentIndex));
				
				SimpleGenotype child = parent1.crossover(parent2, crossOverProbability);
				
				if (random.nextDouble() < mutationProbability) {
					child.mutate(mutationSTD);
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
			SimpleNeuralNetwork ann;
			
			if(pred == 0)
			{
				ann = new SimpleNeuralNetwork(best1.elementAt(0));
			}
			else if (pred == 1)
			{
				ann = new SimpleNeuralNetwork(best2.elementAt(0));		
			}
			else
			{
				ann = new SimpleNeuralNetwork(best3.elementAt(0));
			}
			
			SimpleNeuralNetworkBehaviour annBehaviour = new SimpleNeuralNetworkBehaviour(boardSize, ann);
			
			testPredatorPieces.add(new Piece(random.nextInt(boardSize), random.nextInt(boardSize), false, env, annBehaviour));
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
		* rootOfNumTests + " test score. Best Fitness of : " + bestGenotypes.elementAt(0).getFitness()*100);
		
		System.out.println("Stagnator = " + stagnator + ", Stagnatingcaptures and testCaptureCount = " + Stagnatingcaptures + "," + testCaptureCount);
		
//		if(Stagnatingcaptures == 0)
//				Stagnatingcaptures = testCaptureCount;
//		else if(Stagnatingcaptures == testCaptureCount)
//		{
//			stagnator++;
//			System.out.println("Stagnating!!!" + stagnator);
//		}
//		else
//		{
//			Stagnatingcaptures = testCaptureCount;
//			stagnator = 0;
//		}
//			
//		if(stagnator > 5)
//		{
//			stagnator = 0;
//			burstMutate(predator);
//		}
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

//private static void burstMutate(int j) 
//{
//	
//	if(j == 0)
//	{
//		best1.clear();
//		bestGenotypes.addAll(best1);
//	}
//	else if (j == 1)
//	{
//		best2.clear();
//		bestGenotypes.addAll(best2);
//	}
//	else
//	{
//		best3.clear();
//		bestGenotypes.addAll(best3);
//	}
//	
//	
//	System.out.println("Running burst mutation");
//	Vector<Genotype> newGenotypes = new Vector<Genotype>();
//	
//	for(int i = 0 ; i < 15 ; i++)
//	{
//		Collections.sort(bestGenotypes);
//		Collections.reverse(bestGenotypes);
//		newGenotypes.add(bestGenotypes.elementAt(i));
//	}
//	
//	
//	double mutationStdDev = 0.8;
//	//System.out.println(bestGenotypes.size());
//	int numberOfOffspring = bestGenotypes.size() - 15;
//	for (int i = 0; i < numberOfOffspring; i++) 
//	{
//		Genotype parent1 = bestGenotypes.elementAt(random.nextInt(bestGenotypes.size()-1));
//		Genotype parent2 = bestGenotypes.elementAt(random.nextInt(bestGenotypes.size()-1));
//		Genotype child = Genotype.crossover(parent1, parent2);
//		if (random.nextDouble() < mutationProbability) {
//			child.mutate(mutationStdDev);
//		}
//		newGenotypes.add(child);
//	}
//	
//	if(j == 0)
//	{
//		best1.clear();
//		best1.addAll(newGenotypes);
//	}
//	else if (j == 1)
//	{
//		best2.clear();
//		best2.addAll(newGenotypes);
//	}
//	else
//	{
//		best3.clear();
//		best3.addAll(newGenotypes);
//	}
//}
//
//	//Run a set of evaluations on the predators to get fitness values for the
//	// genotypes
	private static double trial(Vector<Piece> predatorPieces,
			Vector<Piece> preyPieces, Environment env, int evaluations)
	{
		//captureCount = 0;
		int captures = 0;
		double newFitness = 0;
		double shortestDistance = Integer.MAX_VALUE;;
		for (int eval = 0; eval < evaluations; eval++)
		{
			//System.out.println("Running evaluation " + eval );
			env.resetCaptureCount();
			env.setPieces(predatorPieces, preyPieces);
			SimulationResult result = env.run(false, false);
			captures = result.preyCaught;
			captureCount += captures; 
				
				
			if(captures >= 1)
				newFitness += 1;
			else
			{
				for(Piece aPrey: preyPieces)
				{
					for(Piece aPred: predatorPieces)
					{
						if (aPrey.getDistance(aPred) < (visionRange))
						{
							if(aPrey.getDistance(aPred) < shortestDistance)
								shortestDistance = aPrey.getDistance(aPred);
						}
					}
				}
				newFitness += 1/shortestDistance;
			}
		}
		
		return newFitness/evaluations;
	}
}
