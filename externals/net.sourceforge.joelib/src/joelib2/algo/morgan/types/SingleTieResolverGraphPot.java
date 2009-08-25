///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SingleTieResolverGraphPot.java,v $
//  Purpose:  Helper class for resolving renumbering ties.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.algo.morgan.types;

import joelib2.algo.morgan.AtomDoubleParent;
import joelib2.algo.morgan.SingleTieResolver;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.types.GraphPotentials;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import org.apache.log4j.Category;


/**
 * Interface for resolving renumbering ties.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:29 $
 */
public class SingleTieResolverGraphPot implements SingleTieResolver
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.algo.morgan.types.SingleTieResolverGraphPot");

    //~ Instance fields ////////////////////////////////////////////////////////

    private AtomProperties atomProperties;
    private boolean initialized = false;

    //~ Methods ////////////////////////////////////////////////////////////////

    public double getResolvingValue(AtomDoubleParent ap, Molecule mol)
    {
        // initialize atom properties
        if (!initialized)
        {
            return 0.0;
        }

        //System.out.println("gp:"+atomProperties.getDoubleValue(ap.atomIdx));
        //System.out.println("gpP:"+atomProperties.getDoubleValue(mol.getAtom(ap.parent).getIdx()));
        return atomProperties.getDoubleValue(ap.atomIdx);
    }

    public boolean init(Molecule mol)
    {
        initialized = false;

        String propertyName = GraphPotentials.getName();

        // get atom properties or calculate if not already available
        FeatureResult tmpPropResult = null;

        try
        {
            tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                    propertyName);
        }
        catch (FeatureException e)
        {
            logger.error("Atom property " + propertyName + " does not exist.");

            return false;
        }

        if (tmpPropResult instanceof AtomProperties)
        {
            atomProperties = (AtomProperties) tmpPropResult;
        }
        else
        {
            logger.error("Property '" + propertyName +
                "' must be an atom type.");

            return false;
        }

        initialized = true;

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
