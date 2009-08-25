///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DoubleResult.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
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
import joelib2.feature.NumberFormatResult;

import joelib2.io.IOType;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import wsi.ra.text.DecimalFormatHelper;
import wsi.ra.text.DecimalFormatter;

import org.apache.log4j.Category;


/**
 * Double results.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:30 $
 */
public class DoubleResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, NativeValue, NumberFormatResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(DoubleResult.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public double value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DoubleResult object
     */
    public DoubleResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        DoubleResult newObj = new DoubleResult();

        return clone(newObj);
    }

    public DoubleResult clone(DoubleResult other)
    {
        super.clone(other);
        other.value = value;

        return other;
    }

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof DoubleResult)
        {
            DoubleResult other = (DoubleResult) otherObj;

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
        return "64-bit floating point value IEEE 754";
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
            value = Double.parseDouble(sValue);
        }
        catch (NumberFormatException ex)
        {
            // 'Inf' are infinity values also !;-)
            if (ex.toString().lastIndexOf("Inf") != -1)
            {
                // to catch MOE descriptors correctly
                value = Double.POSITIVE_INFINITY;
            }
            else if ((ex.toString().lastIndexOf("NaN") != -1) ||
                    (ex.toString().lastIndexOf("-Na") != -1))
            {
                // to catch MOE descriptors correctly
                value = Double.NaN;
            }
            else if (ex.toString().lastIndexOf("-In") != -1)
            {
                // to catch MOE descriptors correctly
                value = Double.NEGATIVE_INFINITY;
            }
            else
            {
                logger.error(ex.toString());
                throw ex;
            }

            //System.out.println("double result value set to "+value);
        }

        return true;
    }

    /**
     *  Gets the double attribute of the DoubleResult object
     *
     * @return   The double value
     */
    public double getDouble()
    {
        return value;
    }

    /**
     * Gets the doubleNV attribute of the DoubleResult object
     *
     * @return   The doubleNV value
     */
    public double getDoubleNV()
    {
        return value;
    }

    /**
     * Gets the intNV attribute of the DoubleResult object
     *
     * @return   The intNV value
     */
    public int getIntNV()
    {
        return (int) value;
    }

    /**
     * Gets the stringNV attribute of the DoubleResult object
     *
     * @return   The stringNV value
     */
    public String getStringNV()
    {
        return DecimalFormatHelper.instance().format(value);
    }

    public int hashCode()
    {
        long bits = Double.doubleToLongBits(value);
        int dh = (int) (bits ^ (bits >>> 32));

        return dh;
    }

    /**
     *  Constructor for the DoubleResult object
     *
     * @param _descName  Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    /**
     * Gets the doubleNV attribute of the DoubleResult object
     *
     * @return   The doubleNV value
     */
    public boolean isDoubleNV()
    {
        return true;
    }

    /**
     * Gets the intNV attribute of the DoubleResult object
     *
     * @return   The intNV value
     */
    public boolean isIntNV()
    {
        return false;
    }

    /**
     *  Sets the double attribute of the DoubleResult object
     *
     * @param _v  The new double value
     */
    public void setDouble(double _v)
    {
        value = _v;
    }

    /**
     * Sets the doubleNV attribute of the DoubleResult object
     *
     * @param _value  The new doubleNV value
     */
    public void setDoubleNV(double _value)
    {
        value = _value;
    }

    /**
     * Sets the intNV attribute of the DoubleResult object
     *
     * @param _value  The new intNV value
     */
    public void setIntNV(int _value)
    {
        value = (double) _value;
    }

    /**
     * Sets the stringNV attribute of the DoubleResult object
     *
     * @param _value  The new stringNV value
     */
    public void setStringNV(String _value)
    {
        value = Double.parseDouble(_value);
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        return toString(ioType, DecimalFormatHelper.instance());
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String toString(IOType ioType, DecimalFormatter format)
    {
        return format.format(value);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
