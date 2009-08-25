///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: InsertSort.java,v $
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
 *  Insert Sort.
 *      This routine was developed for a algorithm lesson at the university of
 *      T&uuml;bingen in 1999.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:40 $
 * @see        Comparator
 * @see        Comparable
 * @see        java.util.Arrays#sort(java.lang.Object[], java.util.Comparator)
 * @see        Sorter
 * @see        joelib2.sort.QuickInsertSort
 */
public class InsertSort extends Sorter
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     */
    public InsertSort()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     */
    public void sort(int[] a)
    {
        int i;
        int j;
        int v;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (i = 0; i < a.length; i++)
        {
            v = a[i];
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (a[j - 1] > v))
            {
                a[j] = a[j - 1];
                j--;
            }

            a[j] = v;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     */
    public void sort(double[] a)
    {
        int i;
        int j;
        double v;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (i = 0; i < a.length; i++)
        {
            v = a[i];
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (a[j - 1] > v))
            {
                a[j] = a[j - 1];
                j--;
            }

            a[j] = v;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     */
    public void sort(long[] a)
    {
        int i;
        int j;
        long v;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (i = 0; i < a.length; i++)
        {
            v = a[i];
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (a[j - 1] > v))
            {
                a[j] = a[j - 1];
                j--;
            }

            a[j] = v;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  x  Description of the Parameter
     * @param  c  Description of the Parameter
     */
    public void sort(Object[] x, Comparator c)
    {
        int i;
        int j;
        Object v;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (i = 0; i < x.length; i++)
        {
            v = x[i];
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (c.compare(x[j - 1], v) > 0))
            {
                x[j] = x[j - 1];
                j--;
            }

            x[j] = v;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  vec  Description of the Parameter
     * @param  c    Description of the Parameter
     */
    public void sort(List vec, Comparator c)
    {
        int i;
        int j;
        Object v;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (i = 0; i < vec.size(); i++)
        {
            v = vec.get(i);
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (c.compare(vec.get(j - 1), v) > 0))
            {
                vec.set(j, vec.get(j - 1));
                j--;
            }

            vec.set(j, v);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy  Description of the Parameter
     */
    public void sortX(XYDoubleArray xy)
    {
        int j;
        double vx;
        double vy;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (int i = 0; i < xy.x.length; i++)
        {
            vy = xy.y[i];
            vx = xy.x[i];
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (xy.x[j - 1] > vx))
            {
                xy.y[j] = xy.y[j - 1];
                xy.x[j] = xy.x[j - 1];
                j--;
            }

            xy.y[j] = vy;
            xy.x[j] = vx;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy  Description of the Parameter
     */
    public void sortX(XYIntArray xy)
    {
        int j;
        int vx;
        int vy;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (int i = 0; i < xy.x.length; i++)
        {
            vy = xy.y[i];
            vx = xy.x[i];
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (xy.x[j - 1] > vx))
            {
                xy.y[j] = xy.y[j - 1];
                xy.x[j] = xy.x[j - 1];
                j--;
            }

            xy.y[j] = vy;
            xy.x[j] = vx;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy  Description of the Parameter
     */
    public void sortY(XYDoubleArray xy)
    {
        int i;
        int j;
        double vx;
        double vy;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (i = 0; i < xy.y.length; i++)
        {
            vy = xy.y[i];
            vx = xy.x[i];
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (xy.y[j - 1] > vy))
            {
                xy.y[j] = xy.y[j - 1];
                xy.x[j] = xy.x[j - 1];
                j--;
            }

            xy.y[j] = vy;
            xy.x[j] = vx;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy  Description of the Parameter
     */
    public void sortY(XYIntArray xy)
    {
        int i;
        int j;
        int vx;
        int vy;

        //Betrachte Element fuer Element und fuege jedes an seinen richtigen Platz zwischen
        //die bereits betrachteten ein.
        for (i = 0; i < xy.y.length; i++)
        {
            vy = xy.y[i];
            vx = xy.x[i];
            j = i;

            //1. breche while Schleife ab, wenn j-1 einen Wert kleiner Null hat.
            //2. breche while Schleife ab, wenn das Element bei j-1 groeSer als das
            //   Element bei i ist.
            while ((j > 0) && (xy.y[j - 1] > vy))
            {
                xy.y[j] = xy.y[j - 1];
                xy.x[j] = xy.x[j - 1];
                j--;
            }

            xy.y[j] = vy;
            xy.x[j] = vx;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
