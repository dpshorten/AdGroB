import java.util.Vector;

public class PredatorGroup implements Comparable<PredatorGroup>{

	public int getTotalNumEvaluations() {
		return totalNumEvaluations;
	}

	public void setTotalNumEvaluations(int totalNumEvaluations) {
		this.totalNumEvaluations = totalNumEvaluations;
	}

	private Vector<Piece> pieces;
	private Vector<Vector<Genotype>> genotypes;
	private double captureRatio;
	private int totalNumEvaluations;
	
	public PredatorGroup(Vector<Vector<Genotype>> someGenotypes, Environment env) {
		captureRatio = 0;
		totalNumEvaluations = 0;
		genotypes = someGenotypes;
		pieces = new Vector<Piece>();
		int j = 0;
		for(Vector<Genotype> hiddenNodes : genotypes) {
			ESPArtificialNeuralNetwork network = new ESPArtificialNeuralNetwork(hiddenNodes);
			ESPArtificialNeuralNetworkBehaviour behaviour = new ESPArtificialNeuralNetworkBehaviour(ESPEvolution.boardSize, network);
			pieces.add(new Piece(ESPEvolution.predatorPositions[2 * j],
						ESPEvolution.predatorPositions[2 * j + 1], false, env,
								behaviour));
			j++;
		}
	}

	public Vector<Piece> getPieces() {
		return pieces;
	}

	public void setPieces(Vector<Piece> pieces) {
		this.pieces = pieces;
	}

	public Vector<Vector<Genotype>> getGenotypes() {
		return genotypes;
	}

	public void setGenotypes(Vector<Vector<Genotype>> genotypes) {
		this.genotypes = genotypes;
	}

	public double getCaptureRatio() {
		return captureRatio;
	}

	public void updateCaptureRatio(double newRatio, int numEvaluations) {
		if(numEvaluations == 0){
			captureRatio = newRatio;
			totalNumEvaluations += numEvaluations;
		}
		else{
			captureRatio = (captureRatio * totalNumEvaluations + newRatio * numEvaluations) 
					/ ((double) (totalNumEvaluations + numEvaluations));
			totalNumEvaluations += numEvaluations;
		}
	}
	
	@Override
	public int compareTo(PredatorGroup other) {
		if(captureRatio > other.getCaptureRatio())
			return 1;
		else if (captureRatio < other.getCaptureRatio())
			return -1;
		else if (captureRatio == other.getCaptureRatio())
			return 0;
		else{
			System.out.println("Something went wrong with the PredatorGroup comparisons! (Probably NaN related)");
			return 0;
		}
	}
}
