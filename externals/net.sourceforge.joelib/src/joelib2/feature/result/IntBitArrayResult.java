///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IntBitArrayResult.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
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
 * @.license    GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:30 $
 */
public class IntBitArrayResult extends BasicPairDataCML
    implements FeatureResult, BitVectorValue, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            IntBitArrayResult.class.getName());
    private final static String basicFormat = "<bit_pos_1,...bit_pos_n>\n" +
        "where bit_pos_n is the position of a set bit in the bit string.";

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public BitVector value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntResult object
     */
    public IntBitArrayResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        IntBitArrayResult newObj = new IntBitArrayResult();

        newObj.value = new BasicBitVector();

        return clone(newObj);
    }

    public IntBitArrayResult clone(IntBitArrayResult other)
    {
        super.clone(other);
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
        return basicFormat;
    }

    /**
     *  Description of the Method
     *
     * @param  pairData  Description of the Parameter
     * @param  ioType    Description of the Parameter
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
     * @param  ioType  Description of the Parameter
     * @return         Description of the Return Value
     */
    public boolean fromString(IOType ioType, String sValue)
    {
        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            List list;
            list = BasicLineArrayHelper.booleanArrayFromString(sValue);

            boolean[] array = (boolean[]) list.get(0);

            //            maxBitSize=array.length;
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

            //                  if (arraySize == null)
            //                  {
            //                          logger.error("Number of size is missing in array.");
            //                          return false;
            //                  }
            //                  else
            //                  {
            //                          int size = Integer.parseInt(arraySize);
            boolean[] array = BasicArrayHelper.booleanArrayFromTrueFalseString(
                    sValue, arrayDelimiter);
            value = new BasicBitVector();
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
            int[] array = BasicArrayHelper.intArrayFromSimpleString(sValue,
                    " _");
            value = new BasicBitVector();
            value.fromIntArray(array);
        }

        //    maxPossibleBit=Integer.parseInt(PropertyHolder.instance().getProperty(this,"maxPossibleBit"));
        return true;
    }

    public BitVector getBinaryValue()
    {
        return value;
    }

    /**
     *  Description of the Method
     *
     * @param  _descName  Description of the Parameter
     * @return            Description of the Return Value
     */
    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    /**
     *  Sets the double attribute of the DoubleResult object
     *
     * @param  bits  The new bits value
     */
    public void setBits(BitVector bits)
    {
        value = bits;
    }

    /**
     *  Description of the Method
     *
     * @param  ioType  Description of the Parameter
     * @return         Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        StringBuffer sb = new StringBuffer();

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            //boolean[] array = value.toBoolArray();
            //LineArrayHelper.toString(sb, array).toString();
            int[] array = value.toIntArray();
            BasicLineArrayHelper.toString(sb, array).toString();
        }
        else if (ioType.equals(BasicIOTypeHolder.instance().getIOType("CML")))
        {
            String delimiter = null;

            if (this.hasCMLProperties())
            {
                StringString tmp = this.getCMLProperty("delimiter");

                if (tmp != null)
                {
                    delimiter = tmp.getStringValue2();
                }
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
            int[] array = value.toIntArray();
            BasicArrayHelper.toSimpleString(sb, array, "_");
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
