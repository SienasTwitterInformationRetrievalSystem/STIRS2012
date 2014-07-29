package STIRS.Lucene;

import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * RankedTweetList holds an ArrayList of Tweets in the order that Lucene outputs
 * 
 * @author Dr. Sharon Small & Carl Tompkins v1.0
 * @version 7/5/2011 v1.0
 */
public class RankedTweetList {

	//private static final long serialVersionUID = 1L;
	private ArrayList<Tweet> tweets;

	/**
	 * Constructor initializes the tweet list and the number of tweet list
	 * 
	 * @param tweets
	 *            the ArrayList that contains the Tweets
	 */
	public RankedTweetList(ArrayList<Tweet> tweets) {
		this.tweets = tweets;
	}

	public ArrayList<Tweet> getRankedList() {
		return tweets;
	}

	/**
	 * getTweet() returns a the tweet at the specific index
	 * 
	 * @param tweetNum
	 *            the index that the tweet resides at (0 -> size of list)
	 * @return Tweet located at tweetNum
	 */
	public Tweet getTweet(int tweetNum) {
		return tweets.get(tweetNum);
	}

	/**
	 * size() returns the number of tweets that were found
	 */
	public int size() {
		return tweets.size();
	}

	public String toString() {
		return "RankedTweetList [tweets=" + tweets + ", numTweets=" + size()
				+ "]";
	}

	/**
	 * Saves the list to file with each Tweet being on a tab separated line
	 * 
	 * @param fName
	 *            The desired file name
	 * @return true if the save was successful, false otherwise
	 */
	public boolean save(String fName) {
		try {
			PrintWriter p = new PrintWriter(new FileWriter(fName));
			
			for (Tweet t : tweets) {
				p.println(t.toExcel());
			}
			
			p.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void remove(int i) {
		tweets.remove(i);
	}
}