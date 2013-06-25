import java.util.Vector;


public class ESPEvolution {
	static final int numHiddenNodes = 10;
	static final int numPredators = 3;
	static final int subPopulationSize = 100;
	
	static Vector<ESPPopulation> agentPopulations;
	
	public static void main(String[] args){
		
		//Initialise a population of genotypes for each predator
		for(int i=0;i<numPredators;i++)
			agentPopulations.add(new ESPPopulation(numHiddenNodes, subPopulationSize));
		
		//Run trials to determine fitness of genotypes
		
	}
}
