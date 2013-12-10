
public class TrialResult {
		public double avgEvalFitness;
		public int captureCount;
		public int generations;

		public TrialResult(double avgEvalFitness, int captureCount, int generations) {
			this.avgEvalFitness = avgEvalFitness;
			this.captureCount = captureCount;
			this.generations = generations;
		}
}