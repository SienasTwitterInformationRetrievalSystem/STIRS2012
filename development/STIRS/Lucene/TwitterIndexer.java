package STIRS.Lucene;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import filteringTaskIndexer.TweetIDComparable;

/**
 * TwitterIndexer uses Apache Lucene to create an index of Twitter tweets.
 * 
 * @author Carl Tompkins v1.0
 * @version 6/12/2011 v1.0
 */
public class TwitterIndexer {

	// Number of files indexed
	static int numDocuments;

	// File that contains the directory of the documents
	File docDir;

	// Where the documents are located
	String docsPath;

	// Where to store the index
	String indexPath;

	public TwitterIndexer(ArrayList<TweetIDComparable> tweets, String indexPath) {

		TwitterIndexer.numDocuments = 0;

		Date start = new Date();

		try {
			System.out.print("Writing index file to: " + indexPath + "\n");

			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31,
					analyzer);

			iwc.setOpenMode(OpenMode.CREATE);

			// increase the RAM buffer. But if you do this, increase the max
			// heap size to the JVM (eg add -Xmx512m or -Xmx1g):
			iwc.setRAMBufferSizeMB(512.0);

			IndexWriter writer = new IndexWriter(dir, iwc);

			indexDocsIDComparable(tweets, writer, analyzer);

			Date end = new Date();
			System.out.println("Number of files indexed: " + numDocuments);
			System.out.println("Total time elapsed: "
					+ getElapsedTime(end.getTime() - start.getTime()));
		} catch (IOException e) {
			System.err.print("ERROR: " + e.getClass() + " Message: "
					+ e.getMessage());
		}
	}

	/**
	 * Constructor that initializes the variables of the indexer.
	 * 
	 * @param indexPath
	 *            Where the index should be stored
	 * @param newIndex
	 *            Whether this is a new index
	 * @param docsPath
	 *            Path of the corpus file
	 */
	public TwitterIndexer(String indexPath, boolean newIndex, String docsPath) {
		this.indexPath = indexPath;
		this.docsPath = docsPath;
		TwitterIndexer.numDocuments = 0;

		Date start = new Date();
		try {
			System.out.print("Writing index file to: " + indexPath + "\n");

			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31,
					analyzer);

			docDir = new File(docsPath);

			if (newIndex) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			// increase the RAM buffer. But if you do this, increase the max
			// heap size to the JVM (eg add -Xmx512m or -Xmx1g):
			iwc.setRAMBufferSizeMB(512.0);

			IndexWriter writer = new IndexWriter(dir, iwc);

			indexDocs(writer, docDir, analyzer);

			writer.close();
			Date end = new Date();
			System.out.println("Number of files indexed: " + numDocuments);
			System.out.println("Total time elapsed: "
					+ getElapsedTime(end.getTime() - start.getTime()));
		} catch (IOException e) {
			System.err.print("ERROR: " + e.getClass() + " Message: "
					+ e.getMessage());
		}
	}

	/**
	 * Gets the time in a more readable format after getting time elasped from
	 * start to the end of the program
	 * 
	 * @param elapsed
	 * @return time the time in hours , minutes, seconds, and milliseconds
	 */
	private String getElapsedTime(long elapsed) {
		long hrs = 0, min = 0, sec = 0, ms = 0;
		String time = null;

		if (elapsed < 1000) {
			ms = elapsed;
		} else if (elapsed >= 1000 && elapsed < 60000) {
			sec = elapsed / 1000;
			ms = elapsed % 1000;
		} else if (elapsed >= 60000 && elapsed < 3600000) {
			min = elapsed / 60000;
			sec = (elapsed - min * 60 * 1000) / 1000;
			ms = (elapsed - min * 60 * 1000) % 1000;
		} else {
			hrs = elapsed / 3600000;
			min = (elapsed - hrs * 3600000) / 60000;
			sec = (elapsed - hrs * 3600000 - min * 60000) / 1000;
			ms = (elapsed - hrs * 3600000 - min * 60000 - sec * 1000);
		}

		time = hrs + "hr:" + min + "min:" + sec + "sec:" + ms + "ms";
		return time;
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is
	 * given, recurses over files and directories found under the given
	 * directory.
	 * 
	 * NOTE: This method indexes one document per input file. This is slow. For
	 * good throughput, put multiple documents into your input file(s). An
	 * example of this is in the benchmark module, which can create "line doc"
	 * files, one document per line, using the <a href=
	 * "../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 * 
	 * @param writer
	 *            Writer to the index where the given file/dir info will be
	 *            stored
	 * @param file
	 *            The file to index, or the directory to recurse into to find
	 *            files to index
	 * @throws IOException
	 */
	static void indexDocs(IndexWriter writer, File file, Analyzer analyzer)
			throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();

				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]), analyzer);
					}
				}
			} else {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					// at least on windows, some temporary files raise this
					// exception with an "access denied" message
					// checking if the file can be read doesn't help
					return;
				}
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(fis, "Cp1252"));
					String statusLine;
					while ((statusLine = reader.readLine()) != null) {
						// The separate parts of the tweets are separated by
						// tabs
						// Tokenize a string, using \t as the delimiter
						StringTokenizer strtok = new StringTokenizer(
								statusLine, "\t");
						String tweetIDString = null, usernameString = null, dateString = null, httpStatus = null, statusString = null;

						for (int i = 0; i < 5; i++) {
							if (strtok.hasMoreTokens()) {
								switch (i) {
								case 0:
									tweetIDString = strtok.nextToken();
									break;
								case 1:
									usernameString = strtok.nextToken();
									break;
								case 2:
									httpStatus = strtok.nextToken();
									break;
								case 3:
									dateString = strtok.nextToken();
									break;
								case 4:
									statusString = strtok.nextToken();
									break;
								}
							} else {
								break;
							}
						}

						if (tweetIDString != null
								&& usernameString != null
								&& dateString != null
								&& httpStatus != null
								&& statusString != null
								&& (httpStatus.equals("200") || httpStatus
										.equals("302"))) {

							// make a new, empty document
							Document doc = new Document();

							// Add the tweet ID of the status
							Field tweetID = new Field("tweetID", tweetIDString,
									Field.Store.YES, Field.Index.NOT_ANALYZED);
							tweetID.setOmitTermFreqAndPositions(true);
							doc.add(tweetID);

							// Add the username of the tweeter
							// Chose NOT_ANALYZED b/c Index the field's value
							// without using an Analyzer, so it can be searched.
							Field username = new Field("username",
									usernameString, Field.Store.YES,
									Field.Index.NOT_ANALYZED);
							doc.add(username);

							// Add the date of the tweet
							Field date = new Field("date", dateString,
									Field.Store.YES, Field.Index.NOT_ANALYZED);
							date.setOmitTermFreqAndPositions(true);
							doc.add(date);

							// Add the tweet itself. It indexes the content, but
							// it does not store the content itself. This is
							// important to know.
							Field status = new Field("status", statusString,
									Field.Store.YES, Field.Index.ANALYZED);
							doc.add(status);

							if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
								// New index, so we just add the document (no
								// old document can be there):
								// System.out.println("Adding tweet " +
								// tweetID.stringValue());
								writer.addDocument(doc, analyzer);
							} else {
								// Existing index (an old copy of this document
								// may have been indexed) so
								// we use updateDocument instead to replace the
								// old one matching the exact
								// path, if present:
								// System.out.println("updating " + file);
								writer.updateDocument(
										new Term("path", file.getPath()), doc);
							}
							// increase document counter
							numDocuments++;
						}
						reader.close();
					}
				} finally {
					fis.close();
				}
			}
		}
	}

	static void indexDocsIDComparable(ArrayList<TweetIDComparable> tweet,
			IndexWriter writer, Analyzer analyzer) throws IOException {
		// do not try to index files that cannot be read

		try {
			String statusLine;
			for (int j = 0; j < tweet.size(); j++) {
				statusLine = tweet.get(j).getRawTweetInfo();
				// The separate parts of the tweets are separated by tabs
				// Tokenize a string, using \t as the delimiter
				StringTokenizer strtok = new StringTokenizer(statusLine, "\t");
				String tweetIDString = null, usernameString = null, dateString = null, httpStatus = null, statusString = null;

				for (int i = 0; i < 5; i++) {
					if (strtok.hasMoreTokens()) {
						switch (i) {
						case 0:
							tweetIDString = strtok.nextToken();
							break;
						case 1:
							usernameString = strtok.nextToken();
							break;
						case 2:
							httpStatus = strtok.nextToken();
							break;
						case 3:
							dateString = strtok.nextToken();
							break;
						case 4:
							statusString = strtok.nextToken();
							break;
						}
					} else
						break;
				}

				if (tweetIDString != null
						&& usernameString != null
						&& dateString != null
						&& httpStatus != null
						&& statusString != null
						&& (httpStatus.equals("200") || httpStatus
								.equals("302"))) {

					// make a new, empty document
					Document doc = new Document();

					// Add the tweet ID of the status
					Field tweetID = new Field("tweetID", tweetIDString,
							Field.Store.YES, Field.Index.NOT_ANALYZED);
					tweetID.setOmitTermFreqAndPositions(true);
					doc.add(tweetID);

					// Add the username of the tweeter
					// Chose NOT_ANALYZED b/c Index the field's value without
					// using an Analyzer, so it can be searched.
					Field username = new Field("username", usernameString,
							Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(username);

					// Add the date of the tweet
					Field date = new Field("date", dateString, Field.Store.YES,
							Field.Index.NOT_ANALYZED);
					date.setOmitTermFreqAndPositions(true);
					doc.add(date);

					// Add the tweet itself. It indexes the content, but it does
					// not store the content itself. This is
					// important to know.
					Field status = new Field("status", statusString,
							Field.Store.YES, Field.Index.ANALYZED);
					doc.add(status);

					writer.addDocument(doc, analyzer);

					// increase document counter
					numDocuments++;
				}
			}
		} finally {
		}
	}

	/**
	 * Executes the indexing process a main class with appropriate arguments
	 */
	public static void main(String[] args) {
		boolean newIndex = true;
		String docsPath = null, indexPath = null;
		// index_path is where the index will be stored; documents_path is the
		// actual corpus file
		final String USAGE = "Usage: java TwitterIndexer -index INDEX_PATH -docs DOCUMENTS_PATH [-update]";

		// Loop over the arguments and place them in appropriate variables
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
			} else if ("-update".equals(args[i])) {
				newIndex = false;
			} else if ("-docs".equals(args[i])) {
				docsPath = args[i + 1];
			}
		}

		if (indexPath == null || docsPath == null) {
			System.out.print(USAGE);
			System.exit(1);
		}

		new TwitterIndexer(indexPath, newIndex, docsPath);
	}
}