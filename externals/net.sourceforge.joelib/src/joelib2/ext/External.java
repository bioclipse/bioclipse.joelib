///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: External.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.ext;

import joelib2.process.MoleculeProcess;


/**
 *  Interface definition for calling external programs from JOELib.
 *
 * A simple example for calling external programs is the {@link Title2Data} class.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:29 $
 * @see Title2Data
 */
public interface External extends MoleculeProcess
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public String getDescriptionFile();

    public ExternalInfo getExternalInfo();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */

    //    public boolean process(Molecule mol, Hashtable properties);
    public boolean isThisOSsupported();

    public void setExternalInfo(ExternalInfo _info);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
