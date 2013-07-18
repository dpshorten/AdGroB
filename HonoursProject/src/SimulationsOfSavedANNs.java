import java.util.Random;
import java.util.Vector;


public class SimulationsOfSavedANNs {
	public static void main(String[] args) {
		int boardSize = 100;
		Random random = new Random();
		Environment env = new Environment(boardSize, 1);
		
		for (int i = 0; i < 10; i++) {
			Vector<Piece> predatorPieces = new Vector<Piece>();
			Vector<Piece> preyPieces = new Vector<Piece>();
			
			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(
					boardSize, 1);
			int preyX = random.nextInt(boardSize);
			int preyY = random.nextInt(boardSize);
			preyPieces.add(new Piece(preyX, preyY, true, env, runAway));
			
			String[] fileNames = {"PredatorBehaviour0", "PredatorBehaviour1", "PredatorBehaviour2"};
			for (String fileName : fileNames) {
				ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(fileName);
				ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(boardSize, ann);
				predatorPieces.add(new Piece(5, 5, false, env, annBehaviour));
			}
			env.setPieces(predatorPieces, preyPieces);
			env.run(true, i == 0 ? false : true);
		}
	}
}
