///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: FeatureInfo.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 15, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.8 $
//          $Date: 2005/02/17 16:48:30 $
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
package joelib2.feature;

/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.8 $, $Date: 2005/02/17 16:48:30 $
 */
public interface FeatureInfo
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets descriptor name.
     *
     * @return the decsriptor name
     */
    String getName();

    /**
     * Gets the descriptor result class representation name.
     *
     * @return the descriptor result class representation name
     */
    String getResult();

    /**
     * Gets descriptor type, e.g. no_coordinates, topological, geometrical, energegetic.
     *
     * @return the descriptor type
     * @see #TYPE_UNKNOWN
     * @see #TYPE_NO_COORDINATES
     * @see #TYPE_TOPOLOGICAL
     * @see #TYPE_GEOMETRICAL
     * @see #TYPE_ENERGETIC
     */
    String getType();

    /**
     * Gets descriptor type dimensions.
     *
     * @return the descriptor type dimensions
     * @see #REQUIRED_DIMENSION_UNKNOWN
     * @see #REQUIRED_DIMENSION_NO_COORDINATES
     * @see #REQUIRED_DIMENSION_TOPOLOGICAL
     * @see #REQUIRED_DIMENSION_GEOMETRICAL
     * @see #REQUIRED_DIMENSION_ENERGETIC
     */
    int getTypeDimension();

    /**
     * Gets the descriptor informations.
     *
     * @return the descriptor informations
     */
    String toString();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
