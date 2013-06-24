import java.util.Vector;

public abstract class Behaviour {
	int boardSize;
	
	public Behaviour(int boardSize){
		this.boardSize = boardSize;
	}
	
	//0: North  1: East  2: South  3: West
	public abstract int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey);
}
