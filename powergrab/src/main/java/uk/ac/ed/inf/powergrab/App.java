package uk.ac.ed.inf.powergrab;
import java.util.List;
import java.util.ArrayList;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.LineString;



// TODO: Auto-generated Javadoc
/**
 * The Class App.
 */
public class App 
{
	
	/** The Constant POWER_COST_PER_MOVE. */
	static final float POWER_COST_PER_MOVE = -1.25f;
	
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main( String[] args )
    {
    	CommandArgsParser  arguments = new CommandArgsParser(args);
    	
    	StationsMap.MapDate mapDate = arguments.getMapDate();
    	Position startPos = arguments.getStartCorridinate();
    	int generator = arguments.getGenerator();
    	boolean isStateful = arguments.isStateful();
    	String mapUrl = HelperFunctions.formatUrl(mapDate, "", "");
  
    	StationsMap stationsMap = new StationsMap(mapUrl, mapDate);
    	Drone currDrone;
    	if (isStateful)
    		currDrone =  new StatefulDrone(startPos, arguments.getDroneType(), generator);
    	else
    		currDrone =  new StatelessDrone(startPos, arguments.getDroneType(), generator);
    	
    	
    	List<Point> path = new ArrayList<>();
    	int moves=1;
    	path.add(Point.fromLngLat(currDrone.getPosition().getLongitude(), currDrone.getPosition().getLatitude()));
    	String logs = "";
    	while(moves <= 250 && currDrone.getPower() >= POWER_COST_PER_MOVE) {
    		System.out.println(moves+"th steps\n");
    		String logLine="";
    		// inspect the map, and current position
    		Position dronePosition = currDrone.getPosition();
    		logLine += dronePosition.getLatitude() + ", "+ dronePosition.getLongitude() +", ";
    		// allowable moves
    		List<Direction> possibleMoveDir = currDrone.inspectDirections(stationsMap);
    		if(possibleMoveDir.size() == 0)
    			break;
    		
    		// Decide
    		Direction nextDir = currDrone.Decide(possibleMoveDir, stationsMap);
    		System.out.println(nextDir);
    		// Move
    		currDrone.move(nextDir);
    		currDrone.addPower(POWER_COST_PER_MOVE); 
    		++ moves;
    		//Charge
    		currDrone.charge(stationsMap);
    		
    		
    		
    		
    		
    		
    		path.add(Point.fromLngLat(currDrone.getPosition().getLongitude(), currDrone.getPosition().getLatitude()));
    		logLine +=nextDir + ", " +dronePosition.getLatitude() + ", "+ dronePosition.getLongitude() +", "+currDrone.getPowerCoins()+", "+currDrone.getPower();
    		logs +=logLine+"\n";
    		
    		
    		
    		System.out.println("----------------------------------------------------------------------------------------------\n");
    		
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
    	
    	String filename = currDrone.getType() +"-"+mapDate.formatDate("-", false);
    	HelperFunctions.saveJson(mapSource, filename);
    	HelperFunctions.saveLogs(logs, filename);
    	

    	
    	
    }
}
