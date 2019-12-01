package uk.ac.ed.inf.powergrab;

public class Station {
	final private String id;
	final private Position position;
	private float coins;
	private float power;
	public Station(String id, float coins, float power, Position position) {
		this.id = id;
		this.setCoins(coins);
		this.setPower(power);
		this.position = position;
	}
	public String getId() {
		return id;
	}
	public float getCoins() {
		return coins;
	}
	public void setCoins(float coins) {
		this.coins = coins;
	}
	public float getPower() {
		return power;
	}
	public void setPower(float power) {
		this.power = power;
	}
	public Position getPosition() {
		return position;
	}

}
