
public class BehaviourTests {
	private static final int numRuns = 1;
	
	public static void main(String[] args) {
		int[] MAESP = new int[numRuns]; 
		int[] OBM = new int[numRuns]; 
		int[] GEANN = new int[numRuns]; 
		for(int i = 0; i < numRuns; i++) {
			System.out.println("\n\nTest number " + (i + 1) + "\n\n");
			System.out.println("\n MAESP \n");
			MAESP[i] = ESPEvolution.run(false, false, false, false).generations;
			System.out.println("\n MAESP with OBM \n");
			OBM[i] = ESPEvolution.run(false, false, false, true).generations;
			System.out.println("\n GEANN \n");
			GEANN[i] = DeltaEvolution.run();
			
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
			System.out.print(MAESP[i] + " ");
		}
		System.out.println("Average " + mean(GEANN));
	}
	
	private static double mean(int[] arr) {
		int sum = 0;
		for(int i = 0; i < arr.length; i++){
			sum += arr[i];
		}
		return sum/((double)arr.length);
	}
}
