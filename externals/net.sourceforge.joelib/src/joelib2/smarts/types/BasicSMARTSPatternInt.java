///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicSMARTSPatternInt.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:40 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.smarts.types;

import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.types.BasicInt;
import joelib2.util.types.IntValue;


/**
 *  Atom representation.
 *
 * @.author     wegnerj
 *     30. Januar 2002
 */
public class BasicSMARTSPatternInt extends BasicSMARTSValue
    implements java.io.Serializable, IntValue, SMARTSPatternInt
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
     *  Constructor for the SMARTSPatternInt object
     *
     * @param  pattern  Description of the Parameter
     * @param  intValue   Description of the Parameter
     */
    public BasicSMARTSPatternInt(SMARTSPatternMatcher pattern, int intValue)
    {
        this.smartsValue = pattern;
        this.intValue = intValue;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof BasicInt)
        {
            BasicInt other = (BasicInt) otherObj;

            if ((other.intValue == this.intValue))
            {
                isEqual = true;
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

    public int hashCode()
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

    public String toString()
    {
        StringBuffer sb = new StringBuffer(10);
        sb.append('<');
        sb.append(smartsValue.getSmarts());
        sb.append(',');
        sb.append(this.intValue);
        sb.append('>');

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
