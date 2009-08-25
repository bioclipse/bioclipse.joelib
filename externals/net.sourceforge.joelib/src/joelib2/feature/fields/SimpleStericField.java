package joelib2.feature.fields;

import javax.swing.plaf.basic.BasicTextUI.BasicCaret;
import javax.vecmath.Point3f;

import joelib2.feature.fields.potentials.Identity;
import joelib2.feature.fields.potentials.LennardJones;
import joelib2.feature.fields.potentials.StericPotential;
import joelib2.io.BasicReader;
import joelib2.molecule.BasicConformerAtom;
import joelib2.molecule.BasicConformerMolecule;

public class SimpleStericField 
{
	double resolution;
	StericPotential potential;
	double minPot = Double.POSITIVE_INFINITY;
	double maxPot = Double.NEGATIVE_INFINITY;
	public SimpleStericField(double resolution, StericPotential potential)
	{
		this.resolution = resolution;
		this.potential = potential;
	}
	
	public SimpleField getField(BasicConformerMolecule mol)
	{
		Point3f[][][] grid = FieldHelper.getGrid(mol, resolution);
		float[][][] t = new float[grid.length][grid[0].length][grid[0][0].length];
		for(int i = 0; i < t.length; i++)
		{
			for(int j = 0; j < t[i].length; j++)
			{
				for(int k = 0; k < t[i][j].length; k++)
				{
					Point3f point = grid[i][j][k];
					int n = mol.getAtomsSize();
					double sum = 0.0;
					for(int l = 1; l <= n; l++)
					{
						BasicConformerAtom atom = (BasicConformerAtom)mol.getAtom(l);
						double[] c = atom.getCoords3Darr();
					
						Point3f temp = new Point3f((float)atom.get3Dx(),(float)atom.get3Dy(),(float)atom.get3Dz());
						float dist = point.distance(temp);
						double pot = potential.getStericPotential(dist);
						if(dist < 1.0)System.out.println("Atom: " + l + " Grid: " + temp.toString() + " --> distance = " + dist + " potential = " +pot);

						sum += pot;
					}
					if(sum < minPot)
					{
						minPot = sum;
						System.out.println("new minimum: " + sum);
					}
					if(sum > maxPot)
					{
						maxPot = sum;
						System.out.println("new maximum: " + sum);
					}
					if(sum > 60.0) sum = 60.0;
					t[i][j][k] = (float)sum;
				}
			}
		}
		return new SimpleField(grid,t,minPot,60.0);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		double resolution = 0.2;
		SimpleStericField sf = new SimpleStericField(resolution, new LennardJones(1.0,1.0,4.0,6.0));
		//SimpleStericField sf = new SimpleStericField(resolution, new Identity());
		
		BasicReader reader = new BasicReader(args[0]);
		BasicConformerMolecule mol = new BasicConformerMolecule();
		reader.readNext(mol);
		SimpleField field = sf.getField(mol);
		float[][][] t = field.vals;
		for(int i = 0; i < t.length; i++)
		{
			for(int j = 0; j < t[i].length; j++)
			{
				for(int k = 0; k < t[i][j].length; k++)
				{
//					System.out.print(t[i][j][k] + " ");
					if(t[i][j][k] > 0.0)
					{
						System.out.println("grid("+field.grid[i][j][k].toString()+") = " + t[i][j][k]);
					}
				}
//				System.out.println();
			}
//			System.out.println("*** "+i+" ****");
		}
		//new SimpleFieldViewer(FieldHelper.getGrid(mol, resolution), t);
		System.out.println(field.minPot + "\t" + field.maxPot);
		JMolFieldViewer viewer = new JMolFieldViewer();
		viewer.draw(mol, field);
		
	}

}
