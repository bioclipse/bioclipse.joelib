///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DecimalFormatHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:44 $
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
package wsi.ra.text;

import wsi.ra.tool.BasicPropertyHolder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;

import java.util.Locale;

import org.apache.log4j.Category;


/**
 * Some methods to faciliate the work with descriptors.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:44 $
 */
public class DecimalFormatHelper implements DecimalFormatter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "wsi.ra.tool.DecimalFormatHelper");
    private static DecimalFormatHelper instance;

    //~ Instance fields ////////////////////////////////////////////////////////

    private DecimalFormat decimalFormat;
    private String decimalFormatPattern;
    private DecimalFormatSymbols decimalSymbols;
    private boolean groupingUsed = false;
    private Locale locale;
    private BasicPropertyHolder propertyHolder;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorHelper object
     *
     * @param  _value           Description of the Parameter
     * @param  _name            Description of the Parameter
     * @param  _representation  Description of the Parameter
     */
    private DecimalFormatHelper()
    {
        propertyHolder = BasicPropertyHolder.instance();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static synchronized DecimalFormatHelper instance()
    {
        if (instance == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " +
                    DecimalFormatHelper.class.getClass().getName() +
                    " instance.");
            }

            instance = new DecimalFormatHelper();
            instance.loadProperties();
        }

        return instance;
    }

    /**
     * Formats a double value using the initialized <tt>DecimalFormat</tt> object.
     * Deviant to the Java standard the exponential terms with E0 are removed.
     *
     * @see wsi.ra.text.DecimalFormatter#format(double)
     */
    public String format(double value)
    {
        StringBuffer sb = new StringBuffer(decimalFormatPattern.length());
        decimalFormat.format(value, sb, new FieldPosition(0));

        // workaround for mysterious conversion of
        // 'NaN' --> '?'
        if ((sb.length() == 1) && (Character.isDigit(sb.charAt(0)) == false))
        {
            return "NaN";
        }

        // 'NaN' --> '-?'
        if ((sb.length() == 2) && (Character.isDigit(sb.charAt(1)) == false))
        {
            return "NaN";
        }

        // remove E0 extension
        if ((sb.length() > 2) && (sb.charAt(sb.length() - 2) == 'E') &&
                (sb.charAt(sb.length() - 1) == '0'))
        {
            //System.out.println(sb.toString()+"-->"+sb.substring(0,sb.length()-2));
            String tmp = sb.substring(0, sb.length() - 2);

            return tmp;
        }
        else
        {
            return sb.toString();
        }
    }

    private synchronized boolean loadProperties()
    {
        String valueS;
        String className = this.getClass().getName();

        valueS = propertyHolder.getProperties().getProperty(className +
                ".double.locale");

        if (valueS == null)
        {
            // use always english number format
            valueS = "en";
        }

        locale = new Locale(valueS);

        valueS = propertyHolder.getProperties().getProperty(className +
                ".double.format");

        if (valueS == null)
        {
            // use always exponents
            //valueS="0.0E0";
            //valueS="#####0.0################E0";
            valueS = "0.0#####################E0";
        }

        decimalFormatPattern = valueS;
        decimalSymbols = new DecimalFormatSymbols(locale);
        decimalFormat = new DecimalFormat(decimalFormatPattern, decimalSymbols);

        valueS = propertyHolder.getProperties().getProperty(className +
                ".double.groupingUsed");

        if (valueS == null)
        {
            // use always number format without grouping
            valueS = "false";
        }

        if (((valueS != null) && valueS.equalsIgnoreCase("true")))
        {
            groupingUsed = true;
        }
        else
        {
            groupingUsed = false;
        }

        decimalFormat.setGroupingUsed(groupingUsed);

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
