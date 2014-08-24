package org.bh.app.filexplorer.evt;

import java.util.EventListener;

public interface AddressBarListener extends EventListener
{
	public void willChangeAddress(AddressChangeEvent evt);
	public void didChangeAddress(AddressChangeEvent evt);
}
