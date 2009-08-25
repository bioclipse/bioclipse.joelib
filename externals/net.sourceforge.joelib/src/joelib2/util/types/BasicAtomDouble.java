///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicAtomDouble.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.4 $
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
 * Atom and double z value.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.4 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicAtomDouble extends BasicAtomWrapper
    implements java.io.Serializable, AtomDouble
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public double doubleValue;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomZPos object
     *
     * @param  _atom  Description of the Parameter
     * @param  doubleValue     Description of the Parameter
     */
    public BasicAtomDouble(Atom atom, double doubleValue)
    {
        this.atom = atom;
        this.doubleValue = doubleValue;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Compare this object with another object.
     *
     * @param  obj  the reference object with which to compare.
     * @return      <tt>true</tt> only if the specified object is also a
     *      comparator and it imposes the same ordering as this comparator.
     * @see         java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof AtomDouble)
        {
            throw new ClassCastException("Object must of type " +
                BasicAtomDouble.class.getName());
        }

        AtomDouble atomDouble = (AtomDouble) obj;

        if (this.doubleValue == atomDouble.getDoubleValue())
        {
            isEqual = true;
        }

        return isEqual;
    }

    public double getDoubleValue()
    {
        return doubleValue;
    }

    /**
     * @see java.lang.Double#doubleToLongBits(double)
     */
    public int hashCode()
    {
        long bits = Double.doubleToLongBits(doubleValue);
        int dh = (int) (bits ^ (bits >>> 32));

        return atom.hashCode() & dh;
    }

    /**
     * @param doubleValue The doubleValue to set.
     */
    public void setDoubleValue(double doubleValue)
    {
        this.doubleValue = doubleValue;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
