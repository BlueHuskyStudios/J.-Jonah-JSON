package org.bh.tools.util.json;

import static bht.tools.util.Do.eq;
import static bht.tools.util.Do.s;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * JSONPair, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2013
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.1.0
 * @since 2014-08-11
 *		+ 1.1.1 (2015-01-03) Kyli Rouge added documentation, marked pairs protected
 *		+ 1.1.0 (2014-08-11) Kyli Rouge added hashCode() and equals(Object)
 *		+ 1.0.0 (2014-08-11) Kyli Rouge created JSONObject
 */
public class JSONObject implements JSONable
{
	protected HashMap<String, JSONPair> pairs;
	
	/**
	 * Creates a new JSONObject with an empty set of pairs.
	 */
	public JSONObject()
	{
		pairs = new HashMap<>();
	}
	
	/**
	 * Returns the value of the pair whose name corresponds to the given {@link CharSequence}
	 * @param <T> the type of value the expected pair would contain
	 * @param name the name of the expected pair
	 * @return the given pair, if it exists, else {@code null}
	 */
	public <T> T get(CharSequence name)
	{
		return (T)getPair(s(name)).value;
	}
	
	/**
	 * Returns the pair whose name corresponds to the given {@link CharSequence}
	 * @param <T> the type of value the expected pair would contain
	 * @param name the name of the expected pair
	 * @return the given pair, if it exists, else {@code null}
	 */
	public <T> JSONPair<T> getPair(CharSequence name)
	{
		return pairs.get(s(name));
	}
	
	/**
	 * Adds or puts the given pair to this object
	 * @param <T> the type of value to add
	 * @param name the key for the pair
	 * @param newValue the value for the pair
	 * @return {@code this}
	 */
	@SuppressWarnings("Convert2Diamond") // I feel more secure explicitly stating it here
	public <T> JSONObject set(CharSequence name, T newValue)
	{
		String sName = s(name);
		
		if (pairs.containsKey(sName))
			pairs.get(sName).value = newValue;
		else
			pairs.put(sName, new JSONPair<T>(sName, newValue));
		return this;
	}
	
	/**
	 * Adds the given pair
	 * 
	 * @deprecated {@link #set(CharSequence, Object)} provides a more robust functionality
	 * @param <T> the type of {@link JSONPair} that is provided
	 * @param newPair the pair to add
	 * @return {@code this}
	 */
	public <T> JSONObject add(JSONPair newPair)
	{
		pairs.put(s(newPair.name), newPair);
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
        Iterator<Entry<String,JSONPair>> i = pairs.entrySet().iterator();
        if (!i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<String,JSONPair> e = i.next();
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
		for (String key: pairs.keySet())
		{
			if (!other.pairs.containsKey(key))
				return false;
			if (!eq(
				other.pairs.get(key).value,
				      pairs.get(key).value))
				return false;
		}
		return true;
	}
}
