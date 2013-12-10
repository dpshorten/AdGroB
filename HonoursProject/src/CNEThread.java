
public class CNEThread extends AlgorithmThread{
	public CNEThread() {
		result = 0;
	}
	
	public void run() {
		result = (new CNEEvolution()).run();
	}
}
