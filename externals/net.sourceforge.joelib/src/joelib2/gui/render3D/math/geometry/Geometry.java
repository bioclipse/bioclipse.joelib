///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Geometry.java,v $
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

import org.apache.log4j.Category;


/**
 * A public class that provides mathematical calculation on
 *   some geometry entities.
 *
 * @.author    Zhidong Xie (zxie@tripos.com)
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class Geometry
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(Geometry.class
            .getName());

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * calculate angle among three points in 3D space
     * note: calling Point3D.distSquare() is more efficient than Point3D.distance().
     *
     * @param P1   Description of the Parameter
     * @param O    Description of the Parameter
     * @param P2   Description of the Parameter
     * @return     angle : angle P1-O-P2
     */
    public static BasicRadAngle angle(Point3D P1, Point3D O, Point3D P2)
    {
        return O.angleWith(P1, P2);
    }

    /**
     * calculate distance between two points in 3D space
     *
     * @param P1  :  point 1
     * @param P2  :  point 2
     * @return    distance  : |P1P2|
     */
    public static double distance(Point3D P1, Point3D P2)
    {
        return P1.distanceTo(P2);
    }

    /**
     * return the projection of va on vb
     *
     * @param va  Description of the Parameter
     * @param vb  Description of the Parameter
     * @return    Description of the Return Value
     */
    public static GeoVector3D project(GeoVector3D va, GeoVector3D vb)
    {
        GeoVector3D v2 = new GeoVector3D(vb);
        v2.normalize();
        v2.scale(va.dot(v2));

        return v2;
    }

    /**
     * Return a geometric vector that is the result of subtracting second input
     * geometric vector from the first one.
     *
     * @param va  first geometric vector
     * @param vb  second geometric vector
     * @return    va-vb
     */
    public static GeoVector3D subtract(GeoVector3D va, GeoVector3D vb)
    {
        GeoVector3D result = new GeoVector3D(va);
        result.minus(vb);

        return result;
    }

    /**
     * Return a geometric vector that is the result of summation of two
     * input geometric vectors
     *
     * @param va  first geometric vector
     * @param vb  second geometric vector
     * @return    va+vb
     */
    public static GeoVector3D sum(GeoVector3D va, GeoVector3D vb)
    {
        GeoVector3D result = new GeoVector3D(va);
        result.add(vb);

        return result;
    }

    /**
     * calculate angle among three points in 3D space
     * note: calling Point3D.distSquare() is more efficient than
     *          calling Point3D.distance().
     *
     * @param P1   Description of the Parameter
     * @param P2   Description of the Parameter
     * @param P3   Description of the Parameter
     * @param P4   Description of the Parameter
     * @return     angle : angle P1-O-P2
     */
    public static BasicRadAngle torsionAngle(Point3D P1, Point3D P2, Point3D P3,
        Point3D P4)
    {
        BasicRadAngle result = new BasicRadAngle(0.0);

        // use same local variable names as in $TA_TOOLS/utl/source/utl_geom.c
        GeoVector3D tv1 = new GeoVector3D(P1, P2);
        GeoVector3D tv2 = new GeoVector3D(P2, P3);
        GeoVector3D tv3 = new GeoVector3D(P3, P4);

        // error checking: distance is too small?
        if (tv1.length() <= 0.001)
        {
            logger.error("Error: distance between point 1 and point" +
                "2 is too small to ensure accurate torsion angle calculation");

            return result;
        }

        if (tv2.length() <= 0.001)
        {
            logger.error("Error: distance between point 2 and point" +
                "3 is too small to ensure accurate torsion angle calculation");

            return result;
        }

        if (tv3.length() <= 0.001)
        {
            logger.error("Error: distance between point 3 and point" +
                "4 is too small to ensure accurate torsion angle calculation");

            return result;
        }

        // error checking: co-linear?
        double angleP1P2P3 = P2.angleWith(P1, P3).getDegreeAngle();

        if (((174.9 <= angleP1P2P3) && (angleP1P2P3 <= 180.1)) ||
                ((-180.1 <= angleP1P2P3) && (angleP1P2P3 <= -174.9)))
        {
            logger.error("Error: point 1, 2, and 3 are co-linear, hence " +
                "it is meaningless to calculate torsion angle");

            return result;
        }

        double angleP2P3P4 = P3.angleWith(P2, P4).getDegreeAngle();

        if (((174.9 <= angleP2P3P4) && (angleP2P3P4 <= 180.1)) ||
                ((-180.1 <= angleP2P3P4) && (angleP2P3P4 <= -174.9)))
        {
            logger.error("Error: point 2, 3, and 4 are co-linear, hence " +
                "it is meaningless to calculate torsion angle");

            return result;
        }

        GeoVector3D tva = tv1.cross(tv2);
        GeoVector3D tvb = tv2.cross(tv3);

        tva.normalize();
        tvb.normalize();

        // tva "dot" tvb gives cosine torsionAngle
        double ct = tva.dot(tvb);

        // tva "cross" tvb gives a vector V which is parallel or antiparallel
        // to tv2, and V = sin( torsionAngle ) r, where r is the unit vector
        // of tv2
        GeoVector3D ttvc = tva.cross(tvb);

        // if V is parallel to tv2, sin( torsionAngle) is >= 0 else < 0.
        // In Java, the following value relationship holds:
        //    if sin( anyAngle ) < 0, anyAngle == - acos( cos( anyAngle ) )
        //    else anyAngle == acos( cos( anyAngle ) ).
        if (ttvc.dot(tv2) < 0)
        {
            // antiparallel
            result.setRadAngle(-Math.acos(ct));
        }
        else
        {
            // parallel
            result.setRadAngle(Math.acos(ct));
        }

        return result;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
