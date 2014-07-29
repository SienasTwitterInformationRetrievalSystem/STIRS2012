package stirsx.util;

import java.io.*;

/**
 * Formats the queries properly so that they are all on a single line 
 * This is done by just combining the lines together from the given page 
 * that they provided us with queries
 * 
 * @author Karl Appel v1.0
 * @version 6/2011 v.1.0
 */
public class QueryFormatter {
	public static void main (String args[]) throws Exception{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
		PrintWriter writer = new PrintWriter(new File(args[1]));
		
		String line ="";
		while(line != null){
			line = reader.readLine();
			
			if(line != null && line.equals("<top>")){
				
				while(!line.equals("</top>")){
					writer.print(line);
					line = reader.readLine();
				}
				
				writer.print(line);
				writer.println();
				writer.flush();
			}
		}
		reader.close();
		writer.close();
	}
}