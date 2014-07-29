package STIRS.Lucene;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Tweet class holds information that is related to a tweet; for example,
 * the tweetID, the status, the date, and the relevance score given to the Tweet
 * by Lucene.
 * 
 * @author Matthew Kemmer and Carl Tompkins v1.0
 * @author David Purcell v1.5
 * @author Karl Appel and Lauren Mathews v2.0
 * 
 * @version 6/2011 v1.5
 * @version 7/25/12 v2.0
 */
public class Tweet implements Comparable<Tweet> {
	String tweetID = null, status = null, username = null, date = null;
	String topicNum;
	int docNum = -1, rankInResult;
	float score = -1;
	String tag = "";
	String decision = "";

	public Tweet(String tweetID, String status, String username, String date,
			String topicNum, int docNum, int rankInResult, float score) {
		this.tweetID = tweetID;
		this.status = status;
		this.username = username;
		this.date = date;
		this.docNum = docNum;
		this.score = score;
		this.topicNum = topicNum;
		this.rankInResult = rankInResult;

	}

	/**
	 * Compares two Tweet objects, based on their score field
	 * 
	 * @param o
	 *            A Tweet object
	 * 
	 * @return 1 if o has a greater score, -1 if o has a lower score, 0 if equal
	 */
	public int compareTo(Tweet o) {
		return Float.compare(o.score, this.score);
	}

	/**
	 * Formats this Tweet as to TREC's liking
	 * 
	 * @return A single line in the TREC format.
	 */
	public String format(boolean raw) {
		String format;

		if (raw) {
			format = topicNum + " Q0 " + tweetID + " " + rankInResult + " "
					+ score + " TestRun";
		} else {
			format = " ," + topicNum + "," + tweetID + ",\"" + status + "\",";
			Pattern pattern = Pattern.compile("http://[A-Za-z./%&?=;0-9\\-_]+");
			Matcher matcher = pattern.matcher(status);
			while (matcher.find()) {
				format += "\"=HYPERLINK(\"\"" + matcher.group()
						+ "\"\",\"\"LINK\"\")\"";
			}

		}

		return format;
	}

	public String format(boolean raw, String task) {
		String format = "";

		if (!raw) {
			format = topicNum + "," + tweetID + ",\"" + status + "\",";
			Pattern pattern = Pattern.compile("http://[A-Za-z./%&?=;0-9\\-_]+");
			Matcher matcher = pattern.matcher(status);

			while (matcher.find()) {
				format += "\"=HYPERLINK(\"\"" + matcher.group()
						+ "\"\",\"\"LINK\"\")\"";
			}
		} else if (task == null) {

			format = topicNum + " Q0 " + tweetID + " " + rankInResult + " "
					+ score + tag;

		} else if (task.equals("adhoc")) {
			format = topicNum + " " + tweetID + " " + score + " " + tag;
		} else if (task.equals("filtering")) {
			format = topicNum + " " + tweetID + " " + score + " " + decision
					+ " " + tag;
		}

		return format;
	}

	public String getTweetID() {
		return tweetID;
	}

	public void setTweetID(String tweetID) {
		this.tweetID = tweetID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDate() {
		return date;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getDocNum() {
		return docNum;
	}

	public void setDocNum(int docNum) {
		this.docNum = docNum;
	}

	/**
	 * Sets the rank for this Tweet based on its score compared to others tied
	 * to the same query.
	 * 
	 * @param r
	 *            The rank for this Tweet
	 */
	public void setRank(int r) {
		rankInResult = r;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int hashCode() {
		return new BigInteger(tweetID).hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Tweet)) {
			return false;
		}

		return tweetID.equals(((Tweet) obj).tweetID);
	}

	public String toString() {
		String tweetString = "[";
		tweetString += "Tweet ID: " + tweetID + ", ";
		tweetString += "Tweet Status: " + status + ", ";
		tweetString += "Tweet Username: " + username + ", ";
		tweetString += "Tweet Date: " + date + "]";
		return tweetString;
	}

	public String toExcel() {
		String tweetString = tweetID;
		tweetString += "\t" + status;
		tweetString += "\t" + username;
		tweetString += "\t" + date;
		tweetString += "\t" + score;
		return tweetString;
	}

	public int getTopic() {
		return Integer.parseInt(topicNum.substring(2));
	}

	public void setTopic(int newNumber) {
		if (newNumber < 10) {
			topicNum = "MB00" + newNumber;
		} else if (newNumber < 100) {
			this.topicNum = "MB0" + newNumber;
		} else if (newNumber < 1000) {
			this.topicNum = "MB" + newNumber;
		}
	}
}