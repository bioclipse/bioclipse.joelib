///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Atom.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.11 $
//          $Date: 2005/02/17 16:48:36 $
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
package joelib2.molecule;

import joelib2.math.Vector3D;

import joelib2.molecule.types.Residue;

import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.ListIterator;
import joelib2.util.iterator.NbrAtomIterator;

import java.util.List;
import java.util.Properties;


/**
 *
 * @.author       wegner
 * @.wikipedia Atom
 * @.license      GPL
 * @.cvsversion   $Revision: 1.11 $, $Date: 2005/02/17 16:48:36 $
 */
public interface Atom extends Node
{
    //~ Static fields/initializers /////////////////////////////////////////////

    static final int ELECTRONS_UNDEFINED = Integer.MIN_VALUE;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Adds a bond to this atom.
     *
     * @param  bond  the new bond to add
     */
    boolean addBond(Bond bond);

    /**
     *  Returns a <tt>BondIterator</tt> for all bonds in this atom.
     *
     * <blockquote><pre>
     * BondIterator bit = atom.bondIterator();
     * Bond bond;
     * while (bit.hasNext())
     * {
     *   bond = bit.nextBond();
     *
     * }
     * </pre></blockquote>
     *
     * @return    the <tt>BondIterator</tt> for all bonds in this atom
     * @see #nbrAtomIterator()
     */
    BondIterator bondIterator();

    /**
     *  Deletes all contained informations in this atom.
     */
    void clear();

    /**
     *  Clone this <tt>Atom</tt> object.
     *
     * @return    cloned atom
     */
    Object clone();

    /**
     *  Delete a bond from this atom.
     *
     * @param  del_b  the bond to delete
     * @return        <tt>true</tt> if the given bond was deleted succesfully
     */
    boolean deleteBond(Bond bond2delete);

    void deleteResidue();

    /**
     * Checks if two atoms are equal.
     *
     * Compares hybridization, charge, isotope, atom type and flags,
     * AND atom index number, partial charge and position.
     *
     * @param type
     * @return
     */
    boolean equals(Object obj);

    /**
     * Checks if two atoms are equal.
     *
     * Compares hybridization, charge, isotope, atom type and flags.
     * When <tt>fullComparison</tt> is set to <tt>false</tt> the parent molecule,
     * atom index number, partial charge and position are ignored.
     *
     * @param type
     * @param full When set to <tt>false</tt> the parent molecule,
     * atom index number, partial charge and position are ignored.
     * @return
     */
    boolean equals(Atom type, boolean full);

    /**
     *  Gets the x coordinate of the <tt>Atom</tt> .
     *
     * @return    the x coordinate
     */
    double get3Dx();

    /**
     *  Gets the y coordinate of the <tt>Atom</tt> .
     *
     * @return    the y coordinate
     */
    double get3Dy();

    /**
     *  Gets the z coordinate of the <tt>Atom</tt> .
     *
     * @return    the z coordinate
     */
    double get3Dz();

    /**
     * Gets the atomic number of the <tt>Atom</tt> object.
     *
     * @return    the atomic number
     */
    int getAtomicNumber();

    /**
     *  Gets the bond attribute of the <tt>Atom</tt> object
     *
     * @param  nbr  Description of the Parameter
     * @return      The bond value
     */
    Bond getBond(Atom nbr);

    /**
     *  Returns a {@link java.util.Vector} for all bonds in this atom.
     *
     * @return    the {@link java.util.Vector} for all bonds in this atom
     */
    List getBonds();

    int getBondsSize();

    /**
     *  Gets the vector attribute of the <tt>Atom</tt> object
     *
     * @return    The vector value
     */
    Vector3D getCoords3D();

    int getFlags();

    /**
     *  Gets the formal charge of the <tt>Atom</tt> .
     *
     * @return    the formal charge of this atom
     */
    int getFormalCharge();

    /**
     * Set the number of free electrons.
     *
     * @param the number of free electrons or <tt>FREE_ELECTRONS_NOT_DEF</tt>
     * @see #ELECTRONS_UNDEFINED
     */
    int getFreeElectrons();

    /**
     * Isotpe value for this atom.
     *
     * @return byte
     */
    int getIsotope();

    /**
     *  Gets the nextAtom attribute of the <tt>Atom</tt> object
     *
     * @return    The nextAtom value
     */
    Atom getNextAtom();

    /**
     *  Gets the parent attribute of the <tt>Atom</tt> object
     *
     * @return    The parent value
     */
    Molecule getParent();

    Residue getResidue();

    /**
     *  Gets the type of the <tt>Atom</tt>.
     *
     * This is the JOELib internal atom type, which can be used via the
     * look-up table in {@link joelib2.data.BasicAtomTypeConversionHolder} to export molecules to other
     * formats, like Synyl MOL2, MM2, Tinker, etc.
     *
     * @return    the type of this atom
     */
    String getType();

    /**
     *  Gets the custom types of the <tt>Atom</tt> that has been previously added to this atom using the 
    addCustomType method.
     * @return   a properties object with all custom types assigned to this atom
     */
    Properties getCustomTypes();
    
