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
	
	public SimulationResult(int turns, int preyCaught) {
		this.turns = turns;
		this.preyCaught = preyCaught;
	}
	
	public String toString() {
		return "turns: " + turns + " prey caught: " + preyCaught;
	}
}
