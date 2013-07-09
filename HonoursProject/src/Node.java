import java.util.Vector;


public abstract class Node {
	protected final double BETA = 1.0;
	protected int number;
	protected Vector<Double> inputs;
	
	public Node(int number) {
		this.number = number;
		this.inputs = new Vector<Double>();
		for(int i = 0; i < 10; i++) {
			inputs.add(new Double(0));
		}
	}
	
	protected double sigmoid(double t) {
		return 1/(1 + Math.pow(Math.E, - BETA * t ));
	}
	
	public void receiveInput(double input, int position) {
		inputs.setElementAt(new Double(input), position);
	}
	
	public abstract double calculateAndPassOnActivation();
}
