package org.bh.tools.util.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import static org.bh.tools.util.json.Test.findDifference;
import static org.bh.tools.util.json.Test.toJSONObject;

/**
 * JSONParser, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-11
 */
public class JSONParser
{
	public static final char OBJECT_START        = '{';
	public static final char OBJECT_END          = '}';
	public static final char ARRAY_START         = '[';
	public static final char ARRAY_END           = ']';
	public static final char SEPARATOR           = ',';
	public static final char KEY_END             = ':';
	public static final char STRING_ESCAPE_START = '\\';
	public static final String STRING_START_END  = "\"'";
	public static final String VALUE_END         = ",}]";
	
	/** <code>/\*.*?\*&#47;s</code> */
	public static final Pattern COMMENT_BLOCK = Pattern.compile("\\*.*?\\*", Pattern.DOTALL);
	/** {@code /\/\/.?$/m} */
	public static final Pattern COMMENT_LINE = Pattern.compile("//.?$", Pattern.MULTILINE);
	/** <code>/(\*.*?\*)|(\/\/.?$)&#47;sm</code> */
	public static final Pattern COMMENT_ANY = Pattern.compile(
			"(" + COMMENT_BLOCK.pattern() + ")|(" + COMMENT_LINE.pattern() + ')',
			Pattern.DOTALL | Pattern.MULTILINE);
	
	/** {@code /".*?"/} */
	public static final Pattern STRING = Pattern.compile("[" + STRING_START_END + "].*?[" + STRING_START_END + "]");
	/** {@code /(true)|(false)/} */
	public static final Pattern BOOLEAN = Pattern.compile("(true)|(false)");
	/** {@code /\d+/} */
	public static final Pattern INTEGER = Pattern.compile("\\d+");
	/** {@code /\d*\.\d+/} */
	public static final Pattern FLOAT = Pattern.compile("\\d*\\.\\d+");
	/** {@code /\[.+?(,.+?)*?\]/} */
	public static final Pattern ARRAY = Pattern.compile("\\[([^,]+?(,[^,]+?)*?)?\\]");
	/** {@code /\{".*?":.*?(,".*?":.*?)*?\}/} */
	public static final Pattern OBJECT = Pattern.compile("\\{(" + STRING.pattern() + ":.*?(," + STRING.pattern() + ":.*?)*?)?\\}");
	
	private static final NoClassDefFoundError NO_COMPATIBLE_JS_ENGINE;
	private static ScriptEngine js;
	
	static
	{
		NO_COMPATIBLE_JS_ENGINE =
			new NoClassDefFoundError("Could not find a compatible JavaScript engine."
				+ " Try the org.bh.tools.util.json.JSONParser#hardcodedParse(CharSequence) method instead.");
	}
	
	
	public static boolean isStringStartEnd(char c)
	{
		return STRING_START_END.indexOf(c) != -1;
	}
	private static boolean isValueEnd(char c)
	{
		return VALUE_END.indexOf(c) != -1;
	}
	
