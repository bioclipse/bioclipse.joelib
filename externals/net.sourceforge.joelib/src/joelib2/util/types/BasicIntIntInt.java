///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicIntIntInt.java,v $
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
 * {@link BasicIntInt} and one more integer value.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicIntIntInt extends BasicInt implements java.io.Serializable,
    IntIntInt
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
     *  Constructor for the IntIntInt object
     */
    public BasicIntIntInt()
    {
        intPair = new BasicIntInt();
    }

    /**
     *  Constructor for the IntIntInt object
     *
     * @param  _ii  Description of the Parameter
     * @param  _i   Description of the Parameter
     */
    public BasicIntIntInt(BasicIntInt _ii, int _i)
    {
        intValue = _i;
        intPair = _ii;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        if (otherObj instanceof BasicIntIntInt)
        {
            BasicIntIntInt iii = (BasicIntIntInt) otherObj;

            if ((iii.intValue == this.intValue) &&
                    iii.intPair.equals(this.intPair))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * @return Returns the intPair.
     */
    public BasicIntInt getIntPair()
    {
        return intPair;
    }

    public int hashCode()
    {
        return intPair.hashCode() & intValue;
    }

    /**
     * @param intPair The intPair to set.
     */
    public void setIntPair(BasicIntInt intPair)
    {
        this.intPair = intPair;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(10);
        sb.append('<');
        sb.append(intValue);
        sb.append(',');
        sb.append(intPair.toString());
        sb.append('>');

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
