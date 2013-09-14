import java.util.Vector;

public class Main
{
	public static void main (String [] args)
	{
		final int boardSize = 10;
		final int numPrey = 1;
		
		//TODO: This line was bugged when I did the last pull
		//Environment env = new Environment(boardSize, 1);
		Environment env = new Environment(boardSize);
		
		Vector<Piece> predatorPieces = new Vector<Piece>();
		Vector<Piece> preyPieces = new Vector<Piece>();
		StochasticRunAwayBehaviour runAway = new StochasticRunAwayBehaviour(boardSize, 1);
		
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
		
		SimpleNeuralNetwork SNNBehaviour1;
		SimpleNeuralNetwork SNNBehaviour2;
		SimpleNeuralNetwork SNNBehaviour3;
		try
		{
			SNNBehaviour1 = new SimpleNeuralNetwork("SimplePredatorBehaviour0");
			SNNBehaviour2 = new SimpleNeuralNetwork("SimplePredatorBehaviour0");
			SNNBehaviour3 = new SimpleNeuralNetwork("SimplePredatorBehaviour0");
		}
		catch(Exception e)
		{
			System.out.println("Error opening file for SNN, using default");
			SNNBehaviour1 = new SimpleNeuralNetwork(genotypes1);
			SNNBehaviour2 = new SimpleNeuralNetwork(genotypes2);
			SNNBehaviour3 = new SimpleNeuralNetwork(genotypes3);
		}
		
		SimpleNeuralNetworkBehaviour SNNB1= new SimpleNeuralNetworkBehaviour(boardSize, SNNBehaviour1);
		SimpleNeuralNetworkBehaviour SNNB2= new SimpleNeuralNetworkBehaviour(boardSize, SNNBehaviour2);
		SimpleNeuralNetworkBehaviour SNNB3= new SimpleNeuralNetworkBehaviour(boardSize, SNNBehaviour3);
		
		
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour1 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork1);  
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour2 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork2);  
		ESPArtificialNeuralNetworkBehaviour ESPTestBehaviour3 = new ESPArtificialNeuralNetworkBehaviour(boardSize, ESPTestNetwork3);  
		
		RuleBasedBehaviour RBB1 = new RuleBasedBehaviour(boardSize);
		
		predatorPieces.add(new Piece(2,1,false,env,SNNB1));
		predatorPieces.add(new Piece(2,2,false,env,SNNB2));
		predatorPieces.add(new Piece(2,3,false,env,SNNB3));
		
		preyPieces.add(new Piece(0, 1, true, env, runAway));
		
		//for (int i = 0; i < numPrey; i++) {
		//	preyPieces.add(new Piece(0, i, true, env, runAway));
		//}
		env.setPieces(predatorPieces, preyPieces);
		
		SimulationResult result = env.run(true, false);
		
		System.out.println("Board size: " + boardSize);
		System.out.println(result.preyCaught);
	}

	private static void SimpleNeuralNetwork() {
		// TODO Auto-generated method stub
		
	}
}