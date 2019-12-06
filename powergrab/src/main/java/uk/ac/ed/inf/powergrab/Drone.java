package uk.ac.ed.inf.powergrab;

import java.util.Random;
import java.util.List;
import uk.ac.ed.inf.powergrab.StationsMap.Station;

/**
 * The Class Drone, is an abstract class all drones should inherit and implement the below abstract functions
 */
abstract class Drone {
	
	/** The power. */
	private float power;
	
	/** The power coin. */
	private float powerCoin;
	
	/** The drone type. */
	String type;
	
	/** The current position of a drone. */
	Position currentPos;
	
	/** The random seed. */
	protected  Random rnd;
	
	/**
	 * Instantiates a new drone.
	 * constructor for the drones that takes its initial position on the map, random generator seed, and the type of the drone
	 * @param drone the starting position in a map
	 * @param seed the random seed generator 
	 * @param type the type of the drone.
	 */
	public Drone(Position pos, int seed, String type) {
		currentPos = pos;
		this.type = type;
		rnd = new Random(seed);
		power = 250f;
		powerCoin = 0f;
		
	}
	
	/**
	 * Decide, decides on the direction of the next move a drone can make.
	 *
	 * @param possibleDirection the legal directions a drone can move to
	 * @param stationsMap the stations map
	 * @return the direction, of the next move a drone can make
	 */
	abstract public Direction Decide(List<Direction> possibleDirection, StationsMap stationsMap);
	
	/**
	 *  function to be implemented by each drone to determine the utility of station depends on 
	 *  how a particular drone evaluate it.
	 *
	 * @param station, the station object
	 * @return the float of evaluation of utility of a particular station
	 */
	abstract protected float evaluateUtility(Station station);
	
	/**
	 * Inspect directions.
	 *
	 * @param stationsMap the stations map
	 * @return the list of legal directions can be after the current possition
	 */
	protected List<Direction> inspectDirections(StationsMap stationsMap){
		return stationsMap.getPossibleMoves(getPosition());
	}
	
	/**
	 * Gets the drone's power.
	 *
	 * @return the power
	 */
	public float getPower() {
		return power;
	}
	
	/**
	 * Gets the power coins.
	 *
	 * @return the power coins
	 */
	public float getPowerCoins() {
		return powerCoin;
	}
	
	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public Position getPosition() {
		return currentPos;
	}
	
	/**
	 * Move, given a direction, it moves a drone to a given direction by 0.003 degree
	 *
	 * @param to the direction to move to
	 * @return the position
	 */
	public Position move(Direction to) {
		Position nextPos = currentPos.nextPosition(to);
		currentPos = nextPos;
		return currentPos;	
	}
	
	/**
	 * Gets the type of the drone, either stateless or stateful.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Charge, handle the charging of a drone to nearest stations among all stations .
	 * Handles the negatives power or coins of station outweigh the drone's current power or coins
	 *
	 * @param stationsMap the stations map
	 * @return void
	 */
	public void charge(StationsMap stationsMap) {
		String sId = stationsMap.getClosestStation(this.getPosition());
		double distance = StationsMap.calcDistance(this.getPosition(), stationsMap.getStationById(sId).getPosition());
		if(distance >= 0.00025)
			return;
		float station_power = stationsMap.getStationById(sId).getPower();
		float station_coins = stationsMap.getStationById(sId).getCoins();
		
		if ((power + station_power) < 0)
			stationsMap.getStationById(sId).setPower(this.power + station_power);
		else
			stationsMap.getStationById(sId).setPower(0);
			
			
		if ((powerCoin + station_coins) < 0)
			stationsMap.getStationById(sId).setCoins(this.powerCoin + station_coins);
		else
			stationsMap.getStationById(sId).setCoins(0);
		
		// the following functions are the ones that prevent the drone from having negative power or coins.
		addPower(station_power); 
		addPowerCoins(station_coins);
		}
	/**
	 * Adds the power coins
	 * It avoids for drone having negative coins.
	 *
	 * @param coins the coins
	 */
	private void addPowerCoins(float coins) {
		float temp = powerCoin + coins;
		if (temp<0)
			powerCoin = 0;
		else
			powerCoin = temp;
	}
	
	/**
	 * Adds the power.
	 * Avoids for drone having negative power
	 * @param p the p
	 */
	public void addPower(float p) {
		float temp = power + p;
		if (temp<0)
			power = 0;
		else
			power = temp;
	}
	
	/**
	 * Eliminates negative directions (negativeDirections) in given directions. They are all passed by reference.
	 *
	 * @param negativeDirections the negative directions to remove from directions
	 * @param directions the possible directions
	 * 
	 */
	static protected void eliminateNegDirections(List<Direction> negativeDirections, List<Direction> directions) {
		for(Direction rDir: negativeDirections)
			directions.remove(rDir);
		
	}
}
