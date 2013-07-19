import java.util.Vector;


public class VectorRunAwayBehaviour extends Behaviour {

	public VectorRunAwayBehaviour(int boardSize) {
		super(boardSize);
	}

	@Override
	// 0: North 1: East 2: South 3: West 4: Stay
	public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) {

		//Calculate vector representing optimal direction to run
		Point directionToRun = new Point(0,0);
		for(Piece pred : predator){
			Point currentOffset = Point.getSmallestOffset(pred.getPosition(), myPos, boardSize);
			long xdir = (currentOffset.x == 0 ? boardSize : Math.round(boardSize/(double)currentOffset.x));
			long ydir = (currentOffset.y == 0 ? boardSize : Math.round(boardSize/(double)currentOffset.y));
			directionToRun.x += xdir;
			directionToRun.y += ydir;
			//System.out.println("predator: " + pred.getPosition() + " prey: " +myPos + " offset: " + currentOffset + " xdir: "+xdir+" ydir: "+ydir + " distance: "+Point.getDistance(myPos, pred.getPosition(), boardSize));
		}
		
		//Calculate which tile is closest fit to vector
		if(directionToRun.x == 0 && directionToRun.y == 0)
			return 4;
		else{
			int absx = Math.abs(directionToRun.x);
			int absy = Math.abs(directionToRun.y);
			
			if(absx == absy){
				if(directionToRun.x > 0 && directionToRun.y > 0)
					return (Math.random() > 0.5 ? 0 : 1);
				else if(directionToRun.x > 0 && directionToRun.y < 0)
					return (Math.random() > 0.5 ? 1 : 2);
				else if(directionToRun.x < 0 && directionToRun.y > 0)
					return (Math.random() > 0.5 ? 0 : 3);
				else if(directionToRun.x < 0 && directionToRun.y < 0)
					return (Math.random() > 0.5 ? 2 : 3);
			}
			else{
				if(absx > absy)
					return (directionToRun.x > 0 ? 1 : 3);
				else
					return (directionToRun.y > 0 ? 0 : 2);
			}
		}
		
		return 0;
	}

}
