///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AromaticityTyper.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.13 $
//        $Date: 2005/03/03 07:13:36 $
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

import joelib2.feature.result.AtomDynamicResult;
import joelib2.feature.result.BondDynamicResult;

import joelib2.molecule.Molecule;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia Aromaticity
 * @.wikipedia Friedrich August Kekulé von Stradonitz
 * @.wikipedia Molecule
 * @.license    GPL
 * @.cvsversion $Revision: 1.13 $, $Date: 2005/03/03 07:13:36 $
 */
public interface AromaticityTyper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Assign the aromaticity flag to atoms and bonds.
     *
     * 3 rings will be excluded.
     * Please remember that the aromaticity typer JOEAromaticTyper.assignAromaticFlags(Molecule)
     * assign ONLY aromaticity flags and NOT the internal aromatic bond order Bond.JOE_AROMATIC_BOND_ORDER.
     *
     * @param  mol  the molecule
     */
    void assignAromaticFlags(Molecule mol, AtomDynamicResult atoms,
        BondDynamicResult bonds);
    
    boolean isUseAromaticityModel();
    
    void setUseAromaticityModel(boolean useAromaticityModel);
}
