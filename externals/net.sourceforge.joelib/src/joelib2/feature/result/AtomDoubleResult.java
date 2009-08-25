///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomDoubleResult.java,v $
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

import joelib2.molecule.types.AtomProperties;


/**
 * Double results of atom properties.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:30 $
 */
public class AtomDoubleResult extends DoubleArrayResult
    implements AtomProperties, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DoubleResult object
     */
    public AtomDoubleResult()
    {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        AtomDoubleResult newObj = new AtomDoubleResult();

        newObj.value = new double[this.value.length];

        return clone(newObj);
    }

    public AtomDoubleResult clone(AtomDoubleResult other)
    {
        return (AtomDoubleResult) super.clone(other);
    }

    public double getDoubleValue(int atomIdx)
    {
        return value[atomIdx - 1];
    }

    public int getIntValue(int atomIdx)
    {
        return (int) value[atomIdx - 1];
    }

    public String getStringValue(int atomIdx)
    {
        return Double.toString(value[atomIdx - 1]);
    }

    public Object getValue(int atomIdx)
    {
        return new Double(value[atomIdx - 1]);
    }

    public void setDoubleValue(int atomIdx, double _value)
    {
        value[atomIdx - 1] = _value;
    }

    public void setIntValue(int atomIdx, int _value)
    {
        value[atomIdx - 1] = (double) _value;
    }

    public void setStringValue(int atomIdx, String _value)
    {
        value[atomIdx - 1] = Double.parseDouble(_value);
    }

    public void setValue(int atomIdx, Object _value)
    {
        value[atomIdx - 1] = ((Double) _value).doubleValue();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
