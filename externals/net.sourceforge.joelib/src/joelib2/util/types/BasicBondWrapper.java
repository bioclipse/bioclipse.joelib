///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicBondWrapper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:42 $
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
package joelib2.util.types;

import joelib2.molecule.Bond;


/**
 * Atom and {@link BasicIntInt}.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicBondWrapper implements java.io.Serializable, BondWrapper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    public Bond bond;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicBondWrapper()
    {
    }

    public BasicBondWrapper(Bond bond)
    {
        this.bond = bond;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the atom.
     */
    public Bond getBond()
    {
        return bond;
    }

    /**
     * @param atom The atom to set.
     */
    public void setBond(Bond bond)
    {
        this.bond = bond;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
