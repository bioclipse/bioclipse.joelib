///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Vector3D.java,v $
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

import joelib2.util.RandomNumber;


/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.4 $, $Date: 2005/02/17 16:48:35 $
 */
public interface Vector3D
{
    //  The global constant XYZVectors

    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    final static Vector3D ZERO = new BasicVector3D(0.0f, 0.0f, 0.0f);

    /**
     *  Description of the Field
     */
    final static Vector3D XAXIS = new BasicVector3D(1.0f, 0.0f, 0.0f);

    /**
     *  Description of the Field
     */
    final static Vector3D YAXIS = new BasicVector3D(0.0f, 1.0f, 0.0f);

    /**
     *  Description of the Field
     */
    final static Vector3D ZAXIS = new BasicVector3D(0.0f, 0.0f, 1.0f);

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  v2  Description of the Parameter
     * @return     Description of the Return Value
     */
    Vector3D add(final Vector3D v2);

    /**
     *  Description of the Method
     *
     * @param  v  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D adding(final Vector3D v);

    /**
     *  Description of the Method
     *
     * @param  f  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D adding(final double[] f);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    Object clone();

    void createOrthoXYZVector(Vector3D res);

    Vector3D cross(final Vector3D v2);

    /**
     *  Description of the Method
     *
     * @param  vv  Description of the Parameter
     * @return     Description of the Return Value
     */
    double distSq(final Vector3D vv);

    /**
     *  Description of the Method
     *
     * @param  c  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D div(final int c);

    /**
     *  Description of the Method
     *
     * @param  c  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D diving(final double c);

    boolean equals(Object obj);

    /**
     *  Description of the Method
     *
     * @param  to  Description of the Parameter
     * @return     Description of the Return Value
     */
    Vector3D get(Vector3D to);

    /**
     *  Description of the Method
     *
     * @param  c  Description of the Parameter
     */
    void get(double[] c);

    /**
     *  Description of the Method
     *
     * @param  c     Description of the Parameter
     * @param  cidx  Description of the Parameter
     */
    void get(double[] c, int cidx);

    double getX3D();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    double getY3D();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    double getZ3D();

    int hashCode();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    double length();

    double length_2();

    /**
     *  Description of the Method
     *
     * @param  c  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D mul(final int c);

    /**
     *  Description of the Method
     *
     * @param  v2  Description of the Parameter
     * @return     Description of the Return Value
     */
    Vector3D mul(final Vector3D v2);

    /**
     *  Description of the Method
     *
     * @param  c  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D muling(final double c);

    /**
     *  Description of the Method
     *
     * @param  m  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D muling(final Matrix3D m);

    Vector3D normalize();

    /**
     *  Description of the Method
     *
     * @param  v2  Description of the Parameter
     * @return     Description of the Return Value
     */
    boolean notEquals(final Vector3D v2);

    /**
     *  Description of the Method
     */
    void randomUnitXYZVector();

    void randomUnitXYZVector(RandomNumber oeRandP);

    Vector3D set(final Vector3D v);

    /**
     *  Description of the Method
     *
     * @param  c  Description of the Parameter
     */
    void set(final double[] c);

    /**
     *  Description of the Method
     *
     * @param  c     Description of the Parameter
     * @param  cidx  Description of the Parameter
     */
    void set(final double[] c, int cidx);

    /**
     *  Description of the Method
     *
     * @param  x  Description of the Parameter
     * @param  y  Description of the Parameter
     * @param  z  Description of the Parameter
     */
    void set(final double x, final double y, final double z);

    /**
     *  Copy this vector to the vector <tt>v</tt>.
     *
     * @param  v  The new to value
     * @return    Description of the Return Value
     */
    Vector3D setTo(Vector3D v);

    /**
     *  Sets the x attribute of the XYZVector object
     *
     * @param  x  The new x value
     */
    void setX3D(final double x);

    /**
     *  Sets the y attribute of the XYZVector object
     *
     * @param  y  The new y value
     */
    void setY3D(final double y);

    /**
     *  Sets the z attribute of the XYZVector object
     *
     * @param  z  The new z value
     */
    void setZ3D(final double z);

    /**
     *  Description of the Method
     *
     * @param  v2  Description of the Parameter
     * @return     Description of the Return Value
     */
    Vector3D sub(final Vector3D v2);

    /**
     *  Description of the Method
     *
     * @param  c  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D sub(final int c);

    /**
     *  Description of the Method
     *
     * @param  v  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D subing(final Vector3D v);

    /**
     *  Description of the Method
     *
     * @param  f  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D subing(final double[] f);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    String toString();

    /**
     *  Description of the Method
     *
     * @param  v  Description of the Parameter
     * @return    Description of the Return Value
     */
    String toString(Vector3D v);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
