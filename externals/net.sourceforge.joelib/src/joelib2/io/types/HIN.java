///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: HIN.java,v $
//  Purpose:  Reader/Writer for Undefined files.
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

import cformat.PrintfFormat;
import cformat.PrintfStream;

import joelib2.data.BasicAtomTypeConversionHolder;
import joelib2.data.BasicElementHolder;

import joelib2.feature.types.atomlabel.AtomPartialCharge;

import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.BondIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom representation.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:34 $
 */
public class HIN implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.HIN");
    private static final String description = "Hyperchem";
    private static final String[] extensions = new String[]{"hin"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean forceUnixStyle = true;

    private LineNumberReader lnr;
    private int moleculeEntry = 1;
    private PrintfStream ps;

    //~ Constructors ///////////////////////////////////////////////////////////

    public HIN()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }
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
        StringBuffer molecule = new StringBuffer(10000);
        String delimiter = "endmol";
        String line;

        while ((line = lnr.readLine()) != null)
        {
            if ((line.length() > 0) &&
                    (line.charAt(0) == delimiter.charAt(0)) &&
                    (line.indexOf(delimiter) != -1))
            {
                molecule.append(line);
                molecule.append(HelperMethods.eol);

                break;
            }

            molecule.append(line);
            molecule.append(HelperMethods.eol);
        }

        if (line == null)
        {
            return null;
        }
        else
        {
            return molecule.toString();
        }
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
        // Right now only read in the first molecule
        String line;
        Vector tmpV = new Vector();
        BasicAtomTypeConversionHolder.instance().setFromType("XYZ");

        while (((line = lnr.readLine()) != null) &&
                (line.startsWith("mol") == false))
        {
            if (line == null)
            {
                return false;
            }
        }

        if (line == null)
        {
            return false;
        }

        //System.out.println(line);
        HelperMethods.tokenize(tmpV, line, " \t\r\n");

        if (tmpV.size() > 2)
        {
            // there seems to be a molecule title
            // set molecule title
            if (title == null)
            {
                mol.setTitle((String) tmpV.get(2));
            }
            else
            {
                mol.setTitle(title);
            }
        }

        // start reading atom informations
        mol.beginModify();

        int atomLine = 1;
        Atom atom;
        BasicAtomTypeConversionHolder.instance().setToType("INT");

        double x;
        double y;
        double z;
        int bo;
        int max;
        int end;

        while (((line = lnr.readLine()) != null) &&
                (line.startsWith("endmol") == false))
        {
            //System.out.println(line);
            if (line == null)
            {
                throw new MoleculeIOException("Missing 'endmol' tag.");
            }

            //Don't really know how long it'll be
            HelperMethods.tokenize(tmpV, line, " \t\r\n");

            if (tmpV.size() <= 11)
            {
                skipReaderEntry();
                throw new MoleculeIOException("Corrupted atom line " +
                    atomLine + ".");
            }

            try
            {
                atom = mol.newAtom(true);
                atom.setAtomicNumber(BasicElementHolder.instance().getAtomicNum(
                        (String) tmpV.get(3)));
                x = Double.parseDouble((String) tmpV.get(6));
                y = Double.parseDouble((String) tmpV.get(7));
                z = Double.parseDouble((String) tmpV.get(8));
                atom.setCoords3D(x, y, z);
                atom.setType(BasicAtomTypeConversionHolder.instance().translate(
                        (String) tmpV.get(3)));

                // resolve bond informations
                max = 11 + (2 * Integer.parseInt((String) tmpV.get(10)));

                for (int i = 11; i < max; i += 2)
                {
                    switch (((String) tmpV.get(i + 1)).charAt(0))
                    {
                    case 's':
                        bo = 1;

                        break;

                    case 'd':
                        bo = 2;

                        break;

                    case 't':
                        bo = 3;

                        break;

                    case 'a':
                        bo = 5;

                        break;

                    default:
                        bo = 1;

                        break;
                    }

                    end = Integer.parseInt((String) tmpV.get(i));

                    //                                  System.out.println(
                    //                                          "add bond: " + mol.numAtoms() + " " + end);
                    // add only bonds, where inverse does not exists
                    if (mol.existsBond(end, mol.getAtomsSize()) == false)
                    {
                        mol.addBond(mol.getAtomsSize(), end, bo);
                    }
                }
            }
            catch (Exception ex)
            {
                skipReaderEntry();
                throw new MoleculeIOException("Error in atom line " + atomLine +
                    ": " + ex.getMessage());
            }

            atomLine++;
        }

        if (line == null)
        {
            throw new MoleculeIOException("Missing 'endmol' tag.");
        }

        mol.endModify();

        return (true);
    }

    public boolean readable()
    {
        return true;
    }

    public void resetMoleculeEntryNumber()
    {
        moleculeEntry = 1;
    }

    /**
     *  Description of the Method
     *
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean skipReaderEntry() throws IOException
    {
        String line;

        while ((line = lnr.readLine()) != null)
        {
            if ((line.length() > 0) && (line.charAt(0) == 'e') &&
                    (line.indexOf("endmol") != -1))
            {
                break;
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
        PrintfFormat f8_5 = new PrintfFormat("%8.5f");
        PrintfFormat s3 = new PrintfFormat("%-3s");

        ps.print("mol ");
        ps.print(moleculeEntry);
        ps.print(" ");

        if (title == null)
        {
            ps.print(mol.getTitle());
        }
        else
        {
            ps.print(title);
        }

        if (forceUnixStyle)
        {
            ps.print('\n');
        }
        else
        {
            ps.println();
        }

        BasicAtomTypeConversionHolder.instance().setFromType("INT");
        BasicAtomTypeConversionHolder.instance().setToType("XYZ");

        Atom atom;

        for (i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);

            //                  ps.printf(
            //                          s3,
            //                          JOEElementTable.instance().getSymbol(atom.getAtomicNum()));
            //                  ps.printf(f15_5, atom.getZ());
            //                  ps.println();
            ps.print("atom ");
            ps.print(i);
            ps.print(" - ");
            ps.printf(s3,
                BasicElementHolder.instance().getSymbol(
                    atom.getAtomicNumber()));
            ps.print(" **  - ");
            ps.printf(f8_5, atom.get3Dx());
            ps.print(' ');
            ps.printf(f8_5, atom.get3Dy());
            ps.print(' ');
            ps.print(' ');
            ps.printf(f8_5, atom.get3Dz());
            ps.print(' ');
            ps.print(' ');
            ps.printf(f8_5, AtomPartialCharge.getPartialCharge(atom));
            ps.print(' ');
            ps.print(atom.getValence());
            ps.print(' ');

            BondIterator bit = atom.bondIterator();
            Bond bond;
            char bondCharacter;

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                switch (bond.getBondOrder())
                {
                case 1:
                    bondCharacter = 's';

                    break;

                case 2:
                    bondCharacter = 'd';

                    break;

                case 3:
                    bondCharacter = 't';

                    break;

                case 5:
                    bondCharacter = 'a';

                    break;

                default:
                    bondCharacter = 's';

                    break;
                }

                ps.print(bond.getNeighbor(atom).getIndex());
                ps.print(' ');
                ps.print(bondCharacter);
                ps.print(' ');
            }

            if (forceUnixStyle)
            {
                ps.print('\n');
            }
            else
            {
                ps.println();
            }
        }

        ps.print("endmol ");
        ps.print(moleculeEntry);

        if (forceUnixStyle)
        {
            ps.print('\n');
        }
        else
        {
            ps.println();
        }

        moleculeEntry++;

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

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
