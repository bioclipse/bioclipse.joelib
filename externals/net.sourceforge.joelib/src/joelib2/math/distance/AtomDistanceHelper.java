///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomDistanceHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:35 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.math.distance;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.result.DoubleMatrixResult;
import joelib2.feature.result.IntMatrixResult;

import joelib2.feature.types.DistanceMatrix;
import joelib2.feature.types.GeomDistanceMatrix;

import joelib2.molecule.Molecule;

import org.apache.log4j.Category;


/**
 * Atom-atom distance helper methods.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 */
public class AtomDistanceHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.math.distance.AtomDistanceHelper");
    public static final String TOPOLOGICAL_DISTANCE = DistanceMatrix.getName();
    public static final String GEOMETRICAL_DISTANCE = GeomDistanceMatrix
        .getName();

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
         * @param mol1
         * @return
         */
    public static double[][] getDistanceMatrix(Molecule mol,
        String distanceMatrixKey)
    {
        FeatureResult tmpResult = null;

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol,
                    distanceMatrixKey);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());

            return null;
        }

        double[][] dist;

        if (tmpResult instanceof IntMatrixResult)
        {
            IntMatrixResult distResult = (IntMatrixResult) tmpResult;
            int[][] distances = distResult.value;
            dist = new double[distances.length][distances[0].length];

            for (int i = 0; i < dist.length; i++)
            {
                for (int j = i + 1; j < dist[0].length; j++)
                {
                    if (distances[i][j] == -1)
                    {
                        dist[j][i] = dist[i][j] = Double.MAX_VALUE;
                    }
                    else
                    {
                        dist[j][i] = dist[i][j] = (double) distances[i][j];
                    }
                }
            }
        }
        else if (tmpResult instanceof DoubleMatrixResult)
        {
            dist = ((DoubleMatrixResult) tmpResult).value;
        }
        else
        {
            logger.error("Needed distance '" + distanceMatrixKey +
                "' should be of type " + IntMatrixResult.class.getName() +
                " or " + DoubleMatrixResult.class.getName());

            return null;
        }

        return dist;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
