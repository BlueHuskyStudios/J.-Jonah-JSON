package org.bh.tools.util.json;

import java.util.Collection;
import java.util.Set;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Test, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2013
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-11
 */
public class Test
{
	/**
	 * Tests the JSONObject and JSONPair
	 * @param args unused
	 */
	public static void main(String[] args) throws ScriptException
	{
		long start, key1, key2, key3;
		start = System.nanoTime();
		JSONObject test = new JSONObject();
		key1 = System.nanoTime();
		test.set("string", "derp");
		test.set("array", new String[]{"won","too","tree"});
		test.set("number", 5);
		test.set("boolA", true);
		test.set("boolB", false);
		test.set("JSONObject", new JSONObject().set("the butt", "what what"));
		test.set("Empty JSONObject", new JSONObject());
		test.set("myself", test);
		test.set("pair", new JSONPair<>("lol", "welp"));
		key2 = System.nanoTime();
		String testString = test.toString();
		key3 = System.nanoTime();
//		System.out.println(testString);
		System.out.println(
			"Timings: \r\n" +
				"\tCreation: " + ((key1 - start) / 1_000_000.0) + "ms\r\n" + 
				"\tAddition: " + ((key2 - key1) / 1_000_000.0) + "ms\r\n" + 
				"\tStringification: " + ((key3 - key2) / 1_000_000.0) + "ms"
		);
		
		JSONObject jsono;
		
		start = System.nanoTime();
		jsono = JSONParser.hardcodedParse(testString);
		key1 = System.nanoTime();
		
		String hard = jsono.toString();
		
		key2 = System.nanoTime();
		jsono = JSONParser.softcodedParse(testString);
		key3 = System.nanoTime();
		
		String soft = jsono.toString();
		
		
		System.out.println(findDifference(hard, soft));
		
		System.out.println(
			"hard: " + ((key1 - start) / 1_000_000.0) + "ms\r\n" + 
			"soft: " + ((key3 - key2) / 1_000_000.0) + "ms"
		);
	}
	
	public static JSONObject toJSONObject(JSObject o)
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
						? toJSONArray((JSObject)values[i])
						: toJSONObject((JSObject)values[i])
					: values[i]
			);
		}
		return(parsed);
	}
	
	public static Object[] toJSONArray(JSObject array)
	{
		return array.values().toArray();
	}
	
	public static String findDifference(String s1, String s2)
	{
		if (s1.equals(s2))
			return "Perfection!";
		StringBuilder ret = new StringBuilder(s1 + "\r\n" + s2 + "\r\n");
		final int s1Length = s1.length(), s2Length = s2.length();
		char currentS1, currentS2;
		for (int i = 0, l = Math.min(s1Length, s2Length); i < l; i++)
		{
			currentS1 = s1.charAt(i);
			currentS2 = s2.charAt(i);
			if (currentS1 == currentS2)
				ret.append(' ');
			else
				break;
		}
		return ret.append('^').toString();
	}
}
