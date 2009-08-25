///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicRadAngle.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
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
package joelib2.math;

/**
 * A class that provides mathematical definition of geometric angle
 *
 * @.author    Zhidong Xie (zxie@tripos.com)
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:35 $
 */
public class BasicRadAngle implements RadAngle
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * value of the angle
     */
    protected double radAngle = 0.0;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * default constructor: value is 0.0;
     */
    public BasicRadAngle()
    {
    }

    /**
     * constructor
     *
     * @param value  Description of the Parameter
     */
    public BasicRadAngle(double value)
    {
        this(value, false);
    }

    /**
     * copy constructor
     *
     * @param angle  Description of the Parameter
     */
    public BasicRadAngle(BasicRadAngle angle)
    {
        this.radAngle = angle.radAngle;
    }

    /**
     * full constructor
     *
     * @param inDegree  boolean flag indicating value in degree unit
     * @param value     Description of the Parameter
     */
    public BasicRadAngle(double value, boolean inDegree)
    {
        this.setRadAngle(value, inDegree);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Return angle value in degree unit
     *
     * @return   Description of the Return Value
     */
    public double getDegreeAngle()
    {
        return ((180.0 * radAngle) / Math.PI);
    }

    /**
     * Return angle value in rad unit
     *
     * @return   Description of the Return Value
     */
    public double getRadAngle()
    {
        return radAngle;
    }

    /**
     * Set value of the angle
     *
     * @param value  angle's value to be set, in rad unit
     */
    public void setRadAngle(double value)
    {
        this.setRadAngle(value, false);
    }

    /**
     * Set value of the angle
     *
     * @param value     angle's value to be set
     * @param inDegree  true if value is in degree unit, false if in rad unit
     */
    public void setRadAngle(double value, boolean inDegree)
    {
        this.radAngle = (inDegree) ? (value / 180.0 * Math.PI) : value;
    }

    /**
     * Return string representation of angle
     *
     * @return   Description of the Return Value
     */
    public String toString()
    {
        return Float.toString((float) getDegreeAngle());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
