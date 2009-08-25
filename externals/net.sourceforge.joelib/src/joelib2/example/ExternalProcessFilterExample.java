///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ExternalProcessFilterExample.java,v $
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

import joelib2.ext.ExternalException;
import joelib2.ext.ExternalFactory;
import joelib2.ext.Title2Data;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.BasicReader;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.process.MoleculeProcessException;
import joelib2.process.ProcessFactory;
import joelib2.process.ProcessPipe;

import joelib2.process.filter.FilterException;
import joelib2.process.filter.FilterFactory;
import joelib2.process.filter.HasDataFilter;
import joelib2.process.filter.NOTFilter;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Category;


/**
 *  Example for converting molecules.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public class ExternalProcessFilterExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            ExternalProcessFilterExample.class.getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private String inputFile;

    private BasicIOType inType;
    private ProcessPipe processPipe;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        ExternalProcessFilterExample convert =
            new ExternalProcessFilterExample();

        if (args.length != 3)
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

        String titleAttribute = args[2];

        // get filter
        HasDataFilter hasDataFilter = null;
        NOTFilter notFilter = null;

        try
        {
            hasDataFilter = (HasDataFilter) FilterFactory.instance().getFilter(
                    "HasDataFilter");
            notFilter = (NOTFilter) FilterFactory.instance().getFilter(
                    "NOTFilter");
        }
        catch (FilterException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }

        //initialize filters
        hasDataFilter.init(titleAttribute);
        notFilter.init(hasDataFilter);

        // get process
        Title2Data title2data = null;

        try
        {
            title2data = (Title2Data) ExternalFactory.instance().getExternal(
                    "Title2Data");
            processPipe = (ProcessPipe) ProcessFactory.instance().getProcess(
                    "ProcessPipe");
        }
        catch (ExternalException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
        catch (MoleculeProcessException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }

        processPipe.addProcess(title2data, notFilter);

        return true;
    }

    /**
     *  A unit test for JUnit
     */
    public void test()
    {
        // create simple reader
        FileInputStream input = null;

        try
        {
            input = new FileInputStream(inputFile);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }

        BasicReader reader = null;

        try
        {
            reader = new BasicReader(input, inType);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, inType);

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

            //process data
            try
            {
                processPipe.process(mol, null);
            }
            catch (MoleculeProcessException ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            System.out.println(mol);
        }
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
        sb.append(" <input file>");
        sb.append(" <title_data_attribute>");
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
