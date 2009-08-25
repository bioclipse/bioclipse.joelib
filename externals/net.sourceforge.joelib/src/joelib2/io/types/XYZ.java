///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: XYZ.java,v $
//  Purpose:  Reader/Writer for XYZ files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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

import cformat.PrintfFormat;
import cformat.PrintfStream;
import cformat.ScanfReader;

import joelib2.data.BasicAtomTypeConversionHolder;
import joelib2.data.BasicElementHolder;

import joelib2.io.MoleculeFileIO;

import joelib2.molecule.Atom;
import joelib2.molecule.ConnectionHelper;
import joelib2.molecule.Molecule;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.StringReader;

import org.apache.log4j.Category;


/**
 * Reader/Writer for XYZ files.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:34 $
 */
public class XYZ implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.XYZ");
    private final static String description = "XYZ";
    private final static String[] extensions = new String[]{"xyz"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private long lineCounter;

    // helper variable for skipReaderEntry
    private int linesRemaining;

    private LineNumberReader lnr;
    private PrintfStream ps;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the XYZ object
     */
    public XYZ()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void closeReader() throws IOException
    {
        lnr.close();
    }

    public void closeWriter() throws IOException
    {
        ps.close();
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
     *  Reads an molecule entry as (unparsed) <tt>String</tt> representation.
     *
     * @return                  <tt>null</tt> if the reader contains no more
     *      relevant data. Otherwise the <tt>String</tt> representation of the
     *      whole molecule entry is returned.
     * @exception  IOException  typical IOException
     */
    public String read() throws IOException
    {
        String line;

        if ((line = lnr.readLine()) == null)
        {
            return null;
        }

        int s;

        try
        {
            s = Integer.parseInt(line);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }

        StringBuffer sb = new StringBuffer(s * 100);

        // set number of atoms
        sb.append(line);
        sb.append('\n');

        // number of atoms
        linesRemaining = s;

        // set title
        if ((line = lnr.readLine()) == null)
        {
            return null;
        }

        sb.append(line);
        sb.append('\n');

        // set element and coordinates
        for (int i = 1; i <= s; i++, linesRemaining--)
        {
            if ((line = lnr.readLine()) == null)
            {
                linesRemaining--;
                skipReaderEntry();

                return null;
            }

            //sb.append(i-1);
            sb.append(line);
            sb.append('\n');
        }

        //System.out.println(sb);
        return sb.toString();
    }

    /**
     *  Description of the Method
     *
     * @param  mol              Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean read(Molecule mol) throws IOException
    {
        return read(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol              Description of the Parameter
     * @param  title            Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean read(Molecule mol, String title) throws IOException
    {
        int i;
        int natoms;

        String line;

        ScanfReader scanf;

        // delete molecule data
        mol.clear();

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        scanf = new ScanfReader(new StringReader(line));
        natoms = scanf.scanInt();

        if (natoms == 0)
        {
            return (false);
        }

        mol.reserveAtoms(natoms);
        BasicAtomTypeConversionHolder.instance().setFromType("XYZ");

        String str;
        double x;
        double y;
        double z;
        Atom atom;
        String elemString;

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        // set molecule title
        if (title == null)
        {
            mol.setTitle(line);
        }
        else
        {
            mol.setTitle(title);
        }

        BasicAtomTypeConversionHolder.instance().setToType("INT");

        // get all atoms
        linesRemaining = natoms;

        for (i = 1; i <= natoms; i++, linesRemaining--)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            scanf = new ScanfReader(new StringReader(line));

            //tokenize(vs,buffer);
            //if (vs.size() != 4) return(false);
            //x = atof((char*)vs[1].c_str());
            //y = atof((char*)vs[2].c_str());
            //z = atof((char*)vs[3].c_str());
            atom = mol.newAtom(true);

            //set atomic number
            elemString = scanf.scanString();
            atom.setAtomicNumber(BasicElementHolder.instance().getAtomicNum(
                    elemString));

            //set coordinates
            x = scanf.scanFloat();
            y = scanf.scanFloat();
            z = scanf.scanFloat();
            atom.setCoords3D(x, y, z);

            //set type
            str = BasicAtomTypeConversionHolder.instance().translate(
                    elemString);
            atom.setType(str);
        }

        // connect the atoms with bonds
        ConnectionHelper.connectTheDots(mol);

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
        int i;
        PrintfFormat f15_5 = new PrintfFormat("%15.5f");
        PrintfFormat s3 = new PrintfFormat("%3s");

        ps.printf("%d", mol.getAtomsSize());
        ps.println();

        //        System.out.println("TITLE:"+mol.getTitle());
        if (title == null)
        {
            ps.print(mol.getTitle());
        }
        else
        {
            ps.print(title);
        }

        ps.printf("\t%15.7f", mol.getEnergy());
        ps.println();
        BasicAtomTypeConversionHolder.instance().setFromType("INT");
        BasicAtomTypeConversionHolder.instance().setToType("XYZ");

        Atom atom;

        for (i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);

            ps.printf(s3,
                BasicElementHolder.instance().getSymbol(
                    atom.getAtomicNumber()));
            ps.printf(f15_5, atom.get3Dx());
            ps.printf(f15_5, atom.get3Dy());
            ps.printf(f15_5, atom.get3Dz());
            ps.println();
        }

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
