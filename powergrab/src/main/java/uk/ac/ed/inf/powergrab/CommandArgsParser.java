package uk.ac.ed.inf.powergrab;

/**
 * The Class CommandParser parses and validates the command arguments 
 * passed to the run the drone simulation.
 */
public class CommandArgsParser {
	
	/**  The date of the map. */
	private StationsMap.MapDate mapDate;
	
	/** The initial drone's starting position. */
	private Position startCorridinate;
	
	/** The random seed generating number. */
	private int generator;
	
	/**  The drone type to use. */
	private String droneType;
	
	/** Check whether the drone is the stateful or stateless. */
	private boolean isStateful;
	
	
	/**
	 * Instantiates a new command parser.
	 *
	 * @param args the args
	 */
	public CommandArgsParser(String args[]){
		
		try {
			parse(args);
		}
		catch (IllegalArgumentException e) {
			throw e;
		}
	}
	
	/**
	 * Validates and Parses the command arguments.
	 *
	 * @param args the args
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	private void parse(String args[]) throws IllegalArgumentException {
		if (args.length != 7)
			throw new IllegalArgumentException("The argumment should be in this format"
					+ "dd mm yyyy latitude longitute randomGenerator droneType");
		for(int i =0; i<args.length-1; ++i)
			if(!isAllowedNumeric(args[i])) {
				throw new IllegalArgumentException("The argumment should be of the format"
						+ "dd mm yyyy latitude longitute randomGenerator droneType");
			}
		
		// validates the arguments
		String day = args[0].length() == 1 ? '0'+args[0]:args[0];
		String month = args[1].length() == 1 ? '0'+args[1]:args[1];
		String year = args[2];
		if(day.length()> 2 || month.length() > 2 || year.length() > 4)
			throw new IllegalArgumentException("First 3 arguments for date "
					+ "should have the format: dd mm yyyy");
		mapDate = new StationsMap.MapDate(day,  month, year);
		startCorridinate =  new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
		generator = Integer.parseInt((args[5]));
		
		String dType =args[6].toLowerCase();
		if (dType.equals("stateful")) {
			droneType = dType;
			isStateful = true;
		}else if (dType.equals("stateless")) {
			droneType = dType;
			isStateful = false;
		}else 
			throw new IllegalArgumentException(args[4] + "the drone type can "
					+ " either be stateless or stateful, not" +args[4]);
	
	}
	
	/**
	 * Gets the drone type.
	 *
	 * @return the drone type
	 */
	public String getDroneType() {
		return droneType;
	}
	
	/**
	 * Gets the map date.
	 *
	 * @return the map date
	 */
	public StationsMap.MapDate getMapDate() {
		return mapDate;
	}
	
	/**
	 * Gets the starting coordinate.
	 *
	 * @return the start coordinate
	 */
	public Position getStartCorridinate() {
		return startCorridinate;
	}
	
	/**
	 * Checks if is stateful.
	 *
	 * @return true, if is stateful
	 */
	public boolean isStateful() {
		return isStateful;
	}
	
	/**
	 * Gets the generator.
	 *
	 * @return the generator
	 */
	public int getGenerator() {
		return generator;
	}
	
	
	/**
	 * Checks helper function to check if a string is 2-4 digit number or floating numbers.
	 *
	 * @param str the str
	 * @return true, if is allowed numeric
	 */
	private static boolean isAllowedNumeric(final String str) {
		if(str.length() > 4) {
			try {
				Double.parseDouble(str);
				return true;
			}catch(NumberFormatException e) {
				return false;
			}
		}else {
			try {
				Integer.parseInt(str);
				return true;
			}catch(NumberFormatException e) {
				return false;
			}
		}
		
		
		
	}
	
}
