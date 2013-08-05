
public class BehaviourTests {
	private static final int numRuns = 5;
	
	public static void main(String[] args) {
		int[] withMigration = new int[numRuns]; 
		int[] withoutMigration = new int[numRuns]; 
		for(int i = 0; i < numRuns; i++) {
			withMigration[i] = ESPEvolution.run(true);
			withoutMigration[i] =  ESPEvolution.run(false);
		}
		System.out.println("\n\nSummary\n");
		System.out.println("Without Migration");
		for(int i = 0; i < numRuns; i++) {
			System.out.print(withoutMigration[i] + " ");
		}
		System.out.println("Average " + mean(withoutMigration));
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