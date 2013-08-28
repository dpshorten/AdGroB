import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

//TODO:
//genotype distance only
//experiment framework
//crossover instead of migration
//enforced specialisation
//epoch changes - wait until 2 or 3 generations over threshhold?

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
		ExperimentParams[] params = new ExperimentParams[linecount];
		
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
		String line = "";
		try{
			int counter = 0;
			while(s.hasNextLine()){
				line = s.nextLine();
				Scanner ls = new Scanner(line);
				int reps = ls.nextInt();
				String useM = ls.next();
				String useB = ls.next();
				String useG = ls.next();
				
				boolean useMigration = useM.equals("t");
				boolean useBehaviourDistance = useB.equals("t");
				boolean useGenotypeDistance = useG.equals("t");
				
				params[counter] = new ExperimentParams(reps, useMigration, useBehaviourDistance, useGenotypeDistance);
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
				System.out.println("Experiment "+ex+": Migration="+params[ex].useMigration+", BehaviourDistace="
						+params[ex].useBehaviourDistance+", GenotypeDistance="+params[ex].useGenotypeDistance);
				fw.write("\n\nExperiment "+ex+": Migration="+params[ex].useMigration+", BehaviourDistace="
						+params[ex].useBehaviourDistance+", GenotypeDistance="+params[ex].useGenotypeDistance+"\n");
				
				for(int rep=0; rep<params[ex].reps; rep++){
					System.out.println("\nRep "+rep+"/"+params[ex].reps);
					fw.write("\nRep: "+rep+"\n");
					TrialResult result = ESPEvolution.run(params[ex].useMigration, params[ex].useBehaviourDistance, params[ex].useGenotypeDistance);
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

	private static class ExperimentParams{
		int reps;
		boolean useMigration;
		boolean useBehaviourDistance;
		boolean useGenotypeDistance;
		
		public ExperimentParams(int reps, boolean useMigration, boolean useBehaviourDistance, boolean useGenotypeDistance){
			this.reps = reps;
			this.useMigration = useMigration;
			this.useBehaviourDistance = useBehaviourDistance;
			this.useGenotypeDistance = useGenotypeDistance;
		}
	}
}
