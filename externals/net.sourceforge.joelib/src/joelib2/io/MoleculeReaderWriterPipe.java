///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: MoleculeReaderWriterPipe.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.4 $
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
import java.io.OutputStream;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license    GPL
 * @.cvsversion $Revision: 1.4 $, $Date: 2005/02/17 16:48:34 $
 */
public interface MoleculeReaderWriterPipe
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Closes the reader/writer.
     *
     * @exception IOException          input/output exception
     */
    void close() throws IOException;

    /**
     * Creates a simple reader/writer pipe.
     *
     * @param _in       input file
     * @param _inType   input type
     * @param _out      output file
     * @param _outType  output type
     * @throws IOException input/output exception
     */
    void init(InputStream in, IOType inType, OutputStream out, IOType outType)
        throws IOException;

    /**
     * Creates a simple reader/writer pipe where the file types are
     * resolved by the file extensions.
     *
     * Optional parameters:
     * [-i&lt;inputType>]         - Input type
     * [-o&lt;outputType>]        - Output type
     * [+v]                    - Switch verbosity mode on
     * [-?][--help]            - Shows this message
     * inputFile               - Input file
     * outputFile              - Output file
     *
     * @param args the arguments
     * @throws IOException input/output exception
     */
    void initByCommandLine(String[] args) throws IOException;

    /**
     * Returns the last loaded molecule.
     *
     * @return the last loaded molecule
     */
    Molecule loadedMolecule();

    void molecule2handle(Molecule mol);

    /**
     * Returns the number of loaded molecules.
     *
     * @return the number of loaded molecules
     */
    int moleculesLoaded();

    /**
     * Returns the number of written molecules.
     *
     * @return the number of written molecules
     */
    int moleculesWritten();

    /**
     * Reads/writes the next molecule and calls {@link #molecule2handle(Molecule)}.
     *
     * @return                         <tt>true</tt> if more molecules are available
     * @exception IOException          input/output exception
     * @exception MoleculeIOException  molecule parsing exception
     */
    boolean readWriteNext() throws IOException, MoleculeIOException;

    /**
     * Shows usage.
     */
    void showUsage();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
