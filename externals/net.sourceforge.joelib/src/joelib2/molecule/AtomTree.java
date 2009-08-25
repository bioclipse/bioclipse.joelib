///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomTree.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:36 $
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
package joelib2.molecule;

import java.util.List;


/**
 * Atom tree.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:36 $
 */
public class AtomTree implements java.io.Serializable
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Atom atom;
    private AtomTree previous;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the atom tree.
     *
     * @param  atom  atom
     * @param  prv   previous atom tree
     */
    public AtomTree(Atom _atom, AtomTree _prv)
    {
        atom = _atom;
        previous = _prv;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the atom index of this atom tree node.
     *
     * @return    The atom index
     */
    public int getAtomIdx()
    {
        return atom.getIndex();
    }

    /**
     * Adds this atom to the given <tt>path</tt>.
     *
     * @param  path  {@link java.util.List} of <tt>Atom</tt>
     */
    public void pathToRoot(List path)
    {
        path.add(atom);

        if (previous != null)
        {
            previous.pathToRoot(path);
        }
    }

    /**
     * @return Returns the atom.
     */
    protected Atom getAtom()
    {
        return atom;
    }

    /**
     * @return Returns the previous.
     */
    protected AtomTree getPrevious()
    {
        return previous;
    }

    /**
     * @param atom The atom to set.
     */
    protected void setAtom(Atom atom)
    {
        this.atom = atom;
    }

    /**
     * @param previous The previous to set.
     */
    protected void setPrevious(AtomTree previous)
    {
        this.previous = previous;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
