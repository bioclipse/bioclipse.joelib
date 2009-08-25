///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ConvertExample.java,v $
//  Purpose:  Example for converting molecules.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.13 $
//            $Date: 2006/07/24 22:29:15 $
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
package joelib2.example;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.BasicMoleculeWriter;
import joelib2.io.BasicReader;
import joelib2.io.MoleculeCallback;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;
import joelib2.io.PropertyWriter;

import joelib2.io.types.cml.CMLSequentialSAXReader;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeHelper;

import joelib2.molecule.types.BasicPairData;

import joelib2.process.filter.HasAllDataFilter;
import joelib2.process.filter.NativeValueFilter;
import joelib2.process.filter.SMARTSFilter;

import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.StopWatch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Example for converting molecules and calculating descriptors.
 *
 * <p>
 * <blockquote><pre>
 * Usage:
 *java -cp . joelib2.test.Convert [options] &lt;input file> [&lt;output file>]
 *
 *Options:
 * [-i&lt;inputFormat>]       - Format of the input file
 * [-o&lt;outputFormat>]      - Format of the output file
 * [-h]                    - Removes all hydrogens from molecule
 * [+h]                    - Adds hydrogens to molecule
 * [+p]                    - Adds only polar hydrogens (+h implicit)
 * [-e]                    - Converts only non-empty molecules
 * [-d]                    - Remove all descriptors from the molecule
 * [+d]                    - Adds all available descriptors to the molecule
 * [+v]                    - Switch verbosity ON
 * [+snd]                  - Shows all available native value descriptors
 * [+sad]                  - Shows all available atom property descriptors
 * [+sall]                 - Shows all available descriptors
 * [-salt]                 - Strip salts and gets only largest contigous fragment
 * [+split&lt;SIZE>]       - Generated splitted output file of SIZE
 * [+x&lt;descriptor name>]   - Converts only molecules where &lt;descriptor name> exists
 * [-r&lt;skip  desc. rule>]  - Skips molecules, if rule fits
 * [+r&lt;conv. desc. rule>]  - Converts only molecules where rule fits
 * [+f&lt;lineStructure>]     - Required if you use FLAT output format which other input format
 * [+s&lt;lineStructure>]     - Can be used for an alternate SMILES entry line structure
 * [-m&lt;SMARTS rule>]       - Skips molecules, if SMARTS rule fits
 * [+m&lt;SMARTS rule>]       - Converts only molecules where SMARTS rule fits
 * [-um&lt;SMARTS rule>]      - Skips molecules, if SMARTS rule fits
 * [+um&lt;SMARTS rule>]      - Converts only molecules where SMARTS rule fits
 * [-?][--help]            - Shows this message
 *
 *If no output file is defined, all molecules will be written to stdout.
 *
 *Filter rules have the form:
 *&lt;native value descriptor>&lt;relation>&lt;value>
 *where &lt;relation> is &lt;, &lt;=, ==, >, >= or !=
 *Example:
 *"+rNumber_of_halogen_atoms==2"
 *
 *SMARTS filter rules have the form:
 *&lt;SMARTS pattern>&lt;relation>&lt;value>
 *where &lt;relation> is &lt;, &lt;=, ==, >, >= or !=
 *Example:
 *"+umaNC=O==1"
 *Converts all molecules, where the molecule contains ONE NC=O group connected to an aromatic atom (aNC=O).
 * </pre></blockquote>
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.13 $, $Date: 2006/07/24 22:29:15 $
 * @see joelib2.JOE
 * @see joelib2.process.filter.NativeValueFilter
 * @.cite smarts
 */