	/**
	 * Uses vanilla Java code to parse the given string into a {@link JSONObject}. This may fail if the definition of JSON
	 * changes. If it does, try {@link #softcodedParse(java.lang.CharSequence)} instead.
	 * 
	 * @param json the string containing a valid JSON object to parse. JS comments and whitepace are O.K.
	 * @return the given string, parsed as a {@link JSONObject}
	 */
	public static JSONObject hardcodedParse(CharSequence json)
	{
		//<editor-fold defaultstate="collapsed" desc="Remove whitespace and comments">
		json =
			removeWhitespace( // then remove whitespace
					removeComments(json) // first remove comments
			)
		;
//		System.out.println("Parsing " + json);
		//</editor-fold>
		
		//<editor-fold defaultstate="collapsed" desc="Basic Validation">
		if (json.length() < 2) // no valid JSON string is less than 2 characters
			throw new IllegalArgumentException("JSON must be at least {}");
		char current = json.charAt(0);
		if (current != '{') // all JSON strings must start with a {, since we already removed whitespace and comments
			throw new IllegalArgumentException("JSON must start with {");
		
		JSONObject ret = new JSONObject();
		
		if (json.length() == 2) // there's only 1 possible JSON string that's 2 charactersL {}
			if (json.equals("{}")) // if it is {},
				return ret; // return an empty JSON object
			else
				throw new IllegalArgumentException("two-character JSON must be {}"); // nahp dun care what it is it's WRONG
		//</editor-fold>

		//<editor-fold defaultstate="collapsed" desc="parser objects">
		// if we're parsing a string
//		boolean inString = false;
		// for building strings
		StringBuilder string = new StringBuilder();
		StringBuilder mystery = new StringBuilder();
		// if we're parsing an array
		//boolean inArray = false; - unneeded since hardcodedParseArray
		// for building arrays
		//ArrayList array = new ArrayList(); - unneeded since hardcodedParseArray
		// false == in key, true == in value
		boolean inVal = false;
		// how many arrays or objects deep we are
		//short arrayDepth = 0, objectDepth = 0; - unneeded since getInnerString
		// this is a lookback so we can think about what's already happened
		char prev = current;
		// is this a double-quoted string?
		boolean dqString = true; // usually true
		// since we already confirmed that the first character is {, we start on the second.
		current = json.charAt(1);
		// our key
		CharSequence key = "This should never be a key";
		int i = 1;
		//</editor-fold>
		
		try{
		mainParser: for(
			int l = json.length();
			i < l;
			prev = current,
				current = i++ >= l - 1
					? json.charAt(l - 1)
					: json.charAt(i))
		{
			if (string != null && string.toString().equals("definition"))
				System.out.println("definition!");
			if (isStringStartEnd(current) && prev != STRING_ESCAPE_START)
			{
				if (!inString) // if this is the beginning of a new string
				{
					inString = true;
					string = new StringBuilder();
					dqString = current == '"';
				}
				else if (dqString ? current == '"' : current == '\'') // if this is the end of a string
				{
					inString = false;
					// increment the character to validate
					//prev = current; // prev will be our quote
					current = json.charAt(++i);
					
					// "string" is our string!
					if (inVal) // if we're in a value
					{
						/*if (inArray) // if we're in an array
						{
							array.add(string.toString()); // quote the string and add it to the array
							if (current == ARRAY_END) // if this is the end of an array
							{
								inArray = inVal = false; // then both the array and this value are over
								ret.set(key, array.toArray()); // add this array to the object
							}
						}
						else // if we're not in an array, then the value is over*/
						{
							inVal = false;
							// all values must be followed by a comma, end bracket, or end brace
							if (!isValueEnd(current))
								throw new InvalidJSONException("All values must be followed by one of (" + VALUE_END + ")", json, i, ret);
							ret.set(key, string);
							inVal = false;
						}
						if (!inVal && i == l - 1)
							break mainParser;
					}
					else // if we're in a key
					{
						inVal = true; // the next string will be a value.
						key = string;
						if (current != KEY_END) // all keys must be followed by a colon
							throw new InvalidJSONException("All keys must be followed by (" + KEY_END + ")", json, i, ret);
					}
				}
				else
					string.append(current);
				continue mainParser;
			}
			else if (inString)
			{
				string.append(current);
				continue mainParser;
			}
			else if (current == OBJECT_START) // if we're entering a sub-object
			{
				CharSequence innerObject = getInnerString(json, i, OBJECT_START, OBJECT_END); // extract the inner object
				JSONObject val = JSONParser.hardcodedParse(innerObject); // recursively parse that object
				current = json.charAt(i += innerObject.length()); // remember to properly place that counter
				inVal = false; // we're done with this value
				ret.set(key, val);
				continue mainParser;
			}
			else if (current == ARRAY_START)
			{
				CharSequence innerArray = getInnerString(json, i, ARRAY_START, ARRAY_END); // extract the inner object
				Object[] val = JSONParser.hardcodedParseArray(innerArray); // recursively parse that object
				current = json.charAt(i += innerArray.length()); // remember to properly place that counter
				inVal = false; // we're done with this value
				ret.set(key, val);
				continue mainParser;
			}
			else if (isValueEnd(current))
			{
				inVal = false;
				if (mystery.length() != 0) // if we're parsing a mystery value (hopefully primitive)
				{
					Object primitive = makePrimitive(mystery);
					/*if (inArray)
						array.add(primitive);
					else*/
						ret.set(key, primitive); // make a primitive out of it and add it to the object
					mystery = new StringBuilder(); // reset the mystery builder
					continue mainParser;
				}
				else // we've probably already parsed it. Just move on to the next one
					continue mainParser;
			}
			else
				mystery.append(current);
		}
		}
		catch(Throwable t)
		{
			throw new InvalidJSONException("unexpected failure", json, i, ret, t);
		}
		return ret;
	}

