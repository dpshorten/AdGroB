public class Piece
{
	Environment env;
	Point pos;
	boolean isPrey;
	Behaviour behaviour;
	
	public Piece(int x, int y, boolean prey, Environment env, Behaviour behaviour)
	{
		pos = new Point(x,y);
		isPrey = prey;
		this.env = env;
		this.behaviour = behaviour;
	}
	
	public Piece(Point pos, boolean prey, Environment env, Behaviour behaviour)
	{
		this.pos = pos;
		isPrey = prey;
		this.env = env;
		this.behaviour = behaviour;
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

	void setPosition(int newX, int newY) 
	{
		pos.x = newX;
		pos.y = newY;
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
		default: throw new Exception("Behaviour returned an invalid direction (<0 or >3).");
		}
		
		ensurePositionIsInBounds();
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
}