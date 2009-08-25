///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicAtomInt.java,v $
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

import joelib2.molecule.Atom;


/**
 * Atom and number of hydrogen atoms.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicAtomInt extends BasicAtomWrapper
    implements java.io.Serializable, AtomInt
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int intValue;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomHCount object
     *
     * @param  _atom    Description of the Parameter
     * @param  _hCount  Description of the Parameter
     */
    public BasicAtomInt(Atom _atom, int _hCount)
    {
        atom = _atom;
        intValue = _hCount;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the intValue.
     */
    public int getIntValue()
    {
        return intValue;
    }

    /**
     * @param intValue The intValue to set.
     */
    public void setIntValue(int intValue)
    {
        this.intValue = intValue;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
