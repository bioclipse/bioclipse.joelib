///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ExternalInfo.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.ext;

import joelib2.process.ProcessInfo;

import java.util.List;


/**
 * Informations for an external process.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:29 $
 */
public class ExternalInfo extends ProcessInfo
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    protected List arguments;

    /**
     *  Description of the Field
     */
    protected String linux;

    /**
     *  Description of the Field
     */
    protected String solaris;

    /**
     *  Description of the Field
     */
    protected String windows;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorInfo object
     *
     * @param  _name             Description of the Parameter
     * @param  _representation   Description of the Parameter
     * @param  _descriptionFile  Description of the Parameter
     * @param  _linux            Description of the Parameter
     * @param  _windows          Description of the Parameter
     * @param  _solaris          Description of the Parameter
     * @param  _arguments        Description of the Parameter
     */
    public ExternalInfo(String _name, String _representation,
        String _descriptionFile, String _linux, String _windows,
        String _solaris, List _arguments)
    {
        super(_name, _representation, _descriptionFile);
        linux = _linux;
        windows = _windows;
        solaris = _solaris;
        arguments = _arguments;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the arguments attribute of the ExternalInfo object
     *
     * @return    The arguments value
     */
    public List getArguments()
    {
        return arguments;
    }

    /**
     * Gets the executable for the actual operating system.
     *
     * @return    The executable value
     */
    public String getExecutable()
    {
        return getExecutable(ExternalHelper.getOperationSystemName());
    }

    /**
     *  Gets the executable attribute of the ExternalInfo object
     *
     * @param  osName  Description of the Parameter
     * @return         The executable value
     */
    public String getExecutable(String osName)
    {
        //      String os=ExternalHelper.getOperationSystemName();
        if (osName.equals(ExternalHelper.OS_WINDOWS))
        {
            return windows;
        }
        else if (osName.equals(ExternalHelper.OS_LINUX))
        {
            return linux;
        }
        else if (osName.equals(ExternalHelper.OS_SOLARIS))
        {
            return solaris;
        }

        return null;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer(100);

        sb.append("<name:");
        sb.append(name);
        sb.append(", representation class:");
        sb.append(representation);

        if (arguments != null)
        {
            for (int i = 0; i < arguments.size(); i++)
            {
                sb.append(", arg" + i + ":");
                sb.append(arguments.get(i));
            }
        }
        else
        {
            sb.append(", <no args>");
        }

        sb.append(">");

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
