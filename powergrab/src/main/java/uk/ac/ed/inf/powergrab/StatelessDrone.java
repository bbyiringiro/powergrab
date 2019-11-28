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
		
		List<String> stationsInRange  = stationsMap.getStationsWithIn(this.getPosition(), 0.0003);
		if(stationsInRange.size() ==0)
		  return possibleDirections.get(rnd.nextInt(possibleDirections.size()));
		
		Direction nextDir;
		List<Direction> dirsCopy = new ArrayList<>(possibleDirections);
		List<String> positiveStations = new ArrayList<>();
		List<String> negStations = new ArrayList<>();
		Set<String> zeroStations = new HashSet<String>();
		ArrayList<Direction> removeDirs = new ArrayList<>();
		
		
		for(String sId: stationsInRange) {
			if(evaluateUtility(stationsMap.getStationById(sId))>0)
				positiveStations.add(sId);
			else if(evaluateUtility(stationsMap.getStationById(sId)) <0)
				negStations.add(sId);
			else 
				zeroStations.add(sId);
		}
		
		
		
		
	
		
		
		if(positiveStations.size() ==0) {
			
//			System.out.println(possibleDirections.size());
			for(int i=0; i<possibleDirections.size(); ++i) {
				Direction   dir = possibleDirections.get(i);
				Position tempNextPos = this.getPosition().nextPosition(dir);
				for(String negSId: negStations) {
					double distanceToneg = StationsMap.calcDistance(tempNextPos, stationsMap.getStationById(negSId).getPosition());
					
					String closerNonNegativeStation = "";
					if(zeroStations.size() > 0 ) {
						closerNonNegativeStation = stationsMap.getClosestStation(zeroStations, tempNextPos);
						Station safeStation  = stationsMap.getStationById(closerNonNegativeStation);
						double distanceSaferStation = StationsMap.calcDistance(tempNextPos, safeStation.getPosition());
						
						
						
						if(distanceToneg <0.00025 && distanceToneg < distanceSaferStation) 
							removeDirs.add(dir);
						
					}
					else {
						if(distanceToneg <0.00025) {
							removeDirs.add(dir);
						}
					}
				}
			
			}
			
			
			for(Direction rDir: removeDirs) {
				possibleDirections.remove(rDir);
			}
			
			nextDir =possibleDirections.get(rnd.nextInt(possibleDirections.size()));
			return nextDir;
		}else {
			
			
			positiveStations.sort( new Comparator<String>() {
				@Override
				public int compare(String id1, String id2) {
					double s1_utility = evaluateUtility(stationsMap.getStationById(id1));
					double s2_utility = evaluateUtility(stationsMap.getStationById(id2));
					if(s1_utility == s2_utility)
						return 0;
					return (s1_utility < s2_utility) ? 1:-1;
				}
			});
			
			String nextStation = positiveStations.get(0);
			Station targetStation  = stationsMap.getStationById(nextStation);
			
			for(int i=0; i<possibleDirections.size(); ++i) {
				Direction   dir = possibleDirections.get(i);
				for(String negSId: negStations) {
					Position tempNextPos = this.getPosition().nextPosition(dir);
					double distanceTotarget = StationsMap.calcDistance(tempNextPos, targetStation.getPosition());
					double distanceToneg = StationsMap.calcDistance(tempNextPos, stationsMap.getStationById(negSId).getPosition());
					
					String closerNonNegativeStation = "";
					if(zeroStations.size() > 0 ) {
						closerNonNegativeStation = stationsMap.getClosestStation(zeroStations, tempNextPos);
						Station safeStation  = stationsMap.getStationById(closerNonNegativeStation);
						double distanceSaferStation = StationsMap.calcDistance(tempNextPos, safeStation.getPosition());
						
//						if(distanceToneg <0.00025 && (distanceToneg < distanceSaferStation || distanceSaferStation < distanceTotarget)) {
//							removeDirs.add(dir);
//						}
//						else
						
						
						
						if(distanceSaferStation < 0.00025 && distanceTotarget < 0.00025 && distanceSaferStation < distanceTotarget) {
							removeDirs.add(dir);
						}
						if(distanceToneg <0.00025 && distanceSaferStation < 0.00025 && (distanceToneg < distanceSaferStation || distanceSaferStation < distanceTotarget)) {
							removeDirs.add(dir);
						}
						
						
	//							
						 if(distanceToneg <0.00025 && distanceToneg < distanceTotarget) {
							removeDirs.add(dir);
						}
					}
					else {
						if(distanceToneg <0.00025 && distanceToneg < distanceTotarget) {
							removeDirs.add(dir);
						}
					}
					
					
				}
				
				
				
		
				
				if(zeroStations.size() > 0 ) {
					Position tempNextPos = this.getPosition().nextPosition(dir);
					
					
			
					String closerNonNegativeStation = "";
					
						closerNonNegativeStation = stationsMap.getClosestStation(zeroStations, tempNextPos);
						Station safeStation  = stationsMap.getStationById(closerNonNegativeStation);
						double distanceSaferStation = StationsMap.calcDistance(tempNextPos, safeStation.getPosition());
						double distanceTotarget = StationsMap.calcDistance(tempNextPos, targetStation.getPosition());
					
					
						if(distanceSaferStation <0.00025 && distanceTotarget < 0.00025 && distanceSaferStation < distanceTotarget) {
							removeDirs.add(dir);
						}
					
	
				}
				
					
			
			}
			
			
			
			
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
			
//			System.out.println(possibleDirections);

			if(possibleDirections.size()==0)
			{
//				System.out.println("found here"+possibleDirections.size());
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
				nextDir = dirsCopy.get(0);
				return nextDir;	
			}
			int n=1;
//			if(possibleDirections.size()==2)
//				n=2;
//			else if(possibleDirections.size()>3)
//				n=3;
//			else if(possibleDirections.size()==4)
//				n=4;
//			else if(possibleDirections.size()>5)
//				n=5;
			

			nextDir = possibleDirections.get(rnd.nextInt(n));
			return nextDir;	
			
		}
	}
		
	
	
	Direction getDirectionTo(String stationId, List<Direction> possibleDirections, StationsMap stationsMap) {
		Direction nextDir;
		Station tempStation  = stationsMap.getStationById(stationId);
		nextDir = possibleDirections.get(0);
		double minDist = StationsMap.calcDistance(tempStation.getPosition(), this.getPosition().nextPosition(nextDir));
		boolean first=true;
		for(Direction dir: possibleDirections) {
			if(first) {first=false; continue; }
			double tempDist = StationsMap.calcDistance(tempStation.getPosition(), this.getPosition().nextPosition(dir));
			if(tempDist < minDist) {
				nextDir = dir;
				minDist = tempDist;
			}
			
		}
		return nextDir;
		
		
	}
	
	
	
	
	
	protected double evaluateUtility(Station station) {
		
//		Feature 1: 100 coins, 2 units of power
//
//		Feature 2: 50 coins, 50 units of power
//		
//		This is a decision which has been left to you to make as part of designing your drone's strategy.  Try to make an argument in favour of Feature 1, and then try to make an argument in favour of Feature 2.  Might the answer depend on the state of the drone?
		
		
		return station.getCoins() + station.getPower()/10;
	}
	
	


}
