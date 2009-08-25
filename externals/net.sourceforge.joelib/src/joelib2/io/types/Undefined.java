///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Undefined.java,v $
//  Purpose:  Reader/Writer for Undefined files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
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
package joelib2.io.types;

import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Molecule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Category;


/**
 * Reader/Writer for Undefined files.
 *
 * @.author     wegnerj
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:34 $
 */
public class Undefined implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.Undefined");
    private final static String description = "Undefined";
    private final static String[] extensions =
        new String[]{"udef", "undefined"};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Undefined object
     */
    public Undefined()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void closeReader() throws IOException
    {
    }

    public void closeWriter() throws IOException
    {
    }

    /**
     *  Description of the Method
     *
     * @param is               Description of the Parameter
     * @exception IOException  Description of the Exception
     */
    public void initReader(InputStream is) throws IOException
    {
    }

    /**
     *  Description of the Method
     *
     * @param os               Description of the Parameter
     * @exception IOException  Description of the Exception
     */
    public void initWriter(OutputStream os) throws IOException
    {
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String inputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String[] inputFileExtensions()
    {
        return extensions;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String outputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String[] outputFileExtensions()
    {
        return extensions;
    }

    /**
     *  Reads an molecule entry as (unparsed) <tt>String</tt> representation.
     *
     * @return                  <tt>null</tt> if the reader contains no more
     *      relevant data. Otherwise the <tt>String</tt> representation of the
     *      whole molecule entry is returned.
     * @exception  IOException  typical IOException
     */
    public String read() throws IOException
    {
        logger.error(
            "Reading UNDEFINED data as String representation is not implemented yet !!!");

        return null;
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean read(Molecule mol) throws IOException, MoleculeIOException
    {
        return read(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean read(Molecule mol, String title) throws IOException,
        MoleculeIOException
    {
        return (false);
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean readable()
    {
        return false;
    }

    public boolean skipReaderEntry() throws IOException
    {
        return true;
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException, MoleculeIOException
    {
        return write(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title) throws IOException,
        MoleculeIOException
    {
        return (false);
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean writeable()
    {
        return false;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
