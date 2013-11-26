import java.util.Vector;


public class InputNode extends Node {
	private double exteriorInput;
	private Vector<HiddenNode> children;
	
	public InputNode(int number, Vector<HiddenNode> children) {
		super(number);
		this.children = children;
		exteriorInput = 0;
	}
	
	public double calculateAndPassOnActivation() {
		for(Node child : children) {
			child.receiveInput(exteriorInput, number);
		}
		return inputs.get(0).doubleValue();
	}
	
	public double calculateAndPassOnActivation(int in, int out) {
		for(Node child : children) {
			child.receiveInput(exteriorInput, number);
		}
		return inputs.get(0).doubleValue();
	}
	
	public void setExteriorInput(double input) {
		// NEATEN THIS
		exteriorInput = input/((double)100);
	}
}
