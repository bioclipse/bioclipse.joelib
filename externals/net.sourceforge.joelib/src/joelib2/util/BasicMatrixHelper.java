///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicMatrixHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2006/01/22 19:00:57 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
///////////////////////////////////////////////////////////////////////////////
package joelib2.util;

import wsi.ra.text.DecimalFormatter;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Helper methods for wrinting and loading matrices.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2006/01/22 19:00:57 $
 */
public class BasicMatrixHelper implements MatrixHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public static String DEFAULT_SEPARATOR = ",";

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicMatrixHelper.class.getName());
    private static BasicMatrixHelper matrixHelper;
    private static String START_TAG = "<";
    private static String END_TAG = ">";

    //~ Instance fields ////////////////////////////////////////////////////////

    private String separator;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOERandom object
     */
    private BasicMatrixHelper()
    {
        separator = DEFAULT_SEPARATOR;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  separator  Description of the Parameter
     * @param  sMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static byte[][] byteMatrixFromString(String sMatrix,
        String separator)
    {
        String matrix = sMatrix;
        int columns;
        int rows;
        String matrixSizeString;
        int matrixStart = matrix.indexOf("<<");

        // not the excepted matrix definition, return null
        if (matrixStart == -1)
        {
            return null;
        }

        int matrixEnd = matrix.indexOf(">>") + 2;
        matrixSizeString = matrix.substring(0, matrixStart);
        matrix = matrix.substring(matrixStart, matrixEnd);

        StringTokenizer matrixSize = new StringTokenizer(matrixSizeString, " ");
        StringTokenizer tokens = new StringTokenizer(matrix, "><" + separator);

        rows = Integer.valueOf(matrixSize.nextToken()).intValue();
        columns = Integer.valueOf(matrixSize.nextToken()).intValue();

        byte[][] byteMatrix = new byte[rows][columns];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                byteMatrix[i][j] = Byte.valueOf(tokens.nextToken()).byteValue();
            }
        }

        return byteMatrix;
    }

    /**
     *  Description of the Method
     *
     * @param  separator  Description of the Parameter
     * @param  sMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static double[][] doubleMatrixFromRectangleString(String sMatrix,
        String separator)
    {
        StringTokenizer stColum = new StringTokenizer(sMatrix, "\r\n");
        String tmpColum;
        Vector columV = new Vector(20);
        StringTokenizer stRow;
        String tmpRowElem;
        Vector rowV;
        int rows = -1;
        int colum = 0;

        while (stColum.hasMoreTokens())
        {
            tmpColum = stColum.nextToken();
            colum++;
            stRow = new StringTokenizer(tmpColum, separator);
            rowV = new Vector(20);

            while (stRow.hasMoreTokens())
            {
                tmpRowElem = stRow.nextToken();
                rowV.add(new Double(tmpRowElem));
            }

            if (rows == -1)
            {
                rows = rowV.size();
            }
            else
            {
                if (rows != rowV.size())
                {
                    logger.error("Invalid number of row entries (" + rows +
                        "!=" + rowV.size() + ") in double matrix in column " +
                        colum);

                    return null;
                }
            }

            columV.add(rowV);
        }

        //System.out.println("col:"+columV.size()+"row:"+rows);
        // copy values to double matrix
        int columns = columV.size();
        double[][] matrix = new double[columns][rows];

        for (int i = 0; i < columns; i++)
        {
            rowV = (Vector) columV.get(i);

            for (int j = 0; j < rows; j++)
            {
                matrix[i][j] = ((Double) rowV.get(j)).doubleValue();
            }
        }

        return matrix;
    }

    /**
     *  Description of the Method
     *
     * @param  separator  Description of the Parameter
     * @param  sMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static double[][] doubleMatrixFromSimpleString(String sMatrix,
        int rows, int columns, String separator)
    {
        StringTokenizer st = new StringTokenizer(sMatrix, separator);

        String tmp;

        // copy values to double matrix
        double[][] matrix = new double[columns][rows];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                if (st.hasMoreTokens())
                {
                    tmp = st.nextToken();
                }
                else
                {
                    logger.error(
                        "Missing matrix element in double matrix of size " +
                        (columns * rows) + ".");

                    return null;
                }

                try
                {
                    matrix[j][i] = Double.parseDouble(tmp);
                }
                catch (NumberFormatException ex)
                {
                    logger.error(ex.getMessage() + " when parsing '" + tmp +
                        "'");

                    //System.exit(1);
                }
            }
        }

        return matrix;
    }

    /**
     *  Description of the Method
     *
     * @param  separator  Description of the Parameter
     * @param  sMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static double[][] doubleMatrixFromString(String sMatrix,
        String separator)
    {
        String matrix = sMatrix;
        int columns;
        int rows;
        String matrixSizeString;
        int matrixStart = matrix.indexOf("<<");

        // not the excepted matrix definition, return null
        if (matrixStart == -1)
        {
            return null;
        }

        int matrixEnd = matrix.indexOf(">>") + 2;
        matrixSizeString = matrix.substring(0, matrixStart);
        matrix = matrix.substring(matrixStart, matrixEnd);

        StringTokenizer matrixSize = new StringTokenizer(matrixSizeString, " ");
        StringTokenizer tokens = new StringTokenizer(matrix, "><" + separator);

        rows = Integer.valueOf(matrixSize.nextToken()).intValue();
        columns = Integer.valueOf(matrixSize.nextToken()).intValue();

        double[][] doubleMatrix = new double[rows][columns];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                doubleMatrix[i][j] = Double.valueOf(tokens.nextToken())
                                           .doubleValue();
            }
        }

        return doubleMatrix;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicMatrixHelper instance()
    {
        if (matrixHelper == null)
        {
            matrixHelper = new BasicMatrixHelper();
        }

        return matrixHelper;
    }

    /**
     *  Description of the Method
     *
     * @param  separator  Description of the Parameter
     * @param  sMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static int[][] intMatrixFromSimpleString(String sMatrix, int rows,
        int columns, String separator)
    {
        StringTokenizer st = new StringTokenizer(sMatrix, separator);

        String tmp;

        // copy values to double matrix
        int[][] matrix = new int[columns][rows];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                if (st.hasMoreTokens())
                {
                    tmp = st.nextToken();

                    //System.out.println("load: "+i+" "+j+" ="+tmp);
                }
                else
                {
                    logger.error(
                        "Missing matrix element in int matrix of size " +
                        (columns * rows) + ".");

                    //Object obj=null;
                    //obj.toString();
                    return null;
                }

                matrix[j][i] = Integer.parseInt(tmp);
            }
        }

        return matrix;
    }

    /**
     *  Description of the Method
     *
     * @param  separator  Description of the Parameter
     * @param  sMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static int[][] intMatrixFromString(String sMatrix, String separator)
    {
        String matrix = sMatrix;
        int columns;
        int rows;
        String matrixSizeString;
        int matrixStart = matrix.indexOf("<<");

        // not the excepted matrix definition, return null
        if (matrixStart == -1)
        {
            return null;
        }

        int matrixEnd = matrix.indexOf(">>") + 2;
        matrixSizeString = matrix.substring(0, matrixStart);
        matrix = matrix.substring(matrixStart, matrixEnd);

        StringTokenizer matrixSize = new StringTokenizer(matrixSizeString, " ");
        StringTokenizer tokens = new StringTokenizer(matrix, "><" + separator);

        rows = Integer.valueOf(matrixSize.nextToken()).intValue();
        columns = Integer.valueOf(matrixSize.nextToken()).intValue();

        int[][] intMatrix = new int[rows][columns];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                intMatrix[i][j] = Integer.valueOf(tokens.nextToken())
                                         .intValue();
            }
        }

        return intMatrix;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  dMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toRectangleString(StringBuffer sb,
        double[][] dMatrix, String separator)
    {
        return toRectangleString(sb, dMatrix, separator, null);
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  iMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toRectangleString(StringBuffer sb,
        int[][] iMatrix, String separator)
    {
        if ((iMatrix == null) || (iMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < iMatrix.length; i++)
        {
            for (int j = 0; j < iMatrix[0].length; j++)
            {
                sb.append(iMatrix[i][j]);

                if (j < (iMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            if (i < (iMatrix.length - 1))
            {
                sb.append("\n");
            }
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  bMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toRectangleString(StringBuffer sb,
        byte[][] bMatrix, String separator)
    {
        if ((bMatrix == null) || (bMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < bMatrix.length; i++)
        {
            for (int j = 0; j < bMatrix[0].length; j++)
            {
                sb.append(bMatrix[i][j]);

                if (j < (bMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            if (i < (bMatrix.length - 1))
            {
                sb.append("\n");
            }
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  dMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toRectangleString(StringBuffer sb,
        double[][] dMatrix, String separator, DecimalFormatter format)
    {
        if ((dMatrix == null) || (dMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < dMatrix.length; i++)
        {
            for (int j = 0; j < dMatrix[0].length; j++)
            {
                sb.append(' ');
                sb.append((j+1));
                sb.append(' ');
                if (format == null)
                {
                    sb.append(dMatrix[i][j]);
                }
                else
                {
                    sb.append(format.format(dMatrix[i][j]));
                }

                if (j < (dMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            if (i < (dMatrix.length - 1))
            {
                sb.append("\n");
            }
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  bMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb,
        double[][] dMatrix, String separator)
    {
        return toSimpleString(sb, dMatrix, separator, null);
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  bMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb, int[][] iMatrix,
        String separator)
    {
        if ((iMatrix == null) || (iMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < iMatrix.length; i++)
        {
            for (int j = 0; j < iMatrix[0].length; j++)
            {
                sb.append(iMatrix[i][j]);

                if (j < (iMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append(separator);
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  bMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb,
        boolean[][] bMatrix, String separator)
    {
        if ((bMatrix == null) || (bMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < bMatrix.length; i++)
        {
            for (int j = 0; j < bMatrix[0].length; j++)
            {
                sb.append(bMatrix[i][j]);

                if (j < (bMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append(separator);
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  bMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toSimpleString(StringBuffer sb,
        double[][] dMatrix, String separator, DecimalFormatter format)
    {
        if ((dMatrix == null) || (dMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < dMatrix.length; i++)
        {
            for (int j = 0; j < dMatrix[0].length; j++)
            {
                if (format == null)
                {
                    sb.append(dMatrix[i][j]);
                }
                else
                {
                    sb.append(format.format(dMatrix[i][j]));
                }

                if (j < (dMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append(separator);
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  separator  Description of the Parameter
     * @param  sb         Description of the Parameter
     * @param  bMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, byte[][] bMatrix,
        String separator)
    {
        if ((bMatrix == null) || (bMatrix.length == 0))
        {
            return sb;
        }

        sb.append(bMatrix.length);
        sb.append(' ');
        sb.append(bMatrix[0].length);

        sb.append(START_TAG);

        for (int i = 0; i < bMatrix.length; i++)
        {
            sb.append(START_TAG);

            for (int j = 0; j < bMatrix[0].length; j++)
            {
                sb.append(bMatrix[i][j]);

                if (j < (bMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append(END_TAG);
        }

        sb.append(END_TAG);

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  iMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @param  sb         Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, int[][] iMatrix,
        String separator)
    {
        if ((iMatrix == null) || (iMatrix.length == 0))
        {
            return sb;
        }

        sb.append(iMatrix.length);
        sb.append(' ');
        sb.append(iMatrix[0].length);

        sb.append(START_TAG);

        for (int i = 0; i < iMatrix.length; i++)
        {
            sb.append(START_TAG);

            for (int j = 0; j < iMatrix[0].length; j++)
            {
                sb.append(iMatrix[i][j]);

                if (j < (iMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append(END_TAG);
        }

        sb.append(END_TAG);

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  dMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @param  sb         Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[][] dMatrix,
        String separator)
    {
        if ((dMatrix == null) || (dMatrix.length == 0))
        {
            return sb;
        }

        sb.append(dMatrix.length);
        sb.append(' ');
        sb.append(dMatrix[0].length);

        sb.append(START_TAG);

        for (int i = 0; i < dMatrix.length; i++)
        {
            sb.append(START_TAG);

            for (int j = 0; j < dMatrix[0].length; j++)
            {
                sb.append(dMatrix[i][j]);

                if (j < (dMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append(END_TAG);
        }

        sb.append(END_TAG);

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  dMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @param  sb         Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[][] dMatrix,
        String separator, DecimalFormatter format)
    {
        if ((dMatrix == null) || (dMatrix.length == 0))
        {
            return sb;
        }

        sb.append(dMatrix.length);
        sb.append(' ');
        sb.append(dMatrix[0].length);

        sb.append(START_TAG);

        for (int i = 0; i < dMatrix.length; i++)
        {
            sb.append(START_TAG);

            for (int j = 0; j < dMatrix[0].length; j++)
            {
                sb.append(format.format(dMatrix[i][j]));

                if (j < (dMatrix[0].length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append(END_TAG);
        }

        sb.append(END_TAG);

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  iMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toTranspRectString(StringBuffer sb,
        int[][] iMatrix, String separator)
    {
        if ((iMatrix == null) || (iMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < iMatrix[0].length; i++)
        {
            for (int j = 0; j < iMatrix.length; j++)
            {
                sb.append(iMatrix[j][i]);

                if (j < (iMatrix.length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append("\n");
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  bMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toTranspRectString(StringBuffer sb,
        byte[][] bMatrix, String separator)
    {
        if ((bMatrix == null) || (bMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < bMatrix[0].length; i++)
        {
            for (int j = 0; j < bMatrix.length; j++)
            {
                sb.append(bMatrix[j][i]);

                if (j < (bMatrix.length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append("\n");
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  dMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toTranspRectString(StringBuffer sb,
        double[][] dMatrix, String separator)
    {
        return toTranspRectString(sb, dMatrix, separator, null);
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  dMatrix    Description of the Parameter
     * @param  separator  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toTranspRectString(StringBuffer sb,
        double[][] dMatrix, String separator, DecimalFormatter format)
    {
        if ((dMatrix == null) || (dMatrix.length == 0))
        {
            return sb;
        }

        for (int i = 0; i < dMatrix[0].length; i++)
        {
            for (int j = 0; j < dMatrix.length; j++)
            {
                if (format == null)
                {
                    sb.append(dMatrix[j][i]);
                }
                else
                {
                    sb.append(format.format(dMatrix[j][i]));
                }

                if (j < (dMatrix.length - 1))
                {
                    sb.append(separator);
                }
            }

            sb.append("\n");
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public byte[][] byteMatrixFromString(String sMatrix)
    {
        return byteMatrixFromString(sMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sMatrix   Description of the Parameter
     * @param  daVector  Description of the Parameter
     * @return           Description of the Return Value
     */
    public double[][] doubleMatrixFromRectangleString(String sMatrix)
    {
        return doubleMatrixFromRectangleString(sMatrix, separator);
    }

    public double[][] doubleMatrixFromSimpleString(String sMatrix, int rows,
        int columns)
    {
        return doubleMatrixFromSimpleString(sMatrix, rows, columns, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sMatrix   Description of the Parameter
     * @param  daVector  Description of the Parameter
     * @return           Description of the Return Value
     */
    public double[][] doubleMatrixFromString(String sMatrix)
    {
        return doubleMatrixFromString(sMatrix, separator);
    }

    /**
     *  Gets the separator attribute of the MatrixHelper object
     *
     * @return    The separator value
     */
    public String getSeparator()
    {
        return separator;
    }

    public int[][] intMatrixFromSimpleString(String sMatrix, int rows,
        int columns)
    {
        return intMatrixFromSimpleString(sMatrix, rows, columns, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public int[][] intMatrixFromString(String sMatrix)
    {
        return intMatrixFromString(sMatrix, separator);
    }

    /**
     *  Sets the separator attribute of the MatrixHelper object
     *
     * @param  _separator  The new separator value
     */
    public void setSeparator(String _separator)
    {
        separator = _separator;
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  iMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toRectangleString(StringBuffer sb, int[][] iMatrix)
    {
        return toRectangleString(sb, iMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toRectangleString(StringBuffer sb, double[][] dMatrix)
    {
        return toRectangleString(sb, dMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  bMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toRectangleString(StringBuffer sb, byte[][] bMatrix)
    {
        return toRectangleString(sb, bMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toRectangleString(StringBuffer sb, double[][] dMatrix,
        DecimalFormatter format)
    {
        return toRectangleString(sb, dMatrix, separator, format);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, double[][] dMatrix)
    {
        return toSimpleString(sb, dMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, int[][] iMatrix)
    {
        return toSimpleString(sb, iMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, boolean[][] bMatrix)
    {
        return toSimpleString(sb, bMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, double[][] dMatrix,
        DecimalFormatter format)
    {
        return toSimpleString(sb, dMatrix, separator, format);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  bMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, byte[][] bMatrix)
    {
        return toString(sb, bMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  iMatrix  Description of the Parameter
     * @param  sb       Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, int[][] iMatrix)
    {
        return toString(sb, iMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  dMatrix  Description of the Parameter
     * @param  sb       Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, double[][] dMatrix)
    {
        return toString(sb, dMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  dMatrix  Description of the Parameter
     * @param  sb       Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, double[][] dMatrix,
        DecimalFormatter format)
    {
        return toString(sb, dMatrix, separator, format);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  iMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toTranspRectString(StringBuffer sb, int[][] iMatrix)
    {
        return toTranspRectString(sb, iMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toTranspRectString(StringBuffer sb, double[][] dMatrix)
    {
        return toTranspRectString(sb, dMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  bMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toTranspRectString(StringBuffer sb, byte[][] bMatrix)
    {
        return toTranspRectString(sb, bMatrix, separator);
    }

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toTranspRectString(StringBuffer sb, double[][] dMatrix,
        DecimalFormatter format)
    {
        return toTranspRectString(sb, dMatrix, separator, format);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
