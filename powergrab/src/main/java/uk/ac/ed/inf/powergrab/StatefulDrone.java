package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class StatefulDrone extends Drone {
	
	public ArrayList<Integer> positiveStation;
	public ArrayList<Integer> negativeStation;
	private ArrayList<Integer> visitedPositiveStation;
	public StatefulDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
	}
	
	
	public Direction moveAwayDirections(List<Direction> directions, int station_idx) {
		
		// choosing the direction that a give me closor to a point;
		int n = directions.size();

//				System.out.println(nextDir);
				double tempDistance;
//				System.out.println(minDist);
				for (int j=0; j<n; ++j) {
					tempDistance = calcDistance(currentPos.nextPosition(directions.get(j)), (Point) mapFeatures.get(station_idx).geometry());
					if(tempDistance < 0.00025) {
						return directions.get(j);
					}
				}
		
		return null;
	}
	
	public List<Integer> getNegativeStationNearBy(){
		double tempDistance;
		List<Integer> withInRangeStation = new ArrayList<>();
		for(int i=0; i<negativeStation.size();  ++i) {
			tempDistance = calcDistance(currentPos, (Point) mapFeatures.get(negativeStation.get(i)).geometry());
			if( tempDistance < 0.0055)
				withInRangeStation.add(negativeStation.get(i));
		}
		return withInRangeStation;
	}

	@Override
	public Direction chooseDirection() {
		List<Direction> possibleMoveDir = getPossibleMoves();
		Direction nextDir;
		
		List<Integer> neg_stations_idx = getNegativeStationNearBy();
		List<Direction> copyOfPossibleMoveDir = new ArrayList<>(possibleMoveDir);
		int directionsize;
		int newSize;
		do {
			directionsize = possibleMoveDir.size();
			for(int k=0; k<neg_stations_idx.size(); ++k) {
				possibleMoveDir.remove(moveAwayDirections(possibleMoveDir, neg_stations_idx.get(k)));
			}
			newSize = possibleMoveDir.size();
		 }while((newSize != directionsize) && newSize>0);
		
		
		int station_idx = nextToVisit();
		
		if(station_idx==-1) {
			return possibleMoveDir.get(rnd.nextInt(possibleMoveDir.size()));
		}
		
		possibleMoveDir.sort( new Comparator<Direction>() {
			@Override
			public int compare(Direction d1, Direction d2) {
				double d_to_dest1 = calcDistance(currentPos.nextPosition(d1), (Point) mapFeatures.get(station_idx).geometry());
				double d_to_dest2 = calcDistance(currentPos.nextPosition(d2), (Point) mapFeatures.get(station_idx).geometry());
				if(d_to_dest1 == d_to_dest2) // TODO .. set epsilon to consider two doubles to be equal
					return 0;
				return (d_to_dest1 > d_to_dest2) ? 1:-1;
			}

		});
		
		return possibleMoveDir.get(rnd.nextInt(2));	
	}
	
//	public Direction getCloserDir(List<Direction> directions, int station_idx) {
//		
//		// choosing the direction that a give me closor to a point;
//		int n = directions.size();
//		Direction nextDir = directions.get(rnd.nextInt(n));
////				System.out.println(nextDir);
//				double minDist = calcDistance(currentPos.nextPosition(nextDir), (Point) mapFeatures.get(station_idx).geometry());;
//				double tempDistance;
////				System.out.println(minDist);
//				for (int j=0; j<n; ++j) {
//					tempDistance = calcDistance(currentPos.nextPosition(directions.get(j)), (Point) mapFeatures.get(station_idx).geometry());
//					if(tempDistance < minDist) {
//						nextDir = directions.get(j);
//						minDist = tempDistance;
//					}
//				}
//		
//		return nextDir;
//	}
	
	
	
	@Override
	public String loadMap(String mapUrl) {
		
		// TODO Auto-generated method stub
		FeatureCollection fc;
		String mapSource="";
		
		positiveStation = new ArrayList<>();
		negativeStation = new ArrayList<>();
		visitedPositiveStation = new ArrayList<>();
		
    	
    	try {
			mapSource = GeoJsonHandler.readJsonFromURL(mapUrl);
			fc  = FeatureCollection.fromJson(mapSource);
			mapFeatures =(ArrayList<Feature>) fc.features();
			
			for(int i=0; i<mapFeatures.size(); ++i) {
				if(mapFeatures.get(i).properties().get("coins").getAsDouble() >= 0) {
					positiveStation.add(i);
					
				}else { 
					negativeStation.add(i);
					}
			}
					
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return mapSource;
		
	}
	
	
	
	public int nextToVisit() {
		
		double tempDistance;
		if (positiveStation.size()==0) 
			return -1;
		int closest_idx = positiveStation.get(0);
		double closestDistance  = calcDistance( (Point) mapFeatures.get(positiveStation.get(0)).geometry());
		for(int i=1; i<positiveStation.size();  ++i) {
			tempDistance = calcDistance((Point)mapFeatures.get(positiveStation.get(i)).geometry());
			if( tempDistance < closestDistance) {
				closestDistance = tempDistance;
				closest_idx = positiveStation.get(i);
			}
				
		}
		
		
		return closest_idx;
		
	}
	
	
	
	
	
	
  public void charge() {
		
		
		int closestStation = ClosestStation();
		double distance = calcDistance( (Point) mapFeatures.get(closestStation).geometry());
//		System.out.println(currentPos.toString());
//		System.out.println(closestStation.geometry().toString());
//		System.out.println(distance);
		
		if(distance >= 0.00025)
			return;
		Feature station = mapFeatures.get(closestStation);
		
		double station_power = station.properties().get("power").getAsDouble();
		double station_coins = station.properties().get("coins").getAsDouble();
		System.out.println("Station -> Coins: "+station_coins+" power: "+station_power);
		System.out.println("Before Drone -> Coins: "+powerCoin+" power: "+power);
		// at the station now
		if(station_coins < 0) {
			if ((power + station_power) < 0)
				station.addNumberProperty("power", power + station_power);
			else
				station.addNumberProperty("power", 0);
			
			if ((powerCoin + station_coins) < 0)
				station.addNumberProperty("coins", powerCoin + station_coins);
			else
				station.addNumberProperty("coins", 0);
		}
		else {
			station.addNumberProperty("coins", 0);
			station.addNumberProperty("power", 0);
			
		}
		
		addPower(station_power);
		addPowerCoins(station_coins);
		mapFeatures.set(closestStation, station);
		System.out.println("After Drone -> Coins: "+powerCoin+" power: "+power);
		System.out.println("charged");
		System.out.println("After Station -> Coins: "+station.properties().get("coins").getAsDouble()+" power: "+station.properties().get("power").getAsDouble());
		
		if(positiveStation.contains(closestStation)) {
			positiveStation.remove(positiveStation.indexOf(closestStation));
		}
		else if (negativeStation.contains(closestStation) && (station.properties().get("coins").getAsDouble() + station.properties().get("power").getAsDouble()) ==0)
			negativeStation.remove(negativeStation.indexOf(closestStation));
	}
}
