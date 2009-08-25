///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ResidueData.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.9 $
//        $Date: 2005/02/17 16:48:29 $
//        $Author: wegner $
//
//Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                       U.S.A., 1999,2000,2001
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                       Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                       2003,2004,2005
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

package joelib2.data;

import joelib2.molecule.Molecule;

import joelib2.util.BitVector;

import joelib2.util.types.StringInt;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia  Amino acid
 * @.wikipedia  Residue
 * @.wikipedia Molecule
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public interface ResidueData
{
    //~ Methods ////////////////////////////////////////////////////////////////

    boolean assignBonds(Molecule mol, BitVector bv);

    int lookupBO(String s);

    int lookupBO(String s1, String s2);

    StringInt lookupType(String atmid);

    boolean setResName(String s);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
