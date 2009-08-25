///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SingleTieResolverDistance.java,v $
//  Purpose:  Helper class for resolving renumbering ties.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
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

import joelib2.feature.result.IntMatrixResult;

import joelib2.feature.types.DistanceMatrix;

import joelib2.molecule.Molecule;

import org.apache.log4j.Category;


/**
 * Interface for resolving renumbering ties.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:29 $
 */
public class SingleTieResolverDistance implements SingleTieResolver
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.algo.morgan.types.SingleTieResolverDistance");

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean initialized = false;

    private int[] maxDistances;

    //~ Methods ////////////////////////////////////////////////////////////////

    public double getResolvingValue(AtomDoubleParent ap, Molecule mol)
    {
        // initialize atom properties
        if (!initialized)
        {
            return 0.0;
        }

        //System.out.println("dist:"+maxDistances[ap.atomIdx - 1]);
        //System.out.println("distP:"+maxDistances[mol.getAtom(ap.parent).getIdx() - 1]);
        return maxDistances[ap.atomIdx - 1];
    }

    public boolean init(Molecule mol)
    {
        initialized = false;

        // get distance matrix or calculate if not already available
        FeatureResult tmpResult = null;
        String distanceMatrixKey = DistanceMatrix.getName();

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol,
                    distanceMatrixKey);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());
            logger.error("Can not calculate distance matrix.");

            return false;
        }

        if (!(tmpResult instanceof IntMatrixResult))
        {
            logger.error("Needed descriptor '" + distanceMatrixKey +
                "' should be of type " + IntMatrixResult.class.getName() + ".");

            return false;
        }

        IntMatrixResult distResult = (IntMatrixResult) tmpResult;
        int[][] distances = distResult.value;

        maxDistances = new int[mol.getAtomsSize()];

        for (int i = 0; i < distances.length; i++)
        {
            maxDistances[i] = 0;

            for (int ii = 0; ii < i; ii++)
            {
                if (maxDistances[i] < distances[i][ii])
                {
                    maxDistances[i] = distances[i][ii];
                }
            }
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
