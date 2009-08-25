///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MorganTest.java,v $
//  Purpose:  Unique molecule numbering test.
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
package joelib2.algo.morgan;

import joelib2.algo.morgan.types.BasicTieResolver;

import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomImplicitValence;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.BasicReader;

import joelib2.molecule.Atom;
import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import wsi.ra.tool.BasicResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Category;


/**
 * Unique molecule numbering test.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public class MorganTest
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.algo.morgan.MorganTest");

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        MorganTest morgan = new MorganTest();

        if (args.length != 1)
        {
            morgan.usage();
            System.exit(0);
        }
        else
        {
            //        String molURL = new String("joelib/test/test.mol");
            morgan.test(args[0], BasicIOTypeHolder.instance().getIOType("SDF"),
                BasicIOTypeHolder.instance().getIOType("SDF"));
        }
    }

    /**
     *  A unit test for JUnit
     *
     * @param  molURL   Description of the Parameter
     * @param  inType   Description of the Parameter
     * @param  outType  Description of the Parameter
     */
    public void test(String molURL, BasicIOType inType, BasicIOType outType)
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
        Molecule renumberedMol;
        Morgan morgan = new Morgan(new BasicTieResolver());

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

            System.out.println("--------------------------------------");
            mol.deleteHydrogens();

            //logger.info("Processing (atoms="+mol.numAtoms()+"):" + mol.getTitle());
            //System.out.println("Hashcode:" + getHashcode(mol));
            //System.out.println("Molecule before renumbering:");
            //System.out.println(mol);
            if (morgan.calculate(mol))
            {
                renumberedMol = morgan.renumber(mol);

                //System.out.println("Molecule after renumbering:");
                //System.out.println(mol);
                String status;
                String statusSMILES;

                if (morgan.tieResolvingProblem())
                {
                    status = "Unsure";
                    statusSMILES = "Nearly Unique/Canonical";
                }
                else
                {
                    status = "Sure";
                    statusSMILES = "Unique/Canonical";
                }

                System.out.println(status +
                    " hashcode(without E/Z and S/R) for " + mol.getTitle() +
                    ": " + getHashcode(renumberedMol));
                System.out.print("Basic SMILES: " +
                    mol.toString(
                        BasicIOTypeHolder.instance().getIOType("SMILES")));
                System.out.print(statusSMILES + " SMILES: " +
                    renumberedMol.toString(
                        BasicIOTypeHolder.instance().getIOType("SMILES")));
            }
            else
            {
                System.out.println("");
            }

            //System.out.println(mol.toString(IOTypeHolder.instance().getIOType("SDF")));
        }
    }

    /**
     *  Description of the Method
     */
    public void usage()
    {
        StringBuffer sb = new StringBuffer();
        String programName = this.getClass().getName();

        sb.append("Usage is : ");
        sb.append("java -cp . ");
        sb.append(programName);

        System.out.println(sb.toString());

        System.exit(0);
    }

    /**
     * Primitive hashcode method without chirality and cis/trans.
     *
     * @param mol
     * @return int
     */
    private int getHashcode(Molecule mol)
    {
        int hash = mol.getRotorsSize();
        Atom atom;

        // take number of rings into account
        hash = (31 * hash) + mol.getSSSR().size();

        for (int i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);

            hash = (31 * hash) + atom.getIndex();
            hash = (31 * hash) + atom.getAtomicNumber();
            hash = (31 * hash) + AtomHeavyValence.valence(atom);
            hash = (31 * hash) + AtomImplicitValence.getImplicitValence(atom);
        }

        return hash;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
