public class Main
{
	public static void main (String [] args)
	{

		Environment env = new Environment(3);
		
		SimulationResult result = env.run(2, 2);
		System.out.println(result);
	}
}