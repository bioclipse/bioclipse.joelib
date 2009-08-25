///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ComparisonExample.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
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

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.PairData;

import joelib2.process.MoleculeProcessException;

import joelib2.process.types.DistanceCalculation;

import wsi.ra.tool.BasicResourceLoader;
import wsi.ra.tool.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.List;

import org.apache.log4j.Category;


/**
 *  Example for converting molecules.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public class ComparisonExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            ComparisonExample.class.getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public PrintStream generatedOutput;
    private String comparisonInputFile;
    private BasicIOType comparisonInType;
    private String descriptorNameFile;
    private String identifier;
    private String outputFile;
    private String targetInputFile;
    private BasicIOType targetInType;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        ComparisonExample comparison = new ComparisonExample();

        if (args.length < 6)
        {
            comparison.usage();
            System.exit(0);
        }
        else
        {
            if (comparison.parseCommandLine(args))
            {
                comparison.test();
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
            targetInType = BasicIOTypeHolder.instance().getIOType(inTypeS);

            if (targetInType == null)
            {
                logger.error("Input type '" + inTypeS + "' not defined.");

                return false;
            }
        }

        targetInputFile = args[1];

        if (args[2].indexOf("-i") == 0)
        {
            String inTypeS = args[2].substring(2);
            comparisonInType = BasicIOTypeHolder.instance().getIOType(inTypeS);

            if (comparisonInType == null)
            {
                logger.error("Input type '" + inTypeS + "' not defined.");

                return false;
            }
        }

        comparisonInputFile = args[3];

        descriptorNameFile = args[4];
        outputFile = args[5];

        if (args.length > 6)
        {
            identifier = args[6];
        }

        return true;
    }

    /**
     *  A unit test for JUnit
     */
    public void test()
    {
        if ((targetInType == null) || (targetInputFile == null) ||
                (comparisonInType == null) || (comparisonInputFile == null) ||
                (descriptorNameFile == null) || (outputFile == null))
        {
            logger.error("Not correctly initialized.");
            logger.error("target input type: " + targetInType.getName());
            logger.error("target input file: " + targetInputFile);
            logger.error("comparison input type: " +
                comparisonInType.getName());
            logger.error("comparison input file: " + comparisonInputFile);
            logger.error("descriptor file: " + descriptorNameFile);
            logger.error("output file: " + outputFile);
            System.exit(1);
        }

        // get molecules from resource URL
        FileInputStream cin = null;

        //    FileOutputStream  out      = null;
        // get molecule loader/writer
        MoleculeFileIO cloader = null;

        //    MoleculeFileType  writer   = null;
        PrintStream result = null;

        try
        {
            cin = new FileInputStream(comparisonInputFile);
            cloader = MoleculeFileHelper.getMolReader(cin, comparisonInType);
            result = new PrintStream(new FileOutputStream(outputFile));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (!cloader.readable())
        {
            logger.error(comparisonInType.getRepresentation() +
                " is not readable.");
            logger.error("You're invited to write one !;-)");
            System.exit(1);
        }

        //    if (!writer.writeable())
        //    {
        //      logger.error(outType.getRepresentation() + " is not writeable.");
        //      logger.error("You're invited to write one !;-)");
        //      System.exit(1);
        //    }
        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(comparisonInType,
                comparisonInType);
        boolean success = true;
        StopWatch watch = new StopWatch();
        int molCounter = 0;

        // get names for descriptors2compare
        List tmpNames = BasicResourceLoader.readLines(descriptorNameFile);

        if (tmpNames == null)
        {
            logger.error(
                "File with descriptor names of the output data could not be found.");
            System.exit(1);
        }

        int size = tmpNames.size();
        String[] descriptorNames = new String[size];

        for (int i = 0; i < size; i++)
        {
            descriptorNames[i] = (String) tmpNames.get(i);
        }

        // initialize comparison/distanceCalculation class
        DistanceCalculation distCalc = new DistanceCalculation();

        try
        {
            distCalc.init(targetInType, targetInputFile, descriptorNames);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }

        logger.info("Start comparison calculation ...");

        //    String distString="DISTANCE2"+distCalc.getTargetMol().getTitle();
        double[] distances;

        for (;;)
        {
            mol.clear();

            try
            {
                success = cloader.read(mol);

                if (!success)
                {
                    break;
                }

                if (mol.isEmpty())
                {
                    logger.warn("No molecule loaded. Continue...");

                    //          System.exit(1);
                }

                try
                {
                    distCalc.process(mol, null);

                    //          System.out.print("dist:");
                    // print identifier, if available
                    if (identifier != null)
                    {
                        PairData pairData = mol.getData(identifier, false);

                        if (pairData != null)
                        {
                            result.print(pairData);
                        }
                        else
                        {
                            result.print("-1 ");
                        }

                        result.print(' ');
                    }

                    distances = distCalc.getDistanceValues();

                    for (int i = 0; i < distances.length; i++)
                    {
                        //            System.out.print(" " + distances[i]);
                        result.print(' ');
                        result.print(distances[i]);
                    }

                    result.println();

                    //          System.out.println(" in mol: " + mol.getTitle());
                }
                catch (MoleculeProcessException ex)
                {
                    logger.error(ex.toString());
                    distCalc = null;

                    return;
                }

                //        System.out.println("aaa:"+mol.toString());
                //      try{
                //        success = writer.write(mol);
                //        if (!success)
                //        {
                //          break;
                //        }
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
                    " molecules successful compared in " +
                    watch.getPassedTime() + " ms.");
            }
        }

        logger.info("... " + molCounter + " molecules successful compared in " +
            watch.getPassedTime() + " ms.");
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
        sb.append(" -i<inputFormat>");
        sb.append(" <target_input file>");
        sb.append(" -i<inputFormat>");
        sb.append(" <comparison_input file>");
        sb.append(" <descriptor file>");
        sb.append(" <output file>");
        sb.append(" <identifier>");
        sb.append("\nSupported molecule types:");
        sb.append(BasicIOTypeHolder.instance().toString());
        sb.append(
            "\n\nThis is version $Revision: 1.9 $ ($Date: 2005/02/17 16:48:29 $)\n");
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
