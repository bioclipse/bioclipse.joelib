///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: PropertyHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2007/03/03 00:03:50 $
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

import joelib2.process.MoleculeProcessException;

import java.util.Map;

import org.apache.log4j.Category;


/**
 *  Some helper methods for calling classes which accept properties ({@link PropertyAcceptor}).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2007/03/03 00:03:50 $
 */
public class PropertyHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.util.JOEPropertyHelper");

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Don't let anyone instantiate this class
     */
    private PropertyHelper()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  process                  Description of the Parameter
     * @param  availProperties          Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public static boolean checkProperties(PropertyAcceptor propAcceptor,
        Map availProperties) // throws JOEPropertyException
    {
        BasicProperty[] acceptedProperties = propAcceptor.acceptedProperties();

        if (acceptedProperties == null)
        {
            return true;
        }

        //    if(availProperties==null)
        //    {
        //      logger.warn("Empty property definition.");
        //      return false;
        //    }
        //      boolean allPropAvailable=true;
        String propName;
        String representation;

        for (int i = 0; i < acceptedProperties.length; i++)
        {
            propName = acceptedProperties[i].propName;
            representation = acceptedProperties[i].representation;

            if (!acceptedProperties[i].isOptional())
            {
                if ((availProperties == null) ||
                        !availProperties.containsKey(propName))
                {
                    logger.error("Process parameter/property '" + propName +
                        "'" + " with type '" + representation +
                        "' is missing in " + propAcceptor.getClass().getName() +
                        ".");

                    //          throw new JOEPropertyException("Process parameter/property '" + propName + "'" +
                    //              " with type '" + representation + "' is missing.");
                }

                //          allPropAvailable=false;
            }
            else
            {
                if (availProperties != null)
                {
                    Object prop = availProperties.get(
                            acceptedProperties[i].propName);

                    //                    if ((prop != null) &&
                    //                            !(prop instanceof representation))
                    //                    {
                    //                        logger.error("Parameter/property '" + propName + "'" +
                    //                            " should be of type '" + representation.getName() +
                    //                            "' not of type '" + prop.getClass().getName() +
                    //                            "' in " + propAcceptor.getClass().getName() + ".");
                    //
                    //                        //            throw new JOEPropertyException("Parameter/property '" + propName + "'" +
                    //                        //                " should be of type '" + representation + "' not of type '" +
                    //                        //                prop.getClass().getName() + "'.");
                    //                    }
                }
            }
        }

        //      return allPropAvailable;
        return true;
    }

    /**
     *  Gets single process property.
     *
     * @param  property                 Description of the Parameter
     * @param  availProperties          Description of the Parameter
     * @return                          The property value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public static Object getProperty(String property, Map availProperties) // throws JOEPropertyException
    {
        if (availProperties == null)
        {
            return null;

            // exception only reasonable if known if this is an optinal parameter
            //      throw new JOEProcessException("Can not get parameter/property '" + property + "'" +
            //                " because propertiy definition is empty.");
        }

        return availProperties.get(property);
    }

    /**
     *  Gets single process property or default value if not defined or <tt>null
     *  </tt>.
     *
     * @param  process                  Description of the Parameter
     * @param  property                 Description of the Parameter
     * @param  availProperties          Description of the Parameter
     * @return                          The property value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public static Object getProperty(PropertyAcceptor propAcceptor,
        String property, Map availProperties) // throws JOEProcessException
    {
        // do nothing
        // go on and check default properties
        // exception only reasonable if known if this is an optional parameter
        //      if (availProperties == null)
        //        {
        //            //      throw new JOEProcessException("Can not get parameter/property '" + property + "'" +
        //            //                " because propertiy definition is empty.");
        //        }
        //        else
        //        {
        // return property
        if (availProperties == null)
        {
            return null;
        }

        Object objProperty = availProperties.get(property);

        if (objProperty != null)
        {
            return objProperty;
        }

        //        }
        // property not found or null
        BasicProperty[] acceptedProperties = propAcceptor.acceptedProperties();
        String propName;

        for (int i = 0; i < acceptedProperties.length; i++)
        {
            propName = acceptedProperties[i].propName;

            //      System.out.println("TEST: " + propName + " " + propName.equals(property));
            if (propName.equals(property))
            {
                return acceptedProperties[i].getDefaultProperty();
            }
        }

        return null;
    }

    /**
     *  Sets the property attribute of the JOEPropertyHelper class
     *
     * @param  property                 The new property value
     * @param  availProperties          The new property value
     * @param  obj                      The new property value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public static void setProperty(String property, Map availProperties,
        Object obj) throws MoleculeProcessException
    {
        if (availProperties == null)
        {
            return;
        }

        availProperties.put(property, obj);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
