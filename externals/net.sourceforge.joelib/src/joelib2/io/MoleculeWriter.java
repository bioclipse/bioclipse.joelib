///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: MoleculeWriter.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//        $Date: 2005/02/17 16:48:34 $
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
package joelib2.io;

import joelib2.molecule.Molecule;

import java.io.IOException;
import java.io.OutputStream;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:34 $
 */
public interface MoleculeWriter
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Close the simple reader.
     * IOExceptions are supressed.
     */
    void close();

    /**
     * Creates a simple writer.
     *
     * @param _out      output stream
     * @param _outType  output type
     * @return          <tt>true</tt> if more molecules are available
     * @throws IOException input/output exception
     */
    boolean init(OutputStream out, IOType outType) throws IOException;

    /**
     * Returns the number of written molecules.
     *
     * @return the number of written molecules
     */
    int moleculesWritten();

    /**
     * Writes next molecule.
     *
     * @param mol molecule to store
     * @return <tt>true</tt> if the molecule was written successfully
     * @throws IOException input/output exception
     * @throws MoleculeIOException molecule parsing exception
     */
    boolean writeNext(Molecule mol) throws IOException, MoleculeIOException;
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