public class ConvertExample implements MoleculeCallback
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(ConvertExample.class
            .getName());
    public static final int CONTINUE = 0;
    public static final int STOP = 1;
    public static final int STOP_USAGE = 2;
    private static BasicIOType verboseType = BasicIOTypeHolder.instance()
                                                              .getIOType(
            "SMILES");

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public PrintStream generatedOutput;
    private int actualSplitNumber = 1;
    private boolean addDescriptors = false;
    private boolean addHydrogens = false;
    private HasAllDataFilter allDescFilter = null;
    private List exists = null;
    private String inputFile;
    private BasicIOType inType;
    private int molCounter = 0;
    private FileOutputStream out = null;
    private String outputFile;
    private BasicIOType outType;
    private boolean polarOnly = false;
    private boolean removeDescriptors = false;
    private boolean removeHydrogens = false;
    private List<NativeValueFilter> rules = null;
    private boolean skipEmpty = false;
    private List<SMARTSFilter> smartsRules = null;

    // not implemented
    private boolean splitFile = false;
    private int splitMoleculeNumber = 1;
    private int splitSize = 1;
    private boolean stripSalt = false;
    private boolean usePHvalueCorrection = true;
    private boolean verbose = false;
    private StopWatch watch = new StopWatch();
    private MoleculeFileIO writer = null;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        ConvertExample convert = new ConvertExample();

        int status = convert.parseCommandLine(args);

        //for (int i = 0; i < args.length; i++)
        //{
        //      System.out.println("ARG: "+args[i]);
        //}
        if (status == CONTINUE)
        {
            convert.convert();
        }
        else if (status == STOP_USAGE)
        {
            convert.usage();
            System.exit(1);
        }
        else if (status == STOP)
        {
            System.exit(0);
        }
    }

    /**
     *  A unit test for JUnit
     *
     */
    public void convert()
    {
        FileInputStream in = null;

        // get molecule loader/writer
        MoleculeFileIO loader = null;

        try
        {
            //      in = new BufferedInputStream(new FileInputStream(inputFile));
            in = new FileInputStream(inputFile);
        }
        catch (FileNotFoundException ex)
        {
            logger.error("Can not find input file: " + inputFile);

            return;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (outputFile != null)
        {
            try
            {
                if (splitFile)
                {
                    //      out = new BufferedOutputStream( new FileOutputStream(outputFile));
                    out = new FileOutputStream(getSplitName(outputFile,
                                splitMoleculeNumber - 1));
                    actualSplitNumber = 1;
                }
                else
                {
                    //      out = new BufferedOutputStream( new FileOutputStream(outputFile));
                    out = new FileOutputStream(outputFile);
                }

                writer = MoleculeFileHelper.getMolWriter(out, outType);
            }
            catch (FileNotFoundException ex)
            {
                logger.error("Can not create output file: " + outputFile);

                return;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            if (!writer.writeable())
            {
                logger.error(outType.getRepresentation() +
                    " is not writeable.");
                logger.error("You're invited to write one !;-)");

                return;
            }
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, outType);
        boolean success = true;
        watch.resetTime();
        logger.info("Start file conversion ...");

        if (inType.equals(BasicIOTypeHolder.instance().getIOType("CML")))
        {
            CMLSequentialSAXReader cmlReader = new CMLSequentialSAXReader();

            try
            {
                cmlReader.initReader(in, this);
            }
            catch (FileNotFoundException ex)
            {
                logger.error("Can not find input file: " + inputFile);

                return;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            try
            {
                cmlReader.read(mol);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            if (inType.equals(BasicIOTypeHolder.instance().getIOType("ZIP")))
            {
                // force slower preparser for CML files !!!
                BasicPropertyHolder.instance().getProperties().setProperty(
                    "joelib2.io.types.ChemicalMarkupLanguage.useSlowerMemorySavingPreparser",
                    "true");
                logger.info(
                    "Activating slower preparser for CML files in ZIP files.");
            }

            try
            {
                loader = MoleculeFileHelper.getMolReader(in, inType);

                if (!loader.readable())
                {
                    logger.error(inType.getRepresentation() +
                        " is not readable.");
                    logger.error("You're invited to write one !;-)");

                    return;
                }
            }
            catch (FileNotFoundException ex)
            {
                logger.error("Can not find input file: " + inputFile);

                return;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            for (;;)
            {
                mol.clear();

                try
                {
                    success = loader.read(mol);

                    if (!success)
                    {
                        break;
                    }

                    if (!handleSingle(mol))
                    {
                        break;
                    }
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();

                    return;
                }
                catch (MoleculeIOException ex)
                {
                    ex.printStackTrace();
                    molCounter++;
                    logger.info("Molecule entry (#" + molCounter +
                        ") was skipped: " + mol.getTitle());

                    continue;
                }
            }
        }

        try
        {
            if (!inType.equals(BasicIOTypeHolder.instance().getIOType("CML")))
            {
                if (loader != null)
                {
                    loader.closeReader();
                }
            }

            if (writer != null)
            {
                writer.closeWriter();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return;
        }

        logger.info("... " + molCounter +
            " molecules successful converted in " + watch.getPassedTime() +
            " ms.");
    }

    public boolean handleMolecule(Molecule mol)
    {
        try
        {
            handleSingle(mol);
        }
        catch (IOException ex)
        {
            logger.error(ex.getMessage());
            ex.printStackTrace();
        }
        catch (MoleculeIOException ex)
        {
            logger.error(ex.getMessage());
            molCounter++;
            logger.info("Molecule entry (#" + molCounter + ") was skipped: " +
                mol.getTitle());
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  args  Description of the Parameter
     * @return       Description of the Return Value
     */
    public int parseCommandLine(String[] args)
    {
        // get default properties
        Properties prop = BasicPropertyHolder.instance().getProperties();

        String arg;

        for (int i = 0; i < args.length; i++)
        {
            arg = args[i];

            if (arg.startsWith("--help"))
            {
                return STOP_USAGE;
            }
            else if (arg.startsWith("-?"))
            {
                return STOP_USAGE;
            }
            else if (arg.startsWith("-h"))
            {
                removeHydrogens = true;
            }
            else if (arg.startsWith("+h"))
            {
                addHydrogens = true;
            }
            else if (arg.startsWith("-pH"))
            {
                usePHvalueCorrection = false;
            }
            else if (arg.startsWith("+pH"))
            {
                usePHvalueCorrection = true;
            }
            else if (arg.startsWith("+p"))
            {
                addHydrogens = true;
                polarOnly = true;
            }
            else if (arg.startsWith("+v"))
            {
                verbose = true;
            }
            else if (arg.startsWith("-v"))
            {
                verbose = false;
            }
            else if (arg.startsWith("+split"))
            {
                splitFile = true;

                String size = arg.substring(6);

                //System.out.println("splitsize: "+size);
                if (size.trim().length() != 0)
                {
                    splitSize = Integer.parseInt(size);
                }

                splitMoleculeNumber = 1;
            }
            else if (arg.startsWith("-salt"))
            {
                stripSalt = true;
            }
            else if (arg.startsWith("+snd"))
            {
                List nativeDescs = FeatureHelper.instance().getNativeFeatures();
                StringBuffer sb = new StringBuffer(nativeDescs.size() * 20);
                sb.append("\nNative value descriptors:\n");

                int s = nativeDescs.size();

                for (int ii = 0; ii < s; ii++)
                {
                    sb.append(nativeDescs.get(ii));
                    sb.append('\n');
                }

                System.out.println(sb.toString());

                return STOP;
            }
            else if (arg.startsWith("+sad"))
            {
                List atomPropDescs = FeatureHelper.instance()
                                                  .getAtomLabelFeatures();
                StringBuffer sb = new StringBuffer(atomPropDescs.size() * 20);
                sb.append("\nAtom property descriptors:\n");

                int s = atomPropDescs.size();

                for (int ii = 0; ii < s; ii++)
                {
                    sb.append(atomPropDescs.get(ii));
                    sb.append('\n');
                }

                System.out.println(sb.toString());

                return STOP;
            }
            else if (arg.startsWith("+sall"))
            {
                Enumeration enumeration = FeatureHelper.instance()
                                                       .getFeatureNames();
                StringBuffer sb = new StringBuffer(1000);
                sb.append("\nDescriptors:\n");

                String tmp;

                while (enumeration.hasMoreElements())
                {
                    tmp = (String) enumeration.nextElement();
                    sb.append(tmp);
                    sb.append('\n');
                }

                System.out.println(sb.toString());

                return STOP;
            }
            else if (arg.startsWith("-e"))
            {
                skipEmpty = true;
            }
            else if (arg.startsWith("-d"))
            {
                removeDescriptors = true;
            }
            else if (arg.startsWith("+d"))
            {
                addDescriptors = true;
            }
            else if (arg.startsWith("-i"))
            {
                String inTypeS = arg.substring(2);
                inType = BasicIOTypeHolder.instance().getIOType(inTypeS
                        .toUpperCase());

                if (inType == null)
                {
                    logger.error("Input type '" + inTypeS + "' not defined.");

                    return STOP_USAGE;
                }
            }
            else if (arg.startsWith("-o"))
            {
                String outTypeS = arg.substring(2);
                outType = BasicIOTypeHolder.instance().getIOType(outTypeS
                        .toUpperCase());

                if (outType == null)
                {
                    logger.error("Output type '" + outTypeS + "' not defined.");

                    return STOP_USAGE;
                }
            }
            else if (arg.startsWith("+x"))
            {
                String exist = arg.substring(2);

                if (exists == null)
                {
                    exists = new Vector();
                }

                exists.add(exist);
            }
            else if (arg.startsWith("-r"))
            {
                String rule = arg.substring(2);

                if (rules == null)
                {
                    rules = new Vector<NativeValueFilter>();
                }

                NativeValueFilter filter = new NativeValueFilter();

                if (filter.fromString(rule))
                {
                    rules.add(filter);
                }
                else
                {
                    logger.warn("Rule '" + rule +
                        "' was not added to filter rules.");
                }
            }
            else if (arg.startsWith("+r"))
            {
                String rule = arg.substring(2);

                if (rules == null)
                {
                    rules = new Vector<NativeValueFilter>();
                }

                NativeValueFilter filter = new NativeValueFilter();

                if (filter.fromString(rule))
                {
                    filter.invertRelation();
                    rules.add(filter);
                }
                else
                {
                    logger.warn("Rule '" + rule +
                        "' was not added to filter rules.");
                }
            }
            else if (arg.startsWith("+f"))
            {
                String lineStructure = arg.substring(2);
                prop.setProperty("joelib2.io.types.Flat.lineStructure",
                    lineStructure);
            }
            else if (arg.startsWith("+s"))
            {
                String lineStructure = arg.substring(2);
                prop.setProperty("joelib2.io.types.Smiles.lineStructure",
                    lineStructure);
            }
            else if (arg.startsWith("-m"))
            {
                String smartsRule = arg.substring(2);

                if (smartsRules == null)
                {
                    smartsRules = new Vector<SMARTSFilter>();
                }

                SMARTSFilter filter = new SMARTSFilter();

                if (filter.fromString(smartsRule, false))
                {
                    smartsRules.add(filter);
                }
                else
                {
                    logger.warn("SMARTS Rule '" + smartsRule +
                        "' was not added to rules.");
                }
            }
            else if (arg.startsWith("+m"))
            {
                String smartsRule = arg.substring(2);

                if (smartsRules == null)
                {
                    smartsRules = new Vector<SMARTSFilter>();
                }

                SMARTSFilter filter = new SMARTSFilter();

                if (filter.fromString(smartsRule, false))
                {
                    filter.invertRelation();
                    smartsRules.add(filter);
                }
                else
                {
                    logger.warn("SMARTS Rule '" + smartsRule +
                        "' was not added to rules.");
                }
            }
            else if (arg.startsWith("-um"))
            {
                String smartsRule = arg.substring(3);

                if (smartsRules == null)
                {
                    smartsRules = new Vector<SMARTSFilter>();
                }

                SMARTSFilter filter = new SMARTSFilter();

                if (filter.fromString(smartsRule, true))
                {
                    smartsRules.add(filter);
                }
                else
                {
                    logger.warn("Unique SMARTS Rule '" + smartsRule +
                        "' was not added to rules.");
                }
            }
            else if (arg.startsWith("+um"))
            {
                String smartsRule = arg.substring(3);

                if (smartsRules == null)
                {
                    smartsRules = new Vector<SMARTSFilter>();
                }

                SMARTSFilter filter = new SMARTSFilter();

                if (filter.fromString(smartsRule, true))
                {
                    filter.invertRelation();
                    smartsRules.add(filter);
                }
                else
                {
                    logger.warn("Unique SMARTS Rule '" + smartsRule +
                        "' was not added to rules.");
                }
            }
            else
            {
                if (inputFile == null)
                {
                    inputFile = arg;
                }
                else
                {
                    outputFile = arg;

                    if (outputFile.equalsIgnoreCase(inputFile))
                    {
                        logger.error("'" + inputFile + "' and '" + outputFile +
                            "' are the same file.");

                        return STOP_USAGE;
                    }
                }
            }
        }

        if (inputFile == null)
        {
            logger.error("No input file defined.");

            return STOP_USAGE;
        }

        if (!checkInputType())
        {
            return STOP_USAGE;
        }

        if (!checkOutputType())
        {
            return STOP_USAGE;
        }

        if (exists != null)
        {
            allDescFilter = new HasAllDataFilter(exists);
        }

        if (rules != null)
        {
            logger.info("Using skip rules:");

            for (int i = 0; i < rules.size(); i++)
            {
                logger.info("Skip when " + rules.get(i));
            }
        }

        return CONTINUE;
    }

    /**
     *  Description of the Method
     */
    public void usage()
    {
        StringBuffer sb = new StringBuffer();
        String programName = this.getClass().getName();

        sb.append("Usage is :\n");
        sb.append("java -cp . ");
        sb.append(programName);
        sb.append(" [options]");
        sb.append(" <input file>");
        sb.append(" [<output file>]");
        sb.append("\n\n");
        sb.append("Options:\n");
        sb.append(" [-i<inputFormat>]       - Format of the input file\n");
        sb.append(" [-o<outputFormat>]      - Format of the output file\n");
        sb.append(
            " [-h]                    - Removes all hydrogens from molecule\n");
        sb.append(" [+h]                    - Adds hydrogens to molecule\n");
        sb.append(
            " [+p]                    - Adds only polar hydrogens (+h implicit)\n");
        sb.append(
            " [-e]                    - Converts only non-empty molecules\n");
        sb.append(
            " [-d]                    - Removes all descriptors from the molecule\n");
        sb.append(
            " [+d]                    - Adds all available descriptors to the molecule\n");
        sb.append(" [+v]                    - Switch verbosity ON\n");
        sb.append(
            " [+split<SIZE>]             - Generated splitted output file of SIZE\n");
        sb.append(
            " [+snd]                  - Shows all available native value descriptors\n");
        sb.append(
            " [+sad]                  - Shows all available atom property descriptors\n");
        sb.append(
            " [+sall]                 - Shows all available descriptors\n");
        sb.append(
            " [-salt]                 - Strip salts and gets only largest contigous fragment\n");
        sb.append(
            " [+x<descriptor name>]   - Converts only molecules where <descriptor name> exists\n");
        sb.append(" [-r<skip  desc. rule>]  - Skips molecules, if rule fits\n");
        sb.append(
            " [+r<conv. desc. rule>]  - Converts only molecules where rule fits\n");
        sb.append(
            " [+f<lineStructure>]     - Required if you use FLAT output format which other input format\n");
        sb.append(
            " [+s<lineStructure>]     - Can be used for an alternate SMILES entry line structure\n");
        sb.append(
            " [-m<SMARTS rule>]       - Skips molecules, if SMARTS rule fits\n");
        sb.append(
            " [+m<SMARTS rule>]       - Converts only molecules where SMARTS rule fits\n");
        sb.append(
            " [-um<SMARTS rule>]      - Skips molecules, if unique SMARTS rule fits\n");
        sb.append(
            " [+um<SMARTS rule>]      - Converts only molecules where unique SMARTS rule fits\n");
        sb.append(" [-?][--help]            - Shows this message\n\n");
        sb.append(
            "If no output file is defined, all molecules will be written to stdout.\n");
        sb.append("\nFilter rules have the form:\n");
        sb.append("<native value descriptor><relation><value>\n");
        sb.append("where <relation> is <, <=, ==, >, >= or !=\n");
        sb.append("Example:\n");
        sb.append("\"+rNumber_of_halogen_atoms==2\"\n");
        sb.append("\nSMARTS filter rules have the form:\n");
        sb.append("<SMARTS pattern><relation><value>\n");
        sb.append("where <relation> is <, <=, ==, >, >= or !=\n");
        sb.append("Example:\n");
        sb.append("\"+umaNC=O==1\"\n");
        sb.append(
            "Converts all molecules, where the molecule contains ONE NC=O group connected to an aromatic atom (aNC=O).\n");
        sb.append("\nSupported molecule types:\n");
        sb.append(BasicIOTypeHolder.instance().toString());

        //show available native value descriptors
        List nativeFeat = FeatureHelper.instance().getNativeFeatures();
        sb.append("\n\nNative value descriptors: ");

        int s = nativeFeat.size();

        for (int i = 0; i < s; i++)
        {
            sb.append(nativeFeat.get(i));

            if (i < (s - 1))
            {
                sb.append(", ");
            }
        }

        sb.append(
            "\n\nThis is version $Revision: 1.13 $ ($Date: 2006/07/24 22:29:15 $)\n");

        System.out.println(sb.toString());
    }

    private boolean checkInputType()
    {
        if (inType == null)
        {
            try
            {
                inType = BasicReader.checkGetInputType(inputFile);
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());

                return false;
            }
        }

        return true;
    }

    private boolean checkOutputType()
    {
        if (outputFile != null)
        {
            if (outType == null)
            {
                try
                {
                    outType = BasicMoleculeWriter.checkGetOutputType(
                            outputFile);
                }
                catch (IOException e)
                {
                    System.out.println(e.getMessage());

                    return false;
                }
            }
        }

        return true;
    }

    private void getAndStoreDescriptor(Molecule mol, String descName)
    {
        FeatureResult result = null;

        try
        {
            result = FeatureHelper.instance().featureFrom(mol, descName);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());
        }

        if (result == null)
        {
            logger.error("Feature '" + descName +
                "' was not calculated and will not be stored for " +
                mol.getTitle());
        }
        else
        {
            BasicPairData dp = new BasicPairData();
            dp.setKey(descName);
            dp.setKeyValue(result);
            mol.addData(dp);
        }
    }

    private String getSplitName(String name, int number)
    {
        int index = name.lastIndexOf('.');

        if (index == -1)
        {
            return name + number;
        }
        else
        {
            if (splitSize == 1)
            {
                return name.substring(0, index) + "." + (number + 1) +
                    name.substring(index);
            }
            else
            {
                return name.substring(0, index) + "." + (number + 1) + "-" +
                    (number + splitSize) + name.substring(index);
            }
        }
    }

    private boolean handleSingle(Molecule mol) throws MoleculeIOException,
        IOException
    {
        boolean success;

        //StopWatch stopWatch=new StopWatch();
        //stopWatch.resetTime();
        if (skipEmpty && mol.isEmpty())
        {
            logger.warn("Empty molecule '" + mol.getTitle() + "' was ignored.");

            return true;
        }

        if (removeHydrogens)
        {
            // use begin and end modify to update
            // rotamer informations
            mol.beginModify();
            mol.deleteHydrogens();
            mol.endModify();
        }

        if (stripSalt)
        {
            MoleculeHelper.stripSalts(mol);
        }

        if (addHydrogens)
        {
            mol.addHydrogens(polarOnly, usePHvalueCorrection, true);
        }

        if (addDescriptors)
        {
            Enumeration enumeration = FeatureHelper.instance()
                                                   .getFeatureNames();
            String tmp;

            while (enumeration.hasMoreElements())
            {
                tmp = (String) enumeration.nextElement();

                getAndStoreDescriptor(mol, tmp);
            }
        }

        if (!skipCriteria(mol))
        {
            if (outputFile != null)
            {
                if (verbose)
                {
                    System.out.println("write " + mol.toString(verboseType));
                }

                if (removeDescriptors)
                {
                    if (writer instanceof PropertyWriter)
                    {
                        success = ((PropertyWriter) writer).write(mol, null,
                                false, null);
                    }
                    else
                    {
                        success = writer.write(mol);
                    }
                }
                else
                {
                    success = writer.write(mol);
                }

                if (!success)
                {
                    return false;
                }
            }
            else
            {
                System.out.println(mol);
            }
        }

        molCounter++;

        if ((molCounter % 500) == 0)
        {
            logger.info("... " + molCounter +
                " molecules successful converted in " + watch.getPassedTime() +
                " ms.");
        }

        if (splitFile)
        {
            if (splitSize == actualSplitNumber)
            {
                actualSplitNumber = 1;

                try
                {
                    if (writer != null)
                    {
                        writer.closeWriter();

                        //      out = new BufferedOutputStream( new FileOutputStream(outputFile));
                        out = new FileOutputStream(getSplitName(outputFile,
                                    splitMoleculeNumber));

                        try
                        {
                            writer = MoleculeFileHelper.getMolWriter(out,
                                    outType);
                        }
                        catch (MoleculeIOException e1)
                        {
                            e1.printStackTrace();

                            return false;
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();

                    return false;
                }
            }
            else
            {
                actualSplitNumber++;
            }

            splitMoleculeNumber++;
        }
        //if(mol!=null)System.out.println("time "+stopWatch.getPassedTime()+" atoms "+mol.getAtomsSize()+" mol "+mol.getTitle());

        return true;
    }

    private boolean skipCriteria(Molecule mol)
    {
        boolean what = false;

        if (allDescFilter != null)
        {
            what = allDescFilter.accept(mol);

            if (!what)
            {
                return true;
            }
            else
            {
                what = false;
            }
        }

        if (rules != null)
        {
            for (int i = 0; i < rules.size(); i++)
            {
                what = ((NativeValueFilter) rules.get(i)).accept(mol);

                //System.out.println(rules.get(i) + "=" + what);
                if (what)
                {
                    return true;
                }
            }
        }

        if (smartsRules != null)
        {
            for (int i = 0; i < smartsRules.size(); i++)
            {
                what = ((SMARTSFilter) smartsRules.get(i)).accept(mol);

                //System.out.println(smartsRules.get(i) + "=" + what);
                if (what)
                {
                    return true;
                }
            }
        }

        return what;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
