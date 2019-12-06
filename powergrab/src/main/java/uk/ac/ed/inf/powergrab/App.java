package uk.ac.ed.inf.powergrab;

import java.util.List;
import java.util.ArrayList;
import com.mapbox.geojson.Point;
/**
 * The Class App contains the main process that runs and controls  the drones through their APIs
 * It also instantiate the map objects and calls functions that save files.
 */
public class App 
{
	/** The Constants of power consumed per move and maximum number of allowed moves*/
	static final float POWER_COST_PER_MOVE = -1.25f;
	static final int MAX_NUMBER_OF_MOVES = 250;
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main( String[] args )
    {
    	/** parses the command line input and initialises all required variables with appropriate representation */
    	CommandArgsParser  arguments = new CommandArgsParser(args);
    	StationsMap.MapDate mapDate = arguments.getMapDate();
    	Position startPos = arguments.getStartCorridinate();
    	int generator = arguments.getGenerator();
    	boolean isStateful = arguments.isStateful();
    	
    	// initialises and download the map of stations 
    	String mapUrl = StationsMap.formatMapUrl(mapDate, "", "");
    	StationsMap stationsMap = new StationsMap(mapUrl, mapDate);
    	
    	// instantiate a drone depending on type passed in the command arguments 
    	Drone currDrone;
    	if (isStateful)
    		currDrone =  new StatefulDrone(startPos, arguments.getDroneType(), generator);
    	else
    		currDrone =  new StatelessDrone(startPos, arguments.getDroneType(), generator);
    	
    	// list point to hold path the drone takes
    	List<Point> path = new ArrayList<>();
    	path.add(Point.fromLngLat(
    			currDrone.getPosition().getLongitude(), 
    			currDrone.getPosition().getLatitude())
    			);
    	// variable to hold the moves a drone makes and number of gains at each step
    	String logs = "";
    	int moves=1;
    	while(moves <= MAX_NUMBER_OF_MOVES && currDrone.getPower() >= POWER_COST_PER_MOVE) {
    		String movesLogLine="";
    		
    		// inspect the map and get current position and legal moves
    		Position dronePosition = currDrone.getPosition();
    		movesLogLine += dronePosition.getLatitude() + ", "+ dronePosition.getLongitude() +", ";
    		List<Direction> possibleMoveDir = currDrone.inspectDirections(stationsMap);
    		if(possibleMoveDir.size() == 0) // only happen if a only is initialised out of the range position
    			break;
    		// Decide
    		Direction nextDir = currDrone.Decide(possibleMoveDir, stationsMap);
    		// Move
    		currDrone.move(nextDir);
    		currDrone.addPower(POWER_COST_PER_MOVE); 
    		++ moves;
    		//Charge
    		currDrone.charge(stationsMap);
    		path.add(Point.fromLngLat(
    				currDrone.getPosition().getLongitude(), 
    				currDrone.getPosition().getLatitude())
    				);
    		movesLogLine +=nextDir + ", "
    				+dronePosition.getLatitude() + ", "
    				+ dronePosition.getLongitude() +", "
    				+currDrone.getPowerCoins()+", "
    				+currDrone.getPower();
    		logs +=movesLogLine+"\n";
    		
    	}
    	// merges the drone path to the original map source then save them geoJSO files and .txt file for the path logs
    	String finalGeoJsonMap = StationsMap.mergePathToMap(stationsMap, path);
    	String filename = IOUtils.generateFileName(currDrone, stationsMap.mapDate);
    	IOUtils.saveJson(finalGeoJsonMap, filename);
    	IOUtils.saveLogs(logs, filename);
   
    }
}
