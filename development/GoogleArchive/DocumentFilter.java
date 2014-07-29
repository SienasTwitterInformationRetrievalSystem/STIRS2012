
package GoogleArchive;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.HTMLElements;
import au.id.jericho.lib.html.Source;

/**
 * Used to filter documents retrieved straight from the web, 
 * whether they be in pdf, doc, or html format.  Currently, only html format
 * has been tested.
 * 
 * @author Stacey Gaddis
 */
public class DocumentFilter {
	
	// Versions html parser can take on
	public final static int GOOGLEPARSER = 1;	// NOT TESTED
	public final static int JERICHOPARSER = 2;

	// by default set to jericho parser
	private static int HTMLPARSER = JERICHOPARSER;
	
	// used to remove leftover garbage from web pages
	static BadTextForm btf = new BadTextForm();
	
	// the saved set of html element names
	static Set<?> blockNames = HTMLElements.getBlockLevelElementNames();
	
	/**
	 * Values can be:
	 * GOOGLEPARSER - uses h2tc.exe
	 * JERICHOPARSER - uses jericho.jar and HTML2Text function call
	 * 
	 * Default is set to JERICHOPARSER
	 * 
	 * @param parser	- the selected html parser
	 */
	public void setParser(int parser){
		HTMLPARSER = parser;
	}
	
	/**
	 * The options for overwrite are set in FormatText and are:
	 * AUTOOVERWRITE - automatically overwrite any conflicts with filenames
	 * AUTORENAME - automatically rename the file to somethign that wont conflict
	 * FAILIFDUP - fail if the file already exists
	 * Default value is set to AUTOOVERWRITE
	 * 
	 * @param overwrite	- determines what to do when a file already exists 
	 * with the same name when formatting.
	 */
	public void setOverwrite(int overwrite){
		FormatDocument.OVERWRITE = overwrite;
	}
	
	/**
	 * Set to true if formating with paths that are urls, straight from the
	 * web.  This will mean the tags will attempt to extract more information.
	 * 
	 * @param is	- true if you will be passing file names in that
	 * are urls and not local file paths
	 */
	public void setIsUrl(boolean is){
		FormatDocument.ISURL = is;
	}
	
	/**
	 * Set the destination path to output files.  If you don't want the files
	 * saved anywhere but only the filtered text returned as a string, this
	 * should be called with path = null or path = ""
	 * 
	 * @param path	- path where to save formatted files
	 */
	public void setDestPath(String path){
		FormatDocument.init(2, 30, path);
	}
	
	/**
	 * Removes bad text fragments from the text.  Fragments are extracted
	 * by splitting up the text from new lines and periods.  Whether
	 * the text is 'bad' or not is determined by BadTextForm.java
	 * 
	 * This should remove most junk from documents and leave only the
	 * pertinent information.  Works best on news feed-like documents.
	 * 
	 * @param s - Text to remove bad fragments from
	 * @return	updated text with bad fragments removed
	 */
	public synchronized static StringBuffer removeBadText(String s){
		
		String parts[] = s.split("\n\n");
		StringBuffer ret = new StringBuffer();
		
		// split the filtered text by new lines and search for paragraphs
		for(int i = 0; i < parts.length; i++){
				// make sure it is at least long enough to be a short sentence so we avoid getting random
				// lists and junk in here
				if(parts[i].length() > 60 && !btf.badText(parts[i])){
					String toAppend = btf.removeBadChars(parts[i]);
					ret.append( toAppend + "\n\n");
				}
			}
		
		return ret;
	}
	
	/**
	 * Find if there are any good elements in the list of elements
	 * @param childElements
	 * @return
	 */
	private synchronized static boolean hasNestedElements(Element currE, ArrayList<String> goodElementNames){
		List<?> childElements = currE.getChildElements();
		for(int i = 0; i < childElements.size(); i++){
			Element child = ((Element)childElements.get(i));
			if(child.getName() != null
					&& goodElementNames.contains(child.getName().toLowerCase()))
				return true;
			if(hasNestedElements(child, goodElementNames))
				return true;
		}
		return false;
	}
	
