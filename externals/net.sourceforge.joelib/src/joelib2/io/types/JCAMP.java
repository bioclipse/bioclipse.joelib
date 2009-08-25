///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: JCAMP.java,v $
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

import joelib2.jcamp.JCAMPData;
import joelib2.jcamp.JCAMPDataBlock;

import joelib2.molecule.Molecule;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import org.apache.log4j.Category;


/**
 *  A class to interpret JCAMP-DX data (JCAMP-CS is not implemented yet). The
 *  supported data types are XYPAIRS, XYDATA=(X++(Y..Y)), PEAK TABLE and LINK.
 *  <br>
 *  If you want load a file with multiple blocks or inner blocks you must use
 *  <code>JCampMultipleFile</code>.<br>
 *  This class can only load separated single blocks with one TITLE and END
 *  label !<br>
 *  <br>
 *
 *  <ul>
 *    <li> ... The International Union of Pure and Applied Chemistry (IUPAC)
 *    took over responsibility from the Joint Commitee on Atomic and Molecular
 *    Physical Data (JCAMP) in 1995 ...<br>
 *    <a href="http://jcamp.isas-dortmund.de/">I U P A C<br>
 *    Committee on Printed and Electronic Publications <br>
 *    Working Party on Spectroscopic Data Standards (JCAMP-DX)</a> <br>
 *    <br>
 *
 *    <li> <a href="http://wwwchem.uwimona.edu.jm:1104/software/jcampdx.html">
 *    The Department of Chemistry at the University of the West Indies</a>
 *  </ul>
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:34 $
 * @.cite dl93
 * @.cite dw88
 * @.cite ghhjs91
 * @.cite lhdl94
 * @.cite dhl90
 * @see joelib2.jcamp.JCAMPParser
 */
