package stirsx.util;

import java.io.*;

public class renameTagger {
	public static void main(String args[]) throws Exception {

		String inputFile = args[0];
		String outputFile = args[1];
		String find = args[2];
		String replaceWith = args[3];

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFile)));
		PrintWriter writer = new PrintWriter(new File(outputFile));
		String line = reader.readLine();

		while (line != null) {
			line = line.replaceAll(find, replaceWith);
			writer.println(line);
			writer.flush();
			line = reader.readLine();
		}
		
		writer.close();
		reader.close();
	}
}