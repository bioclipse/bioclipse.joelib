///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BondIsClosure.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:32 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
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
package joelib2.feature.types.bondlabel;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractDynamicBondProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;

import joelib2.feature.result.DynamicArrayResult;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BondProperties;

import joelib2.util.BasicBitVector;
import joelib2.util.BitVector;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.NbrAtomIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Is this atom negatively charged atom.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:32 $
 */
public class BondIsClosure extends AbstractDynamicBondProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:32 $";
    private static Category logger = Category.getInstance(BondIsClosure.class
            .getName());
    private static final Class[] DEPENDENCIES = new Class[]{};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public BondIsClosure()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.BondDynamicResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return BondIsClosure.class.getName();
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
     *  Returns <tt>true</tt> if this is a ring atom.
     *
     * @return    <tt>true</tt> if this is a ring atom
     */
    public static boolean isClosure(Bond bond)
    {
        boolean isClosure = false;
        BondProperties btCache;

        if (bond.getParent().getModificationCounter() == 0)
        {
            try
            {
                btCache = (BondProperties) FeatureHelper.instance().featureFrom(
                        bond.getParent(), getName());
            }
            catch (FeatureException e1)
            {
                throw new RuntimeException(e1.getMessage());
            }

            if (btCache != null)
            {
                if (btCache.getIntValue(bond.getIndex()) != 0)
                {
                    isClosure = true;
                }
            }
            else
            {
                //logger.error("No closure bond informations available.");
                throw new RuntimeException(
                    "No closure bond informations available.");
            }
        }
        else
        {
            throw new RuntimeException(
                "Could not access bond property. Modification counter is not zero.");
        }

        return isClosure;
    }

    public Object getBondPropertiesArray(Molecule mol)
    {
        int bondsSize = mol.getBondsSize();
        boolean[] closure = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, bondsSize);
        perceiveClosureBond(mol, closure);

        return closure;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }

    /**
     * @param ait
     * @param uatoms
     * @param curr
     * @return
     */
    private void getCurrentAtom(AtomIterator ait, BitVector uatoms,
        List<Atom> curr)
    {
        Atom current = null;

        //      add only first unvisited atom from this molecule
        while (ait.hasNext())
        {
            current = ait.nextAtom();

            if (!uatoms.get(current.getIndex()))
            {
                uatoms.set(current.getIndex());
                curr.add(current);

                break;
            }
        }
    }

    /**
     *
     * A closure bond is given if this bond is NOT part of a breadth-first-search (BFS)
     * tree starting from the first atoms in this molecule/fragments. So, if the BFS starting from the first
     * atom of this molecule will NOT traverse through this bond (begin and end atom) then this
     * a closure bond. A closure bond can be regarded as a ring closure bond for the
     * branches of a BFS search tree.
     *
     * @param mol
     */
    private void perceiveClosureBond(Molecule mol, boolean[] closure)
    {
        Bond bond;
        BasicBitVector uatoms = new BasicBitVector(mol.getAtomsSize() + 1);
        BasicBitVector ubonds = new BasicBitVector(mol.getAtomsSize() + 1);
        List<Atom> curr = new Vector<Atom>();
        AtomIterator ait = mol.atomIterator();

        while (uatoms.countBits() < mol.getAtomsSize())
        {
            // get first/next atom
            if (curr.size() == 0)
            {
                ait.reset();
                getCurrentAtom(ait, uatoms, curr);
            }

            // iterate over all neighbours for current atoms
            // add them to used atoms and add them to used bonds
            // replace current atoms with these neighbour atoms
            pickNeigbours(curr, uatoms, ubonds);
        }

        BondIterator bit = mol.bondIterator();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (!ubonds.get(bond.getIndex()))
            {
                closure[bond.getIndex()] = true;

                //System.out.println("ISCLOSURE: bond "+bond.getIdx()+" (begin="+bond.getBeginAtomIdx()+",end="+bond.getEndAtomIdx()+" set to closure bond.");
            }
        }
    }

    /**
     * @param atom
     * @param curr
     * @param uatoms
     * @param ubonds
     */
    private void pickNeigbours(List<Atom> curr, BasicBitVector uatoms,
        BasicBitVector ubonds)
    {
        List<Atom> next = new Vector<Atom>();
        Atom nbr;

        while (curr.size() != 0)
        {
            for (int i = 0; i < curr.size(); i++)
            {
                NbrAtomIterator nait = ((Atom) curr.get(i)).nbrAtomIterator();

                while (nait.hasNext())
                {
                    nbr = nait.nextNbrAtom();

                    if (!uatoms.get(nbr.getIndex()))
                    {
                        uatoms.set(nbr.getIndex());
                        ubonds.set(nait.actualBond().getIndex());

                        //System.out.println("ISCLOSURE: bond "+nait.actualBond().getIdx()+" (begin="+nait.actualBond().getBeginAtomIdx()+",end="+nait.actualBond().getEndAtomIdx()+" is NOT a closure bond.");
                        next.add(nbr);
                    }
                }
            }

            curr.clear();

            for (int nn = 0; nn < next.size(); nn++)
            {
                curr.add(next.get(nn));
            }

            next.clear();
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