public class JCAMP implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.JCAMP");
    private final static String description =
        "Joint Commitee on Atomic and Molecular Physical Data (JCAMP)";
    private final static String[] extensions = new String[]{"jdx", "dx", "cs"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private JCAMPData jcamp;

    // data type is chemical structure
    private final String JCAMP_CHEMICAL_STRUCTURE = "JCAMP-CS";

    // data type is spectra
    private final String JCAMP_SPECTRA = "JCAMP-DX";

    // marks start of data
    private final String JCAMP_START = "TITLE";

    // marks end of data
    private final String JCAMP_STOP = "END";
    private int jcampCount;

    // ... is link block
    private final String LINK_TYPE = "LINK";

    // data type ...
    private final String LINK_TYPE_DATA = "DATA TYPE";

    //    private String textBlock;
    //    private StringTokenizer textTokenizer = null;
    private LineNumberReader lnr;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Undefined object
     */
    public JCAMP()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     *@exception  IOException  Description of the Exception
     */
    public void closeReader() throws IOException
    {
    }

    /**
     *  Description of the Method
     *
     *@exception  IOException  Description of the Exception
     */
    public void closeWriter() throws IOException
    {
    }

    public String getJCAMPData()
    {
        return (String) jcamp.getDXEntry(0).getBlockData();
    }

    /**
     *  Description of the Method
     *
     *@param  is               Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public void initReader(InputStream is) throws IOException
    {
        lnr = new LineNumberReader(new InputStreamReader(is));
        jcampCount = 0;
    }

    /**
     *  Description of the Method
     *
     *@param  os               Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public void initWriter(OutputStream os) throws IOException
    {
        //ps = new PrintfStream(os);
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String inputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String[] inputFileExtensions()
    {
        return extensions;
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String outputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
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
            "Reading JCAMP data as String representation is not implemented yet !!!");

        return null;
    }

    /**
     *  Description of the Method
     *
     *@param  mol              Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public boolean read(Molecule mol) throws IOException
    {
        return read(mol, null);
    }

    /**
     *  Description of the Method
     *
     *@param  mol              Description of the Parameter
     *@param  title            Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public boolean read(Molecule mol, String title) throws IOException
    {
        // start parsing
        jcamp = new JCAMPData();

        JCAMPDataBlock dataBlock = null;

        do
        {
            jcampCount++;
            dataBlock = resolveJCAMPBlocks(0, null, jcampCount);
            addDataBlock(dataBlock);
        }
        while (dataBlock != null);

        return (false);
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public boolean readable()
    {
        return true;
    }

    public boolean skipReaderEntry() throws IOException
    {
        return skipReaderEntry(1);
    }

    /**
     *  Description of the Method
     *
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public boolean skipReaderEntry(int actualDepth) throws IOException
    {
        String line;
        int depth = 0;

        while ((line = lnr.readLine()) != null)
        {
            if ((line.length() > 0) && (line.charAt(0) == '#') &&
                    (line.indexOf(JCAMP_STOP) != -1))
            {
                depth++;
            }

            if (depth == actualDepth)
            {
                break;
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     *@param  mol              Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException
    {
        return write(mol, null);
    }

    /**
     *  Description of the Method
     *
     *@param  mol              Description of the Parameter
     *@param  title            Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title) throws IOException
    {
        return (false);
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public boolean writeable()
    {
        return false;
    }

    /**
     *  Adds a JCAMP block.
     *
     *@param  block  The feature to be added to the DataBlock attribute
     *@return        Description of the Return Value
     */
    private boolean addDataBlock(JCAMPDataBlock block)
    {
        if (block != null)
        {
            if (block.getBlockType() == JCAMPDataBlock.CS_TYPE)
            {
                jcamp.addCSEntry(block);

                //                System.out.println("CS_ID: " + block.getBlockID());
                //                System.out.println("CS:\n" + block.getBlockData());
            }
            else if (block.getBlockType() == JCAMPDataBlock.DX_TYPE)
            {
                jcamp.addDXEntry(block);

                //                System.out.println("DX_ID: " + block.getBlockID());
                //                System.out.println("DX:\n" + block.getBlockData());
            }
            else if (block.getBlockType() == JCAMPDataBlock.LINK_TYPE)
            {
                jcamp.addLinkEntry(block);

                //                System.out.println("LINK_ID: " + block.getBlockID());
                //                System.out.println("LINK:\n" + block.getBlockData());
            }
        }

        return true;
    }

    /*
     *  -------------------------------------------------------------------------*
     *  private methods
     *  -------------------------------------------------------------------------
     */

    /**
     *  Gets the single JCAMP data blocks.
     *
     *@param  _depth           Description of the Parameter
     *@param  previousLine     Description of the Parameter
     *@param  blockID          Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    private JCAMPDataBlock resolveJCAMPBlocks(int _depth, String previousLine,
        int blockID) throws IOException
    {
        // now parse the whole file contents
        String label = null;
        String data = null;
        StringBuffer tempBuffer = new StringBuffer(20000);
        int depth = _depth;
        JCAMPDataBlock dataBlock = new JCAMPDataBlock();

        boolean firstLabelTreated = false;
        String nextLine = null;
        boolean goOn = true;

        //      while (textTokenizer.hasMoreTokens())
        while (goOn)
        {
            if (firstLabelTreated || (previousLine == null))
            {
                // get next line
                nextLine = lnr.readLine();

                if (nextLine == null)
                {
                    goOn = false;

                    continue;
                }

                //          nextLine = textTokenizer.nextToken();
                // jump over empty lines
                if (nextLine.equals(""))
                {
                    continue;
                }

                nextLine = JCAMPDataBlock.removeCommentsInLine(nextLine);
            }
            else
            {
                nextLine = previousLine;
            }

            //is label line or data line?
            if ((nextLine.charAt(0) == '#') && (nextLine.charAt(1) == '#'))
            {
                // try to determine the data type of one TITLE ... END data set
                label = JCAMPDataBlock.getLabelInLine(nextLine);

                if (label != null)
                {
                    // is LINK type ?
                    if (label.equals(LINK_TYPE_DATA))
                    {
                        data = JCAMPDataBlock.getDataInLine(nextLine);

                        if ((data != null) && data.equals(LINK_TYPE))
                        {
                            // overwrite DX or CS type already defined WITHOUT WARNING
                            dataBlock.setBlockType(JCAMPDataBlock.LINK_TYPE);
                        }

                        //else
                        //{
                        //    // check for IR, MS, UV - spectra type ... if you want ...
                        //    // and store it as 'DATA TYPE'
                        //}
                    }

                    // is CS type ?
                    else if (label.equals(JCAMP_CHEMICAL_STRUCTURE))
                    {
                        // set only if undefined
                        if (dataBlock.getBlockType() ==
                                JCAMPDataBlock.UNDEFINED_TYPE)
                        {
                            dataBlock.setBlockType(JCAMPDataBlock.CS_TYPE);
                        }
                    }

                    // is DX type ?
                    else if (label.equals(JCAMP_SPECTRA))
                    {
                        // set only if undefined
                        if (dataBlock.getBlockType() ==
                                JCAMPDataBlock.UNDEFINED_TYPE)
                        {
                            dataBlock.setBlockType(JCAMPDataBlock.DX_TYPE);
                        }
                    }

                    // nested data block ?
                    else if (label.equals(JCAMP_START))
                    {
                        depth++;

                        if (depth == 2)
                        {
                            JCAMPDataBlock nestedDataBlock;
                            nestedDataBlock = resolveJCAMPBlocks(depth,
                                    nextLine, blockID);
                            addDataBlock(nestedDataBlock);
                            depth--;

                            continue;
                        }
                        else if (depth > 3)
                        {
                            return null;
                        }
                    }

                    // finsh data block
                    else if (label.equals(JCAMP_STOP))
                    {
                        // add uncommented data to buffer
                        tempBuffer.append(nextLine + "\n");
                        dataBlock.setBlockData(tempBuffer.toString(), depth);
                        tempBuffer.delete(0, tempBuffer.length());
                        dataBlock.setBlockID(blockID);

                        return dataBlock;
                    }
                }
            }

            // add uncommented data to buffer
            tempBuffer.append(nextLine + "\n");

            firstLabelTreated = true;
        }

        return null;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
