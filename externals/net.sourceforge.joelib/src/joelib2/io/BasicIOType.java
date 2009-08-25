///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicIOType.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:34 $
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
package joelib2.io;

import org.apache.log4j.Category;


/**
 * Input/output type for molecules.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:34 $
 */
public final class BasicIOType implements java.io.Serializable, IOType
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(BasicIOType.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private String name;
    private String representation;
    private int typeNumber;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IOType object
     *
     * @param  _value           Description of the Parameter
     * @param  _name            Description of the Parameter
     * @param  _representation  Description of the Parameter
     */
    protected BasicIOType(String _name, String _representation, int _typeNumber)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initializing " + this.getClass().getName());
        }

        typeNumber = _typeNumber;
        name = _name;
        representation = _representation;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  is  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean equals(Object obj)
    {
        if ((obj instanceof BasicIOType) && (obj != null))
        {
            if (((BasicIOType) obj).typeNumber == typeNumber)
            {
                return true;
            }
        }

        return false;
    }

    /**
     *  Gets the name attribute of the IOType class
     *
     * @param  type  Description of the Parameter
     * @return       The name value
     */
    public String getName()
    {
        return name;
    }

    /**
     *  Gets the representation attribute of the IOType class
     *
     * @param  type  Description of the Parameter
     * @return       The representation value
     */
    public String getRepresentation()
    {
        return representation;
    }

    /**
     *  Gets the type attribute of the IOType class
     *
     * @param  _value  Description of the Parameter
     * @return         The type value
     */
    public int getTypeNumber()
    {
        return typeNumber;
    }

    public int hashCode()
    {
        return this.typeNumber;
    }

    /**
     *  Sets the representation attribute of the IOType object
     *
     * @param  _representation  The new representation value
     */
    public void setRepresentation(String _representation)
    {
        representation = _representation;
    }

    public String toString()
    {
        return name;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
