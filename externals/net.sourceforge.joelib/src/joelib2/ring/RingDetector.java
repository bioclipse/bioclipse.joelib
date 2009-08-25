///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RingDetector.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
//            $Date: 2005/02/17 16:48:38 $
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
package joelib2.ring;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.result.AtomDynamicResult;
import joelib2.feature.result.BondDynamicResult;
import joelib2.feature.result.DynamicArrayResult;

import joelib2.feature.types.atomlabel.AtomInRing;
import joelib2.feature.types.bondlabel.BondInRing;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.BasicBitVector;

import joelib2.util.iterator.BondIterator;

import joelib2.util.types.BasicInt;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom tree.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/17 16:48:38 $
 */
public class RingDetector
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(RingDetector.class
            .getName());
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.12 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:38 $";
    private static final Class[] DEPENDENCIES = new Class[]{};

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @param mol
     * @param ringAtoms
     * @param ringBonds
     * @return
     */
    public static synchronized boolean findRingAtomsAndBonds(Molecule mol,
        AtomDynamicResult ringAtoms, BondDynamicResult ringBonds)
    {
        if (mol.hasData(BondInRing.getName()) &&
                mol.hasData(AtomInRing.getName()))
        {
            return false;
        }

        BasicBitVector visitedAtoms = new BasicBitVector(mol.getAtomsSize() +
                1);
        BasicBitVector visitedBonds = new BasicBitVector(mol.getAtomsSize() +
                1);
        int atomsSize = mol.getAtomsSize() + 1;
        int bondsSize = mol.getBondsSize();
        List<BasicInt> bondIndexPath = new Vector<BasicInt>(atomsSize);

        for (int i = 0; i < bondsSize; i++)
        {
            bondIndexPath.add(new BasicInt(Integer.MAX_VALUE));
        }

        boolean[] ringBondsArr = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, bondsSize);
        int atoms = mol.getAtomsSize();
        boolean[] ringAtomsArr = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, atoms);
        int startDepth = 0;

        for (int atomIndex = 1; atomIndex <= atoms; atomIndex++)
        {
            if (!visitedAtoms.get(atomIndex))
            {
                findRings(mol, ringBondsArr, ringAtomsArr, bondIndexPath,
                    visitedAtoms, visitedBonds, atomIndex, startDepth);
            }
        }

        if (ringAtoms != null)
        {
            ringAtoms.setArray(ringAtomsArr);
            ringAtoms.setKey(AtomInRing.getName());
            ringAtoms.setKeyValue(ringAtoms);
        }

        if (ringBonds != null)
        {
            ringBonds.setArray(ringBondsArr);
            ringBonds.setKey(BondInRing.getName());
            ringBonds.setKeyValue(ringBonds);
        }

        return true;
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
     * @param mol
     * @param ringBonds
     * @param ringAtoms
     * @param bondIndexPath
     * @param visitedAtoms
     * @param visitedBonds
     * @param actualAtomIndex
     * @param depth
     */
    private static void findRings(Molecule mol, boolean[] ringBonds,
        boolean[] ringAtoms, List<BasicInt> bondIndexPath,
        BasicBitVector visitedAtoms, BasicBitVector visitedBonds,
        int actualAtomIndex, int depth)
    {
        Atom atom;
        Bond bond;
        BasicInt actualBondIndex;

        if (visitedAtoms.get(actualAtomIndex))
        {
            int depth_1 = depth - 1;
            actualBondIndex = bondIndexPath.get(depth_1--);
            bond = mol.getBond(actualBondIndex.getIntValue());
            ringBonds[actualBondIndex.getIntValue()] = true;

            while (depth_1 >= 0)
            {
                actualBondIndex = bondIndexPath.get(depth_1--);
                bond = mol.getBond(actualBondIndex.getIntValue());
                ringBonds[actualBondIndex.getIntValue()] = true;
                ringAtoms[bond.getBeginIndex() - 1] = true;
                ringAtoms[bond.getEndIndex() - 1] = true;

                if ((bond.getBeginIndex() == actualAtomIndex) ||
                        (bond.getEndIndex() == actualAtomIndex))
                {
                    break;
                }
            }
        }
        else
        {
            visitedAtoms.setBitOn(actualAtomIndex);
            atom = mol.getAtom(actualAtomIndex);

            BondIterator bit = atom.bondIterator();

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                if (!visitedBonds.get(bond.getIndex()))
                {
                    actualBondIndex = bondIndexPath.get(depth);
                    actualBondIndex.setIntValue(bond.getIndex());
                    visitedBonds.setBitOn(bond.getIndex());
                    findRings(mol, ringBonds, ringAtoms, bondIndexPath,
                        visitedAtoms, visitedBonds, bond.getNeighborIndex(
                            atom), depth + 1);
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
