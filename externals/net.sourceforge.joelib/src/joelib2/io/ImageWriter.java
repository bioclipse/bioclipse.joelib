///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ImageWriter.java,v $
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

import java.awt.Image;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface ImageWriter
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    public void closeReader() throws IOException;

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    public void closeWriter() throws IOException;

    public void initReader(InputStream is) throws IOException;

    /**
     *  Description of the Method
     *
     * @param  os               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    public void initWriter(OutputStream os) throws IOException;

    public String inputDescription();

    public String[] inputFileExtensions();

    /**
     *  Reads an molecule entry as (unparsed) <tt>String</tt> representation.
     *
     * @return                  <tt>null</tt> if the reader contains no more
     *      relevant data. Otherwise the <tt>String</tt> representation of the
     *      whole molecule entry is returned.
     * @exception  IOException  typical IOException
     */
    public String read() throws IOException;

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  IOException          Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public boolean read(Molecule mol) throws IOException, MoleculeIOException;

    /**
     *  Loads an molecule in MDL SD-MOL format and sets the title. If <tt>title
     *  </tt> is <tt>null</tt> the title line in the molecule file is used.
     *
     * @param  mol                      Description of the Parameter
     * @param  title                    Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  IOException          Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public boolean read(Molecule mol, String title) throws IOException,
        MoleculeIOException;

    public boolean readable();

    /**
     *  Description of the Method
     *
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean skipReaderEntry() throws IOException;

    /**
     *  Description of the Method
     *
     * @param  mol              Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException;

    /**
     *  Description of the Method
     *
     * @param  mol              Description of the Parameter
     * @param  title            Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title) throws IOException;

    public boolean writeable();

    public boolean writeImage(Image image, OutputStream os) throws IOException;
}
