import java.util.Vector;

public class PredatorGroup implements Comparable<PredatorGroup>{

	private Vector<Vector<Genotype>> genotypes;
	private double captureRatio;
	private double mostRecentCaptureRatio;
	private int totalNumEvaluations;
	private Environment env;
	
	public PredatorGroup(Vector<Vector<Genotype>> someGenotypes, Environment enviro) {
		captureRatio = 0;
		mostRecentCaptureRatio = 0;
		totalNumEvaluations = 0;
		genotypes = someGenotypes;
		env = enviro;
	}

	public Vector<Piece> getPieces() {
		Vector<Piece> pieces = new Vector<Piece>();
		int j = 0;
		for(Vector<Genotype> hiddenNodes : genotypes) {
			ESPArtificialNeuralNetwork network = new ESPArtificialNeuralNetwork(hiddenNodes);
			ESPArtificialNeuralNetworkBehaviour behaviour = new ESPArtificialNeuralNetworkBehaviour(EvolutionParameters.boardSize, network);
			pieces.add(new Piece(EvolutionParameters.predatorPositions[2 * j],
					EvolutionParameters.predatorPositions[2 * j + 1], false, env,
								behaviour));
			j++;
		}
		return pieces;
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
		mostRecentCaptureRatio = newRatio;
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
	
	public double getMostRecentCaptureRatio() {
		return mostRecentCaptureRatio;
	}

	public void setMostRecentCaptureRatio(double mostRecentCaptureRatio) {
		this.mostRecentCaptureRatio = mostRecentCaptureRatio;
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
	
	public int getTotalNumEvaluations() {
		return totalNumEvaluations;
	}

	public void setTotalNumEvaluations(int totalNumEvaluations) {
		this.totalNumEvaluations = totalNumEvaluations;
	}
}
