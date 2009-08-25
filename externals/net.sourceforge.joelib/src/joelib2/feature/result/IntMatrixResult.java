///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IntMatrixResult.java,v $
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

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.io.types.ChemicalMarkupLanguage;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicLineMatrixHelper;
import joelib2.util.BasicMatrixHelper;

import joelib2.util.types.StringString;

import org.apache.log4j.Category;


/**
 * Integer matrix results of variable size.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:30 $
 */
public class IntMatrixResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(IntMatrixResult.class
            .getName());
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
    public int[][] value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntMatrixResult object
     */
    public IntMatrixResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        IntMatrixResult newObj = new IntMatrixResult();

        newObj.value = new int[this.value.length][this.value[0].length];

        return clone(newObj);
    }

    public IntMatrixResult clone(IntMatrixResult other)
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
            value = BasicLineMatrixHelper.intMatrixFromString(sValue);
        }
        else if (ioType.equals(BasicIOTypeHolder.instance().getIOType("CML")))
        {
            if (this.hasCMLProperties())
            {
                logger.error("CML properties are missing");

                return false;
            }

            String matrixDelimiter = this.getCMLProperty("delimiter")
                                         .getStringValue2();
            String matrixRows = this.getCMLProperty("rows").getStringValue2();
            String matrixColumns = this.getCMLProperty("columns")
                                       .getStringValue2();

            if (matrixDelimiter == null)
            {
                matrixDelimiter = ChemicalMarkupLanguage.getDefaultDelimiter() +
                    " \t\r\n";
            }

            if (matrixRows == null)
            {
                logger.error("Number of rows is missing in matrix.");

                return false;
            }
            else if (matrixColumns == null)
            {
                logger.error("Number of columns is missing in matrix.");

                return false;
            }
            else
            {
                int rows = Integer.parseInt(matrixRows);
                int columns = Integer.parseInt(matrixColumns);
                value = BasicMatrixHelper.intMatrixFromSimpleString(sValue,
                        rows, columns, matrixDelimiter);
            }
        }
        else
        {
            value = BasicMatrixHelper.instance().intMatrixFromString(sValue);
        }

        return true;
    }

    public boolean init(String _descName)
    {
        this.setKey(_descName);

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
        StringBuffer sb = new StringBuffer();

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            BasicLineMatrixHelper.toString(sb, value).toString();
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

            //MatrixHelper.instance().toSimpleString(sb, value, delimiter).toString();
            BasicMatrixHelper.toTranspRectString(sb, value, delimiter);
        }
        else
        {
            BasicMatrixHelper.instance().toString(sb, value).toString();
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
