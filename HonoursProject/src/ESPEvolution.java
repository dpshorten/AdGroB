import java.util.Vector;
import java.util.Random;

public class ESPEvolution {
	static final int numHiddenNodes = 10;
	static final int numPredators = 3;
	static final int subPopulationSize = 100;
	static final int trialsPerGeneration = 1000;
	static final int evaluationsPerTrial = 6;
	static final int generations = 100;
	static final int boardSize = 5;
	
	static Vector<ESPPopulation> agentPopulations;
	
	public static void main(String[] args){
		
		//Initialise a population of genotypes for each predator
		for(int i=0; i<numPredators; i++)
			agentPopulations.add(new ESPPopulation(numHiddenNodes, subPopulationSize));
		
		Random random = new Random();
		Environment env = new Environment(boardSize);
		
		for(int gen=0; gen<generations; gen++){
			
			//For each generation, a number of trials are run
			for(int trial=0; trial<trialsPerGeneration; trial++){
				
				Vector<Piece> predatorPieces = new Vector<Piece>();
				
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
					predatorPieces.add(new Piece(0, 0, false, env, annBehaviour));
				}
				
				//Run a set of evaluations on the predators to get fitness values for the genotypes
			}
		}
	}
}
