package queryBreakdown;
import java.util.*;
import java.util.logging.Logger;
import java.io.*;

import stirsx.util.POSTagger;

import STIRS.QueryProcessor.LuceneQuery;
import STIRS.QueryProcessor.QueryProcessor;

/**
 * This class is going to divide the query into multiple parts AND and OR
 * statements between each word. This is going to be done by using part of
 * speech tagging for the entire query phrase
 * 
 * @author Karl Appel v1.0
 */
public class DivideQuery {

	public DivideQuery(){}
	
	/**
	 * Divides the query phrases into and gets their part of speech tags 
	 * @param queryFile the trec query file 
	 * @param task the actual task we want
	 * @return arraylist of lucene query
	 */
	public ArrayList<LuceneQuery> doDividing(String queryFile, String rawFile , String outputFile , String task)
			throws Exception {
		BufferedReader secondReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(queryFile))));
		PrintWriter writerRaw = new PrintWriter(new File(rawFile));
		PrintWriter writerNewQueries = new PrintWriter(new File(outputFile));
		ArrayList<LuceneQuery> queries = new ArrayList<LuceneQuery>();
		
		//assigns the appropriate processor using a ternary operator to determine the right method 
		QueryProcessor processor = (task == null) ? new QueryProcessor(new File(queryFile),
				Logger.getLogger(DivideQuery.class.getName()), task) : new QueryProcessor(new File(queryFile),
						Logger.getLogger(DivideQuery.class.getName()));
		
		queries = processor.getSanitizedQueries();

		POSTagger tagger = new POSTagger();
		tagger.initializeTagger();

		for (int i = 0; i < queries.size(); i++){

			String tags[] = tagger.findTags(queries.get(i).getQuery());
			String[] queryTerms = queries.get(i).getQueryTerms();
			
			for (int j = 0; j < queryTerms.length; j++){
				writerRaw.print((j + 1) + "." + queryTerms[j]);
				writerRaw.print(" ");
				writerRaw.flush();
			}
			
			writerRaw.println();
			
			for(int j = 0; j < tags.length; j++){
				writerRaw.print((j + 1) + "." + tags[j]);
				writerRaw.print(" ");
				writerRaw.flush();
			}
			
			writerRaw.println();
		
			ArrayList<String> wantedTags = new ArrayList<String>();
			wantedTags.add("NN");
			wantedTags.add("CD");
			
			String fullLine = "";
			
			for(int j = 0; j < tags.length ; j++){
				if(j == tags.length -1 ){
					fullLine += queryTerms[j];
					break;
				}
				
				System.out.println(fullLine);
				String line = queryTerms[j] + " ";
				fullLine += line;
				
				writerRaw.print(line);
				
				if(wantedTags.contains(tags[j]) && !((queryTerms[j +1 ].toLowerCase()).equals("and"))){
					writerRaw.print("AND ");
					fullLine += "AND ";
				}
				
				writerRaw.flush();
				writerNewQueries.flush();
			}
			fullLine = secondReader.readLine().replaceAll(queries.get(i).getQuery(), fullLine);
			System.out.println(queryTerms[tags.length -1]);
			writerRaw.print(queryTerms[tags.length -1]);
			writerNewQueries.println(fullLine);
			writerRaw.println();
			writerRaw.flush();
		}
		
		secondReader.close();
		writerRaw.close();
		writerNewQueries.close();
		
		return null;
	}

	public static void main(String args[]) throws Exception {
		DivideQuery d= new DivideQuery();
		
		String inputFile = args[0];
		String output = args[1];
		String task = args[2];
		String rawOutputFile = args[3];
		
		if(args[2].equals("null")){
			args[2] = null;
		}
		
		d.doDividing(inputFile , rawOutputFile , output,  task);
	}
}