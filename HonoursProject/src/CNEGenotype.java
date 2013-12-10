import java.util.Random;
import java.util.Vector;


public class CNEGenotype implements Comparable<CNEGenotype>{
	private Vector<Double> weights;
	private double fitness;

	// Makes a random Genotype with weights between -0.5 and 0.5.
	public CNEGenotype() {
		weights = new Vector<Double>();
		Random random = new Random();
		for (int i = 0; i < 70 * EvolutionParameters.numPredators; i++) {
			weights.add(new Double(cauchy(1, random.nextDouble())));
		}
	}

	public void mutate() {
		for(int i = 0; i < EvolutionParameters.CNEweightMutationsPerPredator * EvolutionParameters.numPredators; i++) {
			Random random = new Random();
			int indexToMutate = random.nextInt(weights.size());
			double mutationAmount = cauchy(EvolutionParameters.CNEGamma, random.nextGaussian());
				weights.set(indexToMutate, weights.get(indexToMutate) + mutationAmount);
		}
	}
	
	public static CNEGenotype crossover(CNEGenotype father, CNEGenotype mother) {
		Random random = new Random();
		CNEGenotype child = new CNEGenotype();
		Vector<Double> childWeights = new Vector<Double>();
		for(int i = 0; i < mother.getWeights().size(); i++) {
			if(random.nextBoolean()) {
				childWeights.add(mother.getWeights().get(i));
			} else {
				childWeights.add(father.getWeights().get(i));
			}
		}
		child.setWeights(childWeights);
		return child;
	}
	
	private static double cauchy(double gamma, double random) {
        return gamma*Math.tan(Math.PI * (random - 0.5));
    }
	
	public Vector<Double> getWeights() {
		return new Vector<Double>(weights);
	}
	
	public void setWeights(Vector<Double> weights) {
		this.weights = new Vector<Double>(weights);
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public int compareTo(CNEGenotype otherCNEGenotype) {
		if(this.fitness > otherCNEGenotype.getFitness()) {
			return 1;
		} else if (this.fitness == otherCNEGenotype.getFitness()) {
			return 0;
		} else {
			return -1;
		}
	}
	
	public void resetFitness() {
		fitness = 0;
	}
}
