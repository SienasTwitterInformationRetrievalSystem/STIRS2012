package GoogleArchive;

import java.net.MalformedURLException;
import java.net.URL;

public class Link {
	private String url = null;
	private String description = null;
	private URL u = null;
	
	public Link(String newURL, String newDescrip) {
		url = newURL;
		description = newDescrip;
		
		try {
			u = new URL(newURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public String getUrl() {
		return u.toString();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		String rString = "";
		rString+=url+" | "+description;
		return rString;
	}
}