import java.util.Random;
import java.util.Vector;

public class Genotype 
{
	Vector<Double> inputWeights;
	Vector<Double> outputWeights;
	
	public Genotype() {
		inputWeights = new Vector<Double>();
		outputWeights = new Vector<Double>();
	}
	
	public void mutate() {
		//stub
	}
	
	public static Genotype crossover(Genotype geno1, Genotype geno2) {
		//stub
		return new Genotype();
	}
	
	public Vector<Double> getInputWeights() {
		return (Vector<Double>) inputWeights.clone();
	}
	
	public Vector<Double> getOutputWeights() {
		return (Vector<Double>) outputWeights.clone();
	}
}
