import java.util.Random;
import java.util.Vector;

public class Genotype implements Comparable<Genotype>
{
	/*
	 * Inputs are:
	 * 0 - x prey offset
	 * 1 - y prey offset
	 * Outputs are:
	 * 0 - North
	 * 1 - East
	 * 2 - South
	 * 3 - West
	 * 4 - Stay
	 */
	private final int WEIGHT_MUTATIONS_PER_GENOTYPE_MUTATION = 1;
	private final double MUTATION_AMOUNT_STDEV = 0.1;
	private Vector<Double> inputWeights;
	private Vector<Double> outputWeights;
	private double fitness = 0;
	private int fitnessSourceCount = 0;
	
	// Makes a random Genotype with weights between -0.5 and 0.5.
	public Genotype() {
		inputWeights = new Vector<Double>();
		outputWeights = new Vector<Double>();
		Random random = new Random();
		for(int i = 0; i < 2; i++) {
			inputWeights.add(new Double(random.nextDouble() - 0.5));
		}
		for(int i = 0; i < 5; i++) {
			outputWeights.add(new Double(random.nextDouble() - 0.5));
		}
	}
	
	// Int flag makes the the new genotype not have its weights set.
	// Saves a little computation in crossover.
	public Genotype(int a) {
		inputWeights = new Vector<Double>();
		outputWeights = new Vector<Double>();
	}
	
	public void mutate() {
		for(int i = 0; i < WEIGHT_MUTATIONS_PER_GENOTYPE_MUTATION; i++) {
			Random random = new Random();
			int indexToMutate = random.nextInt(inputWeights.size() + outputWeights.size());
			double mutationAmount = random.nextGaussian() * MUTATION_AMOUNT_STDEV; 
			if(indexToMutate < outputWeights.size()) {
				indexToMutate %= outputWeights.size();
				outputWeights.set(indexToMutate, outputWeights.get(indexToMutate).doubleValue() + mutationAmount);
			} else {
				indexToMutate %= inputWeights.size();
				inputWeights.set(indexToMutate, inputWeights.get(indexToMutate).doubleValue() + mutationAmount);
			}
		}
	}
	
	public static Genotype crossover(Genotype father, Genotype mother) {
		Random random = new Random();
		Genotype child = new Genotype(1);
		Vector<Double> childInputWeights = new Vector<Double>();
		Vector<Double> childOutputWeights = new Vector<Double>();
		for(int i = 0; i < mother.getInputWeights().size(); i++) {
			boolean useMother = random.nextBoolean();
			if(useMother) {
				childInputWeights.add(mother.getInputWeights().get(i));
			} else {
				childInputWeights.add(father.getInputWeights().get(i));
			}
		}
		for(int i = 0; i < mother.getOutputWeights().size(); i++) {
			boolean useMother = random.nextBoolean();
			if(useMother) {
				childOutputWeights.add(mother.getOutputWeights().get(i));
			} else {
				childOutputWeights.add(father.getOutputWeights().get(i));
			}
		}
		child.setInputWeights(childInputWeights);
		child.setOutputWeights(childOutputWeights);
		return child;
	}
	
	public Vector<Double> getInputWeights() {
		Vector<Double> copy = new Vector<Double>(inputWeights.size());
		for (Double element : inputWeights) {
			copy.add(new Double(element));
		}
		return copy;
	}
	
	public Vector<Double> getOutputWeights() {
		Vector<Double> copy = new Vector<Double>(outputWeights.size());
		for (Double element : outputWeights) {
			copy.add(new Double(element));
		}
		return copy;
	}
	
	public void setInputWeights(Vector<Double> newInputWeights) {
		inputWeights.removeAllElements();
		for(Double element : newInputWeights) {
			inputWeights.add(new Double(element));
		}
	}
	
	public void setOutputWeights(Vector<Double> newOutputWeights) {
		outputWeights.removeAllElements();
		for(Double element : newOutputWeights) {
			outputWeights.add(new Double(element));
		}
	}
	
	//Include a new fitness value in the average fitness of the genotype
	public void updateFitness(double newFitness){
		if(fitnessSourceCount == 0){
			fitness = newFitness;
			fitnessSourceCount++;
		}
		else{
			fitness = (fitness * fitnessSourceCount + newFitness) / (double) (fitnessSourceCount + 1);
			fitnessSourceCount++;
		}
	}
	
	@Override
	public int compareTo(Genotype other) {
		if(fitness > other.fitness)
			return 1;
		else if (fitness < other.fitness)
			return -1;
		else if (fitness == other.fitness)
			return 0;
		else{
			System.out.println("Something went wrong with the genotype comparisons! (Probably NaN related)");
			return 0;
		}
	}
	
	public Genotype clone() {
		Genotype clone = new Genotype();
		clone.setInputWeights(this.getInputWeights());
		clone.setOutputWeights(this.getOutputWeights());
		return clone;
	}
	
	public double euclideanDistanceSquared(Genotype otherGenotype) {
		double distanceSquared = 0;
		Vector<Double> otherInputWeights = otherGenotype.getInputWeights();
		Vector<Double> otherOutputWeights = otherGenotype.getOutputWeights();
		for(int i = 0; i < inputWeights.size(); i++) {
			distanceSquared += Math.pow(inputWeights.elementAt(i).doubleValue() - otherInputWeights.elementAt(i).doubleValue(), 2);
		}
		for(int i = 0; i < outputWeights.size(); i++) {
			distanceSquared += Math.pow(outputWeights.elementAt(i).doubleValue() - otherOutputWeights.elementAt(i).doubleValue(), 2);
		}
		return distanceSquared;
	}
}
