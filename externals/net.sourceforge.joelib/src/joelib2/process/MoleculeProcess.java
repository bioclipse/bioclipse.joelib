///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeProcess.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:38 $
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
package joelib2.process;

import joelib2.molecule.Molecule;

import joelib2.util.PropertyAcceptor;

import java.util.Map;


/**
 *  Interface definition for calling molecule processes.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:38 $
 * @see joelib2.process.ProcessPipe
 */
public interface MoleculeProcess extends PropertyAcceptor
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean clear();

    public ProcessInfo getProcessInfo();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException;

    public void setProcessInfo(ProcessInfo _info);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
