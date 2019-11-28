package uk.ac.ed.inf.powergrab;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GeoJsonHandler{
	
	public GeoJsonHandler() {
//		mapString = mapURL;
	}
	
	public static String readJsonFromURL(String url) throws MalformedURLException, IOException {
		
		InputStream inputStream;
//		try 
//		{
			URL mapUrl = new URL(url);
			HttpURLConnection request = (HttpURLConnection) mapUrl.openConnection();
			request.setReadTimeout(10000); // milliseconds
			request.setConnectTimeout(15000); // milliseconds
			request.setRequestMethod("GET");
			request.setDoInput(true);
			request.connect();
//			try
			inputStream = request.getInputStream();
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader(inputStream));
			
			String mapSource = root.toString();
			inputStream.close();
//			catch
			
			
			
			return mapSource;
//		}
//		catch(MalformedURLException e) 
//		{
//			
//		}
//		catch (IOException e) {
//			// TODO: handle exception
//		}
//		finally 
//		{
//			
//		}
		
	}
	
	
  
	
	
	
}