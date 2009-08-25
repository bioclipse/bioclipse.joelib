///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DistanceMetricValue.java,v $
//  Purpose:  Interface to have a fast method to native descriptor values.
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
package joelib2.feature;

/**
 * Interface to have a fast method to getting similarity metric values.
 * The <tt>target</tt> object must be checked. All used metrics must
 * implement the {@link joelib2.math.similarity.DistanceMetric} class.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:29 $
 * @see joelib2.math.similarity.DistanceMetric
 * @see joelib2.math.similarity.DistanceMetricHelper
 */
public interface DistanceMetricValue
{
    //~ Methods ////////////////////////////////////////////////////////////////

    double getDistance(Object target);

    void loadDefaultMetric() throws FeatureException;

    void loadMetric(String represenation) throws FeatureException;
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
