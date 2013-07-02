import java.util.Collections;
import java.util.Vector;
import java.util.Random;
import org.python.util.PythonInterpreter;

public class ESPEvolution {
	static final int numHiddenNodes = 10;
	static final int numPredators = 3;
	static final int subPopulationSize = 100;
	static final int trialsPerGeneration = 100; //1000
	static final int evaluationsPerTrial = 1; //6
	static final int generations = 100;
	static final int boardSize = 20;
	static final double mutationProbability = 0.4;
	
	static Vector<ESPPopulation> agentPopulations = new Vector<ESPPopulation>();
	
	public static void main(String[] args){
		
		//Initialise a population of genotypes for each predator
		for(int i=0; i<numPredators; i++)
			agentPopulations.add(new ESPPopulation(numHiddenNodes, subPopulationSize));
		
		Random random = new Random();
		Environment env = new Environment(boardSize);
		
		Vector<Piece> preyPieces = new Vector<Piece>();
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 10000);
		int preyX = random.nextInt(boardSize);
		int preyY = random.nextInt(boardSize);
		preyPieces.add(new Piece(preyX,preyY,true,env,runAway));
		
		for(int gen=0; gen<generations; gen++){
			
			System.out.println("Generation: "+gen+" beginning trials");
			int captureCount = 0;
			//For each generation, a number of trials are run to get fitness values for the genotypes
			for(int trial=0; trial<trialsPerGeneration; trial++){
				
				Vector<Piece> predatorPieces = new Vector<Piece>();
				//usedGenotypes will contain a vector of active genotypes for each predator
				Vector<Vector<Genotype>> usedGenotypes = new Vector<Vector<Genotype>>();
				
				//Build an ANN for each predator using a randomly choosen node from each subpopulation
				for(int pred=0; pred<numPredators; pred++){
					Vector<Genotype> hiddenNodes = new Vector<Genotype>();
					for(int node=0; node<numHiddenNodes; node++){
						//Get an integer in the range [0,99]
						int rand = random.nextInt(subPopulationSize);
						hiddenNodes.add(agentPopulations.elementAt(pred).getSubPopulationForNode(node).getGenotype(rand));
					}
					
					ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(hiddenNodes);
					ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(boardSize, ann);
					predatorPieces.add(new Piece(10, 10, false, env, annBehaviour));
					usedGenotypes.add(hiddenNodes);
				}
				
				//Run a set of evaluations on the predators to get fitness values for the genotypes
				double[] avgEvalFitnesses = new double[numPredators];
				for(int eval=0; eval<evaluationsPerTrial; eval++){
					// Reset the prey position so that it is not the same as the previous evaluation.
					for (Piece prey : preyPieces) {
						prey.setPosition(preyX, preyY);
					}
					env.setPieces(predatorPieces, preyPieces);
					SimulationResult result = env.run(false);
					captureCount += result.preyCaught;
					for(int i = 0; i<numPredators; i++){
						double fitness = boardSize - result.distancesFromPrey.elementAt(i);
						avgEvalFitnesses[i] += fitness;
					}
				}
				//Update the genotypes fitnesses with the average fitness over the evaluations
				for(int i = 0; i<numPredators; i++){
					avgEvalFitnesses[i] = avgEvalFitnesses[i] / evaluationsPerTrial;
					for(Genotype genotype : usedGenotypes.elementAt(i))
						genotype.updateFitness(avgEvalFitnesses[i]);
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
				}
			}//replacement
			
			System.out.println("Generation: "+gen+" done: "+captureCount + " captures, ");
		}//generations
				
		// Create the fittest predators.
		Vector<Piece>fittestPredatorPieces = new Vector<Piece>();
		for(ESPPopulation agentPopulation : agentPopulations){
			Vector<Genotype> hiddenNodes = agentPopulation.getFittestGenotypeInEachSubPopulation();
			ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(hiddenNodes);
			ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(boardSize, ann);
			fittestPredatorPieces.add(new Piece(5, 5, false, env, annBehaviour));
		}
		// Run the simulation with them.
		System.out.println(fittestPredatorPieces.size());
		env.setPieces(fittestPredatorPieces, preyPieces);
		env.run(true);
		// Run the visualisation
		
	}//main
}
