///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MDLSD.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner, Wayne Volkmuth volkmuth@renovis.com
//  Version:  $Revision: 1.18 $
//            $Date: 2007/03/03 00:03:49 $
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
import cformat.ScanfFormat;
import cformat.ScanfReader;

import joelib2.data.BasicAromaticityTyper;
import joelib2.data.BasicElementHolder;
import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.FeatureHelper;

import joelib2.feature.result.StringVectorResult;

import joelib2.feature.types.atomlabel.AtomIsChiral;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;
import joelib2.feature.types.bondlabel.BondKekuleType;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;
import joelib2.io.PropertyWriter;

import joelib2.math.BasicVector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.BondHelper;
import joelib2.molecule.IsomerismHelper;
import joelib2.molecule.KekuleHelper;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.BasicRGroupData;
import joelib2.molecule.types.PairData;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.NbrAtomIterator;
import joelib2.util.iterator.PairDataIterator;

import joelib2.util.types.BasicIntInt;

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
 *  Reader/Writer for SDF files.
 *
 * For speeding up descriptor molecule files have a look at the {@link joelib2.feature.ResultFactory}.
 *
 * @.author     wegnerj
 * @.author Wayne Volkmuth
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.18 $, $Date: 2007/03/03 00:03:49 $
 * @.cite mdlMolFormat
 */
