package uk.ac.ed.inf.powergrab;
import java.util.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.EnumMap;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.LineString;

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
    	
    	Drone currDrone =  new StatefulDrone(startPos, "stateful", generator);
    	String mapSource = "";
    	
    	try {
    		mapSource = currDrone.loadMap(mapUrl);
    	}
    	catch (Exception e) {
			// TODO: handle exception
		}
    	
    	List<Point> path = new ArrayList<>();
    	path.add(Point.fromLngLat(currDrone.getPosition().getLongitude(), currDrone.getPosition().getLatitude()));
    	String logs = "";
    	while(moves <= 250 && currDrone.getPower() >= 1.25) {
    		print(moves+"th steps\n");
    		String logLine="";
    		Position dronePosition = currDrone.getPosition();
    		logLine += dronePosition.getLatitude() + ", "+ dronePosition.getLongitude() +", ";
    		
    		// inspect the current state of the map, and current position;
    		// calculate allowables moves of the drone;
    		// decide in which direction to move;
    		//move to your next position, update your position
    		// charge from the nearest changing station (if in range)
    		
    		Direction nextDir = currDrone.chooseDirection();
    		System.out.println(nextDir);
    		currDrone.move(nextDir);
    		currDrone.charge();
    		
    		++ moves;
    		currDrone.consumedPower(1.25); 
    		
    		
    		
    		
    		
    		path.add(Point.fromLngLat(currDrone.getPosition().getLongitude(), currDrone.getPosition().getLatitude()));
    		logLine +=nextDir + ", " +dronePosition.getLatitude() + ", "+ dronePosition.getLongitude() +", "+currDrone.getPowerCoins()+", "+currDrone.getPower();
    		logs +=logLine+"\n";
    		
    		
    		
    		print("----------------------------------------------------------------------------------------------\n");
    		
    	}
    	
    	
    	System.out.println("Finished with PowerCoin:"+ currDrone.getPowerCoins() + " Power: "+currDrone.getPower());
    	System.out.print("moves :"+ (moves-1));
    	
    	LineString pathLineStrign = LineString.fromLngLats(path);
    	
    	Feature outFeature = Feature.fromGeometry(pathLineStrign);
    	FeatureCollection fc = FeatureCollection.fromJson(mapSource);
    	List<Feature> features = (ArrayList<Feature>) fc.features();
    	features.add(outFeature);
    	FeatureCollection out = FeatureCollection.fromFeatures(features);
    	mapSource = out.toJson();
    	
    	String filename = currDrone.getTyep() +"-"+mapDate.formatDate("-", false);
    	Logger.saveJson(mapSource, filename);
    	Logger.saveLogs(logs, filename);
    	

    	
    	
    }
}
