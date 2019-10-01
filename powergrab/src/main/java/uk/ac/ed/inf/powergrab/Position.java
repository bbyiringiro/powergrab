package uk.ac.ed.inf.powergrab;



public class Position{
	public double latitude;
	public double longitude;
	public Position(double latitude, double longitude) 
	{
		
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Position nextPosition(Direction direction) 
	{ 
		double w, h; 
		double r =  0.0003;
		
		Position newPosition;
		
		
		switch (direction)
		{
		case N:
			System.out.print("collled");
			newPosition = new Position(latitude + r,  longitude);
			break;
		case NNE:
			w = r * Math.cos( Math.toRadians(67.5));
			h = r * Math.sin( Math.toRadians(67.5));
			
			newPosition = new Position(latitude + h, longitude + w);
			break;
		case NE:
			w = r * Math.cos( Math.toRadians(45));
			h = r * Math.sin( Math.toRadians(45));
			
			newPosition = new Position(latitude + h, longitude + w);
			break;
		case ENE:
			w = r * Math.cos( Math.toRadians(22.5));
			h = r * Math.sin( Math.toRadians(22.5));
			
			newPosition = new Position(latitude + h, longitude + w);
			break;
		case E:
			newPosition = new Position(latitude, longitude + r);
			break;
		case ESE:
			w = r * Math.cos(Math.toRadians(22.5));
			h = r * Math.sin(Math.toRadians(22.5));
			
			newPosition = new Position(latitude - h, longitude + w);
			break;
		case SE:
			w = r * Math.cos( Math.toRadians(45));
			h = r * Math.sin( Math.toRadians(45));
			
			newPosition = new Position(latitude - h, longitude + w);
			break;
		case SSE:
			w = r * Math.cos( Math.toRadians(67.5));
			h = r * Math.sin( Math.toRadians(67.5));
			
			newPosition = new Position(latitude - h, longitude + w);
			break;
		case S:
			newPosition = new Position(latitude - r, longitude);
			break;
		case SSW:
			w = r * Math.cos( Math.toRadians(67.5));
			h = r * Math.sin( Math.toRadians(67.5));
			
			newPosition = new Position(latitude - h, longitude - w);
			break;
		case SW:
			
			w = r * Math.cos( Math.toRadians(45));
			h = r * Math.sin( Math.toRadians(45));
			
			newPosition = new Position(latitude - h, longitude - w);
			break;
		case WSW:
			w = r * Math.cos( Math.toRadians(22.5));
			h = r * Math.sin( Math.toRadians(22.5));
			
			newPosition = new Position(latitude - h, longitude - w);
			break;
		case W:
			newPosition = new Position(latitude, longitude - r);
			break;
		case WNW:
			w = r * Math.cos( Math.toRadians(22.5));
			h = r * Math.sin( Math.toRadians(22.5));
			
			newPosition = new Position(latitude + h, longitude - w);
			break;
		case NW:
			w = r * Math.cos( Math.toRadians(45));
			h = r * Math.sin( Math.toRadians(45));
			
			newPosition = new Position(latitude + h, longitude - w);
			break;
		case NNW:
			w = r * Math.cos( Math.toRadians(67.5));
			h = r * Math.sin( Math.toRadians(67.5));
			
			newPosition = new Position(latitude + h, longitude - w);
			break;
		default:
			// when direction is not know stay still;
			newPosition = this;
		}
		
		if (newPosition.inPlayArea())
			return newPosition;
		else
			return this;
//			
	}
	public boolean inPlayArea() 
	{ 
		
		return  latitude > 55.942617 &&
				latitude < 55.946233 &&
				longitude < -3.184319 &&
				longitude > -3.192473;
	}
	
	public String toString() {
		return "("+latitude+","+longitude+")";
	}
	

}
