///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeFileCreation.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
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
package joelib2.process.types;

import joelib2.io.BasicIOTypeHolder;

import joelib2.molecule.Molecule;

import joelib2.process.MoleculeProcess;
import joelib2.process.MoleculeProcessException;
import joelib2.process.ProcessInfo;

import joelib2.util.BasicProperty;
import joelib2.util.HelperMethods;
import joelib2.util.PropertyHelper;

import wsi.ra.io.BasicBatchFileUtilities;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/
import java.util.Map;

import org.apache.log4j.Category;


/**
 *  Calling processor classes if the filter rule fits.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:38 $
 */
public class MoleculeFileCreation implements MoleculeProcess
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            MoleculeFileCreation.class.getName());
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty("IO_TYPE", "java.lang.String",
                "IOType of the file to create", false),
            new BasicProperty("FILE", "java.lang.String",
                "Filename of the file that will be created in the temporary directory.",
                true, "joelib-molecule.tmp")
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private ProcessInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescSelectionWriter object
     */
    public MoleculeFileCreation()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public BasicProperty[] acceptedProperties()
    {
        return ACCEPTED_PROPERTIES;
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
        // check properties
        if (!PropertyHelper.checkProperties(this, properties))
        {
            throw new MoleculeProcessException(
                "Empty property definition for process or missing property entry.");

            //return false;
        }

        // get plain file name
        String filename = (String) PropertyHelper.getProperty(this, "FILE",
                properties);

        // create full path to file and create file
        String fullFilename = HelperMethods.getTempFileBase() + filename;

        try
        {
            fullFilename = BasicBatchFileUtilities.instance().createNewFileName(
                    fullFilename);
        }
        catch (Exception ex)
        {
            logger.error(ex.toString());

            return false;
        }

        // get io type and create molecule file
        String ioTypeName = (String) PropertyHelper.getProperty("IO_TYPE",
                properties);

        if (!HelperMethods.moleculeToFile(fullFilename, mol,
                    BasicIOTypeHolder.instance().getIOType(ioTypeName), true))
        {
            return false;
        }

        // store created file name in properties
        PropertyHelper.setProperty("FILE", properties, fullFilename);

        if (logger.isDebugEnabled())
        {
            logger.debug("Molecule filename  '" + fullFilename + "' created.");
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
