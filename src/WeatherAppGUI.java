import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.json.simple.JSONObject;

public class WeatherAppGUI  extends JFrame{
private JSONObject weatherData;
	
	public WeatherAppGUI(){
		setTitle("CHECK THE WEATHER");
		//program bo nacisnieciu x przestaje dzialac
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//rozmiar okna  
		setSize(450,800);
		
		
		//kolor tła
		Color color=new Color(245, 255, 255);
		getContentPane().setBackground(color);
		
		//chcemy aby okno było w centrum ekranu
		setLocationRelativeTo(null);
		
		
		//manualnie bedziemy ustawiac obiekty
		setLayout(null);
		
		//ochrona przed proba zmiany rozmiaru okienka
		setResizable(false);
		
		
		addComponents();
	}

	private void addComponents() {
		JTextField searchTextField = new JTextField();
		
		//lokalizacja(zmienne x i y)  i rozmiar search baru (szerokosc i dlugosc)
		searchTextField.setBounds(15,15,351,45);
		
		//zmieniam czcionke i ustawiam jej wyglad, rozmiar
		searchTextField.setFont(new Font("Dialog",Font.PLAIN,24));
		
		add(searchTextField);
	
		
		
		//obrazek przedstawiajacy pogode
		JLabel weatherConditionImage= new JLabel(loadImage("src/assets/cloudy.png"));
		weatherConditionImage.setBounds(0,125,450,217);
		add(weatherConditionImage);
		
		
		//ile stopni
		JLabel temperatureText = new JLabel("10°C");
		temperatureText.setBounds(0,350,450,54);
		temperatureText.setFont(new Font("Dialog",Font.BOLD,48));
		
		//wyposrodkowanie liczby na ekranie 
		temperatureText.setHorizontalAlignment(SwingConstants.CENTER);		
		add(temperatureText);
		
		
		//opis pogody
		JLabel weatherConditionDesc = new JLabel("Cloudy");
		weatherConditionDesc.setBounds(0,405,450,36);
		weatherConditionDesc.setFont(new Font("Dialog",Font.PLAIN,32));
		weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);	
		add(weatherConditionDesc);
		
		//wilgotnosc obraz		
		JLabel humidityImage= new JLabel(loadImage("src/assets/humidity.png"));
		humidityImage.setBounds(15,475,74,66);	
		add(humidityImage);
		
		//wilgotonosc text 
		
		JLabel humidityText= new JLabel("<html><b>Humidity</b> 100%</html>");
		humidityText.setBounds(90,475,85,55);	
		humidityText.setFont(new Font("Dialog",Font.PLAIN,16));
		add(humidityText);
		
		
		
		
		//wiatr obraz
		JLabel windImage = new JLabel(loadImage("src/assets/windspeed.png"));
		windImage.setBounds(220,475,74,66);
		add(windImage);
		
		//wiatr Text
		JLabel windText= new JLabel("<html><b>Wind Speed</b> 15 km/h</html>");
		windText.setBounds(320,475,85,55);
		windText.setFont(new Font("Dialog",Font.PLAIN,13));
		add(windText);
		
		
		//dodaje mape z temperatura
		JLabel mapImageTemp = new JLabel(loadImage("src/assets/map.png"));		
		mapImageTemp.setBounds(10,560,200,200);
		mapImageTemp.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		add(mapImageTemp);
		
		
		//dodaje mape z temperatura
		JLabel mapImageRain = new JLabel(loadImage("src/assets/map.png"));		
		mapImageRain.setBounds(225,560,200,200);
		mapImageRain.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		add(mapImageRain);
		
		
		//Search button
		JButton searchButton = new JButton(loadImage("src/assets/search.png"));
		
		//zmienienie kursora na palec kiedy najezdam na przycisk
		searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		searchButton.setBounds(375,13,47,45);
		searchButton.addActionListener(new ActionListener() {

			

			
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//pobierz lokakcje ktora napisał uzytkownik
				String userInput=searchTextField.getText();			//
				
				//usun wszytskie white-spaces z nazwy
				if(userInput.replaceAll("\\s","").length()<=0) {
					return;
				}
				
				//pozysujemy dane z backendu
				weatherData= WeatherApp.getWeatherData(userInput);
				
				String  weatherCondition= (String) weatherData.get("weatherCondition");

				//na podstaawie krotkiego switcha decydujjemy o obrazie 
				switch(weatherCondition) {
				case "Clear":
					weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
					break;
				case "Cloudy":
					weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
					break;
				case "Rain":
					weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
					break;
				case "Snow":
					weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
					break;
				}
				
				//updatuj opis
				weatherConditionDesc.setText(weatherCondition);
				
				//updatuj temperature
				
				double temperature =(double) weatherData.get("temperature");
				temperatureText.setText(temperature +" C");
				
				
				//updatuj humidity
				long humidity = (long) weatherData.get("humidity");
				humidityText.setText("<html><b>Humidity</b> "+humidity+"%</html>");
				
				//updatuj windspeed
				double windspeed=(double) weatherData.get("windspeed");
				windText.setText("<html><b>Wind Speed</b> "+windspeed+"km/h</html>");
				
				
				
				
				//dodajemy mape pogodową 
				WeatherAppMap.getWeatherMap(userInput);	
				mapImageTemp.setIcon(loadImage("src/assets/combined.png"));
				mapImageRain.setIcon(loadImage("src/assets/combined2.png"));
		
			}
			
		});
		add(searchButton);
		
		
		
		
	
		
	}

	private ImageIcon loadImage(String resourcePath) {
		try {
			
			File file= new File(resourcePath);
			BufferedImage img= ImageIO.read(file);
			
			return new ImageIcon(img);
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Could not find resource ");
		return null;
	}
}
