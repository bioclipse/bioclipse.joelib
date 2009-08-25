///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicVector3D.java,v $
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

import org.apache.log4j.Category;


/**
 * Vector to represent x,y,z coordinates.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.4 $, $Date: 2005/02/17 16:48:35 $
 */
public class BasicVector3D implements Cloneable, java.io.Serializable, Vector3D
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(BasicVector3D.class
            .getName());

    //  The global constant XYZVectors

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public double x3D;

    /**
     *  Description of the Field
     */
    public double y3D;

    /**
     *  Description of the Field
     */
    public double z3D;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the XYZVector object
     */
    public BasicVector3D()
    {
        this(0.0f, 0.0f, 0.0f);
    }

    /**
     *  Constructor for the XYZVector object
     *
     * @param  other  Description of the Parameter
     */
    public BasicVector3D(final Vector3D other)
    {
        //  Copy Constructor
        setX3D(other.getX3D());
        setY3D(other.getY3D());
        setZ3D(other.getZ3D());
    }

    /**
     *  Constructor for the XYZVector object
     *
     * @param  x  Description of the Parameter
     * @param  y  Description of the Parameter
     * @param  z  Description of the Parameter
     */
    public BasicVector3D(final double x3D, final double y3D, final double z3D)
    {
        this.setX3D(x3D);
        this.setY3D(y3D);
        this.setZ3D(z3D);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D add(final Vector3D vector1, final Vector3D vector2)
    {
        return add(new BasicVector3D(), vector1, vector2);
    }

    /**
     *  Description of the Method
     *
     * @param  result  Description of the Parameter
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D add(Vector3D result, final Vector3D vector1,
        final Vector3D vector2)
    {
        result.setX3D(vector1.getX3D() + vector2.getX3D());
        result.setY3D(vector1.getY3D() + vector2.getY3D());
        result.setZ3D(vector1.getZ3D() + vector2.getZ3D());

        return result;
    }

    // create a random unit vector in R3

    public static double angle(final Vector3D vector1, final Vector3D vector2)
    {
        return
            Math.acos(dot(vector1, vector2) /
                (Math.sqrt(vector1.length() * vector2.length()))) *
            (180 / (Math.PI));
    }

    // calculate angle between vectors

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @param  vector3  Description of the Parameter
     * @param  vector4  Description of the Parameter
     * @return    Description of the Return Value
     */
    public static double calcTorsionAngle(Vector3D vector1, Vector3D vector2,
        Vector3D vector3, Vector3D vector4)
    {
        double torsion;
        Vector3D b1 = new BasicVector3D();
        Vector3D b2 = new BasicVector3D();
        Vector3D b3 = new BasicVector3D();
        Vector3D c1 = new BasicVector3D();
        Vector3D c2 = new BasicVector3D();
        Vector3D c3 = new BasicVector3D();

        BasicVector3D.sub(b1, vector1, vector2);
        BasicVector3D.sub(b2, vector2, vector3);
        BasicVector3D.sub(b3, vector3, vector4);

        BasicVector3D.cross(c1, b1, b2);
        BasicVector3D.cross(c2, b2, b3);
        BasicVector3D.cross(c3, c1, c2);

        if ((c1.length() * c2.length()) < 0.001)
        {
            torsion = 0.0;
        }
        else
        {
            torsion = BasicVector3D.xyzVectorAngle(c1, c2);

            if (dot(b2, c3) > 0.0)
            {
                torsion *= -1.0;
            }
        }

        return torsion;
    }

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D cross(final Vector3D vector1, final Vector3D vector2)
    {
        return cross(new BasicVector3D(), vector1, vector2);
    }

    /**
     *  Description of the Method
     *
     * @param  result  Description of the Parameter
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D cross(Vector3D result, final Vector3D vector1,
        final Vector3D vector2)
    {
        result.setX3D((vector1.getY3D() * vector2.getZ3D()) -
            (vector1.getZ3D() * vector2.getY3D()));
        result.setY3D((-vector1.getX3D() * vector2.getZ3D()) +
            (vector1.getZ3D() * vector2.getX3D()));
        result.setZ3D((vector1.getX3D() * vector2.getY3D()) -
            (vector1.getY3D() * vector2.getX3D()));

        return result;
    }

    //  Dot Product

    /**
     *  Description of the Method
     *
     * @param  original  Description of the Parameter
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public static Vector3D div(final Vector3D original, final int value)
    {
        return div(new BasicVector3D(), original, value);
    }

    /**
     *  Description of the Method
     *
     * @param  result  Description of the Parameter
     * @param  original   Description of the Parameter
     * @param  value   Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D div(Vector3D result, final Vector3D original,
        final double value)
    {
        result.setX3D(original.getX3D() / value);
        result.setY3D(original.getY3D() / value);
        result.setZ3D(original.getZ3D() / value);

        return result;
    }

    // vector and matrix ops

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static double dot(final Vector3D vector1, final Vector3D vector2)
    {
        double d;

        d = (vector1.getX3D() * vector2.getX3D()) +
            (vector1.getY3D() * vector2.getY3D()) +
            (vector1.getZ3D() * vector2.getZ3D());

        return d;
    }

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static boolean equals(final Vector3D vector1, final Vector3D vector2)
    {
        boolean isEqual = false;

        if ((vector1.getX3D() == vector2.getX3D()) &&
                (vector1.getY3D() == vector2.getY3D()) &&
                (vector1.getZ3D() == vector2.getZ3D()))
        {
            isEqual = true;
        }

        return isEqual;
    }

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D mul(final Vector3D vector1, final Vector3D vector2)
    {
        return mul(new BasicVector3D(), vector1, vector2);
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     * @param  matrix  Description of the Parameter
     * @return    Description of the Return Value
     */
    public static Vector3D mul(final Vector3D vector, final Matrix3D matrix)
    {
        return mul(new BasicVector3D(), vector, matrix);
    }

    /**
     *  Description of the Method
     *
     * @param  original  Description of the Parameter
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public static Vector3D mul(final Vector3D original, final int value)
    {
        return mul(new BasicVector3D(), original, value);
    }

    /**
     *  Description of the Method
     *
     * @param  result  Description of the Parameter
     * @param  vector1   Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D mul(Vector3D result, final Vector3D vector1,
        final Vector3D vector2)
    {
        result.setX3D(vector2.getX3D() * vector1.getX3D());
        result.setY3D(vector2.getY3D() * vector1.getY3D());
        result.setZ3D(vector2.getZ3D() * vector1.getZ3D());

        return result;
    }

    // create a vector orthogonal to me

    /**
     *  Description of the Method
     *
     * @param  result  Description of the Parameter
     * @param  vector   Description of the Parameter
     * @param  matrix   Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D mul(Vector3D result, final Vector3D vector,
        final Matrix3D matrix)
    {
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
     * @param  result  Description of the Parameter
     * @param  matrix   Description of the Parameter
     * @param  vector   Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D mul(Vector3D result, final Matrix3D matrix,
        final Vector3D vector)
    {
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
     * @param  result  Description of the Parameter
     * @param  original   Description of the Parameter
     * @param  value   Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D mul(Vector3D result, final Vector3D original,
        final double value)
    {
        result.setX3D(value * original.getX3D());
        result.setY3D(value * original.getY3D());
        result.setZ3D(value * original.getY3D());

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static boolean notEquals(final Vector3D vector1,
        final Vector3D vector2)
    {
        boolean notEqual = false;

        if ((vector1.getX3D() != vector2.getX3D()) ||
                (vector1.getY3D() != vector2.getY3D()) ||
                (vector1.getZ3D() != vector2.getZ3D()))
        {
            notEqual = true;
        }

        return notEqual;
    }

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D sub(final Vector3D vector1, final Vector3D vector2)
    {
        return sub(new BasicVector3D(), vector1, vector2);
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public static Vector3D sub(final Vector3D vector, final int value)
    {
        return sub(new BasicVector3D(), vector, value);
    }

    /**
     *  Description of the Method
     *
     * @param  result  Description of the Parameter
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D sub(Vector3D result, final Vector3D vector1,
        final Vector3D vector2)
    {
        result.setX3D(vector1.getX3D() - vector2.getX3D());
        result.setY3D(vector1.getY3D() - vector2.getY3D());
        result.setZ3D(vector1.getZ3D() - vector2.getZ3D());

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  result  Description of the Parameter
     * @param  original   Description of the Parameter
     * @param  value   Description of the Parameter
     * @return     Description of the Return Value
     */
    public static Vector3D sub(Vector3D result, final Vector3D original,
        final int value)
    {
        result.setX3D(original.getX3D() - value);
        result.setY3D(original.getY3D() - value);
        result.setZ3D(original.getZ3D() - value);

        return result;
    }

    /**
     *  Description of the Method
     *
     * @param  vector1  Description of the Parameter
     * @param  vector2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public static double xyzVectorAngle(final Vector3D vector1,
        final Vector3D vector2)
    {
        double mag;
        double dotProduct;

        mag = vector1.length() * vector2.length();
        dotProduct = dot(vector1, vector2) / mag;

        if (dotProduct < -0.999999)
        {
            dotProduct = -0.9999999;
        }

        if (dotProduct > 0.9999999)
        {
            dotProduct = 0.9999999;
        }

        if (dotProduct > 1.0)
        {
            dotProduct = 1.0;
        }

        return ((RadAngle.RAD_TO_DEG * Math.acos(dotProduct)));
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     * @return     Description of the Return Value
     */
    public Vector3D add(final Vector3D vector)
    {
        return add(new BasicVector3D(), this, vector);
    }

    /**
     *  Description of the Method
     *
     * @param  add  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D adding(final Vector3D add)
    {
        x3D += add.getX3D();
        y3D += add.getY3D();
        z3D += add.getZ3D();

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  values  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D adding(final double[] values)
    {
        x3D += values[0];
        y3D += values[1];
        z3D += values[2];

        return this;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object clone()
    {
        return this.get(new BasicVector3D());
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     */
    public final void createOrthoXYZVector(Vector3D vector)
    {
        BasicVector3D cO = new BasicVector3D();

        if ((this.getX3D() == 0.0) && (this.getY3D() == 0.0))
        {
            if (this.getZ3D() == 0.0)
            {
                logger.error("Orthovector is  zero vector");

                return;
            }

            cO.setX3D(1.0);
        }
        else
        {
            cO.setZ3D(1.0);
        }

        BasicVector3D.cross(vector, cO, this);
        vector.normalize();
    }

    public Vector3D cross(final Vector3D vector)
    {
        return cross(new BasicVector3D(), this, vector);
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     * @return     Description of the Return Value
     */
    public final double distSq(final Vector3D vector)
    {
        return (((x3D - vector.getX3D()) * (x3D - vector.getX3D())) +
                ((y3D - vector.getY3D()) * (y3D - vector.getY3D())) +
                ((z3D - vector.getZ3D()) * (z3D - vector.getZ3D())));
    }

    /**
     *  Description of the Method
     *
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D div(final int value)
    {
        return div(new BasicVector3D(), this, value);
    }

    /**
     *  Description of the Method
     *
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public BasicVector3D diving(final double value)
    {
        x3D /= value;
        y3D /= value;
        z3D /= value;

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  v2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean equals(Object obj)
    {
        if ((obj instanceof BasicVector3D) && (obj != null))
        {
            return equals(this, (BasicVector3D) obj);
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  toVector  Description of the Parameter
     * @return     Description of the Return Value
     */
    public Vector3D get(Vector3D toVector)
    {
        toVector.setX3D(x3D);
        toVector.setY3D(y3D);
        toVector.setY3D(z3D);

        return toVector;
    }

    /**
     *  Description of the Method
     *
     * @param  values  Description of the Parameter
     */
    public void get(double[] values)
    {
        if (values.length > 3)
        {
            logger.warn("Only elements 0-2 in double array are used.");
        }

        values[0] = x3D;
        values[1] = y3D;
        values[2] = z3D;
    }

    /**
     *  Description of the Method
     *
     * @param  values     Description of the Parameter
     * @param  cidx  Description of the Parameter
     */
    public void get(double[] values, int cidx)
    {
        if ((values.length - 1) < cidx)
        {
            logger.error("Atom coordinate-array length (" + values.length +
                ") smaller than requestd position (" + cidx + ")");

            return;
        }

        values[cidx] = x3D;
        values[cidx + 1] = y3D;
        values[cidx + 2] = z3D;
    }

    public final double getX3D()
    {
        return x3D;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public final double getY3D()
    {
        return y3D;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public final double getZ3D()
    {
        return z3D;
    }

    public int hashCode()
    {
        long bits = Double.doubleToLongBits(x3D);
        int ix = (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(y3D);

        int iy = (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(z3D);

        int iz = (int) (bits ^ (bits >>> 32));

        return ((ix & iy) * 31) & iz;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public final double length()
    {
        double l;

        l = Math.sqrt((x3D * x3D) + (y3D * y3D) + (z3D * z3D));

        return l;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public final double length_2()
    {
        double l;

        l = (x3D * x3D) + (y3D * y3D) + (z3D * z3D);

        return l;
    }

    /**
     *  Description of the Method
     *
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D mul(final int value)
    {
        return mul(new BasicVector3D(), this, value);
    }

    /**
     *  Description of the Method
     *
     * @param  value  Description of the Parameter
     * @return     Description of the Return Value
     */
    public Vector3D mul(final Vector3D value)
    {
        return mul(new BasicVector3D(), this, value);
    }

    /**
     *  Description of the Method
     *
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D muling(final double value)
    {
        x3D *= value;
        y3D *= value;
        z3D *= value;

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  matrix  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D muling(final Matrix3D matrix)
    {
        BasicVector3D vv = new BasicVector3D();

        vv.setX3D((x3D * matrix.get(0, 0)) + (y3D * matrix.get(0, 1)) +
            (z3D * matrix.get(0, 2)));
        vv.setY3D((x3D * matrix.get(1, 0)) + (y3D * matrix.get(1, 1)) +
            (z3D * matrix.get(1, 2)));
        vv.setZ3D((x3D * matrix.get(2, 0)) + (y3D * matrix.get(2, 1)) +
            (z3D * matrix.get(2, 2)));
        setX3D(vv.getX3D());
        setY3D(vv.getY3D());
        setZ3D(vv.getZ3D());

        return this;
    }

    //  Member Functions

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public BasicVector3D normalize()
    {
        double length = length();

        if (length != 0)
        {
            setX3D(x3D / length);
            setY3D(y3D / length);
            setZ3D(z3D / length);
        }

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  v2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean notEquals(final Vector3D v2)
    {
        return notEquals(this, v2);
    }

    /**
     *  Description of the Method
     */
    public void randomUnitXYZVector()
    {
        randomUnitXYZVector(null);
    }

    /**
     *  Description of the Method
     *
     * @param  oeRandP  Description of the Parameter
     */
    public void randomUnitXYZVector(RandomNumber oeRandP)
    {
        boolean doFree = false;

        if (oeRandP == null)
        {
            doFree = true;
            oeRandP = new RandomNumber(1234);
            oeRandP.timeSeed();
        }

        // make sure to sample in the unit sphere
        double double1 = 0.0;
        double double2 = 0.0;
        double double3 = 0.0;
        boolean isInUnit = false;

        while (!isInUnit)
        {
            double1 = oeRandP.nextFloat();
            double2 = oeRandP.nextFloat();
            double3 = oeRandP.nextFloat();

            if (isInUnit = ((double1 * double1) + (double2 * double2) +
                            (double3 * double3)) <= 1.0)
            {
                if ((oeRandP.nextInt() % 2) == 0)
                {
                    double1 *= -1.0;
                }

                if ((oeRandP.nextInt() % 2) == 0)
                {
                    double2 *= -1.0;
                }

                if ((oeRandP.nextInt() % 2) == 0)
                {
                    double3 *= -1.0;
                }
            }
        }

        this.set(double1, double2, double3);
        this.normalize();
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D set(final Vector3D vector)
    {
        if (this == vector)
        {
            return (this);
        }

        setX3D(vector.getX3D());
        setY3D(vector.getY3D());
        setZ3D(vector.getZ3D());

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  values  Description of the Parameter
     */
    public void set(final double[] values)
    {
        if (values.length > 3)
        {
            logger.warn("Only the entries from 0-2 are used in the array.");
        }

        setX3D(values[0]);
        setY3D(values[1]);
        setZ3D(values[2]);
    }

    /**
     *  Description of the Method
     *
     * @param  values     Description of the Parameter
     * @param  cidx  Description of the Parameter
     */
    public void set(final double[] values, int cidx)
    {
        if (cidx > (values.length - 3))
        {
            logger.error("Index " + cidx +
                " exceeds coordinate array with length " + values.length);
            setX3D(0.0);
            setY3D(0.0);
            setZ3D(0.0);
        }
        else
        {
            setX3D(values[cidx]);
            setY3D(values[cidx + 1]);
            setZ3D(values[cidx + 2]);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  x  Description of the Parameter
     * @param  y  Description of the Parameter
     * @param  z  Description of the Parameter
     */
    public void set(final double x3D, final double y3D, final double z3D)
    {
        setX3D(x3D);
        setY3D(y3D);
        setZ3D(z3D);
    }

    /**
     *  Copy this vector to the vector <tt>v</tt>.
     *
     * @param  vector  The new to value
     * @return    Description of the Return Value
     */
    public Vector3D setTo(Vector3D vector)
    {
        vector.setX3D(x3D);
        vector.setY3D(y3D);
        vector.setZ3D(z3D);

        return vector;
    }

    /**
     *  Sets the x attribute of the XYZVector object
     *
     * @param  x  The new x value
     */
    public void setX3D(final double x3D)
    {
        this.x3D = x3D;
    }

    /**
     *  Sets the y attribute of the XYZVector object
     *
     * @param  y  The new y value
     */
    public void setY3D(final double y3D)
    {
        this.y3D = y3D;
    }

    /**
     *  Sets the z attribute of the XYZVector object
     *
     * @param  z  The new z value
     */
    public void setZ3D(final double z3D)
    {
        this.z3D = z3D;
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     * @return     Description of the Return Value
     */
    public Vector3D sub(final Vector3D vector)
    {
        return sub(new BasicVector3D(), this, vector);
    }

    /**
     *  Description of the Method
     *
     * @param  value  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D sub(final int value)
    {
        return sub(new BasicVector3D(), this, value);
    }

    /**
     *  Description of the Method
     *
     * @param  vector  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D subing(final Vector3D vector)
    {
        x3D -= vector.getX3D();
        y3D -= vector.getY3D();
        z3D -= vector.getZ3D();

        return this;
    }

    /**
     *  Description of the Method
     *
     * @param  values  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Vector3D subing(final double[] values)
    {
        x3D -= values[0];
        y3D -= values[1];
        z3D -= values[2];

        return this;
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
     * @param  vector  Description of the Parameter
     * @return    Description of the Return Value
     */
    public String toString(Vector3D vector)
    {
        return "< " + vector.getX3D() + ", " + vector.getY3D() + ", " +
            vector.getZ3D() + " >";
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
