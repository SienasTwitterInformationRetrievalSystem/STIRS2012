package GoogleArchive;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElementName;
import au.id.jericho.lib.html.HTMLElements;
import au.id.jericho.lib.html.Segment;
import au.id.jericho.lib.html.Source;

public class FormatDirectGoogleQuery extends DocumentFilter {
	@SuppressWarnings("rawtypes")
	private static ArrayList googlePairs = null;

	public static String formatFile(String file, String destDir,
			boolean IsFilePath) {
		try {
			FormatDocument.init(2, 30, destDir);
			jerichoFormatHtml(file, IsFilePath);

			return null;
		} catch (Exception e) {
			System.out.println("Error in Filter.java");
			e.printStackTrace();
			return null;
		}
	}

	protected static String jerichoFormatHtml(String file, boolean IsOFilePath) {
		HTML2Text(file, IsOFilePath, FormatDocument.ISURL);

		return null;
	}

	@SuppressWarnings("rawtypes")
	public static String HTML2Text(String file, boolean IsFilePath,
			boolean IsUrl) {
		
		try {
			Source source;
			if (IsFilePath) {
				if (IsUrl){
					source = new Source(new URL(file));
				}else{
					source = new Source(new FileInputStream(file));
				}
			} else{
				source = new Source(file);
			}

			// clear out googlePairs so if an error happens, it won't still be
			// filled with previous questions
			googlePairs = new ArrayList();
			googlePairs = extractHTML2TextForGoogle(source);
			return null;
		} catch (Exception e) {
			System.out.println("Error in Filter.java");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns an arraylist filled with arraylists of link
	 * information. Information will be structured such that each inner array
	 * list contains ( link, title, description )
	 * 
	 * @param source the source file
	 * @return the arraylist of ( link, title, description ) arraylist pairs
	 */
	@SuppressWarnings("rawtypes")
	private synchronized static ArrayList extractHTML2TextForGoogle(
			Source source) {
		boolean prevscript = false;
		List elements = source.findAllElements();
		int maxLines = 0, end = -1;
		Segment seg;
		ArrayList<ArrayList<String>> linkDiscPairs = new ArrayList<ArrayList<String>>();
		ArrayList<String> linkDiscPair = new ArrayList<String>();
		int endPos = -1, startPos;

		int pos = ((Element) elements.get(0)).getStartTag().getEnd();

		for (int i = 0; i < elements.size(); i++) {
			Element currE = (Element) elements.get(i);

			if (currE.getStartTag() != null && !currE.getName().equals(HTMLElementName.SCRIPT)) {
				end = ((Element) elements.get(i)).getStartTag().getBegin();
				if (pos < end) {
					seg = new Segment(source, pos, end);
					
					if (seg.getChildElements().size() == 0 && !prevscript) {
						if (!seg.extractText().equals(""))
							maxLines = 0;
					}
					
					if ((blockNames.contains(currE.getName()) || currE
							.getName().equals(HTMLElementName.BR))
							&& maxLines < 1) {
						maxLines++;
					}
				}
			}

			// added check for no documents returned
			if (currE.getAttributeValue("class") != null
					&& currE.getAttributeValue("class")
							.equalsIgnoreCase("t bt")) {
				// in here there should be the word results somewhere, otherwise we have nothing
				String text = currE.extractText();

				if (text.indexOf("Results") == -1) {
					// no results
					System.out.println("No results returned from Google");
					return new ArrayList();
				}
			}

			if (currE.getAttributeValue("class") != null
					&& currE.getAttributeValue("class").equalsIgnoreCase("l")) {
				linkDiscPair = new ArrayList<String>();
				linkDiscPair.add(currE.getAttributeValue("href"));
				linkDiscPair.add(currE.extractText());
			}
			
			if (currE.getAttributeValue("class") != null
					&& currE.getAttributeValue("class").equalsIgnoreCase("j")) {
				
				// get the font tag
				List children = currE.getChildElements();
				
				// then get all children tags of the font tag
				children = ((Element) children.get(0)).getChildElements();

				// loop through all children looking for the span tag
				for (int j = 0; j < children.size(); j++) {
					Element e = (Element) children.get(j);

					if (e.getName().equalsIgnoreCase("span")) {
						// get the position
						endPos = e.getBegin();
						j = children.size() + 1;
					}
				}

				// get the position of the beginning of the segment
				startPos = currE.getStartTag().getEnd();
				String description = "";

				// sometimes the end tag isn't computed correctly and causes
				// indexoutofbounds error, if that
				// is the case, should just use an empty description - this
				// change made by Stacey to fix an error

				if (endPos > startPos && startPos >= 0 && endPos >= 0
						&& source.length() > endPos){
					// get the line with HTML tags
					description = (String) source.subSequence(startPos, endPos);
				}else{
					description = "No description available.";
				}

				// strip out HTML tags
				description = description.replaceAll("\\<.*?\\>", "");

				// save to the arraylist
				linkDiscPair.add(description);

				// add the pair to the pair list
				linkDiscPairs.add(linkDiscPair);
			}

			if (currE.getStartTag() != null) {
				pos = currE.getStartTag().getEnd();
				
				if ((blockNames.contains(currE.getName()) || currE.getName()
						.equals(HTMLElementName.BR)) && maxLines < 1) {
					maxLines++;
				}
				
				if (currE.getName().equals(HTMLElements.SCRIPT)){
					prevscript = true;
				}else{
					prevscript = false;
				}
			} else{
				pos = source.getEnd();
			}
		}

		return linkDiscPairs;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<String>> getGooglePairs() {
		return googlePairs;
	}
}