///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BondIsEster.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:32 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
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
package joelib2.feature.types.bondlabel;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractDynamicBondProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureHelper;

import joelib2.feature.result.DynamicArrayResult;

import joelib2.feature.types.atomlabel.AtomIsCarbon;
import joelib2.feature.types.atomlabel.AtomIsOxygen;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.BondIterator;

import org.apache.log4j.Category;


/**
 * Is this atom negatively charged atom.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:32 $
 */
public class BondIsEster extends AbstractDynamicBondProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:32 $";
    private static Category logger = Category.getInstance(BondIsEster.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AtomIsCarbon.class, AtomIsOxygen.class, BondIsCarbonyl.class
        };

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public BondIsEster()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.BondDynamicResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return BondIsEster.class.getName();
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
     *  Gets the ester attribute of the <tt>Bond</tt> object
     *
     * @return    The ester value
     */
    public static boolean isEster(Bond bond)
    {
        boolean isEster = false;
        Atom atom1;
        Atom atom2;
        atom1 = atom2 = null;

        if (AtomIsCarbon.isCarbon(bond.getBegin()) &&
                AtomIsOxygen.isOxygen(bond.getEnd()))
        {
            atom1 = bond.getBegin();
            atom2 = bond.getEnd();
        }

        if (AtomIsOxygen.isOxygen(bond.getBegin()) &&
                AtomIsCarbon.isCarbon(bond.getEnd()))
        {
            atom1 = bond.getEnd();
            atom2 = bond.getBegin();
        }

        if ((atom1 == null) || (atom2 == null) || (bond.getBondOrder() != 1))
        {
            isEster = false;
        }
        else
        {
            BondIterator bit = atom1.bondIterator();
            Bond atom1nbr;

            while (bit.hasNext())
            {
                atom1nbr = bit.nextBond();

                if (BondIsCarbonyl.isCarbonyl(atom1nbr))
                {
                    isEster = true;

                    break;
                }
            }
        }

        return isEster;
    }

    public Object getBondPropertiesArray(Molecule mol)
    {
        // get electrotopological state index
        int bondsSize = mol.getBondsSize();
        boolean[] ester = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, bondsSize);

        Bond bond;

        for (int i = 0; i < bondsSize; i++)
        {
            bond = mol.getBond(i);
            ester[i] = isEster(bond);
        }

        return ester;
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
