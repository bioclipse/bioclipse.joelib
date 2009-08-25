///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BitResult.java,v $
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

import joelib2.feature.BitVectorValue;
import joelib2.feature.FeatureResult;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.io.types.ChemicalMarkupLanguage;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicArrayHelper;
import joelib2.util.BasicBitVector;
import joelib2.util.BasicLineArrayHelper;
import joelib2.util.BitVector;

import joelib2.util.types.StringString;

import java.util.List;

import org.apache.log4j.Category;


/**
 *  Atom representation.
 *
 * @.author     wegnerj
 *     30. Januar 2002
 */
public class BitResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, BitVectorValue, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(BitResult.class
            .getName());
    private final static String basicFormat = "n<bit_1,...bit_n>\n" +
        "with n\n" + "and bit_0,...,bit_n of type <0,1>";
    private final static String lineFormat = "n\n" + "bit_1\n" + "...\n" +
        "bit_n\n" + "with n\n" + "and bit_0,...,bit_n of type <0,1>";

    //~ Instance fields ////////////////////////////////////////////////////////

    public int maxBitSize;

    /**
     *  Description of the Field
     */
    public BitVector value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntResult object
     */
    public BitResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);

        value = new BasicBitVector();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        BitResult newObj = new BitResult();

        newObj.value = new BasicBitVector();

        return clone(newObj);
    }

    public BitResult clone(BitResult other)
    {
        other.maxBitSize = this.maxBitSize;
        other.value.clear();
        other.value.or(this.value);

        return other;
    }

    /**
     *  Description of the Method
     *
     * @param  ioType  Description of the Parameter
     * @return         Description of the Return Value
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
     * @param  pairData  Description of the Parameter
     * @return           Description of the Return Value
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
     * @param  sValue  Description of the Parameter
     * @return         Description of the Return Value
     */
    public boolean fromString(IOType ioType, String sValue)
    {
        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            List list;
            list = BasicLineArrayHelper.booleanArrayFromString(sValue);

            boolean[] array = (boolean[]) list.get(0);
            maxBitSize = array.length;
            value.fromBoolArray(array);
        }
        else if (ioType.equals(BasicIOTypeHolder.instance().getIOType("CML")))
        {
            String arrayDelimiter = null;
            String arraySize = null;

            if (this.hasCMLProperties())
            {
                StringString ss;
                ss = this.getCMLProperty("delimiter");

                if (ss != null)
                {
                    arrayDelimiter = ss.getStringValue2();
                }

                ss = this.getCMLProperty("size");

                if (ss != null)
                {
                    arraySize = ss.getStringValue2();
                }
            }

            if (arrayDelimiter == null)
            {
                arrayDelimiter = ChemicalMarkupLanguage.getDefaultDelimiter() +
                    " \t\r\n";
            }

            //                  if(arraySize==null)
            //                  {
            //                          logger.error("Number of size is missing in array.");
            //                          return false;
            //                  }
            //                  else{
            //                          int size=Integer.parseInt(arraySize);
            boolean[] array = BasicArrayHelper.booleanArrayFromTrueFalseString(
                    sValue, arrayDelimiter);
            maxBitSize = array.length;
            value.fromBoolArray(array);

            //                  }
            if (arraySize != null)
            {
                int size = Integer.parseInt(arraySize);

                if (size != array.length)
                {
                    logger.warn("Actual array size=" + array.length +
                        ", expected size=" + size);
                }
            }
        }
        else
        {
            List list;
            list = BasicArrayHelper.booleanArrayFromString(sValue, " _", -1);

            boolean[] array = (boolean[]) list.get(0);
            maxBitSize = array.length;
            value.fromBoolArray(array);
        }

        return true;
    }

    public BitVector getBinaryValue()
    {
        return value;
    }

    public int getMaxBitSize()
    {
        return maxBitSize;
    }

    public boolean init(String descName)
    {
        this.setKey(descName);

        return true;
    }

    /**
     *  Sets the double attribute of the DoubleResult object
     *
     * @param  _v  The new double value
     */
    public void setBits(BitVector bits, int maxBitSize)
    {
        value = bits;
        this.maxBitSize = maxBitSize;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        StringBuffer sb = new StringBuffer();

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            boolean[] array = value.toBoolArr(maxBitSize);
            BasicLineArrayHelper.toString(sb, array).toString();
        }
        else if (ioType.equals(BasicIOTypeHolder.instance().getIOType("CML")))
        {
            String delimiter = null;

            if (this.hasCMLProperties())
            {
                delimiter = this.getCMLProperty("delimiter").getStringValue2();
            }

            if (delimiter == null)
            {
                delimiter = ChemicalMarkupLanguage.getDefaultDelimiter();
            }

            BasicArrayHelper.toTrueFalseString(sb, value.toBoolArray(),
                delimiter).toString();
        }
        else
        {
            boolean[] array = value.toBoolArr(maxBitSize);
            BasicArrayHelper.toString(sb, array, "_", true).toString();
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
