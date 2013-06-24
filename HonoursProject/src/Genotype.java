import java.util.Vector;

public class Genotype 
{
	Vector<Double> inputWeights;
	Vector<Double> outputWeights;
	
	public Genotype() {
		inputWeights = new Vector<Double>();
		outputWeights = new Vector<Double>();
	}
	
	public static Genotype makeRandomGenotype() {
		//stub
		return new Genotype();
	}
	
	public void mutate() {
		//stub
	}
	
	public static Genotype crossover(Genotype geno1, Genotype geno2) {
		//stub
		return new Genotype();
	}
}
