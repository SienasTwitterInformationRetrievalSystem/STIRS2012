package tm2;

import java.net.*;
import java.io.*;

/**
 * Scrapes the website Listorious for Twitter users who post on a given topic
 * 
 * @authoer Denis Kalic & Matthew Kemmer v1.0
 * @version 7/14/2011 v1.0
 */
public class RetweetRank {
	private static String RETWEET_TAG = "<rank>";
	private static String PERCENTILE_TAG = "<percentile>";
	private int retweetRank;
	private double retweetPercentile;
	private String urlContent;
	private String userName;
	private boolean goodUrlContent;

	public RetweetRank(String uName) {
		urlContent = "";
		userName = uName;

		retweetRank = -1;
		retweetPercentile = -1;

		if (getURLContent() < 0) {
			goodUrlContent = false;
		} else {
			goodUrlContent = true;

			setRetweetRank();
			setRetweetPercentile();
		}
	}

	public int getRetweetRank() {
		return retweetRank;
	}

	public double getRetweetPercentile() {
		return retweetPercentile;
	}

	public boolean goodUrlContent() {
		return goodUrlContent;
	}

	public void setUserName(String uName) {
		urlContent = "";
		userName = uName;

		if (getURLContent() < 0) {
			goodUrlContent = false;
		} else {
			goodUrlContent = true;
		}
	}

	public void setRetweetRank() {
		if (goodUrlContent) {
			int i = urlContent.indexOf(RETWEET_TAG) + RETWEET_TAG.length();

			char c = urlContent.charAt(i);
			String num = "";
			
			while (c != '<') {
				if (Character.isDigit(c)) {
					num += c;
				}

				c = urlContent.charAt(++i);
			}

			if (num.length() > 0) {
				retweetRank = Integer.parseInt(num);
			}
		}
	}

	public void setRetweetPercentile() {
		if (goodUrlContent) {
			int i = urlContent.indexOf(PERCENTILE_TAG)
					+ PERCENTILE_TAG.length();

			char c = urlContent.charAt(i);
			String num = "";
			
			while (c != '<') {
				if (Character.isDigit(c) || c == '.') {
					num += c;
				}

				c = urlContent.charAt(++i);
			}

			if (num.length() > 0) {
				retweetPercentile = Double.parseDouble(num) / 100;
			}
		}
	}

	public int getURLContent() {
		try {

			URL u = new URL("http://api.retweetrank.com/rank/" + userName
					+ ".xml?appid=622ce21924b13ed13b6ff5046a7a0e25");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					u.openStream()));

			String line = reader.readLine();
			while (line != null) {
				urlContent += " " + line;

				line = reader.readLine();
			}

			reader.close();

			return 1;
		} catch (MalformedURLException e) {
			return -1;
		} catch (IOException e) {
			return -1;
		}
	}
}