///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Line.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:33 $
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
package joelib2.gui.render3D.math.geometry;

/**
 * A class that defines geometric straight line in 3D space
 *
 * @.author    Zhidong Xie (zxie@tripos.com)
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class Line
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * a point on the line
     */
    Point3D pointA;

    /**
     * the other point on the line
     */
    Point3D pointB;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Default constructor
     */
    public Line()
    {
        pointA = new Point3D(0.0, 0.0, 0.0);
        pointB = new Point3D(0.0, 0.0, 0.0);
    }

    /**
     * Full constructor
     *
     * @param pointA  a point on the line
     * @param pointB  the other point on the line
     */
    public Line(Point3D pointA, Point3D pointB)
    {
        this.pointA = new Point3D(pointA);
        this.pointB = new Point3D(pointB);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Return the distance from a given point to the line
     *
     * @param pointX  Description of the Parameter
     * @return        Description of the Return Value
     */
    public double distanceTo(Point3D pointX)
    {
        return (vectorTo(pointX)).length();
    }

    /**
     * Return the geometric vector from some point on the line to the given
     * point so that the vector is perpendicular to the line. Assume the given
     * point is not on the line
     *
     * @param pointX  the give point
     * @return        Description of the Return Value
     */
    public GeoVector3D vectorTo(Point3D pointX)
    {
        GeoVector3D ap = new GeoVector3D(pointA, pointX);
        GeoVector3D ab = new GeoVector3D(pointA, pointB);
        GeoVector3D ap1 = Geometry.project(ap, ab);
        ap.minus(ap1);

        return ap;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
