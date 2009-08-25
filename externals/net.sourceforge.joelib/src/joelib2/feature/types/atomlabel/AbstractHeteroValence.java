///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AbstractHeteroValence.java,v $
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
import joelib2.molecule.Bond;

import joelib2.util.iterator.BondIterator;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:31 $
 */
public class AbstractHeteroValence
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static int calculate(Atom atom)
    {
        int count = 0;
        BondIterator bit = atom.bondIterator();
        Bond bond;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (!AtomIsHeteroatom.isHeteroatom((bond.getNeighbor(atom))))
            {
                count++;
            }
        }

        return count;
    }
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
