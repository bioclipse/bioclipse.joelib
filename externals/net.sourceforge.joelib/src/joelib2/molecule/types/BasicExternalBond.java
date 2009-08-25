///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicExternalBond.java,v $
//  Purpose:  External bond.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
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

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;


/**
 * External bond.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:37 $
 */
public class BasicExternalBond implements java.io.Serializable, ExternalBond
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Atom atom;
    private Bond bond;
    private int index;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEExternalBond object
     */
    public BasicExternalBond()
    {
    }

    /**
     *  Constructor for the JOEExternalBond object
     *
     * @param  src  Description of the Parameter
     */
    public BasicExternalBond(final BasicExternalBond src)
    {
        index = src.getIndex();
        atom = src.getAtom();
        bond = src.getBond();
    }

    /**
     *  Constructor for the JOEExternalBond object
     *
     * @param  atom  Description of the Parameter
     * @param  bond  Description of the Parameter
     * @param  idx   Description of the Parameter
     */
    public BasicExternalBond(Atom atom, Bond bond, int idx)
    {
        this.index = idx;
        this.atom = atom;
        this.bond = bond;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the atom attribute of the JOEExternalBond object
     *
     * @return    The atom value
     */
    public final Atom getAtom()
    {
        return (atom);
    }

    /**
     *  Gets the bond attribute of the JOEExternalBond object
     *
     * @return    The bond value
     */
    public final Bond getBond()
    {
        return (bond);
    }

    /**
     *  Gets the idx attribute of the JOEExternalBond object
     *
     * @return    The idx value
     */
    public final int getIndex()
    {
        return (index);
    }

    /**
     *  Sets the atom attribute of the JOEExternalBond object
     *
     * @param  atom  The new atom value
     */
    public void setAtom(Atom atom)
    {
        this.atom = atom;
    }

    /**
     *  Sets the bond attribute of the JOEExternalBond object
     *
     * @param  bond  The new bond value
     */
    public void setBond(Bond bond)
    {
        this.bond = bond;
    }

    /**
     *  Sets the idx attribute of the JOEExternalBond object
     *
     * @param  idx  The new idx value
     */
    public void setIndex(int idx)
    {
        index = idx;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
