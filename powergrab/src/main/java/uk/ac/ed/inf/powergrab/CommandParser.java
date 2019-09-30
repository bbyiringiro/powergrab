package uk.ac.ed.inf.powergrab;


public class CommandParser {
	
	private Date mapDate;
	private Position startCorridinate;
	private int generator;
	private boolean isStateful;
	
	
	public CommandParser(String args[]){
		
		try {
			parse(args);
		}
		catch (Exception e) {
			System.out.print(e);
		}
	}
	private void parse(String args[]) throws Exception {
		if (args.length != 7)
			throw new Exception("This program need sevenn arguments, format....");
		
		mapDate = new Date(args[0],  args[1], args[2]);
		startCorridinate =  new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
		generator = Integer.parseInt((args[5]));
		
		if (args[6].equals("stateful"))
			isStateful = true;
		else if (args[6].equals("stateless"))
			isStateful = false;
		else 
			throw new Exception(args[4] + "state was not recognized, the drone can either be stateless or stateful");
	
	}
	
	public Date getMapDate() {
		return mapDate;
	}
	public Position getStartCorridinate() {
		return startCorridinate;
	}
	public boolean isStateful() {
		return isStateful;
	}
	public int getGenerator() {
		return generator;
	}
	
}
