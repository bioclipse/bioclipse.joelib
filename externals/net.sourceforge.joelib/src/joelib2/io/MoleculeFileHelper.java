///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeFileHelper.java,v $
//  Purpose:  Factory class to get loader/writer classes.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:34 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Category;


/**
 * Factory class to get loader/writer classes.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:34 $
 */
public class MoleculeFileHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            MoleculeFileHelper.class.getName());

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEFileFormat object
     */
    public MoleculeFileHelper()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the moleculeFileType attribute of the JOEFileFormat class
     *
     * @param  type                       Description of the Parameter
     * @return                            The moleculeFileType value
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public static MoleculeFileIO getMoleculeFileType(IOType type)
        throws MoleculeIOException
    {
        // try to load MoleculeFileType representation class
        MoleculeFileIO mfType = null;

        if (logger.isDebugEnabled())
        {
            logger.debug("Load MoleculeFileType: " + type.getRepresentation());
        }

        //System.out.println("Load MoleculeFileType: "+type.getRepresentation());
        try
        {
            mfType = (MoleculeFileIO) Class.forName(type.getRepresentation())
                                           .newInstance();
        }
        catch (ClassNotFoundException ex)
        {
            throw new MoleculeIOException(type.getRepresentation() +
                " not found.");
        }
        catch (InstantiationException ex)
        {
            //ex.printStackTrace();
            throw new MoleculeIOException(type.getRepresentation() +
                " can not be instantiated.");
        }
        catch (IllegalAccessException ex)
        {
            throw new MoleculeIOException(type.getRepresentation() +
                " can't be accessed.");
        }

        if (mfType == null)
        {
            throw new MoleculeIOException("MoleculeFileType class " +
                type.getRepresentation() + " does'nt exist.");

            //            System.exit(1);
        }

        return mfType;
    }

    /**
     *  Gets the molReader attribute of the JOEFileFormat class. <br>
     *  <tt>MoleculeFileType loader = JOEFileFormat.getMolReader(InputStream is,
     *  IOType type)<br>
     *  if (loader.readable())<br>
     *  {<br>
     *  &nbsp;&nbsp;boolean success = loader.read(is, mol, title);<br>
     *  &nbsp;&nbsp;return success;<br>
     *  }<br>
     *  else<br>
     *  {<br>
     *  &nbsp;&nbsp;logger.warn(type.getRepresentation() + " is not readable.");
     *  <br>
     *  &nbsp;&nbsp;return false;<br>
     *  }</tt>
     *
     * @param  is                         Description of the Parameter
     * @param  type                       Description of the Parameter
     * @return                            The molReader value
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public static MoleculeFileIO getMolReader(InputStream is, IOType type)
        throws IOException, MoleculeIOException
    {
        // try to load MoleculeFileType representation class
        MoleculeFileIO loader = getMoleculeFileType(type);

        if (loader != null)
        {
            loader.initReader(is);

            return loader;
        }
        else
        {
            return null;
        }
    }

    /**
     *  Description of the Method<br>
     *  <tt>MoleculeFileType saver = JOEFileFormat.getMolWrite(OutputStream os,
     *  IOType type)<br>
     *  if (saver.writeable())<br>
     *  {<br>
     *  boolean success = saver.write(os, mol, title);<br>
     *  return success;<br>
     *  }<br>
     *  else<br>
     *  {<br>
     *  logger.warn(type.getRepresentation() + " is not writeable.");<br>
     *  return false;<br>
     *  }</tt>
     *
     * @param  os                         Description of the Parameter
     * @param  type                       Description of the Parameter
     * @return                            Description of the Return Value
     * @exception  IOException            Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public static MoleculeFileIO getMolWriter(OutputStream os, IOType type)
        throws IOException, MoleculeIOException
    {
        // try to load MoleculeFileType representation class
        MoleculeFileIO saver = getMoleculeFileType(type);

        if (saver != null)
        {
            saver.initWriter(os);

            return saver;
        }
        else
        {
            return null;
        }
    }

    /**
     * Loads a single molecule from a file.
     *
     * @param inputFile the input file
     * @param type the input type
     * @return the loaded molecule
     * @throws IOException input/ouput exception
     * @throws MoleculeIOException molecule parsing exception
     *
     * @see joelib2.molecule.MoleculeVector
     */
    public static Molecule loadMolFromFile(String inputFile, String type)
        throws IOException, MoleculeIOException
    {
        return MoleculeFileHelper.loadMolFromFile(null, null, inputFile, type);
    }

    /**
     * Loads a single molecule from a file.
     *
     * @param loader the loader class (array with one element) for open molecule input stream
     * @param mol the molecule in which the first molecule will be stored
     * @param inputFile the input file
     * @param type the input type
     * @return the loaded molecule
     * @throws IOException input/ouput exception
     * @throws MoleculeIOException molecule parsing exception
     *
     * @see joelib2.molecule.MoleculeVector
     */
    public static Molecule loadMolFromFile(MoleculeFileIO[] loader,
        Molecule mol, String inputFile, String type) throws IOException,
        MoleculeIOException
    {
        if (inputFile == null)
        {
            //                  logger.error("No input file defined.");
            //                  return null;
            throw new IOException("No input file defined.");
        }

        BasicIOType inType = null;

        if ((loader == null) || ((loader != null) && (loader[0] == null)))
        {
            if (loader == null)
            {
                loader = new MoleculeFileIO[1];
            }

            if (type == null)
            {
                // try to resolve file extension
                inType = BasicReader.checkGetInputType(inputFile);
            }
            else
            {
                inType = BasicIOTypeHolder.instance().getIOType(type
                        .toUpperCase());
            }

            FileInputStream in = null;

            // get molecule loader/writer
            try
            {
                in = new FileInputStream(inputFile);

                if ((loader != null) && (loader[0] == null))
                {
                    loader[0] = getMolReader(in, inType);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if (!loader[0].readable())
        {
            //                  logger.error(inType.getRepresentation() + " is not readable.");
            //                  logger.error("You're invited to write one !;-)");
            //                  return null;
            throw new IOException(inType.getRepresentation() +
                " is not readable.");
        }

        if (mol == null)
        {
            mol = new BasicConformerMolecule(inType,
                    BasicIOTypeHolder.instance().getIOType("SMILES"));
        }
        else
        {
            mol.setInputType(inType);
        }

        boolean success = true;
        mol.clear();

        try
        {
            success = loader[0].read(mol);
        }
        catch (IOException ex)
        {
            //                  ex.printStackTrace();
            //                  logger.error(ex.toString());
            //                  return null;
            throw ex;
        }

        if (!success)
        {
            //                          logger.info("No molecule loaded");
            //                          return null;
            throw new IOException("File contains no valid molecule.");
        }

        if (mol.isEmpty())
        {
            logger.warn("Empty molecule loaded.");
        }

        return mol;
    }

    /**
     * Write a molecule to file.
     *
     * @param mol          the molecule to store
     * @param outputFile   the output file
     * @param type         the output type
     * @return             <tt>true</tt> if the molecule was written successfully
     * @throws IOException input/ouput exception
     * @throws MoleculeIOException molecule parsing exception
     *
     * @see joelib2.molecule.MoleculeVector
     */
    public static boolean saveMolFromFile(Molecule mol, String outputFile,
        String type) throws IOException, MoleculeIOException
    {
        return saveMolToFile(null, mol, outputFile, type);
    }

    /**
     * Write a molecule to file.
     *
     * @param writer       the writer class (array with one element) for open molecule output stream
     * @param mol          the molecule to store
     * @param outputFile   the output file
     * @param type         the output type
     * @return             <tt>true</tt> if the molecule was written successfully
     * @throws IOException input/ouput exception
     * @throws MoleculeIOException molecule parsing exception
     *
     * @see joelib2.molecule.MoleculeVector
     */
    public static boolean saveMolToFile(MoleculeFileIO[] writer, Molecule mol,
        String outputFile, String type) throws IOException, MoleculeIOException
    {
        boolean writerWasNotDefined = false;

        if (outputFile == null)
        {
            //                  logger.error("No output file defined.");
            //                  return false;
            throw new IOException("No output file defined.");
        }

        BasicIOType outType = null;

        if (mol == null)
        {
            //                  logger.error("No molecule defined.");
            //                  return false;
            throw new IOException("No molecule defined.");
        }

        if ((writer == null) || ((writer != null) && (writer[0] == null)))
        {
            writerWasNotDefined = true;

            if (writer == null)
            {
                writer = new MoleculeFileIO[1];
            }

            if (type == null)
            {
                // try to resolve file extension
                try
                {
                    outType = BasicMoleculeWriter.checkGetOutputType(
                            outputFile);
                }
                catch (IOException ioEx)
                {
                    if ((writer != null) && (writer[0] != null))
                    {
                        writer[0].closeWriter();
                    }

                    throw ioEx;
                }
            }
            else
            {
                outType = BasicIOTypeHolder.instance().getIOType(type
                        .toUpperCase());
            }

            FileOutputStream out = null;

            // get molecule loader/writer
            try
            {
                out = new FileOutputStream(outputFile);

                if ((writer != null) && (writer[0] == null))
                {
                    writer[0] = getMolWriter(out, outType);
                }
            }
            catch (Exception ex)
            {
                try
                {
                    if ((writer != null) && (writer[0] != null))
                    {
                        writer[0].closeWriter();
                    }
                }
                catch (Exception exception)
                {
                    throw new IOException(exception.getMessage());
                }

                throw new IOException(ex.getMessage());
            }
        }

        if (!writer[0].writeable())
        {
            if (writerWasNotDefined)
            {
                try
                {
                    if ((writer != null) && (writer[0] != null))
                    {
                        writer[0].closeWriter();
                    }
                }
                catch (Exception exception)
                {
                    throw new IOException(exception.getMessage());
                }
            }

            //                  logger.error(outType.getRepresentation() + " is not writeable.");
            //                  logger.error("You're invited to write one !;-)");
            //                  return false;
            throw new IOException(outType.getRepresentation() +
                " is not writeable.");
        }

        boolean success = false;

        try
        {
            success = writer[0].write(mol);
        }
        catch (IOException ex)
        {
            if (writerWasNotDefined)
            {
                if ((writer != null) && (writer[0] != null))
                {
                    writer[0].closeWriter();
                }
            }

            //                  ex.printStackTrace();
            //                  return false;
            throw ex;
        }

        if (!success)
        {
            if (writerWasNotDefined)
            {
                if ((writer != null) && (writer[0] != null))
                {
                    writer[0].closeWriter();
                }
            }

            //                          logger.error(mol.getTitle() + " was not saved successfully.");
            //                          return false;
            throw new IOException(mol.getTitle() +
                " was not saved successfully.");
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
