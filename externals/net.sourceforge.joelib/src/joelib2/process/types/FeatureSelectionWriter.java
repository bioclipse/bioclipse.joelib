///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: FeatureSelectionWriter.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:38 $
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
package joelib2.process.types;

import joelib2.feature.NativeValue;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;
import joelib2.io.PropertyWriter;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.PairData;

import joelib2.process.BasicProcess;
import joelib2.process.MoleculeProcessException;

import joelib2.util.BasicProperty;

import joelib2.util.iterator.PairDataIterator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Calling processor classes if the filter rule fits.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:38 $
 */
public class FeatureSelectionWriter extends BasicProcess
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            FeatureSelectionWriter.class.getName());

    /**
     *  Description of the Field
     */
    public final static int DESCRIPTORS = 0;

    /**
     *  Description of the Field
     */
    public final static int MOL_AND_DESCRIPTORS = 1;

    /**
     *  Description of the Field
     */
    private final static int DEFAULT_DESC_OTYPE = MOL_AND_DESCRIPTORS;

    //~ Instance fields ////////////////////////////////////////////////////////

    private String commentString;
    private String delimiterString;
    private boolean descNamesChecked;

    //  private final static  JOEProperty[]  ACCEPTED_PROPERTIES    = new JOEProperty[]{
    //      new JOEProperty("SKIP_WRITER", "joelib2.io.MoleculeFileType", "Writer for skipped molecule entries.", true),
    //      new JOEProperty("DELIMITER", "java.lang.String", "Delimiter between descriptors in flat mode.", true),
    //      new JOEProperty("COMMENT", "java.lang.String", "Comment character of the first line in flat mode.", true)
    //      };
    private int descOutputType = DEFAULT_DESC_OTYPE;
    private List descriptorNames;
    private boolean firstLineWritten;
    private int molCounter;
    private PrintStream outStream;

    private BasicIOType outType;
    private MoleculeFileIO outWriter;
    private int skipCounter;
    private PrintStream skipStream;
    private BasicIOType skipType;
    private MoleculeFileIO skipWriter;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescSelectionWriter object
     */
    public FeatureSelectionWriter()
    {
        clear();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean clear()
    {
        descriptorNames = null;
        firstLineWritten = false;
        descNamesChecked = false;
        molCounter = 0;
        skipCounter = 0;
        commentString = "";
        delimiterString = " ";

        return true;
    }

    /**
     *  Gets the comment attribute of the DescSelectionWriter object
     *
     * @return    The comment value
     */
    public String getComment()
    {
        return commentString;
    }

    /**
     *  Gets the delimiter attribute of the DescSelectionWriter object
     *
     * @return    The delimiter value
     */
    public String getDelimiter()
    {
        return delimiterString;
    }

    /**
     *  Description of the Method
     *
     * @param  os             Description of the Parameter
     * @param  _outType       Description of the Parameter
     * @exception  Exception  Description of the Exception
     */
    public void init(OutputStream os, BasicIOType _outType) throws Exception
    {
        init(os, _outType, null, DEFAULT_DESC_OTYPE);
    }

    /**
     *  Description of the Method
     *
     * @param  _outputFile    Description of the Parameter
     * @param  _outType       Description of the Parameter
     * @exception  Exception  Description of the Exception
     */
    public void init(String _outputFile, BasicIOType _outType) throws Exception
    {
        init(_outputFile, _outType, null, DEFAULT_DESC_OTYPE);
    }

    /**
     *  Description of the Method
     *
     * @param  _outputFile       Description of the Parameter
     * @param  _outType          Description of the Parameter
     * @param  _descriptorNames  Description of the Parameter
     * @param  _descOutputType   Description of the Parameter
     * @exception  Exception     Description of the Exception
     */
    public void init(String _outputFile, BasicIOType _outType,
        List _descriptorNames, int _descOutputType) throws Exception
    {
        // initialize output stream
        init(new FileOutputStream(_outputFile), _outType, _descriptorNames,
            _descOutputType);

        // set file where skipped entries should be stored.
        String skipFile;
        int index = _outputFile.lastIndexOf(".");

        if (index == -1)
        {
            skipFile = _outputFile + "_skip";
        }
        else
        {
            skipFile = _outputFile.substring(0, index) + "_skip.sdf";
        }

        setSkipStream(new FileOutputStream(skipFile),
            BasicIOTypeHolder.instance().getIOType("SDF"));
    }

    /**
     *  Description of the Method
     *
     * @param  os                Description of the Parameter
     * @param  _outType          Description of the Parameter
     * @param  _descriptorNames  Description of the Parameter
     * @param  _descOutputType   Description of the Parameter
     * @exception  Exception     Description of the Exception
     */
    public void init(OutputStream os, BasicIOType _outType,
        List _descriptorNames, int _descOutputType) throws Exception
    {
        if (os instanceof PrintStream)
        {
            outStream = (PrintStream) os;
        }
        else
        {
            outStream = new PrintStream(os);
        }

        outType = _outType;

        if ((_descriptorNames != null) && (_descriptorNames.size() == 0))
        {
            logger.warn("No descriptors for writing defined in " +
                this.getClass().getName());
        }

        descriptorNames = _descriptorNames;
        descOutputType = _descOutputType;

        // initialize molecle writer
        try
        {
            outWriter = MoleculeFileHelper.getMolWriter(outStream, outType);
        }
        catch (Exception ex)
        {
            throw ex;
        }

        if (!outWriter.writeable())
        {
            throw new Exception(outType.getRepresentation() +
                " is not writeable.\n" + "You're invited to write one !;-)");
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public BasicProperty[] neededProperties()
    {
        //    return ACCEPTED_PROPERTIES;
        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  properties               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException
    {
        try
        {
            super.process(mol, properties);
        }
        catch (MoleculeProcessException e)
        {
            throw new MoleculeProcessException("Properties for " +
                this.getClass().getName() + " not correct.");
        }

        //    System.out.println("processing:::"+mol.getTitle());
        if (!descNamesChecked)
        {
            checkDescriptorNames(mol);

            if (logger.isDebugEnabled())
            {
                logger.debug("Descriptor names were checked.");
            }
        }

        PairData pairData;

        try
        {
            // write a descriptor name line at the beginning
            if (!firstLineWritten)
            {
                firstLineWritten = true;

                if (descOutputType == DESCRIPTORS)
                {
                    writeFirstLine();
                }
            }

            if (descOutputType == DESCRIPTORS)
            {
                int size = descriptorNames.size();

                for (int i = 0; i < size; i++)
                {
                    // get parsed data
                    pairData = mol.getData((String) descriptorNames.get(i),
                            true);

                    //          System.out.println("write:::"+genericData);
                    if (pairData == null)
                    {
                        logger.warn((String) descriptorNames.get(i) +
                            " data entry don't exist in molecule (#" +
                            (molCounter + 1) + "): " + mol.getTitle());

                        try
                        {
                            skipWriter.write(mol);
                            skipCounter++;
                        }
                        catch (MoleculeIOException ex)
                        {
                            ex.printStackTrace();
                            logger.error("Could not write skipped files.");
                        }

                        break;
                    }
                    else
                    {
                        if (pairData instanceof NativeValue)
                        {
                            outStream.print(((NativeValue) pairData)
                                .getStringNV());
                            outStream.print(delimiterString);
                        }
                        else
                        {
                            outStream.print((String) pairData.toString());
                            outStream.print(delimiterString);
                            logger.warn("Descriptor " + descriptorNames.get(i) +
                                " seems not to be a native type (int, double, atom property int, ...).");
                        }
                    }
                }

                outStream.println();

                // increase counter for succesfull written molecules
                molCounter++;
            }
            else if (descOutputType == MOL_AND_DESCRIPTORS)
            {
                // write molecule with descriptors
                boolean success = false;

                //        System.out.println("write mol and desc: "+JOEHelper.hasInterface(outWriter, "PropertyWriter"));
                if (outWriter instanceof PropertyWriter)
                {
                    try
                    {
                        success = ((PropertyWriter) outWriter).write(mol, null,
                                true, descriptorNames);
                    }
                    catch (MoleculeIOException ex)
                    {
                        //ex.printStackTrace();
                        throw new MoleculeProcessException(
                            "Could not write skipped files. " + ex.toString());
                    }
                }

                if (success)
                {
                    // increase counter for succesfull written molecules
                    molCounter++;
                }
                else
                {
                    try
                    {
                        skipWriter.write(mol);
                        skipCounter++;
                    }
                    catch (MoleculeIOException ex)
                    {
                        //                                              ex.printStackTrace();
                        //                                              logger.error("Could not write skipped files.");
                        throw new MoleculeProcessException(
                            "Could not write skipped files. " + ex.toString());
                    }
                }
            }
        }
        catch (IOException ex)
        {
            //                  ex.printStackTrace();
            throw new MoleculeProcessException(ex.toString());

            //                  return false;
        }

        return true;
    }

    /**
     *  Sets the comment attribute of the DescSelectionWriter object
     *
     * @param  _comment  The new comment value
     */
    public void setComment(String _comment)
    {
        commentString = _comment;
    }

    /**
     *  Sets the delimiter attribute of the DescSelectionWriter object
     *
     * @param  _delimiter  The new delimiter value
     */
    public void setDelimiter(String _delimiter)
    {
        delimiterString = _delimiter;
    }

    /**
     *  Sets the skipStream attribute of the DescSelectionWriter object
     *
     * @param  _skipStream    The new skipStream value
     * @param  _skipType      The new skipStream value
     * @exception  Exception  Description of the Exception
     */
    public void setSkipStream(OutputStream _skipStream, BasicIOType _skipType)
        throws Exception
    {
        if (_skipStream instanceof PrintStream)
        {
            skipStream = (PrintStream) _skipStream;
        }
        else
        {
            skipStream = new PrintStream(_skipStream);
        }

        skipType = _skipType;

        // initialize molecule skip writer
        try
        {
            skipWriter = MoleculeFileHelper.getMolWriter(skipStream, skipType);
        }
        catch (Exception ex)
        {
            throw ex;
        }

        if (!skipWriter.writeable())
        {
            throw new Exception(skipType.getRepresentation() +
                " is not writeable.\n" + "You're invited to write one !;-)");
        }
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    private void checkDescriptorNames(Molecule mol)
    {
        //System.out.println("Check descriptor names");
        if (!descNamesChecked)
        {
            descNamesChecked = true;
        }
        else
        {
            return;
        }

        if (descriptorNames == null)
        {
            descriptorNames = new Vector(20);

            PairDataIterator gdit = mol.genericDataIterator();
            PairData pairData;

            while (gdit.hasNext())
            {
                pairData = gdit.nextPairData();
                descriptorNames.add(pairData.getKey());
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  mol              Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    private void writeFirstLine() throws IOException
    {
        //already called
        //    if(!descNamesChecked) checkDescriptorNames(mol);
        // write first line
        // write only defined descriptors
        outStream.print(commentString);

        //      outStream.print( delimiterString );
        for (int i = 0; i < descriptorNames.size(); i++)
        {
            outStream.print((String) descriptorNames.get(i));
            outStream.print(delimiterString);
        }

        outStream.println();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
