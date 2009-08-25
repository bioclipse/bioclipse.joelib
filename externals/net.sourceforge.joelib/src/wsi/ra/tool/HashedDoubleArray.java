///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: HashedDoubleArray.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.1 $
//          $Date: 2006/03/03 07:13:24 $
//          $Author: wegner $
//
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

import java.util.Arrays;


/**
 *  Some helper methods for calling external programs.
 *
 * @author     wegnerj
 */
public class HashedDoubleArray
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    protected double[] array;
    private int hash;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Don't let anyone instantiate this class
     */
    public HashedDoubleArray()
    {
    }

    /**
     *  Constructor for the HashedIntArray object
     *
     * @param  _array  Description of the Parameter
     */
    public HashedDoubleArray(double[] _array)
    {
        array = _array;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Sets the array attribute of the HashedIntArray object
     *
     * @param  _array  The new array value
     */
    public void setArray(double[] _array)
    {
        array = _array;
        hash = 0;
    }

    /**
     *  Gets the integer array. You should never change the array or call all
     *  changes <code>rehash()</code>.
     *
     * @return    The array value
     */
    public final double[] getArray()
    {
        return array;
    }

    /**
     *  Description of the Method
     *
     * @param  _index  Description of the Parameter
     * @return         Description of the Return Value
     */
    public final double get(int _index)
    {
        //      if(array==null) throw new NullPointerException();
        //      if(_index<0 ||_index>=array.length)throw new ArrayIndexOutOfBoundsException(_index);
        return array[_index];
    }

    /**
     *  Returns a hash code for this integer array. The hash code for a <code>int []</code>
     *  object is computed as <blockquote><pre>
     * arr[0]*31^(n-1) + arr[1]*31^(n-2) + ... + arr[n-1]
     * </pre></blockquote> using <code>int</code> arithmetic, where <code>arr[i]</code>
     *  is the <i>i</i> th value of the integer array, <code>n</code> is the
     *  length of the array, and <code>^</code> indicates exponentiation. (The
     *  hash value of the empty integer array is zero.)
     *
     * @return    a hash code value for this object.
     */
    public int hashCode()
    {
        if (hash == 0)
        {
            hash = hashCode(array);
        }

        return hash;
    }

    /**
     *  Returns a hash code for this integer array. The hash code for a <code>int []</code>
     *  object is computed as <blockquote><pre>
     * arr[0]*31^(n-1) + arr[1]*31^(n-2) + ... + arr[n-1]
     * </pre></blockquote> using <code>int</code> arithmetic, where <code>arr[i]</code>
     *  is the <i>i</i> th value of the integer array, <code>n</code> is the
     *  length of the array, and <code>^</code> indicates exponentiation. (The
     *  hash value of the empty integer array is zero.)
     *
     * @param  arr  Description of the Parameter
     * @return      a hash code value for this object.
     */
    public static int hashCode(double[] arr)
    {
        int h = 0;

        if (arr == null)
        {
            return 0;
        }

        int len = arr.length;

        int shash;

        for (int i = 0; i < len; i++)
        {
            long bits = Double.doubleToLongBits(arr[i]);
            shash = (int) (bits ^ (bits >>> 32));
            h = (31 * h) + shash;
        }

        return h;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public final int length()
    {
        if (array == null)
        {
            return 0;
        }

        return array.length;
    }

    /**
     *  Description of the Method
     *
     * @param  obj  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean equals(Object obj)
    {
        if ((obj == null) || (array == null))
        {
            return false;
        }

        if (obj instanceof double[])
        {
            return Arrays.equals(array, (double[]) obj);
        }
        else if (obj instanceof HashedDoubleArray)
        {
            HashedDoubleArray tmpObj = (HashedDoubleArray) obj;

            if (tmpObj.array.length != this.array.length)
            {
                return false;
            }

            if (tmpObj.hashCode() == hashCode())
            {
                for (int i = 0; i < tmpObj.array.length; i++)
                {
                    if (tmpObj.array[i] != this.array[i])
                    {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    /**
     *  The main program for the HashedIntArray class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        int index = 1;
        double[] test = new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
        HashedDoubleArray hashArr = new HashedDoubleArray();

        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{2, 3, 4, 5, 6, 20};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{0, 2, 3, 4, 5, 6};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{2, 3, 4, 5, 6, 0};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{1, 2, 3, 5, 6, 10};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{1, 2, 3, 4, 5, 20};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{1, 2, 3, 4, 5};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{1, 2, 3, 4, 1};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{1, 2, 3, 4, 5, 6, 7};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{60000, 60000};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]{1, 2};
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]
            {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
                19, 20
            };
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new double[]
            {
                255, 255, 355, 455, 555, 655, 755, 855, 955, 1055, 1155, 1255,
                1355, 1455, 1555, 1655, 1755, 1855, 1955, 2055
            };
        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
    }

    /**
     *  Returns a hash code for this integer array. The hash code for a <code>int []</code>
     *  object is computed as <blockquote><pre>
     * arr[0]*31^(n-1) + arr[1]*31^(n-2) + ... + arr[n-1]
     * </pre></blockquote> using <code>int</code> arithmetic, where <code>arr[i]</code>
     *  is the <i>i</i> th value of the integer array, <code>n</code> is the
     *  length of the array, and <code>^</code> indicates exponentiation. (The
     *  hash value of the empty integer array is zero.)
     *
     * @return    a hash code value for this object.
     */
    public int rehash()
    {
        hash = hashCode(array);

        return hash;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        int len = array.length;
        StringBuffer sb = new StringBuffer(len * 8);
        sb.append('<');

        for (int i = 0; i < len; i++)
        {
            sb.append(array[i]);

            if (i < (len - 1))
            {
                sb.append(",");
            }
        }

        sb.append('>');

        return sb.toString();
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
