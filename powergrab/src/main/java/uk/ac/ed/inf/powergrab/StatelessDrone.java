package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatelessDrone extends Drone {
	
	public StatelessDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
	}
	
	
	@Override
	public Direction Decide(List<Direction> possibleDirections, StationsMap stationsMap) {
		Direction nextDir;
		List<Direction> safeMoves = new ArrayList<>(possibleDirections);
		List<String> stationsInRange  = stationsMap.getStationWithIn(this.getPosition(), 0.0003);
		if(stationsInRange.size() == 0) {
			nextDir = possibleDirections.get(rnd.nextInt(possibleDirections.size()));
		}else {
			stationsInRange.sort( new Comparator<String>() {
				@Override
				public int compare(String id1, String id2) {
					double s1_utility = evaluateUtility(stationsMap.getStationById(id1));
					double s2_utility = evaluateUtility(stationsMap.getStationById(id2));
					if(s1_utility == s2_utility)
						return 0;
					return (s1_utility < s2_utility) ? 1:-1;
				}
			});
			List<String> PosStations = new ArrayList<>();
			List<String> negStations = new ArrayList<>();
			List<String> ZeroStation = new ArrayList<>();
			for(String sId: stationsInRange) {
				if(evaluateUtility(stationsMap.getStationById(sId))>0)
					PosStations.add(sId);
				else if(evaluateUtility(stationsMap.getStationById(sId)) <0)
					negStations.add(sId);
				else 
					ZeroStation.add(sId);
			}
			
			for(String s: stationsInRange) System.out.println(s);
			
			for(int i=0; i<safeMoves.size(); ++i) {
				Direction   dir = safeMoves.get(i);
				for(String negSId: negStations) {
					Position tempNextPos = this.getPosition().nextPosition(dir);
					double distance = StationsMap.calcDistance(tempNextPos, stationsMap.getStationById(negSId).getPosition());
					if(distance <0.00025) {
						safeMoves.remove(dir);
					}
				}
			
			}
			// if all possible position takes closer to the negative stations choose the least costly and all stations in range are negatives
			if(safeMoves.size() == 0 && evaluateUtility(stationsMap.getStationById(stationsInRange.get(0))) < 0) {
				nextDir = getDirectionTo(stationsInRange.get(stationsInRange.size()-1), possibleDirections, stationsMap);
			}else if (safeMoves.size() == 0) {
				nextDir = getDirectionTo(stationsInRange.get(0), possibleDirections, stationsMap);
			}else{
				// the safe moves takes either to positive or zero stations
				List<Direction> nonZeroPosMoves = new ArrayList<>(safeMoves);
				for(Direction dir: nonZeroPosMoves) {
					for(String zeroSId: ZeroStation) {
						Position tempNextPos = this.getPosition().nextPosition(dir);
						double distance = StationsMap.calcDistance(tempNextPos, stationsMap.getStationById(zeroSId).getPosition());
						if(distance <0.00025) {
							safeMoves.remove(dir);
						}
					}
					
				}
				
				if(nonZeroPosMoves.size() == 0 && PosStations.size() >0) {
					nextDir = getDirectionTo(PosStations.get(0), safeMoves, stationsMap);
				}else if (nonZeroPosMoves.size() == 0 || PosStations.size()==0) { // when safe moves either takes me to 
					nextDir = safeMoves.get(rnd.nextInt(safeMoves.size()));
				}else {
					nextDir = getDirectionTo(PosStations.get(0), nonZeroPosMoves, stationsMap);
				}
				
			}
			
			
		}
		
		
		return nextDir;
		
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
	
	
	
	
//	private chooseBestDirectionList<String> sortedStation)
	

//	@Override
//	public Direction chooseDirection() {
//		System.out.println("Scanning...");
//		List<Direction> possibleMoveDir = getPossibleMoves();
//		int n = possibleMoveDir.size();
//		List<Integer> stations_idx = InRangestation_Idx();
//		List<Integer> pos_stations = new ArrayList<>();
//		List<Integer> neg_stations = new ArrayList<>();
//		Direction nextDir;
//		
//		System.out.println(stations_idx.size());
//			
//		
//		if (stations_idx.size() == 0) 
//		{					
//			nextDir = possibleMoveDir.get(rnd.nextInt(n));
//			System.out.println("Choose location randomly 0");
//			return nextDir;	
//		}
//		
//		
//		if (stations_idx.size() > 0) 
//			System.out.println("Before sorting "+stations_idx.get(0));
//		stations_idx.sort( new Comparator<Integer>() {
//			@Override
//			public int compare(Integer s1, Integer s2) {
//				double s1_utility = evaluateUtility(mapFeatures.get(s1));
//				double s2_utility = evaluateUtility(mapFeatures.get(s2));
//				if(s1_utility == s2_utility) // TODO .. set epsilon to consider two doubles to be equal
//					return 0;
//				return (s1_utility < s2_utility) ? 1:-1;
//			}
//
//		});
//	
//		for (int l=0; l<stations_idx.size(); ++l)
//		{
//			if(evaluateUtility(mapFeatures.get(stations_idx.get(l)))>=0)
//				pos_stations.add(stations_idx.get(l));
//			else
//				neg_stations.add(stations_idx.get(l));
//		}
//		
//		
//		int best_closer_station = stations_idx.get(0);
//		
//		System.out.println("after sorting " +stations_idx.get(0));
//		if (stations_idx.size() >1)
//			System.out.println("second"+evaluateUtility(mapFeatures.get(stations_idx.get(1))));
//		System.out.println(evaluateUtility(mapFeatures.get(stations_idx.get(0))));
//		if (evaluateUtility(mapFeatures.get(best_closer_station)) == 0 && stations_idx.size() == 1) // this is to prevent the situation when you sort zero the first but there other negative station in the regiion (0.003) and choosing randomly for all possible location you can bump into them
//			{
//			nextDir = possibleMoveDir.get(rnd.nextInt(n));
//			System.out.println("Choose location randomly 1");
//			
//			return nextDir;	
//			}
//		
//		else if (evaluateUtility(mapFeatures.get(stations_idx.get(0))) <= 0) {
//			List<Direction> copyOfPossibleMoveDir = new ArrayList<>(possibleMoveDir);
//			System.out.println("all stations in range are either negative or zero in utility");
//			System.out.println(copyOfPossibleMoveDir.size() +"before worst direction");
//			int directionsize;
//			int newSize;
//			do {
//				directionsize = copyOfPossibleMoveDir.size();
//				for(int k=0; k<stations_idx.size(); ++k) {
//				 copyOfPossibleMoveDir.remove(moveAwayDirections(copyOfPossibleMoveDir, stations_idx.get(k)));
//				}
//				newSize = copyOfPossibleMoveDir.size();
//			 }while((newSize != directionsize) && newSize>0);
//			
//			System.out.println(copyOfPossibleMoveDir.size() +"remove worst directions");
//			n = copyOfPossibleMoveDir.size();
//			// handling the case when for all possible directions there is a negative station 
//			
//			if (n==0) { // rare but in case all direction goes to negative stations
//				// get closer to the least negative utility station
//				System.out.println("negative stations in all directions");
//				nextDir = closerDirection(possibleMoveDir, stations_idx.get(0), new ArrayList<Integer>()).get(0); // give it empty bad array station.. coz all are bad
//				System.out.println("Choose location randomly 4");
//				return nextDir;
//			}
//			nextDir = copyOfPossibleMoveDir.get(rnd.nextInt(n));
//			System.out.println("Choose location randomly 3");
//			
//			System.out.println(nextDir);
//			return nextDir;	
//		}
//		else {
//			List<Direction> chooseBestDri = closerDirection (possibleMoveDir, best_closer_station, neg_stations);
//			nextDir = chooseBestDri.get(rnd.nextInt(chooseBestDri.size()));
//			System.out.println("Choose wisely 3");
//			return nextDir;
//		}
//		
//	}
//	
//	public List<Direction> closerDirection(List<Direction> directions, int station_idx, List<Integer> badStation) {
//		
//		List<Direction> chooseDirection = new ArrayList<>();		
//		
//		
//		// experimental ...
//		// remove directions that takes me to bad stations
//		int directionsize;
//		int newSize;
////		
//		do {
//			directionsize = directions.size();
//			for(int k=0; k<badStation.size(); ++k) {
//				directions.remove(moveAwayDirections(directions, badStation.get(k)));
//			}
//			newSize = directions.size();
//		 }while((newSize != directionsize) && newSize>0);
////		
//		
//		// choosing the direction that a give me closor to a point;
//				int n = directions.size();
//				int tempDirIndex= rnd.nextInt(n);
//				Direction nextDir = directions.get(tempDirIndex);
//				System.out.println("temp Direction "+nextDir);
//		
//				
//
//				System.out.println("size of bad stations :"+badStation.size());
//				double tempDistance;
//				double minDist = calcDistance(currentPos.nextPosition(directions.get(tempDirIndex)), (Point) mapFeatures.get(station_idx).geometry());
//				boolean changed = false;
////				System.out.println(minDist);
//				for (int j=0; j<n; ++j) {
//					tempDistance = calcDistance(currentPos.nextPosition(directions.get(j)), (Point) mapFeatures.get(station_idx).geometry());
//					// need to be adjusted when 0.0 station are not affecting the randomness  of location or 
//					// 
//					boolean nearbad=false;
//					if(badStation.size()>0) {
//					 
//					 for (int k=0; k <badStation.size(); ++k)
//					 {
//						 double distToNearestBad = calcDistance(currentPos.nextPosition(directions.get(j)), (Point) mapFeatures.get(badStation.get(k)).geometry());
//						 System.out.println("dist to bad station "+distToNearestBad +" if we go "+directions.get(j));
//						 if(distToNearestBad <0.00025) {
//							 nearbad= true;	 
//							 break;
//							 // this will require to change else if for the case when no better location is found; // need more thinking
//						 }
//					 }
//					}
//					System.out.println(tempDistance +" "+directions.get(j));
//					if(tempDistance < 0.00025 && !nearbad) {
//						System.out.println(tempDistance);
//						chooseDirection.add(directions.get(j));
////						changed = true;
//					}
//					
//					if(tempDistance < minDist) {
////						System.out.println(tempDistance);
//						minDist = tempDistance;
//						nextDir = directions.get(j);
//						changed = true;
//					}
//				}
//				
//				if (!changed) {
//					System.out.println("initial random position was never changed");
//				}
////				chooseDirection
//				if(chooseDirection.size() ==0) {
//					System.out.println(minDist);
//					chooseDirection.add(nextDir);
//					
//				}
//				
//		
//		return chooseDirection;
//	}
//	
//	public Direction moveAwayDirections(List<Direction> directions, int station_idx) {
//		
//		// choosing the direction that a give me closor to a point;
//		int n = directions.size();
//
////				System.out.println(nextDir);
//				double tempDistance;
////				System.out.println(minDist);
//				for (int j=0; j<n; ++j) {
//					tempDistance = calcDistance(currentPos.nextPosition(directions.get(j)), (Point) mapFeatures.get(station_idx).geometry());
//					if(tempDistance < 0.00025) {
//						return directions.get(j);
//					}
//				}
//		
//		return null;
//	}
//	
	
	
	protected double evaluateUtility(Station station) {
		
//		Feature 1: 100 coins, 2 units of power
//
//		Feature 2: 50 coins, 50 units of power
//		
//		This is a decision which has been left to you to make as part of designing your drone's strategy.  Try to make an argument in favour of Feature 1, and then try to make an argument in favour of Feature 2.  Might the answer depend on the state of the drone?
		
		
		return station.getCoins() + station.getPower();
	}
	
	


}
