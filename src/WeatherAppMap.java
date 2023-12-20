

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



// UZYWAM OPEN WETAHER API ORAZ google static map API
public class WeatherAppMap {

	public static void getWeatherMap(String locationName) {

		// pozyksuje dane o longitude oraz laltidude z klasy WeatherApp (uzywam
		// geocoding APP)
		JSONArray locationData = WeatherApp.getLocationData(locationName);
		JSONObject location = (JSONObject) locationData.get(0);
		double latitude = (double) location.get("latitude");
		double longitude = (double) location.get("longitude");

		// Musimy zamienic latidtude i longitude na x tile cordinator oraz y ,DLATEGO BO
		// LINK WYMAGA TEGO po prostu
		int[] tabXYCordinate = new int[2];// deklaruje tablice 2 elementową
		// przykladowy zoom na 3 mozesz wybrac pomiedzy 3-7
		tabXYCordinate = latLngToCoords(4, latitude, longitude);

		// API OPENWEATHER API
		// zoom na 4 ustawiam
		String imageUrltemp = "https://tile.openweathermap.org/map/temp_new/4/" + tabXYCordinate[0] + "/"
				+ tabXYCordinate[1] + ".png?appid=25c6272a7ec8a10f6ba85cfbb0fa38ee";
		String imageUrlrain = "https://tile.openweathermap.org/map/precipitation_new/4/" + tabXYCordinate[0] + "/"
				+ tabXYCordinate[1] + ".png?appid=25c6272a7ec8a10f6ba85cfbb0fa38ee";

		// API geopify do mapy(alternatywa dla googla)
		String mapUrl = "https://maps.geoapify.com/v1/staticmap?style=osm-carto&width=256&height=256&center=lonlat:"
				+ longitude + "," + latitude + "&zoom=4&apiKey=68db5f23e36b48be9be3c6f564ac1b13";

		// sciezka odpowiednio do Weather image (mamy layer do mapy dla temperatury oraz
		// layer dla opadow)
		String destinationFileWeatherMapTemp = "src/assets/tempImage.png";
		String destinationFileWeatherMapRain = "src/assets/rainImage.png";

		// sciezka do niezedytowanej mapy , sciezki do dwoch map juz po (nalozeniu na
		// siebie zdjecia mapy oraz layeru)
		String destinationFileMap = "src/assets/map.png";
		String combinationImageTemp = "src/assets/combined.png";
		String combinationImageRain = "src/assets/combined2.png";

		// pobierz obraz z wybranego API
		getMapFromAPI(destinationFileWeatherMapTemp, imageUrltemp);
		getMapFromAPI(destinationFileWeatherMapRain, imageUrlrain);
		getMapFromAPI(destinationFileMap, mapUrl);

		//tworze nowe dwa obrazy (kotre są polaczeniem nieedytowanej mapy oraz odpwoiedniego layeru
		mergeTwoImages(destinationFileMap, destinationFileWeatherMapTemp, combinationImageTemp);
		mergeTwoImages(destinationFileMap, destinationFileWeatherMapRain, combinationImageRain);
	}

	
	
	
	
//metoda by zamienic długość geograficzna i szerokosc na X ,Y Cordynaty 
	public static int[] latLngToCoords(int zoom, double lat, double lon) {
		int tabLatLngConversion[] = new int[2];
		double n = Math.pow(2, zoom);
		double lat_rad = lat * (Math.PI / 180);
		double x_coord = n * ((lon + 180) / 360);
		double y_coord = (0.5) * n * (1 - (Math.log(Math.tan(lat_rad) + (1 / Math.cos(lat_rad))) / Math.PI));
		tabLatLngConversion[0] = (int) Math.floor(x_coord);
		tabLatLngConversion[1] = (int) Math.floor(y_coord);
		return tabLatLngConversion;
	}
	
	
	
	

	public static void getMapFromAPI(String destinationFile, String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			// z czego bede czytał i dokad bede zapisywał 
			InputStream is = url.openStream(); // odczyt z api
			OutputStream os = new FileOutputStream(destinationFile); // obiekt by zapisac do pliku obraz

			byte[] b = new byte[2048];
			int length;
				//czytamy wszystkie   bytes  z instancji  inputtsream  i zapis  do tablicy bytow b 
			while ((length = is.read(b)) != -1) {
				// zapis do pliku, 0 to pierwszy elemnet w tablicy byte[0] a length ostatni byte[lenght]
			
				os.write(b, 0, length);
			}

			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//return destinationFile;
	}

	
	
	
	
	public static void mergeTwoImages(String map, String Mapoverlay, String combineImage) {
		// sciezki do plikow
		File file1 = new File(map);
		File file2 = new File(Mapoverlay);
		try {

			 //tworze obiekty  buffered image 
			BufferedImage image = ImageIO.read(file1);
			BufferedImage overlay = ImageIO.read(file2);

			// tworze nowy obraz ktory jest kombinacja dwoch,
			// kombinacja wymiarow by pasował po nałozeniu na siebie
			int w = Math.max(image.getWidth(), overlay.getWidth());
			int h = Math.max(image.getHeight(), overlay.getHeight());
			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		

			// towrze obiekt typu graphics i on nam umozliwi narysowanie(nałozenie) na
			// obrazie combined , tych dwoch zdjec co z APi wzielismy
			Graphics g = combined.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.drawImage(overlay, 0, 0, null);

			// zwalniam pamiec ,usuwam wszystko z obiektu Graphics.
			g.dispose();

			// Zapisuje jako nowe zdjecie
			ImageIO.write(combined, "PNG", new File(combineImage));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
