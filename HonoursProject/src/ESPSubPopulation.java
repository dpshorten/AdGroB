import java.util.Vector;


public class ESPSubPopulation {
	
	Vector<Genotype> nodeGenotypes;
	
	public ESPSubPopulation(int subPopulationSize){
		nodeGenotypes = new Vector<Genotype>();
		for(int i=0; i<subPopulationSize; i++)
			nodeGenotypes.add(new Genotype());
	}
	
	public Genotype getGenotype(int index){
		return nodeGenotypes.elementAt(index);
	}
	
	public Vector<Genotype> getAllGenotypes(){
		return nodeGenotypes;
	}
	
	public Genotype getFittestGenotype() {
		Genotype fittest = nodeGenotypes.elementAt(0);
		for (Genotype genotype : nodeGenotypes) {
			if(fittest.compareTo(genotype) < 1) {
				fittest = genotype;
			}
		}
		return fittest;
	}
	
	public void runBurstMutation(double mutationAmountStdDev) {
		int populationSize = nodeGenotypes.size();
		Genotype fittest = this.getFittestGenotype();
		nodeGenotypes.clear();
		nodeGenotypes.add(fittest);
		for(int i = 0; i < populationSize - 1; i++) {
			nodeGenotypes.add(fittest.burstMutate(mutationAmountStdDev));
		}
		//System.out.println(nodeGenotypes.elementAt(0).getInputWeights().elementAt(0) 
			//	+ "   " + nodeGenotypes.elementAt(1).getInputWeights().elementAt(0));
	}
}
