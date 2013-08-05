import java.util.Vector;

/*
 * File: SimulationResult.java
 * 
 * Author: David Shorten
 * Date: 20 June 2013
 */

// Struct to hold the result of simulations
public class SimulationResult {
	public int turns;
	public int preyCaught;
	public Vector<Double> finalDistancesFromPrey;
	public Vector<Double> initialDistancesFromPrey;
	
	public SimulationResult(int turns, int preyCaught, Vector<Double> finalDistancesFromPrey, Vector<Double> initialDistancesFromPrey) {
		this.turns = turns;
		this.preyCaught = preyCaught;
		this.finalDistancesFromPrey = finalDistancesFromPrey;
		this.initialDistancesFromPrey = initialDistancesFromPrey;
	}
	
	public String toString() {
		return "turns: " + turns + " prey caught: " + preyCaught + " final distances from prey: " + finalDistancesFromPrey;
	}
}
