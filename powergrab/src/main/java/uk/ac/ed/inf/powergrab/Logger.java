package uk.ac.ed.inf.powergrab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Logger {
	
	public static void saveJson(String jsonString, String filename) 
	{
		jsonString = toFormattedJson(jsonString);
		
		try (OutputStream out = new FileOutputStream(filename+".geojson")) {
 
			byte[] bytes = jsonString.getBytes();
            out.write(bytes);
            out.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
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
	
	
	
	public static String toFormattedJson(String jsonString) 
	  {
	      JsonParser parser = new JsonParser();
	      JsonObject json = parser.parse(jsonString).getAsJsonObject();
	      Gson gson = new GsonBuilder().setPrettyPrinting().create();
	      String formattedJson = gson.toJson(json);

	      return formattedJson;
	  }

}
