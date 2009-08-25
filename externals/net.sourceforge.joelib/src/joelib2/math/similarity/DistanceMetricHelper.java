///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DistanceMetricHelper.java,v $
//  Purpose:  Interface to have a fast method to native descriptor values.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.math.similarity;

import joelib2.feature.FeatureException;


/**
 * Helper class to load distance metric representation classes using reflection.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 * @see joelib2.feature.DistanceMetricValue
 * @see joelib2.math.similarity.DistanceMetric
 */
public class DistanceMetricHelper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static DistanceMetric getDistanceMetric(String represention)
        throws FeatureException
    {
        // try to load distance metric representation class
        DistanceMetric dMetric = null;

        try
        {
            // works only for construtor without arguments
            dMetric = (DistanceMetric) Class.forName(represention)
                                            .newInstance();

            // for metrics with arguments
            //      Class        cls          = Class.forName(resultRepr);
            //      Constructor  constructor[]  = cls.getDeclaredConstructors();
            //      for (int i = 0; i < constructor.length; i++)
            //      {
            //        Class[]  params  = constructor[i].getParameterTypes();
            //        if (params.length == 1)
            //        {
            //          Object[]  inputs  = {descInfo};
            //          descResult = (DescResult) constructor[i].newInstance(inputs);
            //        }
            //      }
        }
        catch (ClassNotFoundException ex)
        {
            throw new FeatureException(represention + " not found.");
        }
        catch (InstantiationException ex)
        {
            throw new FeatureException(represention +
                " can not be instantiated.");
        }
        catch (IllegalAccessException ex)
        {
            throw new FeatureException(represention + " can't be accessed.");
        }

        //        catch (InvocationTargetException ex)
        //        {
        //            ex.printStackTrace();
        //            throw new DescriptorException("InvocationTargetException.");
        //        }
        if (dMetric == null)
        {
            throw new FeatureException("Metric class " + represention +
                " does'nt exist.");
        }
        else
        {
            return dMetric;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
