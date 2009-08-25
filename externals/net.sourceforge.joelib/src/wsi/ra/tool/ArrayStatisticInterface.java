///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ArrayStatisticInterface.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:44 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
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
package wsi.ra.tool;

/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:44 $
 */
public interface ArrayStatisticInterface
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /*-------------------------------------------------------------------------*
     *  public  methods
     *-------------------------------------------------------------------------*/
    void add(double value);

    /**
     * Adds a value that has been seen n times to the observed values
     *
     * @param value  the observed value
     * @param n      the number of times to add value
     */
    void add(double value, int n);

    /**
     * Tells the object to calculate any statistics that don't have their
     * values automatically updated during add. Currently updates the mean
     * and standard deviation.
     */
    void calculateDerived();

    double deScale(double norm);

    /**
     * @return Returns the count.
     */
    int getCount();

    /**
     * @return Returns the max.
     */
    double getMax();

    /**
     * @return Returns the mean.
     */
    double getMean();

    /**
     * @return Returns the min.
     */
    double getMin();

    /**
     * @return Returns the stdDev.
     */
    double getStdDev();

    /**
     * @return Returns the sum.
     */
    double getSum();

    /**
     * @return Returns the sumSq.
     */
    double getSumSq();

    /**
     * Scales the input variables so that they have interval [0,1].
     *
     * @param val  Description of the Parameter
     * @return     Description of the Return Value
     */
    double scale(double val);

    /**
     * Removes a value to the observed values (no checking is done
     * that the value being removed was actually added).
     *
     * @param value  the observed value
     */
    void subtract(double value);

    /**
     * Subtracts a value that has been seen n times from the observed values
     *
     * @param value  the observed value
     * @param n      the number of times to subtract value
     */
    void subtract(double value, int n);

    /**
     * Returns a string summarising the stats so far.
     *
     * @return   the summary string
     */
    String toString();

    double varianceDeNormalization(double norm);

    /**
     * Scales the input variables so that they have similar magnitudes.
     * mean 0 and standard deviation 1.
     *
     * TeX: $x_i^n$ = \frac{x_i-\overline{x}}{\sigma _i}
     *
     * @param val  Description of the Parameter
     * @return     Description of the Return Value
     */
    double varianceNormalization(double val);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
