import java.util.Vector;


public class InputNode extends Node {
	private double exteriorInput;
	
	public InputNode(int number, Vector<Node> children) {
		super(number, children);
		exteriorInput = 0;
	}
	
	public double calculateAndPassOnActivation() {
		for(Node child : children) {
			child.receiveInput(exteriorInput, number);
		}
		return inputs.get(0).doubleValue();
	}
	
	public void setExteriorInput(double input) {
		exteriorInput = input;
	}
}
