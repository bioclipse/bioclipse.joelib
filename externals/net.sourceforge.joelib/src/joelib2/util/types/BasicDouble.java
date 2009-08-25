///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicDouble.java,v $
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
 * Two integer values.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicDouble implements java.io.Serializable, DoubleValue
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
     *  Constructor for the IntInt object
     */
    public BasicDouble()
    {
    }

    /**
     *  Constructor for the IntInt object
     *
     * @param  _i1  Description of the Parameter
     */
    public BasicDouble(double initDouble)
    {
        doubleValue = initDouble;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof BasicInt)
        {
            BasicInt other = (BasicInt) otherObj;

            if ((other.intValue == this.doubleValue))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    /**
     * @return Returns the intValue.
     */
    public double getDoubleValue()
    {
        return doubleValue;
    }

    public int hashCode()
    {
        long bits = Double.doubleToLongBits(doubleValue);
        int dh = (int) (bits ^ (bits >>> 32));

        return dh;
    }

    /**
     * @param intValue The intValue to set.
     */
    public void setDoubleValue(double doubleValue)
    {
        this.doubleValue = doubleValue;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(10);
        sb.append(doubleValue);

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
