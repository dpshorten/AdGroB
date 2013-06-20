
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
}
