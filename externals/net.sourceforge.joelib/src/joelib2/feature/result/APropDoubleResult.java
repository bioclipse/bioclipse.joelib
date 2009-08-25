///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: APropDoubleResult.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.HelperMethods;

import wsi.ra.text.DecimalFormatHelper;
import wsi.ra.text.DecimalFormatter;

import java.io.LineNumberReader;
import java.io.StringReader;

import org.apache.log4j.Category;


/**
 * Double value descriptor which was calculated by using an atom property ({@link joelib2.molecule.types.AtomProperties}).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:30 $
 */
public class APropDoubleResult extends BasicPairData implements Cloneable,
    FeatureResult, NativeValue, NumberFormatResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            APropDoubleResult.class.getName());
    private final static String basicFormat = "<atom_property>\n" +
        "64-bit floating point value IEEE 754";
    private final static String lineFormat = "<atom_property>\n" +
        "64-bit floating point value IEEE 754";

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public String atomProperty;

    /**
     *  Description of the Field
     */
    public double value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntResult object
     */
    public APropDoubleResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        APropDoubleResult newObj = new APropDoubleResult();

        return clone(newObj);
    }

    public APropDoubleResult clone(APropDoubleResult other)
    {
        super.clone(other);
        other.atomProperty = this.atomProperty;
        other.value = this.value;

        return other;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String formatDescription(IOType ioType)
    {
        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            return lineFormat;
        }
        else
        {
            return basicFormat;
        }
    }

    /**
     *  Description of the Method
     *
     * @param pairData  Description of the Parameter
     * @param ioType    Description of the Parameter
     * @return          Description of the Return Value
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
     *  Description of the Method
     *
     * @param sValue  Description of the Parameter
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public boolean fromString(IOType ioType, String sValue)
    {
        StringReader sr = new StringReader(sValue);
        LineNumberReader lnr = new LineNumberReader(sr);

        String tmp = null;

        // get property type
        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("FLAT")) ==
                false)
        {
            try
            {
                atomProperty = lnr.readLine();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                return false;
            }
        }
        else
        {
            int index = sValue.indexOf("\n");

            if (index == -1)
            {
                logger.error(this.getClass().getName() +
                    " missing atom property.");

                return false;
            }

            atomProperty = sValue.substring(index).trim();
        }

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("UNDEFINED")))
        {
            int index = sValue.indexOf("\n");

            if (index == -1)
            {
                logger.error(this.getClass().getName() +
                    " missing double value.");

                return false;
            }

            tmp = sValue.substring(index).trim();
        }
        else if (ioType.equals(BasicIOTypeHolder.instance().getIOType("FLAT")) ==
                false)
        {
            int index = sValue.indexOf("\n");

            if (index == -1)
            {
                logger.error(this.getClass().getName() +
                    " missing double value.");

                return false;
            }

            tmp = sValue.substring(index).trim();
        }

        try
        {
            value = Double.parseDouble(tmp);
        }
        catch (NumberFormatException ex)
        {
            // try to be MOE compatible !!!
            // 'Inf' are infinity values also !;-)
            if (ex.toString().lastIndexOf("Inf") != -1)
            {
                value = Double.POSITIVE_INFINITY;

                //System.out.println("double result value set to "+value);
            }
            else if ((ex.toString().lastIndexOf("NaN") != -1) ||
                    (ex.toString().lastIndexOf("-Na") != -1))
            {
                value = Double.NaN;

                //System.out.println("double result value set to "+value);
            }
            else if (ex.toString().lastIndexOf("-In") != -1)
            {
                value = Double.NEGATIVE_INFINITY;

                //System.out.println("double result value set to "+value);
            }
            else
            {
                throw ex;
            }
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
     * Gets the doubleNV attribute of the APropDoubleResult object
     *
     * @return   The doubleNV value
     */
    public double getDoubleNV()
    {
        return value;
    }

    /**
     * Gets the intNV attribute of the APropDoubleResult object
     *
     * @return   The intNV value
     */
    public int getIntNV()
    {
        return (int) value;
    }

    /**
     * Gets the stringNV attribute of the APropDoubleResult object
     *
     * @return   The stringNV value
     */
    public String getStringNV()
    {
        return DecimalFormatHelper.instance().format(value);
    }

    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    /**
     * Gets the doubleNV attribute of the APropDoubleResult object
     *
     * @return   The doubleNV value
     */
    public boolean isDoubleNV()
    {
        return true;
    }

    /**
     * Gets the intNV attribute of the APropDoubleResult object
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
     * Sets the doubleNV attribute of the APropDoubleResult object
     *
     * @param _value  The new doubleNV value
     */
    public void setDoubleNV(double _value)
    {
        value = _value;
    }

    /**
     * Sets the intNV attribute of the APropDoubleResult object
     *
     * @param _value  The new intNV value
     */
    public void setIntNV(int _value)
    {
        value = (double) _value;
    }

    /**
     * Sets the stringNV attribute of the APropDoubleResult object
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
        StringBuffer sb = new StringBuffer();

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("FLAT")))
        {
            sb.append(format.format(value));
        }
        else
        {
            sb.append(atomProperty);
            sb.append(HelperMethods.eol);
            sb.append(format.format(value));
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
