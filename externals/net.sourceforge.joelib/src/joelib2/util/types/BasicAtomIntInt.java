///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicAtomIntInt.java,v $
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
 * Atom and {@link BasicIntInt}.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicAtomIntInt extends BasicAtomWrapper
    implements java.io.Serializable, AtomIntInt
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public BasicIntInt intPair;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomIntInt object
     *
     * @param  atom   Description of the Parameter
     * @param  _ii  Description of the Parameter
     */
    public BasicAtomIntInt(Atom atom, BasicIntInt _ii)
    {
        this.atom = atom;
        intPair = _ii;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the intPair.
     */
    public BasicIntInt getIntPair()
    {
        return intPair;
    }

    /**
     * @return Returns the intValue1.
     */
    public int getIntValue1()
    {
        return intPair.intValue1;
    }

    /**
     * @return Returns the intValue2.
     */
    public int getIntValue2()
    {
        return intPair.intValue2;
    }

    /**
     * @param intPair The intPair to set.
     */
    public void setIntPair(BasicIntInt intPair)
    {
        this.intPair = intPair;
    }

    /**
     * @param intValue1 The intValue1 to set.
     */
    public void setIntValue1(int intValue1)
    {
        intPair.intValue1 = intValue1;
    }

    /**
     * @param intValue2 The intValue2 to set.
     */
    public void setIntValue2(int intValue2)
    {
        intPair.intValue2 = intValue2;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
