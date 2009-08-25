///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: StringPattern.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.8 $
//          $Date: 2005/02/17 16:48:42 $
//          $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                     Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                     2003,2004,2005
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
package joelib2.util.types;

import java.util.regex.Pattern;


/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.8 $, $Date: 2005/02/17 16:48:42 $
 */
public interface StringPattern
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the pattern.
     */
    Pattern getPattern();

    /**
     * @return Returns the string.
     */
    String getString();

    /**
     * @param pattern The pattern to set.
     */
    void setPattern(Pattern pattern);

    /**
     * @param string The string to set.
     */
    void setString(String string);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
