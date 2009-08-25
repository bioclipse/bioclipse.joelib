///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: MoleculeVector.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.9 $
//          $Date: 2005/02/17 16:48:36 $
//          $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                     Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                     2003,2004,2005
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
package joelib2.molecule;

import joelib2.io.IOType;
import joelib2.io.MoleculeIOException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;


/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.9 $, $Date: 2005/02/17 16:48:36 $
 */
public interface MoleculeVector
{
    //~ Methods ////////////////////////////////////////////////////////////////

    void addMol(Molecule mol);

    /**
     *  Functions for dealing with groups of molecules. MolVec will read either
     *  all molecules from a file or a set of conformers.
     */
    void finalize() throws Throwable;

    /**
     *  Get a specific molecule from a OEMolVector. Index starts at zero.
     *
     * @param  index  Description of the Parameter
     * @return    The mol value
     */
    Molecule getMol(int index);

    /**
     *  Gets the size attribute of the JOEMolVector object
     *
     * @return    The size value
     */
    int getSize();

    /**
     * @return    {@link java.util.List} of <tt>Molecule</tt>
     */
    List getVector();

    /**
     *  Read all molecules from a file into a <code>Vector</code> of <code>Molecule</code>. Input and output
     *  types default to SDF.
     *
     * @param  ifs                        Description of the Parameter
     * @param  in_type                    Description of the Parameter
     * @param  out_type                   Description of the Parameter
     * @param  nToRead                    Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    void read(InputStream ifs, final IOType in_type, final IOType out_type,
        int nToRead) throws IOException;

    /**
     *  Sets the molecule at position <tt>i</tt> of this molecule vector.
     *
     * @param  i    The new mol value
     * @param  mol  The new mol value
     */
    Object setMol(int i, Molecule mol);

    /**
     *  Write a OEMolVector to a file. Output type defaults to SDF.
     *
     * @param  ofs                        Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    void write(OutputStream ofs) throws IOException, MoleculeIOException;

    /**
     *  Write a OEMolVector to a file. Output type defaults to SDF.
     *
     * @param  ofs                        Description of the Parameter
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    void write(OutputStream ofs, IOType type) throws IOException,
        MoleculeIOException;
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
