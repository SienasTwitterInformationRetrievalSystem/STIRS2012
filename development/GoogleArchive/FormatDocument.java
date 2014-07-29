package GoogleArchive;

import java.util.regex.*;
import java.io.*;
import java.util.*;

/**
 * Directory of plain text files Directory to store new formatted text files 
 * A file that contains all paths to be processed (the original html files)
 * 
 * @version 6/2011
 */
public class FormatDocument {
	public final static int AUTOOVERWRITE = 1;
	public final static int FAILIFDUP = 2;
	public final static int AUTORENAME = 3;
	public static String oText; // raw text
	public static String fText;
	public static String oPath; // original path
	public static String fPath;
	public static String mDestPath; // Dir of filtered file
	public static String mDate; // Date info
	public static String mAuthor; // Author info
	public static String mHeadline; // Headline info
	public static String mSrc; // Original Source info
	public static String mCopyright;// Copyright info
	public static String mContent; // real (useful) content
	public static String mDocNo; // DocNo
	public static String mUrl; // source URL
	public static int OVERWRITE = AUTOOVERWRITE; // whether to overwrite an
													// already existing file or
													// not
	public static String originalFilePath;
	public static boolean ISURL = false;
	public static int mLimit; // threshold of paragraph length
	public static int mContentParam; // method of extracting content
	public final static int HTML2TXT = 2;
	public final static int HTMLPARSER = 0;
	public final static int XYZPARSER = 1;
	public static List<String> monthTab; // table of month names
	public static List<String> dayTab; // table of date names
	public static String monthPtnStr; // pattern of month
	public static String filteredPath = "/projects/NL5/NL/NLCOMMON/web/code/filtered1/";
	public static String html2txtPath = "/home/xz7223/scenarios1_txt/";

	public static void preprocess() {
		oText = "";
		fText = "";
		oPath = "";
		mDate = "";
		mAuthor = "";
		mHeadline = "";
		mSrc = "";
		mCopyright = "";
		mContent = "";
		mDocNo = "";
		mUrl = "";
	}

	public static void setOverwrite(int newOW) {
		OVERWRITE = newOW;
	}

	/**
	 * @param contentParam
	 *            - 0: htmlparser ; 1: html2txt ; 2: xyz
	 * @param limit
	 *            threshold of paragraph length
	 * @param dir
	 *            filtered dir
	 */
	public synchronized static void init(int contentParam, int limit,
			String dir, String txt_dir) {
		monthPtnStr = "";
		monthTab = new ArrayList<String>();
		dayTab = new ArrayList<String>();
		setMonthTab();
		setDayTab();
		mLimit = limit;
		mContentParam = contentParam;

		if (!dir.endsWith("/")) {
			filteredPath = dir + "/";
		} else {
			filteredPath = dir;
		}
		if (!txt_dir.equals("") && !txt_dir.endsWith("/")) {
			html2txtPath = txt_dir + "/";
		} else {
			html2txtPath = txt_dir;
		}
	}

	/**
	 * @param path original path of unfiltered file
	 * @param text filtered text string
	 */
	public static String processing(String OFile, String FFile,
			boolean IsOFileName, boolean IsFFileName) {

		preprocess();

		if (IsOFileName) {
			setOPath(OFile);
			fillOText();
		} else
			setOText(OFile);
		if (IsFFileName) {
			setFPath(FFile);
			fillFText();
		} else
			setFText(FFile);

		setDocNo();
		setDateSrcAuthor(10);
		setCopyright(10);

		return writeToFile();
	}

	/**
	 * @param contentParam - 0: htmlparser ; 1: html2txt ; 2: xyz
	 * @param limit threshold of paragraph length
	 * @param dir filtered dir
	 */
	public synchronized static void init(int contentParam, int limit, String dir) {
		monthPtnStr = "";
		monthTab = new ArrayList<String>();
		dayTab = new ArrayList<String>();
		setMonthTab();
		setDayTab();
		mLimit = limit;
		mContentParam = contentParam;

		if (dir != null) {
			if (!dir.endsWith("/")) {
				filteredPath = dir + "/";
			}else {
				filteredPath = dir;
			}
		}
	}

	public static void Prln(String str) {
		System.out.println(str);
	}

