package org.bh.app.filexplorer.util;

import bht.tools.comps.event.NavigationEvent;
import bht.tools.comps.event.NavigationEvent.NavigationState;
import static bht.tools.comps.event.NavigationEvent.NavigationState.STATE_GOING_BACKWARD;
import static bht.tools.comps.event.NavigationEvent.NavigationState.STATE_GOING_FORWARD;
import static bht.tools.comps.event.NavigationEvent.NavigationState.STATE_SKIPPING_FORWARD;
import bht.tools.comps.event.NavigationListener;
import bht.tools.util.ArrayPP;
import bht.tools.util.HistoryArray;
import org.bh.app.filexplorer.evt.ZeroedNavigationEvent;

/**
 * ZeroedHistoryArray, made for BH File Explorer 2014 NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * An extension of {@link HistoryArray} that keeps the current position at 0 and sets and gets positions as positive or
 * negative centered around that. For instance, to navigate back 3, simply call {@link #goTo(int)} and pass it {@code -3}
 * 
 * @param <T> the type of object in this array
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-21
 */
public class ZeroedHistoryArray<T> extends HistoryArray<T>
{
	private ArrayPP<NavigationListener> navigationListeners = new ArrayPP<>();
	protected int here;

	/**
	 * Creates a new <tt>HistoryArray</tt> with a blank <tt>ArrayPP</tt> with the first item as the currently observed item.
	 */
	public ZeroedHistoryArray()
	{
		this(new ArrayPP<T>(), 0);
	}

	/**
	 * Creates a new <tt>HistoryArray</tt> with <tt>initArray</tt> as the basis for the history array.
	 *
	 * @param initArray The array of objects to make into a navigable history array. May be empty or null.
	 */
	@SuppressWarnings("unchecked")
	public ZeroedHistoryArray(T... initArray)
	{
		this(initArray, initArray.length - 1);
	}

	/**
	 * Creates a new <tt>HistoryArray</tt> with <tt>initArray</tt> as the basis for the history array.
	 *
	 * @param initArray The array of objects to make into a navigable history array. May be empty or null.
	 */
	@SuppressWarnings("unchecked")
	public ZeroedHistoryArray(ArrayPP<T> initArray)
	{
		this(initArray.toArray());
	}

	/**
	 * Creates a new <tt>HistoryArray</tt> with <tt>initArray</tt> as the basis for the history array.
	 *
	 * @param initArray The array of objects to make into a navigable history array. May be empty or null.
	 * @param startIndex The item that is used as the "Current" item
	 */
	@SuppressWarnings("unchecked")
	public ZeroedHistoryArray(T[] initArray, int startIndex)
	{
		this(new ArrayPP(initArray), startIndex);
	}

	/**
	 * Creates a new <tt>HistoryArray</tt> with <tt>initArray</tt> as the basis for the history array.
	 *
	 * @param initArray The array of objects to make into a navigable history array. May be empty or null.
	 * @param startIndex The item that is used as the "Current" item
	 */
	@SuppressWarnings("unchecked")
	public ZeroedHistoryArray(ArrayPP<T> initArray, int startIndex)
	{
		t = initArray.toArray();
		here = startIndex;
	}

	/**
	 * Appends the given item to the end of the array, without affecting anything else.
	 *
	 * @param item the item to make the new end
	 * @return the resulting HistoryArray
	 */
	@Override
	public ZeroedHistoryArray<T> append(T item)
	{
		super.add(item);
		return this;
	}

	/**
	 * Trims the array to the current position, appends the given item to the end, and sets the current position to the end (in
	 * that order)
	 *
	 * @param items the item to make the new end
	 * @return the resulting HistoryArray
	 */
	@Override
	public ZeroedHistoryArray<T> add(T... items)
	{
		T current = getCurrent();
		ZeroedNavigationEvent evt =
			new ZeroedNavigationEvent(
				this,
				0,
				current,
				current,
				STATE_SKIPPING_FORWARD
			)
		;

		for (NavigationListener navigationListener : navigationListeners)
			navigationListener.willNavigate(evt);

		trimTo(here);
		super.add(items);
		here = length() - 1;

		for (NavigationListener navigationListener : navigationListeners)
			navigationListener.didNavigate(evt);
		return this;
	}

	@Override
	public ZeroedHistoryArray<T> goNext()
	{
		ZeroedNavigationEvent evt =
			new ZeroedNavigationEvent(
					this,
					1,
					getCurrent(),
					getVirtualNext(),
					STATE_GOING_FORWARD);

		for (NavigationListener navigationListener : navigationListeners)
			navigationListener.willNavigate(evt);

		if (canGoNext())
			here++;
		else
			throw new IllegalStateException("Already at end");

		for (NavigationListener navigationListener : navigationListeners)
			navigationListener.didNavigate(evt);

		return this;
	}

	@Override
	public HistoryArray<T> goBack()
	{
		ZeroedNavigationEvent evt =
			new ZeroedNavigationEvent(
					this,
					-1,
					getCurrent(),
					getVirtualBack(),
					STATE_GOING_BACKWARD);

		for (NavigationListener navigationListener : navigationListeners)
			navigationListener.willNavigate(evt);

		if (canGoBack())
			here--;
		else
			throw new IllegalStateException("Already at end");

		for (NavigationListener navigationListener : navigationListeners)
			navigationListener.didNavigate(evt);
		return this;
	}

