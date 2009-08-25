///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: BatchScriptReplacer.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.8 $
//          $Date: 2005/02/17 16:48:43 $
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
package wsi.ra.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Hashtable;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.8 $, $Date: 2005/02/17 16:48:43 $
 */
public interface BatchScriptReplacer
{
    //~ Methods ////////////////////////////////////////////////////////////////

    //  static public boolean fromFile(String in_file, String out_file, Hashtable variables)
    boolean createBatchFile(Reader reader, Writer writer, Hashtable variables);

    /**
     *  Description of the Method
     *
     *@param  resourceLocation  Description of the Parameter
     *@param  outputFile        Description of the Parameter
     *@param  variables         Description of the Parameter
     *@return                   Description of the Return Value
     */
    boolean fromResource(String resourceLocation, String outputFile,
        Hashtable variables) throws IOException;

    /**
     *  Gets the quoteCharacter attribute of the BatchScriptReplacer object
     *
     *@return    The quoteCharacter value
     */
    char getQuoteCharacter();

    /**
     *  Sets the quoteCharacter attribute of the BatchScriptReplacer object
     *
     *@param  _quotingCharacter  The new quoteCharacter value
     */
    void setQuoteCharacter(char _quotingCharacter);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
