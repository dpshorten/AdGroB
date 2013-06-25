import java.util.Vector;


public abstract class Node {
	protected final double BETA = 1.0;
	protected int number;
	protected Vector<Node> children;
	protected Vector<Double> inputs;
	
	public Node(int number, Vector<Node> children, Vector<Double> inputs) {
		this.number = number;
		this.children = children;
		this.inputs = new Vector<Double>(10);
	}
	
	protected double sigmoid(double t) {
		return 1/(1 + Math.pow(Math.E, - BETA * t ));
	}
	
	public void receiveInput(double input, int position) {
		inputs.setElementAt(new Double(input), position);
	}
	
	public abstract double calculateAndPassOnActivation();
}
