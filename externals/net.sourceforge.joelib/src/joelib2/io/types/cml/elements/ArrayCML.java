///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ArrayCML.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.io.types.cml.elements;

import joelib2.feature.result.BitArrayResult;
import joelib2.feature.result.DoubleArrayResult;
import joelib2.feature.result.IntArrayResult;
import joelib2.feature.result.StringArrayResult;

import joelib2.io.BasicIOTypeHolder;

import joelib2.io.types.cml.ResultCMLProperties;

import joelib2.util.types.BasicStringObject;
import joelib2.util.types.BasicStringString;

import java.util.List;

import org.apache.log4j.Category;


/**
 * Scalar CML element.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class ArrayCML implements ElementCML
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.elements.ArrayCML");
    public static final String SIZE = "size";
    public static final String TITLE = "title";
    public static final String UNITS = "units";
    public static final String ERROR_BASIS = "errorBasis";
    public static final String MIN_VALUES = "minValues";
    public static final String MAX_VALUES = "maxValues";
    public static final String ERROR_VALUES = "errorValues";
    public static final String DELIMITER = "delimiter";
    public static final String DATA_TYPE = "dataType";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected String dataType;
    protected String delimiter;
    protected String errorBasis;
    protected String errorValues;
    protected String maxValues;
    protected String minValues;
    protected String size;
    protected String title;
    protected String units;
    private List<BasicStringObject> arrayStorage;
    private StringBuffer buffer;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the StringString object
     *
     * @param  _s1  Description of the Parameter
     * @param  _s2  Description of the Parameter
     */
    public ArrayCML(List<BasicStringObject> _arrayStorage)
    {
        arrayStorage = _arrayStorage;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean characterData(String value)
    {
        if (buffer == null)
        {
            buffer = new StringBuffer(value.length());
        }

        buffer.append(value);

        return true;
    }

    public void clear()
    {
        size = null;
        title = null;
        units = null;
        errorBasis = null;
        minValues = null;
        maxValues = null;
        errorValues = null;
        delimiter = null;
        dataType = null;
    }

    public boolean endElement(String attribute)
    {
        String trimmedValue = buffer.toString().trim();

        if (title == null)
        {
            logger.error("No title defined for array element: " + trimmedValue);

            return false;
        }

        //System.out.println(arrayTitle+":"+dataType+":="+tmp);
        if (trimmedValue.length() != 0)
        {
            ResultCMLProperties properties = null;

            if (dataType.equals("xsd:boolean"))
            {
                //System.out.println(arrayTitle+":="+tmp);
                BitArrayResult bar = new BitArrayResult();

                if (delimiter == null)
                {
                    delimiter = " \t\r\n";
                }
                else
                {
                    bar.addCMLProperty(new BasicStringString(DELIMITER,
                            delimiter));
                }

                if (size != null)
                {
                    bar.addCMLProperty(new BasicStringString(SIZE, size));
                }

                if (!bar.fromString(
                            BasicIOTypeHolder.instance().getIOType("CML"),
                            trimmedValue))
                {
                    logger.error("Double array entry " + title + "=" +
                        trimmedValue + " was not successfully parsed.");
                }
                else
                {
                    arrayStorage.add(new BasicStringObject(title, bar));
                    properties = bar;
                }
            }
            else if (dataType.equals("xsd:float") ||
                    dataType.equals("xsd:double") ||
                    dataType.equals("xsd:decimal"))
            {
                //System.out.println(arrayTitle+":="+tmp);
                DoubleArrayResult dar = new DoubleArrayResult();

                if (delimiter == null)
                {
                    delimiter = " \t\r\n";
                }
                else
                {
                    dar.addCMLProperty(new BasicStringString(DELIMITER,
                            delimiter));
                }

                if (size != null)
                {
                    dar.addCMLProperty(new BasicStringString(SIZE, size));
                }

                if (!dar.fromString(
                            BasicIOTypeHolder.instance().getIOType("CML"),
                            trimmedValue))
                {
                    logger.error("Double array entry " + title + "=" +
                        trimmedValue + " was not successfully parsed.");
                }
                else
                {
                    arrayStorage.add(new BasicStringObject(title, dar));
                    properties = dar;
                }
            }
            else if (dataType.equals("xsd:integer"))
            {
                //System.out.println(arrayTitle+":="+tmp);
                IntArrayResult iar = new IntArrayResult();

                if (delimiter == null)
                {
                    delimiter = " \t\r\n";
                }
                else
                {
                    iar.addCMLProperty(new BasicStringString(DELIMITER,
                            delimiter));
                }

                if (size != null)
                {
                    iar.addCMLProperty(new BasicStringString(SIZE, size));
                }

                if (!iar.fromString(
                            BasicIOTypeHolder.instance().getIOType("CML"),
                            trimmedValue))
                {
                    logger.error("Integer array entry " + title + "=" +
                        trimmedValue + " was not successfully parsed.");
                }
                else
                {
                    arrayStorage.add(new BasicStringObject(title, iar));
                    properties = iar;
                }
            }
            else if (dataType.equals("xsd:string"))
            {
                //System.out.println(arrayTitle+":="+tmp);
                StringArrayResult iar = new StringArrayResult();

                if (delimiter == null)
                {
                    delimiter = " \t\r\n";
                }
                else
                {
                    iar.addCMLProperty(new BasicStringString(DELIMITER,
                            delimiter));
                }

                if (size != null)
                {
                    iar.addCMLProperty(new BasicStringString(SIZE, size));
                }

                if (!iar.fromString(
                            BasicIOTypeHolder.instance().getIOType("CML"),
                            trimmedValue))
                {
                    logger.error("String array entry " + title + "=" +
                        trimmedValue + " was not successfully parsed.");
                }
                else
                {
                    arrayStorage.add(new BasicStringObject(title, iar));
                    properties = iar;
                }
            }

            if (properties != null)
            {
                if (units != null)
                {
                    properties.addCMLProperty(new BasicStringString(UNITS,
                            units));
                }

                if (errorBasis != null)
                {
                    properties.addCMLProperty(new BasicStringString(ERROR_BASIS,
                            errorBasis));
                }

                if (minValues != null)
                {
                    properties.addCMLProperty(new BasicStringString(MIN_VALUES,
                            minValues));
                }

                if (maxValues != null)
                {
                    properties.addCMLProperty(new BasicStringString(MAX_VALUES,
                            maxValues));
                }

                if (errorValues != null)
                {
                    properties.addCMLProperty(new BasicStringString(
                            ERROR_VALUES, errorValues));
                }
            }
        }

        return true;
    }

    public boolean startElement(String attribute, String value)
    {
        if (attribute.equals(DATA_TYPE))
        {
            dataType = value;
        }
        else if (attribute.equals(TITLE))
        {
            title = value;
        }
        else if (attribute.equals(SIZE))
        {
            size = value;
        }
        else if (attribute.equals(DELIMITER))
        {
            delimiter = value;
        }
        else if (attribute.equals(UNITS))
        {
            units = value;
        }
        else if (attribute.equals(ERROR_BASIS))
        {
            errorBasis = value;
        }
        else if (attribute.equals(MIN_VALUES))
        {
            minValues = value;
        }
        else if (attribute.equals(MAX_VALUES))
        {
            maxValues = value;
        }
        else if (attribute.equals(ERROR_VALUES))
        {
            errorValues = value;
        }

        buffer = null;

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
