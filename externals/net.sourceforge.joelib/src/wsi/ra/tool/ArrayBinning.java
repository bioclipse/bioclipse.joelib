///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ArrayBinning.java,v $
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
 *  A class to store simple binning statistics
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:44 $
 */
public class ArrayBinning implements Serializable, ArrayBinningInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public ArrayStatistic arrayStat;

    /**
     *  Description of the Field
     */
    public int[] binning;
    public boolean containsNaN = false;

    /**
     *  Description of the Field
     */
    public double entropy = Double.NaN;

    /**
     *  Description of the Field
     */
    public int numberOfBins;

    /**
     *  Description of the Field
     */
    public double shannonEntropy = Double.NaN;

    /**
     *  Description of the Field
     */
    public double sum = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the ArrayBinning object
     *
     * @param  _numberOfBins  Description of the Parameter
     * @param  _arrayStat     Description of the Parameter
     */
    public ArrayBinning(int _numberOfBins, ArrayStatistic _arrayStat)
    {
        binning = new int[_numberOfBins];
        arrayStat = _arrayStat;
        numberOfBins = _numberOfBins;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the differential shannon entropy.
     *
     * @param  ab1  Description of the Parameter
     * @param  ab2  Description of the Parameter
     * @return      The dSE value
     */
    public final static double getDSE(ArrayBinning ab1, ArrayBinning ab2)
    {
        if ((ab1 == null) || (ab2 == null))
        {
            return Double.NaN;
        }

        return StatisticUtils.differentialShannon(ab1.binning, ab2.binning,
                ab1.numberOfBins, (int) ab1.getArrayStatistic().count,
                (int) ab2.getArrayStatistic().count);
    }

    /**
     *  Adds a value to the observed values
     *
     * @param  value  the observed value
     * @return        Description of the Return Value
     */
    public int add(double value)
    {
        return add(value, 1);
    }

    /**
     *  Adds a value that has been seen n times to the observed values
     *
     * @param  value  the observed value
     * @param  n      the number of times to add value
     * @return        bin, or -1 if this value could not be binned
     */
    public int add(double value, double n)
    {
        if (Double.isNaN(value))
        {
            containsNaN = true;
        }

        if ((value < arrayStat.min) || (value > arrayStat.max))
        {
            System.out.println(this.getClass().getName() + "value " + value +
                " not valid for " + arrayStat.toString());

            return -1;
        }

        //System.out.println("max: "+arrayStat.max);
        //System.out.println("min: "+arrayStat.min);
        double delta = (arrayStat.max - arrayStat.min) / numberOfBins;
        int intBin;

        //System.out.println("delta: "+delta);
        if (delta == 0.0)
        {
            binning[0] += n;

            return 0;
        }
        else
        {
            if (value == arrayStat.max)
            {
                binning[numberOfBins - 1] += n;

                return numberOfBins - 1;
            }
            else
            {
                //System.out.println("value: "+value);
                double bin = (value - arrayStat.min) / delta;
                intBin = (int) bin;

                if (intBin == numberOfBins)
                {
                    //System.out.println("WARN: "+this.getClass().getName()+" set "+value+" to bin ("+intBin+"-1) in array (0,"+binning.length+")");
                    intBin--;
                }

                //System.out.println("### bin: "+((int)bin));
                //if(bin<0.0 || bin >( (double) numberOfBins)-1)
                //{
                //System.out.println("##############0-"+((int)numberOfBins)+": "+((int)bin));
                //return false;
                //}
                try
                {
                    binning[intBin] += n;
                }
                catch (Exception ex)
                {
                    System.out.println(this.getClass().getName() + "value " +
                        value + " not valid for " + arrayStat.toString());
                    System.out.println(this.getClass().getName() + "set " +
                        value + " to bin " + intBin + " in array (0," +
                        binning.length + ")");
                    ex.printStackTrace();

                    //System.out.println("##############0-"+((int)numberOfBins)+": "+((int)bin));
                    //System.exit(1);
                    return -1;
                }
            }
        }

        return intBin;
    }

    /**
     *  Tells the object to calculate any statistics that don't have their values
     *  automatically updated during add. Currently updates the shannon entropy.
     */
    public void calculateDerived()
    {
        //    if(Double.isNaN(shannonEntropy))
        //    {
        //System.out.println("getting shannon");
        shannonEntropy = StatisticUtils.shannon(binning, (int) arrayStat.count);
        entropy = StatisticUtils.info(binning);

        //    }
    }

    //  public static void main(String [] args) {
    //
    //    try {
    //      ArrayBinning ps = new ArrayBinning();
    //      java.io.LineNumberReader r = new java.io.LineNumberReader(
    //                             new java.io.InputStreamReader(System.in));
    //      String line;
    //      while ((line = r.readLine()) != null) {
    //        line = line.trim();
    //        if (line.equals("") || line.startsWith("@") || line.startsWith("%")) {
    //          continue;
    //        }
    //  java.util.StringTokenizer s
    //          = new java.util.StringTokenizer(line, " ,\t\n\r\f");
    //  int count = 0;
    //  double v1 = 0;
    //  while (s.hasMoreTokens()) {
    //    double val = (new Double(s.nextToken())).doubleValue();
    //    if (count == 0) {
    //      v1 = val;
    //    } else {
    //            System.err.println("MSG: Too many values in line \""
    //                               + line + "\", skipped.");
    //      break;
    //    }
    //    count++;
    //  }
    //        if (count == 1) {
    //          ps.add(v1);
    //        }
    //      }
    //      System.err.println(ps);
    //    } catch (Exception ex) {
    //      ex.printStackTrace();
    //      System.err.println(ex.getMessage());
    //    }
    //  }
    /**
     * @return Returns the arrayStat.
     */
    public ArrayStatistic getArrayStat()
    {
        return arrayStat;
    }

    /**
     *  Gets the descriptorStatistic attribute of the DescStatistic object
     *
     * @return    The descriptorStatistic value
     */
    public final ArrayStatistic getArrayStatistic()
    {
        return arrayStat;
    }

    /**
     * @return Returns the binning.
     */
    public int[] getBinning()
    {
        return binning;
    }

    /**
     * @return Returns the entropy.
     */
    public double getEntropy()
    {
        return entropy;
    }

    /**
     * @return Returns the numberOfBins.
     */
    public int getNumberOfBins()
    {
        return numberOfBins;
    }

    /**
     * @return Returns the shannonEntropy.
     */
    public double getShannonEntropy()
    {
        return shannonEntropy;
    }

    /**
     * @return Returns the sum.
     */
    public double getSum()
    {
        return sum;
    }

    /**
     * @return Returns the containsNaN.
     */
    public boolean isContainsNaN()
    {
        return containsNaN;
    }

    /**
     *  Returns a string summarising the stats so far.
     *
     * @return    the summary string
     */
    public String toString()
    {
        calculateDerived();

        int size = binning.length;
        StringBuffer sb = new StringBuffer(1000);

        // description line
        //sb.append("Count Min Max Sum SumSq Mean StdDev Binning((Min),1-"+numberOfBins+",(Max))\n");
        // show statistical data
        sb.append(arrayStat.count);
        sb.append(' ');

        // show entropy data
        sb.append(shannonEntropy);
        sb.append(' ');
        sb.append(entropy);
        sb.append(' ');
        sb.append(arrayStat.min);
        sb.append(' ');
        sb.append(arrayStat.max);
        sb.append(' ');
        sb.append(arrayStat.sum);
        sb.append(' ');
        sb.append(arrayStat.sumSq);
        sb.append(' ');
        sb.append(arrayStat.mean);
        sb.append(' ');
        sb.append(arrayStat.stdDev);
        sb.append(' ');
        sb.append(containsNaN);

        //        sb.append(' ');
        // show binning data
        //        sb.append('(');
        //        sb.append(arrayStat.min);
        //        sb.append(')');
        //        sb.append(' ');
        //        sb.append(" (");
        //        sb.append(arrayStat.max);
        //        sb.append(")");
        for (int i = 0; i < size; i++)
        {
            sb.append(' ');
            sb.append(binning[i]);
        }

        //    sb.append((arrayStat.stdDev/(arrayStat.max-arrayStat.min)));
        //    sb.append(' ');
        sb.append('\n');

        return sb.toString();
    }

    /**
     * @param arrayStat The arrayStat to set.
     */
    protected void setArrayStat(ArrayStatistic arrayStat)
    {
        this.arrayStat = arrayStat;
    }

    /**
     * @param binning The binning to set.
     */
    protected void setBinning(int[] binning)
    {
        this.binning = binning;
    }

    /**
     * @param containsNaN The containsNaN to set.
     */
    protected void setContainsNaN(boolean containsNaN)
    {
        this.containsNaN = containsNaN;
    }

    /**
     * @param entropy The entropy to set.
     */
    protected void setEntropy(double entropy)
    {
        this.entropy = entropy;
    }

    /**
     * @param numberOfBins The numberOfBins to set.
     */
    protected void setNumberOfBins(int numberOfBins)
    {
        this.numberOfBins = numberOfBins;
    }

    /**
     * @param shannonEntropy The shannonEntropy to set.
     */
    protected void setShannonEntropy(double shannonEntropy)
    {
        this.shannonEntropy = shannonEntropy;
    }

    /**
     * @param sum The sum to set.
     */
    protected void setSum(double sum)
    {
        this.sum = sum;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
