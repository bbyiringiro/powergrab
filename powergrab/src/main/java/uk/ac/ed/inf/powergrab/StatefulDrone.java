package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import uk.ac.ed.inf.powergrab.StationsMap.Station;

/**
 * The Class StatefulDrone.
 * represents a sophisticated limitless drones that avoid negative stations 
 * and try to collect as many coins as it can it a limited time, which is achieved by the strategy mentioned above.

 * 
 */
public class StatefulDrone extends Drone {
	
	/** The direction memory size. */
	final int DIRECTION_MEMORY_SIZE = 4;
	
	/** The directions memory describing pas moves recent moves */
	private EvictingQueue<Direction> directionsMemory;
	
	/** The number of moves so far */
	private int movesCounter = 0;
	
	/** The void places memory. */
	private List<Position> voidPlacesMemory;
	
	/**
	 * Inner class
	 * EvictingQueue Data Structure .
	 * 
	 * @param <K> the key type
	 */
	private class EvictingQueue<K> extends ArrayList<K> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -7598907934193826633L;
		
		/** The max size. */
		final private int maxSize;

	    /**
    	 * Instantiates a new evicting queue.
    	 *
    	 * @param size the size
    	 */
    	public EvictingQueue(int size){
	        this.maxSize = size;
	    }

	    /* 
	     * pops the oldest element when the size reaches the maxSize
    	 * @see java.util.ArrayList#add(java.lang.Object)
    	 */
    	public boolean add(K k){
	        boolean r = super.add(k);
	        if (size() > maxSize){
	            removeRange(0, size() - maxSize);
	        }
	        return r;
	    }
	}
	
	/**
	 * Instantiates a new stateful drone.
	 *
	 * @param startPos the start pos
	 * @param type the type
	 * @param seed the seed
	 */
	public StatefulDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
		directionsMemory = new EvictingQueue<Direction>(DIRECTION_MEMORY_SIZE);
		voidPlacesMemory = new ArrayList<>();
	}
	
	/* 
	 * Implements the drone strategy described in the documentation. Please refer to it to understand this better.
	 *  
	 * @see uk.ac.ed.inf.powergrab.Drone#Decide(java.util.List, uk.ac.ed.inf.powergrab.StationsMap)
	 */
	@Override
	public Direction Decide(List<Direction> possibleDirections, StationsMap stationsMap) {
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
				voidPlacesMemory.add(voidPlace);
				voidPlacesMemory.add(getPosition());
			}
			
			
			String targetId= getNextTarget(stationsToVisit, stationsMap);
			Station targetStation  = stationsMap.getStationById(targetId);
			Position targetPosition = targetStation.getPosition();
			
			for(int i=0; i<possibleDirections.size(); ++i) {
				Direction   dir = possibleDirections.get(i);
				
				Position tempNextPos = this.getPosition().nextPosition(dir);
				stationsInRange  = stationsMap.getStationsWithIn(stationsAround, tempNextPos, 0.00025);
				if(stationsInRange.size()==0) 
				{
					causeCycleCheck(tempNextPos, dir, removeDirs);
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
				
				causeCycleCheck(tempNextPos, dir, removeDirs);
			}
			eliminateNegDirections(removeDirs, possibleDirections);
			if(possibleDirections.size()==0)
				nextDir = StationsMap.getDirectionTo(getPosition(), targetPosition, initialDirections);	
			else 
				nextDir = StationsMap.getDirectionTo(getPosition(), targetPosition, possibleDirections);
		}
		
		directionsMemory.add(nextDir);
		return nextDir;
	}
	
	/**
	 * Scan remaining  unvisited positive stations.
	 *
	 * @param stationsMap the stations map
	 * @return the list of unvisited positive stations
	 */
	protected List<String> scanRemainingStations(StationsMap stationsMap){
		List<String> positiveStations = new ArrayList<>();
		List<String> allStation = stationsMap.getAllStations();
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
	
	/**
	 * determines if a particular directions at particular position will cause a cycle when a drone goes to a position in voidPlacesMemory filed,
	 *  if adds to the directions that should be avoided.
	 *
	 * @param position the position
	 * @param dir the dir
	 * @param removeDirs the remove dirs
	 */
	private void causeCycleCheck(Position position, Direction dir, List<Direction> removeDirs) {
		for(Position pos: voidPlacesMemory) {
			double distanceToVoid = StationsMap.calcDistance(position, pos);
			final double epsilon = 0.0001;
			if(distanceToVoid <epsilon) {
				removeDirs.add(dir);
			}
		}
	}
	
	
	/**
	 * Gets the next target.
	 * return the id of the best station to visit of a least cost and that also maximises utility.

	 * @param positiveStations the positive stations
	 * @param stationsMap the stations map
	 * @return the next target
	 */
	private String getNextTarget(List<String> positiveStations, StationsMap stationsMap) {
		if (positiveStations.size()==0) return "";
		
		String best_target = positiveStations.get(0);
		double leastCost = StationsMap.calcDistance(getPosition(), stationsMap.getStationById(best_target).getPosition())
				/evaluateUtility(stationsMap.getStationById(best_target));
		
		for(String stationId: positiveStations) {
			double tempCost = StationsMap.calcDistance(this.getPosition(), stationsMap.getStationById(stationId).getPosition())
					/evaluateUtility(stationsMap.getStationById(stationId));
			if(tempCost < leastCost) {
				leastCost = tempCost;
				best_target = stationId;
			}
		}
		return best_target;
	}
  
	/* 
	 * calculate the utility of station at a particular time
	 * @see uk.ac.ed.inf.powergrab.Drone#evaluateUtility(uk.ac.ed.inf.powergrab.StationsMap.Station)
	 */
	protected float evaluateUtility(Station station) {
		
		return station.getCoins()*movesCounter + 
				station.getPower()/movesCounter;
	}
	
	
	/**
	 * Cycle happened.
	 * return true if a cycle have happened, given that directions memory only keeps four directions.
	 * It determine when a drone has made four consecutive alternative moves only two types 
	 * (for example, N, S, N, S is cycle, but NNNN and NSSN are not)

	 *
	 * @return true, if successful
	 */
	private boolean cycleHappened() {
		if(directionsMemory.size() < 4) return false;
		else {
			return directionsMemory.get(0)==directionsMemory.get(2) && 
					directionsMemory.get(1)==directionsMemory.get(3) && 
					directionsMemory.get(0)!=directionsMemory.get(1);
		}
		
	}
	
}

