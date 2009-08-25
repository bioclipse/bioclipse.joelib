///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ClearTextFormat.java,v $
//  Purpose:  Reader/Writer for CTX files.
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

import cformat.PrintfFormat;
import cformat.PrintfStream;

import joelib2.data.BasicElementHolder;

import joelib2.feature.FeatureHelper;

import joelib2.feature.result.AtomDynamicResult;
import joelib2.feature.result.BondDynamicResult;
import joelib2.feature.result.DynamicArrayResult;
import joelib2.feature.result.StringVectorResult;

import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.NbrAtomIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Reader/Writer for ClearTeXt (CTX) files.
 *
 * For speeding up descriptor molecule files have a look at the {@link joelib2.feature.ResultFactory}.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:34 $
 */
public class ClearTextFormat implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.ClearTextFormat");
    private static final int ATOM_PROPERTY = 1;
    private static final int BOND_PROPERTY = 2;
    private static final int MOLECULE_PROPERTY = 3;
    private static final int ENSEMBLE_PROPERTY = 4;

    /**
     * Data element for storing the access information for the asymmetric
     * bond properties.
     *
     * index1=ctxID index2=idAtom1 index3=idAtom2 index4=singleEntryID
     *
     * The singleEntryID starts with 0. It's the internal id number which
     * is used in JOELib.
     */
    public static final String ASYM_BOND_PROPERTY_INDEX =
        "ASYM_BOND_PROPERTY_INDEX";
    private final static String description = "CACTVS clear text format (CTX)";
    private final static String[] extensions = new String[]{"ctx"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private LineNumberReader lnr;
    private PrintfStream ps;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void closeReader() throws IOException
    {
        lnr.close();
    }

    public void closeWriter() throws IOException
    {
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
        StringBuffer molecule = new StringBuffer(10000);
        String delimiter = " /END";
        String line;

        while ((line = lnr.readLine()) != null)
        {
            if ((line.length() > 0) &&
                    (line.charAt(1) == delimiter.charAt(1)) &&
                    (line.charAt(2) == delimiter.charAt(2)) &&
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
     *  Loads a ClearTextFile.
     *  Attention: bond properties can have the twice of the size of atoms.
     *  Use the CTX_BOND_INDEX data element to get the correct bond
     *  indices for this properties !!!
     *  This causes from circumstance, that in JOELib bonds are not stored twice !!!
     *
     * @param  mol              Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol) throws IOException,
        MoleculeIOException
    {
        return read(mol, null);
    }

    /**
     *  Loads an molecule in ClearTextFile format and sets the title. If <tt>title
     *  </tt> is <tt>null</tt> the title line in the molecule file is used.
     *  Attention: bond properties can have the twice of the size of atoms.
     *  Use the CTX_BOND_INDEX data element to get the correct bond
     *  indices for this properties !!!
     *  This causes from circumstance, that in JOELib bonds are not stored twice !!!
     *
     * @param  mol              Description of the Parameter
     * @param  title            Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol, String title)
        throws IOException, MoleculeIOException
    {
        int i;

        //    int          natoms;
        //    int          nbonds;
        String line;
        String molName = "Undefined";
        String comment;

        //    ScanfReader  scanf;
        // delete molecule data
        mol.clear();

        // start at reading
        while (true)
        {
            line = lnr.readLine();

            if (line == null)
            {
                return false;
            }

            if ((line.length() > 2) && (line.charAt(1) == '/'))
            {
                // System.out.println("line:"+line);
                if (line.indexOf("IDENT", 2) != -1)
                {
                    line = lnr.readLine();

                    if (line != null)
                    {
                        comment = line;
                        mol.beginModify();

                        break;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }

        // cheak all properties
        boolean exit = false;
        Vector tv = new Vector();
        int counter;
        Atom atom = mol.newAtom();

        //    Bond      bond     = new Bond();
        int ti1;
        int ti2;
        int ti3;
        int ti4;
        double td1;
        double td2;
        double td3;

        while (true)
        {
            line = lnr.readLine();

            if (line == null)
            {
                return false;
            }

            if ((line.length() > 2) && (line.charAt(1) == '/'))
            {
                switch (line.charAt(2))
                {
                case '2':
                case '3':

                    if ((line.indexOf("2DCOORD", 2) != -1) ||
                            (line.indexOf("3DCOORD", 2) != -1))
                    {
                        boolean is2D = (line.charAt(2) == '2') ? true : false;

                        // read atoms coordinates
                        tv.clear();
                        HelperMethods.tokenize(tv, line);

                        // check number of atoms
                        counter = Integer.parseInt((String) tv.get(1)) - 2;

                        if (mol.getAtomsSize() != counter)
                        {
                            logger.error(
                                "Wrong number of 3D coordinates. Should be " +
                                mol.getAtomsSize() + " but it's " + counter +
                                ".");
                            skipReaderEntry();
                            throw new MoleculeIOException(
                                "Wrong number of 3D coordinates. Should be " +
                                mol.getAtomsSize() + " but it's " + counter +
                                ".");

                            //return false;
                        }

                        // get 3D coords
                        //QUESTION: Do the following two lines contain necessary information?
                        line = lnr.readLine();
                        line = lnr.readLine();

                        for (i = 1; i <= counter; i++)
                        {
                            line = lnr.readLine();
                            tv.clear();
                            HelperMethods.tokenize(tv, line);

                            atom = mol.getAtom(i);

                            // x
                            td1 = Double.parseDouble((String) tv.get(1));

                            // y
                            td2 = Double.parseDouble((String) tv.get(2));

                            // z
                            if (is2D)
                            {
                                td3 = 0;
                            }
                            else
                            {
                                td3 = Double.parseDouble((String) tv.get(3));
                            }

                            atom.setCoords3D(td1, td2, td3);
                        }
                    }

                    break;

                case 'A':

                    if (line.indexOf("ATOMS", 2) != -1)
                    {
                        // read atoms
                        tv.clear();
                        HelperMethods.tokenize(tv, line);

                        // reserve space for atoms
                        counter = Integer.parseInt((String) tv.get(1));
                        mol.reserveAtoms(counter);

                        // get atoms
                        for (i = 0; i < counter; i++)
                        {
                            line = lnr.readLine();
                            tv.clear();
                            HelperMethods.tokenize(tv, line);

                            atom.clear();
                            ti1 = Integer.parseInt((String) tv.get(1));
                            atom.setAtomicNumber(ti1);
                            atom.setType(BasicElementHolder.instance()
                                .getSymbol(ti1));

                            //free electrons
                            ti1 = Integer.parseInt((String) tv.get(2));
                            atom.setFreeElectrons(ti1);

                            if (!mol.addAtomClone(atom))
                            {
                                skipReaderEntry();
                                throw new MoleculeIOException(
                                    "Could not add atom.");

                                //return (false);
                            }
                        }
                    }
                    else if (line.indexOf("ATOPROP", 2) != -1)
                    {
                        getProperty(mol, ATOM_PROPERTY);
                    }

                    break;

                case 'B':

                    if (line.indexOf("BONDS", 2) != -1)
                    {
                        // read atoms
                        tv.clear();
                        HelperMethods.tokenize(tv, line);

                        // reserve space for bonds
                        counter = Integer.parseInt((String) tv.get(1));

                        StringBuffer asymBondPropIndex = new StringBuffer(10 *
                                counter);

                        //                          mol.reserveBonds(counter);
                        // get bonds
                        Hashtable bondChecker = new Hashtable(counter);
                        int bondNumber;
                        String tmp1;
                        String tmp2;
                        Integer intTmp;

                        for (i = 0; i < counter; i++)
                        {
                            line = lnr.readLine();
                            tv.clear();
                            HelperMethods.tokenize(tv, line);

                            //                            bond.clear();
                            // atom 1 index
                            ti1 = Integer.parseInt((String) tv.get(1));

                            // atom 2 index
                            ti2 = Integer.parseInt((String) tv.get(2));

                            // bond order
                            ti3 = Integer.parseInt((String) tv.get(3));

                            // bond flags
                            ti4 = 0;

                            // don't get bonds twice !!!
                            tmp1 = tv.get(1) + "_" + tv.get(2);
                            tmp2 = tv.get(2) + "_" + tv.get(1);

                            if ((bondChecker.get(tmp1) == null) &&
                                    (bondChecker.get(tmp2) == null))
                            {
                                if (!mol.addBond(ti1, ti2, ti3, ti4))
                                {
                                    skipReaderEntry();
                                    throw new MoleculeIOException(
                                        "Could not add bond.");

                                    //return (false);
                                }
                                else
                                {
                                    bondChecker.put(tv.get(1) + "_" + tv.get(2),
                                        new Integer(mol.getBondsSize() - 1));
                                }

                                bondNumber = mol.getBondsSize() - 1;
                            }
                            else
                            {
                                intTmp = (Integer) bondChecker.get(tmp1);

                                if (intTmp != null)
                                {
                                    bondNumber = intTmp.intValue();
                                }
                                else
                                {
                                    intTmp = (Integer) bondChecker.get(tmp2);
                                    bondNumber = intTmp.intValue();
                                }
                            }

                            // but save ctx information to enable bond property parsing
                            asymBondPropIndex.append((String) tv.get(0));
                            asymBondPropIndex.append(' ');
                            asymBondPropIndex.append((String) tv.get(1));
                            asymBondPropIndex.append(' ');
                            asymBondPropIndex.append((String) tv.get(2));
                            asymBondPropIndex.append(' ');
                            asymBondPropIndex.append(bondNumber);

                            if (i < (counter - 1))
                            {
                                asymBondPropIndex.append(HelperMethods.eol);
                            }
                        }

                        bondChecker = null;

                        BasicPairData dp = new BasicPairData();
                        dp.setKey(ASYM_BOND_PROPERTY_INDEX);
                        dp.setKeyValue(asymBondPropIndex);
                        mol.addData(dp);
                    }
                    else if (line.indexOf("BONPROP", 2) != -1)
                    {
                        getProperty(mol, BOND_PROPERTY);
                    }

                    break;

                case 'E':

                    if (line.indexOf("END", 2) != -1)
                    {
                        // molecule successfull readed
                        exit = true;
                    }
                    else if (line.indexOf("ENSPROP", 2) != -1)
                    {
                        getProperty(mol, ENSEMBLE_PROPERTY);
                    }

                    break;

                case 'M':

                    if (line.indexOf("MOLPROP", 2) != -1)
                    {
                        getProperty(mol, MOLECULE_PROPERTY);
                    }

                    break;

                case 'N':

                    if (line.indexOf("NAME", 2) != -1)
                    {
                        line = lnr.readLine();

                        if (line != null)
                        {
                            molName = line;
                        }
                        else
                        {
                            logger.error("No molecule name defined.");
                            skipReaderEntry();
                            throw new MoleculeIOException(
                                "No molecule name defined.");

                            //return (false);
                        }
                    }

                    break;
                }
            }

            if (exit)
            {
                break;
            }
        }

        mol.endModify();

        // set comment
        if (comment != null)
        {
            StringVectorResult commentData = new StringVectorResult();
            commentData.addString(comment);
            commentData.setKey(FeatureHelper.COMMENT_IDENTIFIER);
            mol.addData(commentData);
        }

        // set molecule title
        if (title == null)
        {
            mol.setTitle(molName);
        }
        else
        {
            mol.setTitle(title);
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

    public boolean skipReaderEntry() throws IOException
    {
        String line;
        boolean exit = false;

        while (((line = lnr.readLine()) != null) || exit)
        {
            if ((line.length() > 2) && (line.charAt(1) == '/'))
            {
                switch (line.charAt(2))
                {
                case 'E':

                    if (line.indexOf("END", 2) != -1)
                    {
                        // molecule entry successfull skipped
                        exit = true;
                    }
                }
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
        //        PrintfFormat s15 = new PrintfFormat("%15s");
        PrintfFormat d3 = new PrintfFormat("%3d");

        //        mol.addHydrogens();
        String setTitle;

        if (title == null)
        {
            setTitle = mol.getTitle();

            if (setTitle == null)
            {
                setTitle = "Undefined";
            }
        }
        else
        {
            setTitle = title;
        }

        ps.println(" /IDENT        1    1");
        ps.println(setTitle);
        ps.println(" /NAME         1    1");
        ps.println(setTitle);

        // check valences with CACTVS
        ps.println(" /VALENCE      1    1");

        //        ps.println("0"); // don't check
        ps.println("4"); // check all

        ps.println(" /MOLECULS     1    1");
        ps.println("1 1 " + mol.getAtomsSize());

        // write atoms
        ps.print(" /ATOMS      ");
        ps.printf(d3, mol.getAtomsSize());
        ps.print("  ");
        ps.printf(d3, mol.getAtomsSize());
        ps.println();

        Atom atom;

        AtomIterator ait = mol.atomIterator();
        int bondCounter = 1;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            ps.print(atom.getIndex());
            ps.print(' ');
            ps.print(atom.getAtomicNumber());
            ps.print(' ');
            ps.print(atom.getFreeElectrons());
            ps.print(' ');
            ps.print(bondCounter);
            ps.print(' ');
            ps.print((bondCounter + atom.getValence()) - 1);
            bondCounter += atom.getValence();
            ps.println();
        }

        // write bond
        ps.print(" /BONDS      ");
        ps.printf(d3, 2 * mol.getBondsSize());
        ps.print("  ");
        ps.printf(d3, 2 * mol.getBondsSize());
        ps.println();

        Bond bond;
        ait.reset();

        int first;
        int second;
        int index = 1;
        int atomNumber = 0;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            NbrAtomIterator nait = atom.nbrAtomIterator();
            atomNumber++;

            while (nait.hasNext())
            {
                nait.nextNbrAtom();

                bond = nait.actualBond();
                ps.print(index++);
                ps.print(' ');

                if (bond.getBeginIndex() == atomNumber)
                {
                    first = bond.getBeginIndex();
                    second = bond.getEndIndex();
                }
                else
                {
                    second = bond.getBeginIndex();
                    first = bond.getEndIndex();
                }

                ps.print(first);
                ps.print(' ');
                ps.print(second);
                ps.print(' ');
                ps.print(bond.getBondOrder());
                ps.println();
            }
        }

        // write 3D coordinates
        ps.print(" /3DCOORD    ");
        ps.printf(d3, mol.getAtomsSize());
        ps.print("  ");
        ps.printf(d3, mol.getAtomsSize());
        ps.println();
        ait.reset();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            ps.print(atom.getIndex());
            ps.print(' ');
            ps.print(atom.get3Dx());
            ps.print(' ');
            ps.print(atom.get3Dy());
            ps.print(' ');
            ps.print(atom.get3Dz());
            ps.println();
        }

        // write additional descriptor data
        //        if (writePairData) {
        //            GenericDataIterator gdit = mol.genericDataIterator();
        //            JOEGenericData genericData;
        //            PairData pairData;
        //            while (gdit.hasNext()) {
        //                genericData = gdit.nextGenericData();
        //
        //                if (genericData.getDataType() == JOEDataType.JOE_PAIR_DATA) {
        //                    ps.printf(">  <%s>", genericData.getAttribute());
        //                    ps.println();
        //                    pairData = (PairData) genericData;
        //                    ps.println(pairData.toString(IOTypeHolder.instance().getIOType("SDF")));
        //                    ps.println();
        //                }
        //            }
        //        }
        ps.println(" /END          0    0");

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

    /**
     *  Gets the property attribute of the ClearTextFormat object.
     *  Attention: bond properties can have the twice of the size of atoms.
     *  Use the CTX_BOND_INDEX data element to get the correct bond
     *  indices for this properties !!!
     *  This causes from circumstance, that in JOELib bonds are not stored twice !!!
     *
     * @return                  The property value
     * @exception  IOException  Description of the Exception
     */
    private synchronized boolean getProperty(Molecule mol, int propType)
        throws IOException
    {
        String line;
        Vector tv = new Vector();
        int i;
        int counter;

        // get property name
        String propName = lnr.readLine();

        // get property unit
        String propUnit = lnr.readLine();

        // get property description
        String propDesc = lnr.readLine();

        // get data type
        String type = lnr.readLine();

        if (type.equals("R"))
        {
            type = DynamicArrayResult.DOUBLE;
        }
        else if (propUnit.equalsIgnoreCase("[Boolean]") && type.equals("I"))
        {
            type = DynamicArrayResult.BOOLEAN;
        }
        else if (type.equals("I"))
        {
            type = DynamicArrayResult.INT;
        }

        //else if (type.equals("S"))
        //{
        //    // o.k., accept this data type
        //}
        else
        {
            logger.error("data type " + type + " not supported in " + propName +
                ".");

            return false;
        }

        line = lnr.readLine();
        tv.clear();
        HelperMethods.tokenize(tv, line);

        // check number of properties
        counter = Integer.parseInt((String) tv.get(1));

        if (propType == ClearTextFormat.ATOM_PROPERTY)
        {
            if (mol.getAtomsSize() != counter)
            {
                logger.error("Wrong number of atoms in atom property " +
                    propName + ". Should be " + mol.getAtomsSize() +
                    " but it's " + counter + ".");

                return false;
            }
        }
        else if (propType == ClearTextFormat.BOND_PROPERTY)
        {
            if ((mol.getBondsSize() != counter) &&
                    ((mol.getBondsSize() * 2) != counter))
            {
                logger.error("Wrong number of bonds in bond property " +
                    propName + ". Should be " + mol.getBondsSize() +
                    " but it's " + counter + ".");

                return false;
            }
        }

        // get internal String representation for this properties
        StringBuffer sb = new StringBuffer((20 * 3) + (counter * 20));

        // store property type
        if (propType == ClearTextFormat.ATOM_PROPERTY)
        {
            sb.append(AtomDynamicResult.ATOM_PROPERTY);
            sb.append(HelperMethods.eol);
        }
        else if (propType == ClearTextFormat.BOND_PROPERTY)
        {
            sb.append(BondDynamicResult.BOND_PROPERTY);
            sb.append(HelperMethods.eol);
        }
        else if (propType == ClearTextFormat.MOLECULE_PROPERTY)
        {
            sb.append("molecule_property");
            sb.append(HelperMethods.eol);
        }
        else if (propType == ClearTextFormat.ENSEMBLE_PROPERTY)
        {
            sb.append("ensemble_property");
            sb.append(HelperMethods.eol);
        }
        else
        {
            sb.append("undefined_property");
            sb.append(HelperMethods.eol);
        }

        // store property description and property unit
        // empty lines are not allowed
        if (propDesc.trim().length() == 0)
        {
            sb.append("?");
        }
        else
        {
            sb.append(propDesc);
        }

        sb.append(HelperMethods.eol);

        if (propUnit.trim().length() == 0)
        {
            sb.append("?");
        }
        else
        {
            sb.append(propUnit);
        }

        sb.append(HelperMethods.eol);

        //store data type and data
        //    if((propType==this.MOLECULE_PROPERTY || propType==this.ENSEMBLE_PROPERTY)&&
        //       counter==1)
        //    {
        //      sb.append(lnr.readLine());
        //    }
        //    else{
        if (!type.equals("S"))
        {
            sb.append(type);
            sb.append(HelperMethods.eol);
        }

        sb.append(counter);
        sb.append(HelperMethods.eol);

        for (i = 0; i < counter; i++)
        {
            sb.append(lnr.readLine());

            if (i != (counter - 1))
            {
                sb.append(HelperMethods.eol);
            }
        }

        //    }
        BasicPairData dp = new BasicPairData();
        dp.setKey(propName);
        dp.setKeyValue(sb.toString());
        mol.addData(dp);

        if (logger.isDebugEnabled())
        {
            // check data parser and verbose parsed data
            dp = (BasicPairData) mol.getData(propName);
            logger.debug("get '" + propName + "' as " +
                dp.getKeyValue().getClass().getName());
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
