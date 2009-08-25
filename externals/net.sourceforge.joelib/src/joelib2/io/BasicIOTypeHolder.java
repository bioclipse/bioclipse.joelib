///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicIOTypeHolder.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
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
package joelib2.io;

import cformat.PrintfFormat;
import cformat.PrintfStream;

import joelib2.util.HelperMethods;

import joelib2.util.types.BasicStringString;
import joelib2.util.types.StringString;

import wsi.ra.tool.BasicPropertyHolder;

import java.io.ByteArrayOutputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Holder for input/output types for molecules.
 * Molecule import/export types are defined in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file.
 *
 * <p>
 * File types can be defined by using <tt>joelib2.filetypes.NUMBER.name</tt>.
 * The default representating class will be taken from the internal list.
 * If you want another representating class use additional
 * <tt>joelib2.filetypes.NUMBER.representation</tt>.<br>
 * Example:
 * <blockquote><pre>
 * joelib2.filetypes.1.name           = SDF
 * joelib2.filetypes.1.representation = joelib2.io.types.MDLSD
 * joelib2.filetypes.2.name           = SMILES
 * joelib2.filetypes.3.name           = MOL2
 * </pre></blockquote>
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:34 $
 * @see wsi.ra.tool.BasicPropertyHolder
 * @see wsi.ra.tool.BasicResourceLoader
 */
