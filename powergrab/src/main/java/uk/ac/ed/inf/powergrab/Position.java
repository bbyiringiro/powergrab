package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	public Position(double latitude, double longitude) 
	{
		
	}
	public Position nextPosition(Direction direction) 
	{ 
		return this;
	}
	public boolean inPlayArea() 
	{ 
		return true;
	}

}
