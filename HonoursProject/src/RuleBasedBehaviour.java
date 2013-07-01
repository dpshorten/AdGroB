import java.util.Vector;
import java.math.*;


public class RuleBasedBehaviour extends Behaviour
{

	Piece thePrey;

	public RuleBasedBehaviour(int boardsize) 
	{
		super(boardsize);
	}

	// For now it will go for the first prey in the prey vector
	//0: North  1: East  2: South  3: West 4: Stay
	public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) 
	{
		int move = 0;

		if(thePrey == null)
			getPrey(predator, prey);
		
		int index = getIndex(myPos, predator) + 3;
		
		//======================================  GROUP 1 ==================================================
		
		if(index % 3 == 0)
		{	
			// Prey is to the left of predator
			if(thePrey.pos.x < myPos.x)
			{
				if ( Math.abs(thePrey.pos.x - myPos.x) > Math.abs(thePrey.pos.x - myPos.x + boardSize) )
					move = 3;
				else
					move = 1;

			}
			// Prey is to the right of predator
			else if(thePrey.pos.x > myPos.x)
			{
				if ( Math.abs(thePrey.pos.x + myPos.x) < Math.abs(thePrey.pos.x + myPos.x - boardSize) )
					move = 3;
				else
					move = 1;
			}
			else
			{	
				// Prey is above Predator
				if(thePrey.pos.y < myPos.y)
				{
					if ( Math.abs(thePrey.pos.y - myPos.y) > Math.abs(thePrey.pos.y - myPos.y + boardSize) )
						move = 2;
					else
						move = 0;

				}
				// Prey is below Predator
				else if(thePrey.pos.y > myPos.y)
				{
					if ( Math.abs(thePrey.pos.y + myPos.y) < Math.abs(thePrey.pos.y + myPos.y - boardSize) )
						move = 2;
					else
						move = 0;
				}
			}
		}
		
		//======================================  GROUP 2 ==================================================
		//====================== Prey should always be to the right of Group ===============================

		else if (index % 2 == 0)
		{
			if(Math.abs(thePrey.pos.x - myPos.x) > Math.abs(thePrey.pos.y - myPos.y))
			{
				move = 1;
			}
			else
			{	
				// Prey is above Predator
				if(thePrey.pos.y < myPos.y)
				{
					if ( Math.abs(thePrey.pos.y - myPos.y) > Math.abs(thePrey.pos.y - myPos.y + boardSize) )
						move = 2;
					else
						move = 0;

				}
				// Prey is below Predator
				else if(thePrey.pos.y > myPos.y)
				{
					if ( Math.abs(thePrey.pos.y + myPos.y) < Math.abs(thePrey.pos.y + myPos.y - boardSize) )
						move = 2;
					else
						move = 0;
				}
			}
			
		}
		
		//======================================  GROUP 3 ==================================================
		//====================== Prey should always be to the left of Group ===============================
		
		else
		{
			
			if(Math.abs(thePrey.pos.x - myPos.x) > Math.abs(thePrey.pos.y - myPos.y))
			{
				move = 3;
			}
			else
			{	
				// Prey is above Predator
				if(thePrey.pos.y < myPos.y)
				{
					if ( Math.abs(thePrey.pos.y - myPos.y) > Math.abs(thePrey.pos.y - myPos.y + boardSize) )
						move = 2;
					else
						move = 0;

				}
				// Prey is below Predator
				else if(thePrey.pos.y > myPos.y)
				{
					if ( Math.abs(thePrey.pos.y + myPos.y) < Math.abs(thePrey.pos.y + myPos.y - boardSize) )
						move = 2;
					else
						move = 0;
				}
			}
			
		}

		return move;
	}

	public void getPrey(Vector<Piece> predator, Vector<Piece> prey)
	{
		double minDist = Double.MAX_VALUE;

		for(Piece Predator : predator)
		{
			for(Piece Prey : prey)
			{
				double dist = Point.getDistance(Predator.getPosition(), Prey.getPosition(), boardSize);
				if(dist < minDist)
				{
					minDist = dist;
					thePrey = Prey;
				}	
			}	
		}
	}
	
	public int getIndex(Point myPos, Vector<Piece> predator)
	{ 
		int index = 0;
		
		for(Piece P : predator)
		{
			
			if (P.pos.equals(myPos))
			{
				index = predator.indexOf(P);
			}
			
		}
		
		return index;
	}

}

