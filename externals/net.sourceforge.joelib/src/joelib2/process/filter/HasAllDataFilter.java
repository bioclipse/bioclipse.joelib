///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: HasAllDataFilter.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
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

import joelib2.molecule.Molecule;

import wsi.ra.tool.BasicResourceLoader;

import java.util.List;

import org.apache.log4j.Category;


/**
 * Molecule process filter for available descriptor entries.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:38 $
 */
public class HasAllDataFilter implements Filter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.process.filter.HasAllDataFilter");

    //~ Instance fields ////////////////////////////////////////////////////////

    private List attributes;

    private FilterInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorFilter object
     */
    public HasAllDataFilter()
    {
    }

    /**
     *  Constructor for the DescriptorFilter object
     *
     * @param  descNamesURL  Description of the Parameter
     */
    public HasAllDataFilter(String filename)
    {
        List tmpInNames = BasicResourceLoader.readLines(filename);

        if (tmpInNames == null)
        {
            logger.error("File with data names " + filename +
                " could not be found.");

            return;
        }

        init(tmpInNames);
    }

    public HasAllDataFilter(List _attributes)
    {
        init(_attributes);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean accept(Molecule mol)
    {
        if ((attributes == null) || (attributes.size() == 0))
        {
            logger.warn("No data attributes defined in " +
                this.getClass().getName() + ".");

            return false;
        }

        int s = attributes.size();

        for (int i = 0; i < s; i++)
        {
            if (!mol.hasData((String) attributes.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    /**
     *  Gets the processInfo attribute of the DescriptorFilter object
     *
     * @return    The processInfo value
     */
    public FilterInfo getFilterInfo()
    {
        return info;
    }

    /**
     *  Description of the Method
     *
     * @param  _descNames  Description of the Parameter
     */
    public void init(List _attributes)
    {
        attributes = _attributes;
    }

    /**
     *  Sets the filterInfo attribute of the DescriptorFilter object
     *
     * @param  _info  The new filterInfo value
     */
    public void setFilterInfo(FilterInfo _info)
    {
        info = _info;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
