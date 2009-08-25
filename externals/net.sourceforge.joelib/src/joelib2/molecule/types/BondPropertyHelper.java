///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: BondPropertyHelper.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 25, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:37 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.molecule.types;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;

import joelib2.molecule.Bond;


/**
 * TODO description.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:37 $
 */
public class BondPropertyHelper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static boolean getBooleanBondProperty(Bond bond, String name)
        throws FeatureException
    {
        return getBooleanBondProperty(bond, name, false);
    }

    public static boolean getBooleanBondProperty(Bond bond, String name,
        boolean ignoreModCounter) throws FeatureException
    {
        checkNotNull(bond);

        BondProperties labelCache = getLabelCache(bond, name, ignoreModCounter);
        boolean boolValue = false;

        if (labelCache.getIntValue(bond.getIndex()) != 0)
        {
            boolValue = true;
        }

        return boolValue;
    }

    public static double getDoubleBondProperty(Bond bond, String name)
        throws FeatureException
    {
        return getDoubleBondProperty(bond, name, false);
    }

    public static double getDoubleBondProperty(Bond bond, String name,
        boolean ignoreModCounter) throws FeatureException
    {
        checkNotNull(bond);

        BondProperties labelCache = getLabelCache(bond, name, ignoreModCounter);
        double doubleValue = 0;
        doubleValue = labelCache.getDoubleValue(bond.getIndex());

        return doubleValue;
    }

    public static int getIntBondProperty(Bond bond, String name)
        throws FeatureException
    {
        return getIntBondProperty(bond, name, false);
    }

    public static int getIntBondProperty(Bond bond, String name,
        boolean ignoreModCounter) throws FeatureException
    {
        checkNotNull(bond);

        BondProperties labelCache = getLabelCache(bond, name, ignoreModCounter);
        int intValue = 0;
        intValue = labelCache.getIntValue(bond.getIndex());

        return intValue;
    }

    public static String getStringBondProperty(Bond bond, String name)
        throws FeatureException
    {
        return getStringBondProperty(bond, name, false);
    }

    public static String getStringBondProperty(Bond bond, String name,
        boolean ignoreModCounter) throws FeatureException
    {
        checkNotNull(bond);

        BondProperties labelCache = getLabelCache(bond, name, ignoreModCounter);
        String stringValue = null;
        stringValue = labelCache.getStringValue(bond.getIndex());

        return stringValue;
    }

    /**
     * @param bond
     * @param name
     */
    private static BondProperties accessBondProperty(Bond bond, String name,
        boolean ignoreModCounter)
    {
        BondProperties labelCache = null;

        if ((bond.getParent().getModificationCounter() == 0) ||
                ignoreModCounter)
        {
            try
            {
                labelCache = (BondProperties) FeatureHelper.instance()
                                                           .featureFrom(bond
                        .getParent(), name);
            }
            catch (FeatureException e1)
            {
                throw new RuntimeException(e1.getMessage());
            }
        }
        else
        {
            throw new RuntimeException(bond.getParent().getTitle() + ": " +
                "Molecule modification counter must be zero.");
        }

        return labelCache;
    }

    /**
     * @param bond
     */
    private static void checkNotNull(Bond bond)
    {
        if (bond == null)
        {
            throw new RuntimeException("Bond should not be null.");
        }

        if (bond.getParent() == null)
        {
            throw new RuntimeException(
                "Molecule (bond parent) should not be null.");
        }
    }

    /**
     * @param bond
     * @param name
     * @param ignoreModCounter
     * @return
     */
    private static BondProperties getLabelCache(Bond bond, String name,
        boolean ignoreModCounter)
    {
        BondProperties labelCache = accessBondProperty(bond, name,
                ignoreModCounter);

        if (!ignoreModCounter)
        {
            if (labelCache != null)
            {
                if (bond.getParent().getBondsSize() != labelCache.getSize())
                {
                    // try to recalculate
                    bond.getParent().deleteData(name);
                    labelCache = accessBondProperty(bond, name,
                            ignoreModCounter);

                    if (labelCache != null)
                    {
                        if (bond.getParent().getBondsSize() !=
                                labelCache.getSize())
                        {
                            throw new RuntimeException(
                                bond.getParent().getTitle() + ": " +
                                "Bond property " + name + " has " +
                                labelCache.getSize() +
                                " labels but should have " +
                                bond.getParent().getBondsSize() + ".");
                        }
                    }
                }
            }
            else
            {
                throw new RuntimeException(bond.getParent().getTitle() + ": " +
                    "Unable to calculate bond property " + name + ".");
            }
        }

        return labelCache;
    }
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