/*
 * 
 * public int getMove(Point myPos, Vector<Piece> predator, Vector<Piece> prey) 
	{
		int move = 0;

		if(thePrey == null)
			getPrey(predator, prey);
		
		int index = getIndex(myPos, predator) + 3;
		
		//======================================  GROUP 1 ==================================================
		
		if(index % 3 == 0)
		{	
			// Prey is to the left of predator
			if(thePrey.pos.x < myPos.x)
			{
				if ( Math.abs(thePrey.pos.x - myPos.x) > Math.abs(thePrey.pos.x - myPos.x + boardSize) )
					move = 0;
				else
					move = 2;

			}
			// Prey is to the right of predator
			else if(thePrey.pos.x > myPos.x)
			{
				if ( Math.abs(thePrey.pos.x + myPos.x) < Math.abs(thePrey.pos.x + myPos.x - boardSize) )
					move = 0;
				else
					move = 2;
			}
			else
			{	
				// Prey is above Predator
				if(thePrey.pos.y < myPos.y)
				{
					if ( Math.abs(thePrey.pos.y - myPos.y) > Math.abs(thePrey.pos.y - myPos.y + boardSize) )
						move = 1;
					else
						move = 3;

				}
				// Prey is below Predator
				else if(thePrey.pos.y > myPos.y)
				{
					if ( Math.abs(thePrey.pos.y + myPos.y) < Math.abs(thePrey.pos.y + myPos.y - boardSize) )
						move = 1;
					else
						move = 3;
				}
			}
		}
		
		//======================================  GROUP 2 ==================================================
		//====================== Prey should always be to the right of Group ===============================

		else if (index % 2 == 0)
		{
			if(Math.abs(thePrey.pos.x - myPos.x) > Math.abs(thePrey.pos.y - myPos.y))
			{
				move = 2;
			}
			else
			{	
				// Prey is above Predator
				if(thePrey.pos.y < myPos.y)
				{
					if ( Math.abs(thePrey.pos.y - myPos.y) > Math.abs(thePrey.pos.y - myPos.y + boardSize) )
						move = 1;
					else
						move = 3;

				}
				// Prey is below Predator
				else if(thePrey.pos.y > myPos.y)
				{
					if ( Math.abs(thePrey.pos.y + myPos.y) < Math.abs(thePrey.pos.y + myPos.y - boardSize) )
						move = 1;
					else
						move = 3;
				}
			}
			
		}
		
		//======================================  GROUP 3 ==================================================
		//====================== Prey should always be to the left of Group ===============================
		
		else
		{
			
			if(Math.abs(thePrey.pos.x - myPos.x) > Math.abs(thePrey.pos.y - myPos.y))
			{
				move = 0;
			}
			else
			{	
				// Prey is above Predator
				if(thePrey.pos.y < myPos.y)
				{
					if ( Math.abs(thePrey.pos.y - myPos.y) > Math.abs(thePrey.pos.y - myPos.y + boardSize) )
						move = 1;
					else
						move = 3;

				}
				// Prey is below Predator
				else if(thePrey.pos.y > myPos.y)
				{
					if ( Math.abs(thePrey.pos.y + myPos.y) < Math.abs(thePrey.pos.y + myPos.y - boardSize) )
						move = 1;
					else
						move = 3;
				}
			}
			
		}

		return move;
	}
 * 
 * 
 */