	/**
	 * Uses a Java internal JavaScript engine to hardcodedParse the given JSON string. This may fail if your version of Java has no such
	 * engine. If it does fail, try using {@link #hardcodedParse(java.lang.CharSequence)} instead.
	 * 
	 * @param json the string containing a valid JSON object to parse. JS comments and whitepace are O.K.
	 * @return the given string, parsed as a {@link JSONObject}
	 * 
	 * @throws InvalidJSONException if the given object is not valid JSON
	 * @throws NoClassDefFoundError if there is no compatible JavaScript engine. If this is thrown, try using
	 *         {@link #hardcodedParse(java.lang.CharSequence)} instead
	 */
	public static JSONObject softcodedParse(CharSequence json) throws InvalidJSONException, NoClassDefFoundError
	{
		try
		{
			if (js == null) js = new ScriptEngineManager().getEngineByName("javascript");
			Object result = js.eval("j=" + json); // the "j=" is so the JS engine actually sees this as an object
			return makeJSONObject((JSObject)result);
		}
		catch (ScriptException se)
		{
			throw new InvalidJSONException(se); // we want to give them the exception we promised in the JavaDoc.
		}
		catch (Throwable t)
		{
			throw NO_COMPATIBLE_JS_ENGINE;
		}
	}
	
	/**
	 * Reads the given input stream, and passes it as a {@link String} to {@link #hardcodedParse(java.lang.CharSequence)}
	 * 
	 * @param json the stream to read. It should result in a string containing a valid JSON object to parse. JS
	 *        comments and whitepace are O.K.
	 * @return the given string, parsed as a {@link JSONObject}
	 * 
	 * @throws IOException if the given {@link InputStream} throws an {@link IOException} at any time for any reason.
	 * @see #hardcodedParse(java.lang.CharSequence)
	 */
	public static JSONObject hardcodedParse(InputStream json) throws IOException
	{
		return hardcodedParse(bringIn(json));
	}
	
	/**
	 * Reads the given input stream, and passes it as a {@link String} to {@link #softcodedParse(java.lang.CharSequence)}
	 * 
	 * @param json the stream to read. It should result in a string containing a valid JSON object to parse. JS
	 *        comments and whitepace are O.K.
	 * @return the given string, parsed as a {@link JSONObject}
	 * 
	 * @throws IOException if the given {@link InputStream} throws an {@link IOException} at any time for any reason.
	 * @see #softcodedParse(java.lang.CharSequence)
	 */
	public static JSONObject softcodedParse(InputStream json) throws IOException
	{
		return softcodedParse(bringIn(json));
	}
	