	/**
	 * Extracts the text from the html source document.
	 * 
	 * @param source	- the souce document to extract the text from
	 * @return	the extracted text
	 */
	private  synchronized static StringBuffer extractHtml2Text(Source source){
		StringBuffer s = new StringBuffer();
		
		ArrayList<String> goodElements = new ArrayList<String>();
		goodElements.add(HTMLElementName.P);
		goodElements.add(HTMLElementName.TD);
		goodElements.add(HTMLElementName.UL); //- excluding lists for now
		goodElements.add(HTMLElementName.DIV);
		
		ArrayList<String> headerElements = new ArrayList<String>();
		headerElements.add(HTMLElementName.H1);
		headerElements.add(HTMLElementName.H2);
		headerElements.add(HTMLElementName.H3);
		headerElements.add(HTMLElementName.H4);
		headerElements.add(HTMLElementName.TITLE);
		
		List<?> elements = source.findAllElements();
		
		elements.removeAll(source.findAllElements(HTMLElementName.OPTION));
		elements.removeAll(source.findAllElements(HTMLElementName.SELECT));
		elements.removeAll(source.findAllElements(HTMLElementName.SCRIPT));
		
		if(elements.size() == 0){
			System.err.println("Possible Error, no elements in source text to be filtered");
			return new StringBuffer();
		}
	
		boolean possibleHeader = false;
		String header = "";
		
		for (int i = 0; i < elements.size();i++) {
		
			Element currE = (Element)elements.get(i);
			
			if(goodElements.contains(currE.getName().toLowerCase()) || headerElements.contains(currE.getName().toLowerCase())){
				String startTag;
				@SuppressWarnings("unused")
				String endTag;
				
				if(hasNestedElements(currE, goodElements))
					continue;
				
				if(currE.getStartTag() != null){
					startTag = "<"+currE.getStartTag().getName()+">";
				}else {
					startTag = "";
				}
				
				if(currE.getEndTag() != null){
					endTag = "</"+currE.getEndTag().getName()+">";
				}else {
					endTag = "";
				}

				String allText = "";
				
				String[] parts = allText.split("\n\\s*\n");
				ArrayList<String> shortPPs = new ArrayList<String>();
				
				for(int j = 0; j < parts.length; j++){
					if(parts[j].trim().length() > 2000/2){
						String[] lines = parts[j].split("\n");
						
						// don't start at 0 because sometimes the first word is on it's own line
						StringBuffer toAppend = new StringBuffer(lines[0]);
						
						for(int r = 1; r < lines.length; r++){
							// if the line ends with a \n and is less than 25 characters, probably
							// is supposed to be a new paragraph coming up next.
							if(lines[r].length() < 25){
								toAppend.append(lines[r] + " ");
								shortPPs.add(toAppend.toString());
								toAppend = new StringBuffer();
							} else{
								toAppend.append(lines[r] + " ");
							}
						}
						
						if(toAppend.length() > 0){
							shortPPs.add(toAppend.toString());
						}
						
					} else{
						shortPPs.add(parts[j].trim());
					}
				}
				
				for(int k = 0; k < shortPPs.size(); k++){
					String text = ((String)shortPPs.get(k)).trim();
					
					// skip if the element has nested elements of the same type
					// i.e. <p> <p> </p> <p> </p> </p> or nested tables.  only 
					// grab the inner most
					// don't continue if its a list, because that will of course have nested list elements
					
					if(text.indexOf("does not seem to be a viable objective for Beijing") >= 0){
						System.out.println("break");
					}

					if(startTag != null && startTag.equalsIgnoreCase("<"+HTMLElementName.UL+">") && !text.trim().equals("")){
						StringBuffer listElement = new StringBuffer();
						listElement.append("<"+HTMLElementName.UL+">\n");
					} else if(!text.equals("") && (startTag.toUpperCase().matches("<H\\d>") || startTag.toUpperCase().equals("<TITLE>"))){
						text = btf.removeBadChars(text);
						s.append("\n"+startTag.toUpperCase() + "\n"+text + "\n");
						// reset the header because we don't want two in a row
						possibleHeader = false;
						// if its a paragraph with no bad text
					} else if(!text.trim().equals("") && !btf.badText(text) && text.length() > 80){
						// only check for bad stuff if its not a paragraph tag (it might be a div or a td 
						// in which case there is greater possiblity of junk rather than in a <p> tag
						if(!startTag.equalsIgnoreCase("<p>")){
							text = removeBadText(text).toString();
						}
						
						text = btf.removeBadChars(text);
						
						// if empty or too short after removing the bad text, continue on
						if(text.equals("")){
							possibleHeader = false;
							continue;
						}
						
						// check to see if this paragraph was preceeded by a potential header
						if(possibleHeader){
							s.append(header);
							possibleHeader = false;
						} 
						
						text = text.replaceAll("\n\n", "\n<P>\n");
						// just use p tags
						s.append("\n<P>\n" + text +"\n\n");
						
						// only add a maybe header, 
					} else if(startTag.toUpperCase().equals("<H2>") || startTag.toUpperCase().equals("<H3>")){// && HelperFunctions.couldBeHeader(text)){
						// if wasn't originally an h1 header, use only the smallest header tags
						header = "\n<H>\n" + text + "\n";
						
						// set it to false if it was already true (don't want two headers in a row, it could be a list)
						if(possibleHeader){
							possibleHeader = false;
						}else{
							possibleHeader = true;
						}
					} else {
						possibleHeader = false;
					}
				}
				elements.removeAll(currE.getChildElements());
			}
		}
    	String text = s.toString();
    	// make sure all pps are on one line, not multiple, then put back <p> tags on their own line, 
    	// then remove too many spaces, then make sure periods are followed by two spaces for better formatting
    	text = text.replaceAll("\n|\r", " ").replaceAll("<P>", "\n<P>\n").replaceAll(" +", " ").replaceAll("\\. ", "\\.  ");
		return s;
	}
	
