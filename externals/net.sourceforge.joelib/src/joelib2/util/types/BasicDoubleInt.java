///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicDoubleInt.java,v $
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
 * Double and integer value.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicDoubleInt implements java.io.Serializable, DoubleInt
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public double doubleValue;

    /**
     *  Description of the Field
     */
    public int intValue;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntInt object
     */
    public BasicDoubleInt()
    {
    }

    /**
     *  Constructor for the IntInt object
     *
     * @param  _i1  Description of the Parameter
     * @param  _i2  Description of the Parameter
     */
    public BasicDoubleInt(double initDouble, int initInt)
    {
        doubleValue = initDouble;
        intValue = initInt;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof BasicDoubleInt)
        {
            BasicDoubleInt di = (BasicDoubleInt) otherObj;

            if ((di.doubleValue == this.doubleValue) &&
                    (di.intValue == this.intValue))
            {
                return true;
            }
        }

        return isEqual;
    }

    /**
     * @return Returns the doubleValue.
     */
    public double getDoubleValue()
    {
        return doubleValue;
    }

    /**
     * @return Returns the intValue.
     */
    public int getIntValue()
    {
        return intValue;
    }

    /**
     * @see java.lang.Double#doubleToLongBits(double)
     */
    public int hashCode()
    {
        long bits = Double.doubleToLongBits(doubleValue);
        int dh = (int) (bits ^ (bits >>> 32));

        return intValue & dh;
    }

    /**
     * @param doubleValue The doubleValue to set.
     */
    public void setDoubleValue(double doubleValue)
    {
        this.doubleValue = doubleValue;
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
