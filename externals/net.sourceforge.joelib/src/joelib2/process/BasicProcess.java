///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicProcess.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
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
package joelib2.process;

import joelib2.molecule.Molecule;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Simple process represenation.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:38 $
 */
public class BasicProcess implements MoleculeProcess, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(BasicProcess.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private ProcessInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescSelectionWriter object
     */
    public BasicProcess()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initializing " + this.getClass().getName());
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public BasicProperty[] acceptedProperties()
    {
        return null;
    }

    public boolean clear()
    {
        return true;
    }

    /**
     *  Gets the processInfo attribute of the ProcessPipe object
     *
     * @return    The processInfo value
     */
    public ProcessInfo getProcessInfo()
    {
        return info;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException
    {
        //    System.out.println("simple");
        if (!PropertyHelper.checkProperties(this, properties))
        {
            throw new MoleculeProcessException("Properties not correct.");
        }

        return true;
    }

    /**
     *  Sets the processInfo attribute of the ProcessPipe object
     *
     * @param  _info  The new processInfo value
     */
    public void setProcessInfo(ProcessInfo _info)
    {
        info = _info;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
