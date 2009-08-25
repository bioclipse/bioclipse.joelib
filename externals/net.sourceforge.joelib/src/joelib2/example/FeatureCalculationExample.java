///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: FeatureCalculationExample.java,v $
//  Purpose:  Example for loading molecules and get atom properties.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2006/02/22 02:18:22 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
///////////////////////////////////////////////////////////////////////////////
package joelib2.example;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.types.Autocorrelation;
import joelib2.feature.types.BurdenModifiedEigenvalues;
import joelib2.feature.types.GlobalTopologicalChargeIndex;
import joelib2.feature.types.RadialDistributionFunction;

import joelib2.feature.util.AtomPropertyDescriptors;
import joelib2.feature.util.SMARTSDescriptors;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.BasicMoleculeWriter;
import joelib2.io.BasicReader;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;
import joelib2.molecule.ProtonationHelper;

import joelib2.molecule.types.BasicPairData;

import joelib2.process.MoleculeProcessException;

import joelib2.process.types.DescriptorVarianceNorm;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicResourceLoader;
import wsi.ra.tool.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Example for loading molecules and get atom properties.
 *
 * @.author     wegnerj
 */
public class FeatureCalculationExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            FeatureCalculationExample.class.getName());
    public static final int CONTINUE = 0;
    public static final int STOP = 1;
    public static final int STOP_USAGE = 2;
    private static final double[] RDF_SMOOTHINGFACTORS =
        new double[]{1.0, 5.0, 25.0, 150.0};
    private static final String NUMERIC = ".numeric";
    private static final String NUMERIC_NORMALIZED = ".numeric.normalized";

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean calculateAP = true;
    private boolean calculateBinarySMARTS = true;
    private boolean calculateCountSMARTS = false;
    private boolean calculateJCC = true;

    //private boolean calculateMACCS = true;
    private boolean calculateSSKey = true;

    private AtomPropertyDescriptors calculatorAP =
        new AtomPropertyDescriptors();
    private SMARTSDescriptors calculatorSMARTS = new SMARTSDescriptors();
    private String inputFile;
    private BasicIOType inType;
    private boolean normalize = false;
    private String outputFile;
    private BasicIOType outType;
    private String[] smartsDescriptions = null;
    private String smartsFile;
    private String[] smartsPatterns = null;
    private String trueDescName = null;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    *  The main program for the TestSmarts class
    *
    * @param  args  The command line arguments
    */
    public static void main(String[] args)
    {
        FeatureCalculationExample descs = new FeatureCalculationExample();

        int status = descs.parseCommandLine(args);

        if (status == CONTINUE)
        {
            descs.initializeSMARTS();
            descs.calculateNumericDescriptors();
            descs.calculateNormalization();
            descs.calculateNominalDescriptors();
        }
        else if (status == STOP_USAGE)
        {
            descs.usage();
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
    * @param  molURL   Description of the Parameter
    * @param  inType   Description of the Parameter
    * @param  outType  Description of the Parameter
    */
    public void calculateNumericDescriptors()
    {
        if ((inType == null) || (inputFile == null) || (outType == null) ||
                (outputFile == null))
        {
            logger.error("Not correctly initialized.");
            logger.error("input type: " + inType.getName());
            logger.error("input file: " + inputFile);
            logger.error("output type: " + outType.getName());
            logger.error("output file: " + outputFile);
            System.exit(1);
        }

        FileInputStream in = null;
        FileOutputStream out = null;

        // get molecule loader/writer
        MoleculeFileIO loader = null;
        MoleculeFileIO writer = null;

        try
        {
            in = new FileInputStream(inputFile);

            if (!normalize)
            {
                //                              if (!calculateBinarySMARTS && !calculateMACCS &&
                //                                              !calculateSSKey)
                //                              {
                if (!calculateBinarySMARTS && !calculateSSKey)
                {
                    out = new FileOutputStream(outputFile);
                }
                else
                {
                    out = new FileOutputStream(generateFileName(inputFile,
                                NUMERIC));
                }
            }
            else
            {
                out = new FileOutputStream(generateFileName(inputFile,
                            NUMERIC));
            }

            loader = MoleculeFileHelper.getMolReader(in, inType);
            writer = MoleculeFileHelper.getMolWriter(out, outType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        String[] defDescNames =
            {
                BurdenModifiedEigenvalues.getName(), Autocorrelation.getName(),
                GlobalTopologicalChargeIndex.getName(),
                RadialDistributionFunction.getName()
            };

        try
        {
            calculatorAP.setDescriptors2Calculate(defDescNames);
        }
        catch (FeatureException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        calculatorAP.setRDFSmoothings(RDF_SMOOTHINGFACTORS);

        Enumeration enumeration = FeatureHelper.instance().getFeatureNames();
        String tmp;
        Vector jccDescriptorsV = new Vector(100);

        while (enumeration.hasMoreElements())
        {
            tmp = (String) enumeration.nextElement();

            jccDescriptorsV.add(tmp);
        }

        String[] jccDescriptors = new String[jccDescriptorsV.size()];

        for (int i = 0; i < jccDescriptors.length; i++)
        {
            jccDescriptors[i] = (String) jccDescriptorsV.get(i);
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, outType);
        int molCounter = 0;
        StopWatch watch = new StopWatch();
        logger.info("Calculate JCC, AP and counting SMARTS ...");

        for (;;)
        {
            try
            {
                if (!loader.read(mol))
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }
            
            ProtonationHelper.deleteHydrogens(mol);

            //System.out.println("MOLECULE: "+mol.getTitle());
            try
            {
                if (calculateAP)
                {
                    //System.out.print("AP ");
                    calculatorAP.calculate(mol);
                }

                if (smartsPatterns != null)
                {
                    if (calculateCountSMARTS)
                    {
                        calculatorSMARTS.calculate(mol, true);
                    }
                }

                if (calculateJCC)
                {
                    //System.out.print("JCC ");
                    for (int i = 0; i < jccDescriptors.length; i++)
                    {
                        //System.out.print(jccDescriptors[i]);
                        //System.out.println("DESC: "+jccDescriptors[i]);
                        getAndStoreDescriptor(mol, jccDescriptors[i]);
                    }
                }
            }
            catch (FeatureException e1)
            {
                logger.error(e1.getMessage());
            }

            //System.out.println("\nWRITE ");
            // write molecule to file
            try
            {
                if (!writer.write(mol))
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            molCounter++;

            if ((molCounter % 500) == 0)
            {
                logger.info("... " + molCounter +
                    " molecules successful calculated in " +
                    watch.getPassedTime() + " ms.");
            }
        }

        logger.info("... " + molCounter +
            " molecules successful calculated in " + watch.getPassedTime() +
            " ms.");
    }

    /**
    *  Description of the Method
    *
    * @param  args  Description of the Parameter
    * @return       Description of the Return Value
    */
    public int parseCommandLine(String[] args)
    {
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
            else if (arg.startsWith("+countSMARTS"))
            {
                logger.info(
                    "PARAMETER: Counting SMARTS values will be calculated.");
                calculateCountSMARTS = true;
            }
            else if (arg.startsWith("-countSMARTS"))
            {
                logger.info(
                    "PARAMETER: Counting SMARTS values will not be calculated.");
                calculateCountSMARTS = false;
            }
            else if (arg.startsWith("+binarySMARTS"))
            {
                logger.info(
                    "PARAMETER: Binary SMARTS values will be calculated.");
                calculateBinarySMARTS = true;
            }
            else if (arg.startsWith("-binarySMARTS"))
            {
                logger.info(
                    "PARAMETER: Binary SMARTS values will not be calculated.");
                calculateBinarySMARTS = false;
            }
            else if (arg.startsWith("+normalize"))
            {
                logger.info(
                    "PARAMETER: Normalize descriptors, except binary SMARTS and fingerprints.");
                normalize = true;
            }
            else if (arg.startsWith("-normalize"))
            {
                logger.info("PARAMETER: Do not normalize descriptors.");
                normalize = false;
            }
            else if (arg.startsWith("+jcc"))
            {
                logger.info(
                    "PARAMETER: Calculate native descriptors in JCompChem.");
                calculateJCC = true;
            }
            else if (arg.startsWith("-jcc"))
            {
                logger.info(
                    "PARAMETER: Does not calculate native descriptors in JCompChem.");
                calculateJCC = false;
            }
            else if (arg.startsWith("+ap"))
            {
                logger.info(
                    "PARAMETER: Calculate atom property descriptors in JCompChem.");
                calculateAP = true;
            }
            else if (arg.startsWith("-ap"))
            {
                logger.info(
                    "PARAMETER: Does not calculate atom property descriptors in JCompChem.");
                calculateAP = false;
            }

            //            else if (arg.startsWith("+MACCS"))
            //            {
            //                logger.info(
            //                    "PARAMETER: Calculate MACCS fingerprint descriptors.");
            //                calculateMACCS = true;
            //            }
            //            else if (arg.startsWith("-MACCS"))
            //            {
            //                logger.info(
            //                    "PARAMETER: Does not calculate MACCS fingerprint descriptors.");
            //                calculateMACCS = false;
            //            }
            else if (arg.equalsIgnoreCase("+SSKey"))
            {
                logger.info(
                    "PARAMETER: Calculate SSKey fingerprint descriptors.");
                calculateSSKey = true;
            }
            else if (arg.equalsIgnoreCase("-SSKey"))
            {
                logger.info(
                    "PARAMETER: Does not calculate SSKey fingerprint descriptors.");
                calculateSSKey = false;
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
            else if (arg.startsWith("-t"))
            {
                trueDescName = arg.substring(2);
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
                else
                {
                    smartsFile = arg;
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

        if (trueDescName == null)
        {
            logger.warn("No output descriptor (TRUE VALUE) defined !!!");

            if (normalize)
            {
                logger.warn("Now all descriptors will be normalized !!!");
            }
        }

        if (!normalize)
        {
            logger.warn(
                "If you plan to use the calculated descriptors for creating models,");
            logger.warn("you should normalize the data set.");
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
        sb.append(" <output file>");
        sb.append(" [SMARTS definition file]\n");
        sb.append("Options:\n");
        sb.append(" [-i<inputFormat>]       - Format of the input file\n");
        sb.append(" [-o<outputFormat>]      - Format of the output file\n");
        sb.append(
            " [+jcc]          - Calculate all available native descriptors in JCompChem (default)\n");
        sb.append(
            " [-jcc]          - Does not calculate all available native descriptors in JCompChem\n");
        sb.append(
            " [+ap]           - Calculate all available atom property descriptors in JCompChem (default)\n");
        sb.append(
            " [-ap]           - Does not calculate all available atom property descriptors in JCompChem\n");

        //        sb.append(
        //            " [+MACCS]         - Calculate MACCS fingerprint  (default)\n");
        //        sb.append(" [-MACCS]         - Does not calculate MACCS fingerprint\n");
        sb.append(
            " [+SSKey]         - Calculate SSkey fingerprint  (default)\n");
        sb.append(" [-SSKey]         - Does not calculate SSkey fingerprint\n");
        sb.append(" [+countSMARTS]   - Calculate counting SMARTS\n");
        sb.append(
            " [-countSMARTS]   - Does not calculate counting SMARTS  (default)\n");
        sb.append(" [+binarySMARTS]  - Calculate binary SMARTS (default)\n");
        sb.append(" [-binarySMARTS]  - Does not calculate binary SMARTS\n");
        sb.append(
            " [-t<TRUE_VALUE>] - Defines TRUE VALUE descriptor (will NOT be normalized)\n");
        sb.append(
            " [+normalize] - Normalize descriptor values. SMARTS and fingerprints are ignored\n");
        sb.append(
            " [-normalize] - Do not normalize the descriptor values (default)\n");
        sb.append("\nWarning:");
        sb.append(
            "\nIf you use binary AND counting SMARTS you should NOT use your own names, or only the binary SMARTS results will be stored. That's obvious, ONE name, ONE result.");
        sb.append("\n\nSupported molecule types:");
        sb.append(BasicIOTypeHolder.instance().toString());
        System.out.println(sb.toString());

        System.exit(0);
    }

    /**
     *
     */
    private void calculateNominalDescriptors()
    {
        if (!calculateBinarySMARTS && !calculateSSKey)
        {
            return;
        }

        //        if (!calculateBinarySMARTS && !calculateMACCS && !calculateSSKey)
        //        {
        //            return;
        //        }
        FileInputStream in = null;
        FileOutputStream out = null;

        // get molecule loader/writer
        MoleculeFileIO loader = null;
        MoleculeFileIO writer = null;

        try
        {
            if (!normalize)
            {
                in = new FileInputStream(generateFileName(inputFile, NUMERIC));
            }
            else
            {
                in = new FileInputStream(generateFileName(inputFile,
                            NUMERIC_NORMALIZED));
            }

            out = new FileOutputStream(outputFile);
            loader = MoleculeFileHelper.getMolReader(in, inType);
            writer = MoleculeFileHelper.getMolWriter(out, outType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, outType);
        int molCounter = 0;
        StopWatch watch = new StopWatch();
        logger.info("Calculate binary SMARTS and fingerprints...");

        for (;;)
        {
            try
            {
                if (!loader.read(mol))
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            try
            {
                //                if (calculateMACCS)
                //                {
                //                    calculatorSMARTS.calculateMACCS(mol);
                //                }
                if (calculateSSKey)
                {
                    calculatorSMARTS.calculateSSKeys(mol);
                }

                if (smartsPatterns != null)
                {
                    if (calculateBinarySMARTS)
                    {
                        calculatorSMARTS.calculate(mol, false);
                    }
                }
            }
            catch (FeatureException e1)
            {
                logger.error(e1.getMessage());
            }

            // write molecule to file
            try
            {
                if (!writer.write(mol))
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            molCounter++;

            if ((molCounter % 500) == 0)
            {
                logger.info("... " + molCounter +
                    " molecules successful calculated in " +
                    watch.getPassedTime() + " ms.");
            }
        }

        logger.info("... " + molCounter +
            " molecules successful calculated in " + watch.getPassedTime() +
            " ms.");
    }

    /**
     *
     */
    private void calculateNormalization()
    {
        if (!normalize)
        {
            return;
        }

        DescriptorVarianceNorm norm = null;

        //              get process
        //        try
        //        {
        norm = new DescriptorVarianceNorm();

        //            //norm = (DescVarianceNorm) ProcessFactory.instance().getProcess("VarianceNormalization");
        //        }
        //         catch (JOEProcessException ex)
        //        {
        //            ex.printStackTrace();
        //            System.exit(1);
        //        }
        try
        {
            norm.init(inType, generateFileName(inputFile, NUMERIC));

            if (trueDescName != null)
            {
                norm.descriptors2ignore().add(trueDescName);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }

        // create simple reader
        FileInputStream input = null;
        FileOutputStream output = null;

        try
        {
            input = new FileInputStream(generateFileName(inputFile, NUMERIC));
            output = new FileOutputStream(generateFileName(inputFile,
                        NUMERIC_NORMALIZED));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }

        BasicReader reader = null;
        BasicMoleculeWriter writer = null;

        logger.info("Normalize data set...");

        try
        {
            reader = new BasicReader(input, inType);
            writer = new BasicMoleculeWriter(output, outType);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, outType);
        int molCounter = 0;
        StopWatch watch = new StopWatch();

        for (;;)
        {
            try
            {
                if (!reader.readNext(mol))
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            try
            {
                norm.process(mol, null);
            }
            catch (MoleculeProcessException ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            try
            {
                writer.writeNext(mol);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            molCounter++;

            //      System.out.println(mol);
            if ((molCounter % 500) == 0)
            {
                logger.info("... " + molCounter +
                    " molecules successful normalized in " +
                    watch.getPassedTime() + " ms.");
            }
        }

        logger.info("... " + molCounter +
            " molecules successful normalized in " + watch.getPassedTime() +
            " ms.");

        if (trueDescName != null)
        {
            norm.descriptors2ignore().remove(trueDescName);
        }
    }

    private boolean checkInputType()
    {
        if (inType == null)
        {
            inType = BasicIOTypeHolder.instance().filenameToType(inputFile);

            if (inType == null)
            {
                System.out.println("Input type of " + inputFile +
                    " could not be estimated.");

                return false;
            }

            MoleculeFileIO mfType = null;

            try
            {
                mfType = MoleculeFileHelper.getMoleculeFileType(inType);
            }
            catch (MoleculeIOException ex)
            {
                ex.printStackTrace();
            }

            logger.info("Input type set to " + inType.toString() + ": " +
                mfType.inputDescription());
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

    /**
     * @param inputFile2
     * @param string
     * @return
     */
    private String generateFileName(String _inputFile, String nameExtension)
    {
        int index = _inputFile.lastIndexOf(".");

        if (index == -1)
        {
            return _inputFile + nameExtension;
        }
        else
        {
            return _inputFile.substring(0, index) + nameExtension +
                _inputFile.substring(index);
        }
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
            logger.error("Descriptor '" + descName +
                "' was not calculated and will not be stored.");
        }
        else
        {
            BasicPairData dp = new BasicPairData();
            dp.setKey(descName);
            dp.setKeyValue(result);
            mol.addData(dp);
        }
    }

    private void initializeSMARTS()
    {
        List smartsPatternsV = null;
        Vector smartsLine = new Vector();

        if (smartsFile != null)
        {
            smartsPatternsV = BasicResourceLoader.readLines(smartsFile);

            if (smartsPatternsV != null)
            {
                smartsPatterns = new String[smartsPatternsV.size()];
                smartsDescriptions = new String[smartsPatternsV.size()];

                for (int i = 0; i < smartsPatterns.length; i++)
                {
                    HelperMethods.tokenize(smartsLine,
                        (String) smartsPatternsV.get(i), " \t\r\n");

                    if (smartsLine.size() == 0)
                    {
                        smartsPatterns[i] = null;
                    }
                    else
                    {
                        smartsPatterns[i] = (String) smartsLine.get(0);
                    }

                    if (smartsLine.size() > 1)
                    {
                        smartsDescriptions[i] = (String) smartsLine.get(1);
                    }
                }

                try
                {
                    calculatorSMARTS.setSMARTS2Calculate(smartsPatterns,
                        smartsDescriptions);
                }
                catch (FeatureException e)
                {
                    logger.error(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
