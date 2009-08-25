///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomTypePair.java,v $
//  Purpose:  Atom pair descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
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
package joelib2.feature.types.atompair;

/**
 * Atom type pair (depends on atom properties used).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:32 $
 */
public class AtomTypePair
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public AtomPairAtomType atom_1;
    public AtomPairAtomType atom_2;
    public int atomicNumber_1;
    public int atomicNumber_2;

    //~ Constructors ///////////////////////////////////////////////////////////

    /*public AtomTypePair(AtomPairAtomType _atom_1, AtomPairAtomType _atom_2)
    {
            atom_1 = _atom_1;
            atom_2 = _atom_2;
    }*/
    public AtomTypePair(AtomPair pair)
    {
        atom_1 = pair.atomPair1;
        atom_2 = pair.atomPair2;
        atomicNumber_1 = pair.atomPair1.atomicNumber;
        atomicNumber_2 = pair.atomPair2.atomicNumber;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object o)
    {
        AtomTypePair atp = (AtomTypePair) o;

        //return (this.atomicNumber_1 == atp.atomicNumber_1 && this.atomicNumber_2 == atp.atomicNumber_2);
        return (this.atom_1.equals(atp.atom_1) &&
                this.atom_2.equals(atp.atom_2));
    }

    public int hashCode()
    {
        return 0;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
