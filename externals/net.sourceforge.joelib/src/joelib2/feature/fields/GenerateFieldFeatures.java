package joelib2.feature.fields;

import java.io.BufferedWriter;
import java.io.FileWriter;

import joelib2.feature.fields.potentials.Coulomb;
import joelib2.io.BasicReader;
import joelib2.molecule.BasicConformerMolecule;

public class GenerateFieldFeatures {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception 
	{
		double resolution = 0.5;
		SimpleESField sf = new SimpleESField(resolution, new Coulomb(1.0));
		BasicReader reader = new BasicReader(args[0]);
		BasicConformerMolecule mol = new BasicConformerMolecule();
		BufferedWriter w = new BufferedWriter(new FileWriter(args[1]));
		String label = args[2];
		while(reader.readNext(mol))
		{
			w.write(mol.getData(label).getKeyValue().toString() + "");
			int x = 1;
			SimpleField field = sf.getField(mol);
			float[][][] t = field.vals;
			for(int i = 0; i < t.length; i++)
			{
				for(int j = 0; j < t[i].length; j++)
				{
					for(int k = 0; k < t[i][j].length; k++)
					{
						System.out.print(t[i][j][k] + " ");
						w.write(" " + x+":"+t[i][j][k]);
						x++;
					}
					
					System.out.println();
				}
				System.out.println("*** "+i+" ****");
			}
			w.write("\n");
			mol = new BasicConformerMolecule();
		}
		//System.out.println(field.minPot + "\t" + field.maxPot);
	}

}
