///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ProcessPipe.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
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

import joelib2.process.filter.Filter;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Processing pipe for appending multiple processes combined with
 * {@link Filter} functionality.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:38 $
 * @see ProcessPipeEntry
 * @see Filter
 */
public class ProcessPipe implements MoleculeProcess
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(ProcessPipe.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private ProcessInfo info;

    private List processes;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the SimpleReader object
     *
     * @param  _in            Description of the Parameter
     * @param  _inTypeString  Description of the Parameter
     */
    public ProcessPipe()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initializing " + this.getClass().getName());
        }

        processes = new Vector(20);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public BasicProperty[] acceptedProperties()
    {
        return null;
    }

    public void addProcess(MoleculeProcess process)
    {
        addProcess(process, null, null);
    }

    public void addProcess(MoleculeProcess process, Hashtable properties)
    {
        addProcess(process, null, properties);
    }

    public void addProcess(MoleculeProcess process, Filter filter)
    {
        addProcess(process, filter, null);
    }

    public void addProcess(MoleculeProcess process, Filter filter,
        Hashtable properties)
    {
        ProcessPipeEntry entry;

        entry = new ProcessPipeEntry(process, filter, properties);
        processes.add(entry);
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
     * Process single molecule entry.
     *
     * @param  mol  Description of the Parameter
     * @return      <tt>true</tt> if this molecule was sucesfully processed, or
     *               <tt>false</tt> if the molecule was skipped, or an error occurred.
     */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException
    {
        if (!PropertyHelper.checkProperties(this, properties))
        {
            return false;
        }

        ProcessPipeEntry entry;
        boolean allProcessed = true;

        for (int i = 0; i < processes.size(); i++)
        {
            entry = (ProcessPipeEntry) processes.get(i);

            if ((entry.filter == null) || entry.filter.accept(mol))
            {
                //        System.out.println("start process:::");
                if (!entry.process.process(mol, entry.properties))
                {
                    allProcessed = false;

                    break;
                }
            }
            else if ((entry.filter != null) || !entry.filter.accept(mol))
            {
                return false;
            }
        }

        return allProcessed;
    }

    public boolean removeProcess(MoleculeProcess process)
    {
        return processes.remove(process);
    }

    public Object removeProcess(int index)
    {
        return processes.remove(index);
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

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
