///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ArrayBinningInterface.java,v $
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
public interface ArrayBinningInterface
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Adds a value to the observed values
     *
     * @param  value  the observed value
     * @return        Description of the Return Value
     */
    int add(double value);

    /**
     *  Adds a value that has been seen n times to the observed values
     *
     * @param  value  the observed value
     * @param  n      the number of times to add value
     * @return        bin, or -1 if this value could not be binned
     */
    int add(double value, double n);

    /**
     *  Tells the object to calculate any statistics that don't have their values
     *  automatically updated during add. Currently updates the shannon entropy.
     */
    void calculateDerived();

    //  public static void main(String [] args) {
    ArrayStatistic getArrayStat();

    /**
     *  Gets the descriptorStatistic attribute of the DescStatistic object
     *
     * @return    The descriptorStatistic value
     */
    ArrayStatistic getArrayStatistic();

    /**
     * @return Returns the binning.
     */
    int[] getBinning();

    /**
     * @return Returns the entropy.
     */
    double getEntropy();

    /**
     * @return Returns the numberOfBins.
     */
    int getNumberOfBins();

    /**
     * @return Returns the shannonEntropy.
     */
    double getShannonEntropy();

    /**
     * @return Returns the sum.
     */
    double getSum();

    /**
     * @return Returns the containsNaN.
     */
    boolean isContainsNaN();

    /**
     *  Returns a string summarising the stats so far.
     *
     * @return    the summary string
     */
    String toString();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
