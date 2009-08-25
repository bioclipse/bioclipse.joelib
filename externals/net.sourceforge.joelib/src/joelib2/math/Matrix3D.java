///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Matrix3D.java,v $
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
public interface Matrix3D
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public void setMatrixFrom(double[][] matrix);

    double determinant();

    /**
     *  Description of the Method
     *
     * @param  c  Description of the Parameter
     * @return    Description of the Return Value
     */
    Matrix3D diving(final double c);

    /**
     *  Description of the Method
     *
     * @param  alpha  Description of the Parameter
     * @param  beta   Description of the Parameter
     * @param  gamma  Description of the Parameter
     * @param  a      Description of the Parameter
     * @param  b      Description of the Parameter
     * @param  c      Description of the Parameter
     */
    void fillOrth(double alpha, double beta, double gamma, double a, double b,
        double c);

    /**
     *  Description of the Method
     *
     * @param  i  Description of the Parameter
     * @param  j  Description of the Parameter
     * @return    Description of the Return Value
     */
    double get(int i, int j);

    /**
    *  Gets the array attribute of the Matrix3x3 object
    *
    * @param  m  Description of the Parameter
    */
    void getArray(double[] m);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    Matrix3D invert();

    /**
     *  Description of the Method
     *
     * @param  v  Description of the Parameter
     * @param  m  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D mul(final Vector3D v, final Matrix3D m);

    /**
     *  Description of the Method
     *
     * @param  m  Description of the Parameter
     * @param  v  Description of the Parameter
     * @return    Description of the Return Value
     */
    Vector3D mul(final Matrix3D m, final Vector3D v);

    /**
     *  Description of the Method
     *
     * @param  rnd  Description of the Parameter
     */
    void randomRotation(RandomNumber rnd);

    /**
     *  Description of the Method
     *
     * @param  v      Description of the Parameter
     * @param  angle  Description of the Parameter
     */
    void rotAboutAxisByAngle(final Vector3D v, final double angle);

    /**
     *  Description of the Method
     *
     * @param  c        Description of the Parameter
     * @param  noatoms  Description of the Parameter
     */
    void rotateCoords(double[] c, int noatoms);

    /**
     *  Description of the Method
     *
     * @param  i  Description of the Parameter
     * @param  j  Description of the Parameter
     * @param  v  Description of the Parameter
     */
    void set(int i, int j, double v);

    /**
     *  Description of the Method
     *
     * @param  phi    Description of the Parameter
     * @param  theta  Description of the Parameter
     * @param  psi    Description of the Parameter
     */
    void setupRotMat(double phi, double theta, double psi);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    String toString();

    /**
     *  Description of the Method
     *
     * @param  m  Description of the Parameter
     * @return    Description of the Return Value
     */
    String toString(Matrix3D m);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
