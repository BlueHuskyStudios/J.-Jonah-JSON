package org.bh.tools.util.json;

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
	public static void main(String[] args)
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
		System.out.println(test);
		key3 = System.nanoTime();
		System.out.println(
			"Timings: \r\n" +
				"\tCreation: " + ((key1 - start) / 1_000_000.0) + "ms\r\n" + 
				"\tAddition: " + ((key2 - key1) / 1_000_000.0) + "ms\r\n" + 
				"\tStringification: " + ((key3 - key2) / 1_000_000.0) + "ms"
		);
	}
}
