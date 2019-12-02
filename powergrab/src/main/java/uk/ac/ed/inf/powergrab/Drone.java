package uk.ac.ed.inf.powergrab;
import java.util.Random;
import java.util.List;
import uk.ac.ed.inf.powergrab.StationsMap.Station;

// TODO: Auto-generated Javadoc
/**
 * The Class Drone.
 */
abstract class Drone {
	
	/** The power. */
	private float power;
	
	/** The power coin. */
	private float powerCoin;
	
	/** The type. */
	String type;
	
	/** The current pos. */
	Position currentPos;
	
	/** The rnd. */
	protected  Random rnd;
	
	/**
	 * Instantiates a new drone.
	 *
	 * @param pos the pos
	 * @param seed the seed
	 * @param type the type
	 */
	public Drone(Position pos, int seed, String type) {
		currentPos = pos;
		this.type = type;
		rnd = new Random(seed);
		power = 250f;
		powerCoin = 0f;
		
	}
	
	/**
	 * Decide.
	 *
	 * @param possibleDirection the possible direction
	 * @param stationsMap the stations map
	 * @return the direction
	 */
	abstract public Direction Decide(List<Direction> possibleDirection, StationsMap stationsMap);
	
	/**
	 * Evaluate utility.
	 *
	 * @param station the station
	 * @return the float
	 */
	abstract protected float evaluateUtility(Station station);
	
	/**
	 * Inspect directions.
	 *
	 * @param stationsMap the stations map
	 * @return the list
	 */
	protected List<Direction> inspectDirections(StationsMap stationsMap){
		return stationsMap.getPossibleMoves(getPosition());
	}
	
	/**
	 * Gets the power.
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
	 * Move.
	 *
	 * @param to the to
	 * @return the position
	 */
	public Position move(Direction to) {
		Position nextPos = currentPos.nextPosition(to);
		currentPos = nextPos;
		return currentPos;	
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Charge.
	 *
	 * @param stationsMap the stations map
	 */
	public void charge(StationsMap stationsMap) {
		String sId = stationsMap.getClosestStation(this.getPosition());
		double distance = StationsMap.calcDistance(this.getPosition(), stationsMap.getStationById(sId).getPosition());
		if(distance >= 0.00025)
			return;
		float station_power = stationsMap.getStationById(sId).getPower();
		float station_coins = stationsMap.getStationById(sId).getCoins();
		System.out.println("Station -> Coins: "+station_coins+" power: "+station_power);
		System.out.println("Before Drone -> Coins: "+powerCoin+" power: "+power);
		// at the station now
		if(station_coins < 0 || station_power < 0) {
			if ((power + station_power) < 0)
				stationsMap.getStationById(sId).setPower(this.power + station_power);
			else
				stationsMap.getStationById(sId).setPower(0);
			
			if ((powerCoin + station_coins) < 0)
				stationsMap.getStationById(sId).setCoins(this.powerCoin + station_coins);
			else
				stationsMap.getStationById(sId).setCoins(0);
		}
		else {
			stationsMap.getStationById(sId).setCoins(0);
			stationsMap.getStationById(sId).setPower(0);
			
		}
		
		addPower(station_power);
		addPowerCoins(station_coins);
		System.out.println("After Drone -> Coins: "+powerCoin+" power: "+power);
		System.out.println("After Station -> Coins: "+stationsMap.getStationById(sId).getCoins()+" power: "+stationsMap.getStationById(sId).getPower());
	}
	
	
	/**
	 * Adds the power coins.
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
	 *
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
	 * Eliminate neg directions.
	 *
	 * @param negativeDirections the negative directions
	 * @param possibleDirections the possible directions
	 */
	static protected void eliminateNegDirections(List<Direction> negativeDirections, List<Direction> possibleDirections) {
		for(Direction rDir: negativeDirections)
			possibleDirections.remove(rDir);
		
	}
}
