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
	public static final Pattern COMMENT_LINE  = Pattern.compile("//.?$", Pattern.MULTILINE);
	/** <code>/(\*.*?\*)|(\/\/.?$)&#47;sm</code> */
	public static final Pattern COMMENT_ANY   = Pattern.compile(
			"(" + COMMENT_BLOCK.pattern() + ")|(" + COMMENT_LINE.pattern() + ')',
			Pattern.DOTALL | Pattern.MULTILINE);
	
	/** {@code /".*?"/} */
	public static final Pattern STRING  = Pattern.compile("[" + STRING_START_END + "].*?[" + STRING_START_END + "]");
	/** {@code /(true)|(false)/} */
	public static final Pattern BOOLEAN = Pattern.compile("(true)|(false)");
	/** {@code /(true)|(false)/} */
	public static final Pattern NULL    = Pattern.compile("(null)");
	/** {@code /\d+/} */
	public static final Pattern INTEGER = Pattern.compile("\\d+");
	/** {@code /\d*\.\d+/} */
	public static final Pattern FLOAT   = Pattern.compile("\\d*\\.\\d+");
	/** {@code /\[.+?(,.+?)*?\]/} */
	public static final Pattern ARRAY   = Pattern.compile("\\[([^,]+?(,[^,]+?)*?)?\\]");
	/** {@code /\{".*?":.*?(,".*?":.*?)*?\}/} */
	public static final Pattern OBJECT  = Pattern.compile("\\{(" + STRING.pattern() + ":.*?(," + STRING.pattern() + ":.*?)*?)?\\}");
	
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
			throw new IllegalArgumentException("JSON must be at least " + OBJECT_START + OBJECT_END);
		char current = json.charAt(0);
		if (current != '{') // all JSON strings must start with a {, since we already removed whitespace and comments
			throw new IllegalArgumentException("JSON must start with " + OBJECT_START);
		
		JSONObject ret = new JSONObject();
		
		if (json.length() == 2) // there's only 1 possible JSON string that's 2 charactersL {}
			if (json.equals("" + OBJECT_START + OBJECT_END)) // if it is {}...
				return ret; // return an empty JSON object
			else
				throw new IllegalArgumentException("two-character JSON must be " + OBJECT_START + OBJECT_END); // nahp dun care what it is it's WRONG
		//</editor-fold>

		//<editor-fold defaultstate="collapsed" desc="parser objects">
		// if we're parsing a string
