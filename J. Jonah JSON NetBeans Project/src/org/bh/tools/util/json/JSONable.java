package org.bh.tools.util.json;

/**
 * Indicates a class can be saved to and loaded from a {@link JSONObject}.
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 *		- 2014-08-12 (1.0.0) - Kyli created JSONable
 * @since 2014-08-12
 */
public interface JSONable
{
	/**
	 * Returns a {@link JSONObject} version of this object. The returned value should be passable to
	 * {@link #fromJSONObject(JSONObject)}.
	 * 
	 * @return a {@link JSONObject} version of this object
	 * 
	 * @author Kyli Rouge
	 * @version 1.0.0
	 *		- 2014-08-12 (1.0.0; 1.0.0) - Kyli created toJSONObject
	 * @since 2014-08-12
	 */
	public JSONObject toJSONObject();
	
	/**
	 * Creates a {@link JSONObject} version of this object. If the given object has pairs which are not relevant to this
	 * object, they should be ignored. If it is missing pairs that are relevant to this object, their absence should be ignored
	 * and the corresponding value in this object shouldn't change.
	 * 
	 * @param jsono the {@link JSONObject} whose pairs' values will fill this object
	 * @return this object
	 * 
	 * @author Kyli Rouge
	 * @version 1.0.0
	 *		- 2014-08-12 (1.0.0; 1.0.0) - Kyli created toJSONObject
	 * @since 2014-08-12
	 */
	public JSONable fromJSONObject(JSONObject jsono);
}
