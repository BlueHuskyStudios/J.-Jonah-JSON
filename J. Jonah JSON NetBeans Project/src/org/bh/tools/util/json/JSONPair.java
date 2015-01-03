package org.bh.tools.util.json;

import static bht.tools.util.Do.eq;
import static bht.tools.util.Do.s2;
import java.util.Objects;

/**
 * JSONPair, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2013
 * 
 * @param <T> The type of object
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.1.0
 * @since 2014-08-11
 *		- 1.1.0 (2014-08-18) Kyli Rouge added hashCode() and equals(Object)
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
			// ^ This is a single-character String instead of a char to force a toString on name
			s2(value);
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 11 * hash + Objects.hashCode(name);
		hash = 11 * hash + Objects.hashCode(value);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JSONPair<?> other = (JSONPair<?>) obj;
		if (!eq(name, other.name))
			return false;
		if (!eq(value, other.value))
			return false;
		return true;
	}
}
