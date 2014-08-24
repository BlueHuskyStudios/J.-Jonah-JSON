package org.bh.app.filexplorer.evt;

import bht.tools.comps.event.NavigationEvent;

/**
 * ZeroedNavigationEvent, made for BH File Explorer 2014 NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-21
 */
public class ZeroedNavigationEvent extends NavigationEvent
{

	public ZeroedNavigationEvent(
			Object source,
			Number newPosition,
			Object oldObject,
			Object newObject,
			NavigationState state)
	{
		super(source, 0, newPosition, oldObject, newObject, state);
	}
	
}
