///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMILESClosureBond.java,v $
//  Purpose:  Closure bond of an SMILES expression.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
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
package joelib2.smiles;

/**
 *  Closure bond of an SMILES expression.
 *
 * @.author     wegnerj
 * @.wikipedia  Simplified molecular input line entry specification
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:40 $
 */
public class SMILESClosureBond implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * Flags of the bond.
     */
    public int bondflags;

    /**
     * Number of the closure bond.
     */
    public int closureNumber;

    /**
     * Order.
     */
    public int order;

    /**
     * Index of the previous atom.
     */
    public int previous;

    /**
     * Valence of the previous atom.
     */
    public int valence;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Initialize the <tt>ClosureBond</tt>
     *
     * @param _closureNumber  Number of the closure bond
     * @param _previous       Index of the previous atom
     * @param _order          Order
     * @param _bondflags      Flags of the bond
     * @param _valence        Valence of the previous atom
     */
    public SMILESClosureBond(int _closureNumber, int _previous, int _order,
        int _bondflags, int _valence)
    {
        closureNumber = _closureNumber;
        previous = _previous;
        order = _order;
        bondflags = _bondflags;
        valence = _valence;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
