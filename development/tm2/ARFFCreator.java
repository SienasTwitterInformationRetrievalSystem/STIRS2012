package tm2;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.*;

public class ARFFCreator {
	public ARFFCreator(String csv, String arff) throws IOException {

		// load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(csv));
		Instances data = loader.getDataSet();

		// save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(arff));
		saver.setDestination(new File(arff));
		saver.writeBatch();
	}
}