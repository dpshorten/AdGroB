import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;


public class ESPArtificialNeuralNetwork {
	private final int NUMBERINPUTNODES = 2;
	private final int NUMBEROUTPUTNODES = 5;
	private Vector<InputNode> inputNodes;
	private Vector<HiddenNode> hiddenNodes;
	private Vector<OutputNode> outputNodes;
	
	public ESPArtificialNeuralNetwork (Vector<Genotype> hiddenNodeGenotypes) {
		
		outputNodes = new Vector<OutputNode>();
		for(int i = 0; i < NUMBEROUTPUTNODES; i++) {
			outputNodes.add(new OutputNode(i, null));
		}
		
		hiddenNodes = new Vector<HiddenNode>();
		for(int i = 0; i < hiddenNodeGenotypes.size(); i++) {
			hiddenNodes.add(new HiddenNode(i, outputNodes, hiddenNodeGenotypes.get(i)));
		}
		
		inputNodes = new Vector<InputNode>();
		for(int i = 0; i < NUMBERINPUTNODES; i++) {
			inputNodes.add(new InputNode(i, hiddenNodes));
		}
	}
	
	public double[] run(double xOffset, double yOffset) {
		inputNodes.get(0).setExteriorInput(xOffset);
		inputNodes.get(1).setExteriorInput(yOffset);
		for(Node node : inputNodes) {
			node.calculateAndPassOnActivation();
		}
		for(Node node : hiddenNodes) {
			node.calculateAndPassOnActivation();
		}
		int i = 0;
		double[] activations = new double[5];
		for(Node node : outputNodes) {
			activations[i] = node.calculateAndPassOnActivation();
			i += 1;
		}
		return activations;
	}

	public void saveNetwork(String fileName) {
		PrintWriter out = null;
		// Clear the file of any existing data.
		try {
			out = new PrintWriter(new FileWriter(fileName, false));
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
		out.write("");
		out.close();
		// Now instantiate out in appending mode.
		try {
			out = new PrintWriter(new FileWriter(fileName, true));
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
		
		for (HiddenNode hiddenNode : this.hiddenNodes) {
			out.write(hiddenNode.toString());
			out.write("\n");
		}
	}
}
