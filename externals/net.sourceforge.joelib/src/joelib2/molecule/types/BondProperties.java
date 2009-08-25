///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BondProperties.java,v $
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

/**
 * Interface to access bond properties.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:37 $
 */
public interface BondProperties
{
    //~ Methods ////////////////////////////////////////////////////////////////

    double getDoubleValue(int bondIdx);

    int getIntValue(int bondIdx);

    int getSize();

    String getStringValue(int bondIdx);

    Object getValue(int bondIdx);

    void setDoubleValue(int bondIdx, double value);

    void setIntValue(int bondIdx, int value);

    void setStringValue(int bondIdx, String value);

    void setValue(int bondIdx, Object value);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
