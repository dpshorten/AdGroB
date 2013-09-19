import java.util.Random;
import java.util.Vector;

public class SimpleGenotype implements Comparable<SimpleGenotype>
{
	double[] values;
	double fitness;
	int weights = 0;
	int inputNodes = 0;
	int outputNodes = 0;
	int fitnessCount = 0;
	
	Random random = new Random();
	
	public SimpleGenotype()
	{

	}
	
	public SimpleGenotype(Vector<Genotype> genotypes, int numWeights, int inn, int outn)
	{
		values = new double[genotypes.size()*numWeights];
		fitness = 0;
		weights = numWeights;
		inputNodes = inn;
		outputNodes = outn;
		
		for(int i = 0 ; i < genotypes.size(); i ++)
		{
			for(int j = 0 ; j < inputNodes ; j ++)
			{
				values[i*numWeights + j] = (genotypes.elementAt(i).getInputWeights().elementAt(j));
			}
			for (int k = 0 ; k < outputNodes ; k ++)
			{
				values[i*numWeights + inputNodes + k] = (genotypes.elementAt(i).getOutputWeights().elementAt(k));
			}
		}
	}
	
	public SimpleGenotype(SimpleGenotype genotypes)
	{
		this.values = genotypes.getValues();
		fitness = 0;
		weights = genotypes.getWeights();
		inputNodes = genotypes.getInputs();
		outputNodes = genotypes.getOutputs();
	}
	
	public SimpleGenotype(double[] vals , int inn, int out)
	{
		values = vals;
		inputNodes = inn;
		outputNodes = out;
		weights = inn+out;
		fitness = 0;
	}
	
	private int getOutputs() 
	{
		return outputNodes;
	}

	private int getInputs() 
	{
		return inputNodes;
	}

	public double[] getValues()
	{
		return values;
	}
	public int getWeights()
	{
		return weights;
	}
	
	public double getFitness()
	{
		return fitness;
	}
	
	public void mutate(double std)
	{
		
		int indexToMutate = random.nextInt(values.length);
		
		double mutation = random.nextDouble();
		if(mutation <= std)
		{
			values[indexToMutate] = (values[indexToMutate] + ((random.nextGaussian()-0.5) * std));
		}
	}
	
	public SimpleGenotype crossover(SimpleGenotype otherGenotype, double std)
	{
		SimpleGenotype child = new SimpleGenotype(otherGenotype);
		
		double crossover = random.nextDouble();
		for(int i = 0 ; i < values.length ; i ++)
		{
			double [] othervalues = otherGenotype.getValues();
			if(crossover <= std)
			{
				values[i] = othervalues[i];
			}
		}
		
		return child;
	}
	
	public void updateFitness(double newFitness)
	{
		fitness = ((fitness*fitnessCount) + newFitness) / (fitnessCount+1);
		fitnessCount++;
	}
	
	public Vector<Genotype> convertToGenotype()
	{
		Vector<Genotype> convertedGenotype = new Vector<Genotype>();
		
		Vector<Double> inputWeights = new Vector<Double>();
		Vector<Double> outputWeights = new Vector<Double>();
		
		
		for(int i = 0 ; i < values.length/weights ; i ++)
		{
			for(int j = 0 ; j < this.inputNodes ; j ++)
			{
				inputWeights.add(values[i*weights + j]);
			}
			for(int k = 0 ; k < this.outputNodes ; k ++)
			{
				outputWeights.add(values[i*weights + inputNodes + k]);
			}
			
			Genotype newGenotype = new Genotype(inputWeights, outputWeights);
			
			convertedGenotype.add(newGenotype);
		}
		
		return convertedGenotype;
	}
	
	public SimpleGenotype convertToSimpleGenotype(Vector<Genotype> genotypes)
	{
		int inputNodes = genotypes.elementAt(0).getInputWeights().size();
		int outputNodes = genotypes.elementAt(0).getOutputWeights().size();
		int numWeights = inputNodes+outputNodes;
		
		double[] values = new double[genotypes.size() * numWeights];
		
		
		
		for(int i = 0 ; i < genotypes.size(); i ++)
		{
			for(int j = 0 ; j < inputNodes ; j ++)
			{
				values[i*numWeights + j] = (genotypes.elementAt(i).getInputWeights().elementAt(j));
			}
			for (int k = 0 ; k < outputNodes ; k ++)
			{
				values[i*numWeights + inputNodes + k] = (genotypes.elementAt(i).getOutputWeights().elementAt(k));
			}
		}
		
		SimpleGenotype convertedGenotype = new SimpleGenotype(values, inputNodes, outputNodes);
		
		return convertedGenotype;
	}

	@Override
	public int compareTo(SimpleGenotype other) {
		if(getFitness() > other.getFitness())
			return 1;
		else if (getFitness() < other.getFitness())
			return -1;
		else if (getFitness() == other.getFitness())
			return 0;
		else{
			System.out.println("Something went wrong with the genotype comparisons! (Probably NaN related)");
			return 0;
		}
	}
	
	public String toString()
	{
		String toReturn = new String();
		System.out.println(values.length);
		System.out.println(values[weights*0]);
		System.out.println(values[weights*1]);
		for(int i = 0 ; i < values.length/weights ; i ++)
		{
			for(int j = 0 ; j < inputNodes ; j ++) {
				toReturn += values[i*weights + j];
				toReturn += ",";
			}
			toReturn += "\n";
			for(int j = 0 ; j < outputNodes ; j ++) {
				toReturn += values[i*weights + inputNodes + j];
				toReturn += ",";
			}
			toReturn += "\n";
		}
		return toReturn;
	}
}