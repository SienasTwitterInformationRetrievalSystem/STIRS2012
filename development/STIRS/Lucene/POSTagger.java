package STIRS.Lucene;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 * This code will be used to find the part of speech of each word in a sentence
 * 
 * @author Karl Appel
 * @version 6/18/12
 */
public class POSTagger {

	/**
	 * Method to call that has initiated the code necessary to find the part of
	 * speech tagging
	 */
	InputStream modelIn = null;
	POSModel model = null;
	POSTaggerME tagger = null;

	/**
	 * Initializes the necessary information for the tagger in order for it to
	 * work
	 */
	public void initializeTagger() {
		try {
			// model used to determine the part of speech of a sentence
			modelIn = new FileInputStream("/home/gituser/en-pos-maxent.bin");

			// makes the model
			model = new POSModel(modelIn);

			// makes the appropriate tagger
			tagger = new POSTaggerME(model);
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Takes a sentence and gets the part of speech for that sentence
	 * 
	 * @param sentence
	 *            a String that has the sentence
	 * @return tags a array of String with the part of speech for each word in
	 *         the sentence
	 */
	public String[] findtags(String sentence) {
		// tokenizes the sentence to an array
		try {
			StringTokenizer tokenizer = new StringTokenizer(sentence);
			int size = tokenizer.countTokens();
			String[] tokenizedSentence = new String[size];
			
			for (int i = 0; i < size; i++) {
				tokenizedSentence[i] = tokenizer.nextToken();
			}
			
			// gets the tags in a array corresponding to its position in the
			// tokenized sentence
			String tags[] = tagger.tag(tokenizedSentence);
			return tags;
		} catch (NullPointerException e) {
			System.err.println(e);
			System.err.println("DID YOU CALL INITIALIZE METHOD?");
			return null;
		}
	}
}