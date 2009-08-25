///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: StringResult.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2005/02/17 16:48:30 $
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
package joelib2.feature.result;

import joelib2.feature.FeatureResult;

import joelib2.io.IOType;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import org.apache.log4j.Category;


/**
 * String result.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:30 $
 */
public class StringResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(StringResult.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public String value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntResult object
     */
    public StringResult()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        StringResult newObj = new StringResult();

        return clone(newObj);
    }

    public StringResult clone(StringResult other)
    {
        super.clone(other);
        other.value = value;

        return other;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String formatDescription(IOType ioType)
    {
        return "string value";
    }

    /**
     *  Description of the Method
     *
     * @param pairData                   Description of the Parameter
     * @param ioType                     Description of the Parameter
     * @return                           Description of the Return Value
     * @exception NumberFormatException  Description of the Exception
     */
    public boolean fromPairData(IOType ioType, PairData pairData)
        throws NumberFormatException
    {
        this.setKey(pairData.getKey());

        Object value = pairData.getKeyValue();
        boolean success = false;

        if ((value != null) && (value instanceof String))
        {
            success = fromString(ioType, (String) value);
        }

        return success;
    }

    /**
     *  Description of the Method
     *
     * @param sValue                     Description of the Parameter
     * @param ioType                     Description of the Parameter
     * @return                           Description of the Return Value
     * @exception NumberFormatException  Description of the Exception
     */
    public boolean fromString(IOType ioType, String sValue)
        throws NumberFormatException
    {
        value = sValue;

        return true;
    }

    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        return value;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
