package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mapbox.geojson.Point;

public class StatefulDrone extends Drone {
	
	public ArrayList<Integer> positiveStation;
	public ArrayList<Integer> negativeStation;
	public StatefulDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
	}
	
	@Override
	public Direction Decide(List<Direction> possibleDirections, StationsMap stationsMap) {
		Direction nextDir;
		List<Direction> dirsCopy = new ArrayList<>(possibleDirections);
		Set<String> allStation = new HashSet<>(stationsMap.getAllStations());
		List<String> positiveStations = new ArrayList<>();
		
		List<String> stationsInRange  = stationsMap.getStationWithIn(this.getPosition(), 0.00055);
		List<String> negStations = new ArrayList<>();
		Set<String> noneNegativeStations = new HashSet<String>();
		ArrayList<Direction> removeDirs = new ArrayList<>();

		for(String sId: allStation){
			Station tempStation = stationsMap.getStationById(sId);
			double tempUtil = evaluateUtility(tempStation);
			if(tempUtil > 0)
			{
				positiveStations.add(sId);
			}
		}
		
		
		for(String sId: stationsInRange) {
			if(evaluateUtility(stationsMap.getStationById(sId)) <0)
				negStations.add(sId);
			else
				noneNegativeStations.add(sId);
		}
		
		
		
		
		
		
		if(positiveStations.size() ==0) {
			
			
			for(int i=0; i<possibleDirections.size(); ++i) {
				Direction   dir = possibleDirections.get(i);
				for(String negSId: negStations) {
					Position tempNextPos = this.getPosition().nextPosition(dir);
					double distanceToneg = StationsMap.calcDistance(tempNextPos, stationsMap.getStationById(negSId).getPosition());
					if(distanceToneg <0.00025) {
						removeDirs.add(dir);
					}
				}
			
			}
			
			
			for(Direction rDir: removeDirs) {
				possibleDirections.remove(rDir);
			}
			
			
			nextDir =possibleDirections.get(rnd.nextInt(possibleDirections.size()));
			return nextDir;
		}else {
			
			
			
			String nextStation = getNextStation(positiveStations, stationsMap);
			Station targetStation  = stationsMap.getStationById(nextStation);
			
			System.out.println(noneNegativeStations);
			System.out.println(nextStation);
			noneNegativeStations.remove(nextStation);
			System.out.println(noneNegativeStations);
			System.out.println(possibleDirections);
			for(int i=0; i<possibleDirections.size(); ++i) {
				Direction   dir = possibleDirections.get(i);
				for(String negSId: negStations) {
					Position tempNextPos = this.getPosition().nextPosition(dir);
					double distanceTotarget = StationsMap.calcDistance(tempNextPos, targetStation.getPosition());
					double distanceToneg = StationsMap.calcDistance(tempNextPos, stationsMap.getStationById(negSId).getPosition());
					
					String closerNonNegativeStation = "";
					if(noneNegativeStations.size() > 0 ) {
						closerNonNegativeStation = stationsMap.getClosestStation(noneNegativeStations, tempNextPos);
						Station safeStation  = stationsMap.getStationById(closerNonNegativeStation);
						double distanceSaferStation = StationsMap.calcDistance(tempNextPos, safeStation.getPosition());
						
						
						
						if(distanceToneg <0.00025 && (distanceToneg < distanceSaferStation || distanceSaferStation < distanceTotarget)) {
							removeDirs.add(dir);
						}else if(distanceToneg <0.00025 && distanceToneg < distanceTotarget) {
							removeDirs.add(dir);
						}
					}
					else {
						if(distanceToneg <0.00025 && distanceToneg < distanceTotarget) {
							removeDirs.add(dir);
						}
					}
					
					
				}
				
				
				
				if(noneNegativeStations.size() > 0 ) {
					Position tempNextPos = this.getPosition().nextPosition(dir);
					
					
			
					String closerNonNegativeStation = "";
					
						closerNonNegativeStation = stationsMap.getClosestStation(noneNegativeStations, tempNextPos);
						Station safeStation  = stationsMap.getStationById(closerNonNegativeStation);
						double distanceSaferStation = StationsMap.calcDistance(tempNextPos, safeStation.getPosition());
						double distanceTotarget = StationsMap.calcDistance(tempNextPos, targetStation.getPosition());
					
					
						if(distanceSaferStation <0.00025 && distanceTotarget < 0.00025 && distanceSaferStation < distanceTotarget) {
							removeDirs.add(dir);
						}
					
	
				}
				
					
			
			}
			
			
			
			
//			System.out.println(noneNegativeStations.size());
			
			
			for(Direction rDir: removeDirs) {
				possibleDirections.remove(rDir);
			}
			
			
			
			
			Position currPos = this.getPosition();
			possibleDirections.sort( new Comparator<Direction>() {
				@Override
				public int compare(Direction d1, Direction d2) {
					double d_to_dest1 = StationsMap.calcDistance(currPos.nextPosition(d1), stationsMap.getStationById(nextStation).getPosition());
					double d_to_dest2 = StationsMap.calcDistance(currPos.nextPosition(d2), stationsMap.getStationById(nextStation).getPosition());
					if(d_to_dest1 == d_to_dest2) // TODO .. set epsilon to consider two doubles to be equal
						return 0;
					return (d_to_dest1 > d_to_dest2) ? 1:-1;
				}
			});
			
			System.out.println(possibleDirections);
			int n=1;

			if(possibleDirections.size()==0)
			{
				System.out.println("found here"+possibleDirections.size());
				dirsCopy.sort( new Comparator<Direction>() {
					@Override
					public int compare(Direction d1, Direction d2) {
						double d_to_dest1 = StationsMap.calcDistance(currPos.nextPosition(d1), stationsMap.getStationById(nextStation).getPosition());
						double d_to_dest2 = StationsMap.calcDistance(currPos.nextPosition(d2), stationsMap.getStationById(nextStation).getPosition());
						if(d_to_dest1 == d_to_dest2) // TODO .. set epsilon to consider two doubles to be equal
							return 0;
						return (d_to_dest1 > d_to_dest2) ? 1:-1;
					}
				});
				return dirsCopy.get(0);
			}
			if(possibleDirections.size()>=3)
				n=3;
			
//			consider when n==0 ?????????????????????????????
			return possibleDirections.get(rnd.nextInt(n));	
			
		}
		
		
		
	}
	
	
	public String getNextStation(List<String> positiveStations, StationsMap stationsMap) {
		
		
		String nextStation = positiveStations.get(0);
		double closestDistance  = StationsMap.calcDistance(this.getPosition(), stationsMap.getStationById(nextStation).getPosition());///evaluateUtility(stationsMap.getStationById(nextStation));
		for(String sId: positiveStations) {
			double tempDistance = StationsMap.calcDistance(this.getPosition(), stationsMap.getStationById(sId).getPosition());///evaluateUtility(stationsMap.getStationById(sId));
			if( tempDistance < closestDistance) {
				closestDistance = tempDistance;
				nextStation = sId;
			}
		}
			
			return nextStation;
	}
	
  
	protected double evaluateUtility(Station station) {
		
		return station.getCoins() + station.getPower();
	}
}
