import java.util.Vector;


public class ESPPopulation {

	Vector<ESPSubPopulation> subPopulations;
	
	public ESPPopulation(int numSubPopulations, int subPopulationSize){
		for(int i=0;i<numSubPopulations;i++)
			subPopulations.add(new ESPSubPopulation(subPopulationSize));
	}
	
	public ESPSubPopulation getSubPopulationForHiddenNode(int index){
		return subPopulations.elementAt(index);
	}
}
