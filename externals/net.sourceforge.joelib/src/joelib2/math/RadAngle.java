///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: RadAngle.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.4 $
//          $Date: 2005/02/17 16:48:35 $
//          $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                     Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                     2003,2004,2005
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
package joelib2.math;

/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.4 $, $Date: 2005/02/17 16:48:35 $
 */
public interface RadAngle
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    final static double RAD_TO_DEG = 180.0f / Math.PI;

    /**
     *  Description of the Field
     */
    final static double DEG_TO_RAD = Math.PI / 180.0f;

    //~ Methods ////////////////////////////////////////////////////////////////

    double getDegreeAngle();

    /**
     * Return angle value in rad unit
     *
     * @return   Description of the Return Value
     */
    double getRadAngle();

    /**
     * Set value of the angle
     *
     * @param value  angle's value to be set, in rad unit
     */
    void setRadAngle(double value);

    /**
     * Set value of the angle
     *
     * @param value     angle's value to be set
     * @param inDegree  true if value is in degree unit, false if in rad unit
     */
    void setRadAngle(double value, boolean inDegree);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
