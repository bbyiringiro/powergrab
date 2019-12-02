package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

// TODO: Auto-generated Javadoc
/**
 * The Class StationsMap.
 */
public class StationsMap {
	
	/** The stations. */
	private Map<String, Station> stations;
	
	/** The map source. */
	final private String mapSource;
	
	/** The map date. */
	final MapDate mapDate;
	
	/**
	 * The Class Station.
	 */
	static public class Station {
		
		/** The id. */
		final private String id;
		
		/** The position. */
		final private Position position;
		
		/** The coins. */
		private float coins;
		
		/** The power. */
		private float power;
		
		/**
		 * Instantiates a new station.
		 *
		 * @param id the id
		 * @param coins the coins
		 * @param power the power
		 * @param position the position
		 */
		public Station(String id, float coins, float power, Position position) {
			this.id = id;
			this.setCoins(coins);
			this.setPower(power);
			this.position = position;
		}
		
		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public String getId() {
			return id;
		}
		
		/**
		 * Gets the coins.
		 *
		 * @return the coins
		 */
		public float getCoins() {
			return coins;
		}
		
		/**
		 * Sets the coins.
		 *
		 * @param coins the new coins
		 */
		public void setCoins(float coins) {
			this.coins = coins;
		}
		
		/**
		 * Gets the power.
		 *
		 * @return the power
		 */
		public float getPower() {
			return power;
		}
		
		/**
		 * Sets the power.
		 *
		 * @param power the new power
		 */
		public void setPower(float power) {
			this.power = power;
		}
		
		/**
		 * Gets the position.
		 *
		 * @return the position
		 */
		public Position getPosition() {
			return position;
		}

	}
	
	
	/**
	 * The Class MapDate.
	 */
	static public class MapDate {
		
		/** The day. */
		final private String day;
		
		/** The month. */
		final private String month;
		
		/** The year. */
		final private String year;
		
		/**
		 * Instantiates a new map date.
		 *
		 * @param d the d
		 * @param m the m
		 * @param y the y
		 */
		public MapDate(String d,String m, String y)
		{
			day = d;
			month = m;
			year = y;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Date [day=" + day + ", month=" + month + ", year=" + year + "]";
		}
		
		/**
		 * Format date.
		 *
		 * @param delimiter the delimiter
		 * @param reverse the reverse
		 * @return the string
		 */
		public String formatDate(String delimiter, boolean reverse)
		{
			if (delimiter.equals(""))
				delimiter ="/";
			if(reverse)
				return year+delimiter+month+delimiter+day;
			else
				return day+delimiter+month+delimiter+year;
		}

	}

	/**
	 * Instantiates a new stations map.
	 *
	 * @param mapUrl the map url
	 * @param date the date
	 */
	public StationsMap(String mapUrl, MapDate date) {
		mapDate = date;
		mapSource = loadMap(mapUrl);
	}
	
	/**
	 * Load map.
	 *
	 * @param mapUrl the map url
	 * @return the string
	 */
	private String loadMap(String mapUrl) {
		stations = new HashMap<>();
		String stringifiedMap = "";
    	FeatureCollection fc;
    	try {
    		stringifiedMap = downlaodMap(mapUrl);
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
			stations.put(station_id, station);
		}
		return stringifiedMap;
		
	}
	

	
	/**
	 * Gets the stations with in.
	 *
	 * @param dronePosition the drone position
	 * @param range the range
	 * @return the stations with in
	 */
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
	
	 /**
 	 * Gets the stations with in.
 	 *
 	 * @param stations the stations
 	 * @param dronePosition the drone position
 	 * @param range the range
 	 * @return the stations with in
 	 */
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
	
	/**
	 * Gets the station by id.
	 *
	 * @param stationId the station id
	 * @return the station by id
	 */
	public Station getStationById(String stationId) {
		if(stations.containsKey(stationId))
			return stations.get(stationId);
		else
			return null;
	}
	
	/**
	 * Gets the closest station.
	 *
	 * @param position the position
	 * @return the closest station
	 */
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
	
	
	
	/**
	 * Gets the closest station.
	 *
	 * @param stations the stations
	 * @param position the position
	 * @return the closest station
	 */
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
	
	/**
	 * Gets the map source.
	 *
	 * @return the map source
	 */
	public String getMapSource() {
		return mapSource;
	}
	
	
	/**
	 * Gets the all stations.
	 *
	 * @return the all stations
	 */
	public List<String> getAllStations(){
		return new ArrayList<String>(stations.keySet());
	}
	
	/**
	 * Gets the possible moves.
	 *
	 * @param position the position
	 * @return the possible moves
	 */
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
	
	/**
	 * Gets the direction to.
	 *
	 * @param from the from
	 * @param to the to
	 * @param directions the directions
	 * @return the direction to
	 */
	public static Direction getDirectionTo(Position from, Position to, List<Direction> directions) {
		
		Direction bestDirection = directions.get(0);
		double distance = calcDistance(from.nextPosition(bestDirection), to);
		
		for(Direction dir: directions) {
			double tempDistance = StationsMap.calcDistance(from.nextPosition(dir), to);
			if(tempDistance < distance) {
				bestDirection = dir;
				distance = tempDistance;
			}
		}
		
		return bestDirection;
	}
	
	/**
	 * Calc distance.
	 *
	 * @param pos1 the pos 1
	 * @param pos2 the pos 2
	 * @return the double
	 */
	public static double calcDistance(Position pos1, Position pos2) {
		double x2 = Math.pow(pos2.getLatitude() - pos1.getLatitude(), 2);
		double y2 = Math.pow(pos2.getLongitude() - pos1.getLongitude(), 2);
		return Math.sqrt(x2+y2);
	}
	
	/**
	 * Downlaod map.
	 *
	 * @param url the url
	 * @return the string
	 * @throws MalformedURLException the malformed URL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String downlaodMap(String url) throws MalformedURLException, IOException {
		
		InputStream inputStream;
//		try 
//		{
			URL mapUrl = new URL(url);
			HttpURLConnection request = (HttpURLConnection) mapUrl.openConnection();
			request.setReadTimeout(10000); // milliseconds
			request.setConnectTimeout(15000); // milliseconds
			request.setRequestMethod("GET");
			request.setDoInput(true);
			request.connect();
//			try
			inputStream = request.getInputStream();
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader(inputStream));
			
			String mapSource = root.toString();
			inputStream.close();
//			catch
			
			
			
			return mapSource;
//		}
//		catch(MalformedURLException e) 
//		{
//			
//		}
//		catch (IOException e) {
//			// TODO: handle exception
//		}
//		finally 
//		{
//			
//		}
		
	}
	
	
	


}
