
public class Point {
	public int x;
	public int y;

	public Point(){
		this.x = 0;
		this.y = 0;
	}
	
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Point(Point toCopy){
		this.x = toCopy.x;
		this.y = toCopy.y;
	}
	
	public int x() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int y() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean equals(Point otherPoint) {
		return (this.x == otherPoint.x()) & (this.y == otherPoint.y());
	}
	
	public String toString() {
		return x + "  " + y;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if(arg0==null)
			return false;
		else if(!(arg0 instanceof Point))
			return false;
		else{
			Point other = (Point) arg0;
			if(other.x == x && other.y == y)
				return true;
			else
				return false;
		}
	}
	
	@Override
	public int hashCode() {
		return x*x+y*y;
	}
	
	//Returns the distance between two points on a toroidal board
	static double getDistance(Point p1, Point p2, int boardSize){
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
	
	//Returns a point representing the shortest x and y distances from p1 to p2
	//The returned point can have negative values
	static Point getSmallestOffset(Point p1, Point p2, int boardSize){
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
