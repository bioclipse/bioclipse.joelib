///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SimpleExternalProcess.java,v $
//  Purpose:  Calls corina to create 3D structures.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:29 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
///////////////////////////////////////////////////////////////////////////////
package joelib2.ext;

import joelib2.molecule.Molecule;

import joelib2.process.MoleculeProcessException;
import joelib2.process.ProcessInfo;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Simple external process representation.
 *
 * A simple example for calling external programs is the {@link Title2Data} class.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:29 $
 * @see Title2Data
 */
public class SimpleExternalProcess implements External
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.ext.SimpleExternalProcess");

    //~ Instance fields ////////////////////////////////////////////////////////

    private ExternalInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Corina object
     */
    public SimpleExternalProcess()
    {
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
     *  Gets the descriptionFile attribute of the Corina object
     *
     * @return    The descriptionFile value
     */
    public String getDescriptionFile()
    {
        return info.getDescriptionFile();
    }

    /**
     *  Gets the externalInfo attribute of the Corina object
     *
     * @return    The externalInfo value
     */
    public ExternalInfo getExternalInfo()
    {
        return info;
    }

    /**
     *  Gets the processInfo attribute of the ProcessPipe object
     *
     * @return    The processInfo value
     */
    public ProcessInfo getProcessInfo()
    {
        return (ProcessInfo) info;
    }

    /**
     *  Gets the thisOSsupported attribute of the Corina object
     *
     * @return    The thisOSsupported value
     */
    public boolean isThisOSsupported()
    {
        if (ExternalHelper.getOperationSystemName().equals(
                    ExternalHelper.OS_LINUX) ||
                ExternalHelper.getOperationSystemName().equals(
                    ExternalHelper.OS_WINDOWS) ||
                ExternalHelper.getOperationSystemName().equals(
                    ExternalHelper.OS_SOLARIS))
        {
            return true;
        }
        else
        {
            return false;
        }
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
        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition for process or missing property entry.");

            return false;
        }

        return true;
    }

    /**
     *  Sets the externalInfo attribute of the Corina object
     *
     * @param  _info  The new externalInfo value
     */
    public void setExternalInfo(ExternalInfo _info)
    {
        info = _info;
    }

    /**
     *  Sets the processInfo attribute of the ProcessPipe object
     *
     * @param  _info  The new processInfo value
     */
    public void setProcessInfo(ProcessInfo _info)
    {
        info.setName(_info.getName());
        info.setRepresentation(_info.getRepresentation());
        info.setDescriptionFile(_info.getDescriptionFile());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
