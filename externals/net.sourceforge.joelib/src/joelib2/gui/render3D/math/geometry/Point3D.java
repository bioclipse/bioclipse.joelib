///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Point3D.java,v $
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

import joelib2.math.BasicRadAngle;


/**
 * A class that provides mathematical definition of and utility for 3D geometric points
 *
 * @.author    Zhidong Xie (zxie@tripos.com)
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class Point3D
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * X coordinates in 3D space
     */
    protected double x = 0.0;

    /**
     * Y coordinates in 3D space
     */
    protected double y = 0.0;

    /**
     * Z coordinates in 3D space
     */
    protected double z = 0.0;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * default constructor: coordinates are all 0.0;
     */
    public Point3D()
    {
    }

    /**
     * copy constructor
     *
     * @param p  the point to be copied
     */
    public Point3D(Point3D p)
    {
        x = p.x;
        y = p.y;
        z = p.z;
    }

    /**
     * full constructor
     *
     * @param x   Description of the Parameter
     * @param y   Description of the Parameter
     * @param z   Description of the Parameter
     */
    public Point3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * calculate angle among three points in 3D space
     * note: calling distSquare() is more efficient than distance().
     *
     * @param P1   Description of the Parameter
     * @param P2   Description of the Parameter
     * @return     angle : angle P1-this point-P2
     */
    public BasicRadAngle angleWith(Point3D P1, Point3D P2)
    {
        // notation: O-this point, 2-square, P1,P2-points:
        double P1O_2 = distSquareTo(P1);
        double P2O_2 = distSquareTo(P2);
        double P1P2_2 = P1.distSquareTo(P2);
        double result = Math.acos(((P1O_2 + P2O_2) - P1P2_2) / 2.0 /
                Math.sqrt(P1O_2 * P2O_2));

        return new BasicRadAngle(result, false);
    }

    /**
     * calculate distance from this point to the other
     *
     * @param P   Description of the Parameter
     * @return    Description of the Return Value
     */
    public double distanceTo(Point3D P)
    {
        return Math.sqrt(((x - P.x) * (x - P.x)) + ((y - P.y) * (y - P.y)) +
                ((z - P.z) * (z - P.z)));
    }

    /**
     * calculate the squre of distance from this point to the other
     *
     * @param P   Description of the Parameter
     * @return    distance  : |P1P2|^2
     */
    public double distSquareTo(Point3D P)
    {
        return ((x - P.x) * (x - P.x)) + ((y - P.y) * (y - P.y)) +
            ((z - P.z) * (z - P.z));
    }

    /**
     * Return X coordinate
     *
     * @return   The x value
     */
    public double getX()
    {
        return x;
    }

    /**
     * Return Y coordinate
     *
     * @return   The y value
     */
    public double getY()
    {
        return y;
    }

    /**
     * Return Z coordinate
     *
     * @return   The z value
     */
    public double getZ()
    {
        return z;
    }

    /**
     * Set X coordinate
     *
     * @param x  The new x value
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * Set Y coordinate
     *
     * @param y  The new y value
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * Set Z coordinate
     *
     * @param z  The new z value
     */
    public void setZ(double z)
    {
        this.z = z;
    }

    /**
     * move(translate) this point along with the input geometric vector
     *
     * @param gv  geometric vector
     */
    public void translate(GeoVector3D gv)
    {
        x += gv.getX();
        y += gv.getY();
        z += gv.getZ();
    }

    /**
     * move(translate) this point by the input quantity along the 3 axises
     *
     * @param dx  translation along x axis
     * @param dy  translation along y axis
     * @param dz  translation along z axis
     */
    public void translate(double dx, double dy, double dz)
    {
        x += dx;
        y += dy;
        z += dz;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
