///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GroupContributionExample.java,v $
//  Purpose:  Value prediction based on a group contribution model.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Stephen Jelfs, Joerg Kurt Wegner
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

import joelib2.algo.contribution.BasicGroupContributions;
import joelib2.algo.contribution.GroupContributionPredictor;

import joelib2.data.BasicGroupContributionHolder;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.smiles.SMILESParser;

import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.BasicResourceLoader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import java.util.StringTokenizer;

import org.apache.log4j.Category;


/**
 * Test for contribution lists for different models (e.g. logP, MR, PSA).
 *
 * @.author  Stephen Jelfs
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 * @see BasicPropertyHolder
 * @see BasicResourceLoader
 */
public class GroupContributionExample
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            GroupContributionExample.class.getName());
    static GroupContributionPredictor predictor =
        new GroupContributionPredictor();

    //~ Methods ////////////////////////////////////////////////////////////////

    // analyse/predict values for a given dataset (smiles first)
    public static void analyseDataset(String fileName,
        BasicGroupContributions _contrib)
    {
        // smiles parser
        SMILESParser smilesParser = new SMILESParser();

        try
        {
            // open reader
            byte[] bytes = BasicResourceLoader.instance()
                                              .getBytesFromResourceLocation(
                    fileName);

            if (bytes == null)
            {
                logger.error("Experimental values can't be loaded from \"" +
                    fileName + "\".");
                System.exit(1);
            }

            ByteArrayInputStream sReader = new ByteArrayInputStream(bytes);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                        sReader));

            // read lines
            String line;

            while ((line = reader.readLine()) != null)
            {
                // tokenize line
                StringTokenizer tokens = new StringTokenizer(line);

                if (!tokens.hasMoreTokens())
                {
                    continue;
                }

                // build molecule from smiles
                Molecule molecule = new BasicConformerMolecule();
                smilesParser.smiles2molecule(molecule, tokens.nextToken());

                // print prediction
                System.out.println("Prediction: " +
                    (float) predictor.predict(_contrib, molecule) +
                    " Original:" + line + "\n");
            }

            // close reader
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // test property predictions
    public static void main(String[] args)
    {
        BasicGroupContributions contrib = null;

        // FIRST LETS TRY SOME KNOWN PREDICTIONS FROM THE LITERATURE
        // predict polar surface area
        System.out.println("Polar Surface Area(PSA):");
        contrib = BasicGroupContributionHolder.instance().getGroupContributions(
                "PSA");

        // analyse molecules
        analyseDataset("joelib/test/psa.txt", contrib);

        // predict molar refractivity
        System.out.println("Molar Refractivity (MR):");
        contrib = BasicGroupContributionHolder.instance().getGroupContributions(
                "MR");

        // analyse molecules
        analyseDataset("joelib/test/logP.mr.txt", contrib);

        // predict hydrophobicity
        System.out.println("Hydrophobicity (logP):");
        contrib = BasicGroupContributionHolder.instance().getGroupContributions(
                "LogP");

        // analyse molecules
        analyseDataset("joelib/test/logP.mr.txt", contrib);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
