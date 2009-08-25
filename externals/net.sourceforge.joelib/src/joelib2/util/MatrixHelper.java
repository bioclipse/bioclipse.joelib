/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.util;

import wsi.ra.text.DecimalFormatter;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface MatrixHelper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  sMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public byte[][] byteMatrixFromString(String sMatrix);

    /**
     *  Description of the Method
     *
     * @param  sMatrix   Description of the Parameter
     * @param  daVector  Description of the Parameter
     * @return           Description of the Return Value
     */
    public double[][] doubleMatrixFromRectangleString(String sMatrix);

    public double[][] doubleMatrixFromSimpleString(String sMatrix, int rows,
        int columns);

    /**
     *  Description of the Method
     *
     * @param  sMatrix   Description of the Parameter
     * @param  daVector  Description of the Parameter
     * @return           Description of the Return Value
     */
    public double[][] doubleMatrixFromString(String sMatrix);

    /**
     *  Gets the separator attribute of the MatrixHelper object
     *
     * @return    The separator value
     */
    public String getSeparator();

    public int[][] intMatrixFromSimpleString(String sMatrix, int rows,
        int columns);

    /**
     *  Description of the Method
     *
     * @param  sMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public int[][] intMatrixFromString(String sMatrix);

    /**
     *  Sets the separator attribute of the MatrixHelper object
     *
     * @param  _separator  The new separator value
     */
    public void setSeparator(String _separator);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  iMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toRectangleString(StringBuffer sb, int[][] iMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toRectangleString(StringBuffer sb, double[][] dMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  bMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toRectangleString(StringBuffer sb, byte[][] bMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toRectangleString(StringBuffer sb, double[][] dMatrix,
        DecimalFormatter format);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, double[][] dMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, int[][] iMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, boolean[][] bMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, double[][] dMatrix,
        DecimalFormatter format);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  bMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, byte[][] bMatrix);

    /**
     *  Description of the Method
     *
     * @param  iMatrix  Description of the Parameter
     * @param  sb       Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, int[][] iMatrix);

    /**
     *  Description of the Method
     *
     * @param  dMatrix  Description of the Parameter
     * @param  sb       Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, double[][] dMatrix);

    /**
     *  Description of the Method
     *
     * @param  dMatrix  Description of the Parameter
     * @param  sb       Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, double[][] dMatrix,
        DecimalFormatter format);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  iMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toTranspRectString(StringBuffer sb, int[][] iMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toTranspRectString(StringBuffer sb, double[][] dMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  bMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toTranspRectString(StringBuffer sb, byte[][] bMatrix);

    /**
     *  Description of the Method
     *
     * @param  sb       Description of the Parameter
     * @param  dMatrix  Description of the Parameter
     * @return          Description of the Return Value
     */
    public StringBuffer toTranspRectString(StringBuffer sb, double[][] dMatrix,
        DecimalFormatter format);
}
