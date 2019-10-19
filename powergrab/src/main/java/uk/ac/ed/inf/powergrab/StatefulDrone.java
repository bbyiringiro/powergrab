package uk.ac.ed.inf.powergrab;

import java.util.List;

public class StatefulDrone extends Drone {
	public StatefulDrone(Position startPos, String type, int seed) {
		super(startPos, seed, type);
	}

	@Override
	public Direction chooseDirection() {
		
		List<Direction> possibleMoveDir = getPossibleMoves();
		int n = possibleMoveDir.size();
		Direction nextDir = possibleMoveDir.get(rnd.nextInt(n));
		
		return nextDir;	
	}
}
