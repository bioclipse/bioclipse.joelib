///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: KierShape1.java,v $
//  Purpose:  Calculates the Kier Shape for paths with length one.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Jan Bruecker, Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:31 $
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
package joelib2.feature.types;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractDouble;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureHelper;

import joelib2.feature.types.atomlabel.AtomIsHydrogen;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import org.apache.log4j.Category;


/**
 *  Calculates the Kier Shape for paths with length one.
 *
 * @.author    Jan Bruecker
 * @.author    wegner
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:31 $
 * @.cite tc00kiershape
 */
public class KierShape1 extends AbstractDouble
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(KierShape1.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomIsHydrogen.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public KierShape1()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.DoubleResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return KierShape1.class.getName();
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
     * Gets the doubleValue attribute of the KierShape1 object
     *
     * @param mol  Description of the Parameter
     * @return     The doubleValue value
     */
    public double getDoubleValue(Molecule mol)
    {
        double kier;

        double paths = 0;
        double atoms = 0;
        Atom atom;
        AtomIterator ait = mol.atomIterator();

        //    NbrAtomIterator   nait;
        while (ait.hasNext())
        {
            //Molecule graph must be H-Atom depleted
            atom = ait.nextAtom();

            if (!AtomIsHydrogen.isHydrogen(atom))
            {
                atoms++;
            }
        }

        BondIterator bit = mol.bondIterator();
        Bond bond;

        while (bit.hasNext())
        {
            //Molecule graph must be H-Atom depleted
            bond = bit.nextBond();

            if (!AtomIsHydrogen.isHydrogen(bond.getBegin()) &&
                    !AtomIsHydrogen.isHydrogen(bond.getEnd()))
            {
                paths++;
            }
        }

        //    System.out.println("Nodes: " + atoms + "\nPaths: " + paths);
        if (paths > 0)
        {
            kier = ((atoms * ((atoms - 1) * (atoms - 1))) / (paths * paths));
        }
        else
        {
            return 0.0;
        }

        return kier;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
