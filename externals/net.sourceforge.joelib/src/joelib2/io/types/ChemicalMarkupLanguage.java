///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ChemicalMarkupLanguage.java,v $
//  Purpose:  Reader/Writer for CML files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2005/03/03 07:13:49 $
//            $Author: wegner $
//  Original Author: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
//  Original Version: Chemical Development Kit,  http://sourceforge.net/projects/cdk
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

import joelib2.io.MoleculeFileIO;
import joelib2.io.PropertyWriter;

import joelib2.io.types.cml.CDOInterface;
import joelib2.io.types.cml.CMLErrorHandler;
import joelib2.io.types.cml.CMLHandler;
import joelib2.io.types.cml.CMLMoleculeWriter;
import joelib2.io.types.cml.CMLResolver;
import joelib2.io.types.cml.CMLWriterProperties;
import joelib2.io.types.cml.MoleculeArray;
import joelib2.io.types.cml.MoleculeAttributeArray;
import joelib2.io.types.cml.MoleculeFileCDO;
import joelib2.io.types.cml.MoleculeHuge;
import joelib2.io.types.cml.MoleculeLarge;

import joelib2.molecule.BasicMoleculeVector;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicPropertyHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Category;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;


/**
 * Reader/Writer for Chemical Markup Language (CML) files.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/03/03 07:13:49 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class ChemicalMarkupLanguage implements MoleculeFileIO, PropertyWriter,
    CMLWriterProperties
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            ChemicalMarkupLanguage.class.getName());
    private final static String description = "Chemical Markup Language (CML)";
    private final static String[] extensions = new String[]{"cml"};
    public static final int OUTPUT_HUGE = 0;
    public static final int OUTPUT_LARGE = 1;
    public static final int OUTPUT_ARRAY = 2;
    public static final int OUTPUT_ATTRIBUTE_ARRAY = 3;
    public static final String OUTPUT_HUGE_S = "huge";
    public static final String OUTPUT_LARGE_S = "large";
    public static final String OUTPUT_ARRAY_S = "array";
    public static final String OUTPUT_ATTRIBUTE_ARRAY_S = "attributearray";
    public static final float CML_VERSION_1 = 1.0f;
    public static final float CML_VERSION_2 = 2.0f;
    public static final String DEFAULT_DELIMITER = " ";
    private static String DEFAULT_NAMESPACE = "cml";
    private static String DEFAULT_XML_DECLARATION =
        "http://www.xml-cml.org/schema/cml2/core";
    private static float cmlDefaultVersion = CML_VERSION_2;
    private static String defaultDelimiter = DEFAULT_DELIMITER;

    //~ Instance fields ////////////////////////////////////////////////////////

    private MoleculeFileCDO cdo;
    private int cmlOutputType = OUTPUT_HUGE;

    private CMLMoleculeWriter cmlOutputWriter = null;
    private Pattern endPattern = Pattern.compile("</.*:molecule.*>");
    private boolean forceFormalCharge = false;
    private boolean impliciteHydrogens = false;

    //    private ContentHandler handler;
    //  private EntityResolver resolver;
    private InputStreamReader isr;
    private LineNumberReader lnr;
    private boolean moleculeReaded = false;
    private int molReadedIndex;
    private MoleculeVector molVector = new BasicMoleculeVector();
    private String namespace = DEFAULT_NAMESPACE;
    private XMLReader parser;
    private boolean partialCharge = false;
    private PrintStream ps;
    private Pattern startPattern = Pattern.compile("<.*:molecule.*>");
    private boolean storeChemistryKernelInfo = true;
    private boolean symmetryInformations = false;
    private boolean useNamespace = true;
    private boolean useSlowerMemorySavingPreparser = false;
    private String xmlDeclaration = DEFAULT_XML_DECLARATION;

    //~ Methods ////////////////////////////////////////////////////////////////

    public static String getDefaultDelimiter()
    {
        return defaultDelimiter;
    }

    public void closeReader() throws IOException
    {
    }

    public void closeWriter() throws IOException
    {
        if(namespace!=null && namespace.trim().length()!=0){
            ps.println("\n</"+namespace+":list>");
        }
        else{
            ps.println("\n</list>");
        }

        ps.close();
    }

    public boolean forceWriteFormalCharge()
    {
        return forceFormalCharge;
    }

    public float getCMLversion()
    {
        return cmlDefaultVersion;
    }

    public String getNamespace()
    {
        return this.namespace;
    }

    public String getXMLDeclaration()
    {
        return xmlDeclaration;
    }

    /**
     *  Description of the Method
     *
     * @param iStream          Description of the Parameter
     * @exception IOException  Description of the Exception
     */
    public void initReader(InputStream iStream) throws IOException
    {
        initProperties();

        isr = new InputStreamReader(iStream);

        if (useSlowerMemorySavingPreparser)
        {
            lnr = new LineNumberReader(isr);
        }

        initSAXparser(true);

        moleculeReaded = false;
        molReadedIndex = 0;
        cdo.setMoleculeSetOfMolecules(molVector);

        //cdo.setMoleculeCallback(this);
    }

    /**
     *  Description of the Method
     *
     * @param os               Description of the Parameter
     * @exception IOException  Description of the Exception
     */
    public void initWriter(OutputStream os) throws IOException
    {
        initProperties();

        //        ps = new OutputStreamWriter(os);
        ps = new PrintStream(os);

        switch (cmlOutputType)
        {
        case OUTPUT_HUGE:
            cmlOutputWriter = new MoleculeHuge(ps, this);

            break;

        case OUTPUT_LARGE:
            cmlOutputWriter = new MoleculeLarge(ps, this);

            break;

        case OUTPUT_ARRAY:
            cmlOutputWriter = new MoleculeArray(ps, this);

            break;

        case OUTPUT_ATTRIBUTE_ARRAY:
            cmlOutputWriter = new MoleculeAttributeArray(ps, this);

            break;
        }

        //System.out.println(cmlOutputWriter.getClass().getName());
        //ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        ps.println("<?xml version=\"" + cmlDefaultVersion +
            "\" encoding=\"ISO-8859-1\"?>");

        //ps.println("<!DOCTYPE molecule SYSTEM \"cml.dtd\" []>");
        String listOptions =
            "xmlns:cml=\"http://www.xml-cml.org/schema/cml2/core\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core cmlAll.xsd\"";

        if ((namespace != null) && (namespace.trim().length() != 0))
        {
            ps.println("<" + namespace + ":list " + listOptions + ">");
        }
        else
        {
            ps.println("<list " + listOptions + ">");
        }
    }

    /**
     * Description of the Method
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
        StringBuffer molecule = null;
        String line;

        while ((line = lnr.readLine()) != null)
        {
            Matcher endMatcher = endPattern.matcher(line);
            Matcher startMatcher = startPattern.matcher(line);

            if (endMatcher.matches())
            {
                //System.out.println(line+" ::: "+matcher.matches()+" "+matcher.end());
                molecule.append(line.substring(0, endMatcher.end()));

                break;
            }
            else if (startMatcher.matches())
            {
                //System.out.println(line+" ::: "+matcher.matches()+" "+matcher.start());
                molecule = new StringBuffer(10000);
                molecule.append(line.substring(startMatcher.start()));
                molecule.append(HelperMethods.eol);
            }
            else
            {
                if (molecule != null)
                {
                    molecule.append(line);
                    molecule.append(HelperMethods.eol);
                }
            }
        }

        if (line == null)
        {
            return null;
        }
        else
        {
            //System.out.println("##################\n"+molecule);
            return molecule.toString();
        }
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol) throws IOException
    {
        return read(mol, null);
    }

    /**
     * Loads an molecule in MDL SD-MOL format and sets the title.
     * If <tt>title</tt> is <tt>null</tt> the title line in
     * the molecule file is used.
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol, String title)
        throws IOException
    {
        if (useSlowerMemorySavingPreparser)
        {
            String molecule = read();

            if (molecule == null)
            {
                return false;
            }

            initSAXparser(false);

            StringReader stringReader = new StringReader(molecule);
            cdo.setMolecule(mol);
            startSAXparser(stringReader);

            return true;
        }
        else
        {
            if (!moleculeReaded)
            {
                cdo.setMolecule(mol);
                startSAXparser(isr);

                // and send signal for finishing outer loop
                // in the next round!!!
                moleculeReaded = true;
                molReadedIndex = 0;
            }

            if ((molReadedIndex < this.molVector.getSize()))
            {
                mol.set(this.molVector.getMol(molReadedIndex));
                molReadedIndex++;

                return true;
            }
        }

        return false;
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

    public boolean storeChemistryKernelInfo()
    {
        return storeChemistryKernelInfo;
    }

    public boolean useNamespace()
    {
        return this.useNamespace;
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException
    {
        return write(mol, "Undefined");
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
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
        //        if (!done)
        //        {
        //            if (!fragment)
        //            {
        //              ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        //              ps.println("<?xml version=\""+cmlDefaultVersion+"\" encoding=\"ISO-8859-1\"?>");
        //              ps.println("<!DOCTYPE molecule SYSTEM \"cml.dtd\" []>");
        //            }
        //            if (object instanceof SetOfMolecules)
        //            {
        //                write((SetOfMolecules) object);
        //            }
        //            else if (object instanceof Molecule)
        //            {
        //                write((Molecule) object);
        cmlOutputWriter.writeMolecule(mol, writePairData, attribs2write);

        //            }
        //            else
        //            {
        //                throw new UnsupportedChemObjectException("Only supported are SetOfMolecules and Molecule.");
        //            }
        //            if (!fragment)
        //            {
        //                done = true;
        //            }
        //        }
        //        else
        //        {
        //        }
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

    public boolean writeImpliciteHydrogens()
    {
        return impliciteHydrogens;
    }

    public boolean writePartialCharge()
    {
        return partialCharge;
    }

    public boolean writeSymmetryInformations()
    {
        return symmetryInformations;
    }

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    private void initProperties()
    {
        String value;

        value = BasicPropertyHolder.instance().getProperty(this, "output");

        if (value == null)
        {
            cmlOutputType = OUTPUT_HUGE;
        }
        else if (value.equalsIgnoreCase(OUTPUT_HUGE_S))
        {
            cmlOutputType = OUTPUT_HUGE;
        }
        else if (value.equalsIgnoreCase(OUTPUT_LARGE_S))
        {
            cmlOutputType = OUTPUT_LARGE;
        }
        else if (value.equalsIgnoreCase(OUTPUT_ARRAY_S))
        {
            cmlOutputType = OUTPUT_ARRAY;
        }
        else if (value.equalsIgnoreCase(OUTPUT_ATTRIBUTE_ARRAY_S))
        {
            cmlOutputType = OUTPUT_ATTRIBUTE_ARRAY;
        }
        else
        {
            logger.error("Use output type :" + OUTPUT_HUGE_S + ", " +
                OUTPUT_LARGE_S + " and " + OUTPUT_ARRAY_S);
            cmlOutputType = OUTPUT_HUGE;
        }

        //System.out.println("output: '"+value+"' "+cmlOutputType);
        value = BasicPropertyHolder.instance().getProperty(this,
                "output.force.formalCharge");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            forceFormalCharge = true;
        }
        else
        {
            forceFormalCharge = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "output.partialCharge");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            partialCharge = true;
        }
        else
        {
            partialCharge = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "output.hydrogenCount");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            impliciteHydrogens = true;
        }
        else
        {
            impliciteHydrogens = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "output.symmetryInformations");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            symmetryInformations = true;
        }
        else
        {
            symmetryInformations = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "output.useNamespace");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            useNamespace = true;
        }
        else
        {
            useNamespace = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "output.storeChemistryKernelInfo");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            storeChemistryKernelInfo = true;
        }
        else
        {
            storeChemistryKernelInfo = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "useSlowerMemorySavingPreparser");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            useSlowerMemorySavingPreparser = true;
        }
        else
        {
            useSlowerMemorySavingPreparser = false;
        }

        //dtdResourceDir = PropertyHolder.instance().getProperty(this,"DTD.resourceDir");
        double dTmp = BasicPropertyHolder.instance().getDouble(this,
                "output.defaultVersion", 0);

        if (!Double.isNaN(dTmp))
        {
            cmlDefaultVersion = (float) dTmp;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "defaultDelimiter");

        if (value != null)
        {
            defaultDelimiter = value;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "output.namespace");

        if (value != null)
        {
            namespace = value;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "output.xmlDeclaration");

        if (value != null)
        {
            xmlDeclaration = value;
        }
    }

    private void initSAXparser(boolean verbose) throws IOException
    {
        boolean success = false;

        // Aelfred is prefered.

        /*                if (!success)
                                        {
                                                        try
                                                        {
                                                                        parser = new gnu.xml.aelfred2.XmlReader();
                                                                        logger.info("Using Aelfred2 XML parser.");
                                                                        success = true;
                                                        }
                                                        catch (Exception e)
                                                        {
                                                                        logger.warn("Could not instantiate Aelfred2 XML reader!");
                                                        }
                                        }*/

        // If Aelfred is not available try Xerces
        if (!success)
        {
            try
            {
                parser = new org.apache.xerces.parsers.SAXParser();

                if (verbose)
                {
                    logger.info("Using Xerces XML parser.");
                }

                success = true;
            }
            catch (Exception e)
            {
                logger.warn("Could not instantiate Xerces XML reader!");
            }
        }

        if (!success)
        {
            throw new IOException("Could not instantiate any XML parser!");
        }

        cdo = new MoleculeFileCDO();

        try
        {
            parser.setFeature("http://xml.org/sax/features/validation", false);

            if (verbose)
            {
                logger.info("Deactivated validation");
            }
        }
        catch (SAXException e)
        {
            logger.warn("Cannot deactivate validation.");
        }

        parser.setContentHandler(new CMLHandler((CDOInterface) cdo));
        parser.setEntityResolver(new CMLResolver());
        parser.setErrorHandler(new CMLErrorHandler());
    }

    private void startSAXparser(Reader reader)
    {
        try
        {
            parser.parse(new InputSource(reader));
        }
        catch (IOException e)
        {
            logger.warn("IOException: " + e.toString());
        }
        catch (SAXParseException saxe)
        {
            SAXParseException spe = (SAXParseException) saxe;
            String error = "Found well-formedness error in line " +
                spe.getLineNumber();
            logger.error(error);
        }
        catch (SAXException saxe)
        {
            logger.warn("SAXException: " + saxe.getClass().getName());
            logger.warn(saxe.toString());
            saxe.printStackTrace();
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
