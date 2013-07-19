import java.util.Vector;

public abstract class Behaviour {
	int boardSize;
	Vector<Vector<Integer>> offsetHistory;
	int totalNumberOfDecisions;
	int halfBoardSize;
	
	public Behaviour(int boardSize){
		this.boardSize = boardSize;
		this.halfBoardSize = (int)Math.floor(boardSize/((double)2));
		offsetHistory = new Vector<Vector<Integer>>();
		// Negative offset values mean that the offsetHistory dimensions need to be 2 * boardSize.
		for(int i = 0; i <= boardSize; i++) {
			Vector<Integer> offsetHistoryRow = new Vector<Integer>(boardSize);
			for(int j = 0; j <= boardSize; j++) {
				offsetHistoryRow.add(new Integer(0));
			}
			offsetHistory.add(offsetHistoryRow);
		}
		totalNumberOfDecisions = 0;
	}
	
	public void resetHistory() {
		for(Vector<Integer> xVector : offsetHistory) {
			xVector.clear();
			for(int i = 0; i <= boardSize; i++) {
				xVector.add(new Integer(0));
			}
		}
		totalNumberOfDecisions = 0;
	}
	
	//0: North  1: East  2: South  3: West 4: Stay
	public abstract int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey);
}
