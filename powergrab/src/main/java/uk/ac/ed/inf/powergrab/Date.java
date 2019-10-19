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
	
	public String formatDate(String delimiter, boolean reverse)
	{
		if (delimiter.equals(""))
			delimiter ="/";
		if(reverse)
			return year+delimiter+month+delimiter+day;
		else
			return day+delimiter+month+delimiter+year;
	}

}
