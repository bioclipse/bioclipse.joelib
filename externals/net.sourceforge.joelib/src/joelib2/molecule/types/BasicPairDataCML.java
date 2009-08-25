///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: BasicPairDataCML.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.5 $
//          $Date: 2005/02/17 16:48:37 $
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
package joelib2.molecule.types;

import joelib2.util.types.StringString;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.5 $, $Date: 2005/02/17 16:48:37 $
 */
public class BasicPairDataCML extends BasicPairData implements Cloneable,
    PairDataCML
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable<String, StringString> cmlProperties;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *
     */
    public BasicPairDataCML()
    {
        super();
    }

    /**
     * @param attribute
     * @param value
     */
    public BasicPairDataCML(String attribute, Object value)
    {
        super(attribute, value);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void addCMLProperty(StringString property)
    {
        if (cmlProperties == null)
        {
            cmlProperties = new Hashtable<String, StringString>();
        }

        cmlProperties.put(property.getStringValue1(), property);
    }

    /**
     * Warning: The key and the keyValue are no deep clones!
     * @param other
     */
    public Object clone()
    {
        BasicPairDataCML other = new BasicPairDataCML(key, keyValue);
        other.cmlProperties = (Hashtable<String, StringString>) this
            .cmlProperties.clone();

        return other;
    }

    /**
     * Warning: The key and the keyValue are no deep clones!
     * @param other
     */
    public BasicPairDataCML clone(BasicPairDataCML other)
    {
        super.clone(other);

        if ((other != null) && (this.cmlProperties != null))
        {
            other.cmlProperties = (Hashtable<String, StringString>) this
                .cmlProperties.clone();
        }

        return other;
    }

    public boolean equals(Object otherObj)
    {
        // ignore cml properties and use super class
        return super.equals(otherObj);
    }

    public Enumeration<StringString> getCMLProperties()
    {
        if (cmlProperties == null)
        {
            return null;
        }

        return cmlProperties.elements();
    }

    public StringString getCMLProperty(String property)
    {
        StringString cmlProperty = null;

        if ((property != null) && (cmlProperties != null))
        {
            cmlProperty = cmlProperties.get(property);
        }

        return cmlProperty;
    }

    public boolean hasCMLProperties()
    {
        boolean hasCMLProp = false;

        if ((cmlProperties != null) && (cmlProperties.size() != 0))
        {
            hasCMLProp = true;
        }

        return hasCMLProp;
    }

    public int hashCode()
    {
        return super.hashCode();
    }
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
