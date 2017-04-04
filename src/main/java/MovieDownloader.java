import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		try {
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie"; //adds the query string to the omdb api search url
		}catch(UnsupportedEncodingException uee){ // catches a bad encoding exception and returns null
			return null;
		}

		HttpURLConnection urlConnection = null; //creates an http connection to send the request with
		BufferedReader reader = null; //creates a reader to read text from a characer input stream

		String[] movies = null;

		try {

			URL url = new URL(urlString);

			urlConnection = (HttpURLConnection) url.openConnection(); // opens http connection
			urlConnection.setRequestMethod("GET"); // sets http request method to GET
			urlConnection.connect(); // connects to api

			InputStream inputStream = urlConnection.getInputStream(); //reads response from http request
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream)); // uses data from inputstream to read line by line

			String line = reader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}

			if (buffer.length() == 0) {
				return null;
			}
			String results = buffer.toString();
			results = results.replace("{\"Search\":[",""); //formats string into proper output
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			movies = results.split("\n");
		} 
		catch (IOException e) {
			return null;
		} 
		finally { //disconnects from http connection and closes reader
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
				}
			}
		}

		return movies;
	}


	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
