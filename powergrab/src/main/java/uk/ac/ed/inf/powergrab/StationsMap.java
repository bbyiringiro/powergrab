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
	public HashMap<String, Station> stationsMap;
	
	public StationsMap(String mapUrl) {
		loadMap(mapUrl);
	}
	
	private void loadMap(String mapUrl) {
		stationsMap = new HashMap<>();
    	FeatureCollection fc;
		String mapSource="";
    	try {
			mapSource = GeoJsonHandler.readJsonFromURL(mapUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	fc  = FeatureCollection.fromJson(mapSource);
		for(Feature f: fc.features()) {
			String station_id = f.getStringProperty("id");
			Point point = (Point) f.geometry();
			Position position = new Position(point.latitude(), point.longitude());
			Station station = new Station(station_id, f.properties().get("coins").getAsDouble(), f.properties().get("power").getAsDouble(), position);
			stationsMap.put(station_id, station);
		}

		
	}
	
	public List<String> getStationWithIn(Position dronePosition, double range){
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
	
	
	
	public String getClosestStation(Set<String> stations, Position position)
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
	
	static public double calcDistance(Position pos1, Position pos2) {
		double x2 = Math.pow(pos2.getLatitude() - pos1.getLatitude(), 2);
		double y2 = Math.pow(pos2.getLongitude() - pos1.getLongitude(), 2);
		return Math.sqrt(x2+y2);
	}


}
