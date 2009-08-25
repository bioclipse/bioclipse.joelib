///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AtomTypeConversionHolder.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.9 $
//        $Date: 2005/02/17 16:48:29 $
//        $Author: wegner $
//
//Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                       U.S.A., 1999,2000,2001
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

package joelib2.data;

/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia Molecule
 * @.wikipedia Atom
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public interface AtomTypeConversionHolder
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Sets the fromType attribute of the JOETypeTable object
     *
     * @param  from  The new fromType value
     * @return       Description of the Return Value
     */
    boolean setFromType(String from);

    /**
     *  Sets the toType attribute of the JOETypeTable object
     *
     * @param  to  The new toType value
     * @return     Description of the Return Value
     */
    boolean setToType(String to);

    /**
     *  Description of the Method
     *
     * @param  from  Description of the Parameter
     * @return       Description of the Return Value
     */
    String translate(String from);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
