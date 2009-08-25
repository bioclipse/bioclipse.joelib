///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: MoleculeReader.java,v $
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
import java.io.InputStream;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:34 $
 */
public interface MoleculeReader
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Close the simple reader.
     * IOExceptions are supressed.
     */
    void close();

    /**
     * Creates a simple reader.
     *
     * @param _in      Input stream
     * @param _inType  Input type
     * @throws IOException input/output exception
     */
    void init(InputStream in, IOType inType) throws IOException;

    /**
     * Returns the number of loaded molecules.
     *
     * @return the number of loaded molecules
     */
    int moleculesLoaded();

    /**
     * Read next molecule.
     *
     * @param mol                      The molecule storing class
     * @return                         <tt>true</tt> if more molecules are available
     * @exception IOException          Input/output exception
     * @exception MoleculeIOException  Molecule parsing exception
     */
    boolean readNext(Molecule mol) throws IOException, MoleculeIOException;
}
