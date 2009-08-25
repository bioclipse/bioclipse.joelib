///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DescriptorStatisticExample.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
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

import joelib2.process.types.DescriptorBinning;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Category;


/**
 *  Example for converting molecules.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:29 $
 */
public class DescriptorStatisticExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            DescriptorStatisticExample.class.getName());
    public static final int CONTINUE = 0;
    public static final int STOP = 1;
    public static final int STOP_USAGE = 2;
    private static DescriptorBinning binning = null;

    //~ Instance fields ////////////////////////////////////////////////////////

    private String inputFile;

    private BasicIOType inType;
    private String outputFile;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        DescriptorStatisticExample convert = new DescriptorStatisticExample();

        int status = convert.parseCommandLine(args);

        if (status == CONTINUE)
        {
            convert.test();
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
     *  Description of the Method
     *
     * @param  args  Description of the Parameter
     * @return       Description of the Return Value
     */
    public int parseCommandLine(String[] args)
    {
        // get default properties
        //Properties prop = PropertyHolder.instance().getProperties();
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

        return CONTINUE;
    }

    /**
     *  A unit test for JUnit
     */
    public boolean test()
    {
        PrintStream result = null;

        if (outputFile != null)
        {
            try
            {
                result = new PrintStream(new FileOutputStream(outputFile));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                return false;
            }
        }

        binning = DescriptorBinning.getDescBinning(inType, inputFile);

        if (binning != null)
        {
            if (outputFile != null)
            {
                result.println("Descriptor statistic:\n " +
                    binning.getDescStatistic().toString());
                result.println("Descriptor binning:\n " + binning.toString());
            }
            else
            {
                System.out.println("Descriptor statistic:\n " +
                    binning.getDescStatistic().toString() + "\n");
                System.out.println("Descriptor binning:\n " +
                    binning.toString());
            }
        }

        return true;
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
        sb.append("\n\nSupported molecule types:");
        sb.append(BasicIOTypeHolder.instance().toString());
        sb.append(
            "\n\nThis is version $Revision: 1.7 $ ($Date: 2005/02/17 16:48:29 $)\n");

        System.out.println(sb.toString());

        //System.exit(0);
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
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
