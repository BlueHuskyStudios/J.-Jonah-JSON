package org.bh.app.filexplorer.comps;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.io.Serializable;
import javax.swing.JFrame;

/**
 * MainFrame, made for BH File Explorer 2014 NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-08-21
 */
public class MainFrame extends JFrame
{

	public MainFrame() throws HeadlessException
	{
		this("", null);
	}

	public MainFrame(GraphicsConfiguration gc)
	{
		this("", gc);
	}

	public MainFrame(String title) throws HeadlessException
	{
		this(title, null);
	}

	public MainFrame(String title, GraphicsConfiguration gc)
	{
		super(title, gc);
	}
}
