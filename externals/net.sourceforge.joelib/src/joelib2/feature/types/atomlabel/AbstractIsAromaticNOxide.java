///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AbstractIsAromaticNOxide.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 28, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:31 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.feature.types.atomlabel;

import joelib2.feature.types.bondlabel.BondInRing;

import joelib2.molecule.Atom;

import joelib2.util.iterator.NbrAtomIterator;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:31 $
 */
public class AbstractIsAromaticNOxide
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static boolean calculate(Atom atom)
    {
        boolean isAromNO = false;

        if (!AtomIsNitrogen.isNitrogen(atom) ||
                !AtomInAromaticSystem.isValue(atom))
        {
            isAromNO = false;
        }
        else
        {
            Atom neighbor;
            NbrAtomIterator nait1 = atom.nbrAtomIterator();

            while (nait1.hasNext())
            {
                neighbor = nait1.nextNbrAtom();

                if (AtomIsOxygen.isOxygen(neighbor) &&
                        !BondInRing.isInRing(nait1.actualBond()) &&
                        (nait1.actualBond().getBondOrder() == 2))
                {
                    isAromNO = true;
                }
            }
        }

        return isAromNO;
    }
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
