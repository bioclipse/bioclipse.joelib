///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: FactoryInfo.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
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

/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.3 $, $Date: 2005/02/17 16:48:42 $
 */
public interface FactoryInfo
{
    //~ Methods ////////////////////////////////////////////////////////////////

    String getDescriptionFile();

    /**
     * Gets name to access the class.
     *
     * @return    name to access the class
     */
    String getName();

    /**
     * Representation for class with package path and class name.
     *
     * @return    representation for class with package path and class name
     */
    String getRepresentation();

    /**
     *  Sets base directory for the descriptorion for the class.
     *
     * @param  _descriptionFile  base directory for the descriptorion for the class
     */
    void setDescriptionFile(String _descriptionFile);

    void setName(String _name);

    void setRepresentation(String _representation);

    /**
     * Gets informations for a class which can be get by a factory class.
     *
     * @return    informations for a class which can be get by a factory class
     */
    String toString();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
