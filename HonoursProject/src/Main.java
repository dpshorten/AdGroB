public class Main
{
	public static void main (String [] args)
	{

		Environment env = new Environment(10);
		
		SimulationResult result = env.run(3, 1);
		System.out.println(result);
	}
}