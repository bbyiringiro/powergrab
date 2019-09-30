package uk.ac.ed.inf.powergrab;

class Date {
	
	String day;
	String month;
	String year;
	
	public Date(String d,String m, String y)
	{
		day = d;
		month = m;
		year = y;
	}
	
	public String formatDate(String delimiter)
	{
		if (delimiter.equals(""))
			delimiter ="/";
		
		return day+delimiter+month+delimiter+year;
	}

}
