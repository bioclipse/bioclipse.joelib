///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: PairData.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 15, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:37 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.molecule.types;

import joelib2.io.IOType;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:37 $
 */
public interface PairData
{
    //~ Methods ////////////////////////////////////////////////////////////////

    Object clone();

    /**
     * Warning: The key and the keyValue are no deep clones!
     * @param other
     */
    PairData clone(PairData other);

    boolean equals(Object otherObj);

    /**
     * @return Returns the attribute.
     */
    String getKey();

    /**
     *  Gets the value attribute of the PairData object
     *
     * @return    The value value
     */
    Object getKeyValue();

    int hashCode();

    /**
     * @param key The attribute to set.
     */
    void setKey(String key);

    /**
     *  Sets the value attribute of the PairData object
     *
     * @param  v  The new value value
     */
    void setKeyValue(final Object v);

    String toString();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    String toString(IOType ioType);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
