///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DynamicArrayResult.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.13 $
//            $Date: 2005/02/17 16:48:30 $
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
package joelib2.feature.result;

import joelib2.feature.FeatureResult;
import joelib2.feature.NumberFormatResult;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.io.types.cml.ResultCMLProperties;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicArrayHelper;
import joelib2.util.BasicLineArrayHelper;
import joelib2.util.HelperMethods;

import wsi.ra.text.DecimalFormatHelper;
import wsi.ra.text.DecimalFormatter;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

import java.util.List;

import org.apache.log4j.Category;


/**
 * Dynamic array results of variable size.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.13 $, $Date: 2005/02/17 16:48:30 $
 */
public class DynamicArrayResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, ResultCMLProperties, NumberFormatResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            DynamicArrayResult.class.getName());

    private static final String UNKNOWN = "?";

    /**
     * Description of the Field
     */
    public final static String INT = "int";

    /**
     * Description of the Field
     */
    public final static String DOUBLE = "double";

    /**
     * Description of the Field
     */
    public final static String BOOLEAN = "boolean";

    private final static String basicFormat = "propertyType\n" +
        "dataDescription\n" + "dataUnit\n" + "dataType\n" +
        "n<e0,...e(n-1)>\n" + "with propertyType  of type String" +
        "with dataDescription of type String" + "with dataUnit of type String" +
        "with dataType = <" + INT + " ," + DOUBLE + " ," + BOOLEAN + ">" +
        "with n of type 32-bit integer" +
        "with e0,...,e(n-1) of type 32-bit integer or 64-bit floating point value IEEE 754 or boolean value <0,1>";

    private final static String lineFormat = "propertyType\n" +
        "dataDescription\n" + "dataUnit\n" + "dataType\n" + "n\n" + "e0\n" +
        "...\n" + "e(n-1)>\n" + "with propertyType  of type String" +
        "with dataDescription of type String" + "with dataUnit of type String" +
        "with dataType = <" + INT + " ," + DOUBLE + " ," + BOOLEAN + ">" +
        "with n of type 32-bit integer" +
        "with e0,...,e(n-1) of type 32-bit integer or 64-bit floating point value IEEE 754 or boolean value <0,1>";

    //~ Instance fields ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////

    /**
     * Description of the Field
     */
    protected Object array;

    /**
     * Description of the Field
     */
    protected String dataDescription;

    /**
     * Description of the Field
     */
    protected String dataUnit;

    /**
     * Description of the Field
     */
    protected String propertyType;

    //~ Constructors ///////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////

    /**
     * Constructor for the IntArrayResult object
     */
    public DynamicArrayResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
        propertyType = dataDescription = dataUnit = UNKNOWN;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////

    /**
     * Gets the newArray attribute of the DynamicArrayResult class
     *
     * @param type
     *            Description of the Parameter
     * @param size
     *            Description of the Parameter
     * @return The newArray value
     */
    public static Object getNewArray(String type, int size)
    {
        Object newArray = null;

        if (type.equals(INT))
        {
            newArray = new int[size];
        }
        else if (type.equals(DOUBLE))
        {
            newArray = new double[size];
        }
        else if (type.equals(BOOLEAN))
        {
            newArray = new boolean[size];
        }

        return newArray;
    }

    public Object clone()
    {
        DynamicArrayResult newObj = new DynamicArrayResult();

        int size = -1;

        if (array instanceof double[])
        {
            size = ((double[]) this.array).length;
            newObj.array = new double[size];
        }
        else if (array instanceof int[])
        {
            size = ((int[]) this.array).length;
            newObj.array = new int[size];
        }
        else if (array instanceof boolean[])
        {
            size = ((boolean[]) this.array).length;
            newObj.array = new boolean[size];
        }

        return clone(newObj, size);
    }

    public DynamicArrayResult clone(DynamicArrayResult other, int size)
    {
        super.clone(other);
        other.propertyType = this.propertyType;
        other.dataDescription = this.dataDescription;
        other.dataUnit = this.dataDescription;
        System.arraycopy(this.array, 0, other.array, 0, size);

        return other;
    }

    /**
     * Description of the Method
     *
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public String formatDescription(IOType ioType)
    {
        String format = basicFormat;

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            format = lineFormat;
        }

        return format;
    }

    /**
     * Description of the Method
     *
     * @param pairData
     *            Description of the Parameter
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean fromPairData(IOType ioType, PairData pairData)
    {
        this.setKey(pairData.getKey());

        Object value = pairData.getKeyValue();
        boolean success = false;

        if ((value != null) && (value instanceof String))
        {
            success = fromString(ioType, (String) value);
        }

        return success;
    }

    /**
     * Description of the Method
     *
     * @param ioType
     *            Description of the Parameter
     * @param sValue
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean fromString(IOType ioType, String sValue)
    {
        return fromString(ioType, sValue, null);
    }

    /**
     * Gets the array attribute of the DynamicArrayResult object
     *
     * @return The array value
     */
    public Object getArray()
    {
        return array;
    }

    public int getArraySize()
    {
        int size = 0;

        if (array instanceof double[])
        {
            size = ((double[]) array).length;
        }
        else if (array instanceof int[])
        {
            size = ((int[]) array).length;
        }
        else if (array instanceof boolean[])
        {
            size = ((boolean[]) array).length;
        }

        return size;
    }

    /**
     * Gets the booleanArray attribute of the DynamicArrayResult object
     *
     * @return The booleanArray value
     */
    public boolean[] getBooleanArray()
    {
        if (array instanceof boolean[])
        {
            return (boolean[]) array;
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the dataDescription attribute of the DynamicArrayResult object
     *
     * @return The dataDescription value
     */
    public String getDataDescription()
    {
        return dataDescription;
    }

    /**
     * Gets the dataUnit attribute of the DynamicArrayResult object
     *
     * @return The dataUnit value
     */
    public String getDataUnit()
    {
        return dataUnit;
    }

    /**
     * Gets the doubleArray attribute of the DynamicArrayResult object
     *
     * @return The doubleArray value
     */
    public double[] getDoubleArray()
    {
        if (array instanceof double[])
        {
            return (double[]) array;
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the intArray attribute of the DynamicArrayResult object
     *
     * @return The intArray value
     */
    public int[] getIntArray()
    {
        if (array instanceof int[])
        {
            return (int[]) array;
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the propertyType attribute of the DynamicArrayResult object
     *
     * @return The propertyType value
     */
    public String getPropertyType()
    {
        return propertyType;
    }

    public int getSize()
    {
        if (array instanceof double[])
        {
            return ((double[]) array).length;
        }
        else if (array instanceof int[])
        {
            return ((int[]) array).length;
        }
        else if (array instanceof boolean[])
        {
            return ((boolean[]) array).length;
        }

        return -1;
    }

    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    /**
     * Sets the array attribute of the DynamicArrayResult object
     *
     * @param newArray
     *            The new array value
     */
    public void setArray(Object newArray)
    {
        array = newArray;
    }

    /**
     * Sets the dataDescription attribute of the DynamicArrayResult object
     *
     * @param _desc
     *            The new dataDescription value
     */
    public void setDataDescription(String _desc)
    {
        if (_desc.trim().equals(""))
        {
            dataDescription = UNKNOWN;
        }
        else
        {
            dataDescription = _desc;
        }
    }

    /**
     * Sets the dataUnit attribute of the DynamicArrayResult object
     *
     * @param _unit
     *            The new dataUnit value
     */
    public void setDataUnit(String _unit)
    {
        if (_unit.trim().equals(""))
        {
            dataUnit = UNKNOWN;
        }
        else
        {
            dataUnit = _unit;
        }
    }

    /**
     * Sets the propertyType attribute of the DynamicArrayResult object
     *
     * @param _pType
     *            The new propertyType value
     */
    public void setPropertyType(String _pType)
    {
        if ((_pType == null) || _pType.trim().equals(""))
        {
            propertyType = UNKNOWN;
        }
        else
        {
            propertyType = _pType;
        }
    }

    /**
     * Description of the Method
     *
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        return toString(ioType, null, DecimalFormatHelper.instance());
    }

    /**
     * Description of the Method
     *
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public String toString(IOType ioType, DecimalFormatter format)
    {
        return toString(ioType, null, format);
    }

    /**
     * Description of the Method
     *
     * @param sValue
     *            Description of the Parameter
     * @param ioType
     *            Description of the Parameter
     * @param defaultArrayType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    private boolean fromString(IOType ioType, String sValue,
        String defaultArrayType)
    {
        //StringTokenizer st = new StringTokenizer(sValue, " \t\n\r");

        if (logger.isDebugEnabled())
        {
            logger.debug("Input type: " + ioType.toString());
        }

        StringReader sr = new StringReader(sValue);
        LineNumberReader lnr = new LineNumberReader(sr);
        boolean success = loadHeader(lnr);

        if (success)
        {
            String arrayType = getArrayType(lnr, defaultArrayType);

            if (arrayType != null)
            {
                List list;

                if (logger.isDebugEnabled())
                {
                    logger.debug("propertyType: " + propertyType);
                    logger.debug("dataDescription: " + dataDescription);
                    logger.debug("dataUnit: " + dataUnit);
                    logger.debug("arrayType: " + arrayType);
                }

                if (ioType.equals(
                            BasicIOTypeHolder.instance().getIOType("SDF")))
                {
                    list = getLineArray(lnr, arrayType);
                }
                else
                {
                    list = getArray(lnr, arrayType);
                }

                if (list == null)
                {
                    success = false;
                }
                else
                {
                    success = true;
                    array = list.get(0);
                }
            }
            else
            {
                success = false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Results parsed: " + success);
        }

        return success;
    }

    /**
     * @param lnr
     * @param arrayType
     * @return
     */
    private List getArray(LineNumberReader lnr, String arrayType)
    {
        List list = null;
        String sArray = null;
        boolean success = false;

        try
        {
            sArray = lnr.readLine();
            success = true;
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
            success = false;
        }

        if (success)
        {
            //                  int index = sValue.indexOf("\n");
            //                  String sArray = sValue.substring(index).trim();
            //System.out.println("DYN-ARRAY:"+sArray);
            if (arrayType.equals(INT))
            {
                list = BasicArrayHelper.instance().intArrayFromString(sArray);
            }
            else if (arrayType.equals(DOUBLE))
            {
                list = BasicArrayHelper.instance().doubleArrayFromString(
                        sArray);
            }
            else if (arrayType.equals(BOOLEAN))
            {
                list = BasicArrayHelper.instance().booleanArrayFromString(
                        sArray);
            }
        }

        return list;
    }

    /**
     * @param lnr
     * @param defaultArrayType
     * @return
     */
    private String getArrayType(LineNumberReader lnr, String defaultArrayType)
    {
        String arrayType = defaultArrayType;

        // get array data type
        if (defaultArrayType == null)
        {
            try
            {
                arrayType = lnr.readLine();
                //arrayType = st.nextToken();
            }
            catch (Exception ex)
            {
                logger.error(ex.getMessage());
                arrayType = null;
            }
        }

        return arrayType;
    }

    /**
     * @param lnr
     * @param arrayType
     * @return
     */
    private List getLineArray(LineNumberReader lnr, String arrayType)
    {
        List list = null;

        if (arrayType.equals(INT))
        {
            list = BasicLineArrayHelper.intArrayFromString(lnr, -1);
        }
        else if (arrayType.equals(DOUBLE))
        {
            list = BasicLineArrayHelper.doubleArrayFromString(lnr, -1);
        }
        else if (arrayType.equals(BOOLEAN))
        {
            list = BasicLineArrayHelper.booleanArrayFromString(lnr, -1);
        }

        return list;
    }

    /**
     * @param lnr
     * @return
     */
    private boolean loadHeader(LineNumberReader lnr)
    {
        boolean success = true;

        // get property type, data description and data unit
        try
        {
            propertyType = lnr.readLine();
            dataDescription = lnr.readLine();
            dataUnit = lnr.readLine();
            //propertyType = st.nextToken();
            //dataDescription = st.nextToken();
            //dataUnit = st.nextToken();
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());
            success = false;
        }

        return success;
    }

    /**
     * Write this array to <tt>String</tt>.
     *
     * @param ioType
     *            Description of the Parameter
     * @param arrayType
     *            a user defined array type should be written or <tt>null
     *      </tt>
     *            for the standard type
     * @return Description of the Return Value
     */
    private String toString(IOType ioType, String arrayType,
        DecimalFormatter format)
    {
        StringBuffer sb = new StringBuffer();

        writeHeader(sb, arrayType);

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            if (array instanceof double[])
            {
                BasicLineArrayHelper.toString(sb, (double[]) array, format);
            }
            else if (array instanceof int[])
            {
                BasicLineArrayHelper.toString(sb, (int[]) array);
            }
            else if (array instanceof boolean[])
            {
                BasicLineArrayHelper.toString(sb, (boolean[]) array);
            }
        }
        else
        {
            BasicArrayHelper ah = BasicArrayHelper.instance();

            if (array instanceof double[])
            {
                ah.toString(sb, (double[]) array, format);
            }
            else if (array instanceof int[])
            {
                ah.toString(sb, (int[]) array);
            }
            else if (array instanceof boolean[])
            {
                ah.toString(sb, (boolean[]) array);
            }
        }

        return sb.toString();
    }

    /**
     * @param sb
     * @param arrayType
     */
    private void writeHeader(StringBuffer sb, String arrayType)
    {
        // write property type, data description and data unit
        sb.append(propertyType);
        sb.append(HelperMethods.eol);
        sb.append(dataDescription);
        sb.append(HelperMethods.eol);

        if (dataUnit.equals(UNKNOWN))
        {
            if (array instanceof double[])
            {
                sb.append("64-bit floating point IEEE 754");
            }
            else if (array instanceof int[])
            {
                sb.append("32-bit integer");
            }
            else if (array instanceof boolean[])
            {
                sb.append("boolean 0,1");
            }
            else
            {
                sb.append(dataUnit);
            }
        }
        else
        {
            sb.append(dataUnit);
        }

        sb.append(HelperMethods.eol);

        // write array data type
        if (arrayType == null)
        {
            if (array instanceof double[])
            {
                sb.append(DOUBLE);
                sb.append(HelperMethods.eol);
            }
            else if (array instanceof int[])
            {
                sb.append(INT);
                sb.append(HelperMethods.eol);
            }
            else if (array instanceof boolean[])
            {
                sb.append(BOOLEAN);
                sb.append(HelperMethods.eol);
            }
        }
        else
        {
            sb.append(arrayType);
            sb.append(HelperMethods.eol);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
