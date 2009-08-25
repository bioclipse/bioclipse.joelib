///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AbstractReaderWriterPipe.java,v $
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

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import wsi.ra.tool.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Category;


/**
 * Simple reader/writer pipe implementation.
 *
 * For speed optimization of loading descriptor molecule files have a
 * look at the {@link joelib2.feature.ResultFactory}.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:34 $
 */
public abstract class AbstractReaderWriterPipe
    implements MoleculeReaderWriterPipe
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            AbstractReaderWriterPipe.class.getName());
    private static boolean VERBOSE = false;
    private static BasicIOType verboseType = BasicIOTypeHolder.instance()
                                                              .getIOType(
            "SMILES");

    //~ Instance fields ////////////////////////////////////////////////////////

    private InputStream in = null;

    private IOType inType;
    private MoleculeFileIO loader = null;
    private Molecule mol;
    private int molCounterLoaded;
    private int molCounterWritten;
    private OutputStream out = null;
    private IOType outType;
    private StopWatch watch;
    private MoleculeFileIO writer = null;

    //~ Constructors ///////////////////////////////////////////////////////////

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
    public AbstractReaderWriterPipe(String[] args) throws IOException
    {
        initByCommandLine(args);
    }

    /**
     * Creates a simple reader/writer pipe where the file types are
     * resolved by the file extensions.
     *
     * @param inputFile input file
     * @param outputFile output file
     * @throws IOException input/output exception
     */
    public AbstractReaderWriterPipe(String inputFile, String outputFile)
        throws IOException
    {
        BasicIOType tmpOut = BasicMoleculeWriter.checkGetOutputType(outputFile);
        BasicIOType tmpIn = BasicReader.checkGetInputType(inputFile);
        init(new FileInputStream(inputFile), tmpIn,
            new FileOutputStream(outputFile), tmpOut);
    }

    /**
     * Creates a simple reader/writer pipe.
     *
     * @param inputFile input file
     * @param _inTypeString input type
     * @param outputFile output file
     * @param _outTypeString output type
     * @throws IOException input/output exception
     */
    public AbstractReaderWriterPipe(String inputFile, String _inTypeString,
        String outputFile, String _outTypeString) throws IOException
    {
        init(new FileInputStream(inputFile),
            BasicIOTypeHolder.instance().getIOType(
                _inTypeString.toUpperCase()), new FileOutputStream(outputFile),
            BasicIOTypeHolder.instance().getIOType(
                _outTypeString.toUpperCase()));
    }

    /**
     * Creates a simple reader/writer pipe.
     *
     * @param _in input stream
     * @param _inTypeString input type
     * @param _out output stream file
     * @param _outTypeString output type
     * @throws IOException input/output exception
     */
    public AbstractReaderWriterPipe(InputStream _in, String _inTypeString,
        OutputStream _out, String _outTypeString) throws IOException
    {
        init(_in,
            BasicIOTypeHolder.instance().getIOType(
                _inTypeString.toUpperCase()), _out,
            BasicIOTypeHolder.instance().getIOType(
                _outTypeString.toUpperCase()));
    }

    /**
     * Creates a simple reader/writer pipe.
     *
     * @param inStream input stream
     * @param __inType input type
     * @param outStream output stream file
     * @param __outType output type
     * @throws IOException input/output exception
     */
    public AbstractReaderWriterPipe(InputStream inStream, BasicIOType inType,
        OutputStream outStream, BasicIOType outType) throws IOException
    {
        init(inStream, inType, outStream, outType);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * The molecule which should be handled when {@link #readWriteNext()} is called.
     *
     * @param mol the molecule which must be handled.
     */
    public abstract void molecule2handle(Molecule mol);

    /**
     * Shows usage.
     */
    public abstract void showUsage();

    /**
     * Closes the reader/writer.
     *
     * @exception IOException          input/output exception
     */
    public void close() throws IOException
    {
        if (loader != null)
        {
            loader.closeReader();
        }

        if (writer != null)
        {
            writer.closeWriter();
        }
    }

    /**
     * Creates a simple reader/writer pipe.
     *
     * @param inStream       input file
     * @param inType   input type
     * @param outStream      output file
     * @param outType  output type
     * @throws IOException input/output exception
     */
    public void init(InputStream inStream, IOType inType,
        OutputStream outStream, IOType outType) throws IOException
    {
        this.inType = inType;

        if (inType == null)
        {
            //logger.error("Input type not defined.");
            throw new IOException("Input type not defined.");
        }

        this.outType = outType;

        if (outType == null)
        {
            //logger.error("Output type not defined.");
            throw new IOException("Output type not defined.");
        }

        try
        {
            in = inStream;
            loader = MoleculeFileHelper.getMolReader(in, inType);
            out = outStream;
            writer = MoleculeFileHelper.getMolWriter(out, outType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new IOException(
                "Can not get molecule reader/writer pipe instance.");
        }

        if (!loader.readable())
        {
            //            logger.error(inType.getRepresentation() + " is not readable.");
            //            logger.error("You're invited to write one !;-)");
            throw new IOException(inType.getRepresentation() +
                " is not readable.");
        }

        if (!writer.writeable())
        {
            //logger.error(outType.getRepresentation() + " is not writeable.");
            throw new IOException(outType.getRepresentation() +
                " is not writeable.");
        }

        watch = new StopWatch();
        molCounterLoaded = 0;
        molCounterWritten = 0;

        mol = new BasicConformerMolecule(inType, outType);
    }

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
    public void initByCommandLine(String[] args) throws IOException
    {
        BasicIOType inType = null;
        BasicIOType outType = null;
        String inputFile = null;
        String outputFile = null;
        String arg;

        for (int i = 0; i < args.length; i++)
        {
            arg = args[i];

            if (arg.startsWith("--help"))
            {
                showUsage();

                return;
            }
            else if (arg.startsWith("-?"))
            {
                showUsage();

                return;
            }
            else if (arg.startsWith("+v"))
            {
                VERBOSE = true;
            }
            else if (arg.startsWith("-v"))
            {
                VERBOSE = false;
            }
            else if (arg.startsWith("-i"))
            {
                String inTypeS = arg.substring(2);
                inType = BasicIOTypeHolder.instance().getIOType(inTypeS
                        .toUpperCase());

                if (inType == null)
                {
                    throw new IOException("Input type '" + inTypeS +
                        "' not defined.");
                }
            }
            else if (arg.startsWith("-o"))
            {
                String outTypeS = arg.substring(2);
                outType = BasicIOTypeHolder.instance().getIOType(outTypeS
                        .toUpperCase());

                if (outType == null)
                {
                    throw new IOException("Output type '" + outTypeS +
                        "' not defined.");
                }
            }
            else
            {
                if (inputFile == null)
                {
                    inputFile = arg;
                }
                else
                {
                    outputFile = arg;

                    if (outputFile.equalsIgnoreCase(inputFile))
                    {
                        throw new IOException("'" + inputFile + "' and '" +
                            outputFile + "' are the same file.");
                    }
                }
            }
        }

        if (inputFile == null)
        {
            showUsage();
            throw new IOException("No input file defined.");
        }

        if (outputFile == null)
        {
            showUsage();
            throw new IOException("No output file defined.");
        }

        if (outType == null)
        {
            outType = BasicMoleculeWriter.checkGetOutputType(outputFile);
        }

        if (inType == null)
        {
            inType = BasicReader.checkGetInputType(inputFile);
        }

        init(new FileInputStream(inputFile), inType,
            new FileOutputStream(outputFile), outType);
    }

    /**
     * Returns the last loaded molecule.
     *
     * @return the last loaded molecule
     */
    public Molecule loadedMolecule()
    {
        return mol;
    }

    /**
     * Returns the number of loaded molecules.
     *
     * @return the number of loaded molecules
     */
    public int moleculesLoaded()
    {
        return molCounterLoaded;
    }

    /**
     * Returns the number of written molecules.
     *
     * @return the number of written molecules
     */
    public int moleculesWritten()
    {
        return molCounterWritten;
    }

    /**
     * Reads/writes the next molecule and calls {@link #molecule2handle(Molecule)}.
     *
     * @return                         <tt>true</tt> if more molecules are available
     * @exception IOException          input/output exception
     * @exception MoleculeIOException  molecule parsing exception
     */
    public boolean readWriteNext() throws IOException, MoleculeIOException
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
                logger.info("... " + molCounterLoaded +
                    " molecules successful loaded in " + watch.getPassedTime() +
                    " ms.");
                logger.info("... " + molCounterLoaded +
                    " molecules successful written in " +
                    watch.getPassedTime() + " ms.");

                return false;
            }
            else
            {
                molCounterLoaded++;
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

            molecule2handle(mol);

            success = writer.write(mol);

            if (!success)
            {
                logger.info("... " + molCounterLoaded +
                    " molecules successful loaded in " + watch.getPassedTime() +
                    " ms.");
                logger.info("... " + molCounterLoaded +
                    " molecules successful written in " +
                    watch.getPassedTime() + " ms.");

                return false;
            }
            else
            {
                molCounterWritten++;
            }
        }
        catch (IOException ex)
        {
            throw ex;
        }

        if ((molCounterLoaded % 1000) == 0)
        {
            logger.info("... " + molCounterLoaded +
                " molecules successful loaded in " + watch.getPassedTime() +
                " ms.");
        }

        if ((molCounterWritten % 1000) == 0)
        {
            logger.info("... " + molCounterWritten +
                " molecules successful written in " + watch.getPassedTime() +
                " ms.");
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
