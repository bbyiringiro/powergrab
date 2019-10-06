package uk.ac.ed.inf.powergrab;
import java.util.EnumMap;

//import com.mapbox.geojson.Point;
//import com.mapbox.geojson.gson.*;
//import com.mapbox.geojson.

/**
 * Hello world!
 *
 */




public class App 
{
	public static void print(String s) {
		System.out.println(s);
	}
	
    public static void main( String[] args )
    {
    	CommandParser  arguments = new CommandParser(args);
    	
    	Date mapDate = arguments.getMapDate();
    	Position startPos = arguments.getStartCorridinate();
    	int generator = arguments.getGenerator();
    	boolean isStateful = arguments.isStateful();
    	
    	print(startPos.toString());
    	
    	
    }
}
