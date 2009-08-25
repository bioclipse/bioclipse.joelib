///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicMoleculeWriter.java,v $
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Category;


/**
 * Simple writer implementation.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:34 $
 */
public class BasicMoleculeWriter implements MoleculeWriter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicMoleculeWriter.class.getName());
    private static boolean VERBOSE = false;
    private static BasicIOType verboseType = BasicIOTypeHolder.instance()
                                                              .getIOType(
            "SMILES");

    //~ Instance fields ////////////////////////////////////////////////////////

    private int molCounter;
    private OutputStream out = null;

    private IOType outType;
    private StopWatch watch;
    private MoleculeFileIO writer = null;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Creates a simple writer where the file type is resolved by the file extension.
     *
     * @param outputFile      Output file
     * @throws IOException input/output exception
     */
    public BasicMoleculeWriter(String outputFile) throws IOException
    {
        BasicIOType tmpOut = checkGetOutputType(outputFile);
        init(new FileOutputStream(outputFile), tmpOut);
    }

    /**
     * Creates a simple writer.
     *
     * @param outputFile      Output file
     * @param _outTypeString  Output type
     * @throws IOException input/output exception
     */
    public BasicMoleculeWriter(String outputFile, String _outTypeString)
        throws IOException
    {
        init(new FileOutputStream(outputFile),
            BasicIOTypeHolder.instance().getIOType(
                _outTypeString.toUpperCase()));
    }

    /**
     * Creates a simple writer.
     *
     * @param _out            Output stream
     * @param _outTypeString  Output type
     * @throws IOException input/output exception
     */
    public BasicMoleculeWriter(OutputStream _out, String _outTypeString)
        throws IOException
    {
        init(_out,
            BasicIOTypeHolder.instance().getIOType(
                _outTypeString.toUpperCase()));
    }

    /**
     * Creates a simple writer.
     *
     * @param _out            Output stream
     * @param _outType        Output type
     * @throws IOException input/output exception
     */
    public BasicMoleculeWriter(OutputStream out, IOType outType)
        throws IOException
    {
        init(out, outType);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the output type by the output file.
     *
     * The selected output type will be sended to the logger.
     *
     * @param output output file
     * @return the IOType resolved by the file name
     * @throws IOException Input/output exception
     */
    public static BasicIOType checkGetOutputType(String outputFile)
        throws IOException
    {
        BasicIOType outType = null;

        if (outType == null)
        {
            outType = BasicIOTypeHolder.instance().filenameToType(outputFile);

            if (outType == null)
            {
                throw new IOException("Output type of " + outputFile +
                    " could not be estimated.");
            }

            MoleculeFileIO mfType = null;

            try
            {
                mfType = MoleculeFileHelper.getMoleculeFileType(outType);
            }
            catch (MoleculeIOException ex)
            {
                throw new IOException(ex.getMessage());
            }

            logger.info("Output type set to " + outType.toString() + ": " +
                mfType.outputDescription());
        }

        return outType;
    }

    /**
     * Close the simple reader.
     * IOExceptions are supressed.
     */
    public void close()
    {
        try
        {
            if (writer != null)
            {
                writer.closeWriter();
            }
        }
        catch (IOException ex)
        {
            logger.error(ex.toString());
        }
    }

    /**
     * Creates a simple writer.
     *
     * @param outStream      output stream
     * @param _outType  output type
     * @return          <tt>true</tt> if more molecules are available
     * @throws IOException input/output exception
     */
    public boolean init(OutputStream outStream, IOType outType)
        throws IOException
    {
        this.outType = outType;

        if (outType == null)
        {
            //logger.error("Output type not defined.");
            throw new IOException("Output type not defined.");
        }

        try
        {
            out = outStream;
            writer = MoleculeFileHelper.getMolWriter(out, outType);
        }
        catch (Exception ex)
        {
            //ex.printStackTrace();
            throw new IOException("Can not get molecule writer instance.");
        }

        if (!writer.writeable())
        {
            logger.error(outType.getRepresentation() + " is not writeable.");
            logger.error("You're invited to write one !;-)");
            System.exit(1);
        }

        watch = new StopWatch();
        molCounter = 0;

        //    logger.info("Start writing ...");
        return true;
    }

    /**
     * Returns the number of written molecules.
     *
     * @return the number of written molecules
     */
    public int moleculesWritten()
    {
        return molCounter;
    }

    /**
     * Writes next molecule.
     *
     * @param mol molecule to store
     * @return <tt>true</tt> if the molecule was written successfully
     * @throws IOException input/output exception
     * @throws MoleculeIOException molecule parsing exception
     */
    public boolean writeNext(Molecule mol) throws IOException,
        MoleculeIOException
    {
        if (out == null)
        {
            throw new IOException(this.getClass().getName() +
                " not initialized.");
        }

        boolean success = true;

        try
        {
            success = writer.write(mol);

            if (!success)
            {
                logger.info("... " + molCounter +
                    " molecules successful written in " +
                    watch.getPassedTime() + " ms.");

                return false;
            }

            if (VERBOSE)
            {
                System.out.println("written: " + mol.toString(verboseType));
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
                " molecules successful written in " + watch.getPassedTime() +
                " ms.");
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
