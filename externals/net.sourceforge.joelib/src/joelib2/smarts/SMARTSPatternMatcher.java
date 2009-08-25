///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: SMARTSPatternMatcher.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:39 $
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
package joelib2.smarts;

/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:39 $
 */
public interface SMARTSPatternMatcher extends QueryPatternMatcher
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the charge attribute of the JOESmartsPattern object
     *
     * @param  idx  Description of the Parameter
     * @return      The charge value
     */
    int getQueryAtomCharge(int idx);

    /**
     *  Gets the sMARTS attribute of the JOESmartsPattern object
     *
     * @return    The sMARTS value
     */
    String getSmarts();

    /**
     *  Gets the vector binding of this smarts pattern for SMARTS atom with <tt>idx</tt>.
     * E.g.: O=CO[#1:1] where :1 means that the atom #1 (H atom) has the vector binding number 1.
     * This example can be used in <tt>JOEChemTransformation</tt> to delete this H atoms:<br>
     * TRANSFORM O=CO[#1:1] >> O=CO
     *
     * @param  idx  the SMARTS atom <tt>idx</tt>
     * @return     0 if no vector binding is was defined
     */
    int getVectorBinding(int idx);

    /**
     *  Description of the Method
     *
     * @param  s  Description of the Parameter
     * @return    <tt>true</tt> if the initialisation was succesfull
     */
    boolean init(String s);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
