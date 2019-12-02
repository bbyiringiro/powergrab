package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import uk.ac.ed.inf.powergrab.StationsMap.Station;

// TODO: Auto-generated Javadoc
/**
 * The Class StatelessDrone.
 */
public class StatelessDrone extends Drone {
	
	/**
	 * Instantiates a new stateless drone.
	 *
	 * @param startPos the start pos
	 * @param type the type
	 * @param seed the seed
	 */
	public StatelessDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.powergrab.Drone#Decide(java.util.List, uk.ac.ed.inf.powergrab.StationsMap)
	 */
	@Override
	public Direction Decide(List<Direction> possibleDirections, StationsMap stationsMap) {
		List<Direction> nextDirs = new ArrayList<>();
		List<Direction> nextDirs2 = new ArrayList<>();
		float maxUtility = 0;
		float maxUtility2 = 0;
		Direction   leastCostlyDir = null;
		float leastCost = Float.MAX_VALUE;
		List<String> stationsInRange;
		List<Direction> negDirections = new ArrayList<>();
		for(int i=0; i<possibleDirections.size(); ++i) {
			Direction   dir = possibleDirections.get(i);
			Position nextPosition = currentPos.nextPosition(dir);
			
			stationsInRange  = stationsMap.getStationsWithIn(nextPosition, 0.00025);
			if(stationsInRange.size() == 0)
				continue;
			
			
			String closestSId = stationsMap.getClosestStation(stationsInRange, nextPosition);
			Station closestStation  = stationsMap.getStationById(closestSId);
			float closestStationUtility = evaluateUtility(closestStation);
			
			float totalPosUtility = getRegionGain(stationsInRange, stationsMap);
			

			if(closestStationUtility < 0){
				negDirections.add(dir);
				if(closestStationUtility < leastCost) {
					leastCostlyDir = dir;
					leastCost = closestStationUtility;
					
				}
			}
			else if(closestStationUtility>0) {
				if(totalPosUtility < maxUtility)
					continue;
				else if (totalPosUtility == maxUtility && nextDirs.size()>0)
					nextDirs.add(dir);
				else if(totalPosUtility > maxUtility) {
					nextDirs.clear();
					nextDirs.add(dir);
					maxUtility = totalPosUtility;
				}
				
			}else if(closestStationUtility==0) {
				
				if(totalPosUtility < maxUtility2)
					continue;
				else if (totalPosUtility == maxUtility2 && nextDirs2.size()>0)
					nextDirs2.add(dir);
				else if(totalPosUtility > maxUtility2) {
					nextDirs2.clear();
					nextDirs2.add(dir);
					maxUtility2 = totalPosUtility;
				}
	
			}else {
				continue;
			}
		}
		eliminateNegDirections(negDirections, possibleDirections);
		int n = nextDirs.size();
		int n2 = nextDirs2.size();
		if(n !=0)
			return nextDirs.get(rnd.nextInt(n));
		else if(n2 != 0)
			return nextDirs2.get(rnd.nextInt(n2));
		else {
			int n_dirs = possibleDirections.size();
			if(n_dirs==0) {
				// if all directions have negative utility hence have been removed, chooses the least costly 
				//this can happen only when the drone is initiated in that particular area, otherwise it would choose a path that leads it there.
				return leastCostlyDir;
			}
			else {
				System.out.println("Possibility of  "+n_dirs+" in 16");
				return possibleDirections.get(rnd.nextInt(n_dirs));
			}
			
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.powergrab.Drone#evaluateUtility(uk.ac.ed.inf.powergrab.StationsMap.Station)
	 */
	protected float evaluateUtility(Station station) {
		return station.getCoins() + station.getPower()/25;
	}
	
	/**
	 * Gets the region gain.
	 *
	 * @param stations the stations
	 * @param stationsMap the stations map
	 * @return the region gain
	 */
	private float getRegionGain(List<String> stations, StationsMap stationsMap)
	{
		// only filters out the positive utility stations because negatives stations will be avoided at all cost, if possible
		float totalPosUtility = 0f;
		for(String sId: stations) {
			Station tempStation  = stationsMap.getStationById(sId);
			float tempUtilility = evaluateUtility(tempStation);
			if(tempUtilility>0)
				totalPosUtility += tempUtilility;
		}
		
		return totalPosUtility;
	}
	
	


}

