import java.util.Vector;


public class StochasticRunawayBehaviour extends Behaviour {

	public StochasticRunawayBehaviour(int boardSize) {
		super(boardSize);
	}
	
	@Override
  //0: North  1: East  2: South  3: West
  public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) {
    double minDist = -1;
    Point closestOther = new Point();
    
    Vector<Piece> allOtherPieces = new Vector<Piece>();
    allOtherPieces.addAll(predator);
    allOtherPieces.addAll(prey);
    
    for(Piece p : allOtherPieces){
      double dist = getDistance(myPos, p.pos);
      if(minDist==-1 || dist < minDist){
        minDist = dist;
        closestOther = p.pos;
      }
    }
    
    if(closestOther.x == myPos.x){
      double rand = Math.random();
      if(closestOther.y > myPos.y){
        //Return North or East or West
        if(rand < 0.3333) return 0;
        else if(rand > 0.6666) return 1;
        else return 3;
      }
      else{
        //Return East or South or West
        if(rand < 0.3333) return 1;
        else if(rand > 0.6666) return 2;
        else return 3;
      }
    }
    else if(closestOther.y == myPos.y){
      double rand = Math.random();
      if(closestOther.x > myPos.x){
        //Return North or South or West
        if(rand < 0.3333) return 0;
        else if(rand > 0.6666) return 2;
        else return 3;
      }
      else{
        //Return North or East or South
        if(rand < 0.3333) return 0;
        else if(rand > 0.6666) return 1;
        else return 2;
      }
    }
    else if(closestOther.x > myPos.x && closestOther.y > myPos.y){
      //Return North or West
      if(Math.random() > 0.5) return 0;
      else return 3;
    }
    else if(closestOther.x > myPos.x && closestOther.y < myPos.y){
      //Return South or West
      if(Math.random() > 0.5) return 2;
      else return 3;
    }
    else if(closestOther.x < myPos.x && closestOther.y > myPos.y){
      //Return North or East
      if(Math.random() > 0.5) return 0;
      else return 1;
    }
    else if(closestOther.x < myPos.x && closestOther.y < myPos.y){
      //Return East or South
      if(Math.random() > 0.5) return 1;
      else return 2;
    }
    
    return -1;
  }
}