	/**
	 * Makes {@link #hardcodedParse(java.io.InputStream)} and {@link #softcodedParse(java.io.InputStream)} easier to write and
	 * maintain
	 */
	private static String bringIn(InputStream is) throws IOException
	{
		int read;
		ArrayList<Integer> inList = new ArrayList<>();
		while ((read = is.read()) != -1)
			inList.add(read);
		byte[] ints = new byte[inList.size()];
		for(int i = 0; i < ints.length; i++)
			ints[i] = (byte)(int)inList.get(i);
		return new String(ints);
	}
	
	
	private static JSONObject makeJSONObject(JSObject o)
	{
		JSONObject parsed = new JSONObject();
		JSObject resultJSO = (JSObject)o;
		Set<String> keySet = resultJSO.keySet();
		Collection<Object> valueCollection = resultJSO.values();
		String[] keys = new String[keySet.size()];
		Object[] values = new Object[valueCollection.size()];
		keys = keySet.toArray(keys);
		values = valueCollection.toArray(values);
		for (int i = 0, l = keySet.size(); i < l; i++)
		{
			parsed.set(keys[i],
				values[i] instanceof JSObject
					? ((JSObject)values[i]).isArray()
						? makeJSONArray((JSObject)values[i])
						: makeJSONObject((JSObject)values[i])
					: values[i]
			);
		}
		return(parsed);
	}
	
	private static Object[] makeJSONArray(JSObject array)
	{
		return array.values().toArray();
	}
	
	/**
	 * Removes all JavaScript comments. Although JSON does not officially support these, J. Jonah JSON does.
	 * 
	 * @param json the raw JSON, JS comments and all
	 * @return the input without its comments
	 */
	public static CharSequence removeComments(CharSequence json)
	{
		return COMMENT_ANY.matcher(json).replaceAll("");
	}

	/**
	 * Removes all whitespace, thanks to a regex from StackOverflow:
	 * http://stackoverflow.com/a/25250954/453435
	 * 
	 * @param json valid JSON whose unnecessary whitespace is to be removed
	 * @return a JSON string without unnecessary whitespace
	 */
	public static CharSequence removeWhitespace(CharSequence json)
	{
		return (json+"").replaceAll("(\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\")|(\\s+)", "$1");
		/*
		StringBuilder withoutWhitespace = new StringBuilder();
		boolean skipping = false;
		for(int i = 0, l = json.length(); i < l; i++)
		{
			char c = json.charAt(i);
			if (((c == '"' || c == '\'') && (skipping = !skipping)) // do not process quoted strings
					|| skipping                                     // contnue if we're in a quoted string
					|| Character.isWhitespace(c))                   // don't append whitespace characters outside strings
				continue;
			withoutWhitespace.append(c);
		}
		return withoutWhitespace;*/
	}
	
	public static Object makePrimitive(CharSequence mystery)
	{
		if (STRING.matcher(mystery).matches())
			return String.valueOf(mystery);
		if (BOOLEAN.matcher(mystery).matches())
			return Boolean.valueOf(mystery+"");
		else if (FLOAT.matcher(mystery).matches())
			return Double.valueOf(mystery+"");
		else if (INTEGER.matcher(mystery).matches())
			return Long.valueOf(mystery+"");
		else
			throw new InvalidJSONException("Illegal value (possibly unquoted string?): " + mystery);
	}
	
