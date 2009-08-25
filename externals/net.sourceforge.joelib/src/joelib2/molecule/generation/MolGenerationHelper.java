///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolGenerationHelper.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
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
package joelib2.molecule.generation;

import joelib2.data.BasicElementHolder;

import joelib2.feature.types.atomlabel.AtomHybridisation;

import joelib2.math.BasicVector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.AtomHelper;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.NbrAtomIterator;

import joelib2.util.types.BasicIntInt;

import org.apache.log4j.Category;


/**
 * Helper class for molecule generation.
 *
 * @.author     wegnerj
 * @.wikipedia Combinatorial chemistry
 * @.wikipedia Molecule
 * @.license    GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/17 16:48:37 $
 */
public class MolGenerationHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            MolGenerationHelper.class.getName());

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Create molecule by adding R-groups to the given base molecule.
     * For the fragments rudimentary 3D coordinates will be calculated,
     * if the base molecule contains already 2D or 3D coordinates.
     *
     * @param baseMolecule basis molecule
     * @param connections where i1 is the placeholder atom and i2 is the R-group
     * @param rGroups the fragments to add to the base molecule
     * @return Molecule the generated molecule
     */
    public Molecule createNewMolecule(Molecule baseMolecule,
        BasicIntInt[] connections, Molecule[] rGroups)
    {
        Molecule mol = (Molecule) baseMolecule.clone();

        int size = rGroups.length;
        StringBuffer sb = new StringBuffer();
        Molecule fragment;

        for (int m = 0; m < size; m++)
        {
            fragment = rGroups[m];
            sb.append(fragment.getTitle());

            //System.out.print(rGroups[m] + " ");
            if (m < (size - 1))
            {
                sb.append(',');
            }
        }

        sb.append('-');
        sb.append(mol.getTitle());
        mol.setTitle(sb.toString());

        Atom[] placeHolderAtoms = new Atom[connections.length];

        for (int i = 0; i < connections.length; i++)
        {
            BasicIntInt ii = connections[i];
            fragment = rGroups[ii.intValue2 - 1];
            placeHolderAtoms[i] = mol.getAtom(ii.intValue1);

            //System.out.println("connect fragment " + ii.i2 + " to (place holder) atom " + ii.i1+" "+placeHolderAtoms[i]);
        }

        boolean createCoordinates = mol.has2D() || mol.has3D();

        // do ring search for coordinate generation
        if (createCoordinates)
        {
            mol.getSSSR();
        }

        Bond bond;

        //              Atom atom;
        BasicVector3D v = null;

        for (int i = 0; i != rGroups.length; i++)
        {
            BasicIntInt ii = connections[i];
            fragment = rGroups[ii.intValue2 - 1];

            Atom placeHolderAtom = placeHolderAtoms[i];

            //                  int placeHolderIdx = placeHolderAtom.getIdx();
            Atom nbr;
            int ind_nbr;
            int frag_indx;
            Atom frag_next;
            Atom frag1;

            if (placeHolderAtom.getBonds().size() != 0)
            {
                // get bond to place holder atom
                // The atom has only one bond so this is simple
                bond = (Bond) placeHolderAtom.getBonds().get(0);

                // get neighbour atom of the place holder atom
                nbr = bond.getNeighbor(placeHolderAtom);

                mol.beginModify();

                // delete bond to place holder atom
                mol.deleteBond(bond);

                // delete atom to place holder atom
                mol.deleteAtom(placeHolderAtom);

                //System.out.println("delete atom "+placeHolderAtom.getIdx()+" "+placeHolderAtom+" "+placeHolderAtom.getVector());
                mol.endModify();

                frag1 = (Atom) fragment.getAtom(1).clone();

                // create new bond vector
                if (createCoordinates)
                {
                    v = new BasicVector3D();

                    double bondlen =
                        BasicElementHolder.instance().getCovalentRad(nbr
                            .getAtomicNumber()) +
                        BasicElementHolder.instance().getCovalentRad(frag1
                            .getAtomicNumber());
                    AtomHelper.getNewBondVector3D(nbr, v, bondlen);
                }

                mol.beginModify();

                // set new atom coordinates
                // and add first Rgroup atom
                // with a single bond
                if (createCoordinates)
                {
                    frag1.setCoords3D(v.getX3D(), v.getY3D(), v.getZ3D());
                }

                ind_nbr = nbr.getIndex();
                mol.addAtomClone(frag1);
                frag_indx = mol.getAtomsSize();
                mol.addBond(ind_nbr, frag_indx, 1);

                // add all other atoms
                if (fragment.getAtomsSize() > 1)
                {
                    int firstConnectIdx = frag_indx;

                    // add atoms
                    for (int k = 2; k <= fragment.getAtomsSize(); k++)
                    {
                        frag_next = (Atom) fragment.getAtom(k).clone();
                        mol.addAtomClone(frag_next);
                    }

                    int begin;
                    int end;

                    for (int k = 0; k < fragment.getBondsSize(); k++)
                    {
                        bond = fragment.getBond(k);
                        begin = bond.getBeginIndex();
                        end = bond.getEndIndex();

                        // use relative atom indices to first fragment atom
                        // use original bond order
                        mol.addBond(firstConnectIdx - 1 + begin,
                            firstConnectIdx - 1 + end, bond.getBondOrder());
                    }

                    // create coordinates for the rest of the fragment atoms
                    if (createCoordinates)
                    {
                        double bondlen;
                        int generated = 0;

                        // repeat iteration until all fragment atoms has
                        // assigned coordinates (important for non-linear groups)
                        while (generated <= (fragment.getAtomsSize() - 2))
                        {
                            for (int k = 1; k < fragment.getAtomsSize(); k++)
                            {
                                frag_next = mol.getAtom(firstConnectIdx + k);

                                //System.out.println("fragment atom "+(k+1)+" "+frag_next.getX());
                                if ((frag_next.get3Dx() == 0.0) &&
                                        (frag_next.get3Dy() == 0.0) &&
                                        (frag_next.get3Dz() == 0.0))
                                {
                                    NbrAtomIterator nait = frag_next
                                        .nbrAtomIterator();

                                    while (nait.hasNext())
                                    {
                                        nbr = nait.nextNbrAtom();

                                        //System.out.println("fragment atom "+(nbr.getIdx()-firstConnectIdx)+" "+nbr.getX());
                                        if ((nbr.get3Dx() != 0.0) &&
                                                (nbr.get3Dy() != 0.0))
                                        {
                                            v = new BasicVector3D();

                                            // now we have a complete molecule
                                            // and can use corrected bonds
                                            bondlen =
                                                BasicElementHolder.instance()
                                                                  .correctedBondRad(
                                                    nbr.getAtomicNumber(),
                                                    AtomHybridisation
                                                    .getIntValue(nbr)) +
                                                BasicElementHolder.instance()
                                                                  .correctedBondRad(
                                                    frag_next
                                                    .getAtomicNumber(),
                                                    AtomHybridisation
                                                    .getIntValue(frag_next));
                                            AtomHelper.getNewBondVector3D(nbr,
                                                v, bondlen);
                                            frag_next.setCoords3D(v.getX3D(),
                                                v.getY3D(), v.getZ3D());
                                            generated++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                mol.endModify();
            }
            else
            {
                logger.error("Atom " + placeHolderAtom.getIndex() +
                    " has no bond to other atoms.");

                return null;
            }
        }

        return mol;
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
