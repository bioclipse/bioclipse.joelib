///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomDoubleParent.java,v $
//  Purpose:  Helper class for resolving renumbering ties.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.algo.morgan;

/**
 * Helper class for resolving renumbering ties.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:29 $
 */
public class AtomDoubleParent
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * Atom index of the atom.
     */
    public int atomIdx;

    /**
     * Parent atom index of the atom.
     */
    public int parent;

    /**
     * Renumbering tie flag.
     */
    public boolean tie;

    /**
     * Temporary and new atom index stored as double value to handle huge
     * temporary values. The Morgan algorithm can cause really huge
     * values.
     */
    public double tmpAtomIdx;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntInt object
     */
    public AtomDoubleParent()
    {
    }

    /**
     *  Constructor for the IntInt object
     *
     * @param  _i1  Description of the Parameter
     * @param  _i2  Description of the Parameter
     */
    public AtomDoubleParent(int _atomIdx, double _tmpAtomIdx, int _parent,
        boolean _tie)
    {
        atomIdx = _atomIdx;
        tmpAtomIdx = _tmpAtomIdx;
        parent = _parent;
        tie = _tie;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        if (otherObj instanceof AtomDoubleParent)
        {
            AtomDoubleParent ai = (AtomDoubleParent) otherObj;

            if ((ai.atomIdx == this.atomIdx) &&
                    (ai.tmpAtomIdx == this.tmpAtomIdx) &&
                    (ai.parent == this.parent) && (ai.tie == this.tie))
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

    public int hashCode()
    {
        return atomIdx;
    }

    public String toString()
    {
        return "<atomIdx:" + atomIdx + ", tmpAtomIdx:" + tmpAtomIdx +
            " parent:" + parent + " tie:" + tie + ">";
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
