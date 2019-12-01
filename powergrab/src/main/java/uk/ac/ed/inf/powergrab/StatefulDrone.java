package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatefulDrone extends Drone {
	final int DIRECTION_MEMORY_SIZE = 4;
	private EvictingQueue<Direction> directionsMemory;
	private int target_index = 0; // default target (index of max utility station)
	private int movesCounter = 0;
	private ArrayList<Position> voidPlaces;
	
	static private class EvictingQueue<K> extends ArrayList<K> {
		private static final long serialVersionUID = -7598907934193826633L;
		private int maxSize;

	    public EvictingQueue(int size){
	        this.maxSize = size;
	    }

	    public boolean add(K k){
	        boolean r = super.add(k);
	        if (size() > maxSize){
	            removeRange(0, size() - maxSize);
	        }
	        return r;
	    }
	}
	
	public StatefulDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
		directionsMemory = new EvictingQueue<Direction>(DIRECTION_MEMORY_SIZE);
		voidPlaces = new ArrayList<>();
	}
	
	@Override
	public Direction Decide(List<Direction> possibleDirections, StationsMap stationsMap) {
		printMemory();
		movesCounter++;
		Direction nextDir;
		ArrayList<Direction> removeDirs = new ArrayList<>();
		List<String> stationsInRange = new ArrayList<>();
		List<Direction> initialDirections = new ArrayList<>(possibleDirections);
		List<String> stationsAround  = stationsMap.getStationsWithIn(this.getPosition(), 0.00055);
		List<String> stationsToVisit = scanRemainingStations(stationsMap);
		
		if(stationsToVisit.size() == 0) {
			for(int i=0; i<possibleDirections.size(); ++i) {
				Direction   dir = possibleDirections.get(i);
				Position tempNextPos = this.getPosition().nextPosition(dir);
				stationsInRange  = stationsMap.getStationsWithIn(stationsAround, tempNextPos, 0.00025); // improvement check only ones in earlier within range
				if(stationsInRange.size()==0) 
					continue;
				
				String closestSId = stationsMap.getClosestStation(stationsInRange, tempNextPos);
				Station closestStation  = stationsMap.getStationById(closestSId);
				float closestStationUtility = evaluateUtility(closestStation);
				if(closestStationUtility <0) 
					removeDirs.add(dir);
			}
			eliminateNegDirections(removeDirs, possibleDirections);
			if(possibleDirections.size() != 0)
				nextDir =possibleDirections.get(rnd.nextInt(possibleDirections.size()));
			else 
				nextDir = initialDirections.get(rnd.nextInt(initialDirections.size()));
		}else {
			
			if(cycleHappened()) {
				Position voidPlace = currentPos.nextPosition(directionsMemory.get(0));
				voidPlaces.add(voidPlace);
				voidPlaces.add(getPosition());
			}
			
			
			String targetId= getNextTarget(stationsToVisit, stationsMap,target_index);
			Station targetStation  = stationsMap.getStationById(targetId);
			Position targetPosition = targetStation.getPosition();
			
			for(int i=0; i<possibleDirections.size(); ++i) {
				Direction   dir = possibleDirections.get(i);
				
				Position tempNextPos = this.getPosition().nextPosition(dir);
				stationsInRange  = stationsMap.getStationsWithIn(stationsAround, tempNextPos, 0.00025);
				if(stationsInRange.size()==0) 
				{
					leadToCycleCheck(tempNextPos, dir, removeDirs);
					continue;
				}
				double distanceToTarget = StationsMap.calcDistance(tempNextPos, targetPosition);
				String closestSId = stationsMap.getClosestStation(stationsInRange, tempNextPos);
				Station closestStation  = stationsMap.getStationById(closestSId);
				float closestStationUtility = evaluateUtility(closestStation);
				double distanceToclosestStation = StationsMap.calcDistance(tempNextPos, closestStation.getPosition());
				if(closestStationUtility <0)
					removeDirs.add(dir);
				else if(closestStationUtility == 0 && distanceToTarget < 0.00025 && distanceToclosestStation < distanceToTarget)
					removeDirs.add(dir);
				
				leadToCycleCheck(tempNextPos, dir, removeDirs);
			}
			eliminateNegDirections(removeDirs, possibleDirections);
			if(possibleDirections.size()==0)
				nextDir = getBestDirection(getPosition(), targetPosition, initialDirections);	
			else 
				nextDir = getBestDirection(getPosition(), targetPosition, possibleDirections);
		}
		
		directionsMemory.add(nextDir);
		return nextDir;
	}
	
	private List<String> scanRemainingStations(StationsMap stationsMap){
		List<String> positiveStations = new ArrayList<>();
		Set<String> allStation = new HashSet<>(stationsMap.getAllStations());
		for(String sId: allStation){
			Station tempStation = stationsMap.getStationById(sId);
			double tempUtil = evaluateUtility(tempStation);
			if(tempUtil > 0)
			{
				positiveStations.add(sId);
			}
		}
		
		return positiveStations;
	}
	
	private void leadToCycleCheck(Position position, Direction dir, List<Direction> removeDirs) {
		for(Position pos: voidPlaces) {
			double distanceToVoid = StationsMap.calcDistance(position, pos);
			final double epsilon = 0.0001;
			if(distanceToVoid <epsilon) {
				removeDirs.add(dir);
			}
		}
	}
	
	
	private String getNextTarget(List<String> positiveStations, StationsMap stationsMap, int index) {
		positiveStations.sort( new Comparator<String>() {
			@Override
			public int compare(String id1, String id2) {
				
				double s1_utility = StationsMap.calcDistance(getPosition(), stationsMap.getStationById(id1).getPosition())
						/evaluateUtility(stationsMap.getStationById(id1));
				double s2_utility = StationsMap.calcDistance(getPosition(), stationsMap.getStationById(id2).getPosition())
						/evaluateUtility(stationsMap.getStationById(id2));
				if(s1_utility == s2_utility)
					return 0;
				return (s1_utility > s2_utility) ? 1:-1;
			}
		});
		if (positiveStations.size()==0) return "";
		
		if(positiveStations.size()>index)
			return positiveStations.get(index);
		else 
			return positiveStations.get(0);
	
	}
	
	private Direction getBestDirection(Position from, Position to, List<Direction> possibleDirections) {
		possibleDirections.sort( new Comparator<Direction>() {
			@Override
			public int compare(Direction d1, Direction d2) {
				double d_to_dest1 = StationsMap.calcDistance(from.nextPosition(d1), to);
				double d_to_dest2 = StationsMap.calcDistance(from.nextPosition(d2), to);
				if(d_to_dest1 == d_to_dest2) // TODO .. set epsilon to consider two doubles to be equal
					return 0;
				return (d_to_dest1 > d_to_dest2) ? 1:-1;
			}
		});
		return possibleDirections.get(0);
	}
	
  
	protected float evaluateUtility(Station station) {
		
		return station.getCoins()*movesCounter + station.getPower()/movesCounter;
	}
	
	
	private boolean cycleHappened() {
		if(directionsMemory.size() < 4) return false;
		else {
			return directionsMemory.get(0)==directionsMemory.get(2) && directionsMemory.get(1)==directionsMemory.get(3) && directionsMemory.get(0)!=directionsMemory.get(1);
		}
		
	}
	
	public void printMemory() {
		System.out.println(directionsMemory.toString());
	}
	
}

