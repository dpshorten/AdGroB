
public class TrialResult {
		public double[] avgEvalFitnesses;
		public int captureCount;
		public int generations;

		public TrialResult(double[] avgEvalFitnesses, int captureCount, int generations) {
			this.avgEvalFitnesses = avgEvalFitnesses;
			this.captureCount = captureCount;
			this.generations = generations;
		}
}