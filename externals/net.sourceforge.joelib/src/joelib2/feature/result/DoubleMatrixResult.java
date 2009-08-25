///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DoubleMatrixResult.java,v $
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
import joelib2.feature.NumberFormatResult;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.io.types.ChemicalMarkupLanguage;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicLineMatrixHelper;
import joelib2.util.BasicMatrixHelper;

import joelib2.util.types.StringString;

import wsi.ra.text.DecimalFormatHelper;
import wsi.ra.text.DecimalFormatter;

import org.apache.log4j.Category;


/**
 * Double matrix results of variable size.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:30 $
 */
public class DoubleMatrixResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, NumberFormatResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            DoubleMatrixResult.class.getName());
    private final static String basicFormat =
        "nLines nColumns<<e00,...,e0(nLines-1)>...<e(nColumns-1)0,...,e(nColumns-1)(nLines-1)>>\n" +
        "with nLines, eX0,...,eX(nLines-1) of type 32-bit integer" +
        "with nColumns, e0X,...,e(nColumns-1)X of type 32-bit integer";
    private final static String lineFormat = "nLines nColumns\n" + "e00\n" +
        "e01\n" + "...\n" + "e0(nLines-1)\n" + "n10\n" + "e11\n" + "...\n" +
        "e1(nLines-1)\n" + "...\n" + "e(nColumns-1)(nLines-1)\n" +
        "<empty line>\n" +
        "with nLines, eX0,...,eX(nLines-1) of type 32-bit integer" +
        "with nColumns, e0X,...,e(nColumns-1)X of type 32-bit integer";

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public double[][] value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntMatrixResult object
     */
    public DoubleMatrixResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        DoubleMatrixResult newObj = new DoubleMatrixResult();
        newObj.value = new double[this.value.length][this.value[0].length];

        return clone(newObj);
    }

    public DoubleMatrixResult clone(DoubleMatrixResult other)
    {
        super.clone(other);

        int s = this.value.length;

        for (int i = 0; i < s; i++)
        {
            System.arraycopy(this.value[i], 0, other.value[i], 0,
                value[i].length);
        }

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
        String format = basicFormat;

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            format = lineFormat;
        }

        return format;
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
        boolean success = true;

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            value = BasicLineMatrixHelper.doubleMatrixFromString(sValue);
        }
        else if (ioType.equals(BasicIOTypeHolder.instance().getIOType("CML")))
        {
            if (this.hasCMLProperties())
            {
                logger.error("CML properties are missing");
                success = false;
            }
            else
            {
                String matrixDelimiter = this.getCMLProperty("delimiter")
                                             .getStringValue2();
                String matrixRows = this.getCMLProperty("rows")
                                        .getStringValue2();
                String matrixColumns = this.getCMLProperty("columns")
                                           .getStringValue2();

                if (matrixDelimiter == null)
                {
                    matrixDelimiter =
                        ChemicalMarkupLanguage.getDefaultDelimiter() +
                        " \t\r\n";
                }

                if (matrixRows == null)
                {
                    logger.error("Number of rows is missing in matrix.");
                    success = false;
                }
                else if (matrixColumns == null)
                {
                    logger.error("Number of columns is missing in matrix.");
                    success = false;
                }
                else
                {
                    int rows = Integer.parseInt(matrixRows);
                    int columns = Integer.parseInt(matrixColumns);
                    value = BasicMatrixHelper.doubleMatrixFromSimpleString(
                            sValue, rows, columns, matrixDelimiter);
                }
            }
        }
        else
        {
            value = BasicMatrixHelper.instance().doubleMatrixFromString(sValue);
        }

        return success;
    }

    public boolean init(String descName)
    {
        this.setKey(descName);

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  ioType  Description of the Parameter
     * @return         Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        return toString(ioType, DecimalFormatHelper.instance());
    }

    public String toString(IOType ioType, DecimalFormatter format)
    {
        StringBuffer sb = new StringBuffer();

        if ((ioType != null) &&
                ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            BasicLineMatrixHelper.toString(sb, value, format).toString();
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

            //MatrixHelper.instance().toSimpleString(sb, value, delimiter, format).toString();
            BasicMatrixHelper.toTranspRectString(sb, value, delimiter, format)
                             .toString();
        }
        else
        {
            BasicMatrixHelper.instance().toString(sb, value, format).toString();
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