public class MDLSD implements MoleculeFileIO, PropertyWriter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(MDLSD.class
            .getName());

    /**
     *  Description of the Field
     */
    public static String DIMENSION_2D = "2D";
    public static String DIMENSION_3D = "3D";
    public static String dimension = DIMENSION_3D;
    private final static String description = "MDL SD file";
    private final static String[] extensions =
        new String[]{"sdf", "sd", "mdl", "mol", "smol"};

    //  private final static ScanfFormat f10 = new ScanfFormat("%10f");
    private final static ScanfFormat s3sf = new ScanfFormat("%3s");

    //  private final static ScanfFormat d2 = new ScanfFormat("%2d");
    //  private final static ScanfFormat s2 = new ScanfFormat("%2s");
    private final static ScanfFormat d4sf = new ScanfFormat("%4d");
    private final static ScanfFormat d3sf = new ScanfFormat("%3d");
    private final static PrintfFormat d3pf = new PrintfFormat("%3d");
    private final static PrintfFormat d4pf = new PrintfFormat("%4d");
    private final static String sdfDelimiter = "$$$$";

    //~ Instance fields ////////////////////////////////////////////////////////

    private LineNumberReader lnr;
    private boolean overWriteCommentWithKernelInfo = true;
    private PrintfStream ps;

    //  private final static ScanfFormat s6 = new ScanfFormat("%6s");
    private boolean writeAromaticAsKekule = false;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the MDLSD object
     *
     * @return    Description of the Return Value
     */
    public MDLSD()
    {
        //  Determine if we should write this structure as a Kekule structure
        java.util.Properties prop = wsi.ra.tool.BasicPropertyHolder.instance()
                                                                   .getProperties();

        String className = this.getClass().getName();
        String writeKekuleProp = prop.getProperty(className +
                ".writeAromaticityAsKekuleSystem", "false");

        if (writeKekuleProp.equalsIgnoreCase("true"))
        {
            writeAromaticAsKekule = true;
        }

        String kernelProp = prop.getProperty(className +
                ".overWriteCommentWithKernelInfo", "true");

        if (kernelProp.equalsIgnoreCase("true"))
        {
            overWriteCommentWithKernelInfo = true;
        }
        
        if(!BasicAromaticityTyper.instance().isUseAromaticityModel()){
            if(writeAromaticAsKekule){
                logger.warn("A switched OFF aromaticity model causes switching OFF the kekulization mode also.");
            }
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

    /**
     *  Description of the Method
     *
     * @param  is               Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
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
        String line;

        while ((line = lnr.readLine()) != null)
        {
            if ((line.length() > 0) &&
                    (line.charAt(0) == sdfDelimiter.charAt(0)) &&
                    (line.indexOf(sdfDelimiter) != -1))
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
        if (logger.isDebugEnabled())
        {
            logger.debug("Read molecule");
        }

        // delete molecule data
        mol.clear();
        mol.beginModify();

        // read molecule block
        if (!readMolecule(mol, title))
        {
            return false;
        }

        // read SDF molecule properties
        String actualLine = readSDFProps(mol);

        if (logger.isDebugEnabled())
        {
            logger.debug("Read optional molecule properties");
        }

        // read additional molecule properties (features/descriptors)
        if (!readOtherProps(mol, actualLine))
        {
            return false;
        }

        // Do NOT nuke loaded atom and bond labels
        mol.endModify(false);

        return true;
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
        String line;

        while ((line = lnr.readLine()) != null)
        {
            if ((line.length() > 0) && (line.charAt(0) == '$') &&
                    (line.indexOf(sdfDelimiter) != -1))
            {
                break;
            }
        }

        return true;
    }

    /**
     *  Writes a molecule with his <tt>PairData</tt> .
     *
     * @param  mol              Description of the Parameter
     * @return                  Description of the Return Value
     * @exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException
    {
        return write(mol, null, true, null);
    }

    /**
     *  Writes a molecule with his <tt>PairData</tt> .
     *
     * @param  mol              Description of the Parameter
     * @param  title            the molecule title or <tt>null</tt> if the title
     *      from the molecule should be used
     * @return                  <tt>true</tt> if the molecule and the data has
     *      been succesfully written.
     * @exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title) throws IOException
    {
        return write(mol, title, true, null);
    }

    /**
     *  Writes a molecule with his <tt>PairData</tt> .
     *
     * @param  mol              the molecule with additional data
     * @param  title            the molecule title or <tt>null</tt> if the title
     *      from the molecule should be used
     * @param  writePairData    if <tt>true</tt> then the additional molecule data
     *      is written
     * @param  attribs2write    Description of the Parameter
     * @return                  <tt>true</tt> if the molecule and the data has
     *      been succesfully written.
     * @exception  IOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title, boolean writePairData,
        List attribs2write) throws IOException
    {
        //System.out.println(mol.toString(IOTypeHolder.instance().getIOType("SMILES")));
        //    System.out.println("write mol with "+attribs2write);
        writeTitle(mol, title);
        writeFormat(mol);
        writeComment(mol);
        writeHeaderLine(mol);

        List<BasicIntInt> charges = new Vector<BasicIntInt>(5);
        List<BasicIntInt> isotopes = new Vector<BasicIntInt>(5);
        writeAtoms(mol, charges, isotopes);
        writeBonds(mol);

        writeSDFProps(mol, charges, isotopes);
        writeOtherProps(mol, writePairData, attribs2write);

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
     *  Gets the float attribute of the MDLSD class
     *
     * @param  line      Description of the Parameter
     * @param  startPos  Description of the Parameter
     * @param  endPos    Description of the Parameter
     * @return           The float value
     */
    private static float getFloat(String line, int startPos, int endPos)
        throws MoleculeIOException
    {
        // check start and end positions
        if ((startPos < 0) || (endPos > (line.length() + 1)))
        {
            return 0;
        }

        String sub = null;
        String subTrim = null;
        float value = 0;

        try
        {
            // get trimmed substring
            sub = line.substring(startPos, endPos);
            subTrim = sub.trim();

            // try to convert substring to integer
            value = Float.parseFloat(subTrim);
        }
        catch (Exception e)
        {
            logger.error("Can not convert string '" + subTrim +
                "' to type 'float' in line: " + line);
            throw new MoleculeIOException(e.toString());
        }

        sub = null;
        subTrim = null;

        return value;
    }

    /**
     *  Gets the integer attribute of the MDLSD class
     *
     * @param  line      Description of the Parameter
     * @param  startPos  Description of the Parameter
     * @param  endPos    Description of the Parameter
     * @return           The integer value
     */
    private static int getInteger(String line, int startPos, int endPos)
        throws MoleculeIOException
    {
        // check start and end positions
        if ((startPos < 0) || (endPos > (line.length() + 1)))
        {
            return 0;
        }

        String sub = null;
        String subTrim = null;
        int value = 0;

        try
        {
            // get trimmed substring
            sub = line.substring(startPos, endPos);
            subTrim = sub.trim();

            // try to convert substring to integer
            value = Integer.parseInt(subTrim);
        }
        catch (Exception e)
        {
            logger.error("Can not convert string '" + subTrim +
                "' to type 'int' in line: " + line);
            throw new MoleculeIOException(e.toString());
        }

        sub = null;
        subTrim = null;

        return value;
    }

    /**
     * @param type
     * @return
     */
    private int getAtomicNumber(Atom atom, String type, int lineNbr)
        throws IOException, MoleculeIOException
    {
        int atomicNumber;

        if (type.charAt(0) == 'D')
        {
            if (type.length() == 1)
            {
                type = "H";
                atom.setIsotope(2);
            }
            else if (type.charAt(1) == 'u')
            {
                type = "Xx";
            }
        }

        atomicNumber = BasicElementHolder.instance().getAtomicNum(type);

        if (atomicNumber == 0)
        {
            if ((type.equals("Xx") || type.equals("Du")) == false)
            {
                skipReaderEntry();
                throw new MoleculeIOException("Unknown atom type '" + type +
                    "' in atom line #" + lineNbr);
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Dummy atom '" + type + "' in atom line #" +
                        lineNbr);
                }
            }
        }

        return atomicNumber;
    }

    /**
     * @param bond
     * @return
     */
    private int getBondType(Bond bond)
    {
        int bondType;

        // please remember that the aromaticity typer assign ONLY aromaticity flags and
        // NOT the internal aromatic bond order
        if ((bond.getBondOrder() == BondHelper.AROMATIC_BO) ||
                BondInAromaticSystem.isAromatic(bond))
        {
            if (writeAromaticAsKekule && BasicAromaticityTyper.instance().isUseAromaticityModel())
            {
                if (BondKekuleType.getKekuleType(bond) ==
                        KekuleHelper.KEKULE_DOUBLE)
                {
                    bondType = 2;
                }
                else if (BondKekuleType.getKekuleType(bond) ==
                        KekuleHelper.KEKULE_TRIPLE)
                {
                    bondType = 3;
                }
                else if (BondKekuleType.getKekuleType(bond) ==
                    KekuleHelper.KEKULE_SINGLE)
                {
                    bondType = 1;
                }
                else{
                    // so kekulization failed and this is an aromatic bond order
                    bondType = 4;
                }
            }
            else
            {
                bondType = 4;
            }
        }
        else
        {
            // 1 single
            // 2 double
            bondType = bond.getBondOrder();
        }

        return bondType;
    }

    /**
     * @param atom
     */
    private int getCharge(Atom atom, List<BasicIntInt> charges)
    {
        int charge = 0;

        if ((atom.getFormalCharge() >= -3) && (atom.getFormalCharge() <= 3))
        {
            switch (atom.getFormalCharge())
            {
            case 1:
                charge = 3;

                break;

            case 2:
                charge = 2;

                break;

            case 3:
                charge = 1;

                break;

            case -1:
                charge = 5;

                break;

            case -2:
                charge = 6;

                break;

            case -3:
                charge = 7;

                break;
            }
        }
        else
        {
            charges.add(new BasicIntInt(atom.getIndex(),
                    atom.getFormalCharge()));
        }

        return charge;
    }

    /**
     * @param bond
     * @return
     */
    private int getStereo(Bond bond)
    {
        int stereoFlag = 0;

        //check stereochemistry: up/down and cis/trans
        int isomerism = IsomerismHelper.isCisTransBond(bond);

        if ((isomerism != IsomerismHelper.EZ_ISOMERISM_UNDEFINED) ||
                ((bond.getFlags() & BondHelper.IS_WEDGE) != 0) ||
                ((bond.getFlags() & BondHelper.IS_HASH) != 0))
        {
            if (bond.isWedge())
            {
                // wedge bond
                stereoFlag = 1;
            }
            else if (bond.isHash())
            {
                // hatch bond
                stereoFlag = 6;
            }
            else if (isomerism != IsomerismHelper.EZ_ISOMERISM_UNDEFINED)
            {
                stereoFlag = 3;
            }
        }
        else
        {
            stereoFlag = 0;
        }

        return stereoFlag;
    }

    /**
     * @param line
     * @param lineNbr
     * @return
     * @throws MoleculeIOException
     * @throws IOException
     */
    private String readAtomElement(String line, int lineNbr)
        throws MoleculeIOException, IOException
    {
        String type;

        try
        {
            type = line.substring(31, 34).trim();
        }
        catch (Exception e)
        {
            skipReaderEntry();
            throw new MoleculeIOException("No atom type defined. " +
                "Error in atom line #" + lineNbr + ". " + e.getMessage());
        }

        ;

        return type;
    }

    /**
    * @param mol
    * @param atom
    * @return
    */
    private boolean readAtoms(Molecule mol, Atom atom, BasicVector3D xyz,
        int lineNbr) throws MoleculeIOException, IOException
    {
        String line;
        double x;
        double y;
        double z;
        String type;
        int charge = 0;
        int massDifference;

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        // ignore empty lines in atom block
        if (line.trim().length() == 0)
        {
            logger.warn("Skipping empty line in atom block.");

            return true;
        }

        // if(debug)logger.debug("atom "+(i+1)+" line:"+line);
        // scanf = new ScanfReader(new StringReader(line));
        // scanf.useCstandard(false);
        try
        {
            // fast
            x = getFloat(line, 0, 10);
            y = getFloat(line, 10, 20);
            z = getFloat(line, 20, 30);
        }
        catch (MoleculeIOException ex)
        {
            skipReaderEntry();
            throw new MoleculeIOException("No coordinates defined. " +
                "Error in atom line #" + lineNbr + ". " + ex.getMessage());
        }

        type = readAtomElement(line, lineNbr);

        try
        {
            // get mass difference
            massDifference = getInteger(line, 34, 36);

            // get and parse charge of atom
            charge = getInteger(line, 36, 39);

            // slow
            //        x = scanf.scanFloat(f10);
            //        y = scanf.scanFloat(f10);
            //        z = scanf.scanFloat(f10);
            //        type = scanf.scanString(s3);
            //        massDifference = scanf.scanInt(d2);
            //        charge = scanf.scanInt(d3);
            // don't use this informations if not explicitely necessary
            // some databases don't contain all informations !!!
            //            String stereoIgnored = scanf.scanString(s3);
            //            int minQueryHatoms = scanf.scanInt(d3);
            //            String stereoCareBox = scanf.scanString(s3);
            //            int numImpAndExplBonds = scanf.scanInt(d3);
            //            String hDesignator = scanf.scanString(s3);
            //            // 1=reactant, 2=product, 3=intermediate
            //            int reactComponent = scanf.scanInt(d3);
            //            int reactCompNumber = scanf.scanInt(d3);
            //            int atomMappingNumber = scanf.scanInt(d3);
            //            int inversionFlag = scanf.scanInt(d3);
            //            int changeFlag = scanf.scanInt(d3);
        }
        catch (MoleculeIOException ex)
        {
            skipReaderEntry();
            throw new MoleculeIOException("Error in atom line #" + lineNbr +
                ". " + ex.getMessage());
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("type: " + type + " massDifference:" + massDifference +
                " charge:" + charge);
        }

        xyz.setX3D(x);
        xyz.setY3D(y);
        xyz.setZ3D(z);
        atom.setCoords3D(xyz);
        atom.setAtomicNumber(getAtomicNumber(atom, type, lineNbr));

        //atom.setType(type);
        setAtomCharge(atom, charge);

        if (!mol.addAtomClone(atom))
        {
            skipReaderEntry();
            throw new MoleculeIOException("Atom " + lineNbr +
                " could not be added.");
        }

        atom.clear();

        return true;
    }

    /**
     * @param mol
     * @param i
     * @return
     */
    private boolean readBonds(Molecule mol, int i) throws IOException,
        MoleculeIOException
    {
        String line;
        int start;
        int end;
        int order;
        int flag;
        int stereo;

        flag = 0;

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        // ignore empty lines in bond block
        if (line.trim().length() == 0)
        {
            logger.warn("Skipping empty line in bond block.");

            return true;
        }

        //            if(debug)logger.debug("bond "+(i+1)+" line:"+line);
        //      scanf = new ScanfReader(new StringReader(line));
        //      scanf.useCstandard(false);
        //    r1 = buffer;
        try
        {
            // fast
            start = getInteger(line, 0, 3);
            end = getInteger(line, 3, 6);
        }
        catch (MoleculeIOException ex)
        {
            skipReaderEntry();
            throw new MoleculeIOException("Error in bond line #" + (i + 1) +
                ". " + ex.getMessage());
        }

        if (start == end)
        {
            throw new MoleculeIOException("Bond can not connect atom " + start +
                " with itself. Error in bond line #" + (i + 1) + ".");
        }

        try
        {
            order = getInteger(line, 6, 9);

            //                  short topology = (short) getInteger(line,15,18);
            //                  short center   = (short) getInteger(line,18,21);
            // slow
            //        start = scanf.scanInt(d3);
            //        end = scanf.scanInt(d3);
            //        order = scanf.scanInt(d3);
            if (order == 4)
            {
                order = BondHelper.AROMATIC_BO;
            }

            if (line.length() >= 12)
            {
                //handle wedge/hash data
                //fast
                stereo = getInteger(line, 9, 12);
                //System.out.println(mol.getTitle()+" stereo="+stereo);

                //slow
                //          stereo = scanf.scanInt(d3);
                if (stereo != 0)
                {
                    // for single bonds
                    if (stereo == 1)
                    {
                        flag |= BondHelper.IS_WEDGE;
                        System.out.println(mol.getTitle()+" wedge");
                    }
                    else if (stereo == 6)
                    {
                        flag |= BondHelper.IS_HASH;
                        System.out.println(mol.getTitle()+" hash");
                           }

                    //else if (stereo == 3)
                    //{
                    //  // for double bonds
                    //    // cis/trans
                    //}
                }
            }
        }
        catch (MoleculeIOException ex)
        {
            skipReaderEntry();
            throw new MoleculeIOException("Error in bond line #" + (i + 1) +
                ". " + ex.getMessage());
        }

        if (!mol.addBond(start, end, order, flag))
        {
            skipReaderEntry();
            throw new MoleculeIOException("Bond could not be added.");

            //return ( false );
        }

        return true;
    }

    /**
     *
     */
    private boolean readHeader(Molecule mol, String title) throws IOException
    {
        String line;

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("molecule:" + line);
        }

        if (title == null)
        {
            mol.setTitle(line);
        }
        else
        {
            mol.setTitle(title);
        }

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        //creator
        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        String comment = "";

        //comment
        if (line.length() > 0)
        {
            comment = line;
        }

        if ((comment != null) && (comment.trim().length() != 0))
        {
            StringVectorResult commentData = new StringVectorResult();
            commentData.addString(comment);
            commentData.setKey(FeatureHelper.COMMENT_IDENTIFIER);
            mol.addData(commentData);
        }

        return true;
    }

    private boolean readMolecule(Molecule mol, String title)
        throws MoleculeIOException, IOException
    {
        int i;
        int natoms;
        int nbonds;

        // read header: title, format, and comment
        if (!readHeader(mol, title))
        {
            return false;
        }

        // read number of atoms and bonds
        BasicIntInt numbers = new BasicIntInt();

        if (!readNumbers(numbers))
        {
            return false;
        }

        natoms = numbers.intValue1;
        nbonds = numbers.intValue2;

        BasicVector3D xyz = new BasicVector3D();
        Atom atom = mol.newAtom();

        // initialize internal arrays and flags
        mol.reserveAtoms(natoms);

        // read atoms block
        for (i = 1; i <= natoms; i++)
        {
            if (!readAtoms(mol, atom, xyz, i))
            {
                return false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("atoms successful loaded.");
        }

        // read bonds block
        for (i = 0; i < nbonds; i++)
        {
            if (!readBonds(mol, i))
            {
                return false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("bonds successful loaded.");
        }

        return true;
    }

    /**
     * @param mol
     * @param numbers
     * @return
     */
    private boolean readNumbers(BasicIntInt numbers) throws IOException,
        MoleculeIOException
    {
        String line;

        if ((line = lnr.readLine()) == null)
        {
            return (false);
        }

        //    scanf = new ScanfReader(new StringReader(line));
        //    scanf.useCstandard(false);
        try
        {
            // fast
            numbers.intValue1 = getInteger(line, 0, 3);
            numbers.intValue2 = getInteger(line, 3, 6);

            // slow
            //      natoms = scanf.scanInt(d3);
            //      nbonds = scanf.scanInt(d3);
            // don't use this informations if not explicitely necessary
            // some databases don't contain all informations !!!
            //        int numAtomLists = scanf.scanInt(d3);
            //        int obsolete = scanf.scanInt(d3);
            //        boolean chiral = (scanf.scanInt(d3)==1)?true:false;
            //        int numSTextEntries = scanf.scanInt(d3);
            //        int numReactComp_1 = scanf.scanInt(d3);
            //        int numReactants = scanf.scanInt(d3);
            //        int numProducts = scanf.scanInt(d3);
            //        int numIntermediates = scanf.scanInt(d3);
            //        int obsoletePropLines = scanf.scanInt(d3);
            //        String ctabVersion = scanf.scanString(s6);
        }
        catch (MoleculeIOException ex)
        {
            skipReaderEntry();
            throw new MoleculeIOException(
                "Error in atom/bond definition line.");
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("atoms: " + numbers.intValue1 + " bonds:" +
                numbers.intValue2);

            //            logger.debug("atomLists: " + numAtomLists + " chiral:" + chiral +
            //                         " STextEntries:" + numSTextEntries+ " reactComponents+1:" + numReactComp_1+ " reactants:" + numReactants+
            //                         " products:" + numProducts+ " intermediates:" + numIntermediates+ " ctab version:" + ctabVersion);
        }

        return true;
    }

    private boolean readOtherProps(Molecule mol, String firstLine)
        throws MoleculeIOException, IOException
    {
        String attribute;
        String line = firstLine;

        // be more robust for interchanged M END / descriptor entries
        boolean ignoreFirst = false;

        if ((line != null) && (line.charAt(0) == '>'))
        {
            ignoreFirst = true;
        }

        while (ignoreFirst || ((line = lnr.readLine()) != null))
        {
            ignoreFirst = false;

            if ((line.length() != 0) && (line.charAt(0) == '>'))
            {
                int begin = line.indexOf('<');
                int end2 = line.lastIndexOf('>');

                // check and get data item name and add it to the list
                if ((begin > 0) && (end2 > 0))
                {
                    attribute = line.substring(begin + 1, end2);
                }
                else
                {
                    return false;
                }

                //System.out.println("data entry '"+attribute+"':"+line);
                // make as large as possible
                StringBuffer dataEntry = new StringBuffer(500);

                // read additional data until empty line or
                // line starts with $$$$
                readSingleFeature(attribute, dataEntry);

                BasicPairData dp = new BasicPairData();
                dp.setKey(attribute);
                dp.setKeyValue(dataEntry.toString());
                mol.addData(dp);

                if (logger.isDebugEnabled())
                {
                    logger.debug("data '" + attribute + "' added with value: " +
                        dataEntry.toString());
                }

                // slow's down parsing ... let's just use dataEntry=null;
                //        dataEntry.delete(0,dataEntry.length());
                dataEntry = null;
            }

            if ((line == null) || (line.length() == 0))
            {
                continue;
            }

            if ((line.charAt(0) == '$') && (line.indexOf(sdfDelimiter) != -1))
            {
                break;
            }
        }

        return true;
    }

    private String readSDFProps(Molecule mol) throws MoleculeIOException,
        IOException
    {
        String line = null;
        boolean readProperties = true;
        BasicRGroupData rgroup = new BasicRGroupData();

        while (readProperties && ((line = lnr.readLine()) != null))
        {
            if ((line.length() != 0) && (line.charAt(0) == 'M'))
            {
                // read properties and end when
                // 'M END' occurs
                readProperties = storeSDFProps(mol, line, rgroup);
            }
            else
            {
                // some databases don't contain a M END tag.
                // The data block is delimited with a white space, so
                // just break this loop
                break;
            }

            line = null;
        }

        // were there any Rgroups initialized ?
        if ((rgroup != null) && (rgroup.getRGroups().size() != 0))
        {
            //System.out.println("RGROUP added.");
            mol.addData(rgroup);
        }

        return line;
    }

    /**
         * @param previousLine
         */
    private void readSingleFeature(String attribute, StringBuffer dataEntry)
        throws IOException, MoleculeIOException
    {
        boolean succeed = true;
        String line;

        String previousLine = lnr.readLine();

        while (succeed)
        {
            if (previousLine == null)
            {
                skipReaderEntry();
                throw new MoleculeIOException("Data entry <" + attribute +
                    "> has no data.");
            }

            line = lnr.readLine();

            // if(debug)logger.debug("data entry '"+attribute+"':"+line);
            if ((line != null) && (line.length() != 0) &&
                    (line.charAt(0) != '$'))
            {
                dataEntry.append(previousLine);
                dataEntry.append(HelperMethods.eol);
                previousLine = line;
            }
            else
            {
                // add all lines which are not the end tag: $$$$
                if ((line != null) && (line.length() == 0))
                {
                    if (previousLine.length() != 0)
                    {
                        dataEntry.append(previousLine);
                    }

                    succeed = false;
                }
                else if ((line == null) ||
                        ((line.charAt(0) == '$') &&
                            (line.indexOf(sdfDelimiter) != -1)))
                {
                    succeed = false;
                }
                else
                {
                    dataEntry.append(previousLine);
                    succeed = false;
                }
            }
        }
    }

    /**
         * @param charge
         */
    private void setAtomCharge(Atom atom, int charge)
    {
        switch (charge)
        {
        case 0:
            break;

        case 3:
            atom.setFormalCharge(1);

            break;

        case 2:
            atom.setFormalCharge(2);

            break;

        case 1:
            atom.setFormalCharge(3);

            break;

        case 5:
            atom.setFormalCharge(-1);

            break;

        case 6:
            atom.setFormalCharge(-2);

            break;

        case 7:
            atom.setFormalCharge(-3);

            break;

        // case 4: radical
        }
    }

    /**
     * @param scanf
     */
    private void storeCharges(Molecule mol, ScanfReader scanf)
        throws IllegalArgumentException, IOException
    {
        //charge
        int entries = scanf.scanInt(d3sf);
        int i1;
        int i2;

        for (int i = 0; i < entries; i++)
        {
            i1 = scanf.scanInt(d4sf);
            i2 = scanf.scanInt(d4sf);
            mol.getAtom(i1).setFormalCharge(i2);
        }
    }

    /**
     * @param mol
     * @param scanf
     */
    private void storeIsotopes(Molecule mol, ScanfReader scanf)
        throws IllegalArgumentException, IOException
    {
        int i;
        int i1;
        int i2;
        int entries = scanf.scanInt(d3sf);

        for (i = 0; i < entries; i++)
        {
            i1 = scanf.scanInt(d4sf);
            i2 = scanf.scanInt(d4sf);
            mol.getAtom(i1).setIsotope(i2);
        }
    }

    /**
     * @param mol
     * @param scanf
     * @param rgroup
     */
    private void storeRGroups(ScanfReader scanf, BasicRGroupData rgroup)
        throws IllegalArgumentException, IOException
    {
        int i;
        int i1;
        int i2;
        int entries = scanf.scanInt(d3sf);

        for (i = 0; i < entries; i++)
        {
            // atom index
            i1 = scanf.scanInt(d4sf);

            // Rgroup number
            i2 = scanf.scanInt(d4sf);

            //System.out.println("ADD RGROUP: "+intValue1+" "+intValue2);
            rgroup.add(new BasicIntInt(i1, i2));
        }
    }

    /**
         * @param mol
         * @param line
         * @param rgroup
         * @return
         */
    private boolean storeSDFProps(Molecule mol, String line,
        BasicRGroupData rgroup) throws IllegalArgumentException, IOException
    {
        ScanfReader scanf = new ScanfReader(new StringReader(line));
        scanf.scanString(s3sf);

        String value = scanf.scanString(s3sf);
        boolean readProperties = true;

        switch (value.charAt(0))
        {
        case 'C':

            if (value.equals("CHG"))
            {
                storeCharges(mol, scanf);
            }

            break;

        case 'E':

            if (value.equals("END"))
            {
                readProperties = false;
            }

            break;

        case 'I':

            if (value.equals("ISO"))
            {
                storeIsotopes(mol, scanf);
            }

            break;

        case 'R':

            //if (value.equals("RAD"))
            //{
            //    //radical
            //} else
            if (value.equals("RGP"))
            {
                storeRGroups(scanf, rgroup);
            }

            break;
        }

        return readProperties;
    }

    /**
     * @param mol
     */
    private void writeAtoms(Molecule mol, List<BasicIntInt> charges,
        List<BasicIntInt> isotopes)
    {
        Atom atom;
        AtomIterator ait = mol.atomIterator();
        PrintfFormat f10_4 = new PrintfFormat("%10.4f");
        PrintfFormat s3 = new PrintfFormat("%-3s");
        PrintfFormat d2 = new PrintfFormat("%2d");

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (atom.getIsotope() != 0)
            {
                isotopes.add(new BasicIntInt(atom.getIndex(),
                        atom.getIsotope()));
            }

            ps.printf(f10_4, atom.get3Dx());
            ps.printf(f10_4, atom.get3Dy());
            ps.printf(f10_4, atom.get3Dz());
            ps.print(' ');
            ps.printf(s3,
                BasicElementHolder.instance().getSymbol(
                    atom.getAtomicNumber()));

            // mass difference for isotopes
            ps.printf(d2, 0);

            // charge
            ps.printf(d3pf, getCharge(atom, charges));

            // stereo ignored
            ps.printf(d3pf, 0);

            // minimal number of H atoms
            ps.printf(d3pf, 0);

            // stereo care box
            ps.printf(d3pf, 0);

            // valence, number of implicite and explicite atoms connected to this atom
            ps.printf(d3pf, 0);

            // H designator
            ps.printf(d3pf, 0);

            // component: 1=reactant, 2=product, 3=intermediate
            ps.printf(d3pf, 0);

            // number of reaction components
            ps.printf(d3pf, 0);

            // atom mapping number
            ps.printf(d3pf, 0);

            // inversion flag
            ps.printf(d3pf, 0);

            // change flag
            ps.printf(d3pf, 0);
            ps.println();
        }
    }

    /**
     * @param mol
     */
    private void writeBonds(Molecule mol)
    {
        //so the bonds come out sorted
        Atom nbr;
        Bond bond;
        Atom atom;
        AtomIterator ait = mol.atomIterator();
        int writtenBonds = 0;

        // unstable, if people are interested to use the molecule for further things
        // DO NOT USE HERE or clone molecule before using it !
        //if(writeAromaticAsKekule)mol.kekulize();
        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            NbrAtomIterator nait = atom.nbrAtomIterator();

            while (nait.hasNext())
            {
                nbr = nait.nextNbrAtom();

                if (atom.getIndex() < nbr.getIndex())
                {
                    writtenBonds++;
                    bond = nait.actualBond();

                    //check, if aromatic bond
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("bond " + bond.getIndex() + "(" +
                            bond.getBeginIndex() + "," + bond.getEndIndex() +
                            ") has bond order " + bond.getBondOrder());
                    }

                    ps.printf(d3pf, bond.getBeginIndex());
                    ps.printf(d3pf, bond.getEndIndex());
                    ps.printf(d3pf, getBondType(bond));
                    ps.printf(d3pf, getStereo(bond));
                    ps.printf(d3pf, 0);
                    ps.printf(d3pf, 0);
                    ps.printf(d3pf, 0);
                    ps.println();
                }
            }
        }

        if (writtenBonds != mol.getBondsSize())
        {
            logger.warn("Some bonds were not stored for " + mol.getTitle() +
                "!");
        }
    }

    /**
     * @param mol
     */
    private void writeComment(Molecule mol)
    {
        if (!overWriteCommentWithKernelInfo)
        {
            if (mol.hasData(FeatureHelper.COMMENT_IDENTIFIER))
            {
                StringVectorResult commentData = (StringVectorResult) mol
                    .getData(FeatureHelper.COMMENT_IDENTIFIER);
                ps.println(commentData.toString());
            }
            else
            {
                ps.println(
                    "Used JOELib chemistry kernel (expert systems) ID is " +
                    IdentifierExpertSystem.instance().getKernelHash());
            }
        }
        else
        {
            ps.println("Used JOELib chemistry kernel (expert systems) ID is " +
                IdentifierExpertSystem.instance().getKernelHash());
        }
    }

    /**
     * @param mol
     */
    private void writeFormat(Molecule mol)
    {
        if (mol.has2D())
        {
            dimension = DIMENSION_2D;
        }
        else
        {
            dimension = DIMENSION_3D;
        }

        ps.printf("  -ISIS-            %s", dimension);
        ps.println();
    }

    /**
     * @param mol
     */
    private void writeHeaderLine(Molecule mol)
    {
        // number of atoms
        ps.printf(d3pf, mol.getAtomsSize());

        // number of bonds
        ps.printf(d3pf, mol.getBondsSize());

        // number of atom lists
        ps.printf(d3pf, 0);

        // obsolete
        ps.printf(d3pf, 0);

        // chiral flag: 0=not_chiral, 1=chiral
        int chiral = 0;

        for (int atomIdx = 1; atomIdx <= mol.getAtomsSize(); atomIdx++)
        {
            if (AtomIsChiral.isChiral(mol.getAtom(atomIdx)))
            {
                chiral = 1;

                break;
            }
        }

        ps.printf(d3pf, chiral);

        // number of sTest entries
        ps.printf(d3pf, 0);

        // number of reaction components+1
        ps.printf(d3pf, 0);

        // number of reactants
        ps.printf(d3pf, 0);

        // number of products
        ps.printf(d3pf, 0);

        // number if intermediates
        ps.printf(d3pf, 0);

        // number of lines of additional properties
        // including the M END line.
        // Obsolete: Default=999
        ps.printf(d3pf, 1);

        // Ctab version: V2000 or V3000
        ps.println(" V2000");
    }

    /**
         * @param mol
         */
    private void writeOtherProps(Molecule mol, boolean writePairData,
        List attribs2write)
    {
        // write additional descriptor data
        IOType sdf = BasicIOTypeHolder.instance().getIOType("SDF");

        if (writePairData)
        {
            PairData pairData;

            // write all descriptors
            if (attribs2write == null)
            {
                PairDataIterator gdit = mol.genericDataIterator();

                while (gdit.hasNext())
                {
                    pairData = gdit.nextPairData();

                    ps.printf(">  <%s>", pairData.getKey());
                    ps.println();
                    ps.println(pairData.toString(sdf));
                    ps.println();

                    //                        DescResult tmpPropResult;
                    //                        try {
                    //                          tmpPropResult = DescriptorHelper.instance().descFromMol(mol, genericData.getAttribute());
                    //                          AtomProperties atomProperties;
                    //                          if (JOEHelper.hasInterface(tmpPropResult, "AtomProperties"))
                    //                          {
                    //                                  atomProperties = (AtomProperties) tmpPropResult;
                    //                                  int atoms = mol.numAtoms();
                    //                                  for (int j = 0; j < atoms; j++)
                    //                                  {
                    //                                          System.out.println(mol.getTitle()+" "+genericData.getAttribute()+" "+atomProperties.getDoubleValue(j + 1));
                    //                                  }
                    //                          }
                    //                        } catch (DescriptorException e) {
                    //                          e.printStackTrace();
                    //                        }
                }
            }

            // write only descriptors specified in attrib2write
            else
            {
                int size = attribs2write.size();

                for (int i = 0; i < size; i++)
                {
                    // get unparsed data
                    //          System.out.println("write "+ attribs2write.get(i));
                    pairData = mol.getData((String) attribs2write.get(i),
                            false);

                    if (pairData == null)
                    {
                        logger.warn((String) attribs2write.get(i) +
                            " data entry don't exist in molecule: " +
                            mol.getTitle());
                    }
                    else
                    {
                        ps.printf(">  <%s>", pairData.getKey());
                        ps.println();
                        ps.println(pairData.toString(sdf));
                        ps.println();
                    }
                }
            }
        }

        ps.println(sdfDelimiter);
    }

    /**
     * @param mol
     * @param charges
     * @param isotopes
     */
    private void writeSDFProps(Molecule mol, List charges, List isotopes)
    {
        // write molecule properties
        // write atom charges
        if (charges.size() != 0)
        {
            for (int i = 0; i < charges.size(); i++)
            {
                ps.print("M  CHG  1 ");

                BasicIntInt ii = (BasicIntInt) charges.get(i);
                ps.printf(d3pf, ii.intValue1);
                ps.printf(d4pf, ii.intValue2);
                ps.println();
            }
        }

        // write isotope informations
        if (isotopes.size() != 0)
        {
            for (int i = 0; i < isotopes.size(); i++)
            {
                ps.print("M  ISO  1 ");

                BasicIntInt ii = (BasicIntInt) isotopes.get(i);
                ps.printf(d3pf, ii.intValue1);
                ps.printf(d4pf, ii.intValue2);
                ps.println();
            }
        }

        // write Rgroups
        //System.out.println("mol.hasData(JOEDataType.JOE_RGROUP_DATA)="+mol.hasData(JOEDataType.JOE_RGROUP_DATA));
        if (mol.hasData(BasicRGroupData.getName()))
        {
            List rg = ((BasicRGroupData) mol.getData(BasicRGroupData.getName()))
                .getRGroups();

            for (int i = 0; i < rg.size(); i++)
            {
                ps.print("M  RGP  1 ");

                BasicIntInt ii = (BasicIntInt) rg.get(i);
                ps.printf(d3pf, ii.intValue1);
                ps.printf(d4pf, ii.intValue2);
                ps.println();
            }
        }

        ps.println("M  END");
    }

    /**
     * @param title
     */
    private void writeTitle(Molecule mol, String title)
    {
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

        ps.println(setTitle);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
