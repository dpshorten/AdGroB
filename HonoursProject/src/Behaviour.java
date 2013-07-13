import java.util.Vector;

public abstract class Behaviour {
	int boardSize;
	Vector<Vector<Integer>> offsetHistory;
	int totalNumberOfDecisions;
	
	public Behaviour(int boardSize){
		this.boardSize = boardSize;
		offsetHistory = new Vector<Vector<Integer>>();
		for(int i = 0; i < boardSize; i++) {
			offsetHistory.set(i, new Vector<Integer>(boardSize));
		}
		totalNumberOfDecisions = 0;
	}
	
	public void resetHistory() {
		for(Vector<Integer> xVector : offsetHistory) {
			xVector.clear();
		}
		totalNumberOfDecisions = 0;
	}
	
	//0: North  1: East  2: South  3: West 4: Stay
	public abstract int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey);
}
