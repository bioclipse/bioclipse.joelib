///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CMLIDCreator.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//          egonw@sci.kun.nl, wegner@users.sourceforge.net
//Version:  $Revision: 1.9 $
//                      $Date: 2005/02/17 16:48:35 $
//                      $Author: wegner $
//
//Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public License
//as published by the Free Software Foundation; either version 2.1
//of the License, or (at your option) any later version.
//All we ask is that proper credit is given for our work, which includes
//- but is not limited to - adding the above copyright notice to the beginning
//of your source code files, and to any copyright notice that you may distribute
//with programs based on this work.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.io.types.cml;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.smiles.SMILESGenerator;

import joelib2.util.types.BasicStringInt;
import joelib2.util.types.StringInt;

import java.util.Map;


/**
 * Class that provides methods to give unique IDs to ChemObjects.
 * Methods are implemented for Atom, Bond, AtomContainer, SetOfAtomContainers
 * and Reaction.
 *
 * @.author egonw
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:35 $
 */
public class CMLIDCreator
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will not set an id for the AtomContainer.
     *
     * @see #createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers)
     */
    public static void createAtomAndBondIDs(Molecule mol, String molID,
        Map<String, StringInt> atomIDs, Map<String, StringInt> bondIDs)
    {
        CMLIDCreator.createAtomAndBondIDs(mol, molID, atomIDs, bondIDs, 0, 0);
    }

    /**
     * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will not set an id for the AtomContainer.
     *
     * <p>An offset can be used to start numbering at, for example, a3 instead
     * of a1 using an offset = 2.
     *
     * @param atomOffset  Lowest ID number to be used for the Atoms
     * @param bondOffset  Lowest ID number to be used for the Bonds
     *
     * @see #createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers)
     */
    public static void createAtomAndBondIDs(Molecule mol, String molID,
        Map<String, StringInt> atomIDs, Map<String, StringInt> bondIDs,
        int atomOffset, int bondOffset)
    {
        BasicStringInt atomID;
        Atom atom;

        for (int i = 0; i < mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i + 1);
            atomID = new BasicStringInt(molID + ":a" + (i + 1 + atomOffset),
                    atom.getIndex());

            atomIDs.put(molID + ":" + Integer.toString(atom.getIndex()),
                atomID);
            atomID = (BasicStringInt) atomIDs.get(atom);

            //System.out.println(molID + ":" + Integer.toString(atom.getIdx())+" = "+atomID);
        }

        BasicStringInt bondID;

        for (int i = 0; i < mol.getBondsSize(); i++)
        {
            bondID = new BasicStringInt(molID + ":b" + (i + 1 + bondOffset),
                    mol.getBond(i).getIndex());
            bondIDs.put(molID + ":" +
                Integer.toString(mol.getBond(i).getIndex()), bondID);

            //System.out.println(molID + ":" + Integer.toString(mol.getBond(i).getIdx())+" = "+bondID);
        }
    }

    public static String createMoleculeID(Molecule mol)
    {
        return createMoleculeID(mol, true);
    }

    public static String createMoleculeID(Molecule mol, boolean createNumber)
    {
        SMILESGenerator m2s = new SMILESGenerator();

        m2s.init();
        m2s.correctAromaticAmineCharge(mol);

        StringBuffer smiles = new StringBuffer(1000);
        m2s.createSmiString(mol, smiles);

        if (createNumber)
        {
            return "m" + Integer.toString(smiles.toString().hashCode());
        }
        else
        {
            return "m" + smiles.toString();
        }
    }

    //    /**
    //     * Labels the Atom's and Bond's in each AtomContainer using the a1, a2, b1, b2
    //     * scheme often used in CML. It will also set id's for all AtomContainers, naming
    //     * them m1, m2, etc.
    //     * It will not the SetOfAtomContainers itself.
    //     */
    //    public static void createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers containerSet) {
    //        IDCreator.createAtomContainerAndAtomAndBondIDs(containerSet, 0, 0, 0);
    //    }
    //
    //    /**
    //     * Labels the Atom's and Bond's in each AtomContainer using the a1, a2, b1, b2
    //     * scheme often used in CML. It will also set id's for all AtomContainers, naming
    //     * them m1, m2, etc.
    //     * It will not the SetOfAtomContainers itself.
    //     *
    //     * @param containerOffset  Lowest ID number to be used for the AtomContainers
    //     * @param atomOffset  Lowest ID number to be used for the Atoms
    //     * @param bondOffset  Lowest ID number to be used for the Bonds
    //     */
    //    public static void createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers containerSet,
    //                           int containerOffset, int atomOffset, int bondOffset) {
    //        AtomContainer[] containers = containerSet.getAtomContainers();
    //        int atomCount = atomOffset;
    //        int bondCount = bondOffset;
    //        for (int i=0; i<containers.length; i++) {
    //            AtomContainer container = containers[i];
    //            container.setID("m" + (i+1+containerOffset));
    //            IDCreator.createAtomAndBondIDs(container, atomCount, bondCount);
    //            atomCount += container.getAtomCount();
    //            bondCount += container.getBondCount();
    //        }
    //    }
    //
    //    /**
    //     * Labels the reactants and products in the Reaction m1, m2, etc, and the atoms
    //     * accordingly. It does not apply mapping such that mapped atoms have the same ID.
    //     */
    //    public static void createIDs(Reaction reaction) {
    //        IDCreator.createIDs(reaction, 0, 0, 0);
    //    }
    //    public static void createIDs(Reaction reaction,
    //                           int containerOffset, int atomOffset, int bondOffset) {
    //        AtomContainer[] reactants = reaction.getReactants();
    //        int atomCount = atomOffset;
    //        int bondCount = bondOffset;
    //        for (int i=0; i<reactants.length; i++) {
    //            AtomContainer container = reactants[i];
    //            container.setID("m" + (i+1+containerOffset));
    //            IDCreator.createAtomAndBondIDs(container, atomCount, bondCount);
    //            atomCount += container.getAtomCount();
    //            bondCount += container.getBondCount();
    //        }
    //        AtomContainer[] products = reaction.getProducts();
    //        for (int i=0; i<products.length; i++) {
    //            AtomContainer container = products[i];
    //            container.setID("m" + (i+1+containerOffset));
    //            IDCreator.createAtomAndBondIDs(container, atomCount, bondCount);
    //            atomCount += container.getAtomCount();
    //            bondCount += container.getBondCount();
    //        }
    //    }
    //
    //    public static void createIDs(SetOfReactions reactionSet) {
    //        Reaction[] reactions = reactionSet.getReactions();
    //        int containerCount = 0;
    //        int atomCount = 0;
    //        int bondCount = 0;
    //        for (int i=0; i<reactions.length; i++) {
    //            Reaction reaction = reactions[i];
    //            reaction.setID("r" + (i+1));
    //            IDCreator.createIDs(reaction, containerCount, atomCount, bondCount);
    //            containerCount += ReactionManipulator.getAllAtomContainers(reaction).length;
    //            AtomContainer container = ReactionManipulator.getAllInOneContainer(reaction);
    //            atomCount += container.getAtomCount();
    //            bondCount += container.getBondCount();
    //        }
    //    }
    //
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
