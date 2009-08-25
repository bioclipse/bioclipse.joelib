///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: XYIntArray.java,v $
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

/**
 *  Defines two <tt>int[]</tt> arrays.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:40 $
 */
public class XYIntArray implements Cloneable, java.io.Serializable
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int[] x = null;

    /**
     *  Description of the Field
     */
    public int[] y = null;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the XYDoubleArray object
     *
     * @param  length  Description of the Parameter
     */
    public XYIntArray(int length)
    {
        this.x = new int[length];
        this.y = new int[length];
    }

    /**
     *  Constructor for the XYDoubleArray object
     *
     * @param  x  Description of the Parameter
     * @param  y  Description of the Parameter
     */
    public XYIntArray(int[] x, int[] y)
    {
        this.x = x;
        this.y = y;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object clone()
    {
        int[] newX = new int[x.length];
        int[] newY = new int[y.length];
        XYIntArray newArray = new XYIntArray(newX, newY);

        System.arraycopy(x, 0, newArray.x, 0, x.length);
        System.arraycopy(y, 0, newArray.y, 0, y.length);

        return newArray;
    }

    /**
     *  Description of the Method
     */
    public final void sortX()
    {
        QuickInsertSort quickInsertSort = new QuickInsertSort();
        quickInsertSort.sortX(this);
    }

    /**
     *  Description of the Method
     */
    public final void sortY()
    {
        QuickInsertSort quickInsertSort = new QuickInsertSort();
        quickInsertSort.sortY(this);
    }

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     */
    public final void swap(int a, int b)
    {
        int xx = x[a];
        x[a] = x[b];
        x[b] = xx;

        int yy = y[a];
        y[a] = y[b];
        y[b] = yy;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
