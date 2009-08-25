///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Flat.java,v $
//  Purpose:  Flat file format support.
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

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicPropertyHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Flat file format support.
 *
 * @.author     wegnerj
 * @.wikipedia  File format
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:34 $
 */
public class Flat implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.Flat");
    private final static String description =
        "Native descriptor flat file format";
    private final static String[] extensions =
        new String[]{"flat", "dat", "txt"};

    //~ Instance fields ////////////////////////////////////////////////////////

    public boolean firstLineLoaded = false;
    private String delimiter;
    private boolean firstLineWritten = false;
    private String inputDelim;
    private List lineStructure;
    private LineNumberReader lnr;
    private String outputDelim;
    private PrintfStream ps;
    private boolean storeLineInfo = true;
    private int titlePosition;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Smiles object
     */
    public Flat()
    {
        lineStructure = new Vector();
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

    /**
     *  Gets the storeLineInfo attribute of the Smiles object
     *
     * @return    The storeLineInfo value
     */
    public boolean getStoreLineInfo()
    {
        return storeLineInfo;
    }

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    public void initParser() throws IOException
    {
        Properties prop = BasicPropertyHolder.instance().getProperties();
        String className = this.getClass().getName();

        // use system properties if available
        String lineStructString = System.getProperty(className +
                ".lineStructure");

        if (lineStructString == null)
        {
            lineStructString = prop.getProperty(className + ".lineStructure",
                    "TITLE");
        }

        //System.out.println("pLINE:"+lineStructString);
        delimiter = prop.getProperty(className + ".lineStructure.delimiter",
                "|");

        //System.out.println("delimiter: '"+delimiter+"'");
        inputDelim = prop.getProperty(className +
                ".lineStructure.input.delimiter", " \t\n\r");

        //System.out.println("inputDelim:"+inputDelim);
        outputDelim = prop.getProperty(className +
                ".lineStructure.output.delimiter", "\t");

        //System.out.println("outputDelim:"+outputDelim);
        lineStructure.clear();
        HelperMethods.tokenize(lineStructure, lineStructString,
            delimiter + "\n\r");

        titlePosition = -1;

        for (int i = 0; i < lineStructure.size(); i++)
        {
            if (((String) lineStructure.get(i)).equals("TITLE"))
            {
                titlePosition = i;
            }

            //System.out.println("pSTRUCT("+i+"):"+lineStructure.get(i));
        }
    }

    /**
     *  Description of the Method
     *
     * @param  is               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    public synchronized void initReader(InputStream is) throws IOException
    {
        lnr = new LineNumberReader(new InputStreamReader(is));
        initParser();
        firstLineLoaded = false;
    }

    public synchronized void initReader(InputStream is,
        boolean _firstLineLoaded) throws IOException
    {
        lnr = new LineNumberReader(new InputStreamReader(is));
        initParser();
        firstLineLoaded = _firstLineLoaded;
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
        initParser();
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
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String[] outputFileExtensions()
    {
        return extensions;
    }

    public String parseFirstLineReadNext(String line) throws IOException
    {
        String nextLine;
        titlePosition = -1;
        lineStructure.clear();
        HelperMethods.tokenize(lineStructure, line, inputDelim + "\n\r");
        firstLineLoaded = true;

        // read first data line
        if ((nextLine = lnr.readLine()) == null)
        {
            logger.error("Flat file contains no data lines.");

            return null;
        }

        StringBuffer sb = new StringBuffer(100);

        for (int i = 0; i < lineStructure.size(); i++)
        {
            sb.append((String) lineStructure.get(i));

            //System.out.println(lineStructure.get(i) + " ");
            if (i < (lineStructure.size() - 1))
            {
                sb.append(delimiter);
            }

            if (((String) lineStructure.get(i)).equals("TITLE"))
            {
                titlePosition = i;
            }
        }

        Properties prop = BasicPropertyHolder.instance().getProperties();
        String className = this.getClass().getName();
        prop.setProperty(className + ".lineStructure", sb.toString());

        logger.info("Set flat file line structure to: " + sb.toString());

        return nextLine;
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
        String line;

        if ((line = lnr.readLine()) == null)
        {
            return null;
        }

        if (!firstLineLoaded)
        {
            line = parseFirstLineReadNext(line);
        }

        return line;
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
        String line = null;

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        // the first line contains the descriptor data information
        if (!firstLineLoaded)
        {
            line = parseFirstLineReadNext(line);
        }

        Vector data = new Vector();

        // of type String
        HelperMethods.tokenize(data, line, inputDelim);

        if (data.size() < lineStructure.size())
        {
            throw new MoleculeIOException("Line entry \"" + line +
                "\" in line " + lnr.getLineNumber() + " could not be loaded." +
                "Not enough arguments available.");
        }

        //        String setTitle;
        //        if (titlePosition == -1)
        //        {
        //            if (title == null)
        //            {
        //                setTitle = "";
        //            }
        //            else
        //            {
        //                setTitle = title;
        //            }
        //        }
        //        else
        //        {
        //            setTitle = (String) data.get(titlePosition);
        //        }
        for (int i = 0; i < lineStructure.size(); i++)
        {
            //      System.out.println("");
            if (i != titlePosition)
            {
                //      System.out.println("i:"+lineStructure.get(i));
                if (i < data.size())
                {
                    BasicPairData dp = new BasicPairData();
                    dp.setKey((String) lineStructure.get(i));
                    dp.setKeyValue((String) data.get(i));
                    mol.addData(dp);

                    //System.out.println("mol.add: "+lineStructure.get(i)+" "+data.get(i));
                }
                else
                {
                    //        logger.error("Entry \""+(String)lineStructure.get(i)+"\" does not exist in line "+lnr.getLineNumber()+".");
                    throw new IOException("Entry \"" +
                        (String) lineStructure.get(i) +
                        "\" does not exist in line " + lnr.getLineNumber() +
                        ".");
                }
            }
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
     *  Sets the storeLineInfo attribute of the Smiles object
     *
     * @param  _flag  The new storeLineInfo value
     */
    public void setStoreLineInfo(boolean _flag)
    {
        storeLineInfo = _flag;
    }

    /**
     *  Description of the Method
     *
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean skipReaderEntry() throws IOException
    {
        //SMILES line was already readed.
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
        // save line structure information
        if (storeLineInfo && !firstLineWritten)
        {
            //ps.print('#');
            for (int i = 0; i < lineStructure.size(); i++)
            {
                if (i != 0)
                {
                    ps.print(delimiter);
                }

                ps.print((String) lineStructure.get(i));
            }

            ps.println();
            firstLineWritten = true;
        }

        // save SMILES line and properties
        for (int i = 0; i < lineStructure.size(); i++)
        {
            if (i != 0)
            {
                ps.print(outputDelim);
            }

            if (i == titlePosition)
            {
                String printTitle = title;

                if (printTitle == null)
                {
                    printTitle = mol.getTitle();
                }

                if (printTitle.trim().equals(""))
                {
                    printTitle = "Undefined";
                }

                ps.print(printTitle);
            }
            else
            {
                PairData pairData = mol.getData((String) lineStructure.get(i));

                if (pairData == null)
                {
                    logger.warn("Descriptor entry '" + lineStructure.get(i) +
                        "' not found in " + mol.getTitle());
                    logger.warn("Writing NaN");
                    ps.print("NaN");
                }
                else
                {
                    ps.print(pairData.toString(
                            BasicIOTypeHolder.instance().getIOType("FLAT")));
                }
            }
        }

        ps.println();

        return (true);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean writeable()
    {
        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
