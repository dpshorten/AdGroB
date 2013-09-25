import java.util.Random;
import java.util.Vector;

@SuppressWarnings("static-access")
public class SimulationsOfSavedANNs {
	public static void main(String[] args) {

		EvolutionParameters params = new EvolutionParameters();
		Environment env = new Environment(params.boardSize, 1.0, params.numPredators);
		Random random = new Random();
		
		final int simulations = 10;
		for (int i = 0; i < simulations; i++) {
			System.out.println("Running simulation "+(i+1)+"/"+simulations);
			Vector<Piece> predatorPieces = new Vector<Piece>();
			Vector<Piece> preyPieces = new Vector<Piece>();
			
			//VectorRunAwayBehaviour runAway = new VectorRunAwayBehaviour(boardSize);
			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(params.boardSize, 1);
			
			for(int j=0; j<params.numPrey; j++){
				int preyX = random.nextInt(params.boardSize);
				int preyY = random.nextInt(params.boardSize);
				preyPieces.add(new Piece(preyX, preyY, true, env, runAway));
			}
			
			Vector<String> fileNames = new Vector<String>();
			for(int j=0; j<params.numPredators; j++){
				fileNames.add("PredatorBehaviour"+j);
			}
			int j = 0;
			for (String fileName : fileNames) {
				ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(fileName);
				ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(params.boardSize, ann);
				predatorPieces.add(new Piece(1, 1, false, env, annBehaviour));
				j++;
			}
			env.setPieces(predatorPieces, preyPieces);
			env.run(true, i == 0 ? false : true);
		}
		System.out.println("Done.");
	}
}
