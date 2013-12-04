
public class GeNEThread extends AlgorithmThread{
	public GeNEThread() {
		result = 0;
	}
	
	public void run() {
		result = (new DeltaEvolution()).run();
	}
}
