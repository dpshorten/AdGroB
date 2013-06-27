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
	public Vector<Double> distancesFromPrey;
	
	public SimulationResult(int turns, int preyCaught, Vector<Double> distancesFromPrey) {
		this.turns = turns;
		this.preyCaught = preyCaught;
		this.distancesFromPrey = distancesFromPrey;
	}
	
	public String toString() {
		return "turns: " + turns + " prey caught: " + preyCaught + " distances from prey: " + distancesFromPrey;
	}
}
