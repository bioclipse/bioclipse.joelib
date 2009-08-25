///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: QuickInsertSort.java,v $
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
import java.util.Random;


/**
 *  Quick Sort combined with Insert Sort.
 *
 *      This routine was developed for a algorithm lesson at the university of
 *      T&uuml;bingen in 1999.
 *
 * @.author     wegnerj
 * @.wikipedia  Quicksort
 * @.wikipedia  Insertion sort
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:40 $
 * @see        Comparator
 * @see        Comparable
 * @see        java.util.Arrays#sort(java.lang.Object[], java.util.Comparator)
 * @see        Sorter
 * @see        joelib2.sort.InsertSort
 */
public class QuickInsertSort extends Sorter
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private InsertSort insertSort = new InsertSort();
    private boolean randomize;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the mixed sorting algorithm. Combines the advantages of
     *  two sort algorithms. quick sort is good at unsorted arrays O(1), but bad
     *  at sorted arrays O(N^2) insert sort is good at sorted arrays O(1), but bad
     *  at unsorted arrays O(N^2) So this algorithm uses first quick sort and then
     *  for the smaller array fragments insert sort.<br>
     *
     */
    public QuickInsertSort()
    {
        this(false);
    }

    /**
     *  Constructor for the mixed sorting algorithm. Combines the advantages of
     *  two sort algorithms. quick sort is good at unsorted arrays O(1), but bad
     *  at sorted arrays O(N^2) insert sort is good at sorted arrays O(1), but bad
     *  at unsorted arrays O(N^2) So this algorithm uses first quick sort and then
     *  for the smaller array fragments insert sort.<br>
     *  If the array contains a lot of sorted parts, you can use randomization.
     *
     * @param  _randomize  if <tt>true</tt> the array is randomized.
     */
    public QuickInsertSort(boolean _randomize)
    {
        randomize = _randomize;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Sorts an array in ascending order.
     *
     * @param  a  the array.
     */
    public void sort(int[] a)
    {
        if (randomize)
        {
            randomize(a);
        }

        quickInsertSort(a, 0, a.length - 1);
        insertSort.sort(a);
    }

    /**
     *  Sorts an array in ascending order.
     *
     * @param  a  the array.
     */
    public void sort(double[] a)
    {
        if (randomize)
        {
            randomize(a);
        }

        quickInsertSort(a, 0, a.length - 1);
        insertSort.sort(a);
    }

    /**
     *  Sorts an array in ascending order.
     *
     * @param  a  the array.
     */
    public void sort(long[] a)
    {
        if (randomize)
        {
            randomize(a);
        }

        quickInsertSort(a, 0, a.length - 1);
        insertSort.sort(a);
    }

    /**
     *  Sorts an array in ascending order.
     *
     * @param  x  Description of the Parameter
     * @param  c  Description of the Parameter
     */
    public void sort(Object[] x, Comparator c)
    {
        if (randomize)
        {
            randomize(x);
        }

        quickInsertSort(x, 0, x.length - 1, c);
        insertSort.sort(x, c);
    }

    /**
     *  Sorts an array in ascending order.
     *
     * @param  vec  Description of the Parameter
     * @param  c    Description of the Parameter
     */
    public void sort(List vec, Comparator c)
    {
        if (randomize)
        {
            randomize(vec);
        }

        quickInsertSort(vec, 0, vec.size() - 1, c);
        insertSort.sort(vec, c);
    }

    /**
     *  Sorts an <tt>XYDoubleArray</tt> in decreasing order of the x values.
     *
     * @param  xy  Description of the Parameter
     */
    public void sortX(XYDoubleArray xy)
    {
        if (randomize)
        {
            randomize(xy);
        }

        quickInsertSortX(xy, 0, xy.x.length - 1);
        insertSort.sortX(xy);
    }

    /**
     *  Sorts an <tt>XYDoubleArray</tt> in decreasing order of the x values.
     *
     * @param  xy  Description of the Parameter
     */
    public void sortX(XYIntArray xy)
    {
        if (randomize)
        {
            randomize(xy);
        }

        quickInsertSortX(xy, 0, xy.x.length - 1);
        insertSort.sortX(xy);
    }

    /**
     *  Sorts an <tt>XYDoubleArray</tt> in decreasing order of the y values.
     *
     * @param  xy  Description of the Parameter
     */
    public void sortY(XYDoubleArray xy)
    {
        if (randomize)
        {
            randomize(xy);
        }

        quickInsertSortY(xy, 0, xy.y.length - 1);
        insertSort.sortY(xy);
    }

    /**
     *  Sorts an <tt>XYDoubleArray</tt> in decreasing order of the y values.
     *
     * @param  xy  Description of the Parameter
     */
    public void sortY(XYIntArray xy)
    {
        if (randomize)
        {
            randomize(xy);
        }

        quickInsertSortY(xy, 0, xy.y.length - 1);
        insertSort.sortY(xy);
    }

    /*-------------------------------------------------------------------------*
     * private methods
     *------------------------------------------------------------------------- */

    /**
     *  Description of the Method
     *
     * @param  a    Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     */
    private void quickInsertSort(int[] a, int lo0, int hi0)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        int pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = a[(lo0 + hi0) / 2];

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (a[lo] < pElem))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (a[hi] > pElem))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSort(a, lo0, hi);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSort(a, lo, hi0);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a    Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     */
    private void quickInsertSort(double[] a, int lo0, int hi0)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        double pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = a[(lo0 + hi0) / 2];

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (a[lo] < pElem))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (a[hi] > pElem))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSort(a, lo0, hi);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSort(a, lo, hi0);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a    Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     */
    private void quickInsertSort(long[] a, int lo0, int hi0)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        long pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = a[(lo0 + hi0) / 2];

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (a[lo] < pElem))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (a[hi] > pElem))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSort(a, lo0, hi);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSort(a, lo, hi0);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a    Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     * @param  c    Description of the Parameter
     */
    private void quickInsertSort(Object[] a, int lo0, int hi0, Comparator c)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        Object pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = a[(lo0 + hi0) / 2];

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (c.compare(a[lo], pElem) < 0))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (c.compare(a[hi], pElem) > 0))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSort(a, lo0, hi, c);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSort(a, lo, hi0, c);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  vec  Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     * @param  c    Description of the Parameter
     */
    private void quickInsertSort(List vec, int lo0, int hi0, Comparator c)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        Object pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = vec.get((lo0 + hi0) / 2);

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (c.compare(vec.get(lo), pElem) < 0))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (c.compare(vec.get(hi), pElem) > 0))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    swap(vec, lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSort(vec, lo0, hi, c);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSort(vec, lo, hi0, c);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy   Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     */
    private void quickInsertSortX(XYDoubleArray xy, int lo0, int hi0)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        double pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = xy.x[(lo0 + hi0) / 2];

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (xy.x[lo] < pElem))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (xy.x[hi] > pElem))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    xy.swap(lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSortX(xy, lo0, hi);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSortX(xy, lo, hi0);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy   Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     */
    private void quickInsertSortX(XYIntArray xy, int lo0, int hi0)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        int pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = xy.x[(lo0 + hi0) / 2];

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (xy.x[lo] < pElem))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (xy.x[hi] > pElem))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    xy.swap(lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSortX(xy, lo0, hi);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSortX(xy, lo, hi0);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy   Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     */
    private void quickInsertSortY(XYDoubleArray xy, int lo0, int hi0)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        double pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = xy.y[(lo0 + hi0) / 2];

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (xy.y[lo] < pElem))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (xy.y[hi] > pElem))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    xy.swap(lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSortY(xy, lo0, hi);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSortY(xy, lo, hi0);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy   Description of the Parameter
     * @param  lo0  Description of the Parameter
     * @param  hi0  Description of the Parameter
     */
    private void quickInsertSortY(XYIntArray xy, int lo0, int hi0)
    {
        int lo = lo0;

        //Zeiger auf die Elemente unterhalb des partitionElement
        int hi = hi0;

        //Zeiger auf die Elemente oberhalb des partitionElement
        int pElem;

        //partitionElement. It divide the data array in two parts.
        int noRecPar = 20;

        //Ist der zu sortierende Teilbereich kleiner als (hier:20) x Elemente, so ueberspringe
        //die Rekursion. Am Ende aller (mit QuickSort) vorsortierten Elemente wird nun
        //InsertSoert angewendet. Man koennte auch die einzelnen Teilbereiche mit InsertSort
        //sortieren, jedoch ist die Anwendung auf das gesamte vorsortierte Array effektiver.
        if ((hi0 - lo0) >= noRecPar)
        {
            //nimmt einfach das mittlere Element als teilendes Element.
            pElem = xy.y[(lo0 + hi0) / 2];

            //wiederholt die Schleife bis sich die Zeiger schneiden.
            while (lo <= hi)
            {
                //suche das erste Element, das groeSer oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem kleinsten Index (Zeiger).
                while ((lo < hi0) && (xy.y[lo] < pElem))
                {
                    ++lo;
                }

                //suche das erste Element, das kleiner oder gleich dem teilenden Element (partitionElement)
                //ist, beginnend mit dem groeStem Index (Zeiger).
                while ((hi > lo0) && (xy.y[hi] > pElem))
                {
                    --hi;
                }

                // vertausche die gefundenen Werte, wenn die Zeiger noch nicht vertauscht sind.
                if (lo <= hi)
                {
                    xy.swap(lo, hi);
                    ++lo;
                    --hi;
                }
            }

            //Wenn der rechte Index nicht die linke Seite des Array erreicht hat, so
            //muß nun die linke Seite sortiert werden.
            if (lo0 < hi)
            {
                quickInsertSortY(xy, lo0, hi);
            }

            //Wenn der linke Index nicht die rechte Seite des Array erreicht hat, so
            //muß nun die rechte Seite sortiert werden.
            if (lo < hi0)
            {
                quickInsertSortY(xy, lo, hi0);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     */
    private void randomize(int[] a)
    {
        if (a.length == 0)
        {
            return;
        }

        Random wheel = new Random(149);
        int length_1 = a.length - 1;

        if (a.length <= 4)
        {
            swap(a, 0, length_1);

            return;
        }

        int c;
        int length = (int) (a.length / 8);

        //es werden 1/8 aller Elemente vertauscht.
        for (int i = 0; i < length; i++)
        {
            //vertauscht wird aber die linke mit der rechten Haelfte.
            c = Math.abs(wheel.nextInt() % (length_1 - 2));
            swap(a, c, length_1 - c);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     */
    private void randomize(double[] a)
    {
        if (a.length == 0)
        {
            return;
        }

        Random wheel = new Random(149);
        int length_1 = a.length - 1;

        if (a.length <= 4)
        {
            swap(a, 0, length_1);

            return;
        }

        int c;
        int length = (int) (a.length / 8);

        //es werden 1/8 aller Elemente vertauscht.
        for (int i = 0; i < length; i++)
        {
            //vertauscht wird aber die linke mit der rechten Haelfte.
            c = Math.abs(wheel.nextInt() % (length_1 - 2));
            swap(a, c, length_1 - c);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     */
    private void randomize(long[] a)
    {
        if (a.length == 0)
        {
            return;
        }

        Random wheel = new Random(149);
        int length_1 = a.length - 1;

        if (a.length <= 4)
        {
            swap(a, 0, length_1);

            return;
        }

        int c;
        int length = (int) (a.length / 8);

        //es werden 1/8 aller Elemente vertauscht.
        for (int i = 0; i < length; i++)
        {
            //vertauscht wird aber die linke mit der rechten Haelfte.
            c = Math.abs(wheel.nextInt() % (length_1 - 2));
            swap(a, c, length_1 - c);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     */
    private void randomize(Object[] a)
    {
        if (a.length == 0)
        {
            return;
        }

        Random wheel = new Random(149);
        int length_1 = a.length - 1;

        if (a.length <= 4)
        {
            swap(a, 0, length_1);

            return;
        }

        int c;
        int length = (int) (a.length / 8);

        //es werden 1/8 aller Elemente vertauscht.
        for (int i = 0; i < length; i++)
        {
            //vertauscht wird aber die linke mit der rechten Haelfte.
            c = Math.abs(wheel.nextInt() % (length_1 - 2));
            swap(a, c, length_1 - c);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  vec  Description of the Parameter
     */
    private void randomize(List vec)
    {
        if (vec.size() == 0)
        {
            return;
        }

        Random wheel = new Random(149);
        int length_1 = vec.size() - 1;

        if (vec.size() <= 4)
        {
            swap(vec, 0, length_1);

            return;
        }

        int c;
        int length = (int) (vec.size() / 8);

        //es werden 1/8 aller Elemente vertauscht.
        for (int i = 0; i < length; i++)
        {
            //vertauscht wird aber die linke mit der rechten Haelfte.
            c = Math.abs(wheel.nextInt() % (length_1 - 2));
            swap(vec, c, length_1 - c);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy  Description of the Parameter
     */
    private void randomize(XYDoubleArray xy)
    {
        if (xy.x.length == 0)
        {
            return;
        }

        Random wheel = new Random(149);
        int length_1 = xy.x.length - 1;

        if (xy.x.length <= 4)
        {
            xy.swap(0, length_1);

            return;
        }

        int c;
        int length = (int) (xy.x.length / 8);

        //es werden 1/8 aller Elemente vertauscht.
        for (int i = 0; i < length; i++)
        {
            //vertauscht wird aber die linke mit der rechten Haelfte.
            c = Math.abs(wheel.nextInt() % (length_1 - 2));
            xy.swap(c, length_1 - c);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  xy  Description of the Parameter
     */
    private void randomize(XYIntArray xy)
    {
        if (xy.x.length == 0)
        {
            return;
        }

        Random wheel = new Random(149);
        int length_1 = xy.x.length - 1;

        if (xy.x.length <= 4)
        {
            xy.swap(0, length_1);

            return;
        }

        int c;
        int length = (int) (xy.x.length / 8);

        //es werden 1/8 aller Elemente vertauscht.
        for (int i = 0; i < length; i++)
        {
            //vertauscht wird aber die linke mit der rechten Haelfte.
            c = Math.abs(wheel.nextInt() % (length_1 - 2));
            xy.swap(c, length_1 - c);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
