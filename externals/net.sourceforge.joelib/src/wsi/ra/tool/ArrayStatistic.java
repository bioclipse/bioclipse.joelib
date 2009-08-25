///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ArrayStatistic.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
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

import java.io.Serializable;


/**
 * A class to store simple statistics
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:44 $
 */
public class ArrayStatistic implements Serializable, ArrayStatisticInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * The number of values seen
     */
    public int count = 0;

    /**
     * The maximum value seen, or Double.NaN if no values seen
     */
    public double max = Double.NaN;

    /**
     * The mean of values at the last calculateDerived() call
     */
    public double mean = Double.NaN;

    /**
     * The minimum value seen, or Double.NaN if no values seen
     */
    public double min = Double.NaN;

    /**
     * The std deviation of values at the last calculateDerived() call
     */
    public double stdDev = Double.NaN;

    /**
     * The sum of values seen
     */
    public double sum = 0;

    /**
     * The sum of values squared seen
     */
    public double sumSq = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ArrayStatistic()
    {
    }

    public ArrayStatistic(int _count, double _min, double _max, double _sum,
        double _sumSq, double _mean, double _stdDev)
    {
        count = _count;
        sum = _sum;
        sumSq = _sumSq;
        stdDev = _stdDev;
        mean = _mean;
        min = _min;
        max = _max;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Tests the paired stats object from the command line.
     * reads line from stdin, expecting two values per line.
     *
     * @param args  ignored.
     */
    public static void main(String[] args)
    {
        try
        {
            ArrayStatistic ps = new ArrayStatistic();
            java.io.LineNumberReader r = new java.io.LineNumberReader(
                    new java.io.InputStreamReader(System.in));
            String line;

            while ((line = r.readLine()) != null)
            {
                line = line.trim();

                if (line.equals("") || line.startsWith("@") ||
                        line.startsWith("%"))
                {
                    continue;
                }

                java.util.StringTokenizer s = new java.util.StringTokenizer(
                        line, " ,\t\n\r\f");
                int count = 0;
                double v1 = 0;

                while (s.hasMoreTokens())
                {
                    double val = (new Double(s.nextToken())).doubleValue();

                    if (count == 0)
                    {
                        v1 = val;
                    }
                    else
                    {
                        System.err.println("MSG: Too many values in line \"" +
                            line + "\", skipped.");

                        break;
                    }

                    count++;
                }

                if (count == 1)
                {
                    ps.add(v1);
                }
            }

            ps.calculateDerived();
            System.err.println(ps);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
    }

    /*-------------------------------------------------------------------------*
     *  public  methods
     *-------------------------------------------------------------------------*/

    /**
     * Adds a value to the observed values
     *
     * @param value  the observed value
     */
    public void add(double value)
    {
        add(value, 1);
    }

    /**
     * Adds a value that has been seen n times to the observed values
     *
     * @param value  the observed value
     * @param n      the number of times to add value
     */
    public void add(double value, int n)
    {
        sum += (value * n);
        sumSq += (value * value * n);
        count += n;

        if (Double.isNaN(min))
        {
            min = max = value;
        }
        else if (value < min)
        {
            min = value;
        }
        else if (value > max)
        {
            max = value;
        }
    }

    /**
     * Tells the object to calculate any statistics that don't have their
     * values automatically updated during add. Currently updates the mean
     * and standard deviation.
     */
    public void calculateDerived()
    {
        mean = Double.NaN;
        stdDev = Double.NaN;

        if (count > 0)
        {
            mean = sum / (double) count;
            stdDev = Double.POSITIVE_INFINITY;

            if (count > 1)
            {
                stdDev = sumSq - ((sum * sum) / (double) count);
                stdDev /= (count - 1);

                if (stdDev < 0)
                {
                    //          System.err.println("Warning: stdDev value = " + stdDev
                    //                             + " -- rounded to zero.");
                    stdDev = 0;
                }

                stdDev = Math.sqrt(stdDev);
            }
        }
    }

    public double deScale(double norm)
    {
        double val = Double.NaN;

        val = (norm * Math.abs(max - min)) + min;

        return val;
    }

    /**
     * @return Returns the count.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * @return Returns the max.
     */
    public double getMax()
    {
        return max;
    }

    /**
     * @return Returns the mean.
     */
    public double getMean()
    {
        return mean;
    }

    /**
     * @return Returns the min.
     */
    public double getMin()
    {
        return min;
    }

    /**
     * @return Returns the stdDev.
     */
    public double getStdDev()
    {
        return stdDev;
    }

    /**
     * @return Returns the sum.
     */
    public double getSum()
    {
        return sum;
    }

    /**
     * @return Returns the sumSq.
     */
    public double getSumSq()
    {
        return sumSq;
    }

    /**
     * Scales the input variables so that they have interval [0,1].
     *
     * @param val  Description of the Parameter
     * @return     Description of the Return Value
     */
    public double scale(double val)
    {
        double norm = Double.NaN;

        norm = (val - min) / Math.abs(max - min);

        return norm;
    }

    /**
     * Removes a value to the observed values (no checking is done
     * that the value being removed was actually added).
     *
     * @param value  the observed value
     */
    public void subtract(double value)
    {
        subtract(value, 1);
    }

    /**
     * Subtracts a value that has been seen n times from the observed values
     *
     * @param value  the observed value
     * @param n      the number of times to subtract value
     */
    public void subtract(double value, int n)
    {
        sum -= (value * n);
        sumSq -= (value * value * n);
        count -= n;
    }

    /**
     * Returns a string summarising the stats so far.
     *
     * @return   the summary string
     */
    public String toString()
    {
        //calculateDerived();
        return "Count:   " + count + ' ' + "Min:     " + min + ' ' +
            "Max:     " + max + ' ' + "Sum:     " + sum + ' ' + "SumSq:   " +
            sumSq + ' ' + "Mean:    " + mean + ' ' + "StdDev:  " + stdDev + ' ';
    }

    public double varianceDeNormalization(double norm)
    {
        double val = Double.NaN;

        if ((stdDev == 0.0) && (val == 0.0))
        {
            return 0.0;
        }

        val = (norm * stdDev) + mean;

        return val;
    }

    /**
     * Scales the input variables so that they have similar magnitudes.
     * mean 0 and standard deviation 1.
     *
     * TeX: $x_i^n$ = \frac{x_i-\overline{x}}{\sigma _i}
     *
     * @param val  Description of the Parameter
     * @return     Description of the Return Value
     */
    public double varianceNormalization(double val)
    {
        double norm = Double.NaN;

        if ((stdDev == 0.0) && (val == 0.0))
        {
            return 0.0;
        }

        norm = (val - mean) / stdDev;

        //        System.out.println("norm (m:"+mean+", v:"+stdDev+"): "+val+" --> "+norm);
        return norm;
    }

    /**
     * @param count The count to set.
     */
    protected void setCount(int count)
    {
        this.count = count;
    }

    /**
     * @param max The max to set.
     */
    protected void setMax(double max)
    {
        this.max = max;
    }

    /**
     * @param mean The mean to set.
     */
    protected void setMean(double mean)
    {
        this.mean = mean;
    }

    /**
     * @param min The min to set.
     */
    protected void setMin(double min)
    {
        this.min = min;
    }

    /**
     * @param stdDev The stdDev to set.
     */
    protected void setStdDev(double stdDev)
    {
        this.stdDev = stdDev;
    }

    /**
     * @param sum The sum to set.
     */
    protected void setSum(double sum)
    {
        this.sum = sum;
    }

    /**
     * @param sumSq The sumSq to set.
     */
    protected void setSumSq(double sumSq)
    {
        this.sumSq = sumSq;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
