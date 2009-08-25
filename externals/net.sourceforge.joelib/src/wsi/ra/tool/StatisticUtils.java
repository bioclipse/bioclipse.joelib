///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: StatisticUtils.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner, Nikolas H. Fechner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:44 $
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
package wsi.ra.tool;

/**
 * Statistic utils.
 * @.author     wegnerj
 * @.author     Nikolas H. Fechner
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:44 $
 */
public class StatisticUtils
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /** The natural logarithm of 2. */
    public static double log2 = Math.log(2);

    /** The small deviation allowed in double comparisons */
    public static double SMALL = 1e-6;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns the correlation coefficient of two double vectors.
     *
     * @param y1 double vector 1
     * @param y2 double vector 2
     * @param n the length of two double vectors
     * @return the correlation coefficient
     */
    public final static double correlation(double[] y1, double[] y2, int n)
    {
        int i;
        double av1 = 0.0;
        double av2 = 0.0;
        double y11 = 0.0;
        double y22 = 0.0;
        double y12 = 0.0;
        double c;

        if (n <= 1)
        {
            return 1.0;
        }

        for (i = 0; i < n; i++)
        {
            av1 += y1[i];
            av2 += y2[i];
        }

        av1 /= (double) n;
        av2 /= (double) n;

        for (i = 0; i < n; i++)
        {
            y11 += ((y1[i] - av1) * (y1[i] - av1));
            y22 += ((y2[i] - av2) * (y2[i] - av2));
            y12 += ((y1[i] - av1) * (y2[i] - av2));
        }

        if ((y11 * y22) == 0.0)
        {
            c = 1.0;
        }
        else
        {
            c = y12 / Math.sqrt(Math.abs(y11 * y22));
        }

        return c;
    }

    /**
     * Calculates the covariance between the two double arrays a and b.
     * @param a
     * @param b
     * @return
     */
    public static final double covariance(double[] a, double[] b)
    {
        if (a.length != b.length)
        {
            System.err.println("Arrays are not of the same size!");

            return Double.NaN;
        }

        double sumA = 0.0;
        double sumB = 0.0;
        double m_A = 0.0;
        double m_B = 0.0;
        double sum = 0.0;

        for (int i = 0; i < a.length; i++)
        {
            sumA += a[i];
            sumB += b[i];
        }

        m_A = sumA / (double) a.length;
        m_B = sumB / (double) b.length;

        for (int i = 0; i < a.length; i++)
        {
            sum += ((a[i] - m_A) * (b[i] - m_B));
        }

        return sum / (double) a.length;
    }

    /**
     * Computes differential shannon entropy
     *
     * @return DSE=SE(AB)-0.5*[SE(A)+SE(B)]
     */
    public static double differentialShannon(int[] counts1, int[] counts2,
        int n, int countsSum1, int countsSum2)
    {
        if (counts1.length != counts2.length)
        {
            return Double.NaN;
        }

        double seA = 0.0;
        double seB = 0.0;
        double seAB = 0.0;
        double c = 0.0;
        int AB;
        int allSum = countsSum1 + countsSum2;

        for (int i = 0; i < n; i++)
        {
            AB = counts1[i] + counts2[i];
            seA -= xlogx(((double) counts1[i]) / ((double) countsSum1));
            seB -= xlogx(((double) counts2[i]) / ((double) countsSum2));
            seAB -= xlogx(((double) AB) / ((double) allSum));
        }

        c = seAB - (0.5 * (seA + seB));

        return c;
    }

    /**
     * Tests if a is equal to b.
     *
     * @param a a double
     * @param b a double
     */
    public static final boolean eq(double a, double b)
    {
        return ((a - b) < SMALL) && ((b - a) < SMALL);
    }

    /**
     * Returns the correlation coefficient r^2.
     *
     * Correlation ("Statistik", 7 Aufl., Hartung, 1989, Kapitel 9 und 10, S.546-608):
     * a=yMess[i];
     * b=yWahr[i];
     * aa=a*a;
     * bb=b*b;
     * ab=a*b;
     * numerator=sumAB-(sumA*sumB/n);
     * denominator=sqrt[(sumAA-(sumA*sumA/n))*(sumBB-(sumB*sumB/n))];
     * R=correlationcoefficient=numerator/denominator;
     *
     * @.author Joerg Kurt Wegner
     */
    public static double getCorrelationCoefficient(double[] array1,
        double[] array2)
    {
        if ((array1 == null) || (array2 == null))
        {
            return -2.0;
        }

        double sumA = 0;
        double sumB = 0;
        double sumAB = 0;
        double sumAA = 0;
        double sumBB = 0;

        for (int i = 0; i < array1.length; i++)
        {
            double a = array1[i];
            double b = array2[i];

            sumA += a;
            sumB += b;
            sumAA += (a * a);
            sumBB += (b * b);
            sumAB += (a * b);
        }

        double n = (double) array1.length;
        double numerator = sumAB - ((sumA * sumB) / n);
        double denominator = Math.sqrt((sumAA - ((sumA * sumA) / n)) *
                (sumBB - ((sumB * sumB) / n)));
        double corrCoefficient = numerator / denominator;
        corrCoefficient *= corrCoefficient;

        return corrCoefficient;
    }

    /**
     * Tests if a is greater than b.
     *
     * @param a a double
     * @param b a double
     */
    public static final boolean gr(double a, double b)
    {
        return ((a - b) > SMALL);
    }

    /**
     * Tests if a is greater or equal to b.
     *
     * @param a a double
     * @param b a double
     */
    public static final boolean grOrEq(double a, double b)
    {
        return ((b - a) < SMALL);
    }

    /**
     * Computes entropy for an array of integers.
     *
     * @param counts array of counts
     * @return - a log2 a - b log2 b - c log2 c + (a+b+c) log2 (a+b+c)
     * when given array [a b c]
     */
    public static double info(int[] counts)
    {
        int total = 0;
        double x = 0;

        for (int j = 0; j < counts.length; j++)
        {
            x -= xlogx(counts[j]);
            total += counts[j];
        }

        return x + xlogx(total);
    }

    /**
     * Returns the logarithm of a for base 2.
     *
     * @param a a double
     */
    public static final double log2(double a)
    {
        return Math.log(a) / log2;
    }

    /**
     * Main method for testing this class.
     */
    public static void main(String[] ops)
    {
        //    System.out.println("test (0.5, 100): " +
        //                     StatisticUtils.test(100));
    }

    /**
     * Returns index of maximum element in a given
     * array of doubles. First maximum is returned.
     *
     * @param doubles the array of doubles
     * @return the index of the maximum element
     */
    public static int maxIndex(double[] doubles)
    {
        double maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < doubles.length; i++)
        {
            if ((i == 0) || (doubles[i] > maximum))
            {
                maxIndex = i;
                maximum = doubles[i];
            }
        }

        return maxIndex;
    }

    /**
     * Returns index of maximum element in a given
     * array of integers. First maximum is returned.
     *
     * @param ints the array of integers
     * @return the index of the maximum element
     */
    public static int maxIndex(int[] ints)
    {
        int maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < ints.length; i++)
        {
            if ((i == 0) || (ints[i] > maximum))
            {
                maxIndex = i;
                maximum = ints[i];
            }
        }

        return maxIndex;
    }

    /**
     * Computes the mean for an array of doubles.
     *
     * @param vector the array
     * @return the mean
     */
    public static double mean(double[] vector)
    {
        double sum = 0;

        if (vector.length == 0)
        {
            return 0;
        }

        for (int i = 0; i < vector.length; i++)
        {
            sum += vector[i];
        }

        return sum / (double) vector.length;
    }

    /**
     * Returns index of minimum element in a given
     * array of integers. First minimum is returned.
     *
     * @param ints the array of integers
     * @return the index of the minimum element
     */
    public static int minIndex(int[] ints)
    {
        int minimum = 0;
        int minIndex = 0;

        for (int i = 0; i < ints.length; i++)
        {
            if ((i == 0) || (ints[i] < minimum))
            {
                minIndex = i;
                minimum = ints[i];
            }
        }

        return minIndex;
    }

    /**
     * Returns index of minimum element in a given
     * array of doubles. First minimum is returned.
     *
     * @param doubles the array of doubles
     * @return the index of the minimum element
     */
    public static int minIndex(double[] doubles)
    {
        double minimum = 0;
        int minIndex = 0;

        for (int i = 0; i < doubles.length; i++)
        {
            if ((i == 0) || (doubles[i] < minimum))
            {
                minIndex = i;
                minimum = doubles[i];
            }
        }

        return minIndex;
    }

    /**
     * Normalizes the doubles in the array by their sum.
     *
     * @param doubles the array of double
     * @exception IllegalArgumentException if sum is Zero or NaN
     */
    public static void normalize(double[] doubles)
    {
        double sum = 0;

        for (int i = 0; i < doubles.length; i++)
        {
            sum += doubles[i];
        }

        normalize(doubles, sum);
    }

    /**
     * Normalizes the doubles in the array using the given value.
     *
     * @param doubles the array of double
     * @param sum the value by which the doubles are to be normalized
     * @exception IllegalArgumentException if sum is zero or NaN
     */
    public static void normalize(double[] doubles, double sum)
    {
        if (Double.isNaN(sum))
        {
            throw new IllegalArgumentException(
                "Can't normalize array. Sum is NaN.");
        }

        if (sum == 0)
        {
            // Maybe this should just be a return.
            throw new IllegalArgumentException(
                "Can't normalize array. Sum is zero.");
        }

        for (int i = 0; i < doubles.length; i++)
        {
            doubles[i] /= sum;
        }
    }

    /**
     * returns root mean square error.
     */
    public static final double rmsError(double[] array1, double[] array2)
    {
        if ((array1 == null) || (array2 == null))
        {
            return -1.0;
        }

        double errorValueRMS = 0;

        for (int i = 0; i < array1.length; i++)
        {
            // add squared error value
            errorValueRMS += ((array1[i] - array2[i]) *
                    (array1[i] - array2[i]));
        }

        // calculate mean and root of the sum of the squared error values
        errorValueRMS = Math.sqrt(errorValueRMS / (double) array1.length);

        return errorValueRMS;
    }

    /**
     * Computes shannon entropy for an array of integers.
     *
     * @param counts array of counts
     * @return - a log2 a - b log2 b - c log2 c
     * when given array [a b c]
     */
    public static double shannon(int[] counts, int countsSum)
    {
        double x = 0;

        for (int j = 0; j < counts.length; j++)
        {
            x -= xlogx(((double) counts[j]) / ((double) countsSum));
        }

        return x;
    }

    /**
     * Tests if a is smaller than b.
     *
     * @param a a double
     * @param b a double
     */
    public static final boolean sm(double a, double b)
    {
        return ((b - a) > SMALL);
    }

    /**
     * Tests if a is smaller or equal to b.
     *
     * @param a a double
     * @param b a double
     */
    public static final boolean smOrEq(double a, double b)
    {
        return ((a - b) < SMALL);
    }

    /**
     * Computes the sum of the elements of an array of doubles.
     *
     * @param doubles the array of double
     * @return the sum of the elements
     */
    public static double sum(double[] doubles)
    {
        double sum = 0;

        for (int i = 0; i < doubles.length; i++)
        {
            sum += doubles[i];
        }

        return sum;
    }

    /**
     * Computes the sum of the elements of an array of integers.
     *
     * @param ints the array of integers
     * @return the sum of the elements
     */
    public static int sum(int[] ints)
    {
        int sum = 0;

        for (int i = 0; i < ints.length; i++)
        {
            sum += ints[i];
        }

        return sum;
    }

    /**
     * Computes the variance for an array of doubles.
     *
     * @param vector the array
     * @return the variance
     */
    public static double variance(double[] vector)
    {
        double sum = 0;
        double sumSquared = 0;

        if (vector.length <= 1)
        {
            return 0;
        }

        for (int i = 0; i < vector.length; i++)
        {
            sum += vector[i];
            sumSquared += (vector[i] * vector[i]);
        }

        double result = (sumSquared - ((sum * sum) / (double) vector.length)) /
            (double) (vector.length - 1);

        // We don't like negative variance
        if (result < 0)
        {
            return 0;
        }
        else
        {
            return result;
        }
    }

    /**
     * Returns c*log2(c) for a given integer value c.
     *
     * @param c an integer value
     * @return c*log2(c) (but is careful to return 0 if c is 0)
     */
    public static final double xlogx(int c)
    {
        if (c == 0)
        {
            return 0.0;
        }

        return c * StatisticUtils.log2((double) c);
    }

    /**
     * Returns c*log2(c) for a given value c.
     *
     * @param c an integer value
     * @return c*log2(c) (but is careful to return 0 if c is 0)
     */
    public static final double xlogx(double c)
    {
        if (c == 0)
        {
            return 0.0;
        }

        return c * StatisticUtils.log2(c);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
