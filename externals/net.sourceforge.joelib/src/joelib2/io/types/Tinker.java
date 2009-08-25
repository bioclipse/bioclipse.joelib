/**
 *  Filename: $RCSfile: Tinker.java,v $
 *  Purpose:  Atom representation.
 *  Language: Java
 *  Compiler: JDK 1.4
 *  Authors:  Joerg Kurt Wegner
 *  Version:  $Revision: 1.8 $
 *            $Date: 2005/02/17 16:48:34 $
 *            $Author: wegner $
 *  Original Author: ???, OpenEye Scientific Software
 *  Original Version: babel 2.0a1
 *
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */
package joelib2.io.types;

import cformat.PrintfFormat;
import cformat.PrintfStream;

import joelib2.data.BasicAtomTypeConversionHolder;
import joelib2.data.BasicElementHolder;

import joelib2.io.MoleculeFileIO;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import org.apache.log4j.Category;


/**
 * Reader/Writer for Tinker files.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:34 $
 */
public class Tinker implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.Tinker");
    private final static String description = "Tinker XYZ";
    private final static String[] extensions = new String[]{"txyz"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private PrintfFormat d5 = new PrintfFormat("%5d");
    private PrintfFormat d6 = new PrintfFormat("%6d");
    private PrintfFormat f12_6 = new PrintfFormat("%12.6f");
    private boolean forceUnixStyle = true;

    private LineNumberReader lnr;
    private PrintfStream ps;
    private PrintfFormat s2 = new PrintfFormat("%2s");
    private PrintfFormat s20 = new PrintfFormat("%-20s");

    //~ Constructors ///////////////////////////////////////////////////////////

    public Tinker()
    {
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

    public boolean getUseUnixStyle()
    {
        return forceUnixStyle;
    }

    /**
     *  Description of the Method
     *
     * @param is               Description of the Parameter
     * @exception IOException  Description of the Exception
     */
    public void initReader(InputStream is) throws IOException
    {
        lnr = new LineNumberReader(new InputStreamReader(is));
    }

    /**
     *  Description of the Method
     *
     * @param os               Description of the Parameter
     * @exception IOException  Description of the Exception
     */
    public void initWriter(OutputStream os) throws IOException
    {
        ps = new PrintfStream(os);
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String inputDescription()
    {
        return null;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String[] inputFileExtensions()
    {
        return null;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String outputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
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
            "Reading Tinker data as String representation is not implemented yet !!!");

        return null;
    }

    /**
     * Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean read(Molecule mol) throws IOException
    {
        return read(mol, "Undefined");
    }

    /**
     * Description of the Method
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol, String title)
        throws IOException
    {
        return false;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean readable()
    {
        return false;
    }

    public void setUseUnixStyle(boolean _flag)
    {
        forceUnixStyle = _flag;
    }

    public boolean skipReaderEntry() throws IOException
    {
        return true;
    }

    /**
     * Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public synchronized boolean write(Molecule mol) throws IOException
    {
        return write(mol, "Undefined");
    }

    /**
     * Description of the Method
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public synchronized boolean write(Molecule mol, String title)
        throws IOException
    {
        ps.printf(d6, mol.getAtomsSize());
        ps.print(' ');
        ps.printf(s20, mol.getTitle());

        if (forceUnixStyle)
        {
            ps.print('\n');
        }
        else
        {
            ps.println();
        }

        BasicAtomTypeConversionHolder ttab = BasicAtomTypeConversionHolder
            .instance();
        ttab.setFromType("INT");
        ttab.setToType("MM2");

        Atom atom;
        AtomIterator ait = mol.atomIterator();
        String str;
        String str1;
        int index = 1;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            str = atom.getType();
            str1 = ttab.translate(str);
            ps.printf(d6, index);
            ps.print(' ');
            ps.printf(s2,
                BasicElementHolder.instance().getSymbol(
                    atom.getAtomicNumber()));
            ps.print("  ");
            ps.printf(f12_6, atom.get3Dx());
            ps.printf(f12_6, atom.get3Dy());
            ps.printf(f12_6, atom.get3Dz());
            ps.print(' ');
            ps.printf(d5, Integer.parseInt(str1));

            BondIterator bit = atom.bondIterator();
            Bond bond;

            while (bit.hasNext())
            {
                bond = bit.nextBond();
                ps.printf(d6, (bond.getNeighbor(atom)).getIndex());
            }

            if (forceUnixStyle)
            {
                ps.print('\n');
            }
            else
            {
                ps.println();
            }

            index++;
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
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
