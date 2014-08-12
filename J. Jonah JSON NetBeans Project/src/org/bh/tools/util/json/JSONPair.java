package org.bh.tools.util.json;

import java.util.Arrays;

/**
 * JSONPair, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2013
 * 
 * @param <T> The type of object
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-11
 *		- 1.0.0 (2014-08-11) Kyli Rouge created JSONObject
 */
public class JSONPair<T>
{
	CharSequence name;
	T value;

	public JSONPair(CharSequence initName, T initValue)
	{
		name = initName;
		value = initValue;
	}

	public CharSequence getName()
	{
		return name;
	}

	public void setName(CharSequence newName)
	{
		name = newName;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T newValue)
	{
		value = newValue;
	}

	/**
	 * Creates a string representation of this pair and returns it. It's formatted as such:
	 * 
	 * <OL>
	 *	<LI>The name in the pair, surrounded by double quotes ({@code ""})</LI>
	 *	<LI>A colon ({@code ':'})</LI>
	 *	<LI>the return of {@link #stringify(java.lang.Object)} being passed the value</LI>
	 * </OL>
	 * 
	 * @return 
	 */
	@Override
	public String toString()
	{
		return "\"" + name + "\":" +
			// ^ This is a single-character String instead of a char to force a toString on name and value
			stringify(value);
	}
	
	
	
	/**
	 * 
	 *	<UL>
	 *		<LI>If the value is an array, all elements are recursively stringified, separated by commas ({@code ,}), and
	 *			surrounded by square brackets ({@code []})</LI>
	 *		<LI>Else, if the value is a CharSequence, the value is surrounded by quotes ({@code ""})</LI>
	 *		<LI>Else, the value is passed through {@link String#valueOf(java.lang.Object)}</LI>
	 *	</UL>
	 * @param o
	 * @return 
	 */
	public static String stringify(Object o)
	{
		if (o instanceof Object[])
		{
			StringBuilder sb = new StringBuilder("[");
			for(int i = 0, l = ((Object[])o).length; i < l; i++)
			{
				sb.append(stringify(((Object[])o)[i]));
				if (i != l - 1)
					sb.append(',');
			}
			return sb.append(']').toString();
		}
		if (o instanceof CharSequence)
			return "\"" + o + '\"';
		if (o instanceof JSONPair)
			return "{" + o + "}";
		return String.valueOf(o);
	}
}
