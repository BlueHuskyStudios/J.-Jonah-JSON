package org.bh.tools.util.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * JSONPair, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2013
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-11
 *		- 1.0.0 (2014-08-11) Kyli Rouge created JSONObject
 */
public class JSONObject implements JSONable
{
	HashMap<CharSequence, JSONPair> pairs;
	
	public JSONObject()
	{
		pairs = new HashMap<>();
	}
	
	public <T> T get(CharSequence name)
	{
		return (T)getPair(name).value;
	}
	public <T> JSONPair<T> getPair(CharSequence name)
	{
		return pairs.get(name);
	}
	
	@SuppressWarnings("Convert2Diamond") // I feel more secure explicitly stating it here
	public <T> JSONObject set(CharSequence name, T newValue)
	{
		if (pairs.containsKey(name))
			pairs.get(name).value = newValue;
		else
			pairs.put(name, new JSONPair<T>(name, newValue));
		return this;
	}
	
	public <T> JSONObject add(JSONPair newPair)
	{
		pairs.put(newPair.name, newPair);
		return this;
	}

	@Override
	public JSONObject toJSONObject()
	{
		return this;
	}

	@Override
	public String toString()
	{
        Iterator<Entry<CharSequence,JSONPair>> i = pairs.entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<CharSequence,JSONPair> e = i.next();
            JSONPair pair = e.getValue();
            sb.append(pair.value == this ? "\"" + pair.name + "\":\"this JSON object\"" : pair);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',');
        }
	}

	/**
	 * Adds all new values in the given object to this one, and replaces any existing values.
	 * 
	 * @param jsono the object containing all the new values.
	 * @return this
	 */
	@Override
	public JSONable fromJSONObject(JSONObject jsono)
	{
		for (JSONPair pair : jsono.pairs.values())
			add(pair);
		return this;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 89 * hash + Objects.hashCode(this.pairs);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (!(obj instanceof JSONObject))
			return false;
		final JSONObject other = (JSONObject) obj;
		if (pairs == other.pairs)
			return true;
		for (CharSequence key: pairs.keySet())
		{
			if (!other.pairs.containsKey(key))
				return false;
			if (! other.pairs.get(key).value
				.equals(pairs.get(key).value))
				return false;
		}
		return true;
	}
}
