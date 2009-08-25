///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ProcessFactory.java,v $
//  Purpose:  Factory class to get loader/writer classes.
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

import wsi.ra.tool.BasicPropertyHolder;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Category;


/**
 *  Factory class to get molecule processing classes.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:38 $
 */
public class ProcessFactory
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.process.ProcessFactory");
    private final static int DEFAULT_PROCESS_NUMBER = 20;
    private static ProcessFactory instance;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable processHolder;
    private BasicPropertyHolder propertyHolder;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEFileFormat object
     */
    private ProcessFactory()
    {
        propertyHolder = BasicPropertyHolder.instance();

        processHolder = new Hashtable(DEFAULT_PROCESS_NUMBER);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized ProcessFactory instance()
    {
        if (instance == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " +
                    ProcessFactory.class.getClass().getName() + " instance.");
            }

            instance = new ProcessFactory();
            instance.loadInfos();
        }

        return instance;
    }

    /**
     *  Gets the external attribute of the ExternalFactory class
     *
     * @param  processName              Description of the Parameter
     * @return                          The external value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public MoleculeProcess getProcess(String processName)
        throws MoleculeProcessException
    {
        // try to load Process representation class
        MoleculeProcess process = null;

        ProcessInfo processInfo = getProcessInfo(processName);

        if (processInfo == null)
        {
            return null;

            //      throw new JOEProcessException( "Process '" + processName + "' is not defined" );
        }

        try
        {
            process = (MoleculeProcess)this.getClass().getClassLoader()
                .loadClass(processInfo.getRepresentation())
                .newInstance();
        }
        catch (ClassNotFoundException ex)
        {
            throw new MoleculeProcessException(processInfo.getRepresentation() +
                " not found.");
        }
        catch (InstantiationException ex)
        {
            throw new MoleculeProcessException(processInfo.getRepresentation() +
                " can not be instantiated.");
        }
        catch (IllegalAccessException ex)
        {
            throw new MoleculeProcessException(processInfo.getRepresentation() +
                " can't be accessed.");
        }

        //
        if (process == null)
        {
            throw new MoleculeProcessException("Process class " +
                processInfo.getRepresentation() + " does'nt exist.");
        }
        else
        {
            process.setProcessInfo(processInfo);

            return process;
        }
    }

    /**
     *  Gets the externalInfo attribute of the ExternalFactory object
     *
     * @param  name  Description of the Parameter
     * @return       The externalInfo value
     */
    public ProcessInfo getProcessInfo(String name)
    {
        return (ProcessInfo) processHolder.get(name);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Enumeration processes()
    {
        return processHolder.keys();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private synchronized boolean loadInfos()
    {
        String name;
        String representation;
        String descriptionFile;
        Properties prop = propertyHolder.getProperties();
        ProcessInfo processInfo;

        boolean allInfosLoaded = true;
        int i = 0;
        String process_i;

        while (true)
        {
            i++;
            process_i = "joelib2.process." + i;
            name = prop.getProperty(process_i + ".name");

            if (name == null)
            {
                logger.info("" + (i - 1) + " process informations loaded.");

                break;
            }

            representation = prop.getProperty(process_i + ".representation");
            descriptionFile = prop.getProperty(process_i + ".descriptionFile");

            processInfo = new ProcessInfo(name, representation,
                    descriptionFile);

            if ((name != null) && (representation != null) &&
                    (descriptionFile != null))
            {
                processHolder.put(name, processInfo);
            }
            else
            {
                allInfosLoaded = false;

                logger.error("Process info number " + i +
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
