///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ConnectionHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:36 $
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
package joelib2.molecule;

import joelib2.data.BasicElementHolder;

import joelib2.feature.types.atomlabel.AtomBondOrderSum;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;

import joelib2.sort.QuickInsertSort;

import joelib2.util.iterator.AtomIterator;

import joelib2.util.types.AtomDouble;
import joelib2.util.types.BasicAtomDouble;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Connection helper for molecules.
 *
 * @.author     wegnerj
 * @.wikipedia Molecule
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:36 $
 */
public class ConnectionHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.molecule.ConnectionHelper");

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Use inter-atomic distances to identify bonds.
         * <p>
         * For assigning atom types using a geometry-based algorithm have a look at {@.cite ml91} and the
         * structure based expert rules in {@link joelib2.data.BasicAtomTyper}.
     *
     * @.cite ml91
     */
    public static synchronized void connectTheDots(Molecule mol)
    {
        if (mol.isEmpty())
        {
            return;
        }

        if (!mol.has3D())
        {
            return; // not useful on 2D structures
        }

        int zAtomIdx1;
        int zAtomIdx2;
        int max;
        Atom atom;
        Atom nbr;

        List<AtomDouble> zsortedAtoms = new Vector<AtomDouble>();
        Vector rad = new Vector();
        Vector zsorted = new Vector();
        double[] coordArr = new double[mol.getAtomsSize() * 3];
        rad.setSize(mol.getAtomsSize());

        AtomIterator ait = mol.atomIterator();
        zAtomIdx1 = 0;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            (atom.getCoords3D()).get(coordArr, zAtomIdx1 * 3);

            AtomDouble entry = new BasicAtomDouble(atom,
                    atom.getCoords3D().getZ3D());
            zsortedAtoms.add(entry);
            zAtomIdx1++;
        }

        QuickInsertSort qisort = new QuickInsertSort();
        qisort.sort(zsortedAtoms, new AtomZPosComparator());
        max = zsortedAtoms.size();

        for (zAtomIdx1 = 0; zAtomIdx1 < max; zAtomIdx1++)
        {
            atom = ((BasicAtomDouble) zsortedAtoms.get(zAtomIdx1)).atom;

            double fvalue = BasicElementHolder.instance().correctedBondRad(atom
                    .getAtomicNumber(), 3);
            fvalue *= 1.10f;
            rad.set(zAtomIdx1, new double[]{fvalue});
            zsorted.add(new int[]{atom.getIndex() - 1});
        }

        int[] itmp;
        int idx1;
        int idx2;
        double cutoff;
        double zd;
        double tmpf;
        double d2;

        for (zAtomIdx1 = 0; zAtomIdx1 < max; zAtomIdx1++)
        {
            itmp = (int[]) zsorted.get(zAtomIdx1);
            idx1 = itmp[0];

            for (zAtomIdx2 = zAtomIdx1 + 1; zAtomIdx2 < max; zAtomIdx2++)
            {
                itmp = (int[]) zsorted.get(zAtomIdx2);
                idx2 = itmp[0];

                cutoff = ((double[]) rad.get(zAtomIdx1))[0] +
                    ((double[]) rad.get(zAtomIdx2))[0];
                cutoff *= cutoff;

                tmpf = coordArr[(idx1 * 3) + 2] - coordArr[(idx2 * 3) + 2];
                zd = tmpf * tmpf;

                //if (zd > cutoff)
                if (zd > 25.0)
                {
                    // bigger than max cutoff
                    break;
                }

                tmpf = coordArr[idx1 * 3] - coordArr[idx2 * 3];
                d2 = tmpf * tmpf;
                tmpf = coordArr[(idx1 * 3) + 1] - coordArr[(idx2 * 3) + 1];
                d2 += (tmpf * tmpf);
                d2 += zd;

                if (d2 > cutoff)
                {
                    continue;
                }

                if (d2 < 0.4)
                {
                    continue;
                }

                atom = mol.getAtom(idx1 + 1);
                nbr = mol.getAtom(idx2 + 1);

                if (atom.isConnected(nbr))
                {
                    continue;
                }

                if (AtomIsHydrogen.isHydrogen(atom) &&
                        AtomIsHydrogen.isHydrogen(nbr))
                {
                    continue;
                }

                mol.addBond(idx1 + 1, idx2 + 1, 1);
            }
        }

        // If between BeginModify and EndModify, coord pointers are NULL
        // setup molecule to handle current coordinates
        //        if (_c == null)
        //        {
        //            //                   _c = c;
        //            //                   for (atom = BeginAtom(i);atom;atom = NextAtom(i))
        //            //                           atom->SetCoordPtr(&_c);
        //            //                   _vconf.push_back(c);
        //            //                   unset = true;
        //        }
        // Cleanup -- delete long bonds that exceed max valence
        Bond maxbond;

        // Cleanup -- delete long bonds that exceed max valence
        Bond bond;
        double maxlength;
        List bonds;

        for (int i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);

            while ((AtomBondOrderSum.getIntValue(atom) >
                        (BasicElementHolder.instance().getMaxBonds(
                                atom.getAtomicNumber()))) ||
                    (AtomHelper.smallestBondAngle(atom) < 45.0))
            {
                bonds = atom.getBonds();
                maxbond = (Bond) bonds.get(0);
                maxlength = BondHelper.getLength(maxbond);

                for (int m = 0; m < bonds.size(); m++)
                {
                    bond = (Bond) bonds.get(m);

                    if (BondHelper.getLength(bond) > maxlength)
                    {
                        maxbond = bond;
                        maxlength = BondHelper.getLength(bond);
                    }
                }

                mol.deleteBond(maxbond);
            }
        }

        //         if (unset)
        //         {
        //                 _c = null;
        //                 for (atom = BeginAtom(i);atom;atom = NextAtom(i))
        //                         atom->ClearCoordPtr();
        //                 _vconf.resize(_vconf.size()-1);
        //         }
        coordArr = null;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
