import java.util.Vector;

public class StochasticRunAwayBehaviour extends Behaviour {

	// The behaviour automatically outputs a stay unless turnsPerMove divides
	// turnCounter
	int turnsPerMove;
	int turnCounter;

	public StochasticRunAwayBehaviour(int boardSize, int turnsPerMove) {
		super(boardSize);
		this.turnsPerMove = turnsPerMove;
		this.turnCounter = 0;
	}

	@Override
	// 0: North 1: East 2: South 3: West 4: stay
	public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) {
		turnCounter++;
		if (turnCounter % turnsPerMove != 0) {
			return 4;
		} else {
			double minDist = -1;
			Point closestOther = new Point();

			Vector<Piece> allOtherPieces = new Vector<Piece>();
			allOtherPieces.addAll(predator);
			allOtherPieces.addAll(prey);

			// Find and remove the piece whose behaviour is being determined
			Piece me = null;
			for (Piece p : allOtherPieces) {
				if (p.pos.equals(myPos))
					me = p;
			}
			allOtherPieces.remove(me);

			// Find closest other piece
			for (Piece p : allOtherPieces) {
				double dist = Point.getDistance(myPos, p.pos, boardSize);
				if (minDist == -1 || dist < minDist) {
					minDist = dist;
					closestOther = p.pos;
				}
			}

			Point offset = Point.getSmallestOffset(myPos, closestOther,
					boardSize);
			/*
			if (offset.x == 0) {
				double rand = Math.random();
				if (offset.y > 0) {
					// Return North or East or West
					if (rand < 0.3333)
						return 0;
					else if (rand > 0.6666)
						return 1;
					else
						return 3;
				} else {
					// Return East or South or West
					if (rand < 0.3333)
						return 1;
					else if (rand > 0.6666)
						return 2;
					else
						return 3;
				}
			} else if (offset.y == 0) {
				double rand = Math.random();
				if (offset.x > 0) {
					// Return North or South or West
					if (rand < 0.3333)
						return 0;
					else if (rand > 0.6666)
						return 2;
					else
						return 3;
				} else {
					// Return North or East or South
					if (rand < 0.3333)
						return 0;
					else if (rand > 0.6666)
						return 1;
					else
						return 2;
				}
			} else if (offset.x > 0 && offset.y > 0) {
				// Return North or West
				if (Math.random() > 0.5)
					return 0;
				else
					return 3;
			} else if (offset.x > 0 && offset.y < 0) {
				// Return South or West
				if (Math.random() > 0.5)
					return 2;
				else
					return 3;
			} else if (offset.x < 0 && offset.y > 0) {
				// Return North or East
				if (Math.random() > 0.5)
					return 0;
				else
					return 1;
			} else if (offset.x < 0 && offset.y < 0) {
				// Return East or South
				if (Math.random() > 0.5)
					return 1;
				else
					return 2;
			}*/
			
			double offsetRatio = offset.y / ((double)offset.x);
			// 0: North 1: East 2: South 3: West 4: stay
			if (offset.x < 0) {
				if(offsetRatio < -1) {
					return 0;
				} else if(offsetRatio < 1) {
					return 1;
				} else {
					return 2;
				}
			} else {
				if(offsetRatio < -1) {
					return 2;
				} else if(offsetRatio < 1) {
					return 3;
				} else {
					return 0;
				} 
			}
			//return -1;
		}
	}
}
