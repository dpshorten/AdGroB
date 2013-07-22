import java.util.Collections;
import java.util.Vector;
import java.util.Random;

public class ESPEvolution {
	static final int numHiddenNodes = 10;
	static final int numPredators = 3;
	static final int subPopulationSize = 100;
	static final int trialsPerGeneration = 1000; //1000
	static final int evaluationsPerTrial = 6; //6
	static final int generations = 50;
	static final int boardSize = 100;
	static final double mutationProbability = 0.4;
	static final double earlyMutationStdDev = 0.05;
	static final double lateMutationStdDev = 0.01;
	static final double earlyBurstMutationAmountStdDev = 0.3;
	static final double lateBurstMutationAmountStdDev = 0.05;
	static final int burstMutationWaitBeforeRepeat = 30;
	static final int burstMutationWaitBeforeFirst = 20;
	static final int burstMutationTestLookBackDistance = 5;
	static final double burstMutationTestRatioOfPopDifference = 0.001;
	
	static Vector<ESPPopulation> agentPopulations = new Vector<ESPPopulation>();
	
	public static void main(String[] args){
		
		//Initialise a population of genotypes for each predator
		for(int i=0; i<numPredators; i++)
			agentPopulations.add(new ESPPopulation(numHiddenNodes, subPopulationSize));
		
		Random random = new Random();
		Environment env = new Environment(boardSize, 1);
		
		Vector<Piece> preyPieces = new Vector<Piece>();
		//VectorRunAwayBehaviour runAway = new VectorRunAwayBehaviour(boardSize);
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 1);
		int preyX = random.nextInt(boardSize);
		int preyY = random.nextInt(boardSize);
		preyPieces.add(new Piece(preyX,preyY,true,env,runAway));
				
