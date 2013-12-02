
public class GeNEThread extends AlgorithThread{
	public GeNEThread() {
		result = 0;
	}
	
	public void run() {
		result = (new DeltaEvolution()).run();
	}
}
