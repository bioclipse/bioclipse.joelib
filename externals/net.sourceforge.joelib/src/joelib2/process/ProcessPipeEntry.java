///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ProcessPipeEntry.java,v $
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
package joelib2.process;

import joelib2.process.filter.Filter;

import java.util.Hashtable;


/**
 * Processing pipe entry.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:38 $
 * @see MoleculeProcess
 * @see ProcessPipe
 * @see Filter
 */
public class ProcessPipeEntry
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    protected Filter filter;

    /**
     *  Description of the Field
     */
    protected MoleculeProcess process;
    protected Hashtable properties;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the ProcessPipeEntry object
     *
     * @param  _process  Description of the Parameter
     * @param  _filter   Description of the Parameter
     */
    public ProcessPipeEntry(MoleculeProcess _process, Filter _filter,
        Hashtable _properties)
    {
        process = _process;
        filter = _filter;
        properties = _properties;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Two entries are equal, if they contain the same <tt>JOEProcess</tt>
     * object.
     *
     * @param  obj  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof ProcessPipeEntry)
        {
            if (((ProcessPipeEntry) obj).process == this.process)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else if (obj instanceof MoleculeProcess)
        {
            if (((MoleculeProcess) obj) == this.process)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        // nothing to do
        return 0;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
