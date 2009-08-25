///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ResourceLoaderInterface.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.1 $
//          $Date: 2005/06/17 06:33:31 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package wsi.ra.tool;

/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.1 $, $Date: 2005/06/17 06:33:31 $
 */
public interface ResourceLoaderInterface
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the byte data from a file at the given resource location.
     *
     * @param  rawResrcLoc  Description of the Parameter
     * @return                   the byte array of file.
     */
    byte[] getBytesFromResourceLocation(String rawResrcLoc);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
