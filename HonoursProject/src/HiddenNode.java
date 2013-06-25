import java.util.Vector;


public class HiddenNode extends Node{	
	private Vector<Double> inputWeights;
	private Vector<Double> outputWeights;
	
	public HiddenNode(int number, Vector<Node> children) {
		super(number, children);
		inputWeights = new Vector<Double>();
		outputWeights = new Vector<Double>();
	}
	
	public HiddenNode(int number, Vector<Node> children, Genotype genotype) {
		super(number, children);
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
			children.get(i).receiveInput(sigma * outputWeights.get(i), number);
		}
		return sigma;
	}
}
