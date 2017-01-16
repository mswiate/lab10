package lab10;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

public class Chatbot {
	private final static String apiid = "1818e5f1f8165f9561fcf2ce9193e86b";
	private final static String cracowId = "3094802";
	
	public Chatbot(){
		
	}
	
	public static String getAnswer(String question){
		switch(question){
		case "Która godzina?":
			return getHour();
		case "Jaki dziœ dzieñ tygodnia?":
			return getDay();
		case "Jaka jest pogoda w Krakowie?":
			return getWeather();
		default:
			return "Niestety nie znam odpowiedzi na wszystkie pytania. "
					+ "Za to bardzo dobrze znam odpowiedz na te trzy pytania:"
					+ "Która godzina?"
					+ "Jaki dziœ dzieñ tygodnia?"
					+ "Jaka jest pogoda w Krakowie?";
		}
	}
	
	private static String getWeather() {
		StringBuilder weather = new StringBuilder();
		String url = "http://api.openweathermap.org/data/2.5/weather?id=" + cracowId + "&APPID=" + apiid;
		try(BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new URL(url).openStream(), Charset.forName("UTF-8")));){
			for(String line ; ( line = br.readLine() ) != null ; ){
				weather.append(line);
			}
			
			JSONObject jsonWeather = new JSONObject(weather.toString());
			
			String main = jsonWeather.getJSONArray("weather").getJSONObject(0).getString("main");
			String temp = jsonWeather.getJSONObject("main").getDouble("temp") + "";
			
			return "temperatura: " + temp + " ogólnie(in English): " + main;
		}
		catch(Exception ex){
			
		}
		
		
		return "nie udalo sie pobrac informacji o pogodzie";
	}

	private static String getHour() {
		return "Napisz \"coœ\" i popatrz w prawy, górny róg wiadomoœci :)";
		
	}

	private static String getDay(){
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		// 3 letter name form of the day
		return new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
	}
	
}
