///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicNbrAtomIterator.java,v $
//  Purpose:  Iterator for the standard Vector.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:42 $
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
package joelib2.util.iterator;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;

import java.util.List;


/**
 * Gets an iterator over all neighbour atoms in a atom.
 *
 * <blockquote><pre>
 * NbrAtomIterator nait = atom.nbrAtomIterator();
 * Bond bond;
 * Atom nbrAtom;
 * while (nait.hasNext())
 * {
 *          nbrAtom=nait.nextNbrAtom();
 *   bond = nait.actualBond();
 *
 * }
 * </pre></blockquote>
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:42 $
 * @see VectorIterator
 * @see joelib2.molecule.Atom#nbrAtomIterator()
 */
public class BasicNbrAtomIterator extends BasicListIterator
    implements NbrAtomIterator
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Atom atom;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the NbrAtomIterator object
     *
     * @param  bonds  Description of the Parameter
     * @param  atom   Description of the Parameter
     */
    public BasicNbrAtomIterator(List<Bond> bonds, Atom atom)
    {
        super(bonds);
        this.atom = atom;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object actual()
    {
        Bond bond = (Bond) super.actual();

        return bond.getNeighbor(atom);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Bond actualBond()
    {
        return (Bond) super.actual();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object next()
    {
        Bond bond = (Bond) super.next();

        return bond.getNeighbor(atom);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Atom nextNbrAtom()
    {
        return (Atom) next();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