	public static void main(String... args) throws FileNotFoundException, IOException
	{
//		CharSequence test = new StringBuilder();
		File input = new File("U:\\Libraries\\Programs\\Github\\Open-Dictionary-Project\\concept.json");
		FileInputStream fis = new FileInputStream(input);
		String json = bringIn(fis);
//		JSONObject test = hardcodedParse(fis);
		/*Scanner scan = new Scanner(input);
		Pattern everything = Pattern.compile("", Pattern.DOTALL | Pattern.MULTILINE);
		while (scan.hasNext(everything))
			test.append(scan.next(everything));*/
//		System.out.println(test);
//		System.out.println(removeWhitespace(test));
		
		System.out.println("Parsing the following string: \r\n"
				+ " \t" + json);
		
		double hardTotal = 0, softTotal = 0;
		int limit = 1;
		
		System.out.println("Testing both methods " + limit + " times...");
		for (int i = 0; i < limit; i++)
		{
			long start, key1, key2, key3;

			JSONObject jsono;

			start = System.nanoTime();
			jsono = JSONParser.hardcodedParse(json);
			key1 = System.nanoTime();

			String hard = jsono.toString();

			key2 = System.nanoTime();
			jsono = JSONParser.softcodedParse(json);
			key3 = System.nanoTime();

			String soft = jsono.toString();


//			System.out.println(findDifference(hard, soft));

			double
				hardHere = ((key1 - start) / 1_000_000.0),
				softHere = ((key3 - key2) / 1_000_000.0);
			/*System.out.println(
				"test " + i + "\r\n" +
				"\thard: " + hardHere + "ms\r\n" + 
				"\tsoft: " + softHere + "ms"
			);*/

			hardTotal += ((key1 - start) / 1_000_000.0);
			softTotal += ((key3 - key2) / 1_000_000.0);
		}
		System.out.println("\r\n==== " + limit + " TESTS LATER ====\r\n");
		System.out.println(
			"hardTotal: " + hardTotal + "ms; average: " + (hardTotal / limit) + "ms \r\n" +
			"softTotal: " + softTotal + "ms; average: " + (softTotal / limit) + "ms \r\n"
		);
		
		System.out.println("Hardcoded interpreter result: " + JSONParser.hardcodedParse(json));
		System.out.println("Softcoded interpreter result: " + JSONParser.softcodedParse(json));
	}

	private static Object[] hardcodedParseArray(CharSequence array)
	{
		ArrayList ret = new ArrayList();
		
		int i = 1;
		
		char
//			prev = '[', // all arrays start with [
			current = array.charAt(i)
		;
		
		StringBuilder val = new StringBuilder();
		
		for(
			int l = array.length() - 1;
			i < l;
//			prev = current,
				current = array.charAt(++i))
		{
			if (current == ARRAY_START)
			{
				CharSequence innerArray = getInnerString(array, i, ARRAY_START, ARRAY_END);
				ret.add(hardcodedParseArray(innerArray));
				i += innerArray.length();
				continue;
			}
			else if (current == OBJECT_START)
			{
				CharSequence innerObject = getInnerString(array, i, OBJECT_START, OBJECT_END);
				ret.add(hardcodedParse(innerObject));
				i += innerObject.length();
				continue;
			}
			else if (isValueEnd(current)) // if this is the end of a value
			{
				ret.add(makePrimitive(val));
				val = new StringBuilder();
				continue;
			}
			else
			{
				val.append(current);
				continue;
			}
		}
		
		return ret.toArray();
	}
	
	/**
	 * Returns a substring surrounded by the given characters. If another start character is found, its corresponding end
	 * character is ignored.
	 * For example, when given ("[lol, [herp, [derp, qwerp]], smrt, [yay, fun]]", 6, '[', ']'), it will return "[herp, [derp, qwerp]]".
	 * 
	 * @param string the string whose substring is to be extracted
	 * @param startPos the index to start from
	 * @param startChar the character that indicates another substring has started
	 * @param endChar the character that indicates a substring has ended
	 * @return the substring starting at {@code startPos} and ending at its corresponding {@code endChar}
	 */
	private static CharSequence getInnerString(CharSequence string, int startPos, char startChar, char endChar)
	{
		short depth = 1;
		int length = string.length();
		char current = string.charAt(startPos++);
		StringBuilder innerString = new StringBuilder(current+"");
		for (
			current = string.charAt(startPos);
			depth > 0
				&& startPos < length;
			current = string.charAt(++startPos))
		{
			if (current == startChar)
				depth++;
			else if (current == endChar)
				depth--;
			innerString.append(current);
		}
		return innerString;
	}
}
