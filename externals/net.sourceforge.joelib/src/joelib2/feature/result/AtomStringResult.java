///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomStringResult.java,v $
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
 * Integer results of atom properties.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:30 $
 */
public class AtomStringResult extends StringArrayResult
    implements AtomProperties, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DoubleResult object
     */
    public AtomStringResult()
    {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        AtomStringResult newObj = new AtomStringResult();

        newObj.value = new String[this.value.length];

        return clone(newObj);
    }

    public AtomStringResult clone(AtomStringResult other)
    {
        return (AtomStringResult) super.clone(other);
    }

    /**
     * Gets the doubleValue attribute of the AtomIntResult object
     *
     * @param atomIdx  Description of the Parameter
     * @return         The doubleValue value
     */
    public double getDoubleValue(int atomIdx)
    {
        return Double.parseDouble(value[atomIdx - 1]);
    }

    /**
     * Gets the intValue attribute of the AtomIntResult object
     *
     * @param atomIdx  Description of the Parameter
     * @return         The intValue value
     */
    public int getIntValue(int atomIdx)
    {
        return Integer.parseInt(value[atomIdx - 1]);
    }

    /**
     * Gets the stringValue attribute of the AtomIntResult object
     *
     * @param atomIdx  Description of the Parameter
     * @return         The stringValue value
     */
    public String getStringValue(int atomIdx)
    {
        return value[atomIdx - 1];
    }

    /**
     * Gets the value attribute of the AtomIntResult object
     *
     * @param atomIdx  Description of the Parameter
     * @return         The value value
     */
    public Object getValue(int atomIdx)
    {
        return value[atomIdx - 1];
    }

    /**
     * Sets the doubleValue attribute of the AtomIntResult object
     *
     * @param atomIdx  The new doubleValue value
     * @param _value   The new doubleValue value
     */
    public void setDoubleValue(int atomIdx, double _value)
    {
        value[atomIdx - 1] = Double.toString(_value);
    }

    /**
     * Sets the intValue attribute of the AtomIntResult object
     *
     * @param atomIdx  The new intValue value
     * @param _value   The new intValue value
     */
    public void setIntValue(int atomIdx, int _value)
    {
        value[atomIdx - 1] = Integer.toString(_value);
    }

    /**
     * Sets the stringValue attribute of the AtomIntResult object
     *
     * @param atomIdx  The new stringValue value
     * @param _value   The new stringValue value
     */
    public void setStringValue(int atomIdx, String _value)
    {
        value[atomIdx - 1] = _value;
    }

    /**
     * Sets the value attribute of the AtomIntResult object
     *
     * @param atomIdx  The new value value
     * @param _value   The new value value
     */
    public void setValue(int atomIdx, Object _value)
    {
        value[atomIdx - 1] = (String) _value;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