	@Override
	public boolean canGoNext()
	{
		return canGoTo(1);
	}

	@Override
	public boolean canGoBack()
	{
		return canGoTo(-1);
	}

	@Override
	public boolean canGoTo(int index)
	{
		return (index += here) >= 0 && index < length();
	}

	/**
	 * @deprecated always returns {@code 0}
	 * always returns {@code 0}
	 * @return {@code 0}
	 */
	@Override
	public int getCurrentIndex()
	{
		return 0;
	}

	
	@Override
	public HistoryArray<T> goTo(int loc)
	{
		NavigationEvent evt =
			new ZeroedNavigationEvent(
					this,
					loc,
					getCurrent(),
					get(loc),
					loc > 0
						? NavigationState.STATE_SKIPPING_FORWARD
						: NavigationState.STATE_SKIPPING_BACKWARD
			);

		for (NavigationListener navigationListener : navigationListeners)
			navigationListener.willNavigate(evt);

		this.here = loc;

		for (NavigationListener navigationListener : navigationListeners)
			navigationListener.didNavigate(evt);
		
		return this;
	}

	/*@Override
	public T get(int position)
	{
		return super.get(here + position);
	}*/

	@Override
	public T getCurrent()
	{
		return isEmpty() ? null : get(0);
	}

	/*@Override - good enough
	public ArrayPP<T> getBackHist()
	{
		return super.subSet(0, here - 1);
	}

	@Override
	public ArrayPP<T> getNextHist()
	{
		return super.subSet(here + 1, length() - 1);
	}

	public ArrayPP<T> getFullHist()
	{
		return this;
	}*/

	/**
	 * Removes everything after {@code index}. Does not destroy the removed items.
	 *
	 * @param position the position of the last item to keep
	 * @return the resulting {@code this}
	 */
	@Override
	public HistoryArray<T> trimTo(int position)
	{
		return trimTo(position, false);
	}

	/**
	 * Removes everything after {@code index}, destroying them if specified
	 *
	 * @param position the position of the last item to keep
	 * @param destroy if {@code true}, the trimmed items will be destroyed (set to null) beforeCount removal
	 * @return the resulting {@code this}
	 */
	@Override
	public HistoryArray<T> trimTo(int position, boolean destroy)
	{
		position += here;
		if (destroy)
			for (int i = position + 1;
				 i < t.length;
				 i++)
				t[i] = null;
		t = super.subSet(0, position).toArray();
		return this;
	}

	@Override
	public HistoryArray<T> trimToCurrent()
	{
		return trimTo(0);
	}

	/**
	 * @deprecated use {@link get(int)} with a negative number instead
	 * 
	 * @param index the index of the item in the back history
	 * @return {@code this}
	 */
	@Override
	public T getFromBackHist(int index)
	{
		return getBackHist().get(index);
	}

	/**
	 * @deprecated use {@link get(int)} with a positive number instead
	 * 
	 * @param index the index of the item in the back history
	 * @return {@code this}
	 */
	@Override
	public T getFromNextHist(int index)
	{
		return getNextHist().get(index);
	}

	/**
	 * Returns the value of the item which would be "Current" if the {@link #goBack()} method is called
	 *
	 * @return the value of the item which would be "Current" if the {@link #goBack()} method is called
	 * @since May 26, 2012 for File Browser
	 */
	@Override
	public T getVirtualBack()
	{
		return get(-1);
	}

	/**
	 * Returns the value of the item which would be "Current" if the {@link #goNext()} method is called
	 *
	 * @return the value of the item which would be "Current" if the {@link #goNext()} method is called
	 * @since May 26, 2012 for File Browser
	 */
	public T getVirtualNext()
	{
		return get(Math.min(length() - 1, getCurrentIndex() + 1));
	}

	public void addNavigationListener(NavigationListener navigationListener)
	{
		navigationListeners.add(navigationListener);
	}

	public void removeNavigationListener(NavigationListener navigationListener)
	{
		navigationListeners.remove(navigationListener, true);
	}


	/**
	 * Returns the object at position {@code position}
	 *
	 * @param position the position of the object to be gotten (where {@code 0} is the current value)
	 * @return the object at position {@code position}
	 * @throws ArrayIndexOutOfBoundsException if {@code position} is less than {@link #beforeCount()} or greater than
	 *         {@link #afterCount()}
	 * @version 1.1.0
	 */
	@Override
	public T get(int position)
	{
		return super.get(here + position); //To change body of generated methods, choose Tools | Templates.
	}
	
	/**
	 * Returns the number of items <em>before</em> the current position
	 * @return the number of items <em>before</em> the current position
	 */
	public int beforeCount()
	{
		return here;
	}
	
	/**
	 * Returns the number of items <em>after</em> the current position
	 * @return the number of items <em>after</em> the current position
	 */
	public int afterCount()
	{
		return t.length - here;
	}
	
	public static void main(String[] args)
	{
		ZeroedHistoryArray<Character> zha = new ZeroedHistoryArray<>();
		zha.add('a','b','c','d','e','f');
		System.out.println(zha);
	}
}
