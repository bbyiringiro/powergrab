package uk.ac.ed.inf.powergrab;

//import com.mapbox.geojson.Point;
//import com.mapbox.geojson.gson.*;
//import com.mapbox.geojson.

/**
 * Hello world!
 *
 */



public class App 
{
	
    public static void main( String[] args )
    {
    	CommandParser  arguments = new CommandParser(args);
    	
    	Date mapDate = arguments.getMapDate();
    	Position startPos = arguments.getStartCorridinate();
    	int generator = arguments.getGenerator();
    	boolean isStateful = arguments.isStateful();
    	
//    	Point startPoint = Point.fromLngLat(startPos.longitude, startPos.latitude);
    	
    	Position position = new Position(0.0, 0.0);
    	position.nextPosition(Direction.N);
    	
    }
}
