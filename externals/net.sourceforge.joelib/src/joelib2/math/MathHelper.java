///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MathHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
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

import cformat.PrintfFormat;
import cformat.PrintfStream;

import jmat.data.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


/**
 * Mathematical helper methods.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:35 $
 */
public class MathHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private final static int MAX_SWEEPS = 50;

    /**
     *  1/sqrt(3).
     */
    public final static double ONE_OVER_SQRT3 = 0.577350269f;

    /**
     *  sqrt(2/3).
     */
    public final static double SQRT_TWO_THIRDS = 0.816496581f;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @param a  3x3 matrix
     * @param v  3x3 matrix
     */
    public static void jacobi3x3(double[][] a, double[][] v)
    {
        double onorm;
        double dnorm;
        double b;
        double dma;
        double q;
        double t;
        double c;
        double s;
        double[] d = new double[3];
        double atemp;
        double vtemp;
        double dtemp;
        int i;
        int j;
        int k;
        int l;

        //memset((char*)d,'\0',sizeof(double)*3);
        for (j = 0; j < 3; j++)
        {
            for (i = 0; i < 3; i++)
            {
                v[i][j] = 0.0;
            }

            v[j][j] = 1.0;
            d[j] = a[j][j];
        }

        for (l = 1; l <= MAX_SWEEPS; l++)
        {
            dnorm = 0.0;
            onorm = 0.0;

            for (j = 0; j < 3; j++)
            {
                dnorm = dnorm + Math.abs(d[j]);

                for (i = 0; i <= (j - 1); i++)
                {
                    onorm = onorm + Math.abs(a[i][j]);
                }
            }

            if ((onorm / dnorm) <= 1.0e-12)
            {
                break;
            }

            for (j = 1; j < 3; j++)
            {
                for (i = 0; i <= (j - 1); i++)
                {
                    b = a[i][j];

                    if (Math.abs(b) > 0.0)
                    {
                        dma = d[j] - d[i];

                        if ((Math.abs(dma) + Math.abs(b)) <= Math.abs(dma))
                        {
                            t = b / dma;
                        }
                        else
                        {
                            q = (0.5 * dma) / b;
                            t = 1.0 / (Math.abs(q) + Math.sqrt(1.0 + (q * q)));

                            if (q < 0.0)
                            {
                                t = -t;
                            }
                        }

                        c = 1.0 / Math.sqrt((t * t) + 1.0);
                        s = t * c;
                        a[i][j] = 0.0;

                        for (k = 0; k <= (i - 1); k++)
                        {
                            atemp = (c * a[k][i]) - (s * a[k][j]);
                            a[k][j] = (s * a[k][i]) + (c * a[k][j]);
                            a[k][i] = atemp;
                        }

                        for (k = i + 1; k <= (j - 1); k++)
                        {
                            atemp = (c * a[i][k]) - (s * a[k][j]);
                            a[k][j] = (s * a[i][k]) + (c * a[k][j]);
                            a[i][k] = atemp;
                        }

                        for (k = j + 1; k < 3; k++)
                        {
                            atemp = (c * a[i][k]) - (s * a[j][k]);
                            a[j][k] = (s * a[i][k]) + (c * a[j][k]);
                            a[i][k] = atemp;
                        }

                        for (k = 0; k < 3; k++)
                        {
                            vtemp = (c * v[k][i]) - (s * v[k][j]);
                            v[k][j] = (s * v[k][i]) + (c * v[k][j]);
                            v[k][i] = vtemp;
                        }

                        dtemp = ((c * c * d[i]) + (s * s * d[j])) -
                            (2.0 * c * s * b);
                        d[j] = (s * s * d[i]) + (c * c * d[j]) +
                            (2.0 * c * s * b);
                        d[i] = dtemp;
                    }

                    /* end if   */
                }

                /* end for i   */
            }

            /* end for j   */
        }

        /* end for l   */
        /* max_sweeps = l;  */
        for (j = 0; j < (3 - 1); j++)
        {
            k = j;
            dtemp = d[k];

            for (i = j + 1; i < 3; i++)
            {
                if (d[i] < dtemp)
                {
                    k = i;
                    dtemp = d[k];
                }
            }

            if (k > j)
            {
                d[k] = d[j];
                d[j] = dtemp;

                for (i = 0; i < 3; i++)
                {
                    dtemp = v[i][k];
                    v[i][k] = v[i][j];
                    v[i][j] = dtemp;
                }
            }
        }
    }

    public static String matrixToString(Matrix m)
    {
        return matrixToString(m, 6, 2);
    }

    public static String matrixToString(Matrix m, int digitWidth,
        int decimalDigits)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(50000);
        PrintfStream fp = new PrintfStream(baos);
        printMatrix(m, fp, digitWidth, decimalDigits);

        return baos.toString();
    }

    /**
     * Description of the Method
     *
     * @param m              Description of the Parameter
     * @param os             Description of the Parameter
     * @param digitWidth     Description of the Parameter
     * @param decimalDigits  Description of the Parameter
     */
    public static void printMatrix(Matrix m, OutputStream os, int digitWidth,
        int decimalDigits)
    {
        PrintfStream fp = new PrintfStream(os);

        if (os instanceof PrintfStream)
        {
            fp = (PrintfStream) os;
        }
        else
        {
            fp = new PrintfStream(os);
        }

        PrintfFormat d3;

        if (decimalDigits < digitWidth)
        {
            d3 = new PrintfFormat("%" + digitWidth + "." + decimalDigits + "f");
        }
        else
        {
            d3 = new PrintfFormat("%" + digitWidth + "f");
        }

        fp.println();

        // start on new line.
        int rows = m.getRowDimension();
        int colums = m.getColumnDimension();

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < colums; j++)
            {
                fp.printf(d3, m.get(i, j));
            }

            fp.println();
        }

        fp.println();

        // end with blank line.
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
