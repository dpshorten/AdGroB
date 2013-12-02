
public class ESPThread extends AlgorithThread {
	private boolean doMigration, useBehaviourDistance, useGenotypeDistance, doDavidsDeltaThings;
	
	public ESPThread(boolean doMigration, boolean useBehaviourDistance,
			boolean useGenotypeDistance, boolean doDavidsDeltaThings) {
		super();
		this.doMigration = doMigration;
		this.useBehaviourDistance = useBehaviourDistance;
		this.useGenotypeDistance = useGenotypeDistance;
		this.doDavidsDeltaThings = doDavidsDeltaThings;
	}
	
	public void run() {
		result =  (new ESPEvolution()).run(doMigration, useBehaviourDistance, useGenotypeDistance, doDavidsDeltaThings).generations;
	}
}
