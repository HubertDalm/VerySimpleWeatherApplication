import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


//BEDZIEMY KORZYTSAC GEOCODING API ORAZ Z WEATHER API





// ta klasa bedzie zajmowala sie ekstrkcją dany o pogodzie z API i zwroci ją  
public class WeatherApp implements Consumer {
	
	
	
	
	
	static HttpURLConnection connection;
	 //wyrazenie lamda to sprawdzenia czy mam dobry  request
	static Consumer<String> checkConnection = x ->{ try {
		if(connection.getResponseCode()!=200) System.out.println(x);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}};

	
	public static JSONObject getWeatherData(String locationName) {
		

		
		
		
		
		
		
		
		
		// zwraca coordynaty miasta  bazujac na geolocation API 
		JSONArray locationData=getLocationData(locationName);
		
		
		//pozyskaj latitude and longitude data (Szerkosc i dlugosc geograficzna)
		JSONObject location = (JSONObject) locationData.get(0);
		double latitude = (double) location.get("latitude");
		double longitude = (double) location.get("longitude");
		
		
		//zmieniamy url z pomoca danych ktore pozyskalismy
		String urlString="https://api.open-meteo.com/v1/forecast?"+ "latitude="+latitude+"&longitude="+longitude+"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone";
		
		try {
			//laczymy sie z naszym weatger api
			HttpURLConnection connection= fetchApiResponse(urlString);
			

			
			
		//	if(connection.getResponseCode()!=200){
		//		System.out.println("Could not connect to the API");
		//	}
			checkConnection.accept("Could not connect to the API");
			
			//wynik polaczenia z APi przechowamy w stringBuilderze 
			StringBuilder resultJson= new StringBuilder();
			
			//uzyjemy scanneru zeby zwrociło nam dane 
			Scanner scanner = new Scanner(connection.getInputStream());
			
			//odczytaj i przechowaj w  StringBuilderze 
			while(scanner.hasNext()) {
				resultJson.append(scanner.nextLine());
				
			}
			
			//dobra praktyka zeby nie meic przeciekow danych ,zamykamy  url polaczenie 
			scanner.close();
			connection.disconnect();
			
			
			//parsujemy Json string do Obektu
			JSONParser parser = new JSONParser();
			JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
			
			// pozyskujemy dane o pogodzie ,hourly zwiazane jest z propetry hourly w pliku JSON ktory pozyskalismy
			JSONObject hourly= (JSONObject) resultsJsonObj.get("hourly");

			
			
			//checmy pozyskac dane o naszej godzinie ,wiec musimy miec do tego index,ktory pokazuje nam nasza godzine  z listy 
			JSONArray time = (JSONArray) hourly.get("time");
			int index =findIndexOfCurrentTime(time);
			
		
			
			
			//temperatura dla naszej godziny
			JSONArray temperatureData= (JSONArray) hourly.get("temperature_2m");
			double temperature =(double) temperatureData.get(index);
			
			
			//weathercode
			JSONArray weatherCode= (JSONArray) hourly.get("weather_code");
			String weatherCondition=convertWeatherCode((long) weatherCode.get(index));
			
			
			//wilgotnosc dla naszej godziny 
			JSONArray humidityData= (JSONArray) hourly.get("relative_humidity_2m");
			long humidity =(long) humidityData.get(index);
			//predkosc wiatru dla naszej godziny
			JSONArray windData= (JSONArray) hourly.get("wind_speed_10m");
			double windspeed =(double) windData.get(index);
			
			//zbudujemy teraz wlasny obiekt JSON w oparciu o dane ktore pobralismy (temperature,opis,wilogtnosc,wiatr)
			//ten obiekt przekazemy do GUI
			
			JSONObject weatherData= new JSONObject();
			weatherData.put("temperature", temperature);
			weatherData.put("weatherCondition",weatherCondition);
			weatherData.put("humidity",humidity);
			weatherData.put("windspeed",windspeed);
			
			return weatherData;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		return null;
		
	}
	
	











	//latitude and longtidute data  musi zwroci by pogoda mogła dzialac
	
	
	public  static JSONArray getLocationData(String locationName) {
		//replace any white space with + to adhere to API request format np New York
		locationName=locationName.replaceAll(" ", "+");
		
		// API URL z lokalizacja jako parametr	
		String urlString="https://geocoding-api.open-meteo.com/v1/search?name="+locationName+"&count=10&language=en&format=json";
			
		try {
			
			//sprobujemy zrobic requesta do API zeby zwrocil nam lokalizacje
			//potrezbujemy do tego HTTP request			
			// stowrzymy odzielną do tego metode bo bedziemy jej wielokrotnie uzywac w tej klasie
			 connection = fetchApiResponse(urlString);
			
			
			//sparwdzamy stan polaczenia (zobacz HTTP status Code), 200 ozancza ze wszystko jest ok
			
			
			if(connection.getResponseCode()!=200){
				System.out.println("Could not connect to the API");
				return null;
			}else {
				
				//wynik polaczenia z APi
				StringBuilder resultJson= new StringBuilder();
				
				//uzyjemy scanneru zeby zwrociło nam dane 
				Scanner scanner = new Scanner(connection.getInputStream());
				
				//odczytaj i przechowaj w  StringBuilderze 
				while(scanner.hasNext()) {
					resultJson.append(scanner.nextLine());
					
				}
				
				//dobra praktyka zeby nie meic przeciekow danych
				scanner.close();
				connection.disconnect();

				
				
				//parsujemy Json string do Obektu
				JSONParser parser = new JSONParser();
				JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
				
				// tworzymy liste  obiektow JSON, results zwiazane jest z propetry results w pliku JSON ktory pozyskalismy
				JSONArray locationData= (JSONArray) resultsJsonObj.get("results");
				
				return locationData;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	
	

	public static HttpURLConnection fetchApiResponse(String urlString) {
		
		// attempt to create connection (nawiazujemy polaczenie)
		try {
			URL url= new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			// uzywamy  API call metody GET
			connection.setRequestMethod("GET");
			
			//laczymy sie z naszym API		
			connection.connect();
			
			return connection;
			
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		// nie moglismy sie polaczyc z API
		return null;
	}
	


private static int findIndexOfCurrentTime(JSONArray timeList) {
	String CurrentTime= getCurrentTime();
	
	//stworzymy petle ktora bedzie iterowac az napotka godzinie ktora pokrywa sie z currentTime
	for(int i=0 ;i<timeList.size();i++) {
		String time =(String) timeList.get(i);
		if(time.equals(CurrentTime)) {
			return i;
		}
	}
	
	
	return 0;
	}






public static String getCurrentTime() {
	LocalDateTime current= LocalDateTime.now();
	
	//musimy sformatowac tak by wygladało to jak w naszym API czyli  np 	"2023-12-03T00:00"
	
	DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-mm-dd'T'HH':00'");
	String formatDateTime= current.format(formater);
	
	return formatDateTime;
	}




private static String convertWeatherCode(long weatherCode){

		String weatherCondition="";
	if(weatherCode==0) {
		weatherCondition="Clear";
	}else if(weatherCode>0&&weatherCode<=3) {
		weatherCondition="Cloudy";
	}else if((weatherCode>=51 && weatherCode<=67)||(weatherCode>=80 && weatherCode<=99)){
		weatherCondition="Rain";
	}else if(weatherCode>=71&&weatherCode<=77) {
		weatherCondition="Snow";
	}else if(weatherCode==45 || weatherCode==48) {
		weatherCondition="Fog";
	}
	
		return weatherCondition;
	}








	@Override
	public void accept(Object t) {
		// TODO Auto-generated method stub
		
	}


}





