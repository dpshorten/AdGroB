import java.util.Vector;

public abstract class Behaviour {
	int boardSize;
	
	public Behaviour(int boardSize){
		this.boardSize = boardSize;
	}
	
	public abstract int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey);
	
	//Returns the distance between two points on a toroidal board
	double getDistance(Point p1, Point p2){
		int x_dist = 0;
		int y_dist = 0;
		int x_inside = Math.abs(p1.x - p2.x);
		int y_inside = Math.abs(p1.y - p2.y);
		int x_outside = boardSize - x_inside;
		int y_outside = boardSize - y_inside;
		
		if(x_inside <= x_outside)
			x_dist = x_inside;
		else
			x_dist = x_outside;
		
		if(y_inside <= y_outside)
			y_dist = y_inside;
		else
			y_dist = y_outside;
		
		return Math.sqrt( x_dist*x_dist + y_dist*y_dist );
	}
	
	//Returns the smallest point p_r such that p1 + p_r = p2
	//Returned points can have negative values
	Point getSmallestOffset(Point p1, Point p2){
		int x_offset = 0;
		if(p1.x != p2.x){
			//Inside offset is the offset without wrapping around
			//Outside offset is the offset with wrapping around
			int x_inside = p2.x - p1.x;
			int x_outside = (x_inside > 0 ? x_inside - boardSize : x_inside + boardSize);
			x_offset = (Math.abs(x_inside) <= Math.abs(x_outside) ? x_inside : x_outside);
		}
		
		int y_offset = 0;
		if(p1.y != p2.y){
			int y_inside = p2.y - p1.y;
			int y_outside = (y_inside > 0 ? y_inside - boardSize : y_inside + boardSize);
			y_offset = (Math.abs(y_inside) <= Math.abs(y_outside) ? y_inside : y_outside);
		}
		
		return new Point(x_offset, y_offset);
	}
}
