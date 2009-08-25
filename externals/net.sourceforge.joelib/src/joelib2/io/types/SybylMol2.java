///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SybylMol2.java,v $
//  Purpose:  Reader/Writer for Undefined files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/06/17 06:31:24 $
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

import cformat.PrintfFormat;
import cformat.PrintfStream;
import cformat.ScanfReader;

import joelib2.data.BasicAtomTypeConversionHolder;
import joelib2.data.BasicElementHolder;

import joelib2.feature.FeatureHelper;

import joelib2.feature.result.StringVectorResult;

import joelib2.feature.types.atomlabel.AtomPartialCharge;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;
import joelib2.feature.types.bondlabel.BondIsAmide;

import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.math.BasicVector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.BondHelper;
import joelib2.molecule.Molecule;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.StringReader;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Reader/Writer for Sybyl mol2 files.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/06/17 06:31:24 $
 * @.cite sybylmol2
 */
public class SybylMol2 implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.SybylMol2");
    private final static String description = "Sybyl Mol2";
    private final static String[] extensions = new String[]{"mol2"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private LineNumberReader lnr;
    private PrintfStream ps;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Undefined object
     */
    public SybylMol2()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void closeReader() throws IOException
    {
    }

    public void closeWriter() throws IOException
    {
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
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String inputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String[] inputFileExtensions()
    {
        return extensions;
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
            "Reading Sybyl mol2 data as String representation is not implemented yet !!!");

        return null;
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean read(Molecule mol) throws IOException, MoleculeIOException
    {
        return read(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean read(Molecule mol, String title) throws IOException,
        MoleculeIOException
    {
        boolean foundAtomLine = false;
        String comment = null;
        String str;
        String str1;

        // of type String
        Vector vstr = new Vector();
        String line;

        mol.beginModify();

        for (;;)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            if (line.indexOf("@<TRIPOS>MOLECULE") != -1)
            {
                break;
            }
        }

        int lcount;
        int natoms = -1;
        int nbonds = -1;
        ScanfReader scanf;
        String partialChargeVendor = "NotDefined";

        //  scanf.useCstandard( false );
        //  ScanfFormat  dFormat              = new ScanfFormat( "%d" );
        for (lcount = 0;; lcount++)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            if (line.indexOf("@<TRIPOS>ATOM") != -1)
            {
                foundAtomLine = true;

                break;
            }

            if (lcount == 0)
            {
                HelperMethods.tokenize(vstr, line);

                if (title == null)
                {
                    if (vstr.size() != 0)
                    {
                        mol.setTitle(line);
                    }
                }
                else
                {
                    mol.setTitle(title);
                }
            }
            else if (lcount == 1)
            {
                scanf = new ScanfReader(new StringReader(line));
                natoms = scanf.scanInt();
                nbonds = scanf.scanInt();
            }
            else if (lcount == 3)
            {
                if (line.trim().length() == 0)
                {
                    logger.warn(
                        "The vendor of the partial charges was not defined !");
                }
                else
                {
                    partialChargeVendor = line.trim();
                }
            }
            else if (lcount == 4) //energy
            {
                HelperMethods.tokenize(vstr, line);

                if ((vstr.size() == 3) &&
                        ((String) vstr.get(0)).equalsIgnoreCase("Energy"))
                {
                    mol.setEnergy(Double.parseDouble((String) vstr.get(2)));
                }
            }
            else if (lcount == 5) //comment
            {
                if (line.trim().length() != 0)
                {
                    comment = line;
                }
            }
        }

        if (!foundAtomLine)
        {
            mol.endModify();
            mol.clear();
            skipReaderEntry();
            throw new MoleculeIOException("Can not find atom line.");
        }

        mol.reserveAtoms(natoms);

        int i;
        BasicVector3D v = new BasicVector3D();
        Atom atom = mol.newAtom();
        boolean hasPartialCharges = false;
        double x;
        double y;
        double z;
        double pcharge = 0.0;
        List pCharge = new Vector(natoms);

        BasicAtomTypeConversionHolder ttab = BasicAtomTypeConversionHolder
            .instance();
        ttab.setFromType("SYB");

        //    ScanfFormat  fFormat              = new ScanfFormat( "%f" );
        //    ScanfFormat  sFormat              = new ScanfFormat( "%s" );
        for (i = 0; i < natoms; i++)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            scanf = new ScanfReader(new StringReader(line));
            scanf.scanString();
            scanf.scanString();
            x = scanf.scanDouble();
            y = scanf.scanDouble();
            z = scanf.scanDouble();
            str = scanf.scanString();

            // some databases does'nt contain these values
            try
            {
                scanf.scanString();
                scanf.scanString();
                pcharge = scanf.scanDouble();
            }
            catch (EOFException eof)
            {
                // that's o.k., do nothing
                logger.warn("Sybyl mol2 file contains no partial charges.");
            }

            v = new BasicVector3D();
            v.setX3D(x);
            v.setY3D(y);
            v.setZ3D(z);
            atom.setCoords3D(v);

            ttab.setToType("ATN");
            str1 = ttab.translate(str);
            atom.setAtomicNumber(Integer.parseInt(str1));
            ttab.setToType("INT");
            str1 = ttab.translate(str);
            atom.setType(str1);

            // if pcharge was readed !
            if (pcharge != 0.0)
            {
                hasPartialCharges = true;
            }

            if (hasPartialCharges)
            {
                pCharge.add(new Double(pcharge));
            }

            if (!mol.addAtomClone(atom))
            {
                return (false);
            }
        }

        if (hasPartialCharges)
        {
            mol.setAssignPartialCharge(false);
            mol.setPartialChargeVendor(partialChargeVendor.replace(' ', '_'));
        }

        for (;;)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            if (line.indexOf("@<TRIPOS>BOND") != -1)
            {
                break;
            }
        }

        int start;
        int end;
        int order;

        for (i = 0; i < nbonds; i++)
        {
            if ((line = lnr.readLine()) == null)
            {
                return (false);
            }

            scanf = new ScanfReader(new StringReader(line));
            scanf.scanInt();
            start = scanf.scanInt();
            end = scanf.scanInt();
            str = scanf.scanString();
            order = 1;

            if (str.equalsIgnoreCase("ar"))
            {
                order = BondHelper.AROMATIC_BO;
            }
            else if (str.equalsIgnoreCase("am"))
            {
                order = 1;
            }
            else
            {
                order = Integer.parseInt(str);
            }

            mol.addBond(start, end, order);
        }

        // do not nuke perceived data
        mol.endModify(false);

        for (int index = 0; index < pCharge.size(); index++)
        {
            AtomPartialCharge.setPartialCharge(mol.getAtom(index+1),
                ((Double) pCharge.get(index)).doubleValue());
        }

        //must add generic data after end modify - otherwise it will be blown away
        if (comment != null)
        {
            StringVectorResult commentData = new StringVectorResult();
            commentData.addString(comment);
            commentData.setKey(FeatureHelper.COMMENT_IDENTIFIER);
            mol.addData(commentData);
        }

        return (true);
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean readable()
    {
        return true;
    }

    public boolean skipReaderEntry() throws IOException
    {
        return true;
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException, MoleculeIOException
    {
        return write(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title) throws IOException,
        MoleculeIOException
    {
        String str;
        String str1;
        String rnum;
        String rlabel;

        ps.println("@<TRIPOS>MOLECULE");

        if (title == null)
        {
            str = mol.getTitle();

            if ((str == null) || (str.trim().length() == 0))
            {
                ps.println("*****");
            }
            else
            {
                ps.println(str);
            }
        }
        else
        {
            ps.println(title);
        }

        ps.print(' ');
        ps.print(mol.getAtomsSize());
        ps.print(' ');
        ps.print(mol.getBondsSize());
        ps.println(" 0 0 0");
        ps.println("SMALL");

        //  if (mol.hasPartialChargesPerceived())
        //  {
        //    ps.println("GASTEIGER");
        //  }
        //  else
        //  {
        //    ps.println("NO_CHARGES");
        //  }
        ps.print("Energy = ");
        ps.println(mol.getEnergy());

        if (mol.hasData(FeatureHelper.COMMENT_IDENTIFIER))
        {
            StringVectorResult commentData = (StringVectorResult) mol.getData(
                    FeatureHelper.COMMENT_IDENTIFIER);

            if (commentData.getStringVector().size() > 0)
            {
                ps.println(((String) commentData.getStringVector().get(0))
                    .toString());

                if (commentData.getStringVector().size() > 1)
                {
                    logger.warn(
                        "Multiple comments available, only the first entry is written.");
                }
            }
        }
        else
        {
            ps.println();
        }

        ps.println("@<TRIPOS>ATOM");

        BasicAtomTypeConversionHolder ttab = BasicAtomTypeConversionHolder
            .instance();
        ttab.setFromType("INT");
        ttab.setToType("SYB");

        Atom atom;
        int[] labelcount = new int[105]; //Number of elements
        PrintfFormat f10_4 = new PrintfFormat("%10.4f");
        PrintfFormat f12_4 = new PrintfFormat("%12.4f");
        PrintfFormat s1 = new PrintfFormat("%1s");
        PrintfFormat s4 = new PrintfFormat("%4s");
        PrintfFormat s5 = new PrintfFormat("%-5s");
        PrintfFormat s6 = new PrintfFormat("%-6s");
        PrintfFormat s8 = new PrintfFormat("%-8s");
        PrintfFormat d7 = new PrintfFormat("%7d");
        AtomIterator ait = mol.atomIterator();
        StringBuffer label = new StringBuffer(20);

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            str = atom.getType();

            str1 = ttab.translate(str);

            //    System.out.println("convert atom type :"+str+" to:"+str1);
            rlabel = "<1>";
            rnum = "1";

            ps.printf(d7, atom.getIndex());
            ps.printf(s1, "");

            if (label.length() > 0)
            {
                label.delete(0, label.length());
            }

            label.append(BasicElementHolder.instance().getSymbol(
                    atom.getAtomicNumber()));
            label.append(++labelcount[atom.getAtomicNumber()]);
            ps.printf(s6, label.toString());
            ps.printf(f12_4, atom.get3Dx());
            ps.printf(f10_4, atom.get3Dy());
            ps.printf(f10_4, atom.get3Dz());
            ps.print(' ');
            ps.printf(s5, str1);
            ps.printf(s4, rnum);
            ps.printf(s1, "");
            ps.print(' ');
            ps.printf(s8, rlabel);
            ps.printf(f10_4, AtomPartialCharge.getPartialCharge(atom));
            ps.println();
        }

        ps.println("@<TRIPOS>BOND");

        Bond bond;
        BondIterator bit = mol.bondIterator();
        String bondLabel;
        PrintfFormat s2 = new PrintfFormat("%2s");
        PrintfFormat s3 = new PrintfFormat("%3s");
        PrintfFormat d6 = new PrintfFormat("%6d");

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if ((bond.getBondOrder() == BondHelper.AROMATIC_BO) ||
                    BondInAromaticSystem.isAromatic(bond))
            {
                bondLabel = "ar";
            }
            else if (BondIsAmide.isAmide(bond))
            {
                bondLabel = "am";
            }
            else
            {
                bondLabel = Integer.toString(bond.getBondOrder());
            }

            ps.printf(d6, bond.getIndex() + 1);
            ps.printf(d6, bond.getBeginIndex());
            ps.printf(d6, bond.getEndIndex());
            ps.printf(s3, "");
            ps.printf(s2, bondLabel);
            ps.println();
        }

        //ps.println();
        return (true);
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
