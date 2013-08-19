import java.util.Vector;


public class HiddenNode extends Node{	
	private Vector<Double> inputWeights;
	private Vector<Double> outputWeights;
	private Vector<OutputNode> children;
	
	public HiddenNode(int number, Vector<OutputNode> children) {
		super(number);
		this.children = children;
		inputWeights = new Vector<Double>();
		outputWeights = new Vector<Double>();
	}
	
	public HiddenNode(int number, Vector<OutputNode> children, Genotype genotype) {
		super(number);
		this.children = children;
		inputWeights = genotype.getInputWeights();
		outputWeights = genotype.getOutputWeights();
	}
	
	public double calculateAndPassOnActivation() {
		double t = 0;
		for(int i = 0; i < inputWeights.size(); i++){
			t += inputWeights.get(i) * inputs.get(i);
		}
		double sigma = sigmoid(t);
		for(int i = 0; i < outputWeights.size(); i++) {
			if(number < 10)
			children.get(i).receiveInput(sigma * outputWeights.get(i), number);
		}
		return sigma;
	}
	
	public String toString() {
		String toReturn = new String();
		for(Double inputWeight : inputWeights) {
			toReturn += inputWeight;
			toReturn += ",";
		}
		toReturn += "\n";
		for(Double outputWeight : outputWeights) {
			toReturn += outputWeight;
			toReturn += ",";
		}
		return toReturn;
	}
}
