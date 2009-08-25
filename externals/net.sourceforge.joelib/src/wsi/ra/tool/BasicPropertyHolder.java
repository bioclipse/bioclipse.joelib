///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicPropertyHolder.java,v $
//  Purpose:  PropertyHolder for java property file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.4 $
//            $Date: 2005/06/17 06:31:46 $
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
package wsi.ra.tool;

import joelib2.util.HelperMethods;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Category;


/**
 *  Singleton class to hold java properties. The default property holder
 * loads all properties from the file <tt>joelib2.properties</tt> using
 * the {@link BasicResourceLoader}.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.4 $, $Date: 2005/06/17 06:31:46 $
 * @see BasicResourceLoader
 */
public final class BasicPropertyHolder implements PropertyHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static BasicPropertyHolder instance;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicPropertyHolder.class.getName());
    private static String DEFAULT_PROPERTY_FILE = "joelib2.properties";
    private static final String specialSaveChars = "=: \t\r\n\f#!";

    /** A table of hex digits */
    private static final char[] hexDigit =
        {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F'
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private Properties prop;
    private String propFile;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the PropertyHolder object
     *
     * @param  _propFile        Description of the Parameter
     * @exception  IOException  Description of the Exception
     */
    private BasicPropertyHolder(String _propFile) throws IOException
    {
        prop = new Properties();

        if (System.getProperty("javawebstart.version") != null)
        {
            // If the property is found, JOELib2 is running with Java Web Start. To fix
            // bug 4621090, the security manager is set to null.
            System.setSecurityManager(null);
        }

        if (System.getProperty("user.home") == null)
        {
            System.err.println(
                "Error starting JOELib2: the property 'user.home' is not defined.");
            System.exit(1);
        }

        File udir = new File(new File(System.getProperty("user.home")),
                ".joelib2");

        if (!udir.exists())
        {
            logger.info("JOELib2 property directory created at " +
                udir.getAbsolutePath());
            logger.info(
                "Users which prefer to work there can copy the joelib2/src/joelib2.properties-file to " +
                udir.getAbsolutePath());
            udir.mkdirs();
        }

        File ufile = new File(udir, _propFile);

        BasicResourceLoader loader = BasicResourceLoader.instance();

        //System.out.println("Create PropertyLoaderInstance");
        byte[] bytes = null;

        if (ufile.exists())
        {
            logger.info("joelib2.properties found in user home directory.");
            logger.info("Loading from user home " + ufile.getAbsolutePath());
            bytes = loader.getBytesFromResourceLocation(ufile
                    .getAbsolutePath());
        }
        else
        {
            logger.info(
                "Loading joelib2.properties from classpath and/or jar-file.");
            bytes = loader.getBytesFromResourceLocation(_propFile);
        }

        if (bytes != null)
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            prop.load(bais);
        }

        propFile = _propFile;

        // printing copyright notice
        for (int i = 0; i < HelperMethods.COPYRIGHT.length; i++)
        {
            HelperMethods.copyright2Logger.info(HelperMethods.COPYRIGHT[i]);
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Exists an instance for this <tt>PropertyHolder</tt>.
     *
     * @return    Description of the Return Value
     */
    public static boolean existInstance()
    {
        return (instance != null);
    }

    /**
     *  Gets the double attribute of the PropertyHolder class
     *
     * @param  _prop     Description of the Parameter
     * @param  property  Description of the Parameter
     * @return           The double value
     */
    public static double getDouble(Properties _prop, String property,
        double def)
    {
        String valueS = _prop.getProperty(property);

        if (valueS == null)
        {
            logger.error("Double property '" + property + "' not defined " +
                def + " returned.");

            return def;
        }

        double value = Double.NaN;

        try
        {
            value = Double.parseDouble(valueS);
        }
        catch (NumberFormatException ex)
        {
            logger.error("NumberFormatExeption in " + property + "=" + valueS +
                ". " + def + " returned.");

            return def;
        }

        return value;
    }

    /**
    * Method getDouble.
    * @param _prop
    * @param obj
    * @param property
    * @return double
    */
    public static double getDouble(Properties _prop, Object obj,
        String property, double def)
    {
        String className = obj.getClass().getName();
        String newName = className + '.' + property;

        String value = _prop.getProperty(newName);

        if (value == null)
        {
            //logger.error("Double property '" + newName + "' not found.");
            return def;
        }
        else
        {
            return getDouble(_prop, newName, def);
        }

        //className = null;
        //return Double.NaN;
    }

    /**
     *  Gets the double attribute of the PropertyHolder class
     *
     * @param  _prop     Description of the Parameter
     * @param  property  Description of the Parameter
     * @param  min       Description of the Parameter
     * @param  max       Description of the Parameter
     * @param  def       Description of the Parameter
     * @return           The double value
     */
    public static double getDouble(Properties _prop, String property,
        double min, double max, double def)
    {
        double value = getDouble(_prop, property, def);

        if ((value < min) || (value > max))
        {
            if (value < min)
            {
                value = min;
            }

            if (value > max)
            {
                value = max;
            }

            logger.warn("Value " + property + " must be between " + min +
                " and " + max + ". Now it's set to " + value);
        }

        return value;
    }

    /**
     * Method getDouble.
     * @param _prop
     * @param obj
     * @param property
     * @param min
     * @param max
     * @param def
     * @return double
     */
    public static double getDouble(Properties _prop, Object obj,
        String property, double min, double max, double def)
    {
        String className = obj.getClass().getName();
        String newName = className + '.' + property;

        return getDouble(_prop, newName, min, max, def);
    }

    /**
     *  Gets the int attribute of the PropertyHolder class
     *
     * @param  _prop     Description of the Parameter
     * @param  property  Description of the Parameter
     * @return           The int value
     */
    public static int getInt(Properties _prop, String property, int def)
    {
        String valueS = _prop.getProperty(property);

        if (valueS == null)
        {
            //logger.error("Int property '" + property + "' not defined "+def+" returned.");
            return def;
        }

        int value = Integer.MIN_VALUE;

        try
        {
            value = Integer.parseInt(valueS);
        }
        catch (NumberFormatException ex)
        {
            logger.error("NumberFormatExeption in " + property + "=" + valueS +
                ". " + def + " returned.");

            return def;
        }

        return value;
    }

    /**
    * Method getInt.
    * @param _prop
    * @param obj
    * @param property
    * @return int
    */
    public static int getInt(Properties _prop, Object obj, String property,
        int def)
    {
        String className = obj.getClass().getName();
        String newName = className + '.' + property;

        String value = _prop.getProperty(newName);

        if (value == null)
        {
            //logger.error("Int property '" + newName + "' not found.");
            return def;
        }
        else
        {
            return getInt(_prop, newName, def);
        }

        //className = null;
        //return 0;
    }

    /**
     *  Gets the int attribute of the PropertyHolder class
     *
     * @param  _prop     Description of the Parameter
     * @param  property  Description of the Parameter
     * @param  min       Description of the Parameter
     * @param  max       Description of the Parameter
     * @param  def       Description of the Parameter
     * @return           The int value
     */
    public static int getInt(Properties _prop, String property, int min,
        int max, int def)
    {
        int value = getInt(_prop, property, def);

        if ((value < min) || (value > max))
        {
            if (value < min)
            {
                value = min;
            }

            if (value > max)
            {
                value = max;
            }

            logger.warn("Value " + property + " must be between " + min +
                " and " + max + ". Now it's set to " + value);
        }

        return value;
    }

    /**
    * Method getInt.
    * @param _prop
    * @param obj
    * @param property
    * @param min
    * @param max
    * @param def
    * @return int
    */
    public static int getInt(Properties _prop, Object obj, String property,
        int min, int max, int def)
    {
        String className = obj.getClass().getName();
        String newName = className + '.' + property;

        return getInt(_prop, newName, min, max, def);
    }

    public static String getProperty(Properties _prop, Object obj,
        String property)
    {
        String className = obj.getClass().getName();

        String value = _prop.getProperty(className + '.' + property);

        if (value == null)
        {
            logger.error("Property '" + className + '.' + property +
                "' not found.");
        }

        className = null;

        return value;
    }

    /**
     *  Gets the uRLFromProperty attribute of the PropertyHolder class
     *
     * @param  prop      Description of the Parameter
     * @param  propName  Description of the Parameter
     * @return           The uRLFromProperty value
     */
    public static URL getURLFromProperty(Properties prop, String propName)
    {
        return getURLFromProperty(prop, propName, true);
    }

    /**
     *  Gets the uRLFromProperty attribute of the PropertyHolder class
     *
     * @param  _prop      Description of the Parameter
     * @param  propName   Description of the Parameter
     * @param  showError  Description of the Parameter
     * @return            The uRLFromProperty value
     */
    public static URL getURLFromProperty(Properties _prop, String propName,
        boolean showError)
    {
        String _dataS = _prop.getProperty(propName);

        //System.out.println(propName);
        if (_dataS == null)
        {
            if (showError)
            {
                logger.error("Property " + propName + " not defined.");
            }

            return null;
        }

        URL _data = null;

        //System.out.println(_dataS);
        try
        {
            _data = (new File(_dataS)).toURL();
        }
        catch (MalformedURLException ex)
        {
            if (showError)
            {
                logger.error("Value of " + propName + " has no valid URL.");
            }

            return null;
        }

        return _data;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicPropertyHolder instance()
    {
        return instance(DEFAULT_PROPERTY_FILE);
    }

    /**
     *  Description of the Method
     *
     * @param  _propFile  Description of the Parameter
     * @return            Description of the Return Value
     */
    public static synchronized BasicPropertyHolder instance(String _propFile)
    {
        try
        {
            if (instance == null)
            {
                instance = new BasicPropertyHolder(_propFile);
            }

            //      System.out.println("Load: "+_propFile);
        }
        catch (Exception ex)
        {
            logger.error("Propertyfile " + _propFile + " not found.");
            System.exit(1);
        }

        return instance;
    }

    /**
    * Method getDouble.
    * @param obj
    * @param property
    * @return double
    */

    //  public double getDouble(Object obj, String property)
    //  {
    //          return getDouble(prop, obj, property,0.0);
    //  }

    /**
    * Method getDouble.
    * @param obj
    * @param property
    * @return double
    */
    public double getDouble(Object obj, String property, double def)
    {
        return getDouble(prop, obj, property, def);
    }

    /**
     * Method getDouble.
     * @param obj
     * @param property
     * @param min
     * @param max
     * @param def
     * @return double
     */
    public double getDouble(Object obj, String property, double min, double max,
        double def)
    {
        return getDouble(prop, obj, property, min, max, def);
    }

    public int getInt(String property)
    {
        return getInt(prop, property, 0);
    }

    /**
     *  Gets the int attribute of the PropertyHolder object
     *
     * @param  property  Description of the Parameter
     * @return           The int value
     */
    public int getInt(String property, int def)
    {
        return getInt(prop, property, def);
    }

    /**
    * Method getInt.
    * @param obj
    * @param property
    * @return int
    */

    //  public int getInt(Object obj, String property)
    //  {
    //          return getInt(prop, obj, property,0);
    //  }

    /**
    * Method getInt.
    * @param obj
    * @param property
    * @return int
    */
    public int getInt(Object obj, String property, int def)
    {
        return getInt(prop, obj, property, def);
    }

    /**
     *  Gets the int attribute of the PropertyHolder object
     *
     * @param  property  Description of the Parameter
     * @param  min       Description of the Parameter
     * @param  max       Description of the Parameter
     * @param  def       Description of the Parameter
     * @return           The int value
     */
    public int getInt(String property, int min, int max, int def)
    {
        return getInt(prop, property, min, max, def);
    }

    /**
    * Method getInt.
    * @param obj
    * @param property
    * @param min
    * @param max
    * @param def
    * @return int
    */
    public int getInt(Object obj, String property, int min, int max, int def)
    {
        return getInt(prop, obj, property, min, max, def);
    }

    /**
     *  Gets the properties attribute of the PropertyHolder object
     *
     * @return    The properties value
     */
    public Properties getProperties()
    {
        return prop;
    }

    /**
    *  Gets the property attribute of the RegressionHelper object
    *
    * @param  property  Description of the Parameter
    * @return           The property value
    */
    public String getProperty(Object obj, String property)
    {
        return getProperty(prop, obj, property);
    }

    /**
     *  Gets the propFile attribute of the PropertyHolder object
     *
     * @return    The propFile value
     */
    public String getPropFile()
    {
        return propFile;
    }

    /**
     *  Gets the uRLFromProperty attribute of the PropertyHolder object
     *
     * @param  propName  Description of the Parameter
     * @return           The uRLFromProperty value
     */
    public URL getURLFromProperty(String propName)
    {
        return getURLFromProperty(prop, propName, true);
    }

    /**
     *  Gets the uRLFromProperty attribute of the PropertyHolder object
     *
     * @param  propName   Description of the Parameter
     * @param  showError  Description of the Parameter
     * @return            The uRLFromProperty value
     */
    public URL getURLFromProperty(String propName, boolean showError)
    {
        return getURLFromProperty(prop, propName, showError);
    }

    public void store(String saveAs, String generatingClass)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(saveAs);
            PrintStream ps = new PrintStream(fos);
            ps.println(
                "##############################################################################\n# Automatically generated by " +
                generatingClass +
                "\n##############################################################################");

            Enumeration enumeration = prop.keys();
            String key;
            String[] keys = new String[prop.size()];

            for (int i = 0; enumeration.hasMoreElements(); i++)
            {
                key = (String) enumeration.nextElement();
                keys[i] = key;
            }

            // save at least in alphabetical order or nobody can read this stuff !!!
            Arrays.sort(keys);

            String entry;

            for (int i = 0; i < keys.length; i++)
            {
                key = saveConvert(keys[i], true);
                entry = saveConvert(prop.getProperty(keys[i]), false);
                ps.println(key + "=" + entry);
            }

            fos.close();
        }
        catch (FileNotFoundException e)
        {
            logger.error(e.getMessage());
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    /**
     * Convert a nibble to a hex character
     * @param        nibble        the nibble to convert.
     */
    private static char toHex(int nibble)
    {
        return hexDigit[(nibble & 0xF)];
    }

    /*
     * Converts unicodes to encoded &#92;uxxxx
     * and writes out any of the characters in specialSaveChars
     * with a preceding slash
     */
    private String saveConvert(String theString, boolean escapeSpace)
    {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len * 2);

        if ((len == 1) && theString.equals(" ") && !escapeSpace)
        {
            return "\\u0020";
        }

        for (int x = 0; x < len; x++)
        {
            char aChar = theString.charAt(x);

            switch (aChar)
            {
            case ' ':

                if ((x == 0) || escapeSpace)
                {
                    outBuffer.append('\\');
                }

                outBuffer.append(' ');

                break;

            case '\\':
                outBuffer.append('\\');
                outBuffer.append('\\');

                break;

            case '\t':
                outBuffer.append('\\');
                outBuffer.append('t');

                break;

            case '\n':
                outBuffer.append('\\');
                outBuffer.append('n');

                break;

            case '\r':
                outBuffer.append('\\');
                outBuffer.append('r');

                break;

            case '\f':
                outBuffer.append('\\');
                outBuffer.append('f');

                break;

            default:

                if ((aChar < 0x0020) || (aChar > 0x007e))
                {
                    outBuffer.append('\\');
                    outBuffer.append('u');
                    outBuffer.append(toHex((aChar >> 12) & 0xF));
                    outBuffer.append(toHex((aChar >> 8) & 0xF));
                    outBuffer.append(toHex((aChar >> 4) & 0xF));
                    outBuffer.append(toHex(aChar & 0xF));
                }
                else
                {
                    if (specialSaveChars.indexOf(aChar) != -1)
                    {
                        outBuffer.append('\\');
                    }

                    outBuffer.append(aChar);
                }
            }
        }

        return outBuffer.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
