import java.util.Collections;
import java.util.Vector;
import java.util.Random;

public class ESPEvolution {
	static final int numHiddenNodes = 10;
	static final int numPredators = 3;
	static final int subPopulationSize = 100;
	static final int trialsPerGeneration = 100; //1000
	static final int evaluationsPerTrial = 1; //6
	static final int generations = 1000;
	static final int boardSize = 40;
	static final double mutationProbability = 0.4;
	
	static Vector<ESPPopulation> agentPopulations = new Vector<ESPPopulation>();
	
	public static void main(String[] args){
		
		//Initialise a population of genotypes for each predator
		for(int i=0; i<numPredators; i++)
			agentPopulations.add(new ESPPopulation(numHiddenNodes, subPopulationSize));
		
		Random random = new Random();
		Environment env = new Environment(boardSize);
		
		Vector<Piece> preyPieces = new Vector<Piece>();
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 1);
		int preyX = random.nextInt(boardSize);
		int preyY = random.nextInt(boardSize);
		preyPieces.add(new Piece(preyX,preyY,true,env,runAway));
		
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
							clone.mutate();
						}
						genotypes.remove(replacementIndex);
						genotypes.add(replacementIndex, clone);
						replacementIndex--;
					}
					
				}
			}//replacement
			
			System.out.println("Generation: "+gen+" done: "+captureCount + " captures");
		}//generations
				
		// Create the fittest predators.
		Vector<Piece>fittestPredatorPieces = new Vector<Piece>();
		for(ESPPopulation agentPopulation : agentPopulations){
			Vector<Genotype> hiddenNodes = agentPopulation.getFittestGenotypeInEachSubPopulation();
			ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(hiddenNodes);
			ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(boardSize, ann);
			fittestPredatorPieces.add(new Piece(5, 5, false, env, annBehaviour));
		}
		
		// Run some evaluations on them
		int evaluationsToRun = 100;
		TrialResult result = trial(fittestPredatorPieces, preyPieces, env, evaluationsToRun);
		System.out.println("==Fittest Predators==");
		System.out.println("Capture Count: "+result.captureCount+"/"+evaluationsToRun*preyPieces.size());
		for(int i=0; i<numPredators; i++)
			System.out.println("Predator "+i+" average fitness:"+result.avgEvalFitnesses[i]);
		
		// Run the simulation with them to create a log file
		env.setPieces(fittestPredatorPieces, preyPieces);
		env.run(true);
		
	}//main
	
	//Run a set of evaluations on the predators to get fitness values for the genotypes
	private static TrialResult trial(Vector<Piece> predatorPieces, Vector<Piece> preyPieces, Environment env, int evaluations){
		double[] avgEvalFitnesses = new double[numPredators];
		int captureCount = 0;
		Random random = new Random();
		for(int eval=0; eval<evaluations; eval++){
			// Randomize the prey position so that it is not the same as the previous evaluation.
			for (Piece prey : preyPieces) {
				prey.setPosition(random.nextInt(boardSize), random.nextInt(boardSize));
			}
			env.setPieces(predatorPieces, preyPieces);
			SimulationResult result = env.run(false);
			captureCount += result.preyCaught;
			for(int i = 0; i<numPredators; i++){
				if (result.preyCaught == 0) {
					double fitness = boardSize - result.distancesFromPrey.elementAt(i);
					avgEvalFitnesses[i] += fitness;
				} else {
					avgEvalFitnesses[i] += 1.5 * boardSize;
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
