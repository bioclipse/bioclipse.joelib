///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AbstractDataHolder.java,v $
//  Purpose:  Base class for handling JOELib data.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:29 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.data;

import java.util.Properties;


/**
 * Base class for handling JOELib data.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:29 $
 */
public abstract class AbstractDataHolder extends IdentifierSoftDefaultSystem
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEGlobalDataBase object
     */
    public AbstractDataHolder()
    {
        this(null);
    }

    /**
     *  Constructor for the JOEGlobalDataBase object
     *
     * @param  _prop  Description of the Parameter
     */
    public AbstractDataHolder(Properties property)
    {
        initialized = false;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  buffer  Description of the Parameter
     */
    protected abstract void parseLine(String buffer);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
