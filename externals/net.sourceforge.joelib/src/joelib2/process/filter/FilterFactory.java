///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: FilterFactory.java,v $
//  Purpose:  Factory class to get loader/writer classes.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:38 $
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
package joelib2.process.filter;

import wsi.ra.tool.BasicPropertyHolder;

/*
 *==========================================================================*
 *IMPORTS
 *==========================================================================
 */
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Category;


/**
 * Factory class to get molecule process filter classes.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:38 $
 */
public class FilterFactory
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.process.filter.FilterFactory");
    private final static int DEFAULT_FILTER_NUMBER = 20;
    private static FilterFactory instance;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable filterHolder;
    private BasicPropertyHolder propertyHolder;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEFileFormat object
     */
    private FilterFactory()
    {
        propertyHolder = BasicPropertyHolder.instance();

        filterHolder = new Hashtable(DEFAULT_FILTER_NUMBER);

        loadInfos();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized FilterFactory instance()
    {
        if (instance == null)
        {
            instance = new FilterFactory();
        }

        return instance;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Enumeration filters()
    {
        return filterHolder.keys();
    }

    /**
     *  Gets the external attribute of the ExternalFactory class
     *
     * @param  processName              Description of the Parameter
     * @return                          The external value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public Filter getFilter(String filterName) throws FilterException
    {
        // try to load Filter representation class
        Filter filter = null;

        FilterInfo filterInfo = getFilterInfo(filterName);

        if (filterInfo == null)
        {
            return null;

            //      throw new FilterException( "Filter '" + filterName + "' is not defined" );
        }

        try
        {
            filter = (Filter) Class.forName(filterInfo.getRepresentation())
                                   .newInstance();
        }
        catch (ClassNotFoundException ex)
        {
            throw new FilterException(filterInfo.getRepresentation() +
                " not found.");
        }
        catch (InstantiationException ex)
        {
            throw new FilterException(filterInfo.getRepresentation() +
                " can not be instantiated.");
        }
        catch (IllegalAccessException ex)
        {
            throw new FilterException(filterInfo.getRepresentation() +
                " can't be accessed.");
        }

        //
        if (filter == null)
        {
            throw new FilterException("Filter class " +
                filterInfo.getRepresentation() + " does'nt exist.");
        }
        else
        {
            filter.setFilterInfo(filterInfo);

            return filter;
        }
    }

    /**
     *  Gets the externalInfo attribute of the ExternalFactory object
     *
     * @param  name  Description of the Parameter
     * @return       The externalInfo value
     */
    public FilterInfo getFilterInfo(String name)
    {
        return (FilterInfo) filterHolder.get(name);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private boolean loadInfos()
    {
        String name;
        String representation;
        String descriptionFile;
        Properties prop = propertyHolder.getProperties();
        FilterInfo filterInfo;

        boolean allInfosLoaded = true;
        int i = 0;
        String filter_i;

        while (true)
        {
            i++;
            filter_i = "joelib2.filter." + i;
            name = prop.getProperty(filter_i + ".name");

            if (name == null)
            {
                logger.info("" + (i - 1) + " filter informations loaded.");

                break;
            }

            representation = prop.getProperty(filter_i + ".representation");
            descriptionFile = prop.getProperty(filter_i + ".descriptionFile");

            filterInfo = new FilterInfo(name, representation, descriptionFile);

            if ((name != null) && (representation != null) &&
                    (descriptionFile != null))
            {
                filterHolder.put(name, filterInfo);
            }
            else
            {
                allInfosLoaded = false;

                logger.error("Filter info number " + i +
                    " not properly defined.");
            }
        }

        return allInfosLoaded;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
