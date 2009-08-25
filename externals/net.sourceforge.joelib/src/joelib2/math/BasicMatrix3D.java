///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicMatrix3D.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.4 $
//            $Date: 2005/02/17 16:48:35 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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

import joelib2.util.RandomNumber;


/**
 * 3x3 Matrix.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.4 $, $Date: 2005/02/17 16:48:35 $
 */
public class BasicMatrix3D implements java.io.Serializable, Matrix3D
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public double[][] matrix = new double[3][3];

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Matrix3x3 object
     */
    public BasicMatrix3D()
    {
    }

    /**
     * @param  matrix  3x3 of type double
     */
    public BasicMatrix3D(double[][] matrix)
    {
        setMatrixFrom(matrix);
    }

    /**
     *  Constructor for the Matrix3x3 object
     *
     * @param  row1  Description of the Parameter
     * @param  row2  Description of the Parameter
     * @param  row3  Description of the Parameter
     */
    public BasicMatrix3D(Vector3D row1, Vector3D row2, Vector3D row3)
    {
        matrix[0][0] = row1.getX3D();
        matrix[0][1] = row1.getY3D();
        matrix[0][2] = row1.getZ3D();
        matrix[1][0] = row2.getX3D();
        matrix[1][1] = row2.getY3D();
        matrix[1][2] = row2.getZ3D();
        matrix[2][0] = row3.getX3D();
        matrix[2][1] = row3.getY3D();
        matrix[2][2] = row3.getZ3D();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public double determinant()
    {
        double x;
        double y;
        double z;

        x = matrix[0][0] *
            ((matrix[1][1] * matrix[2][2]) - (matrix[1][2] * matrix[2][1]));
        y = matrix[0][1] *
            ((matrix[1][2] * matrix[2][0]) - (matrix[1][0] * matrix[2][2]));
        z = matrix[0][2] *
            ((matrix[1][0] * matrix[2][1]) - (matrix[1][1] * matrix[2][0]));

        return (x + y + z);
    }

    /**
     *  Description of the Method
     *
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Matrix3D diving(final double value)
    {
        int row;
        int column;

        for (row = 0; row < 3; row++)
        {
            for (column = 0; column < 3; column++)
            {
                matrix[row][column] /= value;
            }
        }

        return this;
    }

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
    public void fillOrth(double alpha, double beta, double gamma, double a,
        double b, double c)
    {
        double v;

        alpha *= RadAngle.DEG_TO_RAD;
        beta *= RadAngle.DEG_TO_RAD;
        gamma *= RadAngle.DEG_TO_RAD;

        double ca = Math.cos(alpha);
        double cb = Math.cos(beta);
        double cg = Math.cos(gamma);
        double sg = Math.sin(gamma);
        v = 1.0f - (ca * ca) - (cb * cb) - (cg * cg) + (2.0f * ca * cb * cg);
        v = Math.sqrt(Math.abs(v)) / sg;

        matrix[0][0] = a;
        matrix[0][1] = b * cg;
        matrix[0][2] = c * cb;

        matrix[1][0] = 0.0f;
        matrix[1][1] = b * sg;
        matrix[1][2] = (c * (ca - (cb * cg))) / sg;

        matrix[2][0] = 0.0f;
        matrix[2][1] = 0.0f;
        matrix[2][2] = c * v;
    }

    /**
     *  Description of the Method
     *
     * @param  row  Description of the Parameter
     * @param  column  Description of the Parameter
     * @return    Description of the Return Value
     */
    public final double get(int row, int column)
    {
        return (matrix[row][column]);
    }

    /**
     *  Gets the array attribute of the Matrix3x3 object
     *
     * @param  matrixArray  Description of the Parameter
     */
    public void getArray(double[] matrixArray)
    {
        matrixArray[0] = matrix[0][0];
        matrixArray[1] = matrix[0][1];
        matrixArray[2] = matrix[0][2];
        matrixArray[3] = matrix[1][0];
        matrixArray[4] = matrix[1][1];
        matrixArray[5] = matrix[1][2];
        matrixArray[6] = matrix[2][0];
        matrixArray[7] = matrix[2][1];
        matrixArray[8] = matrix[2][2];
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Matrix3D invert()
    {
        double[][] temp = new double[3][3];
        double det;

        det = determinant();

        if (det != 0.0)
        {
            temp[0][0] = (matrix[1][1] * matrix[2][2]) -
                (matrix[1][2] * matrix[2][1]);
            temp[1][0] = (matrix[1][2] * matrix[2][0]) -
                (matrix[1][0] * matrix[2][2]);
            temp[2][0] = (matrix[1][0] * matrix[2][1]) -
                (matrix[1][1] * matrix[2][0]);
            temp[0][1] = (matrix[2][1] * matrix[0][2]) -
                (matrix[2][2] * matrix[0][1]);
            temp[1][1] = (matrix[2][2] * matrix[0][0]) -
                (matrix[2][0] * matrix[0][2]);
            temp[2][1] = (matrix[2][0] * matrix[0][1]) -
                (matrix[2][1] * matrix[0][0]);
            temp[0][2] = (matrix[0][1] * matrix[1][2]) -
                (matrix[0][2] * matrix[1][1]);
            temp[1][2] = (matrix[0][2] * matrix[1][0]) -
                (matrix[0][0] * matrix[1][2]);
            temp[2][2] = (matrix[0][0] * matrix[1][1]) -
                (matrix[0][1] * matrix[1][0]);

            int row;
            int column;

            for (row = 0; row < 3; row++)
            {
                for (column = 0; column < 3; column++)
                {
                    matrix[row][column] = temp[row][column];
                }
            }

            this.diving(det);
        }

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     * @param  matrix  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D mul(final Vector3D vector, final Matrix3D matrix)
    {
        Vector3D result = new BasicVector3D();

        result.setX3D((vector.getX3D() * matrix.get(0, 0)) +
            (vector.getY3D() * matrix.get(0, 1)) +
            (vector.getZ3D() * matrix.get(0, 2)));
        result.setY3D((vector.getX3D() * matrix.get(1, 0)) +
            (vector.getY3D() * matrix.get(1, 1)) +
            (vector.getZ3D() * matrix.get(1, 2)));
        result.setZ3D((vector.getX3D() * matrix.get(2, 0)) +
            (vector.getY3D() * matrix.get(2, 1)) +
            (vector.getZ3D() * matrix.get(2, 2)));

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  matrix  Description of the Parameter
     * @param  vector  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D mul(final Matrix3D matrix, final Vector3D vector)
    {
        Vector3D result = new BasicVector3D();

        result.setX3D((vector.getX3D() * matrix.get(0, 0)) +
            (vector.getY3D() * matrix.get(0, 1)) +
            (vector.getZ3D() * matrix.get(0, 2)));
        result.setY3D((vector.getX3D() * matrix.get(1, 0)) +
            (vector.getY3D() * matrix.get(1, 1)) +
            (vector.getZ3D() * matrix.get(1, 2)));
        result.setZ3D((vector.getX3D() * matrix.get(2, 0)) +
            (vector.getY3D() * matrix.get(2, 1)) +
            (vector.getZ3D() * matrix.get(2, 2)));

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  rnd  Description of the Parameter
     */
    public void randomRotation(RandomNumber rnd)
    {
        Vector3D vector = new BasicVector3D();
        vector.randomUnitXYZVector(rnd);

        double rotAngle = (double) (rnd.nextInt() % 36000) / 100.0;

        if ((rnd.nextInt() % 2) == 0)
        {
            rotAngle *= -1.0f;
        }

        this.rotAboutAxisByAngle(vector, rotAngle);
    }

    /**
     *  Description of the Method
     *
     * @param  vector      Description of the Parameter
     * @param  angle  Description of the Parameter
     */
    public void rotAboutAxisByAngle(final Vector3D vector, final double angle)
    {
        double theta = angle * RadAngle.DEG_TO_RAD;
        double sinAngle = Math.sin(theta);
        double cosAngle = Math.cos(theta);
        double cosAngle_1 = 1 - cosAngle;

        Vector3D vtmp = new BasicVector3D(vector);
        vtmp.normalize();

        matrix[0][0] = (cosAngle_1 * vtmp.getX3D() * vtmp.getX3D()) + cosAngle;
        matrix[0][1] = (cosAngle_1 * vtmp.getX3D() * vtmp.getY3D()) +
            (sinAngle * vtmp.getZ3D());
        matrix[0][2] = (cosAngle_1 * vtmp.getX3D() * vtmp.getZ3D()) -
            (sinAngle * vtmp.getY3D());

        matrix[1][0] = (cosAngle_1 * vtmp.getX3D() * vtmp.getY3D()) -
            (sinAngle * vtmp.getZ3D());
        matrix[1][1] = (cosAngle_1 * vtmp.getY3D() * vtmp.getY3D()) + cosAngle;
        matrix[1][2] = (cosAngle_1 * vtmp.getY3D() * vtmp.getZ3D()) +
            (sinAngle * vtmp.getX3D());

        matrix[2][0] = (cosAngle_1 * vtmp.getX3D() * vtmp.getZ3D()) +
            (sinAngle * vtmp.getY3D());
        matrix[2][1] = (cosAngle_1 * vtmp.getY3D() * vtmp.getZ3D()) -
            (sinAngle * vtmp.getX3D());
        matrix[2][2] = (cosAngle_1 * vtmp.getZ3D() * vtmp.getZ3D()) + cosAngle;
    }

    /**
     *  Description of the Method
     *
     * @param  values        Description of the Parameter
     * @param  atoms  Description of the Parameter
     */
    public void rotateCoords(double[] values, int atoms)
    {
        int index;
        int cIndex;
        double x3D;
        double y3D;
        double z3D;

        for (index = 0; index < atoms; index++)
        {
            cIndex = index * 3;
            x3D = (values[cIndex] * matrix[0][0]) +
                (values[cIndex + 1] * matrix[0][1]) +
                (values[cIndex + 2] * matrix[0][2]);
            y3D = (values[cIndex] * matrix[1][0]) +
                (values[cIndex + 1] * matrix[1][1]) +
                (values[cIndex + 2] * matrix[1][2]);
            z3D = (values[cIndex] * matrix[2][0]) +
                (values[cIndex + 1] * matrix[2][1]) +
                (values[cIndex + 2] * matrix[2][2]);
            values[cIndex] = x3D;
            values[cIndex + 1] = y3D;
            values[cIndex + 2] = z3D;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  i  Description of the Parameter
     * @param  j  Description of the Parameter
     * @param  v  Description of the Parameter
     */
    public void set(int i, int j, double v)
    {
        matrix[i][j] = v;
    }

    /**
     * @param matrix2
     */
    public void setMatrixFrom(double[][] matrix)
    {
        this.matrix[0][0] = matrix[0][0];
        this.matrix[0][1] = matrix[0][1];
        this.matrix[0][2] = matrix[0][2];
        this.matrix[1][0] = matrix[1][0];
        this.matrix[1][1] = matrix[1][1];
        this.matrix[1][2] = matrix[1][2];
        this.matrix[2][0] = matrix[2][0];
        this.matrix[2][1] = matrix[2][1];
        this.matrix[2][2] = matrix[2][2];
    }

    /**
     *  Description of the Method
     *
     * @param  phi    Description of the Parameter
     * @param  theta  Description of the Parameter
     * @param  psi    Description of the Parameter
     */
    public void setupRotMat(double phi, double theta, double psi)
    {
        double phiRad = phi * RadAngle.DEG_TO_RAD;
        double thetaRad = theta * RadAngle.DEG_TO_RAD;
        double psiRad = psi * RadAngle.DEG_TO_RAD;

        double cosPhi = Math.cos(phiRad);
        double sinPhi = Math.sin(phiRad);
        double cosTheta = Math.cos(thetaRad);
        double sinTheta = Math.sin(thetaRad);
        double cosPsi = Math.cos(psiRad);
        double sinPsi = Math.sin(psiRad);

        matrix[0][0] = cosTheta * cosPsi;
        matrix[0][1] = cosTheta * sinPsi;
        matrix[0][2] = -sinTheta;

        matrix[1][0] = (sinPhi * sinTheta * cosPsi) - (cosPhi * sinPsi);
        matrix[1][1] = (sinPhi * sinTheta * sinPsi) + (cosPhi * cosPsi);
        matrix[1][2] = sinPhi * cosTheta;

        matrix[2][0] = (cosPhi * sinTheta * cosPsi) + (sinPhi * sinPsi);
        matrix[2][1] = (cosPhi * sinTheta * sinPsi) - (sinPhi * cosPsi);
        matrix[2][2] = cosPhi * cosTheta;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        return toString(this);
    }

    /**
     *  Description of the Method
     *
     * @param  m  Description of the Parameter
     * @return    Description of the Return Value
     */
    public String toString(Matrix3D m)
    {
        StringBuffer sb = new StringBuffer(100);

        sb.append("[ ");
        sb.append(m.get(0, 0));
        sb.append(", ");
        sb.append(m.get(0, 1));
        sb.append(", ");
        sb.append(m.get(0, 2));
        sb.append(" ]");
        sb.append("[ ");
        sb.append(m.get(1, 0));
        sb.append(", ");
        sb.append(m.get(1, 1));
        sb.append(", ");
        sb.append(m.get(1, 2));
        sb.append(" ]");
        sb.append("[ ");
        sb.append(m.get(2, 0));
        sb.append(", ");
        sb.append(m.get(2, 1));
        sb.append(", ");
        sb.append(m.get(2, 2));
        sb.append(" ]");

        return sb.toString();
    }

    /**
     * @return Returns the matrix.
     */
    protected double[][] getMatrix()
    {
        return matrix;
    }

    /**
     * @param matrix The matrix to set.
     */
    protected void setMatrix(double[][] matrix)
    {
        this.matrix = matrix;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
