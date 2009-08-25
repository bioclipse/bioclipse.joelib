package joelib2.feature.fields;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.vecmath.Point3f;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.gui.jmol.*;

public class JMolFieldViewer extends JFrame 
{
	Joelib3DPanel panel;
	public JMolFieldViewer()
	{
		panel = new Joelib3DPanel();
	}
	
	public void draw(BasicConformerMolecule mol, SimpleField field)
	{
		this.getContentPane().add(panel);
		panel.getViewer().openClientFile(null, null, mol);
//		panel.getViewer().evalString("draw line" + i + " (atomno=" + lines[i][0] + ")(atomno="+lines[i][1] + "); " + color);	// Scriptbefehl fuer die einzelnen Kanten
		Point3f[][][] grid = field.grid;
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				for(int k = 0; k < grid[i][j].length; k++)
				{
					//if(field.vals[i][j][k] < -0.01)
					if(field.vals[i][j][k] > 0.5)
						panel.getViewer().evalString("draw point_" + i + "_" + j + "_"+k  +"{"+grid[i][j][k].x+","+grid[i][j][k].y+","+grid[i][j][k].z+"}" +"; color $point_" + i + "_" + j + "_"+k +" " + Arrays.toString(FieldHelper.getColorForEnergy(field.vals[i][j][k],field.minPot, field.maxPot)));	// Scriptbefehl fuer die einzelnen Kanten
		
				}
			}
		}
		setSize(500, 500);
		setVisible(true);
//		panel.getViewer().
	}
}
