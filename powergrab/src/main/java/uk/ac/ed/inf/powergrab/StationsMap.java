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
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/**
 * The Class StationsMap.
 * represents a map that holds the position of all stations and stores their states as well as the position of drone. 
 * It handles all functions related to calculating distances, 
 * locations of stations, directions, downloading and merging maps among others.
 */
public class StationsMap {
	
	/** The stations, (key->values = StationId->Station) */
	private Map<String, Station> stations;
	
	/** The map source. */
	final private String mapSource;
	
	/** The map date. */
	final MapDate mapDate;
	
	/** The default MAP_BASE_URL. */
	final static String MAP_BASE_URL = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
	
	/** The default filename of the geoJson map. */
	final static String MAP_FILE_NAME = "powergrabmap.geojson";
	
	/**
	 * The Inner Class Station.
	 * Holds information about the stations in the map
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
		 * @param day the day
		 * @param month the month
		 * @param year the year
		 */
		public MapDate(String day,String month, String year)
		{
			this.day = day;
			this.month = month;
			this.year = year;
		}
		
		/* 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Date [day=" + day + ", month=" + month + ", year=" + year + "]";
		}
		
		/**
		 * Format date. either in reverse format and/or with a particular delimiters
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
	 * @param mapUrl the map URL
	 * @param date the date
	 */
	public StationsMap(String mapUrl, MapDate date) {
		mapDate = date;
		mapSource = loadMap(mapUrl);
	}
	
	/**
	 *  downloads the map and initialises stations member of StationsMap object
	 *
	 * @param mapUrl the map URL
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
	 * return all stations ids within a given range from that particular position
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
 	 * an overload functions that return stations ids of all stations
 	 *  in given station list within a particular range from a given position
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
	 * return a Station object given its id.
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
	 * Gets the closest station in all stations from at particular position.
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
	 * Gets the closest station in given station from a particular position
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
	 * Gets the original string of map.
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
	 *return the best directions that that takes one from ‘from’ position to ‘to’ position.
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
	 * calculate the distance between position distance.
	 *
	 * @param pos1 the position 1
	 * @param pos2 the position 2
	 * @return the double
	 */
	public static double calcDistance(Position pos1, Position pos2) {
		double x2 = Math.pow(pos2.getLatitude() - pos1.getLatitude(), 2);
		double y2 = Math.pow(pos2.getLongitude() - pos1.getLongitude(), 2);
		return Math.sqrt(x2+y2);
	}
	
	/**
	 * given a URL it return json string of the map on the url
	 *
	 * @param url the url
	 * @return the string
	 * @throws MalformedURLException the malformed URL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String downlaodMap(String url) throws MalformedURLException, IOException {
		
		InputStream inputStream;
		URL mapUrl = new URL(url);
		HttpURLConnection request = (HttpURLConnection) mapUrl.openConnection();
		request.setReadTimeout(10000); 
		request.setConnectTimeout(15000);
		request.setRequestMethod("GET");
		request.setDoInput(true);
		request.connect();
		inputStream = request.getInputStream();
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader(inputStream));
		
		String mapSource = root.toString();
		inputStream.close();
		return mapSource;
		
	}
	
	
	/**
	 *  format the URL on which to download the map
	 *
	 * @param mapDate the map date
	 * @param baseUrl the base url
	 * @param geoJsonFile the geo json file
	 * @return the string
	 */
	public static String formatMapUrl(StationsMap.MapDate mapDate, String baseUrl, String geoJsonFile) {
		String mapDateIndex;
		//not given URL, it uses the default
		if(baseUrl.equals("")) {
			baseUrl = MAP_BASE_URL;
		}
		if(geoJsonFile.equals("")) {
			geoJsonFile = MAP_FILE_NAME;
		}
		
		if (mapDate == null) {
			mapDateIndex = "";
		}
		else
			mapDateIndex =  mapDate.formatDate("", true)+"/";
		
		return baseUrl +mapDateIndex+geoJsonFile;
	}
	
	/**
	 * Merge path to map.
	 *takes geoJson and list of points and create a LineString and adds it to the geoJson of the map
	 * @param stationsMap the stations map
	 * @param path the path
	 * @return the string
	 */
	public static String mergePathToMap(StationsMap stationsMap, List<Point> path) {
		LineString pathLineStrign = LineString.fromLngLats(path);
    	Feature outFeature = Feature.fromGeometry(pathLineStrign);
    	FeatureCollection fc = FeatureCollection.fromJson(stationsMap.getMapSource());
    	List<Feature> features = (ArrayList<Feature>) fc.features();
    	features.add(outFeature);
    	FeatureCollection out = FeatureCollection.fromFeatures(features);
    	return out.toJson();
	}
	
	
	


}
