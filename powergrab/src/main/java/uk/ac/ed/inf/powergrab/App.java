package uk.ac.ed.inf.powergrab;
import java.awt.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.EnumMap;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

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
		System.out.print(s);
	}
	
	public static String formatUrl(Date mapDate, String baseUrl, String geoJsonFile) {
		String mapDateIndex;
		if(baseUrl.equals("")) {
			baseUrl = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
		}
		
		if(geoJsonFile.equals("")) {
			geoJsonFile = "powergrabmap.geojson";
		}
		
		if (mapDate == null) {
			mapDateIndex = "";
		}
		else
			mapDateIndex =  mapDate.formatDate("", true)+"/";
		
		return baseUrl +mapDateIndex+geoJsonFile;
	}
	
    public static void main( String[] args )
    {
    	CommandParser  arguments = new CommandParser(args);
    	
    	Date mapDate = arguments.getMapDate();
    	Position startPos = arguments.getStartCorridinate();
    	int generator = arguments.getGenerator();
    	boolean isStateful = arguments.isStateful();
    	String mapUrl = formatUrl(mapDate, "", "");
    	
    	
    	
    	// get a drone
    	int moves=1;
    	double power = 150;
    	double powerCoins= 150;
    	
    	Drone currDrone =  new StatelessDrone(startPos, "statless", generator);
    	
    	try {
    		currDrone.loadMap(mapUrl);
    	}
    	catch (Exception e) {
			// TODO: handle exception
		}
    	
    	while(moves <= 250 && currDrone.getPower() >= 1.25) {
    		
    		// inspect the current state of the map, and current position;
    		// calculate allowables moves of the drone;
    		// decide in which direction to move;
    		//move to your next position, update your position
    		// charge from the nearest changing station (if in range)
    		
    		currDrone.charge(); // charge the drone first in case, it's initialised at a station
    		Direction nextDir = currDrone.chooseDirection();
    		currDrone.move(nextDir);
    		
    		
    		++ moves;
    		currDrone.consumedPower(1.25); 
    		
    	}
    	
    	
    	System.out.println("Finished with PowerCoin:"+ currDrone.getPowerCoins() + " Power: "+currDrone.getPower());
    	System.out.print("moves :"+ (moves-1));

    	
    	
    }
}
