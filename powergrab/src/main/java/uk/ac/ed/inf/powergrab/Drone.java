package uk.ac.ed.inf.powergrab;
import java.util.Random;
import java.util.List;

abstract class Drone {
	float power;
	float powerCoin;
	String type;
	Position currentPos;
	protected  Random rnd;
	
	public Drone(Position pos, int seed, String type) {
		currentPos = pos;
		this.type = type;
		rnd = new Random(seed);
		power = 250f;
		powerCoin = 0f;
		
	}
	
	abstract public Direction Decide(List<Direction> possibleDirection, StationsMap stationsMap);
	abstract protected float evaluateUtility(Station station);
	
	public float getPower() {
		return power;
	}
	public float getPowerCoins() {
		return powerCoin;
	}
	public void consumedPower(float p) {
		
		float temp = power-p;
		if (temp <0)
			power = 0;
		else
			power -= p;

	}
	public Position getPosition() {
		return currentPos;
	}
	
	public Position move(Direction to) {
		Position nextPos = currentPos.nextPosition(to);
		currentPos = nextPos;
		return currentPos;	
	}
	public String getType() {
		return type;
	}
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
	
	
	private void addPowerCoins(float coins) {
		float temp = powerCoin + coins;
		if (temp<0)
			powerCoin = 0;
		else
			powerCoin = temp;
	}
	
	private void addPower(float p) {
		float temp = power + p;
		if (temp<0)
			power = 0;
		else
			power = temp;
	}
	
	
	
	protected void eliminateNegDirections(List<Direction> negativeDirections, List<Direction> possibleDirections) {
		for(Direction rDir: negativeDirections)
			possibleDirections.remove(rDir);
		
	}
}
