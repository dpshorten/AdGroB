import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

public class ESPArtificialNeuralNetwork {
	private final int NUMBERINPUTNODES = 2;
	private final int NUMBEROUTPUTNODES = 5;
	private Vector<InputNode> inputNodes;
	private Vector<HiddenNode> hiddenNodes;
	private Vector<OutputNode> outputNodes;

	public ESPArtificialNeuralNetwork(Vector<Genotype> hiddenNodeGenotypes) {
		outputNodes = new Vector<OutputNode>();
		hiddenNodes = new Vector<HiddenNode>();
		inputNodes = new Vector<InputNode>();
		this.AddNodesFromGenotypes(hiddenNodeGenotypes);
	}

	public ESPArtificialNeuralNetwork(String fileName) {
		outputNodes = new Vector<OutputNode>();
		hiddenNodes = new Vector<HiddenNode>();
		inputNodes = new Vector<InputNode>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fileName));
		} catch(Exception e) {
			System.out.println("Error : " + e);
		}
		Vector<Genotype> hiddenNodeGenotypes = new Vector<Genotype>();
		String line;
		try{
			while((line = in.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				Vector<Double> inputWeights = new Vector<Double>();
				while(tokenizer.hasMoreTokens()) {
					inputWeights.add(new Double(Double.parseDouble(tokenizer.nextToken())));
				}
				line = in.readLine();
				tokenizer = new StringTokenizer(line, ",");
				Vector<Double> outputWeights = new Vector<Double>();
				while(tokenizer.hasMoreTokens()) {
					outputWeights.add(new Double(Double.parseDouble(tokenizer.nextToken())));
				}
				hiddenNodeGenotypes.add(new Genotype(inputWeights, outputWeights));
			}
		} catch(Exception e) {
			System.out.println("Error : " + e);
		}
		this.AddNodesFromGenotypes(hiddenNodeGenotypes);
	}
	
	private void AddNodesFromGenotypes(Vector<Genotype> hiddenNodeGenotypes) {
		for (int i = 0; i < NUMBEROUTPUTNODES; i++) {
			outputNodes.add(new OutputNode(i, null));
		}
		
		for (int i = 0; i < hiddenNodeGenotypes.size(); i++) {
			hiddenNodes.add(new HiddenNode(i, outputNodes, hiddenNodeGenotypes
					.get(i)));
		}
		
		for (int i = 0; i < NUMBERINPUTNODES; i++) {
			inputNodes.add(new InputNode(i, hiddenNodes));
		}
	}

	public double[] run(double xOffset, double yOffset) {
		inputNodes.get(0).setExteriorInput(xOffset);
		inputNodes.get(1).setExteriorInput(yOffset);
		for (Node node : inputNodes) {
			node.calculateAndPassOnActivation();
		}
		for (Node node : hiddenNodes) {
			node.calculateAndPassOnActivation();
		}
		int i = 0;
		double[] activations = new double[5];
		for (Node node : outputNodes) {
			activations[i] = node.calculateAndPassOnActivation();
			i += 1;
		}
		return activations;
	}

	public void saveNetwork(String fileName) {
		File outputFile = new File(fileName);
		PrintWriter out = null;
		// Clear the file of any existing data.
		try {
			out = new PrintWriter(new FileWriter(outputFile, false));
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
		out.write("");
		out.close();
		// Now instantiate out in appending mode.
		try {
			out = new PrintWriter(new FileWriter(outputFile, true));
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
		// Write the data.
		for (HiddenNode hiddenNode : this.hiddenNodes) {
			out.write(hiddenNode.toString());
			out.write("\n");
		}

		out.close();
	}
}
