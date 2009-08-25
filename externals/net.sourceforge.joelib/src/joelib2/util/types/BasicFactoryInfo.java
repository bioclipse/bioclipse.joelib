///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicFactoryInfo.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:42 $
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
package joelib2.util.types;

/**
 * Create informations for a class which can be get by a factory class using Java reflection.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicFactoryInfo implements java.io.Serializable, FactoryInfo
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Base directory for the descriptorion for the class.
     */
    protected String descriptionFile;

    /**
     *  Name to access the class.
     */
    protected String name;

    /**
     *  Representation for class with package path and class name.
     */
    protected String representation;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Create informations for a class which can be get by a factory class.
     *
     * @param  _name             name to access the class
     * @param  _representation   representation for class with package path and class name
     * @param  _descriptionFile  base directory for the descriptorion for the class
     */
    public BasicFactoryInfo(String name, String representation,
        String descriptionFile)
    {
        this.name = name;
        this.representation = representation;
        this.descriptionFile = descriptionFile;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets base directory for the descriptorion for the class.
     *
     * @return    base directory for the descriptorion for the class
     */
    public String getDescriptionFile()
    {
        return descriptionFile;
    }

    /**
     * Gets name to access the class.
     *
     * @return    name to access the class
     */
    public String getName()
    {
        return name;
    }

    /**
     * Representation for class with package path and class name.
     *
     * @return    representation for class with package path and class name
     */
    public String getRepresentation()
    {
        return representation;
    }

    /**
     *  Sets base directory for the descriptorion for the class.
     *
     * @param  _descriptionFile  base directory for the descriptorion for the class
     */
    public void setDescriptionFile(String descriptionFile)
    {
        this.descriptionFile = descriptionFile;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setRepresentation(String representation)
    {
        this.representation = representation;
    }

    /**
     * Gets informations for a class which can be get by a factory class.
     *
     * @return    informations for a class which can be get by a factory class
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer(100);

        sb.append("<name:");
        sb.append(name);
        sb.append(", representation class:");
        sb.append(representation);
        sb.append(", description file:");
        sb.append(descriptionFile);
        sb.append(">");

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
