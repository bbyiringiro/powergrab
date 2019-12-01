package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class StationsMap {
	public Map<String, Station> stationsMap;
	final private String mapSource;

	public StationsMap(String mapUrl) {
		mapSource = loadMap(mapUrl);
	}
	
	private String loadMap(String mapUrl) {
		stationsMap = new HashMap<>();
		String stringifiedMap = "";
    	FeatureCollection fc;
    	try {
    		stringifiedMap = GeoJsonHandler.readJsonFromURL(mapUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	fc  = FeatureCollection.fromJson(stringifiedMap);
		for(Feature f: fc.features()) {
			String station_id = f.getStringProperty("id");
			Point point = (Point) f.geometry();
			Position position = new Position(point.latitude(), point.longitude());
			Station station = new Station(station_id, f.properties().get("coins").getAsFloat(), f.properties().get("power").getAsFloat(), position);
			stationsMap.put(station_id, station);
		}
		return stringifiedMap;
		
	}
	

	
	public List<String> getStationsWithIn(Position dronePosition, double range){
		double tempDistance;
		List<String> stationsInRangeIds = new ArrayList<>();
		for(String stationId: getAllStations()) {
			Station tempStation = getStationById(stationId);
			tempDistance = calcDistance(dronePosition, tempStation.getPosition());
			if( tempDistance < range)
				stationsInRangeIds.add(stationId);
		}
		return stationsInRangeIds;
	}
	
	 public List<String> getStationsWithIn(List<String> stations,  Position dronePosition, double range){
		double tempDistance;
		List<String> stationsInRangeIds = new ArrayList<>();
		for(String stationId: stations) {
			Station tempStation = getStationById(stationId);
			tempDistance = calcDistance(dronePosition, tempStation.getPosition());
			if( tempDistance < range)
				stationsInRangeIds.add(stationId);
		}
		return stationsInRangeIds;
	}
	
	public Station getStationById(String stationId) {
		if(stationsMap.containsKey(stationId))
			return stationsMap.get(stationId);
		else
			return null;
	}
	
	public String getClosestStation(Position position)
	{
		double closestDist = Double.MAX_VALUE;
		String closestSId="";
		for(String sId: getAllStations()) {
			Station tempStation = getStationById(sId);
			double tempDistance = calcDistance(position, tempStation.getPosition());
			if(tempDistance < closestDist) {
				closestDist = tempDistance;
				closestSId = sId;
			}
			
			
		}
		
		return closestSId;
	}
	
	
	
	public String getClosestStation(List<String> stations, Position position)
	{
		double closestDist = Double.MAX_VALUE;
		String closestSId="";
		for(String sId: stations) {
			Station tempStation = getStationById(sId);
			double tempDistance = calcDistance(position, tempStation.getPosition());
			if(tempDistance < closestDist) {
				closestDist = tempDistance;
				closestSId = sId;
			}
			
			
		}
		
		return closestSId;
	}
	
	public String getMapSource() {
		return mapSource;
	}
	
	
	public Set<String> getAllStations(){
		return stationsMap.keySet();
	}
	
	public List<Direction> getPossibleMoves(Position position) {
		List<Direction> moves = new ArrayList<>();
		for(int i=0; i < Direction.values().length; ++i) {
			Direction dirMove = Direction.values()[i];
			Position pos = position.nextPosition(dirMove);
			if (pos.inPlayArea()) 
				moves.add(dirMove);
		}	
		return moves;
	}
	
	public Direction getCloserDirectionTo(String fromStationID, String toStationId, List<Direction> possibleDirections) {
		Direction nextDir;
		Position fromStation = getStationById(fromStationID).getPosition();
		Position toStation = getStationById(toStationId).getPosition();
		nextDir = possibleDirections.get(0);
		double minDist = StationsMap.calcDistance(fromStation.nextPosition(nextDir), toStation);
		for(Direction dir: possibleDirections) {
			double tempDist = StationsMap.calcDistance(fromStation.nextPosition(dir), toStation);
			if(tempDist < minDist) {
				nextDir = dir;
				minDist = tempDist;
			}
		
		}
		return nextDir;
		
		
	}
	
	static public double calcDistance(Position pos1, Position pos2) {
		double x2 = Math.pow(pos2.getLatitude() - pos1.getLatitude(), 2);
		double y2 = Math.pow(pos2.getLongitude() - pos1.getLongitude(), 2);
		return Math.sqrt(x2+y2);
	}


}
