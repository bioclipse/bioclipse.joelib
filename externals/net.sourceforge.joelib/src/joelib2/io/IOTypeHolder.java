///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: IOTypeHolder.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.9 $
//        $Date: 2005/02/17 16:48:34 $
//        $Author: wegner $
//
//Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                       U.S.A., 1999,2000,2001
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                       Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                       2003,2004,2005
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
package joelib2.io;

import java.util.Enumeration;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical file format
 * @.wikipedia  File format
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:34 $
 */
public interface IOTypeHolder
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns <tt>true</tt> if this file with the appropriate input type can be readed.
     *
     * @param  filename  the filename of the molecule file
     * @return           <tt>true</tt> if this file with the appropriate input type can be readed
     */
    boolean canReadExtension(String filename);

    /**
     * Returns <tt>true</tt> if this file with the appropriate output type can be written.
     *
     * @param  filename  the filename of the molecule file
     * @return           <tt>true</tt> if this file with the appropriate output type can be written
     */
    boolean canWriteExtension(String filename);

    /**
     * Gets an appropriate molecule input/output type for the given filename.
     *
     * @param  filename  the molecule filename
     * @return           the input/output type for the given filename
     */
    BasicIOType filenameToType(String filename);

    /**
     * Gets an enumeration with all available file types.
     *
     * @return    the enumeration with available file types
     */
    Enumeration getFileTypes();

    /**
     *  Gets the IOType from a given input/output name.
     *  The name must be in upper case letters.
     *
     * @param  name  Description of the Parameter
     * @return       The iOType value
     */
    BasicIOType getIOType(String name);

    /**
     * Returns <tt>true</tt> if this input type is readable.
     *
     * @param  name  input type
     * @return       <tt>true</tt> if this input type is readable
     */
    boolean isReadable(String name);

    /**
     * Returns <tt>true</tt> if this output type is writeable.
     *
     * @param  name  output type
     * @return       <tt>true</tt> if this output type is writeable
     */
    boolean isWriteable(String name);

    /**
     * Shows a table of all available input/output types.
     *
     * @return    the table of all available input/output types
     */
    String toString();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
