package org.bh.tools.util.json;

/**
 * Do, made for J. Jonah JSON NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * A class full of tiny-named static methods that act as generalizers or shorten the names of common practices
 * 
 * @author Kyli of Blue Husky Programming
 * @since 2014-08-18
 * @version 1.0.0
 *		- 2014-08-18 (1.0.0) - Kyli Rouge created Do
 */
public class Do
{
	/**
	 * Made because of methods like {@link Long#equals(java.lang.Object)} returning {@code false} for {@code 12L == 12D}. Works
	 * best along with a static import.
	 * <br/>
	 * <br/>
	 * <ul>
	 *	<li>if {@code a == b}, returns true</li>
	 *	<li>if {@code a == null || b == null}, returns false, since null is only ever equal to itself</li>
	 *	<li>if both {@code a} and {@code b} are {@link Number}s, their {@link Number#doubleValue()} returns are compared</li>
	 *	<li>if both {@code a} and {@code b} are {@link Boolean}s, their {@link Boolean#booleanValue()} returns are compared</li>
	 *	<li>if all else fails, {@code a.equals(b)} is returned</li>
	 * </ul>
	 * 
	 * @param a the first object
	 * @param b the second object
	 * @return whether or not they're equal
	 */
	@SuppressWarnings("UnnecessaryUnboxing")
	public static boolean eq(Object a, Object b)
	{
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		
		if (a instanceof Number && b instanceof Number)
			return ((Number)a).doubleValue() == ((Number)b).doubleValue();
		
		if (a instanceof Boolean && b instanceof Boolean)
			return ((Boolean)a).booleanValue() == ((Boolean)b).booleanValue();
		
		return a.equals(b);
	}
	
	/**
	 * A way to safely convert the given object to a string with a tiny method name. Works best along with a static import.
	 * 
	 * @param o the object to stringify
	 * @return the result of passing {@code o} to {@link String#valueOf(Object)}
	 */
	public static String s(Object o)
	{
		return String.valueOf(o);
	}
}
