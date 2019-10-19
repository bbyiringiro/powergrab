package uk.ac.ed.inf.powergrab;


// TODO: Auto-generated Javadoc
/**
 * The Class Position.
 */
public class Position{
	
	/** The latitude. */
	public double latitude;
	
	/** The longitude. */
	public double longitude;
	
	/** The move radius. */
	double moveRadius =  0.0003;
	
	/** The constant for minimum and maximum values latitude. */
	public static final double [] LAT_INTERVAL = {55.942617, 55.946233};
	
	/** The Constant for minimum and maximum value longitude. */
	public static final double [] LONG_INTERVAL = {-3.192473, -3.184319};
	
	

	/**
	 * Instantiates a new position.
	 *
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public Position(double latitude, double longitude) 
	{
		
		this.latitude = latitude;
		this.longitude = longitude;
		
	}
	
	/**
	 * Next position function.
	 *
	 *Given a direction it returns a possible position a drone can be, 
	 *Ignores invalid directions or if the next position is out of scope the map. 
	 *
	 * @param direction the direction
	 * @return the position
	 */
	public Position nextPosition(Direction direction) 
	{ 
		double w, h; 
		double angle;
		if (direction == null)
			return this;
		
		angle = Math.toRadians(90 - 22.5 * direction.ordinal());
		
		w = moveRadius * Math.cos(angle);
		h = moveRadius * Math.sin(angle);
		return new Position(latitude + h,  longitude + w);
		
	}
	
	/**
	 * In play area.
	 *
	 * @return true, if the position is in the scope of the map.
	 */
	public boolean inPlayArea() 
	{ 
		
		return  latitude > LAT_INTERVAL[0] &&
				latitude < LAT_INTERVAL[1] &&
				longitude > LONG_INTERVAL[0] &&
				longitude < LONG_INTERVAL[1];
				
	}
	
	
	/* 
	 * toString of the Position class
	 */
	public String toString() {
		return "("+latitude+","+longitude+")";
	}
	
	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Sets the latitude.
	 *
	 * @param latitude the new latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Sets the longitude.
	 *
	 * @param longitude the new longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	

}
