package GoogleArchive;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.htmlparser.jericho.TextExtractor;
import net.htmlparser.jericho.Source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import STIRS.QueryProcessor.LuceneQuery;
import STIRS.QueryProcessor.QueryProcessor;

public class Downloader {

	private int numArticles = 0;
	private GoogleExpansion ge;
	private String mostCommon = "";
	private ArrayList<LuceneQuery> queries2;

	public Downloader() {
		ge = new GoogleExpansion();
	}

	public Downloader(ArrayList<LuceneQuery> queries) throws IOException,
			InterruptedException {
		ge = new GoogleExpansion();
		this.queries2 = queries;

		for (int j = 0; j < queries.size(); j++) {
			LuceneQuery query = queries.get(j);
			String gquery = gQC(query.getQuery());
			ArrayList<Link> links = setGoogleLinks(gquery);
			getArticles(links);

			for (int i = 0; i < numArticles; i++) {
				findMostCommon("finalText" + i + ".txt", query.getQuery());
			}

			query.setQuery(query.getQuery() + " " + getExpandedQuery());
			queries.set(j, query);
			mostCommon = "";
		}
	}

	public ArrayList<LuceneQuery> getNewQueries() {
		return queries2;
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		ArrayList<LuceneQuery> queries = null;

		File queryFile = new File("testTopics50.txt");
		try {
			Logger logger = Logger.getLogger(Downloader.class.getName());
			QueryProcessor queryProcessor = new QueryProcessor(queryFile,
					logger, "status");
			queries = queryProcessor.getSanitizedQueries();

			@SuppressWarnings("unused")
			String queryStrings = "";
			for (LuceneQuery query : queries) {
				queryStrings += query.toString();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		Downloader dl = new Downloader(queries);
		queries = dl.getNewQueries();
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"Google Output.txt"));

		for (LuceneQuery query : queries) {
			bw.append("<top><num> Number: Example" + query.getQueryNum());
			bw.append(" </num><title> " + query.getQuery());
			bw.append(" </title><querytime> " + query.getQueryTime());
			bw.append(" </querytime><querytweettime> " + query.getTweetTime()
					+ " </querytweettime></top>");
			bw.newLine();
		}
		bw.close();
	}

	public String getExpandedQuery() {
		return ge.compare(mostCommon);
	}

