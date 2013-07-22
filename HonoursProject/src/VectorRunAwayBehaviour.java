import java.util.Vector;


public class VectorRunAwayBehaviour extends Behaviour {

	public VectorRunAwayBehaviour(int boardSize) {
		super(boardSize);
	}

	@Override
	// 0: North 1: East 2: South 3: West 4: Stay
	public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) {

		//Calculate vector representing optimal direction to run
		double maxDist = (double) boardSize * 0.7071;
		double xdir = 0;
		double ydir = 0;
		for(Piece pred : predator){
			Point currentOffset = Point.getSmallestOffset(pred.getPosition(), myPos, boardSize);
			double scalingFactor = maxDist / Math.pow(Point.getDistance(pred.getPosition(), myPos, boardSize), 2);
			double inverseXOffset = (currentOffset.x > 0 ? boardSize/2 - currentOffset.x : -boardSize/2 + currentOffset.x);
			double inverseYOffset = (currentOffset.y > 0 ? boardSize/2 - currentOffset.y : -boardSize/2 + currentOffset.y);
			xdir += (inverseXOffset * scalingFactor);
			ydir += (inverseYOffset * scalingFactor);
			
			System.out.println("offset: " + currentOffset + " inverseX " + inverseXOffset + " inverseY " + inverseYOffset + " scaling " + scalingFactor + " xcont: "+(inverseXOffset * scalingFactor)+" ycont: "+(inverseYOffset * scalingFactor));
		}
		System.out.println("Final vector: "+ xdir + ", "+ydir);
		
		//Calculate which tile is closest fit to vector
		if(xdir == 0 && ydir == 0){
			System.out.println("Decision: stay");
			return 4;
		}
		else{
			double absx = Math.abs(xdir);
			double absy = Math.abs(ydir);
			
			if(absx == absy){
				if(xdir > 0 && ydir > 0){
//					System.out.println("Decision: South or East");
					return (Math.random() > 0.5 ? 1 : 2);
				}
				else if(xdir > 0 && ydir < 0){
//					System.out.println("Decision: North or East");
					return (Math.random() > 0.5 ? 0 : 1);
				}
				else if(xdir < 0 && ydir > 0){
//					System.out.println("Decision: South or West");
					return (Math.random() > 0.5 ? 2 : 3);
				}
				else if(xdir < 0 && ydir < 0){
//					System.out.println("Decision: North or West");
					return (Math.random() > 0.5 ? 0 : 3);
				}
			}
			else{
				if(absx > absy){
					System.out.println("Decision: "+(xdir > 0 ? "East" : "West"));
					return (xdir > 0 ? 1 : 3);
				}
				else{
					System.out.println("Decision: "+(ydir > 0 ? "South" : "North"));
					return (ydir > 0 ? 2 : 0);
				}
			}
		}
		
		return 0;
	}

}
