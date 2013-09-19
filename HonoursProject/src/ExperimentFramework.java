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
						"InterpopulationCrossover="+params[ex].doInterpopulationCrossover + ", BehaviourDistace="
						+params[ex].useBehaviourDistance+", GenotypeDistance="+params[ex].useGenotypeDistance+
						", DeltaThings ="+params[ex].doDavidsDeltaThings);
				fw.write("\n\nExperiment "+ex+": Migration="+params[ex].doMigration+",InterpopulationCrossover="+params[ex].doInterpopulationCrossover + " BehaviourDistace="
						+params[ex].useBehaviourDistance+", GenotypeDistance="+params[ex].useGenotypeDistance+" , DeltaThings ="+params[ex].doDavidsDeltaThings+"\n");
				
				double averageGen = 0, averageCaptures = 0;
				double[] averageAverageFitnesses = new double[params[ex].numPredators];
				
				for(int rep=0; rep<reps[ex]; rep++){
					
					System.out.println("\nRep "+rep+"/"+reps[ex]);
					fw.write("\nRep: "+rep+"\n");
					TrialResult result = ESPEvolution.run(params[ex]);
					
					averageGen += result.generations;
					averageCaptures += result.captureCount;
					for(int i=0; i < result.avgEvalFitnesses.length; i++)
						averageAverageFitnesses[i] =+ result.avgEvalFitnesses[i];
					
					fw.write("Generations: "+result.generations+"\n");
					fw.write("Capture count: "+result.captureCount+"\nAverage fitnesses: ");
					for(int i=0; i<result.avgEvalFitnesses.length; i++)
						fw.write(result.avgEvalFitnesses[i]+" ");
					fw.write("\n");
					
				}//reps
				
				//Summary
				averageGen = averageGen / reps[ex];
				averageCaptures = averageCaptures / reps[ex];
				for(int i=0; i<params[ex].numPredators; i++)
					averageAverageFitnesses[i] = averageAverageFitnesses[i] / reps[ex];
				
				fw.write("\n==================\nSummary of experiment "+ex+"\n==================\n");
				fw.write("Average generations until completion: "+averageGen+"\n");
				fw.write("Average captures: "+averageCaptures+"\n");
				fw.write("Average average fitnesses: ");
				for(int i=0; i<params[ex].numPredators; i++)
					fw.write(averageAverageFitnesses[i]+" ");
				fw.write("\n\n");
				
			}//experiments
			
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
