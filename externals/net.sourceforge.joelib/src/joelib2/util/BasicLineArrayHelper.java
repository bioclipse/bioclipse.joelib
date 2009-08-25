///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicLineArrayHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
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

import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Helper methods for writing and loading line arrays.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:41 $
 */
public class BasicLineArrayHelper implements LineArrayHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicLineArrayHelper.class.getName());
    private static BasicLineArrayHelper lineArrayHelper;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOERandom object
     */
    private BasicLineArrayHelper()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public static List booleanArrayFromString(String sArrays)
    {
        StringReader sr = new StringReader(sArrays);

        return booleanArrayFromString(sr, -1);
    }

    /**
     *  Description of the Method
     *
     * @param  reader  Description of the Parameter
     * @return         Description of the Return Value
     */
    public static List booleanArrayFromString(Reader reader)
    {
        return booleanArrayFromString(reader, -1);
    }

    /**
     *  Description of the Method
     *
     * @param  sArrays  Description of the Parameter
     * @param  size     Description of the Parameter
     * @return          Description of the Return Value
     */
    public static List booleanArrayFromString(String sArrays, int size)
    {
        StringReader sr = new StringReader(sArrays);

        return booleanArrayFromString(sr, size);
    }

    /**
     * Gets boolean arrays from a <tt>Reader</tt>.
     *
     * @param  reader  the <tt>Reader</tt>
     * @param  size    <tt>-1</tt> if the size should be readed from the first entry in the <tt>Reader</tt>
     * @return         A vector with the result arrays
     */
    public static List booleanArrayFromString(Reader reader, int size)
    {
        LineNumberReader lnr = null;

        if (lnr instanceof LineNumberReader)
        {
            lnr = (LineNumberReader) reader;
        }
        else
        {
            lnr = new LineNumberReader(reader);
        }

        try
        {
            String line;
            int arrayLength = -1;

            if (size != -1)
            {
                arrayLength = size;
            }
            else
            {
                line = lnr.readLine();
                arrayLength = Integer.parseInt(line);
            }

            boolean[] array = new boolean[arrayLength];
            Vector tmpVector = new Vector();
            tmpVector.add(array);

            int index = 0;

            while (true)
            {
                line = lnr.readLine();

                if ((line != null) && (index < array.length))
                {
                    if (line.charAt(0) == '0')
                    {
                        array[index] = false;
                    }
                    else if (line.charAt(0) == '1')
                    {
                        array[index] = true;
                    }
                    else
                    {
                        array[index] = (Boolean.valueOf(line)).booleanValue();
                    }

                    if (index < (array.length - 1))
                    {
                        index++;
                    }
                    else
                    {
                        // read new array
                        line = lnr.readLine();

                        if (line != null)
                        {
                            arrayLength = Integer.parseInt(line);
                            array = new boolean[arrayLength];
                            tmpVector.add(array);
                            index = 0;
                        }
                        else
                        {
                            return tmpVector;
                        }
                    }
                }
                else
                {
                    logger.error("int array at index " + index +
                        " is out of range.");

                    return null;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Gets boolean arrays from a <tt>Reader</tt>.
     *
     * @param  reader  the <tt>Reader</tt>
     * @param  size    <tt>-1</tt> if the size should be readed from the first entry in the <tt>Reader</tt>
     * @return         A vector with the result arrays
     */
    public static List booleanArrayFromString(StringTokenizer st, int size)
    {
        if (st == null)
        {
            return null;
        }

        try
        {
            String line;
            int arrayLength = -1;

            if (size != -1)
            {
                arrayLength = size;
            }
            else
            {
                line = st.nextToken();
                arrayLength = Integer.parseInt(line);
            }

            boolean[] array = new boolean[arrayLength];
            Vector tmpVector = new Vector();
            tmpVector.add(array);

            int index = 0;

            while (true)
            {
                line = st.nextToken();

                if ((line != null) && (index < array.length))
                {
                    if (line.charAt(0) == '0')
                    {
                        array[index] = false;
                    }
                    else if (line.charAt(0) == '1')
                    {
                        array[index] = true;
                    }
                    else
                    {
                        array[index] = (Boolean.valueOf(line)).booleanValue();
                    }

                    if (index < (array.length - 1))
                    {
                        index++;
                    }
                    else
                    {
                        // read new array
                        if (st.hasMoreTokens())
                        {
                            line = st.nextToken();
                            arrayLength = Integer.parseInt(line);
                            array = new boolean[arrayLength];
                            tmpVector.add(array);
                            index = 0;
                        }
                        else
                        {
                            return tmpVector;
                        }
                    }
                }
                else
                {
                    logger.error("int array at index " + index +
                        " is out of range.");

                    return null;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public static List doubleArrayFromString(String sArrays)
    {
        StringReader sr = new StringReader(sArrays);

        return doubleArrayFromString(sr, -1);
    }

    /**
     *  Description of the Method
     *
     * @param  reader  Description of the Parameter
     * @return         Description of the Return Value
     */
    public static List doubleArrayFromString(Reader reader)
    {
        return doubleArrayFromString(reader, -1);
    }

    /**
     *  Description of the Method
     *
     * @param  sArrays  Description of the Parameter
     * @param  size     Description of the Parameter
     * @return          Description of the Return Value
     */
    public static List doubleArrayFromString(String sArrays, int size)
    {
        StringReader sr = new StringReader(sArrays);

        return doubleArrayFromString(sr, size);
    }

    /**
     * Gets double arrays from a <tt>Reader</tt>.
     *
     * @param  reader  the <tt>Reader</tt>
     * @param  size    <tt>-1</tt> if the size should be readed from the first entry in the <tt>Reader</tt>
     * @return         A vector with the result arrays
     */
    public static List doubleArrayFromString(Reader reader, int size)
    {
        LineNumberReader lnr = null;

        if (lnr instanceof LineNumberReader)
        {
            lnr = (LineNumberReader) reader;
        }
        else
        {
            lnr = new LineNumberReader(reader);
        }

        try
        {
            String line;
            int arrayLength = -1;

            if (size != -1)
            {
                arrayLength = size;
            }
            else
            {
                line = lnr.readLine();
                arrayLength = Integer.parseInt(line);
            }

            double[] array = new double[arrayLength];
            Vector tmpVector = new Vector();
            tmpVector.add(array);

            int index = 0;

            while (true)
            {
                line = lnr.readLine();

                if ((line != null) && (index < array.length))
                {
                    array[index] = Double.parseDouble(line);

                    if (index < (array.length - 1))
                    {
                        index++;
                    }
                    else
                    {
                        // read new array
                        line = lnr.readLine();

                        if (line != null)
                        {
                            arrayLength = Integer.parseInt(line);
                            array = new double[arrayLength];
                            tmpVector.add(array);
                            index = 0;
                        }
                        else
                        {
                            return tmpVector;
                        }
                    }
                }
                else
                {
                    logger.error("double array at index " + index +
                        " is out of range.");

                    return null;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Gets double arrays from a <tt>Reader</tt>.
     *
     * @param  reader  the <tt>Reader</tt>
     * @param  size    <tt>-1</tt> if the size should be readed from the first entry in the <tt>Reader</tt>
     * @return         A vector with the result arrays
     */
    public static List doubleArrayFromString(StringTokenizer st, int size)
    {
        if (st == null)
        {
            return null;
        }

        try
        {
            String line;
            int arrayLength = -1;

            if (size != -1)
            {
                arrayLength = size;
            }
            else
            {
                line = st.nextToken();
                arrayLength = Integer.parseInt(line);
            }

            double[] array = new double[arrayLength];
            Vector tmpVector = new Vector();
            tmpVector.add(array);

            int index = 0;

            while (true)
            {
                line = st.nextToken();

                if ((line != null) && (index < array.length))
                {
                    array[index] = Double.parseDouble(line);

                    if (index < (array.length - 1))
                    {
                        index++;
                    }
                    else
                    {
                        // read new array
                        if (st.hasMoreTokens())
                        {
                            line = st.nextToken();
                            arrayLength = Integer.parseInt(line);
                            array = new double[arrayLength];
                            tmpVector.add(array);
                            index = 0;
                        }
                        else
                        {
                            return tmpVector;
                        }
                    }
                }
                else
                {
                    logger.error("double array at index " + index +
                        " is out of range.");

                    return null;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicLineArrayHelper instance()
    {
        if (lineArrayHelper == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " + BasicLineArrayHelper.class.getName() +
                    " instance.");
            }

            lineArrayHelper = new BasicLineArrayHelper();
        }

        return lineArrayHelper;
    }

    /**
     *  Description of the Method
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public static List intArrayFromString(String sArrays)
    {
        StringReader sr = new StringReader(sArrays);

        return intArrayFromString(sr, -1);
    }

    /**
     *  Description of the Method
     *
     * @param  reader  Description of the Parameter
     * @return         Description of the Return Value
     */
    public static List intArrayFromString(Reader reader)
    {
        return intArrayFromString(reader, -1);
    }

    /**
     *  Description of the Method
     *
     * @param  sArrays  Description of the Parameter
     * @param  size     Description of the Parameter
     * @return          Description of the Return Value
     */
    public static List intArrayFromString(String sArrays, int size)
    {
        StringReader sr = new StringReader(sArrays);

        return intArrayFromString(sr, size);
    }

    /**
     * Gets int arrays from a <tt>Reader</tt>.
     *
     * @param  reader  the <tt>Reader</tt>
     * @param  size    <tt>-1</tt> if the size should be readed from the first entry in the <tt>Reader</tt>
     * @return         A vector with the result arrays
     */
    public static List intArrayFromString(Reader reader, int size)
    {
        LineNumberReader lnr = null;

        if (lnr instanceof LineNumberReader)
        {
            lnr = (LineNumberReader) reader;
        }
        else
        {
            lnr = new LineNumberReader(reader);
        }

        try
        {
            String line;
            int arrayLength = -1;

            if (size != -1)
            {
                arrayLength = size;
            }
            else
            {
                line = lnr.readLine();
                arrayLength = Integer.parseInt(line);
            }

            int[] array = new int[arrayLength];
            Vector tmpVector = new Vector();
            tmpVector.add(array);

            int index = 0;

            while (true)
            {
                line = lnr.readLine();

                if ((line != null) && (index < array.length))
                {
                    array[index] = Integer.parseInt(line);

                    if (index < (array.length - 1))
                    {
                        index++;
                    }
                    else
                    {
                        // read new array
                        line = lnr.readLine();

                        if (line != null)
                        {
                            arrayLength = Integer.parseInt(line);
                            array = new int[arrayLength];
                            tmpVector.add(array);
                            index = 0;
                        }
                        else
                        {
                            return tmpVector;
                        }
                    }
                }
                else
                {
                    logger.error("int array at index " + index +
                        " is out of range.");

                    return null;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Gets int arrays from a <tt>Reader</tt>.
     *
     * @param  reader  the <tt>Reader</tt>
     * @param  size    <tt>-1</tt> if the size should be readed from the first entry in the <tt>Reader</tt>
     * @return         A vector with the result arrays
     */
    public static List intArrayFromString(StringTokenizer st, int size)
    {
        if (st == null)
        {
            return null;
        }

        try
        {
            String line;
            int arrayLength = -1;

            if (size != -1)
            {
                arrayLength = size;
            }
            else
            {
                line = st.nextToken();
                arrayLength = Integer.parseInt(line);
            }

            int[] array = new int[arrayLength];
            Vector tmpVector = new Vector();
            tmpVector.add(array);

            int index = 0;

            while (true)
            {
                line = st.nextToken();

                if ((line != null) && (index < array.length))
                {
                    array[index] = Integer.parseInt(line);

                    if (index < (array.length - 1))
                    {
                        index++;
                    }
                    else
                    {
                        // read new array
                        if (st.hasMoreTokens())
                        {
                            line = st.nextToken();
                            arrayLength = Integer.parseInt(line);
                            array = new int[arrayLength];
                            tmpVector.add(array);
                            index = 0;
                        }
                        else
                        {
                            return tmpVector;
                        }
                    }
                }
                else
                {
                    logger.error("int array at index " + index +
                        " is out of range.");

                    return null;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public static List stringArrayFromString(String sArrays)
    {
        StringReader sr = new StringReader(sArrays);

        return stringArrayFromString(sr, -1);
    }

    /**
     *  Description of the Method
     *
     * @param  reader  Description of the Parameter
     * @return         Description of the Return Value
     */
    public static List stringArrayFromString(Reader reader)
    {
        return stringArrayFromString(reader, -1);
    }

    /**
     *  Description of the Method
     *
     * @param  sArrays  Description of the Parameter
     * @param  size     Description of the Parameter
     * @return          Description of the Return Value
     */
    public static List stringArrayFromString(String sArrays, int size)
    {
        StringReader sr = new StringReader(sArrays);

        return stringArrayFromString(sr, size);
    }

    /**
     * Gets int arrays from a <tt>Reader</tt>.
     *
     * @param  reader  the <tt>Reader</tt>
     * @param  size    <tt>-1</tt> if the size should be readed from the first entry in the <tt>Reader</tt>
     * @return         A vector with the result arrays
     */
    public static List stringArrayFromString(Reader reader, int size)
    {
        LineNumberReader lnr = null;

        if (lnr instanceof LineNumberReader)
        {
            lnr = (LineNumberReader) reader;
        }
        else
        {
            lnr = new LineNumberReader(reader);
        }

        try
        {
            String line;
            int arrayLength = -1;

            if (size != -1)
            {
                arrayLength = size;
            }
            else
            {
                line = lnr.readLine();
                arrayLength = Integer.parseInt(line);
            }

            String[] array = new String[arrayLength];
            Vector tmpVector = new Vector();
            tmpVector.add(array);

            int index = 0;

            while (true)
            {
                line = lnr.readLine();

                if ((line != null) && (index < array.length))
                {
                    array[index] = line;

                    if (index < (array.length - 1))
                    {
                        index++;
                    }
                    else
                    {
                        // read new array
                        line = lnr.readLine();

                        if (line != null)
                        {
                            arrayLength = Integer.parseInt(line);
                            array = new String[arrayLength];
                            tmpVector.add(array);
                            index = 0;
                        }
                        else
                        {
                            return tmpVector;
                        }
                    }
                }
                else
                {
                    logger.error("String array at index " + index +
                        " is out of range.");

                    return null;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  sb     Description of the Parameter
     * @param  array  Description of the Parameter
     * @return        Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, int[] array)
    {
        return toString(sb, array, true);
    }

    /**
     *  Description of the Method
     *
     * @param  sb     Description of the Parameter
     * @param  array  Description of the Parameter
     * @return        Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, String[] array)
    {
        return toString(sb, array, true);
    }

    /**
     *  Description of the Method
     *
     * @param  sb     Description of the Parameter
     * @param  array  Description of the Parameter
     * @return        Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[] array)
    {
        return toString(sb, array, true);
    }

    /**
     *  Description of the Method
     *
     * @param  sb     Description of the Parameter
     * @param  array  Description of the Parameter
     * @return        Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, boolean[] array)
    {
        return toString(sb, array, true);
    }

    /**
     *  Description of the Method
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, int[] array,
        boolean writeLength)
    {
        if (writeLength)
        {
            sb.append(array.length);
            sb.append(HelperMethods.eol);
        }

        int l_1 = array.length - 1;

        for (int i = 0; i < array.length; i++)
        {
            sb.append(array[i]);

            if (i < l_1)
            {
                sb.append(HelperMethods.eol);
            }
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, String[] array,
        boolean writeLength)
    {
        if (writeLength)
        {
            sb.append(array.length);
            sb.append(HelperMethods.eol);
        }

        int l_1 = array.length - 1;

        for (int i = 0; i < array.length; i++)
        {
            sb.append(array[i]);

            if (i < l_1)
            {
                sb.append(HelperMethods.eol);
            }
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb     Description of the Parameter
     * @param  array  Description of the Parameter
     * @return        Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[] array,
        DecimalFormatter format)
    {
        return toString(sb, array, true, format);
    }

    /**
     *  Description of the Method
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[] array,
        boolean writeLength)
    {
        return toString(sb, array, writeLength, null);
    }

    /**
     *  Description of the Method
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, boolean[] array,
        boolean writeLength)
    {
        if (writeLength)
        {
            sb.append(array.length);
            sb.append(HelperMethods.eol);
        }

        int l_1 = array.length - 1;

        for (int i = 0; i < array.length; i++)
        {
            sb.append((array[i]) ? '1' : '0');

            if (i < l_1)
            {
                sb.append(HelperMethods.eol);
            }
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb           Description of the Parameter
     * @param  array        Description of the Parameter
     * @param  writeLength  Description of the Parameter
     * @return              Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[] array,
        boolean writeLength, DecimalFormatter format)
    {
        if (array == null)
        {
            sb.append(0);
            sb.append(HelperMethods.eol);

            return sb;
        }

        if (writeLength)
        {
            sb.append(array.length);
            sb.append(HelperMethods.eol);
        }

        int l_1 = array.length - 1;

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

            if (i < l_1)
            {
                sb.append(HelperMethods.eol);
            }
        }

        return sb;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
