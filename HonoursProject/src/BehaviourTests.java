
public class BehaviourTests {
	private static final int numRuns = 5;
	
	public static void main(String[] args) {
		int[] withMigration = new int[numRuns]; 
		for(int i = 0; i < numRuns; i++) {
			System.out.println("\n\nTest number " + (i + 1));
			withMigration[i] = DeltaEvolution.run();
		}
		System.out.println("\n\nSummary\n");
		System.out.println("With Migration");
		for(int i = 0; i < numRuns; i++) {
			System.out.print(withMigration[i] + " ");
		}
		System.out.println("Average " + mean(withMigration));
	}
	
	private static double mean(int[] arr) {
		int sum = 0;
		for(int i = 0; i < arr.length; i++){
			sum += arr[i];
		}
		return sum/((double)arr.length);
	}
}
