package org.bh.tools.util.json;

import javax.script.ScriptException;

/**
 * InvalidJSONException, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2013<hr/>
 * 
 * Extends RuntimeException so you can compile without throwing or catching, but I recommend you catch it just in case.
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-15
 */
public class InvalidJSONException extends RuntimeException
{
	/**
	 * Constructs an instance of <code>InvalidJSONException</code> with the specified detail message.
	 * @param msg the detail message.
	 */
	public InvalidJSONException(String msg)
	{
		super(msg);
	}

	public InvalidJSONException(String msg, CharSequence json, int position, JSONObject soFar)
	{
		this(msg + "\r\n - got this far: " + json.subSequence(0, position) + "\r\n - this makes the current object: " + soFar);
	}
	public InvalidJSONException(String msg, CharSequence json, int position, JSONObject soFar, Throwable cause)
	{
		this(msg + "\r\n - got this far: " + json.subSequence(0, position) + "\r\n - this makes the current object: " + soFar, cause);
	}

	public InvalidJSONException(Throwable cause)
	{
		super(cause);
	}

	public InvalidJSONException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}