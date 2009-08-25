///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicIntInt.java,v $
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
public class BasicIntInt implements java.io.Serializable, IntInt
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    public int intValue1;

    public int intValue2;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntInt object
     */
    public BasicIntInt()
    {
    }

    /**
     *  Constructor for the IntInt object
     *
     * @param  _i1  Description of the Parameter
     * @param  _i2  Description of the Parameter
     */
    public BasicIntInt(int initInt1, int initInt2)
    {
        intValue1 = initInt1;
        intValue2 = initInt2;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof BasicIntInt)
        {
            BasicIntInt other = (BasicIntInt) otherObj;

            if ((other.intValue1 == this.intValue1) &&
                    (other.intValue2 == this.intValue2))
            {
                return true;
            }
        }

        return isEqual;
    }

    /**
     * @return Returns the intValue1.
     */
    public int getIntValue1()
    {
        return intValue1;
    }

    /**
     * @return Returns the intValue2.
     */
    public int getIntValue2()
    {
        return intValue2;
    }

    public int hashCode()
    {
        return intValue1 & intValue2;
    }

    /**
     * @param intValue1 The intValue1 to set.
     */
    public void setIntValue1(int intValue1)
    {
        this.intValue1 = intValue1;
    }

    /**
     * @param intValue2 The intValue2 to set.
     */
    public void setIntValue2(int intValue2)
    {
        this.intValue2 = intValue2;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(10);
        sb.append('<');
        sb.append(intValue1);
        sb.append(',');
        sb.append(intValue2);
        sb.append('>');

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
