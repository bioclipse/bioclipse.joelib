///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Sorter.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:40 $
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
package joelib2.sort;

import java.util.Comparator;
import java.util.List;


/**
 *  The basic class for sort classes.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:40 $
 *
 * @see        Comparator
 * @see        Comparable
 * @see        java.util.Arrays#sort(java.lang.Object[], java.util.Comparator)
 * @see        joelib2.sort.InsertSort
 * @see        joelib2.sort.QuickInsertSort
 */
public abstract class Sorter implements IntArraySorter, DoubleArraySorter,
    LongArraySorter, XYDoubleArraySorter, XYIntArraySorter
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  x  Description of the Parameter
     * @param  c  Description of the Parameter
     */
    public abstract void sort(Object[] x, Comparator c);

    /**
     *  Description of the Method
     *
     * @param  x  Description of the Parameter
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     */
    public final static void swap(int[] x, int a, int b)
    {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    /**
     *  Description of the Method
     *
     * @param  x  Description of the Parameter
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     */
    public final static void swap(double[] x, int a, int b)
    {
        double t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    /**
     *  Description of the Method
     *
     * @param  x  Description of the Parameter
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     */
    public final static void swap(long[] x, int a, int b)
    {
        long t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    /**
     *  Description of the Method
     *
     * @param  x  Description of the Parameter
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     */
    public final static void swap(Object[] x, int a, int b)
    {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    /**
     *  Description of the Method
     *
     * @param  vec  Description of the Parameter
     * @param  a    Description of the Parameter
     * @param  b    Description of the Parameter
     */
    public final static void swap(List vec, int a, int b)
    {
        Object t = vec.get(a);
        vec.set(a, vec.get(b));
        vec.set(b, t);
    }

    /**
     *  Description of the Method
     *
     * @param  xy  Description of the Parameter
     * @param  a   Description of the Parameter
     * @param  b   Description of the Parameter
     */
    public final static void swap(XYDoubleArray xy, int a, int b)
    {
        xy.swap(a, b);
    }

    /**
     *  Description of the Method
     *
     * @param  xy  Description of the Parameter
     * @param  a   Description of the Parameter
     * @param  b   Description of the Parameter
     */
    public final static void swap(XYIntArray xy, int a, int b)
    {
        xy.swap(a, b);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