	public static void print() {
		Prln("author: " + mAuthor);
		Prln("headline: " + mHeadline);
		Prln("date: " + mDate);
		Prln("content-----------" + mContent);
		Prln("copyright: " + mCopyright);
	}

	public synchronized static void setDayTab() {
		dayTab.add("Wednesday");
		dayTab.add("Wed.");
		dayTab.add("Wed");
		dayTab.add("Tuesday");
		dayTab.add("Tues.");
		dayTab.add("Tues");
		dayTab.add("Tue.");
		dayTab.add("Tue");
		dayTab.add("Thursday");
		dayTab.add("Thurs.");
		dayTab.add("Thurs");
		dayTab.add("Thur.");
		dayTab.add("Thur");
		dayTab.add("Thu.");
		dayTab.add("Thu");
		dayTab.add("Sunday");
		dayTab.add("Sun.");
		dayTab.add("Sun");
		dayTab.add("Saturday");
		dayTab.add("Sat.");
		dayTab.add("Sat");
		dayTab.add("Monday");
		dayTab.add("Mon.");
		dayTab.add("Mon");
		dayTab.add("Friday");
		dayTab.add("Fri.");
		dayTab.add("Fri");
	}

	public synchronized static void setMonthTab() {
		monthTab.add("APR");
		monthTab.add("APR.");
		monthTab.add("APRIL");
		monthTab.add("AUG");
		monthTab.add("AUG.");
		monthTab.add("AUGUST");
		monthTab.add("Apr");
		monthTab.add("Apr.");
		monthTab.add("April");
		monthTab.add("Aug");
		monthTab.add("Aug.");
		monthTab.add("August");
		monthTab.add("DEC");
		monthTab.add("DEC.");
		monthTab.add("DECEMBER");
		monthTab.add("Dec");
		monthTab.add("Dec.");
		monthTab.add("December");
		monthTab.add("FEB");
		monthTab.add("FEB.");
		monthTab.add("FEBRUARY");
		monthTab.add("Feb");
		monthTab.add("Feb.");
		monthTab.add("February");
		monthTab.add("JAN");
		monthTab.add("JAN.");
		monthTab.add("JANUARY");
		monthTab.add("JUL");
		monthTab.add("JUL.");
		monthTab.add("JULY");
		monthTab.add("JUN");
		monthTab.add("JUN.");
		monthTab.add("JUNE");
		monthTab.add("Jan");
		monthTab.add("Jan.");
		monthTab.add("January");
		monthTab.add("Jul");
		monthTab.add("Jul.");
		monthTab.add("July");
		monthTab.add("Jun");
		monthTab.add("Jun.");
		monthTab.add("June");
		monthTab.add("MAR");
		monthTab.add("MAR.");
		monthTab.add("MARCH");
		monthTab.add("MAY");
		monthTab.add("Mar");
		monthTab.add("Mar.");
		monthTab.add("March");
		monthTab.add("May");
		monthTab.add("NOV");
		monthTab.add("NOV.");
		monthTab.add("NOVEMBER");
		monthTab.add("Nov");
		monthTab.add("Nov.");
		monthTab.add("November");
		monthTab.add("OCT");
		monthTab.add("OCT.");
		monthTab.add("OCTOBER");
		monthTab.add("Oct");
		monthTab.add("Oct.");
		monthTab.add("October");
		monthTab.add("SEP");
		monthTab.add("SEP.");
		monthTab.add("SEPT");
		monthTab.add("SEPT.");
		monthTab.add("SEPTEMBER");
		monthTab.add("Sep");
		monthTab.add("Sep.");
		monthTab.add("Sept");
		monthTab.add("Sept.");
		monthTab.add("September");

		Iterator<String> iter = monthTab.iterator();
		String curr = "";

		if (iter.hasNext()) {
			curr = (String) iter.next();
		}

		monthPtnStr = curr;

		while (iter.hasNext()) {
			curr = (String) iter.next();
			monthPtnStr += "|" + curr;
		}
	}

	public static void setOPath(String path) {
		oPath = path;
	}

	public static void setFPath(String path) {
		fPath = path;
	}

	public static void setOText(String text) {
		oText = text;
	}

	public static void setFText(String text) {
		fText = text;
	}

