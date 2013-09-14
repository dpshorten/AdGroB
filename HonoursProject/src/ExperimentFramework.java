import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ExperimentFramework {

	public static void main(String[] args) {

		//Open script file
		Scanner s = null;
		try {
			s = new Scanner(new File("experimentscript"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(s == null)
			System.exit(0);

		int linecount = 0;
		
		//Count the number of lines
		while(s.hasNextLine()){
			linecount++;
			s.nextLine();
		}
		
		//Array to hold parameters for each experiment
		EvolutionParameters[] params = new EvolutionParameters[linecount];
		Integer[] reps = new Integer[linecount];
		
		s.close();
		s = null;
		try {
			s = new Scanner(new File("experimentscript"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(s == null)
			System.exit(0);
		
		//Parse file to get experiment params
		//reps migration crossover behaviour genotype delta
		String line = "";
		try{
			int counter = 0;
			while(s.hasNextLine()){
				line = s.nextLine();
				Scanner ls = new Scanner(line);
				reps[counter] = ls.nextInt();
				String useM = ls.next();
				String useC = ls.next();
				String useB = ls.next();
				String useG = ls.next();
				String useD = ls.next();
				
				boolean useMigration = useM.equals("t");
				boolean useCrossover = useC.equals("t");
				boolean useBehaviourDistance = useB.equals("t");
				boolean useGenotypeDistance = useG.equals("t");
				boolean useDelta = useD.equals("t");
				
				params[counter] = new EvolutionParameters();
				params[counter].doMigration = useMigration;
				params[counter].useBehaviourDistance = useBehaviourDistance;
				params[counter].useGenotypeDistance = useGenotypeDistance;
				params[counter].doInterpopulationCrossover = useCrossover;
				params[counter].doDavidsDeltaThings = useDelta;
				
				counter++;
				ls.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		s.close();
		
		FileWriter fw = null;
		try {
			fw = new FileWriter("results");
			
			for(int ex=0; ex<linecount; ex++){
				System.out.println("Experiment "+ex+": Migration="+params[ex].doMigration+", " +
						"InterpopulationCrossover="+params[ex].doInterpopulationCrossover + " BehaviourDistace="
						+params[ex].useBehaviourDistance+", GenotypeDistance="+params[ex].useGenotypeDistance+
						" , DeltaThings ="+params[ex].doDavidsDeltaThings);
				fw.write("\n\nExperiment "+ex+": Migration="+params[ex].doMigration+",InterpopulationCrossover="+params[ex].doInterpopulationCrossover + " BehaviourDistace="
						+params[ex].useBehaviourDistance+", GenotypeDistance="+params[ex].useGenotypeDistance+" , DeltaThings ="+params[ex].doDavidsDeltaThings+"\n");
				
				for(int rep=0; rep<reps[ex]; rep++){
					System.out.println("\nRep "+rep+"/"+reps[ex]);
					fw.write("\nRep: "+rep+"\n");
					TrialResult result = ESPEvolution.run(params[ex]);
					fw.write("Capture count: "+result.captureCount+"\nAverage fitnesses: ");
					for(int i=0; i<result.avgEvalFitnesses.length; i++)
						fw.write(result.avgEvalFitnesses[i]+" ");
					fw.write("\n");
					
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

//	private static class MetaParams{
//		int reps;
//		boolean useMigration;
//		boolean useBehaviourDistance;
//		boolean useGenotypeDistance;
//		
//		public MetaParams(int reps, boolean useMigration, boolean useBehaviourDistance, boolean useGenotypeDistance){
//			this.reps = reps;
//			this.useMigration = useMigration;
//			this.useBehaviourDistance = useBehaviourDistance;
//			this.useGenotypeDistance = useGenotypeDistance;
//		}
//	}
}
