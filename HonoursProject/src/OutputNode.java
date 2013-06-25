import java.util.Vector;


public class OutputNode extends Node {
	public OutputNode(int number, Vector<Node> children, Vector<Double> inputs) {
		super(number, children, inputs);
	}
	
	public double calculateAndPassOnActivation() {
		double t = 0;
		for(Double input : inputs) {
			t += input.doubleValue();
		}
		return sigmoid(t);
	}
}
