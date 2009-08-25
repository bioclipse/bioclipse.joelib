///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: PropertyWriter.java,v $
//  Purpose:  TODO description.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:34 $
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
package joelib2.io;

import joelib2.molecule.Molecule;

import java.io.IOException;

import java.util.List;


/**
 * Interface for molecule file formats, which accepts descriptor values.
 *
 * For speed optimization of loading descriptor molecule files have a
 * look at the {@link joelib2.feature.ResultFactory}.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:34 $
 */
public interface PropertyWriter
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Writes a molecule with his <tt>PairData</tt>.
     *
     *@param  mol              the molecule with additional data
     *@param  title            the molecule title or <tt>null</tt> if the title
     *                         from the molecule should be used
     *@param  writePairData    if <tt>true</tt> then the additional molecule
     *                         data is written
     *@param  attrib2write     if <tt>null</tt> all <tt>PairData</tt> elements
     *                         are written, otherwise all data elements are
     *                         written which are listed in <tt>attrib2write</tt>.
     *@return                  <tt>true</tt> if the molecule and the data
     *                         has been succesfully written.
     *@exception  IOException  Description of the Exception
     */
    boolean write(Molecule mol, String title, boolean writePairData,
        List attribs2write) throws IOException, MoleculeIOException;
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
