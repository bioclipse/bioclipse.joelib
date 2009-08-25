///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: TestLayout.java,v $
//  Purpose:  Example for generating 2D coordinates from SMILES.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:41 $
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
package joelib2.util.cdk;

import joelib2.example.SMILESExample;

import joelib2.io.BasicIOTypeHolder;

import joelib2.molecule.Molecule;

import org.apache.log4j.Category;


/**
 * Example for generating 2D coordinates from SMILES.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:41 $
 */
public class TestLayout extends SMILESExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(TestLayout.class
            .getName());

    //~ Constructors ///////////////////////////////////////////////////////////

    public TestLayout()
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
        SMILESExample joeMolTest = new SMILESExample();

        if (args.length != 2)
        {
            joeMolTest.usage();
            System.exit(0);
        }
        else
        {
            Molecule mol = joeMolTest.test(args[0],
                    BasicIOTypeHolder.instance().getIOType("SDF"),
                    BasicIOTypeHolder.instance().getIOType("SDF"));

            // Layout test
            System.out.println("Generate 2D coordinates:");
            CDKTools.generate2D(mol);
            System.out.println(mol);

            joeMolTest.write(mol, args[1],
                BasicIOTypeHolder.instance().getIOType("SDF"));
        }

        System.exit(0);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