public final class BasicIOTypeHolder implements IOTypeHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicIOTypeHolder.class.getName());
    private static BasicIOTypeHolder instance;
    private final static StringString[] defaultTypes =
        {
            new BasicStringString("UNDEFINED", "joelib2.io.types.Undefined"),
            new BasicStringString("ALCHEMY", "joelib2.io.types.Alchemy"),
            new BasicStringString("BALLSTICK", "joelib2.io.types.BallAndStick"),
            new BasicStringString("BGF", ""),
            new BasicStringString("BMP", "joelib2.io.types.BMP"),
            new BasicStringString("BMIN", ""),
            new BasicStringString("BOX", "joelib2.io.types.Box"),
            new BasicStringString("BIOSYM", "joelib2.io.types.BiosymCar"),
            new BasicStringString("CML",
                "joelib2.io.types.ChemicalMarkupLanguage"),
            new BasicStringString("CTX", "joelib2.io.types.ClearTextFormat"),
            new BasicStringString("CHARMM", ""),
            new BasicStringString("CADPAC", ""),
            new BasicStringString("CHEM3D1", ""),
            new BasicStringString("CHEM3D2", ""),
            new BasicStringString("CCC", "joelib2.io.types.CCC"),
            new BasicStringString("CACAO", "joelib2.io.types.Cacao"),
            new BasicStringString("CACAOINT", "joelib2.io.types.CacaoInternal"),
            new BasicStringString("CACHE", "joelib2.io.types.Cache"),
            new BasicStringString("CHEMDRAW", "joelib2.io.types.ChemDraw"),
            new BasicStringString("CSR", "joelib2.io.types.CSR"),
            new BasicStringString("CSSR", "joelib2.io.types.CSSR"),
            new BasicStringString("DELPDB", "joelib2.io.types.DelphiPDB"),
            new BasicStringString("DMOL", "joelib2.io.types.DMol"),
            new BasicStringString("DOCK", ""),
            new BasicStringString("FDAT", ""),
            new BasicStringString("FIX", "joelib2.io.types.FixSmiles"),
            new BasicStringString("FEATURE", "joelib2.io.types.Feat"),
            new BasicStringString("FRACT", ""),
            new BasicStringString("FH", "joelib2.io.types.FenskeZMat"),
            new BasicStringString("GAMESSIN", "joelib2.io.types.Gamess"),
            new BasicStringString("GAMESSOUT", "joelib2.io.types.Gamess"),
            new BasicStringString("GAUSSIANZMAT", ""),
            new BasicStringString("GAUSSIANCART", "joelib2.io.types.Gaussian"),
            new BasicStringString("GAUSSIAN92", ""),
            new BasicStringString("GAUSSIAN94", ""),
            new BasicStringString("GHEMICAL", "joelib2.io.types.Ghemical"),
            new BasicStringString("GIF", "joelib2.io.types.GIF"),
            new BasicStringString("GSTAT", ""),
            new BasicStringString("GROMOS96A", "joelib2.io.types.Gromos96A"),
            new BasicStringString("GROMOS96N", "joelib2.io.types.Gromos96A"),
            new BasicStringString("HIN", "joelib2.io.types.HIN"),
            new BasicStringString("ICON8", ""),
            new BasicStringString("IDATM", ""),
            new BasicStringString("JAGUARIN", "joelib2.io.types.Jaguar"),
            new BasicStringString("JAGUAROUT", "joelib2.io.types.Jaguar"),
            new BasicStringString("JCAMP", "joelib2.io.types.JCAMP"),
            new BasicStringString("JPEG", "joelib2.io.types.JPEG"),
            new BasicStringString("MOL2", "joelib2.io.types.SybylMol2"),
            new BasicStringString("MM2IN", ""),
            new BasicStringString("MM2OUT", ""),
            new BasicStringString("MM3", ""),
            new BasicStringString("MMADS", ""),
            new BasicStringString("MMD", "joelib2.io.types.MacroMode"),
            new BasicStringString("MOLIN", ""),
            new BasicStringString("MOLINVENT", ""),
            new BasicStringString("MPQC", "joelib2.io.types.MPQC"),
            new BasicStringString("M3D", ""),
            new BasicStringString("MOPACCART",
                "joelib2.io.types.MopacCartesian"),
            new BasicStringString("MOPACINT", ""),
            new BasicStringString("MOPACOUT", "joelib2.io.types.Mopac"),
            new BasicStringString("MACCS", ""),
            new BasicStringString("MATLAB", "joelib2.io.types.Matlab"),
            new BasicStringString("MSF", ""),
            new BasicStringString("NWCHEMIN", ""),
            new BasicStringString("NWCHEMOUT", ""),
            new BasicStringString("JOEBINARY", "joelib2.util.JOEBinaryIO "),
            new BasicStringString("PREP", "joelib2.io.types.Amber"),
            new BasicStringString("PCMODEL", ""),
            new BasicStringString("PDB", "joelib2.io.types.PDB"),
            new BasicStringString("PDF", "joelib2.io.types.PDF"),
            new BasicStringString("PNG", "joelib2.io.types.PNG"),
            new BasicStringString("POV", "joelib2.io.types.POVRay"),
            new BasicStringString("PPM", "joelib2.io.types.PPM"),
            new BasicStringString("QCHEMIN", "joelib2.io.types.QChem"),
            new BasicStringString("QCHEMOUT", "joelib2.io.types.QChem"),
            new BasicStringString("RDF", ""),
            new BasicStringString("REPORT", "joelib2.util.Report"),
            new BasicStringString("SDF", "joelib2.io.types.MDLSD"),
            new BasicStringString("SMILES", "joelib2.io.types.Smiles"),
            new BasicStringString("SMIRKS", ""),
            new BasicStringString("SCHAKAL", ""),
            new BasicStringString("SHELX", ""),
            new BasicStringString("SPARTAN", ""),
            new BasicStringString("SPARTANSEMI", ""),
            new BasicStringString("SPARTANMM", ""),
            new BasicStringString("TITLE", "joelib2.io.types.Title"),
            new BasicStringString("TINKER", "joelib2.io.types.Tinker"),
            new BasicStringString("UNICHEM", "joelib2.io.types.UniChem"),
            new BasicStringString("XED", "joelib2.io.types.XED"),
            new BasicStringString("XYZ", "joelib2.io.types.XYZ"),
            new BasicStringString("ZIP", "joelib2.io.types.ZIP")
        };
    private static final int DEFAULT_TYPES_NUMBER = 21;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable<String, BasicIOType> inputExtensions;
    private Hashtable<String, BasicIOType> outputExtensions;
    private Hashtable<String, BasicIOType> typeHolder;
    private int typeNumber;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Initializes the input/output holder factory.
     */
    private BasicIOTypeHolder()
    {
        // initialize hash tables
        typeHolder = new Hashtable<String, BasicIOType>(DEFAULT_TYPES_NUMBER);

        // assume two extensions for each type
        inputExtensions = new Hashtable<String, BasicIOType>(
                DEFAULT_TYPES_NUMBER << 1);
        outputExtensions = new Hashtable<String, BasicIOType>(
                DEFAULT_TYPES_NUMBER << 1);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the instance of the input/output holder factory.
     *
     * @return the instance of the input/output holder factory
     */
    public static synchronized BasicIOTypeHolder instance()
    {
        if (instance == null)
        {
            instance = new BasicIOTypeHolder();
            instance.loadFileTypes();
        }

        return instance;
    }

    /**
     * Returns <tt>true</tt> if this file with the appropriate input type can be readed.
     *
     * @param  filename  the filename of the molecule file
     * @return           <tt>true</tt> if this file with the appropriate input type can be readed
     */
    public boolean canReadExtension(String filename)
    {
        Vector vs = new Vector();
        HelperMethods.tokenize(vs, filename, ".\n\t");

        if (vs.size() == 0)
        {
            return false;
        }

        String ext = ((String) vs.get(vs.size() - 1)).toUpperCase();
        BasicIOType ioType = (BasicIOType) inputExtensions.get(ext);

        boolean read = false;

        if (ioType != null)
        {
            read = true;
        }

        return read;
    }

    /**
     * Returns <tt>true</tt> if this file with the appropriate output type can be written.
     *
     * @param  filename  the filename of the molecule file
     * @return           <tt>true</tt> if this file with the appropriate output type can be written
     */
    public boolean canWriteExtension(String filename)
    {
        Vector vs = new Vector();
        HelperMethods.tokenize(vs, filename, ".\n\t");

        if (vs.size() == 0)
        {
            return false;
        }

        String ext = ((String) vs.get(vs.size() - 1)).toUpperCase();
        BasicIOType ioType = (BasicIOType) outputExtensions.get(ext);

        boolean write = false;

        if (ioType != null)
        {
            write = true;
        }

        return write;
    }

    /**
     * Gets an appropriate molecule input/output type for the given filename.
     *
     * @param  filename  the molecule filename
     * @return           the input/output type for the given filename
     */
    public BasicIOType filenameToType(String filename)
    {
        Vector vs = new Vector();
        HelperMethods.tokenize(vs, filename, ".\n\t");

        if (vs.size() == 0)
        {
            return null;
        }

        String ext = ((String) vs.get(vs.size() - 1)).toUpperCase();
        BasicIOType ioType = (BasicIOType) inputExtensions.get(ext);

        if (ioType != null)
        {
            return ioType;
        }
        else
        {
            ioType = (BasicIOType) outputExtensions.get(ext);
        }

        if (ioType != null)
        {
            return ioType;
        }

        //System.out.println("extension:"+ext+" "+outputExtensions.get(ext));
        //try now slopyy match
        //get the first found entry with matching file extension
        for (Enumeration e = getFileTypes(); e.hasMoreElements();)
        {
            ioType = (BasicIOType) e.nextElement();

            MoleculeFileIO mfType = null;

            try
            {
                mfType = MoleculeFileHelper.getMoleculeFileType(ioType);
            }
            catch (MoleculeIOException ex)
            {
                logger.error(ex.getMessage());

                return null;
            }

            if (mfType != null)
            {
                String[] extensions = mfType.inputFileExtensions();

                if (extensions != null)
                {
                    for (int i = 0; i < extensions.length; i++)
                    {
                        if (ext.indexOf(extensions[i]) != -1)
                        {
                            return ioType;
                        }
                    }
                }

                extensions = mfType.outputFileExtensions();

                if (extensions != null)
                {
                    for (int i = 0; i < extensions.length; i++)
                    {
                        if (ext.indexOf(extensions[i]) != -1)
                        {
                            return ioType;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Gets an enumeration with all available file types.
     *
     * @return    the enumeration with available file types
     */
    public Enumeration getFileTypes()
    {
        return typeHolder.elements();
    }

    /**
     *  Gets the IOType from a given input/output name.
     *  The name must be in upper case letters.
     *
     * @param  name  Description of the Parameter
     * @return       The iOType value
     */
    public BasicIOType getIOType(String name)
    {
        BasicIOType ioType = (BasicIOType) typeHolder.get(name);

        // try to get io type
        if (ioType != null)
        {
            return ioType;
        }

        // try now with BIG letters (... ohhh, BIG hands ... only for Futurama fans)
        else
        {
            ioType = (BasicIOType) typeHolder.get(name.toUpperCase());

            return ioType;
        }

        //    else return typeHolder.get("UNDEFINED");
        //    return null;
    }

    /**
     * Returns <tt>true</tt> if this input type is readable.
     *
     * @param  name  input type
     * @return       <tt>true</tt> if this input type is readable
     */
    public boolean isReadable(String name)
    {
        BasicIOType ioType = (BasicIOType) typeHolder.get(name);

        if (ioType == null)
        {
            return false;
        }
        else
        {
            MoleculeFileIO mfType = null;

            try
            {
                mfType = MoleculeFileHelper.getMoleculeFileType(ioType);
            }
            catch (MoleculeIOException ex)
            {
                logger.error(ex.getMessage());

                return false;
            }

            if (mfType != null)
            {
                return mfType.readable();
            }

            return false;
        }
    }

    /**
     * Returns <tt>true</tt> if this output type is writeable.
     *
     * @param  name  output type
     * @return       <tt>true</tt> if this output type is writeable
     */
    public boolean isWriteable(String name)
    {
        BasicIOType ioType = (BasicIOType) typeHolder.get(name);

        if (ioType == null)
        {
            return false;
        }
        else
        {
            MoleculeFileIO mfType = null;

            try
            {
                mfType = MoleculeFileHelper.getMoleculeFileType(ioType);
            }
            catch (MoleculeIOException ex)
            {
                logger.error(ex.getMessage());

                return false;
            }

            if (mfType != null)
            {
                return mfType.writeable();
            }

            return false;
        }
    }

    /**
     * Shows a table of all available input/output types.
     *
     * @return    the table of all available input/output types
     */
    public String toString()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
        PrintfStream ps = new PrintfStream(baos);
        PrintfFormat s13 = new PrintfFormat("%-13s");
        PrintfFormat s25 = new PrintfFormat("%-25s");
        PrintfFormat d4 = new PrintfFormat("%4d");
        int index = 0;
        ps.println(
            "     Name          Readable      Writeable     Description");
        ps.println(
            "----------------------------------------------------------");

        BasicIOType ioType;

        for (Enumeration e = getFileTypes(); e.hasMoreElements(); index++)
        {
            ioType = (BasicIOType) e.nextElement();

            MoleculeFileIO mfType = null;

            try
            {
                mfType = MoleculeFileHelper.getMoleculeFileType(ioType);
            }
            catch (MoleculeIOException ex)
            {
                logger.error(ex.getMessage());

                return null;
            }

            if (mfType != null)
            {
                ps.printf(d4, index);
                ps.print(' ');
                ps.printf(s13, ioType.getName());
                ps.print(' ');
                ps.printf(s13, "" + mfType.readable());
                ps.print(' ');
                ps.printf(s13, "" + mfType.writeable());
                ps.print(' ');

                if (mfType.inputDescription() != null)
                {
                    ps.printf(s25, mfType.inputDescription());
                }
                else if (mfType.outputDescription() != null)
                {
                    ps.printf(s25, mfType.outputDescription());
                }

                ps.printf(s25, "");
                ps.print(' ');

                if (e.hasMoreElements())
                {
                    ps.print('\n');
                }
            }
        }

        return baos.toString();
    }

    /**
     *  Description of the Method
     *
     * @param  types  Description of the Parameter
     * @return        Description of the Return Value
     */
    private boolean loadFileTypes()
    {
        String name;
        String representation;

        //    String descriptionFile;
        Properties prop = BasicPropertyHolder.instance().getProperties();
        BasicIOType ioType;
        MoleculeFileIO mfType = null;
        String[] inExt;
        String[] outExt;

        // add UNDEFINED molecule loader
        ioType = new BasicIOType(defaultTypes[0].getStringValue1(),
                defaultTypes[0].getStringValue2(), typeNumber++);
        typeHolder.put(defaultTypes[0].getStringValue1(), ioType);

        // add other molecule types
        boolean allInfosLoaded = true;
        int i = 0;

        while (true)
        {
            i++;
            name = prop.getProperty("joelib2.filetypes." + i + ".name");

            //      System.out.println("joelib2.filetypes." + i + ".name:"+name);
            if (name == null)
            {
                logger.info("" + (i - 1) + " input/output types loaded.");

                break;
            }

            representation = prop.getProperty("joelib2.filetypes." + i +
                    ".representation");

            // load default representation if no other representation was defined
            if (representation == null)
            {
                for (int j = 0; j < defaultTypes.length; j++)
                {
                    if (defaultTypes[j].getStringValue1().equals(name))
                    {
                        representation = defaultTypes[j].getStringValue2();
                    }
                }
            }

            if ((name != null) && (representation != null))
            {
                ioType = new BasicIOType(name, representation, typeNumber);

                //check if IOType representation exists
                mfType = null;

                try
                {
                    mfType = MoleculeFileHelper.getMoleculeFileType(ioType);
                }
                catch (MoleculeIOException ex)
                {
                    ex.printStackTrace();
                    System.exit(1);
                }

                if (mfType == null)
                {
                    // representation don't exist
                    continue;
                }
                else
                {
                    // o.k., representation exists
                    typeHolder.put(name, ioType);

                    // get input extensions
                    inExt = mfType.inputFileExtensions();

                    if (mfType.readable() && (inExt != null))
                    {
                        for (int m = 0; m < inExt.length; m++)
                        {
                            inputExtensions.put(inExt[m].toUpperCase(), ioType);
                        }
                    }

                    // get output extensions
                    outExt = mfType.outputFileExtensions();

                    if (mfType.writeable() && (outExt != null))
                    {
                        for (int m = 0; m < outExt.length; m++)
                        {
                            outputExtensions.put(outExt[m].toUpperCase(),
                                ioType);
                        }
                    }

                    typeNumber++;
                }
            }
            else
            {
                allInfosLoaded = false;

                logger.error("File type " + name + " number " + i +
                    " not properly defined.");
            }
        }

        return allInfosLoaded;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