	/**
	 * Extract the text from the html using the method determined by
	 * HTMLPARSER.  
	 * 
	 * @param file	- file of original path or original file text
	 * @param IsFilePath	- determines above
	 * @param IsUrl	- if the file is a url path
	 * @return the filtered string (untagged)
	 */
	public synchronized static String HTML2Text(String file, boolean IsFilePath, boolean IsUrl){
		try{
			StringBuffer text = new StringBuffer();
			Source source;
			if(IsFilePath){
				if(IsUrl)
					source=new Source(new URL(file));
				else{
					source=new Source(new FileInputStream(file));
				}
			}else{
					file = file.replaceAll("<\\?", "<");
					// so lists will be seperated by commas
					file = file.replaceAll("</li>", ", </li>");
					source = new Source(file);
			}
			
			text = extractHtml2Text(source);
			return text.toString();
		}catch(Exception e){
			System.out.println("Error in Filter.java");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * If IsFilePath is true then filter the file given by the location 'file' and
	 * save in destDir (if not null or empty).  The returned string will
	 * also be the filtered text with the appropriate tags (<DOCNO>, <DOC>, etc)
	 * 
	 * @param file	- unformatted file path
	 * @param destDir	- directory to send the formatted file, if null
	 * or empty string will not save files anywhere
	 * @param IsFilePath - set to true if file is a path, set to false if
	 * file is the actual file text
	 * @return
	 */
	public static synchronized String formatFile(String file, String destDir , boolean IsFilePath){
		try{
			FormatDocument.init(2, 30, destDir);
			
			String plainText = "";
			
			if(IsFilePath){
				// determine type of file to be formatted
				if(file.endsWith(".pdf")){
					System.out.println("Processing file " + file);
					org.pdfbox.ExtractText.main(new String[]{file});
					FormatDocument.processing(file, removeBadText(plainText).toString(), IsFilePath, false);
				} else if (file.endsWith(".doc")){
					return null;
				} else if(file.endsWith(".html")){
					if(HTMLPARSER == GOOGLEPARSER){
							 plainText = googleFormatHtml(file, IsFilePath);
					}else if(HTMLPARSER == JERICHOPARSER){
						plainText = jerichoFormatHtml(file, IsFilePath);
					}
				}else{
					System.out.println("File does not end in html, doc or pdf.  Possible errors" +
							" may occur.  Attempting to format file using the html parser.\n");
					if(HTMLPARSER == GOOGLEPARSER){
						plainText = googleFormatHtml(file, IsFilePath);
					}else if(HTMLPARSER == JERICHOPARSER){
						plainText = jerichoFormatHtml(file, IsFilePath);
					}
				}
			} else {
				if(HTMLPARSER == GOOGLEPARSER){
					plainText = googleFormatHtml(file, IsFilePath);
				}else if(HTMLPARSER == JERICHOPARSER){
					plainText = jerichoFormatHtml(file, IsFilePath);
				}
			}
			return plainText;	
		}catch(Exception e){
			System.out.println("Error in Filter.java");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Filters the file and adds appropriate tags around the extracted
	 * and cleaned up text.  The final output is returned as a string.  If
	 * FormatText.filteredPath = null or is empty then no extra file will be
	 * saved, otherwise a new file ending with .txt will be saved in that directory
	 * (or pathname)
	 * 
	 * @param file	- file to be filtered and tagged
	 * @param IsFilePath	- determines if above is file text or a path
	 * @return the filtered and tagged text
	 */
	private synchronized static String jerichoFormatHtml(String file, boolean IsOFilePath){
		String plainText = HTML2Text(file, IsOFilePath, FormatDocument.ISURL);
		return  FormatDocument.processing(file, plainText, IsOFilePath, false);
	}
	
	/**
	 * NOT TESTED
	 * Same as above except uses h2tc.exe instead of jericho.
	 * Filters the file and adds appropriate tags around the extracted
	 * and cleaned up text.  The final output is returned as a string.  If
	 * FormatText.filteredPath = null or is empty then no extra file will be
	 * saved, otherwise a new file ending with .txt will be saved in that directory
	 * (or pathname).  A file will however be saved in the same directory
	 * that the file to be parsed resides in.  
	 * 
	 * @param file	- file to be filtered and tagged
	 * @param IsFilePath	- determines if above is file text or a path
	 * @return the filtered and tagged text
	 */
	private  synchronized static String googleFormatHtml(String file, boolean IsFilePath) throws IOException, InterruptedException {
		
		if(!IsFilePath){
			File f = new File("tmp");
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write(file);
			bw.close();
			file = "tmp";
		}
		
		String cmd = "C:\\fileConverter\\h2tc.exe " + file;
		
	
		Process convert_html = Runtime.getRuntime().exec(cmd);
		InputStreamReader myStreamReader = new InputStreamReader(convert_html.getInputStream());
		String line;
		BufferedReader bufferedReader = new BufferedReader(myStreamReader);
		 while ((line = bufferedReader.readLine()) != null){
				System.out.println(line);
		  }
	
		System.out.println("Processing file " + file);
		return FormatDocument.processing(file, file.substring(0, file.lastIndexOf(".")) + ".txt",IsFilePath, true);
	}
	
	public static void main(String args[]){
		 try{
			 int bs = 0;
			 long start = System.currentTimeMillis();
			 System.out.println("start: " + start);
			 File f = new File(args[0]);
			 if(f.isDirectory()){
				 File[] fs = f.listFiles();
				 
				 for(int i = 0; i < fs.length; i++){
					bs += fs[i].length();
					formatFile(fs[i].getPath(), args[1], true);
				 } 
			 } else{
				 formatFile(f.getPath(), args[1], true);
			 }
			 
			 long end = System.currentTimeMillis();
			 System.out.println("end: " + end);
			 System.out.println("Total Time:  " + (end - start));
			 System.out.println("Total Time:  " + (end - start) + " for " + bs + " bytes");
			 System.out.println("Time per MB: " + (end - start)/(bs/1000000));
		 }catch(Exception ioe){
			 System.out.println("Error in Filter.java");
			 ioe.printStackTrace();
		 }
	}	
}