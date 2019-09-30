package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.PointAsCoordinatesTypeAdapter;

public class Position extends PointAsCoordinatesTypeAdapter{
	public double latitude;
	public double longitude;
	public Position(double latitude, double longitude) 
	{
		//TASK 
		//check if they are inbound
		this.latitude = latitude;
		this.longitude = longitude;
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