    /**
     * Adds a specific custom type to the <tt>Atom</tt>.
     *
     * The type is expressed using a SMARTS string which should describe a single atom (and eventually its direct neighborhood)
     **/
    void addCustomType(String type, String SMARTSPattern);
    
    
    /**
     * Returns <tt>true</tt> if atom has an aromatic bond.
     *
     * @return  <tt>true</tt> if atom has an aromatic bond
     */
    boolean hasAromaticBondOrder();

    /**
     * Returns <tt>true</tt> if atom has bond of given <tt>order</tt>.
     *
     * @param  order  The bond order
     * @return        <tt>true</tt> if atom has bond of given <tt>order</tt>
     */
    boolean hasBondOfOrder(int order);

    /**
     * Returns <tt>true</tt> if the chirality was specified for this atom.
     *
     * @return  <tt>true</tt> if the chirality was specified for this atom
     */
    boolean hasChiralitySpecified();

    /**
     * Returns <tt>true</tt> if atom has a double bond.
     *
     * @return  <tt>true</tt> if atom has a double bond
     */
    boolean hasDoubleBond();

    /**
     * Calculates the hashcode for an atom.
     *
     * Includes hybridization, charge, isotope, atom type and flags.
     * Excludes atom index number, partial charge and position.
     *
     * @param type
     * @return
     */
    int hashCode();

    boolean hasResidue();

    /**
     * Returns <tt>true</tt> if atom has a single bond.
     *
     * @return  <tt>true</tt> if atom has a single bond
     */
    boolean hasSingleBond();

    /**
     *  Insert a bond to this atom.
     *
     * @param  listIter    Description of the Parameter
     * @param  bond  Description of the Parameter
     */
    void insertBond(ListIterator listIter, Bond bond);

    /**
     *  Gets the antiClockwise attribute of the Atom object
     *
     * @return    The antiClockwise value
     */
    boolean isAntiClockwise();

    boolean isChiral();

    /**
     *  Gets the clockwise attribute of the Atom object
     *
     * @return    The clockwise value
     */
    boolean isClockwise();

    /**
     *  Returns <tt>true</tt> if this atom is connected to the atom <tt>at</tt> .
     *
     * @param  atom  Description of the Parameter
     * @return     The connected value
     */
    boolean isConnected(Atom atom);

    /**
     *  Returns a <tt>NbrAtomIterator</tt> for all neighbour atoms in this atom.
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
     * @return    the <tt>NbrAtomIterator</tt> for all neighbour atoms in this atom
     * @see #bondIterator()
     */
    NbrAtomIterator nbrAtomIterator();

    void newResidue();

    int reHash();

    /**
     *  Sets the 'anti clockwise stereo' flag of the <tt>Atom</tt> .
     */
    void setAntiClockwiseStereo();

    /**
     *  Sets the atomic number of the <tt>Atom</tt> object. E.g.: carbon atoms
     *  have the atomic number 6, H atoms the atomic number 1.
     *
     * @param  atomicnum  the new atomic number
     */
    void setAtomicNumber(int atomicnum);

    /**
     * @param bonds The bonds to set.
     */
    void setBonds(List<Bond> bonds);

    /**
     *  Sets the chiral flag of the <tt>Atom</tt> .
     */
    void setChiral();

    /**
     *  Sets the 'clockwise stereo' flag of the <tt>Atom</tt> .
     */
    void setClockwiseStereo();

    /**
     *  Sets the new vector values of the <tt>Atom</tt> object to the given
     *  values from <tt></tt> .
     *
     * @param  c3D  the new vector values
     */
    void setCoords3D(Vector3D c3D);

    /**
     *  Sets the vector attribute of the <tt>Atom</tt> object
     *
     * @param  xPos  The new vector value
     * @param  yPos  The new vector value
     * @param  zPos  The new vector value
     */
    void setCoords3D(double xPos, double yPos, double zPos);

    void setFlags(int flag);

    /**
     *  Sets the formal charge of the <tt>Atom</tt> object.
     *
     * @param  fcharge  the new formal charge of this atom
     */
    void setFormalCharge(int fcharge);

    /**
     * Set the number of free electrons.
     *
     * @param the number of free electrons or <tt>FREE_ELECTRONS_NOT_DEF</tt>
     * @see #ELECTRONS_UNDEFINED
     */
    void setFreeElectrons(int freeEl);

    /**
     * Set isotpe value for this atom.
     *
     * @return byte
     */
    void setIsotope(int isotopeValue);

    /**
     *  Sets the parent molecule for this <tt>Atom</tt> .
     *
     * @param  ptr  the parent molecule for this <tt>Atom</tt>
     */
    void setParent(Molecule ptr);

    void setResidue(Residue res);

    void setType(String type);

    /**
     *  Sets the type of the <tt>Atom</tt> object.
     *
     * @param  type  the new type
     */
    void setType(String type, boolean validate);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    String toString();

    /**
     *  Clears the stereo flag of this atom.
     */
    void unsetStereo();
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
