///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ConvertSkipExample.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:29 $
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
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;
import joelib2.io.PropertyWriter;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeHelper;

import joelib2.molecule.types.BasicPairData;

import joelib2.process.filter.HasAllDataFilter;
import joelib2.process.filter.NativeValueFilter;
import joelib2.process.filter.SMARTSFilter;

import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.BasicResourceLoader;
import wsi.ra.tool.StopWatch;

import java.io.ByteArrayInputStream;
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
 * Example for converting/skipping molecules.
 *
  * <p>
 * <blockquote><pre>
 * Usage:
 * java -cp . joelib2.test.Convert [options] &lt;input file> [&lt;output file>]  [&lt;skip file>]
 *
 * Options:
 * [-i&lt;inputFormat>]       - Format of the input file
 * [-o&lt;outputFormat>]      - Format of the output file
 * [-h]                    - Removes all hydrogens from molecule
 * [+h]                    - Adds hydrogens to molecule
 * [+p]                    - Adds only polar hydrogens (+h implicit)
 * [-e]                    - Converts only non-empty molecules
 * [-d]                    - Remove all descriptors from the molecule
 * [+d|+d&lt;descriptor name>]- Adds all (or defined) available descriptors to the molecule
 * [+df&lt;descFileName>]     - Adds all descriptors defined in the file to the molecule
 * [+v]                    - Switch verbosity ON
 * [+snd]                  - Shows all available native value descriptors
 * [+sad]                  - Shows all available atom property descriptors
 * [+sall]                 - Shows all available descriptors
 * [-salt]                 - Strip salts and gets only largest contigous fragment
 * [+x&lt;descriptor name>]   - Converts only molecules where &lt;descriptor name> exists
 * [-r&lt;skip  desc. rule>]  - Skips molecules, if rule fits
 * [+r&lt;conv. desc. rule>]  - Converts only molecules where rule fits
 * [+f&lt;lineStructure>]     - Required if you use FLAT output format which other input format
 * [+ff&lt;lineStructureFile>]- Required if you use FLAT output format which other input format
 * [+s&lt;lineStructure>]     - Can be used for an alternate SMILES entry line structure
 * [-m&lt;SMARTS rule>]       - Skips molecules, if SMARTS rule fits
 * [+m&lt;SMARTS rule>]       - Converts only molecules where SMARTS rule fits
 * [-um&lt;SMARTS rule>]      - Skips molecules, if SMARTS rule fits
 * [+um&lt;SMARTS rule>]      - Converts only molecules where SMARTS rule fits
 * [-?][--help]            - Shows this message
 *
 * If no output file is defined, all molecules will be written to stdout.
 * The skip file must have the same format as the input file. All skipped
 * entries and empty entries (if -e was selected) were stored in the skip
 * file.
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
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:29 $
 */
