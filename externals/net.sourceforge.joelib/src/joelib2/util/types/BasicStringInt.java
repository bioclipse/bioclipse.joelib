///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicStringInt.java,v $
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
 * String and integer value.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicStringInt implements java.io.Serializable, StringInt
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int intValue;

    /**
     *  Description of the Field
     */
    public String stringValue;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the StringString object
     *
     * @param  _s1  Description of the Parameter
     * @param  _s2  Description of the Parameter
     */
    public BasicStringInt(String initString, int initInt)
    {
        stringValue = initString;
        intValue = initInt;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof BasicStringInt)
        {
            BasicStringInt other = (BasicStringInt) otherObj;

            if ((other.intValue == this.intValue) &&
                    (other.stringValue.equals(this.stringValue)))
            {
                return true;
            }
        }

        return isEqual;
    }

    /**
     * @return Returns the intValue.
     */
    public int getIntValue()
    {
        return intValue;
    }

    /**
     * @return Returns the stringValue.
     */
    public String getStringValue()
    {
        return stringValue;
    }

    public int hashCode()
    {
        return intValue & stringValue.hashCode();
    }

    /**
     * @param intValue The intValue to set.
     */
    public void setIntValue(int intValue)
    {
        this.intValue = intValue;
    }

    /**
     * @param stringValue The stringValue to set.
     */
    public void setStringValue(String stringValue)
    {
        this.stringValue = stringValue;
    }

    public String toString()
    {
        return "<" + stringValue + "," + intValue + ">";
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
