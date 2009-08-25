///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicPairData.java,v $
//  Purpose:  Use to store attribute/value relationships.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:37 $
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
package joelib2.molecule.types;

import joelib2.feature.FeatureResult;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import org.apache.log4j.Category;


/**
 *  Use to store attribute/value relationships.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:37 $
 */
public class BasicPairData implements java.io.Serializable, Cloneable, PairData
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private static Category logger = Category.getInstance(BasicPairData.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    protected String key;

    /**
     *  Description of the Field
     */
    protected Object keyValue;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicPairData()
    {
    }

    /**
     *  Constructor for the PairData object
     *
     * @param  attribute  Description of the Parameter
     * @param  value      Description of the Parameter
     */
    public BasicPairData(String attribute, Object value)
    {
        setKey(attribute);
        setKeyValue(value);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Warning: The key and the keyValue are no deep clones!
     * @param other
     */
    public Object clone()
    {
        return clone(new BasicPairData(key, keyValue));
    }

    /**
     * Warning: The key and the keyValue are no deep clones!
     * @param other
     */
    public PairData clone(PairData other)
    {
        // not really a deep clone!
        other.setKey(this.getKey());
        other.setKeyValue(this.getKeyValue());

        return other;
    }

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof BasicPairData)
        {
            BasicPairData other = (BasicPairData) otherObj;

            if (other.key.equals(this.key) &&
                    other.keyValue.equals(this.keyValue))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    /**
     * @return Returns the attribute.
     */
    public String getKey()
    {
        return key;
    }

    /**
     *  Gets the value attribute of the PairData object
     *
     * @return    The value value
     */
    public Object getKeyValue()
    {
        return (keyValue);
    }

    public int hashCode()
    {
        int hash = 0;

        if (keyValue != null)
        {
            hash = keyValue.hashCode();
        }

        return hash;
    }

    /**
     * @param key The attribute to set.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     *  Sets the value attribute of the PairData object
     *
     * @param  v  The new value value
     */
    public void setKeyValue(final Object v)
    {
        keyValue = v;
    }

    public String toString()
    {
        return toString(BasicIOTypeHolder.instance().getIOType("UNDEFINED"));
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        if (keyValue == null)
        {
            logger.warn("Pair data '" + getKey() + "' contains no value.");

            return "";
        }

        if (keyValue instanceof FeatureResult)
        {
            return ((FeatureResult) keyValue).toString(ioType);
        }
        else
        {
            return keyValue.toString();
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
