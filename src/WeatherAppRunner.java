import javax.swing.*;

public class WeatherAppRunner {

	public static void main(String[] args) {
		
		
		/*TwOrzymy tread  (EVENT DISPATCH  thread) specjalnie dla GUI		 
		 * to jest thread ktory dziala w nieskonczonej petli na biezaco aktualizuje nasze GUI
		 * wszytkie metody obsługujace zdarzenia Event Handler są tu egzekwowane
		 */
		SwingUtilities.invokeLater(
			new Runnable() {	
				public void run() {
			new WeatherAppGUI().setVisible(true);

					
			
			
				}
	});

	}

}
