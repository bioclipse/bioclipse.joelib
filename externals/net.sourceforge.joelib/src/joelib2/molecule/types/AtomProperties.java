///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomProperties.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:37 $
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
package joelib2.molecule.types;

import java.util.zip.DataFormatException;


/**
 * Interface to access atom properties.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:37 $
 */
public interface AtomProperties
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the doubleValue attribute of the AtomProperties object
     *
     * @param atomIdx  Description of the Parameter
     * @return         The doubleValue value
     */
    double getDoubleValue(int atomIdx);

    /**
     * Gets the intValue attribute of the AtomProperties object
     *
     * @param atomIdx                  Description of the Parameter
     * @return                         The intValue value
     * @exception DataFormatException  if this is not an <tt>int</tt> atom property
     */
    int getIntValue(int atomIdx) throws DataFormatException;

    int getSize();

    /**
     * Gets the stringValue attribute of the AtomProperties object
     *
     * @param atomIdx  Description of the Parameter
     * @return         The stringValue value
     */
    String getStringValue(int atomIdx);

    /**
     * Gets the value attribute of the AtomProperties object
     *
     * @param atomIdx  Description of the Parameter
     * @return         The value value
     */
    Object getValue(int atomIdx);

    /**
     * Sets the doubleValue attribute of the AtomProperties object
     *
     * @param atomIdx  The new doubleValue value
     * @param value    The new doubleValue value
     */
    void setDoubleValue(int atomIdx, double value);

    /**
     * Sets the intValue attribute of the AtomProperties object
     *
     * @param atomIdx  The new intValue value
     * @param value    The new intValue value
     */
    void setIntValue(int atomIdx, int value);

    /**
     * Sets the stringValue attribute of the AtomProperties object
     *
     * @param atomIdx  The new stringValue value
     * @param value    The new stringValue value
     */
    void setStringValue(int atomIdx, String value);

    /**
     * Sets the value attribute of the AtomProperties object
     *
     * @param atomIdx  The new value value
     * @param value    The new value value
     */
    void setValue(int atomIdx, Object value);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
