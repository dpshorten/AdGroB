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
}
