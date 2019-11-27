package uk.ac.ed.inf.powergrab;

public class Station {
	private String id;
	private double coins;
	private double power;
	private Position position;
	private boolean isPositive = true;
	public Station(String id, double coins, double power, Position position) {
		this.setId(id);
		this.setCoins(coins);
		this.setPower(power);
		this.position = position;
		if(coins <0 || power<0)
			setPositive(false);
	}
	public String getId() {
		return id;
	}
	private void setId(String id) {
		this.id = id;
	}
	public double getCoins() {
		return coins;
	}
	public void setCoins(double coins) {
		this.coins = coins;
	}
	public double getPower() {
		return power;
	}
	public void setPower(double power) {
		this.power = power;
	}
	public boolean isPositive() {
		return isPositive;
	}
	private void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}
	public Position getPosition() {
		return position;
	}

}
