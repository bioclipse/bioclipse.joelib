///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolconnZ.java,v $
//  Purpose:  Molconn-Z file format support.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
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

import cformat.PrintfStream;

import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.BasicResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * <a href="http://www.edusoft-lc.com/molconn/">Molconn-Z</a> file format support.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:34 $
 */
public class MolconnZ implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.MolconnZ");
    private final static String description = "MolconnZ result file";
    private final static String[] extensions = new String[]{"s"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private List descLines;
    private long lineCounter;
    private int linesRemaining;

    private LineNumberReader lnr;
    private PrintfStream ps;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Smiles object
     */
    public MolconnZ()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    public void closeReader() throws IOException
    {
        lnr.close();
    }

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    public void closeWriter() throws IOException
    {
        ps.close();
    }

    public boolean initializeParser()
    {
        String value;

        if ((value = BasicPropertyHolder.instance().getProperty(this,
                            "parserDefinition")) == null)
        {
            logger.error("Parser description for Molconn-Z file not defined.");

            return false;
        }

        descLines = BasicResourceLoader.readLines(value);

        if (descLines == null)
        {
            logger.error("File with parser description could not be found.");

            return false;
        }

        int size = descLines.size();
        Vector descs;

        for (int i = 0; i < size; i++)
        {
            // parse descriptor lines
            descs = new Vector();

            //      System.out.println("line "+i+"("+size+"):"+descLines.get(i));
            HelperMethods.tokenize(descs, (String) descLines.get(i), " \t\n\r");
            descLines.set(i, descs);
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  is               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    public void initReader(InputStream is) throws IOException
    {
        lnr = new LineNumberReader(new InputStreamReader(is));

        if (!initializeParser())
        {
            throw new IOException("Could not open parser information.");
        }

        lineCounter = 0;
    }

    /**
     *  Description of the Method
     *
     * @param  os               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    public void initWriter(OutputStream os) throws IOException
    {
        ps = new PrintfStream(os);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String inputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String[] inputFileExtensions()
    {
        return extensions;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String outputDescription()
    {
        return null;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String[] outputFileExtensions()
    {
        return null;
    }

    /**
     *  Reads an molecule entry as  (unparsed) <tt>String</tt> representation.
     *
     * @param  mol                        the molecule to store the data
     * @return                            <tt>null</tt> if the reader contains no
     *      more relevant data. Otherwise the <tt>String</tt> representation
     *      of the whole molecule entry is returned.
     * @exception  IOException            typical IOException
     */
    public String read() throws IOException
    {
        int s = descLines.size();
        StringBuffer sb = new StringBuffer(s * 100);
        linesRemaining = s;

        String line;

        for (int i = 1; i <= s; i++, linesRemaining--)
        {
            if ((line = lnr.readLine()) == null)
            {
                linesRemaining--;
                skipReaderEntry();

                return null;
            }

            sb.append(line);
            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     *  Description of the Method
     *
     * @param  mol              Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean read(Molecule mol) throws IOException, MoleculeIOException
    {
        return read(mol, null);
    }

    /**
     *  Loads an molecule in SMILES format and sets the title. If <tt>title</tt>
     *  is <tt>null</tt> the title line in the molecule file is used.
     *
     * @param  mol              Description of the Parameter
     * @param  title            Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol, String title)
        throws IOException, MoleculeIOException
    {
        String line;

        int s = descLines.size();
        linesRemaining = s;

        Vector descs;
        Vector diLine;
        int ds;

        for (int i = 1; i <= s; i++, linesRemaining--)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            lineCounter++;
            descs = (Vector) descLines.get(i - 1);
            diLine = new Vector(30);
            HelperMethods.tokenize(diLine, line, " \t\n\r");

            //System.out.println(""+lnr.getLineNumber()+":"+line);
            ds = descs.size();

            if ((i < 49) && (ds != diLine.size()))
            {
                linesRemaining--;
                skipReaderEntry();
                throw new MoleculeIOException("Line " + lineCounter + "(" + i +
                    ")" + " should contain " + ds + " descriptor values not " +
                    diLine.size() + ": " + line);
            }

            // take the real size of available descriptors
            // only needed for line 49 and 50
            ds = Math.min(diLine.size(), descs.size());

            for (int n = 0; n < ds; n++)
            {
                //System.out.println(descs.get(n));
                BasicPairData dp = new BasicPairData();
                dp.setKey((String) descs.get(n));
                dp.setKeyValue((String) diLine.get(n));
                mol.addData(dp);
            }

            diLine = null;
            line = null;
        }

        return (true);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean readable()
    {
        return true;
    }

    /**
     *  Description of the Method
     *
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean skipReaderEntry() throws IOException
    {
        for (int i = linesRemaining; i > 0; i--)
        {
            if (lnr.readLine() == null)
            {
                lineCounter++;

                return (false);
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  mol              Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException
    {
        return write(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol              Description of the Parameter
     * @param  title            Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title) throws IOException
    {
        return (true);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
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
