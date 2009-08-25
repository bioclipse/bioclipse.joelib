package joelib2.feature.fields;

import java.awt.Color;

import javax.vecmath.Point3f;

import joelib2.gui.render3D.molecule.ViewerMolecule;
import joelib2.molecule.BasicConformerAtom;
import joelib2.molecule.BasicConformerMolecule;

public class FieldHelper 
{
	//public static final float energy_cutoff = 0.000001f;
	public static int[] getColorForEnergy(float energy, double min, double max)
	{
		//if(energy < 0.0f) energy = 0.0f;
		float t = energy - (float)min;
		t = t /(float)(max-min);
		Color c = interpolateColor(Color.blue, Color.red, t);
		return new int[]{c.getRed(),c.getGreen(),c.getBlue()};
	}
	
	private static Color interpolateColor(Color c1, Color c2, float x) {
//        if (x <= 0.0f) {
//          return c1;
//        } else if (x >= 1.0f) {
//          return c2;
//        } else {
          int r = (int) ((1.0f - x) * c1.getRed() + x * c2.getRed());
          int g = (int) ((1.0f - x) * c1.getGreen() + x * c2.getGreen());
          int b = (int) ((1.0f - x) * c1.getBlue() + x * c2.getBlue());

          return new Color(r, g, b);
//        }
      }
	
	public static double[] getBox(BasicConformerMolecule mol)
	{
		double xmin = Double.POSITIVE_INFINITY,
		xmax = Double.NEGATIVE_INFINITY,
		ymin = Double.POSITIVE_INFINITY,
		ymax = Double.NEGATIVE_INFINITY,
		zmin= Double.POSITIVE_INFINITY,
		zmax= Double.NEGATIVE_INFINITY;
		for(int i = 1; i <= mol.getAtomsSize(); i++)
		{
			BasicConformerAtom atom = (BasicConformerAtom)mol.getAtom(i);
			double x  = atom.get3Dx();
			if(x < xmin) xmin = x;
			if(x > xmax) xmax = x;
			double y  = atom.get3Dy();
			if(y < ymin) ymin = y;
			if(y > ymax) ymax = y;
			double z  = atom.get3Dz();
			if(z < zmin) zmin = z;
			if(z > zmax) zmax = z;
		}
		return new double[]{xmin-2.0,xmax+2.0,ymin-2.0,ymax+2.0,zmin-2.0,zmax+2.0};
	}
	
	public static Point3f[][][] getGrid(BasicConformerMolecule mol, double resolution)
	{
//		ViewerMolecule vmol = new ViewerMolecule(mol);
//		vmol.findBB();
//		System.out.println(vmol.getName());
//		double xmin = vmol.getXmin();
//		double xmax = vmol.getXmax();
//		double ymin = vmol.getYmin();
//		double ymax = vmol.getYmax();
//		double zmin = vmol.getZmin();
//		double zmax = vmol.getZmax();
		double[] temp = getBox(mol);
		double xmin = temp[0];
		double xmax = temp[1];
		double ymin = temp[2];
		double ymax = temp[3];
		double zmin = temp[4];
		double zmax = temp[5];
		int x = (int)Math.ceil((xmax-xmin)/resolution);
		int y = (int)Math.ceil((ymax-ymin)/resolution);
		int z = (int)Math.ceil((zmax-zmin)/resolution);
		Point3f[][][] grid = new Point3f[x][y][z];
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				for(int k = 0; k < z; k++)
				{
					float tx = (float)(xmin + (double)i*resolution);
					float ty = (float)(ymin + (double)j*resolution);
					float tz = (float)(zmin + (double)k*resolution);
					grid[i][j][k] = new Point3f(tx,ty,tz);
				}
			}
		}
		System.out.println("Grid("+x+","+y+","+z+") initialized");
		return grid;
	}
}
