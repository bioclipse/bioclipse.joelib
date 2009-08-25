///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicProperty.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:41 $
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
package joelib2.util;

/**
 * JOELib property representation.
 *
 * <p>
 * Properties will be defined here for three reasons:
 * <ol>
 * <li> Type checking, for all possible parameters you can imagine
 * <li> Is this an optional parameter entry ?
 * <li> Allow helper classes to access these data elements (easier)...
 * </ol>
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:41 $
 * @see PropertyHelper
 */
public class BasicProperty implements Property
{
    //~ Instance fields ////////////////////////////////////////////////////////

    protected Object defaultProperty;
    protected String description;

    /**
     * <tt>true</tt> if this parameter is optional, <tt>false</tt> otherwise.
     */
    protected boolean optional;
    protected String propName;
    protected String representation;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for a JOELib property without default value.
     *
     * <p>
     * The <tt>_representation</tt> defines the object type. Here are some examples
     * <blockquote><pre>
     * java.lang.Boolean      - {@link Boolean}
     * [Ljava.lang.String;    - {@link String} array !!!
     * java.lang.Integer      - {@link Integer}
     * </pre></blockquote>
     *
     * @param  _name             name of the parameter
     * @param  _representation   data type (representation) of the parameter.
     *                                                         Arrays looks like: '[Lclass;', e.g. '[Ljava.lang.String;'
     * @param  _description  description of this parameter
     * @param  _optional  <tt>true</tt> if this parameter is optional
     */
    public BasicProperty(String _name, String _representation,
        String _description, boolean _optional)
    {
        this(_name, _representation, _description, _optional, null);
    }

    /**
     *  Constructor for a JOELib property.
     *
     * <p>
     * The <tt>_representation</tt> defines the object type. Here are some examples
     * <blockquote><pre>
     * java.lang.Boolean      - {@link Boolean}
     * [Ljava.lang.String;    - {@link String} array !!!
     * java.lang.Integer      - {@link Integer}
     * </pre></blockquote>
     *
     *
     *
     * @param  _name             name of the parameter
     * @param  _representation   data type (representation) of the parameter.
     *                                                         Arrays looks like: '[Lclass;', e.g. '[Ljava.lang.String;'.
     * @param  _description  description of this parameter
     * @param  _optional  <tt>true</tt> if this parameter is optional
     * @param  _defaultProperty  default value of this parameter which must be of type <tt>_representation</tt>
     */
    public BasicProperty(String _name, String _representation,
        String _description, boolean _optional, Object _defaultProperty)
    {
        propName = _name;
        representation = _representation;
        description = _description;
        optional = _optional;
        defaultProperty = _defaultProperty;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object obj)
    {
        if (obj instanceof BasicProperty)
        {
            if (propName.equals(((BasicProperty) obj).propName))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public Object getDefaultProperty()
    {
        return defaultProperty;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return Returns the propName.
     */
    public String getPropName()
    {
        return propName;
    }

    /**
     * @return Returns the representation.
     */
    public String getRepresentation()
    {
        return representation;
    }

    public int hashCode()
    {
        if (description == null)
        {
            return 0;
        }
        else
        {
            return description.hashCode();
        }
    }

    public boolean isOptional()
    {
        return optional;
    }

    public Object setDefaultProperty(Object defaultObj)
    {
        Object tmp = defaultProperty;
        defaultProperty = defaultObj;

        return tmp;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @param optional The optional to set.
     */
    public void setOptional(boolean optional)
    {
        this.optional = optional;
    }

    /**
     * @param propName The propName to set.
     */
    public void setPropName(String propName)
    {
        this.propName = propName;
    }

    /**
     * @param representation The representation to set.
     */
    public void setRepresentation(String representation)
    {
        this.representation = representation;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
