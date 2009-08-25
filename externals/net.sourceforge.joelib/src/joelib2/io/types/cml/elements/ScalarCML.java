///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ScalarCML.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
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

import joelib2.feature.result.BooleanResult;
import joelib2.feature.result.DoubleResult;
import joelib2.feature.result.IntResult;
import joelib2.feature.result.StringResult;

import joelib2.io.BasicIOType;
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
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class ScalarCML implements ElementCML
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.elements.ScalarCML");
    public static final String TITLE = "title";
    public static final String UNITS = "units";
    public static final String ERROR_BASIS = "errorBasis";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String ERROR_VALUE = "errorValue";
    public static final String DATA_TYPE = "dataType";
    public static final String DICT_REF = "dictRef";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected String dataType;
    protected String dictRef;
    protected String errorBasis;
    protected String errorValue;
    protected String max;
    protected String min;
    protected String title;
    protected String units;
    private StringBuffer buffer;
    private BasicIOType cml = BasicIOTypeHolder.instance().getIOType("CML");
    private List<BasicStringObject> scalarStorage;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the StringString object
     *
     * @param  _s1  Description of the Parameter
     * @param  _s2  Description of the Parameter
     */
    public ScalarCML(List<BasicStringObject> _scalarStorage)
    {
        scalarStorage = _scalarStorage;
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
        title = null;
        errorValue = null;
        errorBasis = null;
        dictRef = null;
        units = null;
        min = null;
        max = null;
        dataType = null;
    }

    public boolean endElement(String attribute)
    {
        String trimmedValue = buffer.toString().trim();

        //System.out.println(scalarTitle+":"+dataType+":="+tmp);
        if (trimmedValue.length() != 0)
        {
            ResultCMLProperties properties = null;

            if (dataType.equals("xsd:boolean"))
            {
                BooleanResult br = new BooleanResult();

                if (!br.fromString(cml, trimmedValue))
                {
                    logger.error("Boolean entry " + title + "=" + trimmedValue +
                        " was not successfully parsed.");

                    return false;
                }
                else
                {
                    scalarStorage.add(new BasicStringObject(title, br));
                    properties = br;
                }
            }
            else if (dataType.equals("xsd:float") ||
                    dataType.equals("xsd:double") ||
                    dataType.equals("xsd:decimal"))
            {
                DoubleResult dr = new DoubleResult();

                if (!dr.fromString(cml, trimmedValue))
                {
                    logger.error("Double entry " + title + "=" + trimmedValue +
                        " was not successfully parsed.");

                    return false;
                }
                else
                {
                    scalarStorage.add(new BasicStringObject(title, dr));
                    properties = dr;
                }
            }
            else if (dataType.equals("xsd:integer"))
            {
                IntResult ir = new IntResult();

                if (!ir.fromString(cml, trimmedValue))
                {
                    logger.error("Integer entry " + title + "=" + trimmedValue +
                        " was not successfully parsed.");

                    return false;
                }
                else
                {
                    scalarStorage.add(new BasicStringObject(title, ir));
                    properties = ir;
                }
            }

            // default
            else //if (dataType.equals("xsd:string"))
            {
                StringResult sr = new StringResult();

                if (!sr.fromString(cml, trimmedValue))
                {
                    System.out.println("String:" + title);
                    logger.error("String entry " + title + "=" + trimmedValue +
                        " was not successfully parsed.");

                    return false;
                }
                else
                {
                    scalarStorage.add(new BasicStringObject(title, sr));
                    properties = sr;
                }
            }

            if (properties != null)
            {
                if (errorValue != null)
                {
                    properties.addCMLProperty(new BasicStringString(ERROR_VALUE,
                            errorValue));
                }

                if (errorBasis != null)
                {
                    properties.addCMLProperty(new BasicStringString(ERROR_BASIS,
                            errorBasis));
                }

                if (dictRef != null)
                {
                    properties.addCMLProperty(new BasicStringString(DICT_REF,
                            dictRef));
                }

                if (units != null)
                {
                    properties.addCMLProperty(new BasicStringString(UNITS,
                            units));
                }

                if (min != null)
                {
                    properties.addCMLProperty(new BasicStringString(MIN, min));
                }

                if (max != null)
                {
                    properties.addCMLProperty(new BasicStringString(MAX, max));
                }
            }
        }

        return true;
    }

    public String getAllCharacterData()
    {
        return buffer.toString().trim();
    }

    /**
     * @return
     */
    public String getDataType()
    {
        return dataType;
    }

    /**
     * @return
     */
    public String getDictRef()
    {
        return dictRef;
    }

    /**
     * @return
     */
    public String getErrorBasis()
    {
        return errorBasis;
    }

    /**
     * @return
     */
    public String getErrorValue()
    {
        return errorValue;
    }

    /**
     * @return
     */
    public String getMax()
    {
        return max;
    }

    /**
     * @return
     */
    public String getMin()
    {
        return min;
    }

    /**
     * @return
     */
    public List getScalarStorage()
    {
        return scalarStorage;
    }

    /**
     * @return
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @return
     */
    public String getUnits()
    {
        return units;
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
        else if (attribute.equals(ERROR_VALUE))
        {
            errorValue = value;
        }
        else if (attribute.equals(ERROR_BASIS))
        {
            errorBasis = value;
        }
        else if (attribute.equals(DICT_REF))
        {
            dictRef = value;
        }
        else if (attribute.equals(UNITS))
        {
            units = value;
        }
        else if (attribute.equals(MAX))
        {
            min = value;
        }
        else if (attribute.equals(MIN))
        {
            max = value;
        }

        buffer = null;

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
