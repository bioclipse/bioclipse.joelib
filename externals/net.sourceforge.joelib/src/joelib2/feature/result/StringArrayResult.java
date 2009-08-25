///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: StringArrayResult.java,v $
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

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.io.types.ChemicalMarkupLanguage;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicArrayHelper;
import joelib2.util.BasicLineArrayHelper;

import joelib2.util.types.StringString;

import java.util.List;

import org.apache.log4j.Category;


/**
 * String array results of variable size.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:30 $
 */
public class StringArrayResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            StringArrayResult.class.getName());
    private final static String basicFormat = "n<e0,...e(n-1)>\n" +
        "with n, e0,...,e(n-1) of type Strings";
    private final static String lineFormat = "n\n" + "e0\n" + "...\n" +
        "e(n-1)>\n" +

        //            "<empty line>\n" +
        "with n, e0,...,e(n-1) of type Strings";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected String[] value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntArrayResult object
     */
    public StringArrayResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        StringArrayResult newObj = new StringArrayResult();

        newObj.value = new String[this.value.length];

        return clone(newObj);
    }

    public StringArrayResult clone(StringArrayResult other)
    {
        super.clone(other);
        System.arraycopy(this.value, 0, other.value, 0, value.length);

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
        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            List list;
            list = BasicLineArrayHelper.stringArrayFromString(sValue);
            value = (String[]) list.get(0);
        }
        else if (ioType.equals(BasicIOTypeHolder.instance().getIOType("CML")))
        {
            String arrayDelimiter = null;
            String arraySize = null;
            StringString ss;

            if (this.hasCMLProperties())
            {
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
            value = BasicArrayHelper.stringArrayFromSimpleString(sValue,
                    arrayDelimiter);

            //                  }
            if (arraySize != null)
            {
                int size = Integer.parseInt(arraySize);

                if (size != value.length)
                {
                    logger.warn("Actual array size=" + value.length +
                        ", expected size=" + size);
                }
            }
        }
        else
        {
            List list;
            list = BasicArrayHelper.instance().stringArrayFromString(sValue);
            value = (String[]) list.get(0);
        }

        return true;
    }

    public int getSize()
    {
        int size = 0;

        if (value != null)
        {
            size = value.length;
        }

        return size;
    }

    /**
     *  Gets the double attribute of the IntArrayResult object
     *
     * @return   The double value
     */
    public String[] getStringArray()
    {
        return (String[]) value;
    }

    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    /**
     *  Sets the double attribute of the IntArrayResult object
     *
     * @param _iarray  The new double value
     */
    public void setStringArray(String[] _sarray)
    {
        value = _sarray;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        StringBuffer sb = new StringBuffer();

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            BasicLineArrayHelper.toString(sb, value).toString();
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

            BasicArrayHelper.toSimpleString(sb, value, delimiter);
        }
        else
        {
            BasicArrayHelper.instance().toString(sb, value).toString();
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
