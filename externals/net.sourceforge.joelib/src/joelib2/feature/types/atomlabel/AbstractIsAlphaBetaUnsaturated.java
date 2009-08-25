///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AbstractIsAlphaBetaUnsaturated.java,v $
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

import joelib2.molecule.Atom;

import joelib2.util.iterator.NbrAtomIterator;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:31 $
 */
public class AbstractIsAlphaBetaUnsaturated
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns <tt>true</tt> if this atom is part of an alpha
     * beta unsaturated fragment.
     *
     * <p>
     * Search for pattern of type:<br>
     * $(*[*,!P,!S]=:#*) if <tt>includePandS=false</tt>
     * or<br>
     * $(**=:#*) if <tt>includePandS=true</tt>
     *
     * @param  includePandS  if <tt>true</tt>
     * @return               <tt>true</tt> if this atom is part of an alpha
     *                        beta unsaturated fragment
     */
    public static boolean calculate(Atom atom, boolean includePandS)
    {
        Atom atom1;
        Atom atom2;
        NbrAtomIterator nait1 = atom.nbrAtomIterator();
        boolean hasABUnsat = false;

        while (nait1.hasNext())
        {
            atom1 = nait1.nextNbrAtom();

            if (includePandS ||
                    (!AtomIsPhosphorus.isPhosphorus(atom1) &&
                        !AtomIsSulfur.isSulfur(atom1)))
            {
                NbrAtomIterator nait2 = atom1.nbrAtomIterator();

                while (nait2.hasNext())
                {
                    atom2 = nait2.nextNbrAtom();

                    if ((atom2 != atom) &&
                            ((nait2.actualBond().getBondOrder() == 2) ||
                                (nait2.actualBond().getBondOrder() == 3) ||
                                (nait2.actualBond().getBondOrder() == 5)))
                    {
                        hasABUnsat = true;

                        break;
                    }
                }

                if (hasABUnsat)
                {
                    break;
                }
            }
        }

        return hasABUnsat;
    }
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
