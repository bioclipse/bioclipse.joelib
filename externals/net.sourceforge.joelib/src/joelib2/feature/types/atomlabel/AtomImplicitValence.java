///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomImplicitValence.java,v $
//  Purpose:  Atom mass.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.14 $
//            $Date: 2005/02/17 16:48:31 $
//            $Author: wegner $
//
//  Copyright (c) Dept. Computer Architecture, University of Tuebingen,
//                Germany, 2001-2005
//, 2003-2005
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

import joelib2.data.BasicImplicitValenceTyper;
import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractDynamicAtomProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;

import joelib2.feature.result.DynamicArrayResult;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;
import joelib2.molecule.types.AtomPropertyHelper;

import java.util.zip.DataFormatException;

import org.apache.log4j.Category;


/**
 * Atom type (JOELib internal).
 *
 * This atom property stores the JOELib internal atom type, which can be used via the
 * look-up table in {@link joelib2.data.BasicAtomTypeConversionHolder} to export molecules to other
 * formats, like Synyl MOL2, MM2, Tinker, etc.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.14 $, $Date: 2005/02/17 16:48:31 $
 */
public class AtomImplicitValence extends AbstractDynamicAtomProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.14 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            AtomImplicitValence.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{BasicImplicitValenceTyper.class};
    private static final int[] IMPVAL_HYB_CARBON = new int[]{2, 3, 4};
    private static final int[] IMPVAL_HYB_NITROGEN = new int[]{1, 2, 3};
    private static final int[] IMPVAL_HYB_OXYGEN = new int[]{2, 2, 2};
    private static final int[] IMPVAL_HYB_PHOSPHOR = new int[]{2, 3, 4};
    private static final int[] IMPVAL_HYB_SULFUR = new int[]{0, 2, 2};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomType object
     */
    public AtomImplicitValence()
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

    public static void decrementImplicitValence(Atom atom)
    {
        //System.out.println("Decrement implicit valence for atom "+atom.getIndex());

        if (atom != null)
        {
            Molecule mol = atom.getParent();
            AtomProperties apCache = (AtomProperties) mol.getData(getName());

            if (apCache != null)
            {
                try
                {
                    int value = apCache.getIntValue(atom.getIndex());
                    apCache.setIntValue(atom.getIndex(), value - 1);
                }
                catch (DataFormatException e)
                {
                    logger.error(e.getMessage());
                }
            }
            else
            {
                logger.error(
                    "No automatic implicit valence informations available.");
            }
        }
    }

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static int getImplicitValence(Atom atom)
    {
        int impVal = 0;

        try
        {
            impVal = AtomPropertyHelper.getIntAtomProperty(atom, getName());
        }
        catch (FeatureException e1)
        {
            logger.error(e1.getMessage());
        }

        return impVal;
    }

    /**
     * @return
     */
    public static int getImplicitValence(Atom atom, int hyb)
    {
        int impval = 1;

        switch (atom.getAtomicNumber())
        {
        case 6:
            impval = getImpVal(hyb, impval, IMPVAL_HYB_CARBON);

            break;

        case 7:
            impval = getImpVal(hyb, impval, IMPVAL_HYB_NITROGEN);

            break;

        case 8:
            impval = getImpVal(hyb, impval, IMPVAL_HYB_OXYGEN);

            break;

        case 16:
            impval = getImpVal(hyb, impval, IMPVAL_HYB_SULFUR);

            break;

        case 15:
            impval = getImpVal(hyb, impval, IMPVAL_HYB_PHOSPHOR);

            break;

        default:
            impval = 1;
        }

        return impval;
    }

    public static String getName()
    {
        return AtomImplicitValence.class.getName();
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
     *  Returns <tt>true</tt> if this is a ring atom.
     *
     * @return    <tt>true</tt> if this is a ring atom
     */
    public static void incrementImplicitValence(Atom atom)
    {
        //System.out.println("Increment implicit valence for atom "+atom.getIndex());

        if (atom != null)
        {
            Molecule mol = atom.getParent();
            AtomProperties apCache = (AtomProperties) mol.getData(getName());

            if (apCache != null)
            {
                try
                {
                    int value = apCache.getIntValue(atom.getIndex());
                    apCache.setIntValue(atom.getIndex(), value + 1);
                }
                catch (DataFormatException e)
                {
                    logger.error(e.getMessage());
                }
            }
            else
            {
                logger.error(
                    "No automatic implicit valence informations available.");
            }
        }
    }

    /**
     *  Returns <tt>true</tt> if this is a ring atom.
     *
     * @return    <tt>true</tt> if this is a ring atom
     */
    public static void setImplicitValence(Atom atom, int impVal)
    {
        if (atom != null)
        {
            Molecule mol = atom.getParent();

            if (mol.getModificationCounter() == 0)
            {
                AtomProperties atCache;

                try
                {
                    atCache = (AtomProperties) FeatureHelper.instance()
                                                            .featureFrom(atom
                            .getParent(), getName());
                }
                catch (FeatureException e1)
                {
                    throw new RuntimeException(e1.getMessage());
                }

                if (atCache != null)
                {
                    atCache.setIntValue(atom.getIndex(), impVal);
                }
                else
                {
                    logger.error(
                        "No automatic implicit valence informations available.");
                }
            }
            else
            {
                throw new RuntimeException(
                    "Could not access atom property. Modification counter is not zero.");
            }
        }
    }

    public Object getAtomPropertiesArray(Molecule mol)
    {
        int[] impVal = (int[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.INT, mol.getAtomsSize());
        BasicImplicitValenceTyper.instance().getImplicitValence(mol, impVal);

        return impVal;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }

    private static int getImpVal(int hyb, int preImpVal, int[] ivHyb)
    {
        int impval = preImpVal;

        if ((hyb >= 1) && (hyb <= 3))
        {
            impval = ivHyb[hyb - 1];
        }

        return impval;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
