import java.util.Vector;
import java.util.ArrayList;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;


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
	
	public Vector<Genotype> getFittestGenotypeInEachSubPopulation() {
		Vector<Genotype> fittestGenotypes = new Vector<Genotype>();
		for(ESPSubPopulation subPopulation : subPopulations) {
			fittestGenotypes.add(subPopulation.getFittestGenotype());
		}
		return fittestGenotypes;
	}
	
	public double[] maxAndMinEuclideanDistance(ESPPopulation otherPopulation) {
		double minDistance = 10000;
		double maxDistance = 0;
		
		ICombinatoricsVector<Integer> startingVector = Factory.createVector(new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		Generator<Integer> generator = Factory.createPermutationGenerator(startingVector);
		// For each permutation of the ordering of the sub-populations of the other population we sum the distances between the 
		// first genotypes of each corresponding sub-population.
		for (ICombinatoricsVector<Integer> permutation : generator) {
			double distanceSquared = 0;
			ArrayList<Integer> permutationVector = (ArrayList<Integer>) permutation.getVector(); 
			for(int i = 0; i < permutationVector.size(); i++) {
				// The first genotype is chosen as using the fittest is too computationally expensive. 
				distanceSquared += subPopulations.elementAt(i).getGenotype(0).euclideanDistanceSquared(
						otherPopulation.getSubPopulationForNode(permutationVector.get(i).intValue()).getGenotype(0));
			}
			if(distanceSquared > maxDistance) {
				maxDistance = distanceSquared;
			} 
			if (distanceSquared < minDistance) {
				minDistance = distanceSquared;
			}
		}
				
		double [] toReturn = {Math.sqrt(minDistance), Math.sqrt(maxDistance)};
		return toReturn;
	}
	
	// Exploratory method for printing out how similar subpops are to one another.
	public void printDistances(ESPPopulation otherPopulation) {
		int i = 0;
		for(ESPSubPopulation thisSubPop : subPopulations) {
			System.out.println("This subpop " + i);
			for(ESPSubPopulation otherSubPop : otherPopulation.subPopulations) {
				System.out.println(thisSubPop.getFittestGenotype().
							euclideanDistanceSquared(otherSubPop.getFittestGenotype()));
			}
			i++;
		}
	}
	
	public void runBurstMutation(double mutationAmountStdDev) {
		for(ESPSubPopulation subPop : subPopulations) {
			subPop.runBurstMutation(mutationAmountStdDev);
		}
	}
	
	public void sendMigrants(ESPPopulation otherPopulation, int numMigrantsPerSubPop) {
		int numMigrationsDone = 0;
		for(ESPSubPopulation subPop : this.subPopulations) {
			for(ESPSubPopulation otherSubPop : otherPopulation.subPopulations) {
				if(subPop.sendMigrantsUsingAvgWeightsDistance(otherSubPop, numMigrantsPerSubPop)) {
					numMigrationsDone++;
				}
			}
		}
		System.out.println(numMigrationsDone + " migrations done");
	}
}