package uk.ac.ed.inf.powergrab;



public class Position{
	public double latitude;
	public double longitude;
	public static final double [] LAT_INTERVAL = {55.942617, 55.946233};
	public static final double [] LONG_INTERVAL = {-3.192473, -3.184319};

	public Position(double latitude, double longitude) 
	{
		
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) 
	{ 
		double w, h; 
		int x=1, y=1;
		double r =  0.0003;
		double angle;
		Position newPosition;
		
		
		boolean isPos [] = getDirectionSigns(direction);
		
		if (!isPos[0])
			y=-1;
		if (!isPos[1])
			x=-1;
		
		switch (direction)
		{
		case N:
		case S:
			newPosition = new Position(latitude + r*y,  longitude);
			break;
		case E:
		case W:
			newPosition = new Position(latitude, longitude + r*x);
			break;
		case NNE:
		case SSE:
		case SSW:
		case NNW:
			angle = Math.toRadians(67.5);
			w = r * Math.cos(angle);
			h = r * Math.sin(angle);
			newPosition = new Position(latitude + h*y, longitude + w*x);
			break;
		case NE:
		case SE:
		case SW:
		case NW:
			angle = Math.toRadians(45);
			w = r * Math.cos(angle);
			h = r * Math.sin(angle);
			newPosition = new Position(latitude + h*y, longitude + w*x);
			break;
		case ENE:
		case ESE:
		case WSW:
		case WNW:
			angle = Math.toRadians(22.5);
			w = r * Math.cos(angle);
			h = r * Math.sin(angle);
			newPosition = new Position(latitude + h*y, longitude + w*x);
			break;
		default:
			// when direction is not know stay still;
			newPosition = this;
		}
		
		if (newPosition.inPlayArea())
			return newPosition;
		else
			return this;			
	}
	public boolean inPlayArea() 
	{ 
		return  latitude > LAT_INTERVAL[0] &&
				latitude < LAT_INTERVAL[1] &&
				longitude > LONG_INTERVAL[0] &&
				longitude < LONG_INTERVAL[1];
				
	}
	
	public static boolean [] getDirectionSigns(Direction dir) 
	{
		boolean signs [] = new boolean[2]; // all values will be initialised as false;
		if (dir.ordinal() <= 4 || dir.ordinal() >= 12)
			signs[0] = true;
		if (dir.ordinal() <= 8) 
			signs[1] = true;
		
		return signs;
	}
	
	public String toString() {
		return "("+latitude+","+longitude+")";
	}
	

}
