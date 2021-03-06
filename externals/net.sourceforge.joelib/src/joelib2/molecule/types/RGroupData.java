///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: RGroupData.java,v $
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

import joelib2.util.types.BasicIntInt;

import java.util.List;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:37 $
 */
public interface RGroupData
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Add new atom-Rgroup entry.
     * {@link BasicIntInt} contains in the first value the atom index and in the
     * second value the rgroup number.
     *
     * @param  r  Description of the Parameter
     */
    void add(BasicIntInt r);

    /**
     * Gets the Rgroup informations.
     * {@link BasicIntInt} contains in the first value the atom index and in the
     * second value the rgroup number.
     */
    List<BasicIntInt> getRGroups();

    /**
     * Set all atom-Rgroup entries.
     * {@link BasicIntInt} contains in the first value the atom index and in the
     * second value the rgroup number.
     */
    void setRGroups(List<BasicIntInt> rGroups);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
