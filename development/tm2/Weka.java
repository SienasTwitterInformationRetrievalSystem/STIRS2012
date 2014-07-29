package tm2;

import weka.classifiers.Classifier;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;

import java.util.*;
import java.io.*;

/**
 * Uses a Weka generated decision tree to predict the relevance of a given set
 * of tweets
 * 
 * @author Matthew Kemmer v1.0
 * @version 7/14/2011 v1.0
 */
public class Weka {
	private Classifier cls;
	private boolean loadedCorrectly;
	private String arffHeading;
	private String modelName;
	private String arffName;

	/**
	 * Creates the constructor for the Weka class. By default when this
	 * constructor is created it will set the arff heading which it will take
	 * from the file. In addition the model will be created
	 */
	public Weka() {
		getFileNames();

		if (setArffHeading()) {
			// setRelevanceNumber();
		} else {
			System.out.println("Arff file not found");
		}

		if (!loadModel()) {
			System.out.println("Model file not found");
		}

		if (arffHeading != null && cls != null) {
			loadedCorrectly = true;
		} else {
			loadedCorrectly = false;
		}
	}

	/**
	 * initializes the modelName and fileName to the specified file below
	 */
	private void getFileNames() {
		modelName = "/home/gituser/trainingSet/model2.model";
		arffName = "/home/gituser/trainingSet/tweetSet.arff";
	}

	/**
	 * Determines whether the model and arff file loaded correctly
	 * 
	 * @return boolean whether model and arff loaded correctly
	 */
	public boolean loadedCorrectly() {
		return loadedCorrectly;
	}

	/**
	 * Loads the model
	 * 
	 * @return whether the model loaded correctly
	 */
	private boolean loadModel() {
		try {
			cls = (Classifier) weka.core.SerializationHelper.read(modelName);
			return true;
		} catch (Exception e) {
			cls = null;
			return false;
		}
	}

	/**
	 * Runs the model on the given set of Tweets. If the predicted relevance of
	 * any of the Tweets is YES, then the relevance on the corresponding objects
	 * will be set to true.
	 * 
	 * @param tweets
	 *            A list of Tweet objects to predict relevance for
	 */
	public void run(ArrayList<WekaTweet> tweets) {
		System.out.println("Running File");
		runTest(tweets);
	}

	/**
	 * Runs the test using the pre-generated model on the .arff file generated
	 * from the Tweets
	 * 
	 * @return true if the method runs correctly, false otherwise
	 */
	private boolean runTest(ArrayList<WekaTweet> tweets) {
		try {
			// Gets the test set Instances(Each tweet) from the arff file
			Instances testSet = new DataSource(arffName).getDataSet();

			// gets the attributes for each instance(except the last, relevance,
			// which is what we are trying to find)
			testSet.setClassIndex(testSet.numAttributes() - 1);

			// for each of the instances determine the probability of it being
			// relevant
			for (int i = 0; i < testSet.numInstances(); i++) {
				// gets the probability of a tweet being relevant based on the
				// model
				double[] scores = cls.distributionForInstance(testSet
						.instance(i));

				tweets.get(i).setScore(scores[1]);
			}

			return true;
		} catch (Exception e) {
			System.out.println("The exception is in the runTest");
			System.err.print(e);
			return false;
		}
	}

	/**
	 * Sets the arff heading and returns true if done successfully
	 * 
	 * @return boolean the success of the arff heading
	 */
	private boolean setArffHeading() {
		try {
			ArffLoader a = new ArffLoader();

			// gets the arff loader initialized
			a.setFile(new File(arffName));
			Instances i = a.getDataSet();
			String s = i.toString();

			// gets the attribute headings
			int index = s.indexOf("@data");
			arffHeading = s.substring(0, index + 6);

			return true;
		} catch (IOException e) {
			System.out.println("Exception in loading ArffLoader");
			arffHeading = null;

			return false;
		}
	}
}