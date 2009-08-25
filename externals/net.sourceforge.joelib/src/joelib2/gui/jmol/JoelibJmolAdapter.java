package joelib2.gui.jmol;

import java.io.BufferedReader;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Properties;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import org.apache.log4j.Category;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolAdapter.Logger;

/**
*An interface between JOELib and JMol. Can be used to visualize JOELib molecules using a JMolPanel.
*
* @.author      jahn
* @.license      GPL
*/
public class JoelibJmolAdapter extends JmolAdapter 
{
	private static Category logger = Category.getInstance(JoelibJmolAdapter.class.getName());
	
	public JoelibJmolAdapter(Logger logger)
	{
		super("JoelibJmolAdapter", logger);
	}


	@Override
	public AtomIterator getAtomIterator(Object clientfile) 
	{
		return new AtomIterator((Molecule)clientfile);
	}



	


	@Override
	public Properties getAtomSetProperties(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return super.getAtomSetProperties(arg0, arg1);
	}

	@Override
	public BondIterator getBondIterator(Object clientfile) 
	{
		return new BondIterator((Molecule)clientfile);
	}
	
	@Override
	public int getEstimatedAtomCount(Object arg0) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Molecule)
			return ((Molecule)arg0).getAtomsSize();
		return 0;
	}


	class AtomIterator extends JmolAdapter.AtomIterator
	{
		Molecule mol;
		MoleculeVector molvec;
		int atomnumber, moleculenumber;
		Atom atom;
		
		AtomIterator(Molecule mol)
		{
			this.mol = mol;
			this.atomnumber = 1;
		}
		
		AtomIterator(MoleculeVector molvec)
		{
			this.molvec = molvec;
			this.atomnumber = 1;
			this.moleculenumber = 0;
		}
		
		public int getAtomSerial()
		{
			return atomnumber;
		}

		@Override
		public Object getUniqueID() 
		{
			return this.atom;
		}
		
		public int getElementNumber()
		{
			return atom.getAtomicNumber();
		}
		
		@Override
		public float getX() 
		{
			return (float)this.atom.get3Dx();
		}

		@Override
		public float getY() 
		{
			return (float)this.atom.get3Dy();
		}

		@Override
		public float getZ() 
		{
			return (float)this.atom.get3Dz();
		}

		@Override
		public boolean hasNext() 
		{
			if(this.mol != null)
			{
				if(this.atomnumber == mol.getAtomsSize()+1)
					return false;
				this.atom = mol.getAtom(atomnumber++);
				return true;
			}
			if(this.molvec.getMol(this.moleculenumber).getAtomsSize()+1 == this.atomnumber)
			{
				if(this.molvec.getSize() == this.moleculenumber+1)
					return false;
				this.atom = molvec.getMol(this.moleculenumber++).getAtom(atomnumber);
				this.atomnumber = 1;
				return true;
			}
			this.atom = molvec.getMol(this.moleculenumber).getAtom(atomnumber++);
			return true;
		}

		@Override
		public String getAtomName() 
		{
			return this.atom.getType();
		}


		@Override
		public String getElementSymbol() 
		{
			return atom.getType();
		}

		@Override
		public int getFormalCharge() 
		{
			return this.atom.getFormalCharge();
		}

	}
	
	class BondIterator extends JmolAdapter.BondIterator
	{
		Molecule mol;
		MoleculeVector molvec;
		Bond bond;
		int bondcount, moleculecount;
		
		BondIterator(Molecule mol)
		{
			this.mol = mol;
			this.bondcount = 0;
		}
		
		BondIterator(MoleculeVector molvec)
		{
			this.molvec = molvec;
			this.bondcount = 0;
			this.moleculecount = 0;
		}
	
		@Override
		public Object getAtomUniqueID1() 
		{
			return this.bond.getBegin();
		}

		@Override
		public Object getAtomUniqueID2() 
		{
			return this.bond.getEnd();
		}

		@Override
		public int getEncodedOrder() 
		{
			return this.bond.getBondOrder();
		}

		@Override
		public boolean hasNext() 
		{
			if(this.mol != null)
			{
				if(bondcount == this.mol.getBondsSize())
					return false;
				this.bond = this.mol.getBond(bondcount++);
				return true;
			}
			else
			{
				if(molvec.getMol(this.moleculecount).getBondsSize() == this.bondcount)
				{
					if(molvec.getSize() == this.moleculecount+1)
						return false;
					this.bond = this.molvec.getMol(this.moleculecount++).getBond(this.bondcount);
					this.bondcount = 0;
					return true;
				}
				this.bond = this.molvec.getMol(this.moleculecount).getBond(this.bondcount++);
				return true;
			}
		}
	}
}

