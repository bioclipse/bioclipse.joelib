///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Residue.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 15, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:37 $
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
package joelib2.molecule.types;

import joelib2.molecule.Atom;

import joelib2.util.iterator.BasicAtomIterator;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:37 $
 */
public interface Residue
{
    //~ Methods ////////////////////////////////////////////////////////////////

    void addAtom(Atom atom);

    /**
     * Gets an iterator over all atoms in this residue.
     *
     * @return   the atom iterator for this residue
     */
    BasicAtomIterator atomIterator();

    void clear();

    //copy residue information
    Object clone();

    BasicResidue clone(BasicResidue to);

    String getAtomID(Atom atom);

    String getChain();

    int getChainNumber();

    int getIndex();

    String getName();

    int getNumber();

    int getSerialNumber(Atom atom);

    boolean isHeteroAtom(Atom atom);

    //  public void insertAtom(Atom atom)
    void removeAtom(Atom atom);

    void setAtomID(Atom atom, String id);

    void setChain(String chain);

    void setChainNumber(int chainnum);

    void setHeteroAtom(Atom atom, boolean hetatm);

    void setIndex(int idx);

    void setName(String name);

    void setNumber(int number);

    void setSerialNumber(Atom atom, int sernum);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
