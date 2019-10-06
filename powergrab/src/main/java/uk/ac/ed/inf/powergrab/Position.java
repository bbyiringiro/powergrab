package uk.ac.ed.inf.powergrab;


public class Position{
	public double latitude;
	public double longitude;
	double moveRadius =  0.0003;
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
		Position newPosition;
		double angle = Math.toRadians(90 - 22.5 * direction.ordinal());
		
		w = moveRadius * Math.cos(angle);
		h = moveRadius * Math.sin(angle);
		newPosition = new Position(latitude + h,  longitude + w);

		// avoiding an invalid position beforehand
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
	
	
	public String toString() {
		return "("+latitude+","+longitude+")";
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	

}
