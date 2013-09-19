import java.util.Vector;


public class OutputNode extends Node {
	public OutputNode(int number, Vector<Node> children) {
		super(number);
	}
	
	public double calculateAndPassOnActivation() {
		double t = 0;
		for(Double input : inputs) {
			t += input.doubleValue();
		}
		return sigmoid(t);
	}
	
	public double calculateAndPassOnActivation(int in , int out) {
		double t = 0;
		for(Double input : inputs) {
			t += input.doubleValue();
		}
		return sigmoid(t);
	}
}
