import java.util.Vector;

public class Main
{
	public static void main (String [] args)
	{
		final int boardSize = 10;
		final int numPrey = 1;
		
		Environment env = new Environment(boardSize);
		
		Vector<Piece> predatorPieces = new Vector<Piece>();
		Vector<Piece> preyPieces = new Vector<Piece>();
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize);
		
		Vector<Genotype> genotypes1 = new Vector<Genotype>();
		for(int i = 0; i < 10; i++) {
			genotypes1.add(new Genotype());
		}
		Vector<Genotype> genotypes2 = new Vector<Genotype>();
		for(int i = 0; i < 10; i++) {
			genotypes2.add(new Genotype());
		}
		Vector<Genotype> genotypes3 = new Vector<Genotype>();
		for(int i = 0; i < 10; i++) {
			genotypes3.add(new Genotype());
		}
		ESPArtificialNeuralNetwork ESPTestNetwork1 = new ESPArtificialNeuralNetwork(genotypes1);  
		ESPArtificialNeuralNetwork ESPTestNetwork2 = new ESPArtificialNeuralNetwork(genotypes2);  
		ESPArtificialNeuralNetwork ESPTestNetwork3 = new ESPArtificialNeuralNetwork(genotypes3);  
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour1 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork1);  
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour2 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork2);  
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour3 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork3);  
		
		predatorPieces.add(new Piece(2,3,false,env,ESPTestBehaviour1));
		predatorPieces.add(new Piece(2,7,false,env,ESPTestBehaviour2));
		predatorPieces.add(new Piece(2,9,false,env,ESPTestBehaviour3));
		for (int i = 0; i < numPrey; i++) {
			preyPieces.add(new Piece(0, i, true, env, runAway));
		}
		env.setPieces(predatorPieces, preyPieces);
		
		SimulationResult result = env.run();
		System.out.println(result);
	}
}