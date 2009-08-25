package joelib2.gui.jmol;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;



import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;

/**
*An simple visualization JPanel for JOELib2 using JMol
*Example (Draw Molecule mol):  "panel.getViewer().openClientFile(null, null, mol);"
*
* @.author      jahn, nhfechner
* @.license      GPL
*/
public class Joelib3DPanel extends JPanel
{
	JmolViewer viewer;
	JmolAdapter adapter;
	
	public Joelib3DPanel()
	{
		adapter = new JoelibJmolAdapter(null);
		viewer = viewer.allocateViewer(this, adapter);
	}
		
	final Dimension currentSize = new Dimension();
	final Rectangle rectClip = new Rectangle();
		
	public void paint(Graphics g)
	{
		viewer.setScreenDimension(getSize(currentSize));
		g.getClipBounds(rectClip);
		viewer.renderScreenImage(g, currentSize, rectClip);
	}
	
	public JmolViewer getViewer()
	{
		return viewer;
	}
}

