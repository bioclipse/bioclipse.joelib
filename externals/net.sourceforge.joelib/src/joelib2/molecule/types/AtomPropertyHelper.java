///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AtomPropertyHelper.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 25, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.6 $
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

import joelib2.molecule.Atom;

import java.util.zip.DataFormatException;


/**
 * TODO description.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.6 $, $Date: 2005/02/17 16:48:37 $
 */
public class AtomPropertyHelper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static boolean getBooleanAtomProperty(Atom atom, String name)
        throws FeatureException
    {
        return getBooleanAtomProperty(atom, name, false);
    }

    public static boolean getBooleanAtomProperty(Atom atom, String name,
        boolean ignoreModCounter) throws FeatureException
    {
        checkNotNull(atom);

        AtomProperties labelCache = getLabelCache(atom, name, ignoreModCounter);
        boolean boolValue = false;

        try
        {
            if (labelCache.getIntValue(atom.getIndex()) != 0)
            {
                boolValue = true;
            }
        }
        catch (DataFormatException e)
        {
            throw new FeatureException(e.getMessage());
        }

        return boolValue;
    }

    public static double getDoubleAtomProperty(Atom atom, String name)
        throws FeatureException
    {
        return getDoubleAtomProperty(atom, name, false);
    }

    public static double getDoubleAtomProperty(Atom atom, String name,
        boolean ignoreModCounter) throws FeatureException
    {
        checkNotNull(atom);

        AtomProperties labelCache = getLabelCache(atom, name, ignoreModCounter);
        double doubleValue = 0;
        doubleValue = labelCache.getDoubleValue(atom.getIndex());

        return doubleValue;
    }

    public static int getIntAtomProperty(Atom atom, String name)
        throws FeatureException
    {
        return getIntAtomProperty(atom, name, false);
    }

    public static int getIntAtomProperty(Atom atom, String name,
        boolean ignoreModCounter) throws FeatureException
    {
        checkNotNull(atom);

        AtomProperties labelCache = getLabelCache(atom, name, ignoreModCounter);
        int intValue = 0;

        try
        {
            intValue = labelCache.getIntValue(atom.getIndex());
        }
        catch (DataFormatException e)
        {
            throw new FeatureException(e.getMessage());
        }

        return intValue;
    }

    public static String getStringAtomProperty(Atom atom, String name)
        throws FeatureException
    {
        return getStringAtomProperty(atom, name, false);
    }

    public static String getStringAtomProperty(Atom atom, String name,
        boolean ignoreModCounter) throws FeatureException
    {
        checkNotNull(atom);

        AtomProperties labelCache = getLabelCache(atom, name, ignoreModCounter);
        String stringValue = null;
        stringValue = labelCache.getStringValue(atom.getIndex());

        return stringValue;
    }

    public static int setIntAtomProperty(Atom atom, String name, int value)
        throws FeatureException
    {
        return setIntAtomProperty(atom, name, false, value);
    }

    public static int setIntAtomProperty(Atom atom, String name,
        boolean ignoreModCounter, int value) throws FeatureException
    {
        checkNotNull(atom);

        AtomProperties labelCache = getLabelCache(atom, name, ignoreModCounter);
        int intValue = 0;

        if (labelCache != null)
        {
            labelCache.setIntValue(atom.getIndex(), value);
        }
        else
        {
            throw new FeatureException("No atom label cache for " + name +
                " available to set atom value " + atom.getIndex());
        }

        return intValue;
    }

    /**
     * @param atom
     * @param name
     */
    private static AtomProperties accessAtomProperty(Atom atom, String name,
        boolean ignoreModCounter)
    {
        AtomProperties labelCache = null;

        if ((atom.getParent().getModificationCounter() == 0) ||
                ignoreModCounter)
        {
            try
            {
                labelCache = (AtomProperties) FeatureHelper.instance()
                                                           .featureFrom(atom
                        .getParent(), name);
            }
            catch (FeatureException e1)
            {
                throw new RuntimeException(e1.getMessage());
            }
        }
        else
        {
            throw new RuntimeException(atom.getParent().getTitle() + ": " +
                "Molecule modification counter must be zero.");
        }

        return labelCache;
    }

    /**
     * @param atom
     */
    private static void checkNotNull(Atom atom)
    {
        if (atom == null)
        {
            throw new RuntimeException("Atom should not be null.");
        }

        if (atom.getParent() == null)
        {
            throw new RuntimeException(
                "Molecule (atom parent) should not be null.");
        }
    }

    /**
     * @param atom
     * @param name
     * @param ignoreModCounter
     * @return
     */
    private static AtomProperties getLabelCache(Atom atom, String name,
        boolean ignoreModCounter)
    {
        AtomProperties labelCache = accessAtomProperty(atom, name,
                ignoreModCounter);

        if (!ignoreModCounter)
        {
            if (labelCache != null)
            {
                if (atom.getParent().getAtomsSize() != labelCache.getSize())
                {
                    // try to recalculate
                    atom.getParent().deleteData(name);
                    labelCache = accessAtomProperty(atom, name,
                            ignoreModCounter);

                    if (labelCache != null)
                    {
                        if (atom.getParent().getAtomsSize() !=
                                labelCache.getSize())
                        {
                            throw new RuntimeException(
                                atom.getParent().getTitle() + ": " +
                                "Atom property " + name + " has " +
                                labelCache.getSize() +
                                " labels but should have " +
                                atom.getParent().getAtomsSize() + ".");
                        }
                    }
                }
            }
            else
            {
                throw new RuntimeException(atom.getParent().getTitle() + ": " +
                    "Unable to calculate atom property " + name + ".");
            }
        }

        return labelCache;
    }
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
