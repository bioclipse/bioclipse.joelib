///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SelectionFilter.java,v $
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
///////////////////////////////////////////////////////////////////////////////
package joelib2.process.filter;

import joelib2.molecule.Molecule;


/**
 *  Interface definition for calling external programs from JOELib.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:38 $
 */
public class SelectionFilter implements Filter
{
    //~ Instance fields ////////////////////////////////////////////////////////

    // Obtain a suitable logger.
    //  private static        Category    logger             = Category.getInstance("joelib2.process.filter.SelectionFilter");
    private FilterInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorFilter object
     */
    public SelectionFilter()
    {
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
        return false;
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
