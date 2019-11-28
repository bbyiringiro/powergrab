package uk.ac.ed.inf.powergrab;


// TODO: Auto-generated Javadoc
/**
 * The Class CommandParser.
 */
public class CommandArgsParser {
	
	/** The map date. */
	private Date mapDate;
	
	/** The start corridinate. */
	private Position startCorridinate;
	
	/** The generator. */
	private int generator;
	
	/** The is stateful. */
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
		catch (Exception e) {
			System.out.print(e);
		}
	}
	
	/**
	 * Parses the.
	 *
	 * @param args the args
	 * @throws Exception the exception
	 */
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
	
	/**
	 * Gets the map date.
	 *
	 * @return the map date
	 */
	public Date getMapDate() {
		return mapDate;
	}
	
	/**
	 * Gets the start corridinate.
	 *
	 * @return the start corridinate
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
	
}
