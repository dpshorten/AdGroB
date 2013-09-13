import java.util.Random;
import java.util.Vector;


public class SimulationsOfSavedANNs {
	public static void main(String[] args) {
		int boardSize = 100;
		int[] predatorPositions = { 30, 30, boardSize - 30, 30, 30, boardSize - 30, boardSize - 30, boardSize - 30};
		
		Random random = new Random();
		Environment env = new Environment(boardSize, 1.0, 3);
		
		final int simulations = 10;
		for (int i = 0; i < simulations; i++) {
			System.out.println("Running simulation "+(i+1)+"/"+simulations);
			Vector<Piece> predatorPieces = new Vector<Piece>();
			Vector<Piece> preyPieces = new Vector<Piece>();
			
			//VectorRunAwayBehaviour runAway = new VectorRunAwayBehaviour(boardSize);
			StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 1);
			//int preyX = random.nextInt(boardSize);
			//int preyY = random.nextInt(boardSize);
			int preyX = i*10;
			int preyY = i*10;
			preyPieces.add(new Piece(preyX, preyY, true, env, runAway));
			
			String[] fileNames = {"PredatorBehaviour0", "PredatorBehaviour1", "PredatorBehaviour2"};
			int j = 0;
			for (String fileName : fileNames) {
				ESPArtificialNeuralNetwork ann = new ESPArtificialNeuralNetwork(fileName);
				ESPArtificialNeuralNetworkBehaviour annBehaviour = new ESPArtificialNeuralNetworkBehaviour(boardSize, ann);
				predatorPieces.add(new Piece(1, 1, false, env, annBehaviour));
				j++;
			}
			env.setPieces(predatorPieces, preyPieces);
			env.run(true, i == 0 ? false : true);
		}
		System.out.println("Done.");
	}
}
