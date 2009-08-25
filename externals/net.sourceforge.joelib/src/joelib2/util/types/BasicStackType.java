///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicStackType.java,v $
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

/**
 * Atom, bond and integer.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicStackType implements java.io.Serializable, StackType
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int atom;

    /**
     *  Description of the Field
     */
    public int bond;

    /**
     *  Description of the Field
     */
    public int previous;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the atom.
     */
    public int getAtom()
    {
        return atom;
    }

    /**
     * @return Returns the bond.
     */
    public int getBond()
    {
        return bond;
    }

    /**
     * @return Returns the previous.
     */
    public int getPrevious()
    {
        return previous;
    }

    /**
     * @param atom The atom to set.
     */
    public void setAtom(int atom)
    {
        this.atom = atom;
    }

    /**
     * @param bond The bond to set.
     */
    public void setBond(int bond)
    {
        this.bond = bond;
    }

    /**
     * @param previous The previous to set.
     */
    public void setPrevious(int previous)
    {
        this.previous = previous;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
