///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicStringString.java,v $
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
 * Two String values.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicStringString implements java.io.Serializable, StringString,
    Cloneable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    private String stringValue1;

    /**
     *  Description of the Field
     */
    private String stringValue2;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the StringString object
     *
     * @param  _s1  Description of the Parameter
     * @param  _s2  Description of the Parameter
     */
    public BasicStringString(String initString1, String initString2)
    {
        stringValue1 = initString1;
        stringValue2 = initString2;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        return new BasicStringString(stringValue1, stringValue2);
    }

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof BasicStringString)
        {
            BasicStringString other = (BasicStringString) otherObj;

            if (other.stringValue1.equals(this.stringValue1) &&
                    other.stringValue2.equals(this.stringValue2))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    /**
     * @return Returns the stringValue1.
     */
    public String getStringValue1()
    {
        return stringValue1;
    }

    /**
     * @return Returns the stringValue2.
     */
    public String getStringValue2()
    {
        return stringValue2;
    }

    public int hashCode()
    {
        return stringValue1.hashCode() & stringValue2.hashCode();
    }

    /**
     * @param stringValue1 The stringValue1 to set.
     */
    public void setStringValue1(String stringValue1)
    {
        this.stringValue1 = stringValue1;
    }

    /**
     * @param stringValue2 The stringValue2 to set.
     */
    public void setStringValue2(String stringValue2)
    {
        this.stringValue2 = stringValue2;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(stringValue1.length() +
                stringValue2.length() + 3);
        buffer.append('<');
        buffer.append(stringValue1);
        buffer.append(',');
        buffer.append(stringValue2);
        buffer.append('>');

        return buffer.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
