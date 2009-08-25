///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: StringString.java,v $
//Purpose:  Atom representation.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.7 $
//        $Date: 2005/02/17 16:48:42 $
//        $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                       Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                       2003,2004,2005
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
package joelib2.util.types;

/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.7 $, $Date: 2005/02/17 16:48:42 $
 */
public interface StringString
{
    //~ Methods ////////////////////////////////////////////////////////////////

    boolean equals(Object otherObj);

    /**
     * @return Returns the stringValue1.
     */
    String getStringValue1();

    /**
     * @return Returns the stringValue2.
     */
    String getStringValue2();

    int hashCode();

    /**
     * @param stringValue1 The stringValue1 to set.
     */
    void setStringValue1(String stringValue1);

    /**
     * @param stringValue2 The stringValue2 to set.
     */
    void setStringValue2(String stringValue2);

    String toString();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
