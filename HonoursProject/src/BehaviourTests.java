import java.util.Vector;


public class BehaviourTests {
	private static final int numRuns = 1;
	
	public static void main(String[] args) {
		
		int[] MAESP = new int[numRuns]; 
		int[] OBM = new int[numRuns]; 
		int[] GeNE = new int[numRuns];
		int[] CNE = new int[numRuns];
		
		Vector<AlgorithmThread> MAESPThreads = new Vector<AlgorithmThread> ();
		Vector<AlgorithmThread> OBMThreads = new Vector<AlgorithmThread> ();
		Vector<AlgorithmThread> GeNEThreads = new Vector<AlgorithmThread> ();
		Vector<AlgorithmThread> CNEThreads = new Vector<AlgorithmThread> ();
		
		for(int i = 0; i < numRuns; i++) {
			
			AlgorithmThread MAESPThread = new ESPThread(false, false, false, false);
			MAESPThread.start();
			MAESPThreads.add(MAESPThread);
			
			//comment
			AlgorithmThread OBMThread = new ESPThread(false, false, false, true);
			OBMThread.start();
			OBMThreads.add(OBMThread);
			
			AlgorithmThread GeNEThread = new GeNEThread();
			GeNEThread.start();
			GeNEThreads.add(GeNEThread);
			
			AlgorithmThread CNEThread = new CNEThread();
			CNEThread.start();
			CNEThreads.add(CNEThread);
		}
		
		for(int i = 0; i < numRuns; i++) {
			try {
			MAESPThreads.elementAt(i).join();
			OBMThreads.elementAt(i).join();
			GeNEThreads.elementAt(i).join();
			CNEThreads.elementAt(i).join();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
		for(int i = 0; i < numRuns; i++) {
			MAESP[i] = MAESPThreads.elementAt(i).getResult();
			OBM[i] = OBMThreads.elementAt(i).getResult();
			GeNE[i] = GeNEThreads.elementAt(i).getResult();	
			CNE[i] = GeNEThreads.elementAt(i).getResult();
		}
		for(int i = 0; i < numRuns; i++) {
			System.out.print(MAESP[i] + ",");
		}
		System.out.println();
		for(int i = 0; i < numRuns; i++) {
			System.out.print(OBM[i] + ",");
		}
		System.out.println();
		for(int i = 0; i < numRuns; i++) {
			System.out.print(GeNE[i] + ",");
		}
		System.out.println();
		for(int i = 0; i < numRuns; i++) {
			System.out.print(CNE[i] + ",");
		}
	}
	
	private static double mean(int[] arr) {
		int sum = 0;
		for(int i = 0; i < arr.length; i++){
			sum += arr[i];
		}
		return sum/((double)arr.length);
	}
}
