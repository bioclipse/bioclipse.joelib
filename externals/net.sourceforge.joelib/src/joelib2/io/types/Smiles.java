///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Smiles.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
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
package joelib2.io.types;

import cformat.PrintfStream;

import joelib2.algo.morgan.Morgan;
import joelib2.algo.morgan.types.BasicTieResolver;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.smiles.SMILESGenerator;
import joelib2.smiles.SMILESParser;

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
 *  Reader/Writer for Simplified Molecular Input Line Entry System (SMILES) files.
 *
 * <p>
 * The SMILES import/export has some additional parameters which were
 * defined in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file for default.
 *
 * <p>
 * <table width="100%" border="1">
 *  <tr>
 *    <td><b>Java property</b></td>
 *    <td><b>Description</b></td>
 *    <td><b>Default value</b></td>
 *  </tr>
 *  <tr>
 *    <td>joelib2.io.types.Smiles.lineStructure</td>
 *    <td>Line structure of the SMILES in the file/inputstream. SMILES and TITLE are
 * fixed code words, which represents the SMILES molecule and the molecule title.
 * All other descriptor names, which represents native descriptor values, are allowed.
 * </td>
 *    <td>SMILES|TITLE</td>
 *  </tr>
 *  <tr>
 *    <td>joelib2.io.types.Smiles.lineStructure.delimiter</td>
 *    <td>The delimiter used in the line structure definition.</td>
 *    <td>|</td>
 *  </tr>
 *  <tr>
 *    <td>joelib2.io.types.Smiles.lineStructure.input.delimiter</td>
 *    <td>The input delimiter in the file/inputstream between line entries.</td>
 *    <td>\&nbsp;\t\n\r</td>
 *  </tr>
 * <tr>
 *    <td>joelib2.io.types.Smiles.lineStructure.output.delimiter</td>
 *    <td>The output delimiter in the file/outputstream between line entries.</td>
 *    <td>\&nbsp;\</td>
 *  </tr>
 *</table>
 *
 * @.author     wegnerj
 * @.wikipedia  Simplified molecular input line entry specification
 * @.wikipedia  Chemical file format
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:34 $
 * @.cite smilesFormat
 * @.cite wei88
 * @.cite www89
 * @see wsi.ra.tool.BasicPropertyHolder
 * @see wsi.ra.tool.BasicResourceLoader
 */
public class Smiles implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.Smiles");
    private final static String description =
        "Simplified Molecular Input Line Entry System (SMILES)";
    private final static String[] extensions = new String[]{"smi", "smiles"};
    private static final Morgan morgan = new Morgan(new BasicTieResolver());

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean canonical = false;
    private String delimiter;
    private String inputDelim;
    private List lineStructure;

    private LineNumberReader lnr;
    private String outputDelim;
    private PrintfStream ps;
    private int smilesPosition;
    private boolean storeLineInfo = false;
    private int titlePosition;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Smiles object
     */
    public Smiles()
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
        String lineStructString = prop.getProperty(className + ".lineStructure",
                "SMILES|TITLE");
        delimiter = prop.getProperty(className + ".lineStructure.delimiter",
                "|");
        inputDelim = prop.getProperty(className +
                ".lineStructure.input.delimiter", " \t\n\r");
        outputDelim = prop.getProperty(className +
                ".lineStructure.output.delimiter", " ");

        lineStructure.clear();
        HelperMethods.tokenize(lineStructure, lineStructString,
            delimiter + "\n\r");

        smilesPosition = -1;
        titlePosition = -1;

        for (int i = 0; i < lineStructure.size(); i++)
        {
            if (((String) lineStructure.get(i)).equals("SMILES"))
            {
                smilesPosition = i;
            }
            else if (((String) lineStructure.get(i)).equals("TITLE"))
            {
                titlePosition = i;
            }

            if (logger.isDebugEnabled())
            {
                logger.debug("SMILES line entry " + i + " is : " +
                    lineStructure.get(i));
            }
        }

        if (smilesPosition == -1)
        {
            //        logger.error("You must define a SMILES entry in the joelib2.SMILES.lineStructure property.");
            throw new IOException(
                "You must define a SMILES entry in the joelib2.SMILES.lineStructure property.");
        }

        String value = null;
        value = BasicPropertyHolder.instance().getProperty(this, "canonical");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            canonical = true;
        }
        else
        {
            canonical = false;
        }
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
        initParser();
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
        return lnr.readLine();
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

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        Vector data = new Vector();

        // of type String
        HelperMethods.tokenize(data, line, inputDelim);

        if (data.size() < lineStructure.size())
        {
            StringBuffer sb = new StringBuffer(lineStructure.size() * 10);

            for (int i = 0; i < lineStructure.size(); i++)
            {
                sb.append(lineStructure.get(i));

                if (i < (lineStructure.size() - 1))
                {
                    sb.append(inputDelim.charAt(0));
                }
            }

            throw new MoleculeIOException("SMILE line entry \"" + line +
                "\" in line " + lnr.getLineNumber() + " could not be loaded." +
                " Not enough arguments available (" + sb + ").");
        }

        //        for (int i = 0; i < data.size(); i++)
        //        {
        //            System.out.print(data.get(i)+" ");
        //        }
        //        System.out.println("");
        String setTitle;

        if (titlePosition == -1)
        {
            if (title == null)
            {
                setTitle = "";
            }
            else
            {
                setTitle = title;
            }
        }
        else
        {
            setTitle = (String) data.get(titlePosition);
        }

        if (!SMILESParser.smiles2molecule(mol,
                    (String) data.get(smilesPosition), setTitle))
        {
            //logger.error("SMILE entry \"" + (String) data.get(smilesPosition) + "\" in line " + lnr.getLineNumber() + " could not be loaded.");
            skipReaderEntry();
            throw new MoleculeIOException("SMILES entry \"" +
                (String) data.get(smilesPosition) + "\" in line " +
                lnr.getLineNumber() + " could not be loaded.");

            //return false;
        }

        for (int i = 0; i < lineStructure.size(); i++)
        {
            if ((i != smilesPosition) && (i != titlePosition))
            {
                if (i < data.size())
                {
                    BasicPairData dp = new BasicPairData();
                    dp.setKey((String) lineStructure.get(i));
                    dp.setKeyValue((String) data.get(i));
                    mol.addData(dp);
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
        SMILESGenerator m2s = new SMILESGenerator();

        m2s.init();

        StringBuffer smiles = new StringBuffer(1000);

        if (canonical)
        {
            Molecule tMol = (Molecule) mol.clone(false);

            morgan.calculate(tMol);

            Molecule rMol = morgan.renumber(tMol);
            m2s.correctAromaticAmineCharge(rMol);
            m2s.createSmiString(rMol, smiles);
        }
        else
        {
            m2s.correctAromaticAmineCharge(mol);
            m2s.createSmiString(mol, smiles);
        }

        // save line structure information
        if (storeLineInfo)
        {
            ps.print('#');

            for (int i = 0; i < lineStructure.size(); i++)
            {
                if (i != 0)
                {
                    ps.print(delimiter);
                }

                ps.print((String) lineStructure.get(i));
            }

            ps.println();
        }

        // save SMILES line and properties
        for (int i = 0; i < lineStructure.size(); i++)
        {
            if (i != 0)
            {
                ps.print(outputDelim);
            }

            if (i == smilesPosition)
            {
                ps.print(smiles.toString());
            }
            else if (i == titlePosition)
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
                    logger.warn("Writing 0");
                    ps.print("0");
                }
                else
                {
                    ps.print(pairData.toString(
                            BasicIOTypeHolder.instance().getIOType("SMILES")));
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
