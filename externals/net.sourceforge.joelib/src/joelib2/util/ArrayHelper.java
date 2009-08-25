/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.util;

import wsi.ra.text.DecimalFormatter;

import java.util.List;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface ArrayHelper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean[] booleanArrayFromSimpleString(String sArrays);

    /**
     *  Loads boolean array from <tt>String</tt> . Format n<b_1,b_2,...,b_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>booleanArrayFromSimpleString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public List booleanArrayFromString(String sArrays);

    public boolean[] booleanArrayFromTrueFalseString(String sArrays);

    /**
     *  Loads integer array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>intArrayFromString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public double[] doubleArrayFromSimpleString(String sArrays);

    /**
     *  Loads double array from <tt>String</tt> . Format n<d_1,d_2,...,d_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>doubleArrayFromSimpleString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public List doubleArrayFromString(String sArrays);

    /**
     *  Gets the separator attribute of the ArrayHelper object
     *
     * @return    The separator value
     */
    public String getSeparator();

    /**
     *  Loads integer array from <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator. It's slower than using
     *  <code>intArrayFromString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public int[] intArrayFromSimpleString(String sArrays);

    /**
     *  Loads integer array from <tt>String</tt> . Format n<i_1,i_2,...,i_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>intArrayFromSimpleString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public List intArrayFromString(String sArrays);

    /**
     *  Sets the separator attribute of the ArrayHelper object
     *
     * @param  _separator  The new separator value
     */
    public void setSeparator(String _separator);

    /**
     *  Loads String array from <tt>String</tt> . Format n<b_1,b_2,...,b_n>. ','
     *  is here the default separator. It's faster to use this method than using
     *  <code>stringArrayFromSimpleString</code>.
     *
     * @param  sArrays  Description of the Parameter
     * @return          Description of the Return Value
     */
    public List stringArrayFromString(String sArrays);

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb         Description of the Parameter
     * @param  arrayrray  Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, int[] arrayrray);

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb         Description of the Parameter
     * @param  arrayrray  Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, double[] arrayrray);

    /**
     *  Write boolean array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb         Description of the Parameter
     * @param  arrayrray  Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, boolean[] arrayrray);

    /**
     *  Write integer array to <tt>String</tt> . Format i_1,i_2,...,i_n. ','
     *  is here the default separator.
     *
     * @param  sb         Description of the Parameter
     * @param  arrayrray  Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toSimpleString(StringBuffer sb, double[] arrayrray,
        DecimalFormatter format);

    /**
     *  Write integer array to <tt>String</tt> . Format n<i_1,i_2, ...,i_n>. ','
     *  is here the default separator.
     *
     * @param  arrayrray  Description of the Parameter
     * @param  sb         Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, int[] arrayrray);

    /**
     *  Write String array to <tt>String</tt> . Format n<i_1,i_2, ...,i_n>. ','
     *  is here the default separator.
     *
     * @param  arrayrray  Description of the Parameter
     * @param  sb         Description of the Parameter
     * @return            Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, String[] arrayrray);

    /**
     *  Description of the Method
     *
     * @param  dArray  Description of the Parameter
     * @param  sb      Description of the Parameter
     * @return         Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, double[] dArray);

    /**
     *  Description of the Method
     *
     * @param  sb      Description of the Parameter
     * @param  dArray  Description of the Parameter
     * @return         Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, boolean[] dArray);

    /**
     *  Description of the Method
     *
     * @param  dArray  Description of the Parameter
     * @param  sb      Description of the Parameter
     * @return         Description of the Return Value
     */
    public StringBuffer toString(StringBuffer sb, double[] dArray,
        DecimalFormatter format);
}
