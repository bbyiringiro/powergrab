package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.mapbox.geojson.Point;

import com.mapbox.geojson.Feature;

public class StatelessDrone extends Drone {
	
	public StatelessDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
	}
	

	@Override
	public Direction chooseDirection() {
		System.out.println("Scanning...");
		List<Direction> possibleMoveDir = getPossibleMoves();
		int n = possibleMoveDir.size();
		List<Integer> stations_idx = InRangestation_Idx();
		List<Integer> pos_stations = new ArrayList<>();
		List<Integer> neg_stations = new ArrayList<>();
		Direction nextDir;
		
		System.out.println(stations_idx.size());
		
		
		
		if (stations_idx.size() == 0) 
		{
			nextDir = possibleMoveDir.get(rnd.nextInt(n));
			System.out.println("Choose location randomly 0");
			return nextDir;	
		}
		
		
		if (stations_idx.size() > 0) 
			System.out.println("Before sorting "+stations_idx.get(0));
		stations_idx.sort( new Comparator<Integer>() {
			@Override
			public int compare(Integer s1, Integer s2) {
				double s1_utility = evaluateUtility(mapFeatures.get(s1));
				double s2_utility = evaluateUtility(mapFeatures.get(s2));
				if(s1_utility == s2_utility) // TODO .. set epsilon to consider two doubles to be equal
					return 0;
				return (s1_utility < s2_utility) ? 1:-1;
			}

		});
		
		for (int l=0; l<stations_idx.size(); ++l)
		{
			if(evaluateUtility(mapFeatures.get(stations_idx.get(l)))>=0)
				pos_stations.add(stations_idx.get(l));
			else
				neg_stations.add(stations_idx.get(l));
		}
		
		
		int best_closer_station = stations_idx.get(0);
		
		System.out.println("after sorting " +stations_idx.get(0));
		if (stations_idx.size() >1)
			System.out.println("second"+evaluateUtility(mapFeatures.get(stations_idx.get(1))));
		System.out.println(evaluateUtility(mapFeatures.get(stations_idx.get(0))));
		if (evaluateUtility(mapFeatures.get(stations_idx.get(0))) == 0 && stations_idx.size() ==1)
			{
			nextDir = possibleMoveDir.get(rnd.nextInt(n));
			System.out.println("Choose location randomly 1");
			
			return nextDir;	
			}
		
		else if (evaluateUtility(mapFeatures.get(stations_idx.get(0))) <= 0) {
			List<Direction> copyOfPossibleMoveDir = new ArrayList<>(possibleMoveDir);
			System.out.println("all stations in range are either negative or zero in utility");
			System.out.println(copyOfPossibleMoveDir.size() +"before worst direction");
			int directiosize;
			int newSize;
			do {
				directiosize = copyOfPossibleMoveDir.size();
				for(int k=0; k<stations_idx.size(); ++k) {
				 copyOfPossibleMoveDir.remove(moveAwayDirections(copyOfPossibleMoveDir, stations_idx.get(k)));
				}
				newSize = copyOfPossibleMoveDir.size();
			 }while((newSize != directiosize) && newSize>0);
			
			System.out.println(copyOfPossibleMoveDir.size() +"remove worst directions");
			n = copyOfPossibleMoveDir.size();
			// handling the case when for all possible directions there is a negative station 
			
			if (n==0) { // rare but in case all direction goes to negative stations
				// get closer to the least negative utility station
				System.out.println("negative stations in all directions");
				nextDir = closerDirection(possibleMoveDir, stations_idx.get(0));
				System.out.println("Choose location randomly 4");
				return nextDir;
			}
			nextDir = copyOfPossibleMoveDir.get(rnd.nextInt(n));
			System.out.println("Choose location randomly 3");
			
			System.out.println(nextDir);
			return nextDir;	
		}
		else {
			nextDir = closerDirection (possibleMoveDir, best_closer_station);
			System.out.println("Choose wisely 3");
			return nextDir;
		}
		
	}
	
	public Direction closerDirection(List<Direction> directions, int station_idx) {
		
		// choosing the direction that a give me closor to a point;
		int n = directions.size();
		int tempDirIndex= rnd.nextInt(n);
		Direction nextDir = directions.get(tempDirIndex);

//				System.out.println(nextDir);
				double tempDistance;
				double minDist = calcDistance(currentPos.nextPosition(directions.get(tempDirIndex)), (Point) mapFeatures.get(station_idx).geometry());
//				System.out.println(minDist);
				for (int j=0; j<n; ++j) {
					tempDistance = calcDistance(currentPos.nextPosition(directions.get(j)), (Point) mapFeatures.get(station_idx).geometry());
					if(minDist > tempDistance) {
//						System.out.println(tempDistance);
						minDist = tempDistance;
						nextDir = directions.get(j);
					}
				}
		
		return nextDir;
	}
	
	public Direction moveAwayDirections(List<Direction> directions, int station_idx) {
		
		// choosing the direction that a give me closor to a point;
		int n = directions.size();

//				System.out.println(nextDir);
				double tempDistance;
//				System.out.println(minDist);
				for (int j=0; j<n; ++j) {
					tempDistance = calcDistance(currentPos.nextPosition(directions.get(j)), (Point) mapFeatures.get(station_idx).geometry());
					if(tempDistance <= 0.00025) {
						return directions.get(j);
					}
				}
		
		return null;
	}
	
	
	
	private double evaluateUtility(Feature station) {
		
//		Feature 1: 100 coins, 2 units of power
//
//		Feature 2: 50 coins, 50 units of power
//		
//		This is a decision which has been left to you to make as part of designing your drone's strategy.  Try to make an argument in favour of Feature 1, and then try to make an argument in favour of Feature 2.  Might the answer depend on the state of the drone?
		
		
		return station.properties().get("coins").getAsDouble() + station.properties().get("power").getAsDouble();
	}
	
	private List<Integer> InRangestation_Idx(){
		double tempDistance;
		List<Integer> withInRangeStation = new ArrayList<>();
		for(int i=0; i<mapFeatures.size();  ++i) {
			tempDistance = calcDistance((Point) mapFeatures.get(i).geometry());
			if( tempDistance < 0.0003)
				withInRangeStation.add(i);
		}
		return withInRangeStation;
	}
	
	


}
