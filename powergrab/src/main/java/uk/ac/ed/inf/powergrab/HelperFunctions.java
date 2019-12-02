package uk.ac.ed.inf.powergrab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// TODO: Auto-generated Javadoc
/**
 * The Class HelperFunctions.
 */
public final class HelperFunctions {
	
	
	
	
	/**
	 * Save json.
	 *
	 * @param jsonString the json string
	 * @param filename the filename
	 */
	public static void saveJson(String jsonString, String filename) 
	{
		jsonString = prettifyJson(jsonString);
		
		try (OutputStream out = new FileOutputStream(filename+".geojson")) {
 
			byte[] bytes = jsonString.getBytes();
            out.write(bytes);
            out.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	/**
	 * Save logs.
	 *
	 * @param logs the logs
	 * @param filename the filename
	 */
	public static void saveLogs(String logs, String filename)
	{
		logs = logs.trim();
		try (OutputStream out = new FileOutputStream(filename+".txt")) {
 
			byte[] bytes = logs.getBytes();
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	
	/**
	 * Prettify json.
	 *
	 * @param jsonString the json string
	 * @return the string
	 */
	public static String prettifyJson(String jsonString) 
	  {
	      JsonParser parser = new JsonParser();
	      JsonObject json = parser.parse(jsonString).getAsJsonObject();
	      Gson gson = new GsonBuilder().setPrettyPrinting().create();
	      String formattedJson = gson.toJson(json);

	      return formattedJson;
	  }
	
	/**
	 * Format url.
	 *
	 * @param mapDate the map date
	 * @param baseUrl the base url
	 * @param geoJsonFile the geo json file
	 * @return the string
	 */
	public static String formatUrl(StationsMap.MapDate mapDate, String baseUrl, String geoJsonFile) {
		String mapDateIndex;
		if(baseUrl.equals("")) {
			baseUrl = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
		}
		
		if(geoJsonFile.equals("")) {
			geoJsonFile = "powergrabmap.geojson";
		}
		
		if (mapDate == null) {
			mapDateIndex = "";
		}
		else
			mapDateIndex =  mapDate.formatDate("", true)+"/";
		
		return baseUrl +mapDateIndex+geoJsonFile;
	}
	

}
