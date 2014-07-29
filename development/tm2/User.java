package tm2;

/**
 * Stores Twitter-specific information about a Tweeter
 * 
 * @author Matthew Kemmer v1.0
 * @version 7/14/2011 v1.0
 */
public class User {
	private String userName;
	private int numStatuses;
	private long joinDate;
	private int listCount;
	private int followerCount;
	private int friendCount;
	private boolean isCelebrity;
	private final double DAY_IN_MILLIS = 86400000;

	/**
	 * Creates a User object with the given information
	 * 
	 * @param u
	 *            The username
	 * @param nS
	 *            The number of statuses the user has on Twitter
	 * @param j
	 *            The join date of the user on Twitter
	 * @param lC
	 *            The number of Twitter lists the user appears on
	 * @param folC
	 *            The number of followers the user has on Twitter
	 * @param frC
	 *            The number of friends the user has on Twitter
	 * @param c
	 *            True if the user is a Twitter-verified celebrity, false
	 *            otherwise
	 */
	public User(String u, int nS, long j, int lC, int folC, int frC, boolean c) {
		userName = u;
		numStatuses = nS;
		joinDate = j;
		listCount = lC;
		followerCount = folC;
		friendCount = frC;
		isCelebrity = c;
	}

	public String getUserName() {
		return userName;
	}

	public int getNumStatuses() {
		return numStatuses;
	}

	public long getJoinDate() {
		return joinDate;
	}

	public int getListCount() {
		return listCount;
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public int getFriendCount() {
		return friendCount;
	}

	public boolean isCelebrity() {
		return isCelebrity;
	}

	/**
	 * Returns the posting frequency, given a time period
	 * 
	 * @param timePeriod
	 *            The time period in milliseconds
	 * @return The posting frequency
	 */
	public double getPostFreq(double timePeriod) {
		return (double) numStatuses
				/ ((System.currentTimeMillis() - joinDate) / timePeriod);
	}

	/**
	 * Returns the average daily number of posts
	 */
	public double getPostPerDay() {
		return getPostFreq(DAY_IN_MILLIS);
	}
}