package uk.ac.ed.inf.powergrab;
import java.util.List;
import java.util.ArrayList;

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
    	CommandArgsParser  arguments = new CommandArgsParser(args);
    	
    	Date mapDate = arguments.getMapDate();
    	Position startPos = arguments.getStartCorridinate();
    	int generator = arguments.getGenerator();
    	boolean isStateful = arguments.isStateful();
    	String mapUrl = formatUrl(mapDate, "", "");
    	
    	

    	Drone currDrone;
    	if (isStateful)
    		currDrone =  new StatefulDrone(startPos, "stateful", generator);
    	else
    		currDrone =  new StatelessDrone(startPos, "stateless", generator);
    	

    	int moves=1;
    	
    	StationsMap stationsMap = new StationsMap(mapUrl);
    	List<Point> path = new ArrayList<>();
    	path.add(Point.fromLngLat(currDrone.getPosition().getLongitude(), currDrone.getPosition().getLatitude()));
    	String logs = "";
    	while(moves <= 250 && currDrone.getPower() >= 1.25) {
    		print(moves+"th steps\n");
    		String logLine="";
    		// inspect the map, and current position
    		Position dronePosition = currDrone.getPosition();
    		logLine += dronePosition.getLatitude() + ", "+ dronePosition.getLongitude() +", ";
    		// allowable moves
    		List<Direction> possibleMoveDir = stationsMap.getPossibleMoves(dronePosition);
    		
    		// Decide
    		Direction nextDir = currDrone.Decide(possibleMoveDir, stationsMap);
    		System.out.println(nextDir);
    		// Move
    		currDrone.move(nextDir);
    		//Charge
    		currDrone.charge(stationsMap);
    		
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
    	
    	String mapSource = stationsMap.getMapSource();
    	
    	
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