public class ConvertSkipExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            ConvertSkipExample.class.getName());
    private static BasicIOType verboseType = BasicIOTypeHolder.instance()
                                                              .getIOType(
            "SMILES");
    public static final int CONTINUE = 0;
    public static final int STOP = 1;
    public static final int STOP_USAGE = 2;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public PrintStream generatedOutput;
    private boolean addDescriptors = false;
    private boolean addHydrogens = false;
    private HasAllDataFilter allDescFilter = null;
    private List descriptor2add = null;
    private List exists = null;

    //  private String inputTypeName;
    private String inputFile;
    private BasicIOType inType;
    private MoleculeFileIO loader;
    private String outputFile;
    private BasicIOType outType;
    private boolean polarOnly = false;
    private boolean removeDescriptors = false;
    private boolean removeHydrogens = false;
    private List rules = null;
    private boolean skipEmpty = false;
    private String skipFile;
    private List smartsRules = null;
    private MoleculeFileIO stringLoader;

    //  private boolean flatFirstLineLoaded = false;
    private boolean stripSalt = false;
    private boolean verbose = false;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the ConvertSkip object
     */
    public ConvertSkipExample()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    *  The main program for the ConvertSkip class
    *
    * @param args  The command line arguments
    */
    public static void main(String[] args)
    {
        ConvertSkipExample convert = new ConvertSkipExample();
        int status = convert.parseCommandLine(args);

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
     *  Convert and skip test.
     */
    public void convert()
    {
        FileInputStream in = null;

        // get molecule loader/writer
        try
        {
            //      in = new BufferedInputStream(new FileInputStream(inputFile));
            in = new FileInputStream(inputFile);
            loader = MoleculeFileHelper.getMolReader(in, inType);
            stringLoader = MoleculeFileHelper.getMolReader(new FileInputStream(
                        inputFile), inType);
        }
        catch (FileNotFoundException ex)
        {
            logger.error("Can not find input file: " + inputFile);
            System.exit(1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (!loader.readable())
        {
            logger.error(inType.getRepresentation() + " is not readable.");
            logger.error("You're invited to write one !;-)");
            System.exit(1);
        }

        FileOutputStream out = null;
        MoleculeFileIO writer = null;

        if (outputFile != null)
        {
            try
            {
                //      out = new BufferedOutputStream( new FileOutputStream(outputFile));
                out = new FileOutputStream(outputFile);
                writer = MoleculeFileHelper.getMolWriter(out, outType);
            }
            catch (FileNotFoundException ex)
            {
                logger.error("Can not create output file: " + outputFile);
                System.exit(1);
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
                System.exit(1);
            }
        }

        FileOutputStream skip = null;
        PrintStream skipPS = null;

        if (skipFile != null)
        {
            try
            {
                skip = new FileOutputStream(skipFile);
                skipPS = new PrintStream(skip);
            }
            catch (FileNotFoundException ex)
            {
                logger.error("Can not create skip file: " + skipFile);
                System.exit(1);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, outType);
        boolean success = true;
        int molCounter = 0;
        int skipCounter = 0;
        String skipMessage;
        String molecule = null;
        StopWatch watch = new StopWatch();
        logger.info("Start file conversion ...");

        for (;;)
        {
            molCounter++;
            mol.clear();

            // get single molecule as SDF block
            try
            {
                molecule = stringLoader.read();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            if (molecule == null)
            {
                break;
            }

            initReader(molecule);

            boolean validMolecule = true;
            skipMessage = null;

            try
            {
                success = loader.read(mol);

                if (skipEmpty && mol.isEmpty())
                {
                    validMolecule = false;
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }
            catch (MoleculeIOException ex)
            {
                validMolecule = false;
                skipMessage = ex.getMessage();
            }

            if (validMolecule)
            {
                if (removeHydrogens)
                {
                    // use begein and end modify to update
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
                    mol.addHydrogens(polarOnly, true, true);
                }

                if (addDescriptors)
                {
                    if (descriptor2add != null)
                    {
                        for (int i = 0; i < descriptor2add.size(); i++)
                        {
                            getAndStoreDescriptor(mol,
                                (String) descriptor2add.get(i));
                        }
                    }
                    else
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
                }
            }

            //System.out.println("validMolecule: "+validMolecule);
            //System.out.println("skipCriteria(mol): "+skipCriteria(mol));
            if ((skipEmpty && mol.isEmpty()) || (!validMolecule) ||
                    (validMolecule && skipCriteria(mol)))
            {
                if (skipFile != null)
                {
                    if (!validMolecule)
                    {
                        skipPS.print(molecule);
                    }
                    else
                    {
                        skipPS.print(mol.toString(inType));
                    }
                }

                skipCounter++;
                logger.info("Molecule entry (#" + molCounter +
                    ") was skipped (#" + skipCounter + "): " + mol.getTitle());

                if (skipMessage != null)
                {
                    logger.info("Because: " + skipMessage);
                }
            }
            else
            {
                try
                {
                    if (outputFile != null)
                    {
                        if (verbose)
                        {
                            System.out.println("write " +
                                mol.toString(verboseType));
                        }

                        if (removeDescriptors)
                        {
                            if (writer instanceof PropertyWriter)
                            {
                                success = ((PropertyWriter) writer).write(mol,
                                        null, false, null);
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
                            break;
                        }
                    }
                    else
                    {
                        System.out.println(mol);
                    }
                }
                catch (MoleculeIOException ex)
                {
                    logger.error("Illegal molecule format");
                    validMolecule = false;
                }
                catch (IOException ex)
                {
                    System.exit(1);
                }

                if ((molCounter % 500) == 0)
                {
                    logger.info("... " + (molCounter - skipCounter - 1) +
                        " molecules successful converted in " +
                        watch.getPassedTime() + " ms.");
                }
            }
        }

        try
        {
            if (loader != null)
            {
                loader.closeReader();
            }

            if (writer != null)
            {
                writer.closeWriter();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        logger.info("... " + (molCounter - skipCounter - 1) +
            " molecules successful converted in " + watch.getPassedTime() +
            " ms.");
        logger.info("... " + skipCounter + " molecules skipped.");
    }

    /**
     *  Description of the Method
     *
     * @param args  Description of the Parameter
     * @return      Description of the Return Value
     */
    public int parseCommandLine(String[] args)
    {
        // get default properties
        Properties prop = BasicPropertyHolder.instance().getProperties();

        String arg;

        for (int i = 0; i < args.length; i++)
        {
            arg = args[i];

            //System.out.println(arg);
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
            else if (arg.startsWith("+p"))
            {
                addHydrogens = true;
                polarOnly = true;
            }
            else if (arg.startsWith("+v"))
            {
                verbose = true;
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
            else if (arg.startsWith("-v"))
            {
                verbose = false;
            }
            else if (arg.startsWith("-e"))
            {
                skipEmpty = true;
            }
            else if (arg.startsWith("-d"))
            {
                removeDescriptors = true;
            }
            else if (arg.startsWith("+df"))
            {
                String tmp = arg.substring(3);

                if (tmp.trim().length() == 0)
                {
                    descriptor2add = null;
                }
                else
                {
                    descriptor2add = BasicResourceLoader.readLines(tmp);
                }

                addDescriptors = true;
            }
            else if (arg.startsWith("+d"))
            {
                String tmp = arg.substring(2);

                if (tmp.trim().length() == 0)
                {
                    descriptor2add = null;
                }
                else
                {
                    if (descriptor2add == null)
                    {
                        descriptor2add = new Vector();
                    }

                    descriptor2add.add(tmp);
                }

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
                    rules = new Vector();
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
                    rules = new Vector();
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
            else if (arg.startsWith("+ff"))
            {
                String tmp = arg.substring(3);
                String lineStructure;

                if (tmp.trim().length() == 0)
                {
                    lineStructure = "";
                }
                else
                {
                    //System.out.println("strfile:"+tmp);
                    List descs = BasicResourceLoader.readLines(tmp);
                    Properties prop2 = BasicPropertyHolder.instance()
                                                          .getProperties();
                    String delimiter = prop2.getProperty(
                            "joelib2.io.types.Flat.lineStructure.delimiter",
                            "|");

                    if (descs == null)
                    {
                        logger.error("File " + tmp + " not found.");

                        return STOP;
                    }

                    if (descs.size() == 1)
                    {
                        lineStructure = (String) (descs.get(0));
                    }
                    else
                    {
                        int size = descs.size();
                        StringBuffer nls = new StringBuffer(size * 20);

                        for (int j = 0; j < size; j++)
                        {
                            nls.append((String) descs.get(j));

                            //System.out.println("STRUCT:"+descs.get(j));
                            if (j < (size - 1))
                            {
                                nls.append(delimiter);
                            }
                        }

                        lineStructure = nls.toString();
                    }
                }

                prop.setProperty("joelib2.io.types.Flat.lineStructure",
                    lineStructure);

                //System.out.println("LINE:"+lineStructure);
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
                    smartsRules = new Vector();
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
                    smartsRules = new Vector();
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
                    smartsRules = new Vector();
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
                    smartsRules = new Vector();
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
                else if (outputFile == null)
                {
                    outputFile = arg;

                    if (outputFile.equalsIgnoreCase(inputFile))
                    {
                        logger.error("'" + inputFile + "' and '" + outputFile +
                            "' are the same file.");

                        return STOP_USAGE;
                    }
                }
                else if (skipFile == null)
                {
                    skipFile = arg;

                    if (skipFile.equalsIgnoreCase(inputFile))
                    {
                        logger.error("'" + inputFile + "' and '" + skipFile +
                            "' are the same file.");

                        return STOP_USAGE;
                    }

                    if (skipFile.equalsIgnoreCase(outputFile))
                    {
                        logger.error("'" + outputFile + "' and '" + skipFile +
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

        checkInputType();
        checkOutputType();

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
        sb.append(" [<skip file>]");
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
            " [-d]                    - Remove all descriptors from the molecule\n");
        sb.append(
            " [+d|+d<descriptor name>]- Adds all (or defined) available descriptors to the molecule\n");
        sb.append(
            " [+df<descFileName>]     - Adds all descriptors defined in the file to the molecule\n");
        sb.append(" [+v]                    - Switch verbosity ON\n");
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
            " [+ff<lineStructureFile>]- Required if you use FLAT output format which other input format\n");
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
        sb.append(
            "The skip file must have the same format as the input file. All skipped\n");
        sb.append(
            "entries and empty entries (if -e was selected) were stored in the skip\n");
        sb.append("file.\n");
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
        List nativeDescs = FeatureHelper.instance().getNativeFeatures();
        sb.append("\n\nNative value descriptors: ");

        int s = nativeDescs.size();

        for (int i = 0; i < s; i++)
        {
            sb.append(nativeDescs.get(i));

            if (i < (s - 1))
            {
                sb.append(", ");
            }
        }

        sb.append(
            "\n\nThis is version $Revision: 1.10 $ ($Date: 2005/02/17 16:48:29 $)\n");

        System.out.println(sb.toString());

        System.exit(0);
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

    /**
     * Description of the Method
     *
     * @param molecule  Description of the Parameter
     */
    private void initReader(String molecule)
    {
        ByteArrayInputStream sReader = new ByteArrayInputStream(molecule
                .getBytes());

        try
        {
            //  try to load MoleculeFileType representation class

            /*loader = JOEFileFormat.getMoleculeFileType(inType);

            if (loader != null)
            {
                    if(inType.equals(IOTypeHolder.instance().getIOType("FLAT")))
                    {
                            if (!flatFirstLineLoaded)
                            {
                                    System.out.println("normal flat");
                                    loader.initReader(sReader);
                                    flatFirstLineLoaded = true;
                            }
                            else
                            {
                                    System.out.println("flat");
                                    ((joelib2.io.types.Flat)loader).initReader(sReader, true);
                            }
                    }
                    else
                    {
                            loader.initReader(sReader);
                    }
            }*/
            if (inType.equals(BasicIOTypeHolder.instance().getIOType("FLAT")))
            {
                //System.out.println("firstLineLoaded 1:"+((joelib2.io.types.Flat)loader).firstLineLoaded);
                loader = MoleculeFileHelper.getMoleculeFileType(inType);
                ((joelib2.io.types.Flat) loader).initReader(sReader, true);
            }
            else
            {
                loader = MoleculeFileHelper.getMolReader(sReader, inType);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
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
