import java.util.Vector;

public abstract class Behaviour {
	public abstract int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey);
	
	double getDistance(Point p1, Point p2){
		return Math.sqrt( (p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y) );
	}
}
