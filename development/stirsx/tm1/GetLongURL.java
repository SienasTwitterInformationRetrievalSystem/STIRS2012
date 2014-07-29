package stirsx.tm1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class GetLongURL {

	/**
	 * args[0] the current URL or line you wish your program to start at within
	 * the list
	 * 
	 * args[1] the current URL or line you wish your program to end at within
	 * the list
	 * 
	 * args[2] the proper file with the URLS. The proper file is one that
	 * contains a TweetID followed by a url and nothing else , with only Tweet
	 * ID and url per line
	 * 
	 * args[3] the name of the outputFile you wish to with the expanded URL
	 */
	public static void main(String args[]) throws IOException {
		// current URL in the list where you want to start
		int startURL = Integer.parseInt(args[0]);
		int endURL = Integer.parseInt(args[1]);
		
		// current URL we are on
		int currentURL = 0;

		// new file
		File outputFile = new File(args[3]);
		File inputFile = new File(args[2]);
		
		// BufferedReader to read file
		BufferedReader bPos = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFile)));
		PrintWriter writer = new PrintWriter(outputFile);

		String currentDir = outputFile.getParent();
		File failed = new File(currentDir + "\\" + "failedURLsFrom" + args[0]
				+ "to" + args[1] + ".txt");
		PrintWriter failedWriter = new PrintWriter(failed);

		// the current text of the line
		String line;

		while (startURL != currentURL) {
			line = bPos.readLine();
			currentURL++;
		}

		line = bPos.readLine();
		int success = 0;
		int failure = 0;

		while (line != null && endURL != currentURL) {
			try {
				// this gets the TweetID and URL on each line
				String[] information = line.split(" ");
				String urlAddress = information[1];
				String expandedURL;
				URL url = new URL(urlAddress);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection(Proxy.NO_PROXY);
				connection.setInstanceFollowRedirects(false);
				connection.connect();
				expandedURL = connection.getHeaderField("Location");
				connection.getInputStream().close();

				if (expandedURL == null) {
					failure++;
					System.out.println(currentURL + " " + line + " NOT FOUND");
					failedWriter.println(line);
					failedWriter.flush();
				}else {
					success++;
					System.out.println(currentURL + " " + line + " "
							+ expandedURL);
					writer.println(line + " " + expandedURL);
					writer.flush();
				}
			}catch (Exception e) {
				failure++;
				failedWriter.println(line);
				failedWriter.flush();
			}
			
			currentURL++;
			line = bPos.readLine();
		}
		
		writer.close();
		failedWriter.close();
		bPos.close();

		// success rate of all the tweets
		double successRate = success / (1.0 * (failure + success));
		System.out.println("Success Rate = " + successRate);
	}
}