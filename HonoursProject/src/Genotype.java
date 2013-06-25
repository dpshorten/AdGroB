import java.util.Random;
import java.util.Vector;

public class Genotype 
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
	
	public static Genotype crossover(Genotype geno1, Genotype geno2) {
		//stub
		return new Genotype();
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
}
