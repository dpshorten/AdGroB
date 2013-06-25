import java.util.Vector;


public class ESPPopulation {

	Vector<ESPSubPopulation> subPopulations;
	
	public ESPPopulation(int numSubPopulations, int subPopulationSize){
		subPopulations = new Vector<ESPSubPopulation>();
		for(int i=0; i<numSubPopulations; i++)
			subPopulations.add(new ESPSubPopulation(subPopulationSize));
	}
	
	public ESPSubPopulation getSubPopulationForNode(int index){
		return subPopulations.elementAt(index);
	}
}
