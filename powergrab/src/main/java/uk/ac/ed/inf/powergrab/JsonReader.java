package uk.ac.ed.inf.powergrab;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  public static void main(String[] args) throws IOException, JSONException {
    JSONObject json = readJsonFromUrl("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson");
//    System.out.println(json.toString());
    System.out.println(json.get("type"));
  }
}





//String sURL = "http://freegeoip.net/json/"; //just a string
//
//// Connect to the URL using java's native library
//URL url = new URL(sURL);
//URLConnection request = url.openConnection();
//request.connect();
//
//// Convert to a JSON object to print data
//JsonParser jp = new JsonParser(); //from gson
//JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
//JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
//String zipcode = rootobj.get("zip_code").getAsString(); //just grab the zipcode



//<dependency>
//<groupId>com.google.code.gson</groupId>
//<artifactId>gson</artifactId>
//<version>2.8.5</version>
//</dependency>


//mapbox
//map.addSource('some id', {
//	  type: 'geojson',
//	  data: 'https://mydomain.mydata.geojson'
//	});


//https://docs.mapbox.com/help/troubleshooting/working-with-large-geojson-data/

//https://docs.mapbox.com/ios/api/maps/3.5.0-beta.2/working-with-geojson-data.html