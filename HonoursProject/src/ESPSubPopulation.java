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
}
