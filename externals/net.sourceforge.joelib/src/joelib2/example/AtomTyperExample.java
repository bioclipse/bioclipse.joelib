///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomTyperExample.java,v $
//  Purpose:  Example for the pattern assignment of SMARTS pattern.
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

import joelib2.data.BasicElementHolder;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.BasicReader;

import joelib2.molecule.Atom;
import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.smarts.ProgrammableAtomTyper;

import wsi.ra.tool.BasicResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Category;


/**
 *  Example for the pattern assignment of SMARTS pattern.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public class AtomTyperExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(AtomTyperExample.class
            .getName());

    //~ Constructors ///////////////////////////////////////////////////////////

    public AtomTyperExample()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        AtomTyperExample joeMolTest = new AtomTyperExample();

        if (args.length != 2)
        {
            joeMolTest.usage();
            System.exit(0);
        }
        else
        {
            //        String pattyFile = new String("joelib/test/patty.txt");
            //        String molURL = new String("joelib/test/multiple.mol");
            joeMolTest.test(args[0], args[1],
                BasicIOTypeHolder.instance().getIOType("SDF"),
                BasicIOTypeHolder.instance().getIOType("SDF"));
        }

        System.exit(0);
    }

    /**
     *  A unit test for JUnit
     *
     * @param  molURL     Description of the Parameter
     * @param  pattyFile  Description of the Parameter
     * @param  inType     Description of the Parameter
     * @param  outType    Description of the Parameter
     */
    public void test(String molURL, String pattyFile, BasicIOType inType,
        BasicIOType outType)
    {
        // get molecules from resource URL
        byte[] bytes = BasicResourceLoader.instance()
                                          .getBytesFromResourceLocation(molURL);

        if (bytes == null)
        {
            logger.error("Molecule can't be loaded at \"" + molURL + "\".");
            System.exit(1);
        }

        ByteArrayInputStream sReader = new ByteArrayInputStream(bytes);

        // get patty rules
        ProgrammableAtomTyper patty = new ProgrammableAtomTyper();

        if (!patty.readRules(pattyFile))
        {
            System.exit(1);
        }

        // create simple reader
        BasicReader reader = null;

        try
        {
            reader = new BasicReader(sReader, inType);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, outType);
        int[] pattyTypes;

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

            System.out.println(mol);

            System.out.println("patty assignment:");
            pattyTypes = patty.assignTypes(mol);

            int i1;

            for (int i = 0; i < mol.getAtomsSize(); i++)
            {
                i1 = i + 1;

                Atom atom = mol.getAtom(i1);
                String typeString = patty.getStringFromType(pattyTypes[i]);

                if (typeString == null)
                {
                    System.out.println("atom #" + i1 + " " +
                        BasicElementHolder.instance().getSymbol(
                            atom.getAtomicNumber()));
                }
                else
                {
                    System.out.println("atom #" + i1 + " " +
                        BasicElementHolder.instance().getSymbol(
                            atom.getAtomicNumber()) + " is of type " +
                        typeString + "");
                }
            }
        }
    }

    /**
     *  Description of the Method
     */
    public void usage()
    {
        StringBuffer sb = new StringBuffer();
        String programName = this.getClass().getName();

        sb.append("\nUsage is : ");
        sb.append("java -cp . ");
        sb.append(programName);
        sb.append(" <SDF file> <patty file>");
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
