///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GroupContributionPredictor.java,v $
//  Purpose:  Value prediction based on a group contribution model.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Stephen Jelfs, Joerg Kurt Wegner
//  Version:  $Revision: 1.4 $
//            $Date: 2005/02/17 16:48:28 $
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
package joelib2.algo.contribution;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.types.atomlabel.AtomExplicitHydrogenCount;
import joelib2.feature.types.atomlabel.AtomImplicitHydrogenCount;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.smarts.SMARTSPatternMatcher;

import java.util.Iterator;

import org.apache.log4j.Category;


/**
 * Value prediction based on a group contribution model.
 *
 * @.author  Stephen Jelfs
 * @.author  wegnerj
 * @.wikipedia QSAR
 * @.wikipedia Data mining
 * @.license GPL
 * @.cvsversion    $Revision: 1.4 $, $Date: 2005/02/17 16:48:28 $
 * @.cite ers00
 * @.cite wc99
 */
public class GroupContributionPredictor
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.4 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:28 $";
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AtomExplicitHydrogenCount.class, AtomImplicitHydrogenCount.class,
            AtomIsHydrogen.class
        };
    private static Category logger = Category.getInstance(
            GroupContributionPredictor.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    // predict molecular properties
    public static double predict(GroupContributions groupContribution,
        Molecule molecule)
    {
        if (molecule.isEmpty())
        {
            logger.warn("Empty molecule '" + molecule.getTitle() +
                "'. Value set to NaN.");

            return Double.NaN;
        }

        // atom contributions
        double[] atomValues = new double[molecule.getAtomsSize()];

        // set atom contributions
        for (int i = 0; i < groupContribution.getAtomSmarts().size(); ++i)
        {
            // get smarts
            SMARTSPatternMatcher smarts = (SMARTSPatternMatcher)
                groupContribution.getAtomSmarts().get(i);

            // find atom matches
            smarts.match(molecule);

            // iterate through matches
            Iterator matches = smarts.getMatches().iterator();

            while (matches.hasNext())
            {
                // get matched atom IDs
                int[] atoms = (int[]) matches.next();

                // store value of matched atom
                atomValues[atoms[0] - 1] =
                    ((Double) groupContribution.getAtomContributions().get(i))
                    .doubleValue();
            }
        }

        // hydrogen contributions
        double[] hydrogenValues = new double[molecule.getAtomsSize()];

        // set hydrogen contributions
        for (int i = 0; i < groupContribution.getHydrogenSmarts().size(); ++i)
        {
            // get smarts
            SMARTSPatternMatcher smarts = (SMARTSPatternMatcher)
                groupContribution.getHydrogenSmarts().get(i);

            // find atom matches
            smarts.match(molecule);

            // iterate through matches
            Iterator matches = smarts.getMatches().iterator();

            while (matches.hasNext())
            {
                // get matched atom IDs
                int[] atoms = (int[]) matches.next();

                // store value of matched atom
                hydrogenValues[atoms[0] - 1] =
                    ((Double) groupContribution.getHydrogenContributions().get(
                            i)).doubleValue();
            }
        }

        // total atomic and hydrogen contributions
        double total = 0.0;

        for (int i = 1; i <= molecule.getAtomsSize(); ++i)
        {
            // get atom
            Atom atom = molecule.getAtom(i);

            // skip hydrogens
            if (AtomIsHydrogen.isHydrogen(atom))
            {
                continue;
            }

            // get total hydrogen count
            int hydrogenCount = AtomImplicitHydrogenCount.getIntValue(atom) +
                AtomExplicitHydrogenCount.getIntValue(atom);

            // add atom contribution to total
            total += atomValues[i - 1];

            // add hydrogen contribution to total
            total += (hydrogenValues[i - 1] * hydrogenCount);
        }

        //        for(int i=0; i<atomValues.length; ++i) {
        //           System.out.println("atom "+i+": "+atomValues[i]);
        //        }
        //        for(int i=0; i<hydrogenValues.length; ++i) {
        //           System.out.println("hydrogen "+i+": "+atomValues[i]);
        //        }
        // return total
        return total;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
