///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicArrayHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:41 $
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
package joelib2.util;

import wsi.ra.text.DecimalFormatter;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Array helper methods for writing and loading arrays.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:41 $
 */
public class BasicArrayHelper implements ArrayHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public static String DEFAULT_SEPARATOR = ",";

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(BasicArrayHelper.class
            .getName());
    private static BasicArrayHelper arrayHelper;

    //~ Instance fields ////////////////////////////////////////////////////////

    private String separator;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOERandom object
     */
    private BasicArrayHelper()
    {
        separator = DEFAULT_SEPARATOR;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Loads boolean array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>booleanArrayFromString</code>.
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static boolean[] booleanArrayFromSimpleString(String sArrays,
        String separator)
    {
        StringTokenizer st = new StringTokenizer(sArrays, separator);

        // start with a vector of size 50
        Vector tmpVector = new Vector(100);

        // get integer values from String
        String tmpString;

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();

            if (tmpString.equals("1"))
            {
                tmpVector.add(Boolean.TRUE);
            }
            else if (tmpString.equals("0"))
            {
                tmpVector.add(Boolean.FALSE);
            }
            else
            {
                logger.error(tmpString +
                    " is not a valid token in a bit string.");

                return null;
            }
        }

        // copy values to integer array
        int size = tmpVector.size();
        boolean[] array = new boolean[size];

        //    System.out.print("bits:");
        for (int i = 0; i < size; i++)
        {
            array[i] = ((Boolean) tmpVector.get(i)).booleanValue();

            //      System.out.print(""+(array[i]==true?'1':'0'));
        }

        //    System.out.println("");
        return array;
    }

    /**
     *  Loads boolean array from <tt>String</tt> . Format n<b_1,b_2,...,b_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>booleanArrayFromSimpleString</code>.
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static List booleanArrayFromString(String sArrays, String separator,
        int size)
    {
        String tmpString;
        int endOfArray;
        int startOfArray;
        int arrayLength = 0;

        startOfArray = sArrays.indexOf('<');

        if (startOfArray == -1)
        {
            return null;
        }

        if (size == -1)
        {
            tmpString = sArrays.substring(0, startOfArray);
            arrayLength = Integer.parseInt(tmpString);
        }
        else
        {
            arrayLength = size;
        }

        tmpString = sArrays.substring(startOfArray, sArrays.length());

        StringTokenizer st = new StringTokenizer(tmpString, separator);
        int arrayIndex = 0;
        int index = 0;
        boolean[] array = new boolean[arrayLength];
        Vector tmpVector = new Vector();
        tmpVector.add(array);

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();

            if (tmpString.charAt(0) == '<')
            {
                tmpString = tmpString.substring(1, tmpString.length());
            }
            else if ((endOfArray = tmpString.lastIndexOf('>')) != -1)
            {
                // add last index entry from this array
                array[index] = (tmpString.substring(0, endOfArray).equals("1")
                        ? true : false);

                // and get the first entry from the new one
                startOfArray = tmpString.indexOf('<');

                if (startOfArray == -1)
                {
                    return tmpVector;
                }

                arrayIndex++;
                arrayLength = Integer.parseInt(tmpString.substring(
                            endOfArray + 1, startOfArray).trim());
                array = new boolean[arrayLength];
                tmpVector.add(array);
                index = 0;
                tmpString = tmpString.substring(startOfArray + 1,
                        tmpString.length());
            }

            if (index < array.length)
            {
                array[index] = (tmpString.equals("1") ? true : false);
            }
            else
            {
                //        throw new ArrayIndexOutOfBoundsException("int array at Vector index "+arrayIndex+" is out of range ("+index+").");
                logger.error("boolean array at Vector index " + arrayIndex +
                    " is out of range.");

                return null;
            }

            index++;
        }

        return tmpVector;
    }

    /**
     *  Loads boolean array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>booleanArrayFromString</code>.
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static boolean[] booleanArrayFromTrueFalseString(String sArrays,
        String separator)
    {
        StringTokenizer st = new StringTokenizer(sArrays, separator);

        // start with a vector of size 50
        Vector tmpVector = new Vector(100);

        // get integer values from String
        String tmpString;

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();

            if (tmpString.equals("true"))
            {
                tmpVector.add(Boolean.TRUE);
            }
            else if (tmpString.equals("false"))
            {
                tmpVector.add(Boolean.FALSE);
            }
            else
            {
                logger.error(tmpString +
                    " is not a valid token in a bit string.");

                return null;
            }
        }

        // copy values to integer array
        int size = tmpVector.size();
        boolean[] array = new boolean[size];

        //    System.out.print("bits:");
        for (int i = 0; i < size; i++)
        {
            array[i] = ((Boolean) tmpVector.get(i)).booleanValue();

            //      System.out.print(""+(array[i]==true?'1':'0'));
        }

        //    System.out.println("");
        return array;
    }

    /**
     *  Loads integer array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>intArrayFromString</code>.
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static double[] doubleArrayFromSimpleString(String sArrays,
        String separator)
    {
        StringTokenizer st = new StringTokenizer(sArrays, separator);

        // start with a vector of size 50
        Vector tmpVector = new Vector(50);

        // get integer values from String
        String tmpString;

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();
            tmpVector.add(new Double(tmpString));
        }

        // copy values to integer array
        int size = tmpVector.size();
        double[] array = new double[size];

        for (int i = 0; i < size; i++)
        {
            array[i] = ((Double) tmpVector.get(i)).doubleValue();
        }

        return array;
    }

    /**
     *  Loads double array from <tt>String</tt> . Format n<d_1,d_2,...,d_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>doubleArrayFromSimpleString</code>.
     *
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static List doubleArrayFromString(String sArrays, String separator,
        int size)
    {
        String tmpString;
        int endOfArray;
        int startOfArray;
        int arrayLength = 0;

        startOfArray = sArrays.indexOf('<');

        if (startOfArray == -1)
        {
            return null;
        }

        if (size == -1)
        {
            tmpString = sArrays.substring(0, startOfArray);
            arrayLength = Integer.parseInt(tmpString);
        }
        else
        {
            arrayLength = size;
        }

        tmpString = sArrays.substring(startOfArray, sArrays.length());

        StringTokenizer st = new StringTokenizer(tmpString, separator);
        int arrayIndex = 0;
        int index = 0;
        double[] array = new double[arrayLength];
        Vector tmpVector = new Vector();
        tmpVector.add(array);

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();

            if (tmpString.charAt(0) == '<')
            {
                tmpString = tmpString.substring(1, tmpString.length());
            }
            else if ((endOfArray = tmpString.lastIndexOf('>')) != -1)
            {
                // add last index entry from this array
                array[index] = Double.parseDouble(tmpString.substring(0,
                            endOfArray));

                // and get the first entry from the new one
                startOfArray = tmpString.indexOf('<');

                if (startOfArray == -1)
                {
                    return tmpVector;
                }

                arrayIndex++;
                arrayLength = Integer.parseInt(tmpString.substring(
                            endOfArray + 1, startOfArray).trim());
                array = new double[arrayLength];
                tmpVector.add(array);
                index = 0;
                tmpString = tmpString.substring(startOfArray + 1,
                        tmpString.length());
            }

            if (index < array.length)
            {
                array[index] = Double.parseDouble(tmpString);
            }
            else
            {
                //        throw new ArrayIndexOutOfBoundsException("int array at Vector index "+arrayIndex+" is out of range ("+index+").");
                logger.error("double array at Vector index " + arrayIndex +
                    " is out of range.");

                return null;
            }

            index++;
        }

        return tmpVector;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicArrayHelper instance()
    {
        if (arrayHelper == null)
        {
            arrayHelper = new BasicArrayHelper();
        }

        return arrayHelper;
    }

    /**
     *  Loads integer array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>intArrayFromString</code>.
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static int[] intArrayFromSimpleString(String sArrays,
        String separator)
    {
        StringTokenizer st = new StringTokenizer(sArrays, separator);

        // start with a vector of size 50
        Vector tmpVector = new Vector(50);

        // get integer values from String
        String tmpString;

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();
            tmpVector.add(new Integer(tmpString));
        }

        // copy values to integer array
        int size = tmpVector.size();
        int[] array = new int[size];

        for (int i = 0; i < size; i++)
        {
            array[i] = ((Integer) tmpVector.get(i)).intValue();
        }

        return array;
    }

    /**
     *  Loads integer array from <tt>String</tt> . Format n<i_1,i_2,...,i_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>intArrayFromSimpleString</code>.
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static List intArrayFromString(String sArrays, String separator,
        int size)
    {
        String tmpString;
        int endOfArray;
        int startOfArray;
        int arrayLength = 0;

        startOfArray = sArrays.indexOf('<');

        if (startOfArray == -1)
        {
            return null;
        }

        if (size == -1)
        {
            tmpString = sArrays.substring(0, startOfArray);
            arrayLength = Integer.parseInt(tmpString);
        }
        else
        {
            arrayLength = size;
        }

        tmpString = sArrays.substring(startOfArray, sArrays.length());

        StringTokenizer st = new StringTokenizer(tmpString, separator);
        int arrayIndex = 0;
        int index = 0;
        int[] array = new int[arrayLength];
        Vector tmpVector = new Vector();
        tmpVector.add(array);

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();

            if (tmpString.charAt(0) == '<')
            {
                tmpString = tmpString.substring(1, tmpString.length());
            }
            else if ((endOfArray = tmpString.lastIndexOf('>')) != -1)
            {
                // add last index entry from this array
                array[index] = Integer.parseInt(tmpString.substring(0,
                            endOfArray));

                // and get the first entry from the new one
                startOfArray = tmpString.indexOf('<');

                if (startOfArray == -1)
                {
                    return tmpVector;
                }

                arrayIndex++;
                arrayLength = Integer.parseInt(tmpString.substring(
                            endOfArray + 1, startOfArray).trim());
                array = new int[arrayLength];
                tmpVector.add(array);
                index = 0;
                tmpString = tmpString.substring(startOfArray + 1,
                        tmpString.length());
            }

            if (index < array.length)
            {
                array[index] = Integer.parseInt(tmpString);
            }
            else
            {
                //        throw new ArrayIndexOutOfBoundsException("int array at Vector index "+arrayIndex+" is out of range ("+index+").");
                logger.error("int array at Vector index " + arrayIndex +
                    " is out of range.");

                return null;
            }

            index++;
        }

        return tmpVector;
    }

    /**
     *  Loads String array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>stringArrayFromString</code>.
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static String[] stringArrayFromSimpleString(String sArrays,
        String separator)
    {
        StringTokenizer st = new StringTokenizer(sArrays, separator);

        // start with a vector of size 50
        Vector tmpVector = new Vector(50);

        // get integer values from String
        String tmpString;

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();
            tmpVector.add(tmpString);
        }

        // copy values to integer array
        int size = tmpVector.size();
        String[] array = new String[size];

        for (int i = 0; i < size; i++)
        {
            array[i] = (String) tmpVector.get(i);
        }

        return array;
    }

    /**
     *  Loads double array from <tt>String</tt> . Format n<d_1,d_2,...,d_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>doubleArrayFromSimpleString</code>.
     *
     *
     * @param  separator  Description of the Parameter
     * @param  sArrays    Description of the Parameter
     * @param  size       Description of the Parameter
     * @return            Description of the Return Value
     */
    public static List stringArrayFromString(String sArrays, String separator,
        int size)
    {
        String tmpString;
        int endOfArray;
        int startOfArray;
        int arrayLength = 0;

        startOfArray = sArrays.indexOf('<');

        if (startOfArray == -1)
        {
            return null;
        }

        if (size == -1)
        {
            tmpString = sArrays.substring(0, startOfArray);
            arrayLength = Integer.parseInt(tmpString);
        }
        else
        {
            arrayLength = size;
        }

        tmpString = sArrays.substring(startOfArray, sArrays.length());

        StringTokenizer st = new StringTokenizer(tmpString, separator);
        int arrayIndex = 0;
        int index = 0;
        String[] array = new String[arrayLength];
        Vector tmpVector = new Vector();
        tmpVector.add(array);

        while (st.hasMoreTokens())
        {
            tmpString = st.nextToken();

            if (tmpString.charAt(0) == '<')
            {
                tmpString = tmpString.substring(1, tmpString.length());
            }
            else if ((endOfArray = tmpString.lastIndexOf('>')) != -1)
            {
                // add last index entry from this array
                array[index] = tmpString.substring(0, endOfArray);

                // and get the first entry from the new one
                startOfArray = tmpString.indexOf('<');

                if (startOfArray == -1)
                {
                    return tmpVector;
                }

                arrayIndex++;
                arrayLength = Integer.parseInt(tmpString.substring(
                            endOfArray + 1, startOfArray).trim());
                array = new String[arrayLength];
                tmpVector.add(array);
                index = 0;
                tmpString = tmpString.substring(startOfArray + 1,
                        tmpString.length());
            }

            if (index < array.length)
            {
                array[index] = tmpString;
            }
            else
            {
                //        throw new ArrayIndexOutOfBoundsException("int array at Vector index "+arrayIndex+" is out of range ("+index+").");
                logger.error("double array at Vector index " + arrayIndex +
                    " is out of range.");

                return null;
            }

            index++;
        }

        return tmpVector;
    }

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  separator    Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb, int[] array,
        String separator)
    {
        if (array == null)
        {
            logger.warn("Empty array.");

            return sb;
        }

        for (int i = 0; i < array.length; i++)
        {
            sb.append(array[i]);

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        return sb;
    }

    /**
     *  Write String array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  separator    Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb, String[] array,
        String separator)
    {
        if (array == null)
        {
            logger.warn("Empty array.");

            return sb;
        }

        for (int i = 0; i < array.length; i++)
        {
            sb.append(array[i]);

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        return sb;
    }

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  separator    Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb, double[] array,
        String separator)
    {
        return toSimpleString(sb, array, separator, null);
    }

    /**
     *  Write boolean array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  separator    Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb, boolean[] array,
        String separator)
    {
        if (array == null)
        {
            logger.warn("Empty array.");

            return sb;
        }

        for (int i = 0; i < array.length; i++)
        {
            if (array[i])
            {
                sb.append('1');
            }
            else
            {
                sb.append('0');
            }

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        return sb;
    }

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  separator    Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb, double[] array,
        String separator, DecimalFormatter format)
    {
        if (array == null)
        {
            logger.warn("Empty array.");

            return sb;
        }

        for (int i = 0; i < array.length; i++)
        {
            if (format == null)
            {
                sb.append(array[i]);
            }
            else
            {
                sb.append(format.format(array[i]));
            }

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        return sb;
    }

    /**
     *  Write integer array to <tt>String</tt> . Format n<i_1,i_2,...,i_n>. ','
     *  is here the default separator.
     *
     * @param  separator    Description of the Parameter
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, int[] array,
        String separator, boolean writeLength)
    {
        if (array == null)
        {
            logger.warn("Empty array.");

            return sb;
        }

        if (writeLength)
        {
            sb.append(array.length);
        }

        sb.append("<");

        for (int i = 0; i < array.length; i++)
        {
            sb.append(array[i]);

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        sb.append(">");

        return sb;
    }

    /**
     *  Write String array to <tt>String</tt> . Format n<i_1,i_2,...,i_n>. ','
     *  is here the default separator.
     *
     * @param  separator    Description of the Parameter
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, String[] array,
        String separator, boolean writeLength)
    {
        if (array == null)
        {
            logger.warn("Empty array.");

            return sb;
        }

        if (writeLength)
        {
            sb.append(array.length);
        }

        sb.append("<");

        for (int i = 0; i < array.length; i++)
        {
            sb.append(array[i]);

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        sb.append(">");

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  separator    Description of the Parameter
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[] array,
        String separator, boolean writeLength)
    {
        return toString(sb, array, separator, writeLength, null);
    }

    /**
     *  Description of the Method
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  separator    Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, boolean[] array,
        String separator, boolean writeLength)
    {
        if (array == null)
        {
            logger.warn("Empty array.");

            return sb;
        }

        if (writeLength)
        {
            sb.append(array.length);
        }

        sb.append("<");

        for (int i = 0; i < array.length; i++)
        {
            sb.append((array[i]) ? '1' : '0');

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        sb.append(">");

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  separator    Description of the Parameter
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[] array,
        String separator, boolean writeLength, DecimalFormatter format)
    {
        if (array == null)
        {
            logger.warn("Empty array.");

            return sb;
        }

        if (writeLength)
        {
            sb.append(array.length);
        }

        sb.append("<");

        for (int i = 0; i < array.length; i++)
        {
            if (format == null)
            {
                sb.append(array[i]);
            }
            else
            {
                sb.append(format.format(array[i]));
            }

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        sb.append(">");

        return sb;
    }

    /**
     *  Write boolean array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  separator    Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toTrueFalseString(StringBuffer sb,
        boolean[] array, String separator)
    {
        for (int i = 0; i < array.length; i++)
        {
            sb.append(array[i]);

            if (i < (array.length - 1))
            {
                sb.append(separator);
            }
        }

        return sb;
    }

    public boolean[] booleanArrayFromSimpleString(String sArrays)
    {
        return booleanArrayFromSimpleString(sArrays, separator);
    }

    /**
     *  Loads boolean array from <tt>String</tt> . Format n<b_1,b_2,...,b_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>booleanArrayFromSimpleString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public List booleanArrayFromString(String sArrays)
    {
        return booleanArrayFromString(sArrays, separator, -1);
    }

    public boolean[] booleanArrayFromTrueFalseString(String sArrays)
    {
        return booleanArrayFromTrueFalseString(sArrays, separator);
    }

    /**
     *  Loads integer array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>intArrayFromString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public double[] doubleArrayFromSimpleString(String sArrays)
    {
        return doubleArrayFromSimpleString(sArrays, separator);
    }

    /**
     *  Loads double array from <tt>String</tt> . Format n<d_1,d_2,...,d_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>doubleArrayFromSimpleString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public List doubleArrayFromString(String sArrays)
    {
        return doubleArrayFromString(sArrays, separator, -1);
    }

    /**
     *  Gets the separator attribute of the ArrayHelper object
     *
     * @return    The separator value
     */
    public String getSeparator()
    {
        return separator;
    }

    /**
     *  Loads integer array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>intArrayFromString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public int[] intArrayFromSimpleString(String sArrays)
    {
        return intArrayFromSimpleString(sArrays, separator);
    }

    /**
     *  Loads integer array from <tt>String</tt> . Format n<i_1,i_2,...,i_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>intArrayFromSimpleString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public List intArrayFromString(String sArrays)
    {
        return intArrayFromString(sArrays, separator, -1);
    }

    /**
     *  Sets the separator attribute of the ArrayHelper object
     *
     * @param  _separator  The new separator value
     */
    public void setSeparator(String _separator)
    {
        separator = _separator;
    }

    /**
     *  Loads String array from <tt>String</tt> . Format n<b_1,b_2,...,b_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>stringArrayFromSimpleString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public List stringArrayFromString(String sArrays)
    {
        return stringArrayFromString(sArrays, separator, -1);
    }

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb         Description of the Parameter
     * @param  arrayrray  Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, int[] arrayrray)
    {
        return toSimpleString(sb, arrayrray, separator);
    }

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb         Description of the Parameter
     * @param  arrayrray  Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, double[] arrayrray)
    {
        return toSimpleString(sb, arrayrray, separator);
    }

    /**
     *  Write boolean array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb         Description of the Parameter
     * @param  arrayrray  Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, boolean[] arrayrray)
    {
        return toSimpleString(sb, arrayrray, separator);
    }

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb         Description of the Parameter
     * @param  arrayrray  Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, double[] arrayrray,
        DecimalFormatter format)
    {
        return toSimpleString(sb, arrayrray, separator, format);
    }

    /**
     *  Write integer array to <tt>String</tt> . Format n<i_1,i_2, ...,i_n>. ','
     *  is here the default separator.
     *
     * @param  arrayrray  Description of the Parameter
     * @param  sb         Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, int[] arrayrray)
    {
        return toString(sb, arrayrray, separator, true);
    }

    /**
     *  Write String array to <tt>String</tt> . Format n<i_1,i_2, ...,i_n>. ','
     *  is here the default separator.
     *
     * @param  arrayrray  Description of the Parameter
     * @param  sb         Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, String[] arrayrray)
    {
        return toString(sb, arrayrray, separator, true);
    }

    /**
     *  Description of the Method
     *
     * @param  dArray  Description of the Parameter
     * @param  sb      Description of the Parameter
     * @return         Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, double[] dArray)
    {
        return toString(sb, dArray, separator, true);
    }

    /**
     *  Description of the Method
     *
     * @param  sb      Description of the Parameter
     * @param  dArray  Description of the Parameter
     * @return         Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, boolean[] dArray)
    {
        return toString(sb, dArray, separator, true);
    }

    /**
     *  Description of the Method
     *
     * @param  dArray  Description of the Parameter
     * @param  sb      Description of the Parameter
     * @return         Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, double[] dArray,
        DecimalFormatter format)
    {
        return toString(sb, dArray, separator, true, format);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
