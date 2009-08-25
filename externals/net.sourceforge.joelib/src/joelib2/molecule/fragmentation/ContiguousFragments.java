///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ContiguousFragments.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:37 $
//            $Author: wegner $
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
package joelib2.molecule.fragmentation;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.types.atomlabel.AtomInRing;
import joelib2.feature.types.bondlabel.BondInRing;
import joelib2.feature.types.bondlabel.BondIsClosure;

import joelib2.molecule.Atom;
import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.BasicMoleculeVector;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.sort.ArraySizeComparator;
import joelib2.sort.QuickInsertSort;

import joelib2.util.BasicBitVector;
import joelib2.util.BitVector;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Fragmentation implementation for contiguous fragments in a molecule.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.10 $, $Date: 2005/02/17 16:48:37 $
 */
public class ContiguousFragments
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(
            ContiguousFragments.class.getName());

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:37 $";
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomInRing.class, BondInRing.class, BondIsClosure.class};

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Each Vector contains the atom numbers of a contiguous fragment.
     * The integer arrays are sorted by size from largest to smallest.
     * <br>
     * Example:
     * <blockquote><pre>
     * Vector fragments=new Vector();
     * mol.contiguousFragments(fragments);
     * int fragmentAtomIdx[];
     * System.out.println(""+fragments.size()+" contiguous fragments in molecule '"+
     *                    mol.getTitle()+"' (salt ?).");
     * for (int i = 0; i &lt; fragments.size(); i++)
     * {
     *   fragmentAtomIdx=(int[])fragments.get(i);
     *   System.out.print("Atoms of fragment "+i+":");
     *          for (int j = 0; j &lt; fragmentAtomIdx.length; j++)
     *   {
     *     System.out.print(fragmentAtomIdx[j]);
     *     System.out.print(' ');
     *   }
     *   System.out.println();
     * }
     * </pre></blockquote>
     *
     * @param  cfl  a Vector that stores the integer arrays for the contiguous fragments atom numbers
     * @see #stripSalts()
     */
    public static synchronized void contiguousFragments(Molecule mol,
        List<int[]> cfl)
    {
        int index;
        AtomIterator ait = mol.atomIterator();
        Vector tmp = null;
        BitVector used = new BasicBitVector(mol.getAtomsSize() + 1);
        BitVector curr = new BasicBitVector(mol.getAtomsSize() + 1);
        BitVector next = new BasicBitVector(mol.getAtomsSize() + 1);
        BitVector frag = new BasicBitVector(mol.getAtomsSize() + 1);
        Atom atom;
        Bond bond;

        while (used.countBits() < mol.getAtomsSize())
        {
            curr.clear();
            frag.clear();
            ait.reset();

            while (ait.hasNext())
            {
                atom = ait.nextAtom();

                if (!used.bitIsOn(atom.getIndex()))
                {
                    curr.setBitOn(atom.getIndex());

                    break;
                }
            }

            frag.orSet(curr);

            while (!curr.isEmpty())
            {
                next.clear();

                for (index = curr.nextBit(-1); index != curr.endBit();
                        index = curr.nextBit(index))
                {
                    atom = mol.getAtom(index);

                    BondIterator bit = atom.bondIterator();

                    while (bit.hasNext())
                    {
                        bond = bit.nextBond();

                        if (!used.bitIsOn(bond.getNeighborIndex(atom)))
                        {
                            next.setBitOn(bond.getNeighborIndex(atom));
                        }
                    }
                }

                used.orSet(curr);
                used.orSet(next);
                frag.orSet(next);
                curr.set(next);
            }

            tmp = new Vector();
            tmp.clear();
            cfl.add(frag.toIntArray());
        }

        QuickInsertSort sorting = new QuickInsertSort();
        ArraySizeComparator arraySizeComparator = new ArraySizeComparator();
        sorting.sort(cfl, arraySizeComparator);
    }

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    /**
     * Fragments a molecule and single atoms occuring in the molecule are allowed.
     */
    public MoleculeVector getFragmentation(Molecule mol)
    {
        return getFragmentation(mol, false, null);
    }

    /**
     * Returns all contiguous fragments of this molecule as single {@link joelib2.molecule.Molecule} objects.
     *
     * @param mol
     * @param skipSingleAtoms
     * @return JOEMolVector
     */
    public MoleculeVector getFragmentation(Molecule mol,
        boolean skipSingleAtoms, List<int[]> origAtomIdx)
    {
        if ((mol == null) || mol.isEmpty())
        {
            logger.warn(
                "Molecule not defined or empty. It can not be fragmented.");

            return null;
        }

        List<int[]> fragmentsAtoms = new Vector<int[]>();
        contiguousFragments(mol, fragmentsAtoms);

        MoleculeVector fragments = new BasicMoleculeVector();
        int[] fragmentAtoms;
        Molecule fragment;
        int[] mappedAtomIdx = new int[mol.getAtomsSize() + 1];
        Map<Bond, String> bonds = new Hashtable<Bond, String>(mol
                .getBondsSize());
        Bond bond;
        Atom atom;
        BondIterator bit;

        for (int fragmentIdx = 0; fragmentIdx < fragmentsAtoms.size();
                fragmentIdx++)
        {
            fragmentAtoms = (int[]) fragmentsAtoms.get(fragmentIdx);

            if (origAtomIdx != null)
            {
                origAtomIdx.add(fragmentAtoms);
            }

            //System.out.println ("fragment " + i + " has "+fragmentAtomIdx.length+" atoms");
            // create new molecule
            fragment = new BasicConformerMolecule(mol.getInputType(),
                    mol.getOutputType());

            //System.out.println("has virtual bond "+newMol.hasData(JOEDataType.JOE_VIRTUAL_BOND_DATA));
            fragment.beginModify();

            if (fragmentAtoms.length == 1)
            {
                // skip non-connected atoms
                if (skipSingleAtoms)
                {
                    continue;
                }

                //System.out.println("atom: "+fragmentAtomIdx[0]);
                fragment.reserveAtoms(1);
                fragment.addAtomClone(mol.getAtom(fragmentAtoms[0]));
            }
            else
            {
                // store mapping positions
                for (int j = 0; j < fragmentAtoms.length; j++)
                {
                    //System.out.println("map "+fragmentAtomIdx[j]+" to "+(j+1));
                    mappedAtomIdx[fragmentAtoms[j]] = j + 1;
                }

                // create atoms and bonds for new molecule
                fragment.reserveAtoms(fragmentAtoms.length);

                for (int j = 0; j < fragmentAtoms.length; j++)
                {
                    //System.out.println("atom: "+fragmentAtomIdx[j]);
                    atom = mol.getAtom(fragmentAtoms[j]);

                    //System.out.println("add atom: "+fragmentAtomIdx[j]);
                    fragment.addAtomClone(atom);

                    bit = atom.bondIterator();

                    //System.out.println("Has bonds: "+bit.hasNext());
                    while (bit.hasNext())
                    {
                        bond = bit.nextBond();

                        // ensure to store bonds only once
                        if (!bonds.containsKey(bond))
                        {
                            //System.out.println("old bond: "+bond.getBeginAtomIdx()+bond.toString()+bond.getEndAtomIdx());
                            //System.out.println("bond: "+mappedAtomIdx[bond.getBeginAtomIdx()]+bond.toString()+mappedAtomIdx[bond.getEndAtomIdx()]);
                            bonds.put(bond, "");
                            fragment.addBond(
                                mappedAtomIdx[bond.getBeginIndex()],
                                mappedAtomIdx[bond.getEndIndex()],
                                bond.getBondOrder(), bond.getFlags());
                        }
                    }
                }
            }

            fragment.endModify();

            // add new molecule
            fragments.addMol(fragment);
        }

        //System.out.println("Finished contigous fragmentation");
        return fragments;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
