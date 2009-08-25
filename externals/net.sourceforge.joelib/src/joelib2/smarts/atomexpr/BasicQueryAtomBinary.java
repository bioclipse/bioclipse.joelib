///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicQueryAtomBinary.java,v $
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
package joelib2.smarts.atomexpr;

/**
 * Binary atom expression for SMARTS substructure search.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:39 $
 */
public class BasicQueryAtomBinary extends BasicQueryAtom
    implements java.io.Serializable, QueryAtomBinary
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public QueryAtom left;

    /**
     *  Description of the Field
     */
    public QueryAtom right;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the BinAtomExpr object
     */
    public BasicQueryAtomBinary()
    {
        super();
    }

    /**
     *  Constructor for the BinAtomExpr object
     *
     * @param  dataType  Description of the Parameter
     */
    public BasicQueryAtomBinary(int type)
    {
        super(type);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the left.
     */
    public QueryAtom getLeft()
    {
        return left;
    }

    /**
     * @return Returns the right.
     */
    public QueryAtom getRight()
    {
        return right;
    }

    /**
     * @param left The left to set.
     */
    public void setLeft(QueryAtom left)
    {
        this.left = left;
    }

    /**
     * @param right The right to set.
     */
    public void setRight(QueryAtom right)
    {
        this.right = right;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