		int burstMutationTicker = burstMutationWaitBeforeFirst;
		Vector<Integer> capturesForEachGeneration = new Vector<Integer>(generations); 
		for(int gen=0; gen<generations; gen++){
			//For each generation, a number of trials are run to get fitness values for the genotypes
			int captureCount = 0;
			for(int trial=0; trial<trialsPerGeneration; trial++){
				Vector<Piece> predatorPieces = new Vector<Piece>();
				//usedGenotypes will contain a vector of active genotypes for each predator
				Vector<Vector<Genotype>> usedGenotypes = new Vector<Vector<Genotype>>();
				
				//Build an ANN for each predator using a randomly choosen node from each subpopulation
				for(int pred=0; pred<numPredators; pred++){
					Vector<Genotype> hiddenNodes = new Vector<Genotype>();
					for(int node=0; node<numHiddenNodes; node++){
						//Add a random genotype from the appropriate subpopulation to the hidden nodes
						int rand = random.nextInt(subPopulationSize);
						hiddenNodes.add(agentPopulations.elementAt(pred).getSubPopulationForNode(node).getGenotype(rand));
					}
					
					ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(hiddenNodes);
					ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(boardSize, ann);
					predatorPieces.add(new Piece(5, 5, false, env, annBehaviour));
					usedGenotypes.add(hiddenNodes);
				}
				
				TrialResult result = trial(predatorPieces, preyPieces, env, evaluationsPerTrial);
				captureCount += result.captureCount;

				//Update the genotypes fitnesses with the average fitness over the evaluations
				for(int i = 0; i<numPredators; i++){
					for(Genotype genotype : usedGenotypes.elementAt(i))
						genotype.updateFitness(result.avgEvalFitnesses[i]);
				}
				
				
			}//trials
			
			//Create offspring by applying crossover and mutation to the genotype subpopulations
			for(int pred=0; pred<numPredators; pred++){
				for(int subpop=0; subpop<numHiddenNodes; subpop++){
					
					//Within each subpopulation, rank the genotypes by fitness with element 0 being the highest fitness
					Vector<Genotype> genotypes = agentPopulations.elementAt(pred).getSubPopulationForNode(subpop).getAllGenotypes();
					Collections.sort(genotypes);
					Collections.reverse(genotypes);
					
					//Replace the bottom ~50% of genotypes with offspring from the top ~25%
					//Also mutate offspring with probability = mutationProbability
					/*
					int numberOfOffspring = subPopulationSize / 2;
					int maxParentIndex = subPopulationSize / 4;
					int replacementIndex = genotypes.size() - 1;
					for(int i=0; i<numberOfOffspring; i++){
						Genotype parent1 = genotypes.elementAt(random.nextInt(maxParentIndex));
						Genotype parent2 = genotypes.elementAt(random.nextInt(maxParentIndex));
						Genotype child = Genotype.crossover(parent1, parent2);
						if(random.nextDouble() < mutationProbability){
							child.mutate();
						}
						genotypes.remove(replacementIndex);
						genotypes.add(replacementIndex, child);
						replacementIndex--;
					}
					*/
					// Cloning for now
					int endOfElites = 10;
					int replacementIndex = genotypes.size() - 1;
					for(int i = 0; i < endOfElites; i++) {
						Genotype clone = genotypes.get(i).clone();
						if(random.nextDouble() < mutationProbability){
							double mutationStdDev = 0; 
							if(captureCount/((double)((trialsPerGeneration * evaluationsPerTrial))) < 0.9) {
								mutationStdDev = earlyMutationStdDev; 
							} else {
								mutationStdDev = lateMutationStdDev; 
							}
							clone.mutate(mutationStdDev);
						}
						genotypes.remove(replacementIndex);
						genotypes.add(replacementIndex, clone);
						replacementIndex--;
					}
					
				}
			}//replacement
			
			// Construct the 3 fittest predators for the 9 instance test.
			Vector<Piece> testPredatorPieces = new Vector<Piece>();
			for (int pred = 0; pred < numPredators; pred++) {
				Vector<Genotype> hiddenNodes = new Vector<Genotype>();
				for (int node = 0; node < numHiddenNodes; node++) {
					hiddenNodes
							.add(agentPopulations.elementAt(pred)
									.getSubPopulationForNode(node)
									.getFittestGenotype());
				}
			
				ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(
						hiddenNodes);
				ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(
						boardSize, ann);
				testPredatorPieces
						.add(new Piece(5, 5, false, env, annBehaviour));
			}
			// Run the 9 instance test.
			int testCaptureCount = 0;
			int preyPlacementIncrememnt = (int)Math.floor(boardSize/((double)3));
			for (int preyRow = 0; preyRow < 3; preyRow++) {
				for (int preyCol = 0; preyCol < 3; preyCol++) {
					Vector<Piece> testPreyPieces = new Vector<Piece>();
					testPreyPieces.add(new Piece(preyRow * preyPlacementIncrememnt, 
							preyCol * preyPlacementIncrememnt, true, env, runAway));
					env.setPieces(testPredatorPieces, testPreyPieces);
					SimulationResult result = env.run(false, false);
					testCaptureCount += result.preyCaught;	
				}
				
			}
						
			System.out.println("Generation "+gen+" done: "+captureCount + " captures, " + testCaptureCount + "/9 test score.");
			
			// Migration	
			if (gen % 3 == 0) {
				double[][] similarities = SocialEntropyBehaviourMeasurement
						.measureSimularity(testPredatorPieces, boardSize, env);
				for (int i = 0; i < similarities.length; i++) {
					for (int j = 0; j < similarities[0].length; j++) {
						if (similarities[i][j] > 0.8 & gen > 15) {
							System.out.println("Migrating " + i + " to " + j);
							agentPopulations.get(i).sendMigrants(
									agentPopulations.get(j), 5);
						}
					}
				}
			}
			// If the improvement is stagnating, burst mutation is run.
			capturesForEachGeneration.add(captureCount);
			if (gen >= burstMutationTestLookBackDistance) {
				if((burstMutationTicker <= 0) &
						(capturesForEachGeneration.get(gen) < (capturesForEachGeneration.get(gen - burstMutationTestLookBackDistance)
								+ (int)(Math.ceil(burstMutationTestRatioOfPopDifference * trialsPerGeneration * evaluationsPerTrial))))) {
					System.out.println("Burst Mutation!!");
					for(ESPPopulation pop : agentPopulations) {
						if(captureCount/((double)((trialsPerGeneration * evaluationsPerTrial))) < 0.9) {
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
		}//generations
				
		// Compute the genotypal euclidean distances between the predators.
		/*
		double[] distances1 = agentPopulations.get(0).maxAndMinEuclideanDistance(agentPopulations.get(1));
		System.out.println("Distances 0 - 1: min - " + distances1[0] + " max - " + distances1[1]);
		double[] distances2 = agentPopulations.get(0).maxAndMinEuclideanDistance(agentPopulations.get(2));
		System.out.println("Distances 0 - 2: min - " + distances2[0] + " max - " + distances2[1]);
		double[] distances3 = agentPopulations.get(1).maxAndMinEuclideanDistance(agentPopulations.get(2));
		System.out.println("Distances 1 - 2: min - " + distances3[0] + " max - " + distances3[1]);
		*/
		
		// Create the fittest predators.
		int j = 0;
		Vector<Piece> fittestPredatorPieces = new Vector<Piece>();
		for(ESPPopulation agentPopulation : agentPopulations){
			Vector<Genotype> hiddenNodes = agentPopulation.getFittestGenotypeInEachSubPopulation();
			ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(hiddenNodes);
			ann.saveNetwork("PredatorBehaviour" + j);
			ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(boardSize, ann);
			fittestPredatorPieces.add(new Piece(5, 5, false, env, annBehaviour));
			j += 1;
		}
		
				
		// Run some evaluations on them
		int evaluationsToRun = 100;
		TrialResult result = trial(fittestPredatorPieces, preyPieces, env, evaluationsToRun);
		System.out.println("==Fittest Predators==");
		System.out.println("Capture Count: "+result.captureCount+"/"+evaluationsToRun*preyPieces.size());
		for(int i=0; i<numPredators; i++)
			System.out.println("Predator "+i+" average fitness:"+result.avgEvalFitnesses[i]);
		
		// Run the simulation with them to create a log file
		/*
		for (Piece prey : preyPieces)
			prey.setPosition(random.nextInt(boardSize), random.nextInt(boardSize));
		for(Piece predator : fittestPredatorPieces)
			predator.setPosition(5, 5);
		preyPieces.get(0).setPosition(preyX, preyY);
		env.setPieces(fittestPredatorPieces, preyPieces);
		env.run(true, false);
		*/
	}//main
	
	//Run a set of evaluations on the predators to get fitness values for the genotypes
	private static TrialResult trial(Vector<Piece> predatorPieces, Vector<Piece> preyPieces, Environment env, int evaluations){
		double[] avgEvalFitnesses = new double[numPredators];
		int captureCount = 0;
		Random random = new Random();
		for(int eval=0; eval<evaluations; eval++){
			// Randomize the prey position so that it is not the same as the previous evaluation.
			for (Piece prey : preyPieces)
				prey.setPosition(random.nextInt(boardSize), random.nextInt(boardSize));
			for(Piece predator : predatorPieces)
				predator.setPosition(5, 5);
			
			env.setPieces(predatorPieces, preyPieces);
			SimulationResult result = env.run(false, false);
			captureCount += result.preyCaught;
			for(int i = 0; i<numPredators; i++){
				if (result.preyCaught == 0) {
					//double fitness = boardSize - result.finalDistancesFromPrey.elementAt(i);
					double fitness = result.initialDistancesFromPrey.elementAt(i) - result.finalDistancesFromPrey.elementAt(i);
					avgEvalFitnesses[i] += fitness;
				} else {
					//avgEvalFitnesses[i] += 2 * boardSize;
					avgEvalFitnesses[i] += 2 * boardSize - result.finalDistancesFromPrey.elementAt(i);
				}
			}
		}
		
		for(int i=0; i<numPredators; i++)
			avgEvalFitnesses[i] = avgEvalFitnesses[i] / evaluationsPerTrial;
		
		return new TrialResult(avgEvalFitnesses, captureCount);
	}
	
	private static class TrialResult{
		public double[] avgEvalFitnesses;
		public int captureCount;
		
		public TrialResult(double[] avgEvalFitnesses, int captureCount) {
			this.avgEvalFitnesses = avgEvalFitnesses;
			this.captureCount = captureCount;
		}
	}
}
