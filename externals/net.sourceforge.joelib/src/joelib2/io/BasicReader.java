///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicReader.java,v $
//  Purpose:  Example for converting molecules.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
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

import wsi.ra.tool.StopWatch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Category;


/**
 * Simple reader implementation.
 *
 * For speed optimization of loading descriptor molecule files have a
 * look at the {@link joelib2.feature.ResultFactory}.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:34 $
 */
public class BasicReader implements MoleculeReader
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(BasicReader.class
            .getName());
    private static boolean VERBOSE = false;
    private static BasicIOType verboseType = BasicIOTypeHolder.instance()
                                                              .getIOType(
            "SMILES");

    //~ Instance fields ////////////////////////////////////////////////////////

    private InputStream in = null;

    private IOType inType;
    private MoleculeFileIO loader = null;
    private int molCounter;
    private StopWatch watch;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Creates a simple reader where the file type is resolved by the file extension.
     *
     * @param inputFile      Input file
     * @throws IOException input/output exception
     */
    public BasicReader(String inputFile) throws IOException
    {
        BasicIOType tmpIn = checkGetInputType(inputFile);
        init(new FileInputStream(inputFile), tmpIn);
    }

    /**
     * Creates a simple reader.
     *
     * @param inputFile      Input file
     * @param _inTypeString  Input type
     * @throws IOException input/output exception
     */
    public BasicReader(String inputFile, String _inTypeString)
        throws IOException
    {
        init(new FileInputStream(inputFile),
            BasicIOTypeHolder.instance().getIOType(
                _inTypeString.toUpperCase()));
    }

    /**
     * Creates a simple reader.
     *
     * @param _in      Input stream
     * @param _inTypeString  Input type
     * @throws IOException input/output exception
     */
    public BasicReader(InputStream _in, String _inTypeString) throws IOException
    {
        init(_in,
            BasicIOTypeHolder.instance().getIOType(
                _inTypeString.toUpperCase()));
    }

    /**
     * Creates a simple reader.
     *
     * @param _in      Input stream
     * @param _inType  Input type
     * @throws IOException input/output exception
     */
    public BasicReader(InputStream inStream, IOType inType) throws IOException
    {
        init(inStream, inType);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the input type by the input file.
     *
     * The selected input type will be sended to the logger.
     *
     * @param inputFile input file
     * @return the IOType resolved by the file name
     * @throws IOException Input/output exception
     */
    public static BasicIOType checkGetInputType(String inputFile)
        throws IOException
    {
        BasicIOType inType = BasicIOTypeHolder.instance().filenameToType(
                inputFile);

        if (inType == null)
        {
            throw new IOException("Input type of " + inputFile +
                " could not be estimated.");
        }

        MoleculeFileIO mfType = null;

        try
        {
            mfType = MoleculeFileHelper.getMoleculeFileType(inType);
        }
        catch (MoleculeIOException ex)
        {
            throw new IOException(ex.getMessage());
        }

        logger.info("Input type set to " + inType.toString() + ": " +
            mfType.inputDescription());

        return inType;
    }

    /**
     * Close the simple reader.
     * IOExceptions are supressed.
     */
    public void close()
    {
        try
        {
            if (loader != null)
            {
                loader.closeReader();
            }
        }
        catch (IOException ex)
        {
            logger.error(ex.toString());
        }
    }

    /**
     * Creates a simple reader.
     *
     * @param inStream      Input stream
     * @param _inType  Input type
     * @throws IOException input/output exception
     */
    public void init(InputStream inStream, IOType inType) throws IOException
    {
        this.inType = inType;

        if (inType == null)
        {
            //logger.error("Input type not defined.");
            throw new IOException("Input type not defined.");
        }

        try
        {
            in = inStream;
            loader = MoleculeFileHelper.getMolReader(in, inType);
        }
        catch (Exception ex)
        {
            //ex.printStackTrace();
            throw new IOException("Can not get molecule reader instance.");
        }

        if (!loader.readable())
        {
            //            logger.error(inType.getRepresentation() + " is not readable.");
            //            logger.error("You're invited to write one !;-)");
            throw new IOException(inType.getRepresentation() +
                " is not readable.");
        }

        watch = new StopWatch();
        molCounter = 0;
    }

    /**
     * Returns the number of loaded molecules.
     *
     * @return the number of loaded molecules
     */
    public int moleculesLoaded()
    {
        return molCounter;
    }

    /**
     * Read next molecule.
     *
     * @param mol                      The molecule storing class
     * @return                         <tt>true</tt> if more molecules are available
     * @exception IOException          Input/output exception
     * @exception MoleculeIOException  Molecule parsing exception
     */
    public boolean readNext(Molecule mol) throws IOException,
        MoleculeIOException
    {
        if (in == null)
        {
            throw new IOException(this.getClass().getName() +
                " not initialized.");
        }

        boolean success = true;
        mol.clear();

        try
        {
            success = loader.read(mol);

            if (!success)
            {
                logger.info("... " + molCounter +
                    " molecules successful loaded in " + watch.getPassedTime() +
                    " ms.");

                return false;
            }

            // it's better to be not to restrictive !!!
            // e.g for descriptor parsing routines which don't care about molecules !;-)
            // use for restrictive mode:
            //if (mol.empty())
            //{
            //    throw new MoleculeIOException("No molecule loaded (molecule empty).");
            //}
            if (VERBOSE)
            {
                System.out.println("readed " + mol.toString(verboseType));
            }

            molCounter++;
        }
        catch (IOException ex)
        {
            throw ex;
        }

        if ((molCounter % 1000) == 0)
        {
            logger.info("... " + molCounter +
                " molecules successful loaded in " + watch.getPassedTime() +
                " ms.");
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
