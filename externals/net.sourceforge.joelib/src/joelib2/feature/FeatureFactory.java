///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: FeatureFactory.java,v $
//  Purpose:  Factory class to get loader/writer classes.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.feature;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Factory class to get descriptor calculation classes.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public class FeatureFactory
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(FeatureFactory.class
            .getName());
    private static FeatureFactory instance;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Initialize descriptor helper factory class.
     */
    private FeatureFactory()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets a descriptor calculation class instance.
     *
     * @param name the name of the descriptor calculation class
     * @return the descriptor calculation instance
     * @throws FeatureException the descriptor exception
     */
    public static Feature getFeature(String name) throws FeatureException
    {
        return getFeature(name, null);
    }

    /**
         * Gets a descriptor calculation class instance.
         *
         * @param name the name of the descriptor calculation class
         * @param properties the properties to initialize the descriptor calculation class
         * @return the descriptor calculation instance
         * @throws FeatureException the descriptor exception
     */
    public static Feature getFeature(String descName, Map properties)
        throws FeatureException
    {
        // try to load Descriptor representation class
        Feature descBase = null;

        BasicFeatureInfo descInfo = FeatureHelper.instance().getFeatureInfo(
                descName);

        if (descInfo != null)
        {
            try
            {
                descBase = (Feature) Class.forName(descInfo.getRepresentation())
                                          .newInstance();
            }
            catch (ClassNotFoundException ex)
            {
                throw new FeatureException(descInfo.getRepresentation() +
                    " not found.");
            }
            catch (InstantiationException ex)
            {
                throw new FeatureException(descInfo.getRepresentation() +
                    " can not be instantiated.");
            }
            catch (IllegalAccessException ex)
            {
                throw new FeatureException(descInfo.getRepresentation() +
                    " can't be accessed.");
            }

            if (descBase == null)
            {
                throw new FeatureException("Feature class " +
                    descInfo.getRepresentation() + " does'nt exist.");
            }
            else
            {
                //          descBase.setDescInfo(descInfo);
                if (properties != null)
                {
                    descBase.initialize(properties);
                }
            }
        }
        else
        {
            throw new FeatureException(
                "No information object available for descriptor with name " +
                descName);
        }

        return descBase;
    }

    /**
     * Gets the instance for the descriptor factory helper class.
     *
     * @return the instance for the descriptor factory helper class
     */
    public static synchronized FeatureFactory instance()
    {
        if (instance == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " + FeatureFactory.class.getName() +
                    " instance.");
            }

            instance = new FeatureFactory();
        }

        return instance;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
