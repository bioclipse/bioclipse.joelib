///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MatrixCML.java,v $
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

import joelib2.feature.result.DoubleMatrixResult;
import joelib2.feature.result.IntMatrixResult;

import joelib2.io.types.cml.ResultCMLProperties;

import joelib2.util.BasicMatrixHelper;

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
public class MatrixCML implements ElementCML
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.elements.MatrixCML");
    public static final String ROWS = "rows";
    public static final String COLUMNS = "columns";
    public static final String TITLE = "title";
    public static final String UNITS = "units";
    public static final String DELIMITER = "delimiter";
    public static final String DATA_TYPE = "dataType";
    public static final String DICT_REF = "dictRef";
    public static final String MATRIX_TYPE = "matrixType";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected String columns;
    protected String dataType;
    protected String delimiter;
    protected String dictRef;
    protected String matrixType;
    protected String rows;
    protected String title;
    protected String units;
    private StringBuffer buffer;
    private List<BasicStringObject> matrixStorage;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the StringString object
     *
     * @param  _s1  Description of the Parameter
     * @param  _s2  Description of the Parameter
     */
    public MatrixCML(List<BasicStringObject> _matrixStorage)
    {
        matrixStorage = _matrixStorage;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public synchronized boolean characterData(String value)
    {
        if (buffer == null)
        {
            buffer = new StringBuffer(value.length());
        }

        //System.out.println("CD: '"+value+"'");
        buffer.append(value);

        return true;
    }

    public void clear()
    {
        title = null;
        rows = null;
        columns = null;
        delimiter = null;
        matrixType = null;
        dictRef = null;
        units = null;
        dataType = null;
    }

    public synchronized boolean endElement(String attribute)
    {
        String trimmedValue = buffer.toString().trim();

        if (title == null)
        {
            logger.error("No title defined for matrix element: " +
                trimmedValue);

            return false;
        }

        //System.out.println(trimmedValue);
        if (trimmedValue.length() != 0)
        {
            ResultCMLProperties properties = null;

            if (dataType.equals("xsd:boolean"))
            {
                // ignore !;-)
            }
            else if (dataType.equals("xsd:float") ||
                    dataType.equals("xsd:double") ||
                    dataType.equals("xsd:decimal"))
            {
                // much more efficient and more standard
                DoubleMatrixResult matrix = new DoubleMatrixResult();

                if (delimiter == null)
                {
                    delimiter = " \t\r\n";
                }
                else
                {
                    matrix.addCMLProperty(new BasicStringString("delimiter",
                            delimiter));
                }

                if (rows == null)
                {
                    logger.error(
                        "Number of rows is missing in matrix:xsd:float '" +
                        title + "'.");

                    return false;
                }
                else if (columns == null)
                {
                    logger.error(
                        "Number of columns is missing in matrix:xsd:float '" +
                        title + "'.");
                }
                else
                {
                    int r = Integer.parseInt(rows);
                    int c = Integer.parseInt(columns);

                    //System.out.println("xsd:float: '"+delimiter+"': "+tmp);
                    matrix.value = BasicMatrixHelper
                        .doubleMatrixFromSimpleString(trimmedValue, r, c,
                            delimiter);

                    if (matrix.value != null)
                    {
                        matrixStorage.add(new BasicStringObject(title, matrix));
                    }
                    else
                    {
                        logger.error("Matrix " + title + " of type " +
                            dataType + " could not be loaded.");
                    }

                    properties = matrix;
                }
            }
            else if (dataType.equals("xsd:integer"))
            {
                // much more efficient and more standard
                IntMatrixResult matrix = new IntMatrixResult();

                if (delimiter == null)
                {
                    delimiter = " \t\r\n";
                }
                else
                {
                    matrix.addCMLProperty(new BasicStringString(DELIMITER,
                            delimiter));
                }

                if (rows == null)
                {
                    logger.error(
                        "Number of rows is missing in matrix:xsd:integer '" +
                        title + "'.");

                    return false;
                }
                else if (columns == null)
                {
                    logger.error(
                        "Number of columns is missing in matrix:xsd:integer '" +
                        title + "'.");

                    return false;
                }
                else
                {
                    int r = Integer.parseInt(rows);
                    int c = Integer.parseInt(columns);

                    //System.out.println("xsd:integer: '"+delimiter+"': "+tmp);
                    //System.out.println("Matrix "+title+":" +trimmedValue);
                    matrix.value = BasicMatrixHelper.intMatrixFromSimpleString(
                            trimmedValue, r, c, delimiter);

                    if (matrix.value != null)
                    {
                        matrixStorage.add(new BasicStringObject(title, matrix));
                    }
                    else
                    {
                        logger.error("Matrix " + title + " of type " +
                            dataType + " could not be loaded.");
                    }

                    properties = matrix;
                }
            }

            if (properties != null)
            {
                if (matrixType != null)
                {
                    properties.addCMLProperty(new BasicStringString(MATRIX_TYPE,
                            matrixType));
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
        else if (attribute.equals(ROWS))
        {
            rows = value;
        }
        else if (attribute.equals(COLUMNS))
        {
            columns = value;
        }
        else if (attribute.equals(DELIMITER))
        {
            delimiter = value;
        }
        else if (attribute.equals(MATRIX_TYPE))
        {
            matrixType = value;
        }
        else if (attribute.equals(DICT_REF))
        {
            dictRef = value;
        }
        else if (attribute.equals(UNITS))
        {
            units = value;
        }

        buffer = null;

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
