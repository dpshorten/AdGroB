import java.util.Vector;


public class InputNode extends Node {
	public InputNode(int number, Vector<Node> children, Vector<Double> inputs) {
		super(number, children, inputs);
	}
	
	public double calculateAndPassOnActivation() {
		for(Node child : children) {
			child.receiveInput(inputs.get(0).doubleValue(), number);
		}
		return inputs.get(0).doubleValue();
	}
}
