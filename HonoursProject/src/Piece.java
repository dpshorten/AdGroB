public class Piece
{
	Environment env;
	Point pos;
	boolean isPrey;
	Behaviour behaviour;
	double turnCounter;
	int ID;
	
	public Piece(int x, int y, boolean prey, Environment env, Behaviour behaviour, int ID)
	{
		pos = new Point(x,y);
		isPrey = prey;
		this.env = env;
		this.behaviour = behaviour;
		this.turnCounter = 0;
		this.ID = ID;
	}
	
	public Piece(Point pos, boolean prey, Environment env, Behaviour behaviour, int ID)
	{
		this.pos = pos;
		isPrey = prey;
		this.env = env;
		this.behaviour = behaviour;
		this.turnCounter = 0;
		this.ID = ID;
	}
	
	Point getPosition(){
		return pos;
	}

	int getPositionX() 
	{
		return pos.x;
	}

	int getPositionY() 
	{
		return pos.y;
	}
	
	Behaviour getBehaviour()
	{
		return behaviour;
	}

	public void setPosition(int newX, int newY) 
	{
		pos.x = newX;
		pos.y = newY;
	}
	
	public double getTurnCounter() {
		return turnCounter;
	}
	
	public void addToTurnCounter(double addedValue) {
		turnCounter += addedValue;
	}
	
	public void resetTurnCounter() {
		turnCounter = 0;
	}

	void makeMove() throws Exception
	{
		//Move represents the direction to move in
		//0: North  1: East  2: South  3: West
		int move = behaviour.getMove(pos, env.predators, env.prey);
		
		switch(move){
		case 0: pos.y--; break;
		case 1: pos.x++; break;
		case 2: pos.y++; break;
		case 3: pos.x--; break;
		case 4: break;
		default: throw new Exception("Behaviour returned an invalid direction (<0 or >4).");
		}
		
		ensurePositionIsInBounds();
	}
	
	double getDistance(Piece otherPiece)
	{
		double distance = Math.sqrt(Math.pow(this.getPositionX() - otherPiece.getPositionX(), 2) + Math.pow(this.getPositionY() - otherPiece.getPositionY(), 2));
		return distance;
	}

	private void ensurePositionIsInBounds ()
	{
		//X coordinate
		
		//right hand side met, move to left
		if(pos.x >= env.boardSize)
			pos.x = 0;

		//left hand side met, move to the right
		if(pos.x < 0)
			pos.x = env.boardSize -1;

		//Y coordinate
		
		//bottom reached, move to top
		if(pos.y >= env.boardSize)
			pos.y = 0;

		//top reached, move to bottom
		if(pos.y < 0)
			pos.y = env.boardSize-1;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
}