	public void getArticles(ArrayList<Link> links) throws IOException,
			InterruptedException {
		int numFetch = 4;

		for (int i = 0; i < Math.min(numFetch, links.size()); i++) {
			URL u = null;
			URLConnection con = null;
			try {
				u = new URL(links.get(i).getUrl());
				if (links.get(i).getUrl().contains("video")
						|| links.get(i).getUrl().contains("youtube")) {
					numFetch++;
					break;
				}
				con = u.openConnection();
				con.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Linux; U; Android 2.2.1; de-de; X2 Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			String file = "htmlText" + i + ".txt";

			try {
				BadTextForm btf = new BadTextForm();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				@SuppressWarnings("unused")
				String input, load = "";
				System.out.print(i + " [");

				while ((input = br.readLine()) != null) {
					input = btf.removeBadChars(input);
					input = input.replace("\t", "");

					// Get rid of styles that may not have been removed
					input = input
							.replaceAll(
									"(#[a-z0-9_A-Z\\-;:!]+)|(\\.[a-z-0-9A-Z_]+)|(\\{[a-z0-9/_A-Z\\-;:\\.! #%\"\\(\\)]+\\})",
									"");
					load += input;
					bw.append(input);
					System.out.print("=");
				}
				bw.close();
				System.out.println("]");
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println("Couldn't get page, moving on...");
			}

			String cmd = "h2tc.exe " + file;

			Process convert_html = Runtime.getRuntime().exec(cmd);
			convert_html.waitFor();

			BufferedReader br = new BufferedReader(new FileReader("htmlText"
					+ i + ".txt"));
			Source source = new Source(br);
			source.setLogger(null);
			TextExtractor te = new TextExtractor(source);

			BufferedWriter bw = new BufferedWriter(new FileWriter("finalText"
					+ i + ".txt"));
			te.writeTo(bw);
			numArticles = numFetch;
		}
	}

	public void findMostCommon(String fileName, String query) {
		FileReader fr = null;

		try {
			fr = new FileReader("C:/Users/Carl/workspace/Twitter/" + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		BufferedReader br = new BufferedReader(fr);
		String doc = "", line;

		try {
			while ((line = br.readLine()) != null) {
				line = line.replace("\t", "");
				line = line.replace("(", "");
				line = line.replace(")", "");
				line = line.replace(":", "");
				line = line.replace(";", "");
				line = line.replace("U.", "US");
				line = line.replace("}", "");
				line = line.replace("{", "");
				doc += line;
			}

			mostCommon += ge.mostCommon(doc, query, numArticles) + " ";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getNumArticles() {
		return numArticles;
	}

	public ArrayList<Link> setGoogleLinks(String query) throws IOException {
		ArrayList<Link> links = new ArrayList<Link>();
		String googleStart = "http://www.google.com/search?hl=en&q=";
		String googleEnd = "&btnG=Google+Search";
		String googleQuery = googleStart + query + googleEnd;

		String googlePage = getURL(googleQuery);

		File f = new File("googlequery.html");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(googlePage);
		bw.close();

		boolean reachedEnd = false;
		int linkStartIndex, linkEndIndex = 0;
		int startIndex = googlePage.indexOf("Results");
		String checkSponsored = googlePage.substring(startIndex,
				googlePage.indexOf("<p>", startIndex));

		if (checkSponsored.contains("Sponsored Links")) {
			startIndex = googlePage.indexOf("<p>", startIndex);
			int startIndex2 = googlePage.indexOf("</table></p><p>");
			startIndex = Math.max(startIndex, startIndex2);
		}

		while (!reachedEnd) {
			linkStartIndex = googlePage.indexOf("<a href=", startIndex);
			linkEndIndex = googlePage.indexOf("&amp;", linkStartIndex);
			String link = googlePage.substring(linkStartIndex, linkEndIndex);

			if (!link.contains("webcache") && !link.contains("q=related")
					&& !link.contains("news?q=") && !link.contains("/search")
					&& !link.contains("youtube") && !link.contains("video")
					&& !link.contains("<table cellpadding")
					&& !link.contains("/imgres") && !link.contains("/images")
					&& !link.contains(".pdf")) {
				link = link.replaceAll("<a href=\"", "");
				link = link.replaceAll("/\"", "/");
				link = link.replaceAll("/url\\?q=", "");
				link = link.replaceAll("%3D", "=");
				link = link.replaceAll("%3F", "?");
				link = link.replaceAll("%26", "&");
				link = link.replaceAll("%2B", "+");

				Link newLink = new Link(link, null);
				links.add(newLink);
				System.out.println(newLink.toString());
			}
			startIndex = linkEndIndex;
			linkStartIndex = googlePage.indexOf("<a href=", startIndex);
			String endCheck = googlePage
					.substring(linkEndIndex, linkStartIndex);

			if (endCheck.contains("Searches related to:")
					|| endCheck.contains("<br clear=\"all\">")) {
				reachedEnd = true;
			}
		}
		return links;
	}

	public synchronized String getURL(String url) {
		StringBuffer text = new StringBuffer(1024);

		try {
			URL u = new URL(url);
			URLConnection con = u.openConnection();

			// to fool google and any other page that might not like to be
			// queried by an application
			con.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");

			BufferedReader urlrdr = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			int c;
			while ((c = urlrdr.read()) != -1) {
				text.append((char) c);
			}
		} catch (MalformedURLException mfurle) {
			return null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Error opening connection to URL");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("unknown exception thrown while retrieving web page");
			return null;
		}
		return text.toString();
	}

	/**
	 * gQC, or googleQueryConverter, converts regular queries to Google-friendly
	 * queries. Spaces are changed to +, and characters are encoded.
	 */
	public String gQC(String query) {
		query = query.replaceAll(" ", "+");
		return query;
	}
}