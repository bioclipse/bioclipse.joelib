///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ProtonationModel.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.11 $
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

import joelib2.molecule.Molecule;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia  Protonation
 * @.wikipedia  Deprotonation
 * @.wikipedia  PH
 * @.wikipedia Molecule
 * @.license    GPL
 * @.cvsversion $Revision: 1.11 $, $Date: 2005/02/17 16:48:29 $
 */
public interface ProtonationModel
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    void assignSeedPartialCharge(Molecule mol, double[] pCharges);

    /**
     * Corrects the molecule for PH.
     * Changes the state of oxygen and nitrogen atoms, if it
     * is allowed to change the formal charges of the atoms, that means
     * if <tt>Molecule.automaticFormalCharge()</tt> returns <tt>true</tt>
     *
     * @param  mol  Description of the Parameter
     */
    void correctForPH(Molecule mol);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
