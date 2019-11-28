package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatefulDrone extends Drone {
	
	public ArrayList<Integer> positiveStation;
	public ArrayList<Integer> negativeStation;
	private EvictingQueue<Direction> memory;
	private int target_index = 0;
	private int counter =0;
	private int moves_counter = 1;
	private ArrayList<Position> voidPlaces;
	
	public StatefulDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
		memory = new EvictingQueue<>(4);
		voidPlaces = new ArrayList<>();
	}
	
	@Override
	public Direction Decide(List<Direction> possibleDirections, StationsMap stationsMap) {
		printMemory();
		++moves_counter;
		
		
		if(isCycle()) {
			
			Position voidPlace = currentPos.nextPosition(memory.get(0));
			voidPlaces.add(voidPlace);
			voidPlaces.add(getPosition());
		}
		
		if(counter >0)
			--counter;
		else
			target_index =0;
		Direction nextDir;
		List<Direction> dirsCopy = new ArrayList<>(possibleDirections);
		Set<String> allStation = new HashSet<>(stationsMap.getAllStations());
		List<String> positiveStations = new ArrayList<>();
		
		List<String> stationsInRange  = stationsMap.getStationsWithIn(this.getPosition(), 0.00055);
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
			
//			System.out.println(possibleDirections.size());
			for(int i=0; i<possibleDirections.size(); ++i) {
				Direction   dir = possibleDirections.get(i);
				Position tempNextPos = this.getPosition().nextPosition(dir);
				for(String negSId: negStations) {
					double distanceToneg = StationsMap.calcDistance(tempNextPos, stationsMap.getStationById(negSId).getPosition());
					
					String closerNonNegativeStation = "";
					if(noneNegativeStations.size() > 0 ) {
						closerNonNegativeStation = stationsMap.getClosestStation(noneNegativeStations, tempNextPos);
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
//			System.out.println(possibleDirections.size());
			
			nextDir =possibleDirections.get(rnd.nextInt(possibleDirections.size()));
			memory.add(nextDir);
			return nextDir;
		}else {
			
			String nextStation = getNextStation(positiveStations, stationsMap,target_index);
			Station targetStation  = stationsMap.getStationById(nextStation);
			noneNegativeStations.remove(nextStation);
			
//			System.out.println(noneNegativeStations);
//			System.out.println(nextStation);
//			System.out.println(noneNegativeStations);
//			System.out.println(possibleDirections);
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
				
				
				
				
				for(Position pos: voidPlaces) {
					Position tempNextPos = this.getPosition().nextPosition(dir);
					double distanceToVoid = StationsMap.calcDistance(tempNextPos, pos);
					final double epsilon = 0.0001;
					if(distanceToVoid <epsilon)
						removeDirs.add(dir);
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
				memory.add(nextDir);
				return nextDir;	
			}
//			if(possibleDirections.size()==2)
//				n=2;
//			else if(possibleDirections.size()==3)
//				n=3;
//			else if(possibleDirections.size()==4)
//				n=4;
//			else if(possibleDirections.size()>5)
//				n=5;
			int n=1;
			if(isCycle() && possibleDirections.size()>1) {
//				++target_index;
//				counter=7;
//				n=possibleDirections.size();
				System.out.println("Cycled so changed direction from "+possibleDirections.get(0) + "to "+possibleDirections.get(rnd.nextInt(n))+ " where n is equal to"+n  );
				
			}
			
			if(target_index >0) {
				n = possibleDirections.size();
			}
			
				
			nextDir = possibleDirections.get(rnd.nextInt(n));
			memory.add(nextDir);
			return nextDir;	
			
		}
		
		
		
	}
	
	
	
	
	public String getNextStation(List<String> positiveStations, StationsMap stationsMap, int index) {
		positiveStations.sort( new Comparator<String>() {
			@Override
			public int compare(String id1, String id2) {
				
				double s1_utility = StationsMap.calcDistance(getPosition(), stationsMap.getStationById(id1).getPosition())/evaluateUtility(stationsMap.getStationById(id1));
				double s2_utility = StationsMap.calcDistance(getPosition(), stationsMap.getStationById(id2).getPosition())/evaluateUtility(stationsMap.getStationById(id2));
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
		
//		String nextStation = positiveStations.get(0);
//		double closestDistance  = StationsMap.calcDistance(this.getPosition(), stationsMap.getStationById(nextStation).getPosition());//evaluateUtility(stationsMap.getStationById(nextStation));
//		for(String sId: positiveStations) {
//			double tempDistance = StationsMap.calcDistance(this.getPosition(), stationsMap.getStationById(sId).getPosition());//evaluateUtility(stationsMap.getStationById(sId));
//			if( tempDistance < closestDistance) {
//				closestDistance = tempDistance;
//				nextStation = sId;
//			}
//		}
//			
//			return nextStation;
	}
	
  
	protected double evaluateUtility(Station station) {
		
		return station.getCoins()*moves_counter + station.getPower()/moves_counter;
	}
	
	
	public void printMemory() {
		System.out.println(memory.toString());
	}
	
	
	private boolean isCycle() {
		if(memory.size() < 4) return false;
		else {
			return memory.get(0)==memory.get(2) && memory.get(1)==memory.get(3) && memory.get(0)!=memory.get(1);
		}
		
	}
}






//
//String nextStation = getNextStation(positiveStations, stationsMap);
//Station targetStation  = stationsMap.getStationById(nextStation);
//noneNegativeStations.remove(nextStation);
////System.out.println(nextStation);
////System.out.println(noneNegativeStations);
//System.out.println(possibleDirections);
//for(int i=0; i<possibleDirections.size(); ++i) {
//	Direction   dir = possibleDirections.get(i);
//	Position tempNextPos = this.getPosition().nextPosition(dir);
//	double distanceTotarget = StationsMap.calcDistance(tempNextPos, targetStation.getPosition());
//	
//	if(negStations.size() > 0 ) {
//	 String closestnegStation = stationsMap.getClosestStation(negStations, tempNextPos);
//	 Station negStation  = stationsMap.getStationById(closestnegStation);
//	 double distanceToneg = StationsMap.calcDistance(tempNextPos, negStation.getPosition());
//			
//	 String closerNonNegativeStation = "";
//		if(noneNegativeStations.size() > 0 ) {
//			closerNonNegativeStation = stationsMap.getClosestStation(noneNegativeStations, tempNextPos);
//			Station safeStation  = stationsMap.getStationById(closerNonNegativeStation);
//			double distanceSaferStation = StationsMap.calcDistance(tempNextPos, safeStation.getPosition());
//			
//			
//			
//			if(distanceToneg <0.00025 && (distanceToneg < distanceSaferStation || distanceSaferStation < distanceTotarget)) {
//				removeDirs.add(dir);
//			} 
//			if(distanceToneg <0.00025 && distanceToneg < distanceTotarget) {
//				removeDirs.add(dir);
//			}
//		}
//		else {
//			 if(distanceToneg <0.00025 && distanceTotarget < 0.00025 && distanceToneg < distanceTotarget) {
//				 removeDirs.add(dir);				 
//				 }
//		}
//	 
//	 
//	 
//	 
//	 }
//	
//
//	
//	if(noneNegativeStations.size() > 0 ) {
//		
//		
//
//		String closerNonNegativeStation = "";
//		
//			closerNonNegativeStation = stationsMap.getClosestStation(noneNegativeStations, tempNextPos);
//			Station safeStation  = stationsMap.getStationById(closerNonNegativeStation);
//			double distanceSaferStation = StationsMap.calcDistance(tempNextPos, safeStation.getPosition());
//		
//		
//			if(distanceSaferStation <0.00025 && distanceTotarget < 0.00025 && distanceSaferStation < distanceTotarget) {
//				removeDirs.add(dir);
//			}
//		
//
//	}
//	
//		
//
//}
//
//
//
//
////System.out.println(noneNegativeStations.size());
//
//
//for(Direction rDir: removeDirs) {
//	possibleDirections.remove(rDir);
//}
//
//System.out.println(possibleDirections);
//
//
//Position currPos = this.getPosition();
//possibleDirections.sort( new Comparator<Direction>() {
//	@Override
//	public int compare(Direction d1, Direction d2) {
//		double d_to_dest1 = StationsMap.calcDistance(currPos.nextPosition(d1), stationsMap.getStationById(nextStation).getPosition());
//		double d_to_dest2 = StationsMap.calcDistance(currPos.nextPosition(d2), stationsMap.getStationById(nextStation).getPosition());
//		if(d_to_dest1 == d_to_dest2) // TODO .. set epsilon to consider two doubles to be equal
//			return 0;
//		return (d_to_dest1 > d_to_dest2) ? 1:-1;
//	}
//});
//
//System.out.println(possibleDirections);
//int n=1;
//
//if(possibleDirections.size()==0)
//{
//	System.out.println("found here"+possibleDirections.size());
//	dirsCopy.sort( new Comparator<Direction>() {
//		@Override
//		public int compare(Direction d1, Direction d2) {
//			double d_to_dest1 = StationsMap.calcDistance(currPos.nextPosition(d1), stationsMap.getStationById(nextStation).getPosition());
//			double d_to_dest2 = StationsMap.calcDistance(currPos.nextPosition(d2), stationsMap.getStationById(nextStation).getPosition());
//			if(d_to_dest1 == d_to_dest2) // TODO .. set epsilon to consider two doubles to be equal
//				return 0;
//			return (d_to_dest1 > d_to_dest2) ? 1:-1;
//		}
//	});
//	return dirsCopy.get(0);
//}
//if(possibleDirections.size()==2)
//	n=2;
//else if(possibleDirections.size()==3)
//	n=3;
//else if(possibleDirections.size()==4)
//	n=3;
//else
//	n=5;
//
////consider when n==0 ?????????????????????????????
//return possibleDirections.get(rnd.nextInt(n));	
