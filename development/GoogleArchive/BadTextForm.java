package GoogleArchive;

import java.util.*;
import java.util.regex.*;

/**
 * @author Sharon G. Small v1.0
 * @author S.Gladdis v1.5
 * @version 2/3/2004 v1.0
 * @version 5/30/2007 v1.5
 */
public class BadTextForm {
	ArrayList<String> beginText = new ArrayList<String>();
	ArrayList<Pattern> patternText = new ArrayList<Pattern>();
	
	public BadTextForm() {
		initializeBegins();
		initializePatterns();
	}

	public String removeBadChars(String text) {
		char[] charArray = text.toCharArray();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] == (char) 146){
				sb.append('\'');
			}
			
			if (charArray[i] == (char) 145
					|| ((int) charArray[i] >= 180 && (int) charArray[i] <= 223)){
				continue;
			}else if (charArray[i] == (char) 151){
				sb.append('-');
			}else if (charArray[i] == (char) 153){ // trademark character
				sb.append("(TM) ");
			}else if (charArray[i] == 160 || (int) charArray[i] == 30){
				sb.append(" ");
			}else if ((int) charArray[i] == 8211){
				sb.append("-");
			}else if ((int) charArray[i] == 8213){
				sb.append("-");
			}else if ((int) charArray[i] == 65533){
				sb.append(" ");
			}else if ((int) charArray[i] > 255){
				sb.append(" ");
			}else{
				sb.append(charArray[i]);
			}
		}
		
		text = sb.toString();
		text = text.replaceAll("&#147;", "\"");
		text = text.replaceAll("<!.*>", "");
		text = text.replaceAll("&#148;", "\"");
		text = text.replaceAll("&trade;", " ");
		text = text.replaceAll("&nbsp;", " ");
		text = text.replaceAll("&quot;", "\"");
		text = text.replaceAll("&amp;", "&");
		text = text.replaceAll("&#151;", "-");
		text = text.replaceAll("&...;", " ");
		text = text.replaceAll("&#153", "tm");
		text = text.replaceAll("&....;", " ");
		text = text.replaceAll("&.....;", " ");
		text = text.replaceAll("&......;", " ");
		text = text.replaceAll("&...", " ");
		text = text.replaceAll("’", "'");
		text = text.replaceAll("�", "'");
		text = text.replaceAll("“", "\"");
		text = text.replaceAll("�.", "\"");
		text = text.replaceAll("&bull;", "");
		text = text.replaceAll("�", "");
		text = text.replaceAll("\t", " "); // get rid of tabs
		text = text.replaceAll(" +", " ");
		text = text.replaceAll(" \n ", " \n");
		text = text.replaceAll("^ *", "");

		return text;
	}

	private void initializeBegins() {
		beginText.add("DIALOG(R)");
		beginText.add("(c)");
		beginText.add(java.lang.Character.toString((char) 169)); // copyright symbol
		beginText.add("COPYRIGHT");
		beginText.add("Headnote");
		beginText.add("<!");
		beginText.add("Author Affiliation");
		beginText.add("CUTLINES");
		beginText.add("Key Sources:");
		beginText.add("To return to the main");
	}

	private void initializePatterns() {
		Pattern pat;
		
		// common city date format
		pat = Pattern.compile("^[A-Z]+\\,\\s[A-Z]{1}[a-z]{2}\\.\\s[0-9]+\\s.*");
		patternText.add(pat);
		
		// common figure, graph, picture ommitted
		pat = Pattern.compile("(.*Omitted)");
		patternText.add(pat);
		
		// common source listing
		pat = Pattern.compile("^Source:.*");
		patternText.add(pat);
		pat = Pattern.compile("^Sources:.*");
		patternText.add(pat);
		pat = Pattern.compile("\\[[A-Z]\\]*");
		patternText.add(pat);
		
		// MANY PIPE LINES INDICITIVE OF WEB TOOL BAR
		pat = Pattern.compile(".*\\|.*{5}");
		patternText.add(pat);

		// if code got accidently inserted
		pat = Pattern.compile(".*document\\.write.*");
		patternText.add(pat);
	}

	public boolean badText(String input) {
		if (input.length() > 2000) {
			input = input.substring(0, 2000) + "... (Paragraph Too Long)";
		}
		
		if (input.indexOf(".") == -1){
			return true;
		}
		
		if (checkBeginText(input)){
			return true;
		}
		
		if (checkPatternText(input)){
			return true;
		}
		
		// ALL UPPERCASE FIRST LETTERS WITH MORE THAN 20 WORDS, INDICITIVE OF
		// WEB KEYWORD LIST WITH 95% ACCURACY
		// CHECK VIA BATCH
		StringTokenizer st = new StringTokenizer(input);
		int count = 0;
		
		while (st.hasMoreTokens()) {
			String word = st.nextToken();
			if (word.matches("[A-Z]+[a-z]*")) {
				count++;
				if (count == 15) {
					// System.out.println("Skipping: " + input);
					return true;
				}
			} else{
				break;
			}
		}

		return false;
	}

	// returns true if the input string starts with
	// bad text (case is ignored)
	public boolean checkBeginText(String input) {
		String check;
		input = input.toLowerCase();
		
		for (int i = 0; i < beginText.size(); i++) {
			check = (String) beginText.get(i);
			check = check.toLowerCase();
			if (input.startsWith(check))
				return true;
		}
		return false;
	}

	// returns true if the input string matches any of the
	// regular expressions in patternText
	public boolean checkPatternText(String input) {
		Pattern pat;
		for (int i = 0; i < patternText.size(); i++) {
			pat = (Pattern) patternText.get(i);
			Matcher mat = pat.matcher(input);
			if (mat.find()){
				return true;
			}
		}
		return false;
	}
}