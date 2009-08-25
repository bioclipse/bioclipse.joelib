///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: PropertyHolder.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.10 $
//          $Date: 2005/02/17 16:48:44 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package wsi.ra.tool;

import java.net.URL;

import java.util.Properties;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.10 $, $Date: 2005/02/17 16:48:44 $
 */
public interface PropertyHolder
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Method getDouble.
     * @param obj
     * @param property
     * @return double
     */
    double getDouble(Object obj, String property, double def);

    /**
     * Method getDouble.
     * @param obj
     * @param property
     * @param min
     * @param max
     * @param def
     * @return double
     */
    double getDouble(Object obj, String property, double min, double max,
        double def);

    int getInt(String property);

    /**
     *  Gets the int attribute of the PropertyHolder object
     *
     * @param  property  Description of the Parameter
     * @return           The int value
     */
    int getInt(String property, int def);

    /**
     * Method getInt.
     * @param obj
     * @param property
     * @return int
     */
    int getInt(Object obj, String property, int def);

    /**
     *  Gets the int attribute of the PropertyHolder object
     *
     * @param  property  Description of the Parameter
     * @param  min       Description of the Parameter
     * @param  max       Description of the Parameter
     * @param  def       Description of the Parameter
     * @return           The int value
     */
    int getInt(String property, int min, int max, int def);

    /**
     * Method getInt.
     * @param obj
     * @param property
     * @param min
     * @param max
     * @param def
     * @return int
     */
    int getInt(Object obj, String property, int min, int max, int def);

    /**
     *  Gets the properties attribute of the PropertyHolder object
     *
     * @return    The properties value
     */
    Properties getProperties();

    /**
     *  Gets the property attribute of the RegressionHelper object
     *
     * @param  property  Description of the Parameter
     * @return           The property value
     */
    String getProperty(Object obj, String property);

    /**
     *  Gets the propFile attribute of the PropertyHolder object
     *
     * @return    The propFile value
     */
    String getPropFile();

    /**
     *  Gets the uRLFromProperty attribute of the PropertyHolder object
     *
     * @param  propName  Description of the Parameter
     * @return           The uRLFromProperty value
     */
    URL getURLFromProperty(String propName);

    /**
     *  Gets the uRLFromProperty attribute of the PropertyHolder object
     *
     * @param  propName   Description of the Parameter
     * @param  showError  Description of the Parameter
     * @return            The uRLFromProperty value
     */
    URL getURLFromProperty(String propName, boolean showError);

    void store(String saveAs, String generatingClass);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
