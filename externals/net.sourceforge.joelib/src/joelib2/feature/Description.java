///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Description.java,v $
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
package joelib2.feature;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 * Description for a descriptor.
 * Link to the {@link #TEXT}, {@link #HTML} and {@link #XML} description for this descriptor.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:29 $
 */
public final class Description
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public final static Description UNDEFINED = new Description(0, "UNDEFINED");

    /**
     *  Description of the Field
     */
    public final static Description TEXT = new Description(1, "TEXT");

    /**
     *  Description of the Field
     */
    public final static Description HTML = new Description(2, "HTML");

    /**
     *  Description of the Field
     */
    public final static Description XML = new Description(3, "XML");

    //public static final int    minType         = 0;

    /**
     *  Number of description types.
     */
    public final static int MAX_TYPE = 3;

    /**
     * Holds all description types.
     */
    private static Hashtable descHolder;
    private static String name;

    //~ Instance fields ////////////////////////////////////////////////////////

    private int value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Description object
     *
     * @param  _value           Description of the Parameter
     * @param  _name            Description of the Parameter
     * @param  _representation  Description of the Parameter
     */
    private Description(int _value, String _name)
    {
        if (descHolder == null)
        {
            descHolder = new Hashtable(MAX_TYPE + 1);
        }

        value = _value;
        name = _name;

        if (descHolder.get(name) == null)
        {
            descHolder.put(name, this);
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the name attribute of the Description class
     *
     * @param  type  Description of the Parameter
     * @return       The name value
     */
    public static String getName(Description type)
    {
        String name = Description.UNDEFINED.getName();

        for (Enumeration e = descHolder.elements(); e.hasMoreElements();)
        {
            Description description = (Description) e.nextElement();

            if (description.value == type.value)
            {
                name = Description.getName(description.value);
            }
        }

        return name;
    }

    /**
     *  Gets the name attribute of the Description class
     *
     * @param  _value  Description of the Parameter
     * @return         The name value
     */
    public static String getName(int _value)
    {
        return getType(_value).getName();
    }

    /**
    *  Gets the type attribute of the Description class
    *
    * @param  _value  Description of the Parameter
    * @return         The type value
    */
    public static Description getType(int _value)
    {
        Description doc = Description.UNDEFINED;

        for (Enumeration e = descHolder.elements(); e.hasMoreElements();)
        {
            Description description = (Description) e.nextElement();

            if (description.value == _value)
            {
                doc = description;
            }
        }

        return doc;
    }

    /**
     *  Gets the type attribute of the Description class
     *
     * @param  _name  Description of the Parameter
     * @return        The type value
     */
    public static Description getType(String _name)
    {
        Description doc = Description.UNDEFINED;

        if (descHolder.get(_name) != null)
        {
            doc = (Description) descHolder.get(name);
        }

        return doc;
    }

    /**
     *  Gets the value attribute of the Description class
     *
     * @param  type  Description of the Parameter
     * @return       The value value
     */
    public static int getValue(Description type)
    {
        int docType = Description.UNDEFINED.value;

        for (Enumeration e = descHolder.elements(); e.hasMoreElements();)
        {
            Description description = (Description) e.nextElement();

            if (description.value == type.value)
            {
                docType = description.value;
            }
        }

        return docType;
    }

    /**
     *  Gets the valueFromName attribute of the Description class
     *
     * @param  _name  Description of the Parameter
     * @return        The valueFromName value
     */
    public static int getValueFromName(String _name)
    {
        return getType(_name).value;
    }

    /**
     *  Description of the Method
     *
     * @param  other  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean equals(Object other)
    {
        boolean isEqual = false;

        if ((other instanceof Description) && (other != null))
        {
            if (((Description) other).value == value)
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    /**
     *  Gets the name attribute of the Description object
     *
     * @return    The name value
     */
    public String getName()
    {
        return name;
    }

    /**
     *  Gets the value attribute of the Description object
     *
     * @return    The value value
     */
    public int getValue()
    {
        return value;
    }

    public int hashCode()
    {
        return value;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
