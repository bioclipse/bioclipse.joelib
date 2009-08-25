///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: FeatureExample.java,v $
//  Purpose:  Example for loading molecules and get atom properties.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
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

import joelib2.algo.BFS;
import joelib2.algo.BFSResult;
import joelib2.algo.DFS;

import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureFactory;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.types.KierShape1;
import joelib2.feature.types.atomlabel.AtomPartialCharge;
import joelib2.feature.types.count.HBD1;
import joelib2.feature.types.count.NumberOfN;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.BasicReader;

import joelib2.molecule.Atom;
import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;

import wsi.ra.tool.BasicResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Category;


/**
 *  Example for loading molecules and get atom properties.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:29 $
 */
public class FeatureExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(FeatureExample.class
            .getName());
    private static final String delimiter =
        "----------------------------------------------------";

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        FeatureExample joeDescTest = new FeatureExample();

        if (args.length != 1)
        {
            joeDescTest.usage();
            System.exit(0);
        }
        else
        {
            //        String molURL = new String("joelib/test/test.mol");
            joeDescTest.test(args[0],
                BasicIOTypeHolder.instance().getIOType("SDF"),
                BasicIOTypeHolder.instance().getIOType("SDF"));
        }

        System.exit(0);
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

        // show all available descriptors
        Enumeration enumeration = FeatureHelper.instance().getFeatureNames();
        System.out.println("Available Descriptors:");
        System.out.println(delimiter);

        int index = 1;
        boolean hbdAvailable = false;
        String tmp;

        for (; enumeration.hasMoreElements(); index++)
        {
            tmp = (String) enumeration.nextElement();
            System.out.println("" + index + ": " + tmp);

            if (tmp.indexOf(HBD1.getName()) != -1)
            {
                hbdAvailable = true;
            }
        }

        //show available atom property descriptors
        List atomPropDescs = FeatureHelper.instance().getAtomLabelFeatures();
        System.out.println("\nAtom property descriptors:");
        System.out.println(delimiter);

        for (int i = 0; i < atomPropDescs.size(); i++)
        {
            System.out.println(atomPropDescs.get(i));
        }

        //show available native value descriptors
        List nativeDescs = FeatureHelper.instance().getNativeFeatures();
        System.out.println("\nNative value descriptors:");
        System.out.println(delimiter);

        for (int i = 0; i < nativeDescs.size(); i++)
        {
            System.out.println(nativeDescs.get(i));
        }

        // get descriptor names
        String[] defDescNames =
            {
                KierShape1.getName(), BFS.getName(), DFS.getName(),
                AtomPartialCharge.getName(), NumberOfN.getName()
            };
        String[] descNames = null;

        if (hbdAvailable)
        {
            descNames = new String[defDescNames.length + 1];
            descNames[defDescNames.length] = HBD1.getName();
        }
        else
        {
            descNames = new String[defDescNames.length];
        }

        for (int i = 0; i < defDescNames.length; i++)
        {
            descNames[i] = defDescNames[i];
        }

        // get descriptor base
        Feature[] descriptor = new Feature[descNames.length];
        FeatureDescription[] descDescription =
            new FeatureDescription[descNames.length];

        try
        {
            for (int i = 0; i < descriptor.length; i++)
            {
                descriptor[i] = FeatureFactory.getFeature(descNames[i]);

                if (descriptor[i] == null)
                {
                    logger.error("Descriptor " + descNames[i] +
                        " can't be loaded.");
                    System.exit(1);
                }

                System.out.println(delimiter);
                System.out.println("Loaded descriptor (" + descNames[i] +
                    "):\n" + descriptor[i].getDescInfo());
                descDescription[i] = descriptor[i].getDescription();
                System.out.println(descDescription[i].getText());
            }
        }
        catch (FeatureException ex)
        {
            //      ex.printStackTrace();
            logger.error(ex.toString());
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

            //------------------------------------------------------------------
            // calculate descriptors and print results
            //------------------------------------------------------------------
            FeatureResult[] results = new FeatureResult[descriptor.length];
            Hashtable[] inits = new Hashtable[descriptor.length];

            // initialize BFS
            Atom startAtom = mol.getAtom(1);
            inits[1] = new Hashtable();
            inits[1].put(BFS.STARTING_ATOM, startAtom);
            inits[2] = new Hashtable();
            inits[2].put(DFS.STARTING_ATOM, startAtom);

            // start calculations
            try
            {
                for (int i = 0; i < descriptor.length; i++)
                {
                    // check if descriptor has already been calculated
                    //          if(mol.hasData("Kier Shape 1"))
                    //          {
                    //            PairData pdTmp = (PairData)mol.getData("Kier Shape 1");
                    //            System.out.println("Kier Shape 1 calculated: "+pdTmp.getValue());
                    //          }
                    descriptor[i].clear();
                    results[i] = descriptor[i].calculate(mol, inits[i]);

                    // has something weird happen
                    if (results[i] == null)
                    {
                        System.exit(1);
                    }

                    // add descriptor data to molecule
                    BasicPairData dp = new BasicPairData();
                    dp.setKey(descriptor[i].getDescInfo().getName());
                    dp.setKeyValue(results[i]);
                    mol.addData(dp);
                }
            }
            catch (FeatureException ex)
            {
                ex.printStackTrace();
            }

            // show SD molecule file
            System.out.println(delimiter);
            System.out.println(mol);

            // use your own data operation on the descriptor result
            // just print result
            System.out.println(delimiter);
            System.out.println("Descriptor result output");

            // print kier 1 result
            //      System.out.println("Kier shape 1 result: " + results[0]);
            // print BFS result
            BFSResult resultBFS = (BFSResult) results[1];

            //      System.out.println("BFS result: " + resultBFS);
            StringBuffer sb = new StringBuffer("BFS result:\n");

            for (int i = 0; i < resultBFS.getTraverse().length; i++)
            {
                sb.append("atom #");
                sb.append(i + 1);
                sb.append(" visited at ");
                sb.append(resultBFS.getTraverse()[i]);
                sb.append(" position when search starts from atom #" +
                    startAtom.getIndex());
                sb.append(".\n");
            }

            System.out.println(sb.toString());

            //      // print DFS result
            //      DFSResult       resultDFS  = (DFSResult) results[2];
            //      sb = new StringBuffer("DFS result:\n");
            //      for (int i = 0; i < resultDFS.discovered.length; i++)
            //      {
            //        sb.append("atom #");
            //        sb.append(i + 1);
            //        sb.append(" discovered at ");
            //        sb.append(resultDFS.discovered[i]);
            //        sb.append(" finished at ");
            //        sb.append(resultDFS.finished[i]);
            //        sb.append(" position when search starts from atom #" + startAtom.getIdx());
            //        sb.append(".\n");
            //      }
            //      System.out.println(sb.toString());
            //
            //      if (hbdAvailable)
            //      {
            //        IntResult  resultHBA  = (IntResult) results[3];
            //        System.out.println("Number of HBA:" + resultHBA.getInt());
            //      }
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
        sb.append(" <SDF file>");
        sb.append(
            "\n\nThis is version $Revision: 1.11 $ ($Date: 2005/02/17 16:48:29 $)\n");

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
