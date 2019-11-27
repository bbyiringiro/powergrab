package uk.ac.ed.inf.powergrab;
import java.util.Random;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import java.util.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

abstract class Drone {
	double power;
	double powerCoin;
	String type;
	Position currentPos;
	protected  Random rnd;
	protected ArrayList<Feature> mapFeatures;
	
	
	public Drone(Position pos, int seed, String type) {
		currentPos = pos;
		this.type = type;
		rnd = new Random(seed);
		power = 250.0;
		powerCoin = 0.0;
		
	}
	
	abstract public Direction Decide(List<Direction> possibleDirection, StationsMap stationsMap);
	abstract protected double evaluateUtility(Station station);
	
	public double getPower() {
		return power;
	}
	public double getPowerCoins() {
		return powerCoin;
	}
	
	public String getTyep() {
		return type;
	}
	public void consumedPower(double p) {
		
		double temp = power-p;
		if (temp <0)
			power = 0;
		else
			power -= p;

	}
	public Position getPosition() {
		return currentPos;
	}
	
	public Position move(Direction to) {
		Position nextPos = currentPos.nextPosition(to);
		currentPos = nextPos;
		return currentPos;	
	}
	
	public void charge(StationsMap stationsMap) {
		String sId = stationsMap.getClosestStation(this.getPosition());
		
		double distance = StationsMap.calcDistance(this.getPosition(), stationsMap.getStationById(sId).getPosition());
//		System.out.println(currentPos.toString());
//		System.out.println(closestStation.geometry().toString());
//		System.out.println(distance);
		
		if(distance >= 0.00025)
			return;
		double station_power = stationsMap.getStationById(sId).getPower();
		double station_coins = stationsMap.getStationById(sId).getCoins();
		System.out.println("Station -> Coins: "+station_coins+" power: "+station_power);
		System.out.println("Before Drone -> Coins: "+powerCoin+" power: "+power);
		// at the station now
		if(station_coins < 0) {
			if ((power + station_power) < 0)
				stationsMap.getStationById(sId).setPower(this.power + station_power);
			else
				stationsMap.getStationById(sId).setPower(0);
			
			if ((powerCoin + station_coins) < 0)
				stationsMap.getStationById(sId).setCoins(this.powerCoin + station_coins);
			else
				stationsMap.getStationById(sId).setCoins(0);
		}
		else {
			stationsMap.getStationById(sId).setCoins(0);
			stationsMap.getStationById(sId).setPower(0);
			
		}
		
		addPower(station_power);
		addPowerCoins(station_coins);
		System.out.println("After Drone -> Coins: "+powerCoin+" power: "+power);
		System.out.println("charged");
		System.out.println("After Station -> Coins: "+stationsMap.getStationById(sId).getCoins()+" power: "+stationsMap.getStationById(sId).getPower());
	}
	
	protected int ClosestStation() {
		double tempDistance;
		int closest_idx = 0;
		double closestDistance  = calcDistance( (Point) mapFeatures.get(closest_idx).geometry());
		for(int i=1; i<mapFeatures.size();  ++i) {
			tempDistance = calcDistance((Point)mapFeatures.get(i).geometry());
			if( tempDistance < closestDistance) {
				closestDistance = tempDistance;
				closest_idx = i;
			}
				
		}
		
		return closest_idx;
	}
	
	
	protected void addPowerCoins(double coins) {
		double temp = powerCoin + coins;
		if (temp<0)
			powerCoin = 0;
		else
			powerCoin = temp;
	}
	
	protected void addPower(double p) {
		double temp = power + p;
		if (temp<0)
			power = 0;
		else
			power = temp;
	}

	public double calcDistance(Point point) {
		double x2 = Math.pow(point.latitude() - currentPos.getLatitude(), 2);
		double y2 = Math.pow(point.longitude() - currentPos.getLongitude(), 2);
		return Math.sqrt(x2+y2);
	}
	
	public double calcDistance(Position pos, Point point) {
		double x2 = Math.pow(point.latitude() - pos.getLatitude(), 2);
		double y2 = Math.pow(point.longitude() - pos.getLongitude(), 2);
		return Math.sqrt(x2+y2);
	}

	public String loadMap(String mapUrl) {
		// TODO Auto-generated method stub
		FeatureCollection fc;
		String mapSource="";
    
    	try {
			mapSource = GeoJsonHandler.readJsonFromURL(mapUrl);
			fc  = FeatureCollection.fromJson(mapSource);
			mapFeatures =(ArrayList<Feature>) fc.features();
//			print(features.get(0).properties().toString());
//			Point p = (Point) features.get(0).geometry();
//			System.out.print(p.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return mapSource;
		
	}
	
	
	
	
	
	
	
	
}




//logs
// I changed my positon class to invalid positions to avoid using sets and make this implementation easier
