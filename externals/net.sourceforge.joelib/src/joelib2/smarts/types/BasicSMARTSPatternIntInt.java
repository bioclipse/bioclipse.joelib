///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicSMARTSPatternIntInt.java,v $
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

import joelib2.util.types.BasicIntInt;
import joelib2.util.types.IntInt;


/**
 *  Atom representation.
 *
 * @.author     wegnerj
 *     30. Januar 2002
 */
public class BasicSMARTSPatternIntInt extends BasicSMARTSValue
    implements java.io.Serializable, IntInt, SMARTSPatternIntInt
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
     *  Constructor for the SMARTSPatternIntInt object
     *
     * @param  pattern  Description of the Parameter
     * @param  intPair  Description of the Parameter
     */
    public BasicSMARTSPatternIntInt(SMARTSPatternMatcher pattern,
        BasicIntInt intPair)
    {
        smartsValue = pattern;
        this.intPair = intPair;
    }

    /**
     *  Constructor for the SMARTSPatternIntInt object
     *
     * @param  pattern  Description of the Parameter
     * @param  int1   Description of the Parameter
     * @param  int2   Description of the Parameter
     */
    public BasicSMARTSPatternIntInt(SMARTSPatternMatcher pattern, int int1,
        int int2)
    {
        smartsValue = pattern;
        intPair = new BasicIntInt(int1, int2);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof BasicIntInt)
        {
            BasicIntInt other = (BasicIntInt) otherObj;

            if ((other.intValue1 == intPair.intValue1) &&
                    (other.intValue2 == intPair.intValue2))
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
        return intPair.intValue1;
    }

    /**
     * @return Returns the intValue2.
     */
    public int getIntValue2()
    {
        return intPair.intValue2;
    }

    public int hashCode()
    {
        return intPair.intValue1 & intPair.intValue2;
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

    public String toString()
    {
        StringBuffer sb = new StringBuffer(10);
        sb.append('<');
        sb.append(smartsValue.getSmarts());
        sb.append(',');
        sb.append(intPair.intValue1);
        sb.append(',');
        sb.append(intPair.intValue2);
        sb.append('>');

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
