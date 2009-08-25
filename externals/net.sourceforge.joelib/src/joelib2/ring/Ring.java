///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Ring.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.8 $
//          $Date: 2005/02/17 16:48:38 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
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
package joelib2.ring;

import joelib2.math.Vector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.BasicBitVector;

import java.util.List;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.8 $, $Date: 2005/02/17 16:48:38 $
 */
public interface Ring
{
    //~ Methods ////////////////////////////////////////////////////////////////

    boolean equals(Object otherObj);

    /**
     *  Description of the Method
     *
     * @param  center  Description of the Parameter
     * @param  norm1   Description of the Parameter
     * @param  norm2   Description of the Parameter
     * @return         Description of the Return Value
     */
    boolean findCenterAndNormal(Vector3D center, Vector3D norm1, Vector3D norm2);

    BasicBitVector getAtomBits();

    /**
     * Returns all atom indexes of atoms which are contained in this ring.
     *
     * @return    atom indexes of atoms which are contained in this ring
     */
    int[] getAtomIndices();

    int[] getBonds();

    /**
     * Returns the parent molecule for this ring.
     *
     * @return    the parent molecule for this ring
     */
    Molecule getParent();

    int hashCode();

    /**
     * Returns <tt>true</tt> if this ring is a aromatic ring.
     *
     * @return    <tt>true</tt> if this ring is a aromatic ring
     */
    boolean isAromatic();

    /**
     * Returns <tt>true</tt> if this ring is a heterocycle.
     *
     * @return <tt>true</tt> if this ring is a heterocycle
     */
    boolean isHetero();

    /**
     * Returns <tt>true</tt> if the atom with index <tt>i</tt> is contained in this ring.
     *
     * @param  atomIndex  the index number of the atom
     * @return    <tt>true</tt> if the atom with index <tt>i</tt> is contained in this ring.
     */
    boolean isInRing(int atomIndex);

    /**
     * Returns <tt>true</tt> if the atom is contained in this ring.
     *
     * @param  atom  the atom
     * @return    <tt>true</tt> if the atom is contained in this ring.
     */
    boolean isMember(Atom atom);

    /**
     * Returns <tt>true</tt> if the bond is contained in this ring.
     *
     * @param  bond  the bond to check
     * @return    <tt>true</tt> if the bond is contained in this ring
     */
    boolean isMember(Bond bond);

    void setAtomIndices(int[] path);

    void setAtomIndices(List<Integer> path);

    /**
     *  Sets the parent attribute of the JOERing object
     *
     * @param  vec1  The new parent value
     */
    void setParent(Molecule vec1);

    /**
     * Returns the size of the ring.
     *
     * @return   the size of this ring
     */
    int size();

    String toString();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
