///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Gaussian.java,v $
//Purpose:  Flat file format support.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.9 $
//                      $Date: 2005/02/17 16:48:34 $
//                      $Author: wegner $
//Original Author:Michael Banck<mbanck@gmx.net>, OpenEye Scientific Software
//Original Version: babel 2.0a1
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.io.types;

import cformat.PrintfFormat;
import cformat.PrintfStream;
import cformat.ScanfReader;

import joelib2.data.BasicAtomTypeConversionHolder;
import joelib2.data.BasicElementHolder;

import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

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
 * Atom representation.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:34 $
 */
public class Gaussian implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.Gaussian");
    private static final String description = "Gaussian Cartesian";
    private static final String[] extensions = new String[]{"gcart", "gau"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private PrintfFormat d = new PrintfFormat("%d");
    private PrintfFormat f10_5 = new PrintfFormat("%10.5f");

    private LineNumberReader lnr;
    private PrintfStream ps;

    //private PrintfFormat d4 = new PrintfFormat("%-4d");
    //private PrintfFormat d5 = new PrintfFormat("%-5d");
    private PrintfFormat s3 = new PrintfFormat("%-3s");

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

    public void initReader(InputStream is) throws IOException
    {
        lnr = new LineNumberReader(new InputStreamReader( /*(ZipInputStream)*/
                    is));
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

    //public Gaussian()
    //{
    //}
    public String inputDescription()
    {
        return description;
    }

    public String[] inputFileExtensions()
    {
        return extensions;
    }

    public String outputDescription()
    {
        return description;
    }

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
            "Reading Gaussian data as String representation is not implemented yet !!!");

        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  IOException          Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol) throws IOException,
        MoleculeIOException
    {
        return read(mol, null);
    }

    /**
     *  Loads an molecule in MDL SD-MOL format and sets the title. If <tt>title
     *  </tt> is <tt>null</tt> the title line in the molecule file is used.
     *
     * @param  mol                      Description of the Parameter
     * @param  title                    Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  IOException          Description of the Exception
     * @exception  MoleculeIOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol, String title)
        throws IOException, MoleculeIOException
    {
        String line;
        ScanfReader scanf;

        // delete molecule data
        mol.clear();

        // skip first 3 lines
        for (int i = 0; i < 3; i++)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }
        }

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        // set molecule title
        if (title == null)
        {
            if (line.length() > 3)
            {
                mol.setTitle(line.substring(3));
            }
            else
            {
                mol.setTitle(line);
            }
        }
        else
        {
            mol.setTitle(title);
        }

        //              skip first 2 lines
        for (int i = 0; i < 2; i++)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }
        }

        BasicAtomTypeConversionHolder.instance().setFromType("XYZ");
        BasicAtomTypeConversionHolder.instance().setToType("INT");

        String str;
        double x;
        double y;
        double z;
        Atom atom;
        String elemString;

        // get all atoms
        while (true)
        {
            if ((line = lnr.readLine()) == null)
            {
                ConnectionHelper.connectTheDots(mol);

                return (false);
            }

            if (line.trim().length() == 0)
            {
                break;
            }

            scanf = new ScanfReader(new StringReader(line));

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

        return true;
    }

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
        ps.print("%cmem=20000000" + "\n");
        ps.print("#Put Keywords Here" + "\n" + "\n");

        int i;

        ps.print("XX ");

        if (title == null)
        {
            ps.print(mol.getTitle());
        }
        else
        {
            ps.print(title);
        }

        ps.print("\n\n");

        BasicAtomTypeConversionHolder.instance().setFromType("INT");
        BasicAtomTypeConversionHolder.instance().setToType("XYZ");

        Atom atom;
        double charge = 0.0;

        for (i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);
            charge = atom.getFormalCharge();
        }

        //      Calculate Multiplicity FIXME: This is a hack!
        double multiplicity = Math.abs(charge) + 1;
        ps.print("  ");
        ps.printf(d, (int) charge);
        ps.print("  ");
        ps.printf(d, (int) multiplicity);
        ps.print("\n");

        for (i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);

            ps.printf(s3,
                BasicElementHolder.instance().getSymbol(
                    atom.getAtomicNumber()));
            ps.print("      ");
            ps.printf(f10_5, atom.get3Dx());
            ps.print("      ");
            ps.printf(f10_5, atom.get3Dy());
            ps.print("      ");
            ps.printf(f10_5, atom.get3Dz());
            ps.print("\n");
        }

        //      file should end with a blank line
        ps.print("\n");

        return (true);
    }

    public boolean writeable()
    {
        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
