package stirsx.tm1;

public class UrlObject implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String TweetID;
	private String content;

	public UrlObject(String TweetID, String content) {
		this.TweetID = TweetID;
		this.content = content;
	}

	public String getContent() {

		return content;
	}

	public String getTweetID() {
		return TweetID;
	}
}