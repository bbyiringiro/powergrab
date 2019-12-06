package uk.ac.ed.inf.powergrab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uk.ac.ed.inf.powergrab.StationsMap.MapDate;

/**
 * The Class IOUtils is a utility class that contains all helper functions used when were saving output files
 * It mostly deals with IO and related formatting.
 */
public final class IOUtils {
	/**
	 * Save json, given a json string it saves it with  filename .geojson file by first prettying it.
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
	 * given a filename, and logs string, it save the string in $filename.txt extension file of given string.
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
	 * Prettify Json string,  prettify Json string to be saved in structured format.
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
	 * generate the file name, given a drone and a map date,
	 *  it generates a standard filename that is used to save geoJson Map and .txt logs.
	 * @param drone the drone
	 * @param mapDate the map date
	 * @return the string
	 */
	static String generateFileName(Drone drone, MapDate mapDate) {
		
		return drone.getType() +"-"+mapDate.formatDate("-", false);
		
	}
	
	

}
