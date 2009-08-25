///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicLineMatrixHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Jan Bruecker
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:41 $
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
package joelib2.util;

import wsi.ra.text.DecimalFormatter;

import java.io.LineNumberReader;
import java.io.StringReader;

import java.util.StringTokenizer;

import org.apache.log4j.Category;


/**
 * Helper methods for writing and loading line matrices.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:41 $
 */
public class BasicLineMatrixHelper implements LineMatrixHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicLineMatrixHelper.class.getName());
    private static BasicLineMatrixHelper lineMatrixHelper;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOERandom object
     */
    private BasicLineMatrixHelper()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  sMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static double[][] doubleMatrixFromString(String sMatrix)
    {
        int columns;
        int lines;
        String matrix = sMatrix;
        StringReader sr = new StringReader(matrix);
        LineNumberReader lnr = new LineNumberReader(sr);

        try
        {
            String line = lnr.readLine();
            StringTokenizer matrixSize = new StringTokenizer(line, " ");

            if ((line != null) && (line.trim().length() != 0))
            {
                lines = Integer.valueOf(matrixSize.nextToken()).intValue();
                columns = Integer.valueOf(matrixSize.nextToken()).intValue();
            }
            else
            {
                return null;
            }

            double[][] doubleMatrix = new double[lines][columns];

            for (int i = 0; i < lines; i++)
            {
                for (int j = 0; j < columns; j++)
                {
                    line = lnr.readLine();
                    doubleMatrix[i][j] = Double.valueOf(line).doubleValue();
                }
            }

            return doubleMatrix;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicLineMatrixHelper instance()
    {
        if (lineMatrixHelper == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " +
                    BasicLineMatrixHelper.class.getName() + " instance.");
            }

            lineMatrixHelper = new BasicLineMatrixHelper();
        }

        return lineMatrixHelper;
    }

    /**
     *  Description of the Method
     *
     * @param  sMatrix    Description of the Parameter
     * @return            Description of the Return Value
     */
    public static int[][] intMatrixFromString(String sMatrix)
    {
        int columns;
        int lines;
        String matrix = sMatrix;
        StringReader sr = new StringReader(matrix);
        LineNumberReader lnr = new LineNumberReader(sr);

        try
        {
            String line = lnr.readLine();
            StringTokenizer matrixSize = new StringTokenizer(line, " ");

            if ((line != null) && (line.trim().length() != 0))
            {
                lines = Integer.valueOf(matrixSize.nextToken()).intValue();
                columns = Integer.valueOf(matrixSize.nextToken()).intValue();
            }
            else
            {
                return null;
            }

            int[][] intMatrix = new int[lines][columns];

            for (int i = 0; i < lines; i++)
            {
                for (int j = 0; j < columns; j++)
                {
                    line = lnr.readLine();

                    //try{
                    intMatrix[i][j] = Integer.valueOf(line).intValue();

                    //}
                    //catch(NumberFormatException nfe)
                    //{
                    //  logger.error(nfe.toString());
                    //  return null;
                    //}
                }
            }

            return intMatrix;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  matrix     Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, int[][] matrix)
    {
        if ((matrix == null) || (matrix.length == 0))
        {
            return sb;
        }

        //System.out.println("\n\nLength: "+matrix.length);
        sb.append(matrix.length);
        sb.append(" ");
        sb.append(matrix[0].length);
        sb.append("\n");

        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[0].length; j++)
            {
                sb.append(matrix[i][j]);
                sb.append("\n");
            }
        }

        return sb;
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  matrix     Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[][] matrix)
    {
        return toString(sb, matrix, null);
    }

    /**
     *  Description of the Method
     *
     * @param  sb         Description of the Parameter
     * @param  matrix     Description of the Parameter
     * @return            Description of the Return Value
     */
    public static StringBuffer toString(StringBuffer sb, double[][] matrix,
        DecimalFormatter format)
    {
        if ((matrix == null) || (matrix.length == 0))
        {
            return sb;
        }

        sb.append(matrix.length);
        sb.append(" ");
        sb.append(matrix[0].length);
        sb.append("\n");

        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[0].length; j++)
            {
                if (format == null)
                {
                    sb.append(matrix[i][j]);
                }
                else
                {
                    sb.append(format.format(matrix[i][j]));
                }

                sb.append("\n");
            }
        }

        return sb;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
