import java.util.Vector;
import java.util.Collections;

public class ESPSubPopulation {
	
	public static final double migrationSimilarityCutOff = 0.8;
	
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
	
	public double averageWeightDistance(ESPSubPopulation otherSubPop){
		
		//Get a random sample from each subpopulation with no repeated elements
		Vector<Genotype> thisCopy = new Vector<Genotype>();
		thisCopy.addAll(nodeGenotypes);
		Vector<Genotype> otherCopy = new Vector<Genotype>();
		otherCopy.addAll(otherSubPop.nodeGenotypes);
		Vector<Genotype> thisSample = new Vector<Genotype>();
		Vector<Genotype> otherSample = new Vector<Genotype>();
		
		int randomSampleSize = nodeGenotypes.size()/4;
		
		for(int i=0; i<randomSampleSize; i++){
			int index = (int) Math.round(Math.random()*(thisCopy.size()-1));
			thisSample.add(thisCopy.elementAt(index));
			otherSample.add(otherCopy.elementAt(index));
			thisCopy.remove(index);
			otherCopy.remove(index);
		}
		
		//Get total distance between each pair of genotypes
		double distanceSum = 0;
		for(Genotype t : thisSample){
			for(Genotype o : otherSample){
				distanceSum += t.averageWeightDistance(o);
			}
		}
		
		//Reduce the sum to the average distance between pairs
		distanceSum = distanceSum / (thisSample.size()*otherSample.size());
		
		return distanceSum;
	}
	
//	public boolean sendMigrants(ESPSubPopulation otherSubPop, int numMigrants) {
//		Collections.sort(this.nodeGenotypes);
//		Collections.reverse(this.nodeGenotypes);
//		Collections.sort(otherSubPop.nodeGenotypes);
//		Collections.reverse(otherSubPop.nodeGenotypes);
//		double distanceSum = 0;
//		for(int i = 0; i < 5; i++) {
//			distanceSum += this.nodeGenotypes.get(i).euclideanDistanceSquared(
//					otherSubPop.nodeGenotypes.get(i));
//		}
//		distanceSum /= 5;
//		if(distanceSum < migrationSimilarityCutOff) {
//			for(int i = 0; i < numMigrants; i++) {
//				otherSubPop.acceptMigrant(this.nodeGenotypes.get(i));
//			}
//			return true;
//		} else {
//			return false;
//		}
//	}
	
	//Send numMigrants random migrants to the other subpopulation
	public void sendMigrants(ESPSubPopulation otherSubPop, int numMigrants){
		for(int i = 0; i < numMigrants; i++) {
			otherSubPop.acceptMigrant(getGenotype((int)Math.round(Math.random()*(nodeGenotypes.size()-1))));
		}
	}
	
	// NB: assumes that nodeGenotypes is sorted in ascending order.
	public void acceptMigrant(Genotype Migrant) {
		Collections.sort(nodeGenotypes);
		Collections.reverse(nodeGenotypes);
		this.nodeGenotypes.remove(this.nodeGenotypes.size() - 1);
		this.nodeGenotypes.add(0, Migrant);
	}
}
