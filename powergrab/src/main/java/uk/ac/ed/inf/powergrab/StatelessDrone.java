package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatelessDrone extends Drone {
	
	public StatelessDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
	}
	
	
	@Override
	public Direction Decide(List<Direction> possibleDirections, StationsMap stationsMap) {
		Direction nextDir = null;
		double maxUtility = 0.00001;
		Direction   leastCostlyDir = null;
		double leastCost = Double.MAX_VALUE;
		List<String> stationsInRange;
		List<Direction> negDirections = new ArrayList<>();
		for(int i=0; i<possibleDirections.size(); ++i) {
			Direction   dir = possibleDirections.get(i);
			Position nextPosition = currentPos.nextPosition(dir);
			stationsInRange  = stationsMap.getStationsWithIn(nextPosition, 0.00025);
			if(stationsInRange.size() == 0)continue;
			
			System.out.println(stationsInRange.size());
			
			
			String closestSId = stationsMap.getClosestStation(stationsInRange, nextPosition);
			Station closestStation  = stationsMap.getStationById(closestSId);
			double tempUtility = evaluateUtility(closestStation);
			if(tempUtility < 0){
				negDirections.add(dir);
				
				if(tempUtility < leastCost) {
					leastCostlyDir = dir;
					leastCost = tempUtility;
					
				}
			}
			else if(tempUtility > maxUtility) {
				nextDir = dir;
				maxUtility = tempUtility;
			}
			
		}
		
		
		for(Direction delDir: negDirections) 
			possibleDirections.remove(delDir);
		
		if(nextDir != null) {
			System.out.println(maxUtility);
			System.out.println("found best to be this "+nextDir);
			return nextDir;
		}else {
			
			int n = possibleDirections.size();
			if(n==0) {
				System.out.println("trapped the least I can do is " + leastCost);
				return leastCostlyDir;
			}
			else {
				System.out.println("Possibility of  "+n+" in 16");
				return possibleDirections.get(rnd.nextInt(n));
			}
			
		}

	}

	// avoid zero devision
	protected double evaluateUtility(Station station) {
		return station.getCoins();
	}
	
	


}
