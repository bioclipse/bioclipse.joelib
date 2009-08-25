///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: FractionRotatableBonds.java,v $
//  Purpose:  Fraction of rotatable bonds.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
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
import joelib2.feature.types.bondlabel.BondIsRotor;

import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.BondIterator;

import org.apache.log4j.Category;


/**
 * Fraction of rotatable bonds.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:31 $
 */
public class FractionRotatableBonds extends AbstractDouble
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            FractionRotatableBonds.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomIsHydrogen.class, BondIsRotor.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape2 object
     */
    public FractionRotatableBonds()
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
        return FractionRotatableBonds.class.getName();
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
     * Gets the defaultAtoms attribute of the NumberOfC object
     *
     * @return   The defaultAtoms value
     */
    public double getDoubleValue(Molecule mol)
    {
        int heavyBonds = 0;
        int rotatableBonds = 0;
        BondIterator bit = mol.bondIterator();
        Bond bond;

        while (bit.hasNext())
        {
            //Molecule graph must be H-Atom depleted
            bond = bit.nextBond();

            if (!AtomIsHydrogen.isHydrogen(bond.getBegin()) &&
                    !AtomIsHydrogen.isHydrogen(bond.getEnd()))
            {
                heavyBonds++;

                if (BondIsRotor.isRotor(bond))
                {
                    rotatableBonds++;
                }
            }
        }

        if (heavyBonds == 0)
        {
            return 0.0;
        }
        else
        {
            return (double) rotatableBonds / (double) heavyBonds;
        }
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
