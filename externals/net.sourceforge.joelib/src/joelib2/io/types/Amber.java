///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Amber.java,v $
//  Purpose:  Reader/Writer for Undefined files.
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
////////////////////////////////////////////////////////////////////////////////**
package joelib2.io.types;

import cformat.PrintfStream;

import joelib2.data.BasicAtomTypeConversionHolder;
import joelib2.data.BasicElementHolder;

import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.math.InternalCoordinates;

import joelib2.molecule.Atom;
import joelib2.molecule.ConnectionHelper;
import joelib2.molecule.Molecule;

import joelib2.util.HelperMethods;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Reader for AmberPrep file format.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:34 $
 */
public class Amber implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.Amber");
    private static final String description = "Amber PREP";
    private static final String[] extensions = new String[]{"prep"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private LineNumberReader lnr;
    private PrintfStream ps;

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
        return null;
    }

    public String[] outputFileExtensions()
    {
        return null;
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
            "Reading Amber data as String representation is not implemented yet !!!");

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
        Vector vs = new Vector();
        Atom atom;
        InternalCoordinates coord;
        List<InternalCoordinates> internals = new Vector<InternalCoordinates>();

        BasicAtomTypeConversionHolder.instance().setFromType("XYZ");
        BasicAtomTypeConversionHolder.instance().setToType("INT");
        mol.beginModify();

        while ((line = lnr.readLine()) != null)
        {
            HelperMethods.tokenize(vs, line);

            if (vs.size() > 8)
            {
                atom = mol.newAtom(true);
                coord = new InternalCoordinates();

                if (mol.getAtomsSize() > 1)
                {
                    coord.atom1 = mol.getAtom(Integer.parseInt(
                                (String) vs.get(4)));
                }

                if (mol.getAtomsSize() > 2)
                {
                    coord.atom2 = mol.getAtom(Integer.parseInt(
                                (String) vs.get(5)));
                }

                if (mol.getAtomsSize() > 3)
                {
                    coord.atom3 = mol.getAtom(Integer.parseInt(
                                (String) vs.get(6)));
                }

                coord.distance = Double.parseDouble((String) vs.get(7));
                coord.angle = Double.parseDouble((String) vs.get(8));
                coord.torsion = Double.parseDouble((String) vs.get(9));
                internals.add(coord);

                atom.setAtomicNumber(BasicElementHolder.instance().getAtomicNum(
                        (String) vs.get(1)));
                atom.setType(BasicAtomTypeConversionHolder.instance().translate(
                        (String) vs.get(1)));
            }
        }

        InternalCoordinates.internalToCartesian(internals, mol);
        mol.endModify();

        ConnectionHelper.connectTheDots(mol);
        mol.setTitle(title);

        return (true);
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
        return (true);
    }

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
