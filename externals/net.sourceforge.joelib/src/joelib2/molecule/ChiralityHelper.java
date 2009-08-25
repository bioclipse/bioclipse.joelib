///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ChiralityHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:36 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.molecule;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.types.GraphPotentials;
import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomHybridisation;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.NbrAtomIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom tree.
 *
 * @.author     wegnerj
 * @.wikipedia  Chirality (chemistry)
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:36 $
 */
public class ChiralityHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:36 $";
    private static Category logger = Category.getInstance(ChiralityHelper.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            GraphPotentials.class, AtomHeavyValence.class,
            AtomHybridisation.class
        };
    private static final double CHIRALITY_GP_THRESHOLD = 0.001;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     */
    public static synchronized void findChiralCenters(Molecule mol,
        boolean[] isChiral)
    {
        if (mayHaveChiralCenter(mol))
        {
            Bond bond;
            BondIterator bit = mol.bondIterator();

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                if (bond.isWedge() || bond.isHash())
                {
                    (bond.getBegin()).setChiral();
                }
            }

            double[] graphPotentials = GraphPotentials.graphPotentials(mol);
            Atom atom;
            AtomIterator ait = mol.atomIterator();

            while (ait.hasNext())
            {
                atom = ait.nextAtom();

                if ((AtomHybridisation.getIntValue(atom) == 3) &&
                        (AtomHeavyValence.valence(atom) >= 3) &&
                        !isChiral[atom.getIndex() - 1])
                {
                    if (checkNeighbours(atom, graphPotentials))
                    {
                        isChiral[atom.getIndex() - 1] = true;
                    }
                }
            }
        }
    }

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

    /**
     * @param atom
     * @param isChiral
     */
    private static boolean checkNeighbours(Atom atom, double[] graphPotentials)
    {
        NbrAtomIterator nait = atom.nbrAtomIterator();
        boolean ischiral = true;
        List<Double> gpNbr = new Vector<Double>();
        Atom thisNbr;
        double otherNbrGP;
        double thisNbrGP;

        while (nait.hasNext())
        {
            thisNbr = nait.nextNbrAtom();
            thisNbrGP = graphPotentials[thisNbr.getIndex() - 1];

            for (int gpNbrIndex = 0; gpNbrIndex < gpNbr.size(); gpNbrIndex++)
            {
                otherNbrGP = gpNbr.get(gpNbrIndex).doubleValue();

                if (Math.abs(thisNbrGP - otherNbrGP) < CHIRALITY_GP_THRESHOLD)
                {
                    ischiral = false;
                }
            }

            if (ischiral)
            {
                gpNbr.add(new Double(graphPotentials[thisNbr.getIndex() - 1]));
            }
            else
            {
                break;
            }
        }

        return ischiral;
    }

    /**
     * @param mol
     * @return
     */
    private static boolean mayHaveChiralCenter(Molecule mol)
    {
        // do quick test to see if there are any possible chiral centers
        boolean mayHaveChiralCenter = false;
        Atom atom;
        Atom nbr;
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if ((AtomHybridisation.getIntValue(atom) == 3) &&
                    (AtomHybridisation.getIntValue(atom) >= 3))
            {
                mayHaveChiralCenter = true;

                break;
            }
        }

        return mayHaveChiralCenter;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
