///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicAPDistanceMetric.java,v $
//  Purpose:  Atom pair descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
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
package joelib2.feature.types.atompair;

import joelib2.math.similarity.DistanceMetric;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Category;


/**
 * Distance metric based on the atom pair descriptor (depends on atom properties used).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:32 $
 */
public class BasicAPDistanceMetric implements DistanceMetric
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.feature.types.atompair.BasicAPDistanceMetric");

    //~ Methods ////////////////////////////////////////////////////////////////

    public double getDistance(Object source, Object target)
    {
        /*if ((source instanceof AtomPairResult) == false)
        {
                logger.error("source must be of type AtomPairResult");
                return Double.NaN;
        }

        if ((target instanceof AtomPairResult) == false)
        {
                logger.error("target must be of type AtomPairResult");
                return Double.NaN;
        }*/
        if (!((source instanceof AtomPairResult) &&
                    (target instanceof AtomPairResult)))
        {
            //            if (!(source instanceof SmoothedAtomPairResult &&
            //                    target instanceof SmoothedAtomPairResult))
            //            {
            //                logger.error(
            //                    "Source and Target have to be both either AtomPairResult or SmoothedAtomPairResult");
            //
            //                return Double.NaN;
            //            }
            logger.error(
                "Source and Target have to be both either AtomPairResult");

            return Double.NaN;
        }

        AtomPairResult sAP = (AtomPairResult) source;
        AtomPairResult tAP = (AtomPairResult) target;
        Hashtable tCloned = (Hashtable) tAP.atomPairs.clone();

        int[] ia;
        int[] ia2;
        int fs = 0;
        int ft = 0;
        int fAllMin = 0;
        AtomPair key;

        // get all descriptors of the source
        // build minimum sum based on all descriptors contained in
        // the source AND the target
        for (Enumeration e = sAP.atomPairs.keys(); e.hasMoreElements();)
        {
            key = (AtomPair) e.nextElement();
            ia = (int[]) sAP.atomPairs.get(key);
            fs += ia[0];

            if (tAP.atomPairs.containsKey(key))
            {
                ia2 = (int[]) tAP.atomPairs.get(key);

                if (ia[0] < ia2[0])
                {
                    fAllMin += ia[0];
                }
                else
                {
                    fAllMin += ia2[0];
                }

                tCloned.remove(key);
            }

            //else
            //{
            //    // 0, because minimum of source and target !!!
            //    //fAllMin += 0;
            //}
        }

        // get all descriptors of the target
        for (Enumeration e = tAP.atomPairs.keys(); e.hasMoreElements();)
        {
            key = (AtomPair) e.nextElement();
            ft += ((int[]) tAP.atomPairs.get(key))[0];
        }

        // build minimum sum based on all descriptors contained in
        // the target
        //              AtomPair element;
        //              for (Enumeration e = tCloned.elements(); e.hasMoreElements();)
        //              {
        //                      element = (AtomPair) e.nextElement();
        //
        //                      ia = (int[]) sAP.atomPairs.get(element);
        //                      // 0, because minimum of source and target !!!
        //                      fAllMin += 0;
        //              }
        double similarity = (double) fAllMin / (0.5 * ((double) (ft + fs)));

        return similarity;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
