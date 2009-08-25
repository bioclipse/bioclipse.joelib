///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IntResult.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
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
import joelib2.feature.NativeValue;

import joelib2.io.IOType;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import wsi.ra.text.DecimalFormatHelper;

import org.apache.log4j.Category;


/**
 * Integer result.
 * E.g. when normalizing the data, the integers will be stored as double values AND
 * can be also loaded as double values !!!
 * If you use <tt>setDoubleNV</tt> the values will be later stored as double values,
 * when converting to String values !
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:30 $
 */
public class IntResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, NativeValue, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(IntResult.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean storeAsDouble = false;

    /**
     *  Description of the Field
     */
    private double value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntResult object
     */
    public IntResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    public IntResult(String key, int value)
    {
        this.setKey(key);
        this.setKeyValue(this);
        this.value = value;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        IntResult newObj = new IntResult();

        return clone(newObj);
    }

    public IntResult clone(IntResult other)
    {
        super.clone(other);
        other.value = value;
        other.storeAsDouble = storeAsDouble;

        return other;
    }

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof IntResult)
        {
            IntResult other = (IntResult) otherObj;

            if ((other.value == this.value))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String formatDescription(IOType ioType)
    {
        return
            "32-bit integer value. Internal stored as double to allow e.g. normalizations.";
    }

    /**
     *  Description of the Method
     *
     * @param pairData                   Description of the Parameter
     * @param ioType                     Description of the Parameter
     * @return                           Description of the Return Value
     * @exception NumberFormatException  Description of the Exception
     */
    public boolean fromPairData(IOType ioType, PairData pairData)
        throws NumberFormatException
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
     *  Description of the Method
     *
     * @param sValue                     Description of the Parameter
     * @param ioType                     Description of the Parameter
     * @return                           Description of the Return Value
     * @exception NumberFormatException  Description of the Exception
     */
    public boolean fromString(IOType ioType, String sValue)
        throws NumberFormatException
    {
        try
        {
            value = Integer.parseInt(sValue);
            storeAsDouble = false;
        }
        catch (NumberFormatException ex)
        {
            try
            {
                // o.k. let's try to resolve double values
                value = (int) Double.parseDouble(sValue);
                storeAsDouble = true;
            }
            catch (NumberFormatException ex2)
            {
                // show exception if this fails
                logger.error(ex2.toString());
                throw ex;

                //                      return false;
            }
        }

        return true;
    }

    /**
     * Gets the doubleNV attribute of the IntResult object
     *
     * @return   The doubleNV value
     */
    public double getDoubleNV()
    {
        return value;
    }

    /**
     *  Gets the double attribute of the DoubleResult object
     *
     * @return   The double value
     */
    public int getInt()
    {
        return (int) Math.round(value);
    }

    /**
     * Gets the intNV attribute of the IntResult object
     *
     * @return   The intNV value
     */
    public int getIntNV()
    {
        return (int) Math.round(value);
    }

    /**
     * Gets the stringNV attribute of the IntResult object
     *
     * @return   The stringNV value
     */
    public String getStringNV()
    {
        return Integer.toString((int) Math.round(value));
    }

    public int hashCode()
    {
        long bits = Double.doubleToLongBits(value);
        int dh = (int) (bits ^ (bits >>> 32));

        return dh;
    }

    public boolean init(String key)
    {
        this.setKey(key);

        return true;
    }

    /**
     * Gets the doubleNV attribute of the IntResult object
     *
     * @return   The doubleNV value
     */
    public boolean isDoubleNV()
    {
        if (storeAsDouble)
        {
            return true;
        }

        return false;
    }

    /**
     * Gets the intNV attribute of the IntResult object
     *
     * @return   The intNV value
     */
    public boolean isIntNV()
    {
        return true;
    }

    /**
     * Sets the doubleNV attribute of the IntResult object
     *
     * @param _value  The new doubleNV value
     */
    public void setDoubleNV(double _value)
    {
        storeAsDouble = true;
        value = _value;
    }

    /**
     *  Sets the double attribute of the DoubleResult object
     *
     * @param _v  The new double value
     */
    public void setInt(int _v)
    {
        storeAsDouble = false;
        value = _v;
    }

    /**
     * Sets the intNV attribute of the IntResult object
     *
     * @param _value  The new intNV value
     */
    public void setIntNV(int _value)
    {
        storeAsDouble = false;
        value = _value;
    }

    /**
     * Sets the stringNV attribute of the IntResult object
     *
     * @param _value  The new stringNV value
     */
    public void setStringNV(String _value)
    {
        try
        {
            value = Integer.parseInt(_value);
            storeAsDouble = false;
        }
        catch (NumberFormatException ex)
        {
            try
            {
                // o.k. let's try to resolve double values
                value = (int) Double.parseDouble(_value);
                storeAsDouble = true;
            }
            catch (NumberFormatException ex2)
            {
                // show exception if this fails
                logger.error(ex2.toString());

                //throw ex;
                //                      return false;
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        String stringValue;

        if (storeAsDouble)
        {
            stringValue = DecimalFormatHelper.instance().format(value);
        }
        else
        {
            stringValue = Integer.toString((int) Math.round(value));
        }

        return stringValue;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
