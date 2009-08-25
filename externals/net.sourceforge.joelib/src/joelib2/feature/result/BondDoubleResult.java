///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BondDoubleResult.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:30 $
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
package joelib2.feature.result;

import joelib2.molecule.types.BondProperties;


/**
 * Double results of bond properties.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:30 $
 */
public class BondDoubleResult extends DoubleArrayResult
    implements BondProperties, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DoubleResult object
     */
    public BondDoubleResult()
    {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        BondDoubleResult newObj = new BondDoubleResult();

        newObj.value = new double[this.value.length];

        return clone(newObj);
    }

    public BondDoubleResult clone(BondDoubleResult other)
    {
        return (BondDoubleResult) super.clone(other);
    }

    public double getDoubleValue(int bondIdx)
    {
        return value[bondIdx - 1];
    }

    public int getIntValue(int bondIdx)
    {
        return (int) value[bondIdx - 1];
    }

    public String getStringValue(int bondIdx)
    {
        return Double.toString(value[bondIdx - 1]);
    }

    public Object getValue(int bondIdx)
    {
        return new Double(value[bondIdx - 1]);
    }

    public void setDoubleValue(int bondIdx, double _value)
    {
        value[bondIdx - 1] = _value;
    }

    public void setIntValue(int bondIdx, int _value)
    {
        value[bondIdx - 1] = (double) _value;
    }

    public void setStringValue(int bondIdx, String _value)
    {
        value[bondIdx - 1] = Double.parseDouble(_value);
    }

    public void setValue(int bondIdx, Object _value)
    {
        value[bondIdx - 1] = ((Double) _value).doubleValue();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
