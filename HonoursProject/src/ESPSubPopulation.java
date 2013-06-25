import java.util.Vector;


public class ESPSubPopulation {
	
	Vector<Genotype> nodeGenotypes;
	
	public ESPSubPopulation(int subPopulationSize){
		for(int i=0;i<subPopulationSize;i++)
			nodeGenotypes.add(new Genotype());
	}
}
