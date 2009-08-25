///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicResidue.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:37 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation version 2 of the License.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.molecule.types;

import joelib2.molecule.Atom;

import joelib2.util.iterator.BasicAtomIterator;

import java.util.List;


/**
 * Residue informations.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:37 $
 */
public class BasicResidue implements java.io.Serializable, Residue
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    protected List<String> atomIndices;
    protected List<Atom> atoms;
    protected String chain;
    protected int chainNumber;
    protected List<boolean[]> heteroAtoms;
    protected int index;
    protected String name;
    protected int number;
    protected List<int[]> serialNumbers;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicResidue()
    {
        chainNumber = 0;
        number = 0;
        name = "";
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static BasicResidue clone(BasicResidue src, BasicResidue to)
    {
        to.chainNumber = src.chainNumber;
        to.number = src.number;
        to.name = src.name;
        to.atomIndices = src.atomIndices;
        to.heteroAtoms = src.heteroAtoms;
        to.serialNumbers = src.serialNumbers;

        return to;
    }

    public void addAtom(Atom atom)
    {
        if (atom != null)
        {
            atom.setResidue(this);

            atoms.add(atom);
            atomIndices.add("");
            heteroAtoms.add(new boolean[]{false});
            serialNumbers.add(new int[]{0});
        }
    }

    /**
     * Gets an iterator over all atoms in this residue.
     *
     * @return   the atom iterator for this residue
     */
    public BasicAtomIterator atomIterator()
    {
        return new BasicAtomIterator(atoms);
    }

    public void clear()
    {
        for (int i = 0; i < atoms.size(); i++)
        {
            ((Atom) atoms.get(i)).setResidue(null);
        }

        chainNumber = 0;
        number = 0;
        name = "";

        atoms.clear();
        atomIndices.clear();
        heteroAtoms.clear();
        serialNumbers.clear();
    }

    //copy residue information
    public Object clone()
    {
        return (new BasicResidue());
    }

    public BasicResidue clone(BasicResidue to)
    {
        return clone(this, to);
    }

    public String getAtomID(Atom atom)
    {
        return (String) (atomIndices.get(getIndex(atom)));
    }

    public String getChain()
    {
        return (chain);
    }

    public int getChainNumber()
    {
        return (chainNumber);
    }

    public int getIndex()
    {
        return (index);
    }

    public String getName()
    {
        return (name);
    }

    public int getNumber()
    {
        return (number);
    }

    public int getSerialNumber(Atom atom)
    {
        return ((int[]) serialNumbers.get(getIndex(atom)))[0];
    }

    public boolean isHeteroAtom(Atom atom)
    {
        return ((boolean[]) heteroAtoms.get(getIndex(atom)))[0];
    }

    //  public void insertAtom(Atom atom)
    //  {
    //    if (atom != null)
    //    {
    //        atom.setResidue(this);
    //
    //        _atoms.add(atom);
    //        _atomid.ensureCapacity(_atoms.size());
    //        _hetatm.ensureCapacity(_atoms.size());
    //        _sernum.ensureCapacity(_atoms.size());
    //   _atomid.add("");
    //   _hetatm.add(new boolean[]{false});
    //   _sernum.add(new int[]{0});
    //    }
    //  }
    public void removeAtom(Atom atom)
    {
        if (atom != null)
        {
            int idx = getIndex(atom);

            if (idx >= 0)
            {
                atom.setResidue(null);

                atoms.remove(idx);
                atomIndices.remove(idx);
                heteroAtoms.remove(idx);
                serialNumbers.remove(idx);
            }
        }
    }

    public void setAtomID(Atom atom, String id)
    {
        atomIndices.set(getIndex(atom), id);
    }

    public void setChain(String chain)
    {
        this.chain = chain;
    }

    public void setChainNumber(int chainnum)
    {
        chainNumber = chainnum;
    }

    public void setHeteroAtom(Atom atom, boolean hetatm)
    {
        boolean[] btmp;
        btmp = (boolean[]) heteroAtoms.get(getIndex(atom));
        btmp[0] = hetatm;
    }

    public void setIndex(int idx)
    {
        this.index = idx;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public void setSerialNumber(Atom atom, int sernum)
    {
        int[] itmp;
        itmp = (int[]) serialNumbers.get(getIndex(atom));
        itmp[0] = sernum;
    }

    protected void finalize() throws Throwable
    {
        Atom atom;

        for (int i = 0; i < atoms.size(); i++)
        {
            atom = (Atom) atoms.get(i);
            atom.setResidue(null);
        }

        atoms.clear();
        super.finalize();
    }

    /**
     * @return Returns the atomIndices.
     */
    protected List<String> getAtomIndices()
    {
        return atomIndices;
    }

    /**
     * @return Returns the atoms.
     */
    protected List<Atom> getAtoms()
    {
        return atoms;
    }

    /**
     * @return Returns the heteroAtoms.
     */
    protected List<boolean[]> getHeteroAtoms()
    {
        return heteroAtoms;
    }

    protected int getIndex(Atom atom)
    {
        return atoms.indexOf(atom);
    }

    /**
     * @return Returns the serialNumbers.
     */
    protected List<int[]> getSerialNumbers()
    {
        return serialNumbers;
    }

    /**
     * @param atomIndices The atomIndices to set.
     */
    protected void setAtomIndices(List<String> atomIndices)
    {
        this.atomIndices = atomIndices;
    }

    /**
     * @param atoms The atoms to set.
     */
    protected void setAtoms(List<Atom> atoms)
    {
        this.atoms = atoms;
    }

    /**
     * @param heteroAtoms The heteroAtoms to set.
     */
    protected void setHeteroAtoms(List<boolean[]> heteroAtoms)
    {
        this.heteroAtoms = heteroAtoms;
    }

    /**
     * @param serialNumbers The serialNumbers to set.
     */
    protected void setSerialNumbers(List<int[]> serialNumbers)
    {
        this.serialNumbers = serialNumbers;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
