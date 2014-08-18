package org.bh.tools.util.json;

public interface JSONable
{
	public JSONObject toJSONObject();
	public JSONable fromJSONObject(JSONObject jsono);
}
