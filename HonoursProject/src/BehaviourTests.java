import java.util.Vector;


public class BehaviourTests {
	private static final int numRuns = 2;
	
	public static void main(String[] args) {
		
		int[] MAESP = new int[numRuns]; 
		int[] OBM = new int[numRuns]; 
		int[] GeNE = new int[numRuns];
		
		Vector<AlgorithThread> MAESPThreads = new Vector<AlgorithThread> ();
		Vector<AlgorithThread> OBMThreads = new Vector<AlgorithThread> ();
		Vector<AlgorithThread> GeNEThreads = new Vector<AlgorithThread> ();
		
		for(int i = 0; i < numRuns; i++) {
			
			AlgorithThread MAESPThread = new ESPThread(false, false, false, false);
			MAESPThread.start();
			MAESPThreads.add(MAESPThread);
			
			AlgorithThread OBMThread = new ESPThread(false, false, false, true);
			OBMThread.start();
			OBMThreads.add(OBMThread);
			
			AlgorithThread GeNEThread = new GeNEThread();
			GeNEThread.start();
			GeNEThreads.add(MAESPThread);
		}
		for(int i = 0; i < numRuns; i++) {
			try {
			MAESPThreads.elementAt(i).join();
			OBMThreads.elementAt(i).join();
			GeNEThreads.elementAt(i).join();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
		for(int i = 0; i < numRuns; i++) {
			MAESP[i] = MAESPThreads.elementAt(i).getResult();
			OBM[i] = MAESPThreads.elementAt(i).getResult();
			GeNE[i] = MAESPThreads.elementAt(i).getResult();			
		}
		System.out.println("\n\nSummary\n");
		System.out.println("MAESP");
		for(int i = 0; i < numRuns; i++) {
			System.out.print(MAESP[i] + " ");
		}
		System.out.println("Average " + mean(MAESP));
		System.out.println("MAESP with OBM");
		for(int i = 0; i < numRuns; i++) {
			System.out.print(OBM[i] + " ");
		}
		System.out.println("Average " + mean(OBM));
		System.out.println("GEANN");
		for(int i = 0; i < numRuns; i++) {
			System.out.print(GeNE[i] + " ");
		}
		System.out.println("Average " + mean(GeNE));
	}
	
	private static double mean(int[] arr) {
		int sum = 0;
		for(int i = 0; i < arr.length; i++){
			sum += arr[i];
		}
		return sum/((double)arr.length);
	}
}
