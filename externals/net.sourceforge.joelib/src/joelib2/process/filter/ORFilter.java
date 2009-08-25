///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ORFilter.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
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

/*==========================================================================*
 * IMPORTS
 *==========================================================================  */

import joelib2.molecule.Molecule;

import org.apache.log4j.Category;


/**
 * Molecule process filter OR condition.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:38 $
 */
public class ORFilter implements Filter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.process.filter.ORFilter");

    //~ Instance fields ////////////////////////////////////////////////////////

    private Filter filter1;
    private Filter filter2;
    private FilterInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorFilter object
     */
    public ORFilter()
    {
    }

    /**
     *  Constructor for the DescriptorFilter object
     *
     * @param  descNamesURL  Description of the Parameter
     */
    public ORFilter(Filter _filter1, Filter _filter2)
    {
        init(_filter1, _filter2);
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
        if ((filter1 == null) || (filter2 == null))
        {
            logger.warn("Filters not defined in " + this.getClass().getName() +
                ".");

            return false;
        }

        return (filter1.accept(mol) || filter2.accept(mol));
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
    public void init(Filter _filter1, Filter _filter2)
    {
        filter1 = _filter1;
        filter2 = _filter2;
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
