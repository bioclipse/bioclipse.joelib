///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomIsAlphaBetaUnsaturated.java,v $
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
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;

import joelib2.feature.result.DynamicArrayResult;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomPropertyHelper;

import org.apache.log4j.Category;


/**
 * Is this atom negatively charged atom.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.13 $, $Date: 2005/02/17 16:48:31 $
 */
public class AtomIsAlphaBetaUnsaturated extends AbstractDynamicAtomProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.13 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            AtomIsAlphaBetaUnsaturated.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomIsPhosphorus.class, AtomIsSulfur.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public AtomIsAlphaBetaUnsaturated()
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
        return AtomIsAlphaBetaUnsaturated.class.getName();
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
     * Returns <tt>true</tt> if this atom is part of an alpha
     * beta unsaturated fragment.
     *
     * <p>
     * Search for pattern of type:<br>
     * $(**=:#*)
     * @return               <tt>true</tt> if this atom is part of an alpha
     *                        beta unsaturated fragment
     */
    public static boolean isValue(Atom atom)
    {
        boolean isTrue = false;

        try
        {
            isTrue = AtomPropertyHelper.getBooleanAtomProperty(atom, getName());
        }
        catch (FeatureException e1)
        {
            logger.error(e1.getMessage());
        }

        return isTrue;
    }

    public Object getAtomPropertiesArray(Molecule mol)
    {
        // get electrotopological state index
        int atomsSize = mol.getAtomsSize();
        boolean[] abUnsaturated = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, atomsSize);

        Atom atom;

        for (int atomIdx = 1; atomIdx <= atomsSize; atomIdx++)
        {
            atom = mol.getAtom(atomIdx);
            abUnsaturated[atomIdx - 1] = AbstractIsAlphaBetaUnsaturated
                .calculate(atom, true);
        }

        return abUnsaturated;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
