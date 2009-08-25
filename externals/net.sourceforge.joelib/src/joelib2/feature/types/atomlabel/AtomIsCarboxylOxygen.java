///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomIsCarboxylOxygen.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.13 $
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
package joelib2.feature.types.atomlabel;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractDynamicAtomProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureHelper;

import joelib2.feature.result.DynamicArrayResult;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.iterator.BondIterator;

import java.util.zip.DataFormatException;

import org.apache.log4j.Category;


/**
 * Is this atom negatively charged atom.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.13 $, $Date: 2005/02/17 16:48:31 $
 */
public class AtomIsCarboxylOxygen extends AbstractDynamicAtomProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.13 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            AtomIsCarboxylOxygen.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AtomIsOxygen.class, AtomHeavyValence.class,
            AtomFreeOxygenCount.class
        };

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public AtomIsCarboxylOxygen()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                joelib2.feature.result.AtomDynamicResult.class.getName());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return AtomIsCarboxylOxygen.class.getName();
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
     *  Returns <tt>true</tt> if carbon is part of an carboxyl group.
     *
     * Atom is connected to a carbon that has a total of 2 attached free oxygens
     *
     * @return  <tt>true</tt> if carbon is part of an carboxyl group
     */
    public static boolean isCarboxylOxygen(Atom thisAtom)
    {
        boolean isCO = false;

        // used cached version, if available
        if (thisAtom.getParent().hasData(getName()))
        {
            AtomProperties labelCache = (AtomProperties) thisAtom.getParent()
                                                                 .getData(
                    getName());

            if (labelCache != null)
            {
                try
                {
                    if (labelCache.getIntValue(thisAtom.getIndex()) != 0)
                    {
                        isCO = true;
                    }
                }
                catch (DataFormatException e)
                {
                    logger.error(e.getMessage());
                }
            }
        }
        else
        {
            if (AtomIsOxygen.isOxygen(thisAtom) &&
                    (AtomHeavyValence.valence(thisAtom) == 1))
            {
                BondIterator bit = thisAtom.bondIterator();
                Bond bond;
                Atom atom = null;

                while (bit.hasNext())
                {
                    bond = bit.nextBond();

                    if (AtomIsCarbon.isCarbon((bond.getNeighbor(thisAtom))))
                    {
                        atom = bond.getNeighbor(thisAtom);

                        break;
                    }
                }

                if ((atom != null) &&
                        (AtomFreeOxygenCount.getIntValue(atom) == 2))
                {
                    isCO = true;
                }
            }
        }

        return isCO;
    }

    public Object getAtomPropertiesArray(Molecule mol)
    {
        int atomsSize = mol.getAtomsSize();
        boolean[] carbox = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, atomsSize);

        Atom atom;

        for (int atomIdx = 1; atomIdx <= atomsSize; atomIdx++)
        {
            atom = mol.getAtom(atomIdx);
            carbox[atomIdx - 1] = isCarboxylOxygen(atom);
        }

        return carbox;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
