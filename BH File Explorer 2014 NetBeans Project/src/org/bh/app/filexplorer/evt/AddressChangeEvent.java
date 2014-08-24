package org.bh.app.filexplorer.evt;

import bht.tools.comps.event.NavigationEvent;
import bht.tools.comps.event.NavigationEvent.NavigationState;

/**
 * AddressChangeEvent, made for BH File Explorer 2014 NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-21
 */
public class AddressChangeEvent extends NavigationEvent
{

	public AddressChangeEvent(Object source,
							  Number oldPosition,
							  Number newPosition,
							  Object oldObject,
							  Object newObject,
							  NavigationState state)
	{
		super(source, oldPosition, newPosition, oldObject, newObject, state);
	}
	
	public AddressChangeEvent(NavigationEvent basis)
	{
		this(
			basis.getSource(),
			basis.getOldPosition(),
			basis.getNewPosition(),
			basis.getOldObject(),
			basis.getNewObject(),
			basis.getState());
	}
}
