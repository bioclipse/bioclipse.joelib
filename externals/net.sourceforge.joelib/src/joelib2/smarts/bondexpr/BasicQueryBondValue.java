///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicQueryBondValue.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:39 $
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
package joelib2.smarts.bondexpr;

/**
 * Leaf bond node for SMARTS.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:39 $
 */
public class BasicQueryBondValue extends BasicQueryBond
    implements java.io.Serializable, QueryBondValue
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int label;

    /**
     *  Description of the Field
     */
    public int value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the LeafBondExpr object
     */
    public BasicQueryBondValue()
    {
        super();
    }

    /**
     *  Constructor for the LeafBondExpr object
     *
     * @param  dataType  Description of the Parameter
     */
    public BasicQueryBondValue(int type)
    {
        super(type);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the label.
     */
    public int getLabel()
    {
        return label;
    }

    /**
     * @return Returns the value.
     */
    public int getValue()
    {
        return value;
    }

    /**
     * @param label The label to set.
     */
    public void setLabel(int label)
    {
        this.label = label;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(int value)
    {
        this.value = value;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