//		boolean inString = false;
		// for building strings
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
//		boolean dqString = true; // usually true - unused since nested loops were utilized
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
					current = ++i > l - 1
						? json.charAt(l - 1)
						: json.charAt(i))
			{
				if (isStringStartEnd(current) && prev != STRING_ESCAPE_START)
				{
					char quote = current;
					StringBuilder string = new StringBuilder();
					prev = current;
					current = json.charAt(++i);

					for(;
						i < l;
						prev = current,
							current = ++i > l - 1
								? json.charAt(l - 1)
								: json.charAt(i))
					{
						if (current != quote && current != STRING_ESCAPE_START)
							string.append(current);
						else if (prev != STRING_ESCAPE_START)
							break;
					}
					if (inVal)
						ret.set(key, string);
					else
						key = string;
					if (string.toString().equals("Some value terminators"))
						System.out.print("");
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
				else if (current == KEY_END)
				{
					inVal = true;
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
	 * Uses a Java internal JavaScript engine to hardcodedParse the given JSON string. This may fail if your version of Java
	 * has no such engine. If it does fail, try using {@link #hardcodedParse(java.lang.CharSequence)} instead. Also note that
	 * there is a VERY significant overhead to using this method the first time (up to 1 second) due to javascript engine
	 * initialization.
	 * 
	 * @param json the string containing a valid JSON object to parse. JS comments and whitepace are O.K.
	 * @return the given string, parsed as a {@link JSONObject}
	 * 
	 * @throws InvalidJSONException if the given object is not valid JSON
	 * @throws NoClassDefFoundError if there is no compatible JavaScript engine. If this is thrown, try using
	 *         {@link #hardcodedParse(java.lang.CharSequence)} instead
	 */
	@SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
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
	 * Attempts to use {@link #softcodedParse(java.lang.CharSequence)}. If this fails in a way we might expect, then
	 * {@link #hardcodedParse(java.lang.CharSequence)} is attempted.
	 * 
	 * @param json the string containing a valid JSON object to parse. JS comments and whitepace are O.K.
	 * @return the given string, parsed as a {@link JSONObject}
	 * 
	 * @throws InvalidJSONException if the given object is not valid JSON
	 */
	public static JSONObject tryBothParse(CharSequence json)
	{
		try
		{
			return softcodedParse(json);
		}
		catch (InvalidJSONException | NoClassDefFoundError t)
		{
			return hardcodedParse(json);
		}
	}
	
	
	/**
	 * Attempts to use {@link #softcodedParse(java.lang.CharSequence)}. If this fails in a way we might expect, then
	 * {@link #hardcodedParse(java.lang.CharSequence)} is attempted.
	 * 
	 * @param json the stream to read. It should result in a string containing a valid JSON object to parse. JS
	 *        comments and whitepace are O.K.
	 * @return the given string, parsed as a {@link JSONObject}
	 * 
	 * @throws IOException if the given {@link InputStream} throws an {@link IOException} at any time for any reason.
	 * @see #hardcodedParse(java.lang.CharSequence)
	 */
	public static JSONObject tryBothParse(InputStream json) throws IOException
	{
		return tryBothParse(bringIn(json));
	}
	
	/**
	 * Makes {@link #hardcodedParse(java.io.InputStream)} and {@link #softcodedParse(java.io.InputStream)} easier to write and
	 * maintain by abstracting the input process
	 */
	static String bringIn(InputStream is) throws IOException
	{
		int read;
		ArrayList<Byte> inList = new ArrayList<>();
		while ((read = is.read()) != -1)
			inList.add((byte)read);
		byte[] ints = new byte[inList.size()];
		for(int i = 0; i < ints.length; i++)
			ints[i] = (byte)inList.get(i);
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
			return String.valueOf(mystery.subSequence(1, mystery.length() - 1)); // strip the quotes and return the internal string
		if (BOOLEAN.matcher(mystery).matches())
			return Boolean.valueOf(mystery+"");
		else if (FLOAT.matcher(mystery).matches())
			return Double.valueOf(mystery+"");
		else if (INTEGER.matcher(mystery).matches())
			return Long.valueOf(mystery+"");
		else
			throw new InvalidJSONException("Illegal value (possibly unquoted string?): " + mystery);
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
				current = array.charAt(i < l ? ++i : l - 1))
		{
			/* Note that each of these if statements is only going to look at the first character of the value */
			
			if (current == ARRAY_START) // if this value is a nested array
			{
				CharSequence nestedArray = getInnerString(array, i, ARRAY_START, ARRAY_END); // extract the text of the array
				ret.add(hardcodedParseArray(nestedArray)); // recursively use this method to parse the array and add it
				i += nestedArray.length(); // make sure our counter is placed just after the nested array
				continue;
			}
			else if (current == OBJECT_START) // if this value is an object
			{
				CharSequence innerObject = getInnerString(array, i, OBJECT_START, OBJECT_END); // extract the text of the object
				ret.add(hardcodedParse(innerObject)); // use hardcodedParse to parse the object and add it
				i += innerObject.length(); // make sure our counter is placed just after the object
				continue;
			}
			/*else if (isValueEnd(current)) // if this is the end of a value - not needed since internal loops are used
			{
				ret.add(makePrimitive(val)); // since it wasn't an array or object, it's a primitive
				val = new StringBuilder(); // reset the value builder for the next one
				continue;
			}*/
			else if (isStringStartEnd(current)) // if we're in a string, we want to ignore value enders
			{
				CharSequence innerString = getInnerString(array, i, current); // getInnerString returns the string, but not its quotes
				ret.add(innerString);
				i += innerString.length() + 2; // properly offset i, accounting for the stripped quotes
				val = new StringBuilder();
				continue;
			}
			else // some value that's not an array, object, or string
			{
				for(; i < l; current = array.charAt(++i)) // make a sub-loop mimicing the major one
				{
					if (isValueEnd(current)) // if we're at the end of the value
						break;
					else
						val.append(current);
				}
				
				ret.add(makePrimitive(val));
				val = new StringBuilder();
				continue;
			}
		}
		if (val.length() != 0) // if we've yet to process the last item
			ret.add(makePrimitive(val));
		
		return ret.toArray();
	}
	
	/**
	 * Returns a substring surrounded by the given characters. If another start character is found, its corresponding end
	 * character is ignored.
	 * For example, when given {@code ("[lol, [herp, [derp, qwerp]], smrt, [yay, fun]]", 6, '[', ']')}, it will return
	 * {@code "[herp, [derp, qwerp]]"}.
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
	
	/**
	 * Returns a substring surrounded by the given characters. If another start character is found and is preceded by
	 * {@link #STRING_ESCAPE_START}, it is added and parsing continues.
	 * For example, when given {@code ("[lol, \"\\\"herp\\\" and \\\"derp\\\" aren't words.\", "smrt"]", 6, '"')}, it will
	 * return {@code "\"herp\" and \"derp\" aren't words."}, <STRONG>without</STRONG> the surrounding quotes.
	 * 
	 * @param string the string whose substring is to be extracted
	 * @param startPos the index to start from
	 * @param startEndChar the character that indicates the substring has started or ended
	 * @return the substring starting at {@code startPos} and ending at its corresponding {@code startEndChar}
	 */
	private static CharSequence getInnerString(CharSequence string, int startPos, char startEndChar)
	{
		int length = string.length();
		char prev = string.charAt(startPos), current; // prev == "
		StringBuilder innerString = new StringBuilder("");
		for (
			current = string.charAt(++startPos); // increment so we don't parse or add the "
			startPos < length;
			prev = current,
				current = string.charAt(++startPos))
		{
			if (current == startEndChar && prev != STRING_ESCAPE_START) // break on " but not \"
				break;
			else if (current == STRING_ESCAPE_START && prev != STRING_ESCAPE_START) // append \" as " and \\ as \
				continue;
			innerString.append(current);
		}
		return innerString;
	}
}