	/**
	 * Fill in the original text of the document
	 */
	public static void fillOText() {
		String tmp_path = oPath;
		int index;

		if ((index = tmp_path.lastIndexOf("/")) > -1
				&& index < tmp_path.length()) {
			tmp_path = tmp_path.substring(index + 1, tmp_path.length());
		}

		File tfile = new File(tmp_path);
		char[] filedata = new char[(int) tfile.length()];

		try {
			BufferedReader in = new BufferedReader(new FileReader(tfile));
			in.read(filedata, 0, filedata.length);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		oText = new String(DocumentFilter.removeBadText(filedata.toString()));
	}

	/**
	 * Fill in the filtered document text
	 */
	public static void fillFText() {
		String tmp_path = fPath;
		int index;

		if ((index = tmp_path.lastIndexOf("/")) > -1
				&& index < tmp_path.length()) {
			tmp_path = tmp_path.substring(index + 1, tmp_path.length());
		}

		File tfile = new File(tmp_path);
		char[] filedata = new char[(int) tfile.length()];

		try {
			BufferedReader in = new BufferedReader(new FileReader(tfile));
			in.read(filedata, 0, filedata.length);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		fText = new String(DocumentFilter.removeBadText(filedata.toString()));
	}

	/*
	 * this method will go through the first lines of text, checking if there is
	 * information of Date/Author
	 * 
	 * @param n - number of lines to look up
	 */
	public static void setDateSrcAuthor(int n) {
		String[] lines = oText.split("\n");
		int size = n;
		boolean gotDate = false;
		boolean gotAuthor = false;
		boolean gotSrc = false;

		if (lines.length < n) {
			size = lines.length;
		}
		String line;

		for (int i = 0; i < size; i++) {
			line = lines[i];

			if (!gotDate) {
				if (lookupDate(line)) {
					gotDate = true;
				}
			}

			if (!gotAuthor) {
				if (lookupAuthor(line))
					gotAuthor = true;
			}

			if (!gotSrc) {
				if (lookupSrc(line))
					gotSrc = true;
			}
		}
	}

	/*
	 * this method will go through the last n lines of text, checking if there
	 * is copyright information
	 * 
	 * @param n -- number of lines look up
	 */
	public static void setCopyright(int n) {
		String[] lines = oText.split("\n");
		int size = n;

		if (lines.length < n) {
			size = lines.length;
		}

		String line;

		for (int i = 0; i < size; i++) {
			line = lines[lines.length - 1 - i];
			if (lookupCopyright(line)) {
				break;
			}
		}
	}

	/**
	 * method to set Document ID according to its path
	 **/
	public static void setDocNo() {
		if (fPath == null) {
			return;
		}
		String[] tmp = fPath.split("/");

		if (tmp != null && tmp.length == 1) {
			tmp = fPath.split("\\\\");
		}

		if (tmp != null) {
			if (tmp.length == 1) {
				mDocNo = tmp[0];
			} else if (tmp.length > 1) {
				mDocNo = tmp[tmp.length - 1];
			} else {
				mDocNo = "ERROR";
			}
			mDestPath = filteredPath + mDocNo;
		}
	}

	public static String writeToFile() {
		Calendar cal = new GregorianCalendar();

		// Get the components of the date
		StringBuffer sb = new StringBuffer();
		int mYear = cal.get(Calendar.YEAR); // 2002
		int mMonth = cal.get(Calendar.MONTH); // 0=Jan, 1=Feb, ...
		int mDay = cal.get(Calendar.DAY_OF_MONTH); // 1...
		mMonth++;

		if (mDestPath != null && !mDestPath.equals("")) {
			if (mDestPath.lastIndexOf(".") > 0) {
				mDestPath = mDestPath.substring(0, mDestPath.lastIndexOf("."));
			}

			mDestPath = mDestPath + ".txt";
		}

		if (mContent == null || mContent.length() < 100) {
			if (fText == null) {
				System.err.println("Unable to extract text from: " + mDestPath);
				return null;
			}
		}

		Pattern p = Pattern.compile("<[/]*[\\p{Alpha}]+>");
		Matcher m = p.matcher(mContent);

		if (m.find()) {
			mContent = m.replaceAll(" ");
		}

		try {
			sb.append("<h>");
			sb.append("<DOC>\n");
			sb.append("<DOCNO>" + mDocNo + "</DOCNO>\n");
			sb.append("<DOR>" + mYear);

			if (mMonth < 10) {
				sb.append("0" + mMonth);
			} else {
				sb.append(mMonth);
			}

			if (mDay < 10) {
				sb.append("0" + mDay);
			} else {
				sb.append("" + mDay);
			}

			sb.append("</DOR>\n");

			if (mHeadline.startsWith("org.htmlparser.util.ParserException")) {
				mHeadline = "Not Available";
			}
			sb.append("<TITLE> " + mHeadline + " </TITLE>\n");

			if (mAuthor.length() > 0) {
				sb.append("<AUTHOR>" + mAuthor + " </AUTHOR>\n");
			} else {
				sb.append("<AUTHOR>Not Available</AUTHOR>\n");
			}

			if (!mUrl.startsWith("<http")) {
				mUrl = "unknown";
			}

			sb.append("<PUBNAME><" + mUrl + "></PUBNAME>\n");

			if (mDate.length() > 0) {
				sb.append("<ORIGDATE>" + mDate + "</ORIGDATE>\n");
			} else {
				sb.append("<ORIGDATE>00000000</ORIGDATE>\n");
			}

			sb.append("<DOCCLASS>U</DOCCLASS>\n");
			sb.append("<VENDOR>Google Mined</VENDOR>\n");
			sb.append("</h>\n");
			sb.append("<HEADER>\n</HEADER>\n");
			sb.append("<TEXT>\n\n");
			sb.append(fText);
			sb.append("\n\n</TEXT>\n</DOC>\n");

			// Make sure if a file already exists with that name, the program
			// takes the appropriate action
			if (OVERWRITE != AUTOOVERWRITE && mDestPath != null
					&& !mDestPath.equals("")) {

				File f = new File(mDestPath);

				if (f.exists()) {
					if (OVERWRITE == FAILIFDUP) {
						System.err
								.println("File already exists with that name");
						return null;
					} else {
						int i = 0;

						while (f.exists()) {
							mDestPath = mDestPath + i;
							f = new File(mDestPath);
							i++;
						}
					}
				}

				BufferedWriter out = new BufferedWriter(new FileWriter(
						mDestPath));

				out.write(sb.toString());
				out.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		return sb.toString();
	}

	public static String checkChar(String str) {
		if (str == null)
			return "";
		int i = 0;
		char c;
		String result = "";
		while (i < str.length()) {
			c = str.charAt(i++);
			if (c <= '~' && c >= ' ')
				result += c;
		}
		return result;
	}

	public static boolean lookupDate(String line) {
		Pattern pDate1 = Pattern.compile("(" + monthPtnStr
				+ ")[\\p{Digit}\\p{Punct}\\p{Space}]{1,5}[0-9]{4}");
		Pattern pDate2 = Pattern.compile("(" + monthPtnStr
				+ ")[\\p{Punct}\\p{Space}][0-9]{4}");
		Matcher mDate1 = pDate1.matcher(line);
		Matcher mDate2 = pDate2.matcher(line);

		int index = -1;
		boolean flag = false;

		for (int i = 0; i < monthTab.size(); i++) {
			String cur = (String) monthTab.get(i);

			if ((index = line.indexOf(cur)) > -1) {
				if (mDate1.find(index)) {
					mDate = mDate1.group();
					flag = true;
				} else if (mDate2.find(index)) {
					mDate = mDate2.group();
					flag = true;
				}
			}

			if (flag) {
				break;
			}
		}
		return flag;

	}

	public static boolean lookupSrc(String line) {
		Pattern pSrc1 = Pattern
				.compile("[\\p{Upper}][\\p{Alpha}]{3,10}[\\p{Alpha}\\p{Space}]*[:,-]");
		Pattern pSrc2 = Pattern
				.compile("[\\p{Upper}\\p{Space}]{3,}+[\\p{Alpha}\\p{Space}\\(\\)]*[\\p{Space}]*[:-]");
		Pattern pSrc3 = Pattern.compile("[\\p{Upper}][\\p{Alpha}]{3,10} Times");
		Matcher matchSrc1 = pSrc1.matcher(line);
		Matcher matchSrc2 = pSrc2.matcher(line);
		Matcher matchSrc3 = pSrc3.matcher(line);
		boolean flag = false;

		if (matchSrc1.find()) {
			String tmp = matchSrc1.group();

			mSrc = tmp.substring(0, tmp.length() - 1);

			if (line.indexOf(mSrc) == 0) {
				flag = true;
			} else
				mSrc = "";
		}

		if (!flag) {
			if (matchSrc2.find()) {
				String tmp = matchSrc2.group();

				if (line.indexOf(tmp) == 0) {
					mSrc = tmp.substring(0, tmp.length() - 1);
					flag = true;
				}
			}
		}

		if (!flag) {
			if (matchSrc3.find()) {
				String tmp = matchSrc3.group();
				if (line.indexOf(tmp) == 0) {
					mSrc = tmp;
					flag = true;
				}
			}
		}

		for (int j = 0; j < dayTab.size(); j++) {
			String day = (String) dayTab.get(j);

			if (mSrc.equalsIgnoreCase(day)) {
				mSrc = "";
				flag = false;
				break;
			}
		}

		if (mSrc.equalsIgnoreCase("Date")) {
			mSrc = "";
			flag = false;
		}
		mSrc = mSrc.trim();
		return flag;

	}

	public static boolean lookupAuthor(String line) {
		Pattern pAuthor1 = Pattern
				.compile("by[[:-]\\p{Space}]+([\\p{Upper}][\\p{Space}\\p{Alpha}\\.\\-&&[^\\n]]+)");
		Pattern pAuthor2 = Pattern
				.compile("By[[:-]\\p{Space}]+([\\p{Upper}][\\p{Space}\\p{Alpha}\\.\\-&&[^\\n]]+)");
		Pattern pAuthor3 = Pattern
				.compile("BY[[:-]\\p{Space}]+([\\p{Upper}][\\p{Space}\\p{Alpha}\\.\\-&&[^\\n]]+)");
		Pattern pAuthor4 = Pattern
				.compile("Author[[:-]\\p{Space}]+([\\p{Upper}][\\p{Space}\\p{Alpha}\\.\\-&&[^\\n]]+)");
		Pattern pAuthor5 = Pattern
				.compile("AUTHOR[[:-]\\p{Space}]+([\\p{Upper}][\\p{Space}\\p{Alpha}\\.\\-&&[^\\n]]+)");
		Pattern pAuthor6 = Pattern
				.compile("author[[:-]\\p{Space}]+([\\p{Upper}][\\p{Space}\\p{Alpha}\\.\\-&&[^\\n]]+)");
		Pattern pAuthor7 = Pattern
				.compile("[\\p{Upper}][\\p{Alpha}]{3,10}[\\p{Space}\\.\\-&&[^\\n]][\\p{Upper}][\\p{Alpha}]{3,10}");
		Pattern pAuthor8 = Pattern
				.compile("[\\p{Upper}][\\p{Alpha}]{3,10}[\\p{Space}\\.\\-&&[^\\n]][\\p{Upper}][\\p{Alpha}]{3,10}[\\p{Space}\\.\\-&&[^\\n]][\\p{Upper}][\\p{Alpha}]{3,10}");

		boolean flag = false;
		Matcher mAuthor1 = pAuthor1.matcher(line);
		Matcher mAuthor2 = pAuthor2.matcher(line);
		Matcher mAuthor3 = pAuthor3.matcher(line);
		Matcher mAuthor4 = pAuthor4.matcher(line);
		Matcher mAuthor5 = pAuthor5.matcher(line);
		Matcher mAuthor6 = pAuthor6.matcher(line);
		Matcher mAuthor7 = pAuthor7.matcher(line.trim());
		Matcher mAuthor8 = pAuthor8.matcher(line.trim());

		if (mAuthor1.find()) {
			mAuthor = mAuthor1.group(1);
			flag = true;
		} else if (mAuthor2.find()) {
			mAuthor = mAuthor2.group(1);
			flag = true;
		} else if (mAuthor3.find()) {
			mAuthor = mAuthor3.group(1);
			flag = true;
		} else if (mAuthor4.find()) {
			mAuthor = mAuthor4.group(1);
			flag = true;
		} else if (mAuthor5.find()) {
			mAuthor = mAuthor5.group(1);
			flag = true;
		} else if (mAuthor6.find()) {
			mAuthor = mAuthor6.group(1);
			flag = true;
		} else if (mAuthor7.find()) {
			mAuthor = mAuthor7.group();

			if (mAuthor.length() == line.trim().length()) {
				flag = true;
			} else {
				mAuthor = "";
			}
		} else if (mAuthor8.find()) {
			mAuthor = mAuthor8.group();

			if (mAuthor.length() == line.trim().length()) {
				flag = true;
			} else {
				mAuthor = "";
			}
		} else {
			flag = false;
			return flag;
		}

		int i;
		String cur;

		for (i = 0; i < monthTab.size(); i++) {
			cur = (String) monthTab.get(i);

			if (mAuthor.equalsIgnoreCase(cur)) {
				mAuthor = "";
				flag = false;
				break;
			}
		}

		for (i = 0; i < dayTab.size(); i++) {
			cur = (String) dayTab.get(i);

			if (mAuthor.equalsIgnoreCase(cur)) {
				mAuthor = "";
				flag = false;
				break;
			}
		}
		return flag;
	}

	public static boolean lookupCopyright(String line) {
		int start = line.indexOf("Copyright");
		int end = line.indexOf("All rights reserved");

		if (start > -1 && end > -1) {
			if (start + 10 < line.length() && end < line.length()) {
				start += 10;
				if (start > end) {
					return false;
				}

				mCopyright = line.substring(start, end);
				char c;

				if (mCopyright != null && mCopyright.length() >= 1) {
					c = mCopyright.charAt(0);
					while (mCopyright.length() >= 2 && !(c <= 'Z' && c >= 'A')
							&& !(c <= 'z' && c >= 'a')
							&& !(c <= '9' && c >= '0')) {
						mCopyright = mCopyright.substring(1,
								mCopyright.length());
						c = mCopyright.charAt(0);
					}
				}
				return true;
			} else
				return false;
		} else
			return false;
	}

	public static String skiptag(String html, String begintag, String endtag) {
		String nhtml = "";
		String htmp = html.toLowerCase();
		String btmp = begintag.toLowerCase();
		String etmp = endtag.toLowerCase();
		int bi = 0, ei = 0;

		while ((bi < html.length()) && ((bi = htmp.indexOf(btmp, ei)) >= 0)) {
			if ((ei < html.length()) && (bi < html.length()) && (ei >= 0)
					&& (bi >= 0) && (ei <= bi)) {
				nhtml += html.substring(ei, bi);
			} else
				break;

			ei = htmp.indexOf(etmp, bi + 1);
		}

		if (ei >= 0) {
			nhtml += html.substring(ei);
		}

		return nhtml.trim();
	}

	public static char[] extractText(String path, int blimit)
			throws IOException {
		File tfile = new File(path);

		BufferedReader in = new BufferedReader(new FileReader(tfile));
		;

		char[] filedata = new char[(int) tfile.length()];

		try {
			in.read(filedata, 0, filedata.length);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		in.close();

		String rawstring = new String(filedata);

		rawstring = rawstring.replaceAll("&nbsp;", " ");
		rawstring = skiptag(rawstring, "<script", "</script");

		char[] rawdata = new char[(int) rawstring.length()];

		rawstring.getChars(0, rawstring.length(), rawdata, 0);
		char[] data = new char[rawdata.length];
		boolean isContent = true;

		int i = 0, j = 0;
		int len = 0;
		boolean keeptag = false;

		for (i = 0; i < rawdata.length; i++) {
			if (isContent) {
				len++;
				data[j++] = rawdata[i];
			}

			if ((rawdata[i] == '<') && isContent) {
				if ((i < rawdata.length - 1) && (rawdata[i + 1] == '!')
						&& (rawdata[i + 2] == '-')) {

					while ((i < rawdata.length - 1)
							&& !((rawdata[i] == '-') && (rawdata[i + 1] == '>'))) {
						i++;
					}

					if (i < rawdata.length - 2) {
						i += 1;
					}

					len = 0;
					if (j > 0)
						j--;
					continue;
				}

				isContent = false;

				if ((j >= len) && (len < blimit) && !keeptag) {
					j -= len;
				} else if (j > 0) {
					data[j - 1] = ' ';
				}
				len = 0;
				if (i < rawdata.length - 1) {
					if (((rawdata[i + 1] == 'b') || (rawdata[i + 1] == 'B'))
							&& (rawdata[i + 2] != 'r')) {
						keeptag = true;
					} else {
						keeptag = false;
					}
				}
			} else if ((rawdata[i] == '>') && !isContent) {
				isContent = true;
			}
		}

		for (i = j; i < rawdata.length; i++) {
			data[i] = (char) '\0';
		}
		return data;
	}

	public static String chooseText(String rawstring, int blimit) {
		String[] lines = rawstring.split("[\\n\\t]");
		String fstring = "";
		int i = 1;
		int newline = 0;
		boolean textbegin = false;

		while (i < lines.length) {
			if (lines[i].toLowerCase().indexOf("copyright") >= 0) {
				break;
			}

			if (lines[i].length() > 1600) {
				return "";
			}

			if (lines[i].trim().length() >= blimit) {
				fstring += lines[i] + "\n\n";
				newline = 0;
				textbegin = true;
			}

			if (lines[i].length() == 0) {
				if (newline == 0) {
					fstring += "";
				}

				newline++;
				if ((newline >= 4) && textbegin) {
					break;
				}
			} else {
				newline = 0;
			}
			i++;
		}

		return fstring.trim();
	}

	public static void main(String[] args) {
		try {
			if (args.length < 5) {
				System.out
						.println("Usage: java FormatText -html2txt Limit Corpus TxtDir DestDir");
				System.exit(0);
			}

			/* Parse the command line */
			int count = 0;
			int content = 2;

			/* what are we goin to use ? */
			String content_method = args[count++];
			if (content_method.equalsIgnoreCase("-htmlparser")) {
				content = 0;
			} else if (content_method.equalsIgnoreCase("xyzparser")) {
				content = 1;
			} else if (content_method.equalsIgnoreCase("-html2txt")) {
				content = 2;
			} else {
				content = 2;
			}

			int limit;
			String corpus;
			String txt_dir;
			String destdir;
			String tmp = args[count++]; // holds the second parameter

			// remove the '-' from the limit parameter if there is one
			if (tmp.startsWith("-")) {
				tmp = tmp.substring(1, tmp.length()); // tmp holds some number
			}

			limit = Integer.parseInt(tmp); // paragraphs shorter than this value
											// will not be processed
			corpus = args[count++]; // file of unfiltered files. i.e. .html
									// files before HTML2TXT was applied
			txt_dir = args[count++]; // directory of filtered file i.e. html
										// files after HTML2TXT was applied
			destdir = args[count++]; // directory of final-processed file will
										// be stored after this program is
										// applied

			File file = new File(txt_dir);

			if (!file.exists()) {
				System.out.println("Text-doc directory doesn't exist!");
				System.exit(0);
			}

			if (destdir.endsWith("/")) {
				destdir = destdir.substring(0, destdir.length() - 1);
			}
			
			file = new File(destdir);

			if (!file.exists()) {
				System.out.println("Destination directory doesn't exist!");
				System.exit(0);
			}

			Prln("prepare for file list");

			// + Read each line of the corpus file
			// + Check to see if that file exists
			// + Add it to a list of files 'files'
			BufferedReader in = new BufferedReader(new FileReader(corpus));
			String line;
			List<String> files = new ArrayList<String>();

			while ((line = in.readLine()) != null) {
				file = new File(line);
				
				if (!file.exists()) {
					System.out.println("Skip file -" + line + "doesn't exist!");
					continue;
				}
				
				files.add(line.trim());
			}
			in.close();
			
			// Initialize this program by:
			// 1) setting which operation will be performed (e.g. html2txt)
			// 2) setting the smallest paragraph size
			// 3) setting where the final files will be stored
			// 3) setting where the .txt files are located
			// System.out.println("Calling init");
			FormatDocument.init(content, limit, destdir, txt_dir);
		} catch (IOException ioe) {
			System.out.println("Error in FormatText.java");
			ioe.printStackTrace(System.out);
		}
	}
}