///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GhemicalExample.java,v $
//  Purpose:  Example for converting molecules.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:29 $
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

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.util.ghemical.GhemicalInterface;

import wsi.ra.tool.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Category;


/**
 * Example for converting molecules.
 *
 * @.author     wegnerj
 */
public class GhemicalExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(GhemicalExample.class
            .getName());
    private static boolean VERBOSE = false;
    private static BasicIOType verboseType = BasicIOTypeHolder.instance()
                                                              .getIOType(
            "SMILES");

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public PrintStream generatedOutput;
    private String inputFile;
    private BasicIOType inType;
    private String outputFile;
    private BasicIOType outType;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        GhemicalExample convert = new GhemicalExample();

        if (args.length != 4)
        {
            convert.usage();
            System.exit(0);
        }
        else
        {
            if (convert.parseCommandLine(args))
            {
                convert.test();
            }
            else
            {
                System.exit(1);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  args  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean parseCommandLine(String[] args)
    {
        if (args[0].indexOf("-i") == 0)
        {
            String inTypeS = args[0].substring(2);
            inType = BasicIOTypeHolder.instance().getIOType(inTypeS
                    .toUpperCase());

            if (inType == null)
            {
                logger.error("Input type '" + inTypeS + "' not defined.");

                return false;
            }
        }

        inputFile = args[1];

        if (args[2].indexOf("-o") == 0)
        {
            String outTypeS = args[2].substring(2);
            outType = BasicIOTypeHolder.instance().getIOType(outTypeS
                    .toUpperCase());

            if (outType == null)
            {
                logger.error("Output type '" + outTypeS + "' not defined.");

                return false;
            }
        }

        outputFile = args[3];

        return true;
    }

    /**
     *  A unit test for JUnit
     *
     */
    public void test()
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

        // get molecules from resource URL
        //    BufferedInputStream   in       = null;
        //    BufferedOutputStream  out      = null;
        FileInputStream in = null;
        FileOutputStream out = null;

        // get molecule loader/writer
        MoleculeFileIO loader = null;
        MoleculeFileIO writer = null;

        try
        {
            //      in = new BufferedInputStream(new FileInputStream(inputFile));
            //      out = new BufferedOutputStream( new FileOutputStream(outputFile));
            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputFile);
            loader = MoleculeFileHelper.getMolReader(in, inType);
            writer = MoleculeFileHelper.getMolWriter(out, outType);
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

        if (!writer.writeable())
        {
            logger.error(outType.getRepresentation() + " is not writeable.");
            logger.error("You're invited to write one !;-)");
            System.exit(1);
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, outType);
        boolean success = true;
        StopWatch watch = new StopWatch();
        int molCounter = 0;

        //GhemicalInterface ghemical = GhemicalInterface.instance();
        double tresholdDeltaE = 1.0e-14;
        double tresholdStep = 6.0e-11;
        int numSteps = 2000;

        logger.info("Start energy minimization ...");

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

                if (mol.isEmpty())
                {
                    logger.error("No molecule loaded.");
                    System.exit(1);
                }

                //mol.deleteHydrogens();
                // molecular mechanics (MM) energy minimization
                if (!GhemicalInterface.instance().doGeometryOptimization(mol,
                            numSteps, tresholdDeltaE, tresholdStep, true))
                {
                    logger.error("Could not apply energy minimization.");
                    System.exit(1);
                }

                if (VERBOSE)
                {
                    System.out.println("write " + mol.toString(verboseType));
                }

                success = writer.write(mol);

                if (!success)
                {
                    break;
                }

                molCounter++;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }
            catch (MoleculeIOException ex)
            {
                ex.printStackTrace();
                molCounter++;
                logger.info("Molecule entry (#" + molCounter +
                    ") was skipped: " + mol.getTitle());
            }

            if ((molCounter % 500) == 0)
            {
                logger.info("... " + molCounter +
                    " molecules successful converted in " +
                    watch.getPassedTime() + " ms.");
            }
        }

        logger.info("... " + molCounter +
            " molecules successful converted in " + watch.getPassedTime() +
            " ms.");
    }

    /**
     *  Description of the Method
     */
    public void usage()
    {
        StringBuffer sb = new StringBuffer();
        String programName = this.getClass().getName();

        //              String programPackage = this.getClass().getPackage().getName();
        sb.append("Usage is :\n");
        sb.append("java -cp . ");
        sb.append(programName);
        sb.append(" -i<inputFormat>");
        sb.append(" <input file>");
        sb.append(" -o<outputFormat>");
        sb.append(" <output file>");
        sb.append("\nSupported molecule types:");
        sb.append(BasicIOTypeHolder.instance().toString());
        System.out.println(sb.toString());

        System.exit(0);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
