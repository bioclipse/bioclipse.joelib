///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMARTSFragmentation.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:37 $
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
package joelib2.molecule.fragmentation;

import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicResourceLoader;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Fragmentation implementation based on SMARTS patterns for a molecule.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:37 $
 */
public class SMARTSFragmentation
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.molecule.fragmentation.SMARTSFragmentation");

    //~ Instance fields ////////////////////////////////////////////////////////

    private ContiguousFragments contigFragmenter = new ContiguousFragments();
    private List description;
    private List smarts;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Fragments a molecule and single atoms occuring in the molecule are allowed.
     */
    public MoleculeVector getFragmentation(Molecule mol)
    {
        return getFragmentation(mol, false, null);
    }

    /**
     * Fragments the molecule using SMARTS rules.
     *
     * @param molOriginal
     * @param skipSingleAtoms
     * @return JOEMolVector
     */
    public MoleculeVector getFragmentation(Molecule molOriginal,
        boolean skipSingleAtoms, List origAtomIdx)
    {
        if (smarts == null)
        {
            logger.error("No SMARTS pattern defined.");

            return null;
        }

        if ((molOriginal == null) || molOriginal.isEmpty())
        {
            logger.warn(
                "Molecule not defined or empty. It can not be fragmented.");

            return null;
        }

        Molecule mol = (Molecule) molOriginal.clone();

        SMARTSPatternMatcher pSMARTS;

        //String _description;
        List matchList;
        int[] iaTmp;

        for (int i = 0; i < smarts.size(); i++)
        {
            pSMARTS = (SMARTSPatternMatcher) smarts.get(i);

            //_description = (String) description.get(i);
            if ((mol == null) || mol.isEmpty())
            {
                logger.error("No molecular structure available for " +
                    mol.getTitle());

                continue;
            }
            else
            {
                // find substructures
                pSMARTS.match(mol);
                matchList = pSMARTS.getMatches();

                for (int nn = 0; nn < matchList.size(); nn++)
                {
                    iaTmp = (int[]) matchList.get(nn);

                    // that's the easiest way to generate fragments
                    // rings will be opened !;-)
                    mol.deleteBond(mol.getBond(iaTmp[0], iaTmp[1]));
                }
            }
        }

        // skip non-connected atoms will be applied in the
        // contigous fragmenter
        return contigFragmenter.getFragmentation(mol, skipSingleAtoms,
                origAtomIdx);
    }

    public boolean setPattySMARTS(String pattySMARTSfile)
    {
        List pattyLines = BasicResourceLoader.readLines(pattySMARTSfile);

        return setPattySMARTS(pattyLines);
    }

    public boolean setPattySMARTS(List pattySMARTS)
    {
        boolean successfull = true;
        smarts = new Vector(pattySMARTS.size());
        description = new Vector(pattySMARTS.size());

        String line;
        String smartsPattern;
        Vector lineV = new Vector();

        for (int i = 0; i < pattySMARTS.size(); i++)
        {
            line = (String) pattySMARTS.get(i);

            //System.out.println("------------------------------");
            HelperMethods.tokenize(lineV, line, " \t\r\n");

            // parse, initialize and generate SMARTS pattern
            // to allow fast pattern matching
            smartsPattern = (String) lineV.get(0);

            SMARTSPatternMatcher parsedSmarts = new BasicSMARTSPatternMatcher();

            if (!parsedSmarts.init(smartsPattern))
            {
                logger.error("Invalid SMARTS pattern: " + smartsPattern);
                successfull = false;

                continue;
            }

            if (parsedSmarts.getQueryAtomsSize() < 2)
            {
                logger.error("SMARTS pattern " + smartsPattern +
                    " must have at least two atoms to allow fragmentation.");
                successfull = false;

                continue;
            }

            // store smarts pattern and description
            smarts.add(parsedSmarts);

            if (lineV.size() < 2)
            {
                logger.warn("No description for SMARTS pattern: " +
                    smartsPattern);
                description.add("");
            }
            else
            {
                description.add(lineV.get(1));
            }
        }

        return successfull;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
