package org.bh.tools.util.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * JSONParser, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-11
 */
public class JSONParser
{
	public static final Pattern COMMENT_BLOCK;
	public static final Pattern COMMENT_LINE;
	public static final Pattern COMMENT_ANY;
	
	
	static
	{
		COMMENT_BLOCK = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
		COMMENT_LINE = Pattern.compile("//.?$", Pattern.MULTILINE);
		COMMENT_ANY = Pattern.compile(
			"(" + COMMENT_BLOCK.pattern() + ")|(" + COMMENT_LINE.pattern() + ')',
			Pattern.DOTALL | Pattern.MULTILINE);
	}
	
	
	public static JSONObject fromString(CharSequence json)
	{
		json = 
			removeWhitespace(
				removeComments(json)
			)
		;
		if (json.length() < 2)
			throw new IllegalArgumentException("JSON must be at least {}");
		char current = json.charAt(0);
		if (current != '{')
			throw new IllegalArgumentException("JSON must start with {");
		
		JSONObject ret = new JSONObject();
		
		if (json.length() == 2)
			if (json.equals("{}"))
				return ret;
			else
				throw new IllegalArgumentException("two-character JSON must be {}");

		boolean inString = false;
		short arrayDepth = 0, objectDepth = 0;
		char prev = current;
		current = json.charAt(1);
		StringBuilder string = new StringBuilder();
		for(
			int i = 1,
				l = json.length();
			i < l;
			current = json.charAt(++i))
		{
			if ((current == '"' || current == '\'') && prev != '\\')
				if (inString = !inString) // if this is the beginning of a new string
				{
					string = new StringBuilder();
					continue;
				}
				else // if this is the end of a string
				{
					string.append(current);
				}
		}
		return null;
	}

	private static CharSequence removeComments(CharSequence json)
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
	private static CharSequence removeWhitespace(CharSequence json)
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
	
	public static void main(String... args) throws FileNotFoundException
	{
		CharSequence test = "";
		File input = new File("U:\\Libraries\\Programs\\Github\\Open-Dictionary-Project\\concept.json");
		FileInputStream fis = new FileInputStream(input);
		fis.
		Scanner scan = new Scanner(input);
		Pattern everything = Pattern.compile("", Pattern.DOTALL | Pattern.MULTILINE);
		while (scan.hasNext(everything))
			test += scan.next(everything);
		System.out.println(test);
		System.out.println(removeWhitespace(test));
	}
}
