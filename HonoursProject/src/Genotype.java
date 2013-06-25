import java.util.Random;
import java.util.Vector;

public class Genotype 
{
	/*
	 * Input weights are:
	 * 0 - x prey offset
	 * 1 - y prey offset
	 * Output weights are:
	 * 0 - North
	 * 1 - West
	 * 2 - East
	 * 3 - South
	 * 4 - Stay
	 */
	
	private Vector<Double> inputWeights;
	private Vector<Double> outputWeights;
	private Double activationThreshold;
	
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
		activationThreshold = new Double(random.nextDouble() - 0.5);
	}
	
	public void mutate() {
		//stub
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
	
	public Double getActivation() {
		return new Double(activationThreshold);
	}
}
