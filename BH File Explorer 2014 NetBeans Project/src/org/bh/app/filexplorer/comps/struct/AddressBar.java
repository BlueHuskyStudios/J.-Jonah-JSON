package org.bh.app.filexplorer.comps.struct;

import bht.tools.comps.event.NavigationEvent.NavigationState;
import bht.tools.util.ArrayPP;
import java.io.File;
import javax.swing.text.JTextComponent;
import org.bh.app.filexplorer.evt.AddressBarListener;
import org.bh.app.filexplorer.evt.AddressChangeEvent;

public abstract class AddressBar extends JTextComponent
{
	private File currentFile;
	private ArrayPP<AddressBarListener> addressBarListeners = new ArrayPP<AddressBarListener>();

	public AddressBar(File initCurrentFile)
	{
		currentFile = initCurrentFile;
	}

	public File getCurrentFile()
	{
		return currentFile;
	}

	public void setCurrentFile(File newCurrentFile)
	{
		AddressChangeEvent evt =
			new AddressChangeEvent(
					this,
					0,
					1,
					currentFile,
					newCurrentFile,
					NavigationState.STATE_GOING_FORWARD);
		currentFile = newCurrentFile;
	}
}
