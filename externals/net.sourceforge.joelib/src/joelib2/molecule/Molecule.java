///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Molecule.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.13 $
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

import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.io.IOType;

import joelib2.math.Vector3D;

import joelib2.molecule.types.PairData;
import joelib2.molecule.types.Residue;

import joelib2.util.BitVector;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BasicRingIterator;
import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.NativeValueIterator;
import joelib2.util.iterator.PairDataIterator;
import joelib2.util.iterator.ResidueIterator;
import joelib2.util.iterator.RingIterator;

import java.util.List;


/**
 *
 * @.author       wegner
 * @.wikipedia    Molecule
 * @.license      GPL
 * @.cvsversion   $Revision: 1.13 $, $Date: 2005/02/17 16:48:36 $
 */
public interface Molecule extends Graph
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds (cloned) atom to the <tt>Molecule</tt>.
     * Also checks for any bonds that should be made to the new atom.
     * Atoms starts with the index 1, which is a difference to bonds
     * which starts with the bond index 0.
     *
     * <p>
     * It's recommended calling the <tt>beginModify()</tt> method before the
     * first atom will be added and calling the <tt>endModify()</tt> method
     * after the last atom has been added. This causes, that the coordinates
     * of the atoms will also be stored in a coordinate array in the molecule.
     *
     * @param  atom  The <tt>Atom</tt> to add, which will be deep cloned.
     * @return       <tt>true</tt> if successfull
     *
     * @see #addBond(int, int, int)
     * @see #addBond(int, int, int, int)
     * @see #addBond(int, int, int, int, int)
     * @see #addBondClone(Bond)
     * @see #beginModify()
     * @see #endModify()
     * @see #endModify(boolean)
     */
    boolean addAtomClone(Atom atom);

    /**
     * Adds a bond to the <tt>Molecule</tt>.
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * @param  first      The start atom index (atoms begins with index 1)
     * @param  second     The end atom index (atoms begins with index 1)
     * @param  order      The bond order
     * @return         <tt>true</tt> if successfull
     *
     * @see #addAtomClone(Atom)
     * @see #addBond(int, int, int, int)
     * @see #addBond(int, int, int, int, int)
     * @see #addBondClone(Bond)
     * @see #connectTheDots()
     */
    boolean addBond(int first, int second, int order);

    /**
     * Adds a bond to the <tt>Molecule</tt>.
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * @param  first      The start atom index (atoms begins with index 1)
     * @param  second     The end atom index (atoms begins with index 1)
     * @param  order      The bond order
     * @param  flags     The stereo flag
     * @return            <tt>true</tt> if successfull
     *
     * @see #addBond(int, int, int)
     * @see #addBond(int, int, int, int, int)
     * @see #addBondClone(Bond)
     * @see #connectTheDots()
     */
    boolean addBond(int first, int second, int order, int flags);

    /**
     * Adds a bond to the <tt>Molecule</tt>.
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * @param  first      The start atom index (atoms begins with index 1)
     * @param  second     The end atom index (atoms begins with index 1)
     * @param  order      The bond order
     * @param  stereo     The stereo flag
     * @param  insertpos  The position at which the bond should be inserted
     * @return            <tt>true</tt> if successfull
     *
     * @see #addAtomClone(Atom)
     * @see #addBond(int, int, int)
     * @see #addBond(int, int, int, int)
     * @see #addBondClone(Bond)
     * @see #connectTheDots()
     */
    boolean addBond(int first, int second, int order, int stereo, int insertpos);

    /**
     * Adds (cloned) bond to the <tt>Molecule</tt>.
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * @param  bond  The <tt>Bond</tt> to add, which will be deep cloned.
     * @return       Description of the Return Value
     *
     * @see #addAtomClone(Atom)
     * @see #addBond(int, int, int)
     * @see #addBond(int, int, int, int)
     * @see #addBond(int, int, int, int, int)
     * @see #connectTheDots()
     */
    boolean addBondClone(Bond bond);

    /**
     *  Adds a <tt>JOEGenericData</tt> object to this molecule but don't overwrite
     *  existing data elements with the same name, if they exists already.
     *
     * <p>
     * There exist a lot of default data types which where defined in
     * {@link JOEDataType}. These data types are used for caching ring
     * searches and storing special data types like comments or virtual bonds.
     * Furthermore there exist the most important data type {@link PairData}
     * for storing descriptor values. Read the {@link JOEDataType} description
     * for details.
     *
     * <p>
     * Missing descriptor values can be calculated by using
     * {@link FeatureHelper#featureFrom(Molecule, String)}.<br>
     * Example:
     * <blockquote><pre>
     * DescResult result=null;
     * try
     * {
     *         result=DescriptorHelper.instance().descFromMol(mol, descriptorName);
     * }
     * catch (DescriptorException ex)
     * {
     *         // descriptor can not be calculated
     * }
     * </pre></blockquote>
     * Notice the difference between {@link JOEGenericData} and
     * {@link FeatureResult}. {@link FeatureResult} values can be added to
     * molecules by using {@link PairData}
     * <blockquote><pre>
     * PairData dp = new PairData();
     * dp.setAttribute(descriptorName);
     * dp.setValue(result);
     * mol.addData(dp);
     * </pre></blockquote>
     *
     * @param  data  The new data value
     * @see #addData(JOEGenericData, boolean)
     * @see #getData(JOEDataType)
     * @see #getData(String)
     * @see #getData(String, boolean)
     * @see #genericDataIterator()
     * @see JOEDataType
     * @see PairData
     * @see FeatureHelper#featureFrom(Molecule, String)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult, boolean)
     */
    void addData(PairData data);

    /**
     *  Adds a <tt>JOEGenericData</tt> object to this molecule.
     *
     * <p>
     * There exist a lot of default data types which where defined in
     * {@link JOEDataType}. These data types are used for caching ring
     * searches and storing special data types like comments or virtual bonds.
     * Furthermore there exist the most important data type {@link PairData}
     * for storing descriptor values. Read the {@link JOEDataType} description
     * for details.
     *
     * <p>
     * Missing descriptor values can be calculated by using
     * {@link FeatureHelper#featureFrom(Molecule, String)}.<br>
     * Example:
     * <blockquote><pre>
     * DescResult result=null;
     * try
     * {
     *         result=DescriptorHelper.instance().descFromMol(mol, descriptorName);
     * }
     * catch (DescriptorException ex)
     * {
     *         // descriptor can not be calculated
     * }
     * </pre></blockquote>
     * Notice the difference between {@link JOEGenericData} and
     * {@link FeatureResult}. {@link FeatureResult} values can be added to
     * molecules by using {@link PairData}
     * <blockquote><pre>
     * PairData dp = new PairData();
     * dp.setAttribute(descriptorName);
     * dp.setValue(result);
     * mol.addData(dp);
     * </pre></blockquote>
     *
     * @param  data  The new data value
     * @param  overwrite  Overwrite already existing data element, if <tt>true</tt>
     * @see #addData(JOEGenericData)
     * @see #getData(JOEDataType)
     * @see #getData(String)
     * @see #getData(String, boolean)
     * @see #genericDataIterator()
     * @see JOEDataType
     * @see PairData
     * @see FeatureHelper#featureFrom(Molecule, String)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult, boolean)
     */
    void addData(PairData data, boolean overwrite);

    /**
     * Adds hydrogens atoms to this molecule.
     * The pH value will be corrected.
     *
     * @return    <tt>true</tt> if successfull
     */
    boolean addHydrogens();

    /**
     * Adds hydrogens atoms to this molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     * The pH value will be corrected.
     *
     * @param  polaronly  Add only polar hydrogens, if <tt>true</tt>
     * @return            <tt>true</tt> if successfull
     */
    boolean addHydrogens(boolean polaronly);

    /**
     * Adds hydrogens atoms to the given atom.
     * The pH value will not be corrected.
     *
     * @param  atom  The atom to which the hydogens should be added
     * @return               <tt>true</tt> if successfull
     */
    boolean addHydrogens(Atom atom);

    /**
     * Adds hydrogens atoms to this molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     * The pH value correction mode can be choosen.
     * Dont't use (experimental) coordinate vector for added hydrogens.
     *
     * @param  polaronly     Add only polar hydrogens, if <tt>true</tt>
     * @param  correctForPH  Corrects molecule for pH if <tt>true</tt>
     * @return               <tt>true</tt> if successfull
     */
    boolean addHydrogens(boolean polaronly, boolean correctForPH);

    /**
     * Adds hydrogens atoms to this molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     * The pH value correction mode can be choosen.
     * The (experimental) coordinate vector for added hydrogens mode can be choosen.
     *
     * <p>
     * If useCoordV is <tt>true</tt> the coordinate vector for added H atoms will be used.
     * This will be slower, because <tt>endModify()</tt> will be called after
     * every added hydrogen atom. If useCoordV is <tt>false</tt> the coordinate vector
     * for added H atoms will be ignored, which will be faster.

     *
     * @param  polaronly     Add only polar hydrogens, if <tt>true</tt>
     * @param  correctForPH  Corrects molecule for pH if <tt>true</tt>
     * @param  useCoordV     The coordinate vector for added hydrogens will be used if <tt>true</tt>
     * @return               <tt>true</tt> if successfull
     * @see #endModify()
     * @see #endModify(boolean)
     */
    boolean addHydrogens(boolean polaronly, boolean correctForPH,
        boolean useCoordV);

    /**
     * Add polar hydrogens to molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     * The pH value will be corrected.
     *
     * @return    Description of the Return Value
     */
    boolean addPolarHydrogens();

    /**
     * Adds residue information to this molecule.
     *
     * @param  residue  The residue information
     * @return          <tt>true</tt> if successfull
     */
    boolean addResidue(Residue residue);

    /**
     * Aligns atom a1 on p1 and atom a2 along start-to-end vector.
     *
     * @param  atom1  first atom
     * @param  atom2  second atom
     * @param  start  start point
     * @param  end  end point
     */
    void align(Atom atom1, Atom atom2, Vector3D start, Vector3D end);

    /**
     * Gets an iterator over all atoms in this molecule.
     *
     * Possibility one:
     * <blockquote><pre>
     * AtomIterator ait = mol.atomIterator();
     * Atom atom;
     * while (ait.hasNext())
     * {
     *   atom = ait.nextAtom();
     *
     * }
     * </pre></blockquote>
     *
     * Atoms starts with the index 1, which is a difference to bonds
     * which starts with the bond index 0.
     * Possibility two:
     * <blockquote><pre>
     * int atoms = mol.numAtoms();
     * Atom atom;
     * for (int i=1; i&lt;=atoms; i++)
     * {
     *   atom=mol.getAtom(i);
     *
     * }
     * </pre></blockquote>
     *
     * @return    the atom iterator for this molecule
     * @see #bondIterator()
     * @see #conformerIterator()
     * @see #getRingIterator()
     * @see #genericDataIterator
     * @see #nativeValueIterator()
     */
    AtomIterator atomIterator();

    /**
     * Begins modification of atoms and increase modification counter.
     *
     * The modification will be only processed if <tt>getMod()</tt> is 0!!!
     * If you have called beginModify/endModify twice you can not expect
     * that these changes are already available correctly.
     * This fits especially for deleted and added atoms, because endModify
     * updates the atomId's, writes the atom coordinates to the rotamer
     * arrays and checks the aromaticity.
     *
     * @see #endModify()
     * @see #endModify(boolean)
     * @see #decrementMod()
     * @see #incrementMod()
     * @see #getModificationCounter()
     */
    void beginModify();

    /**
     *  Gets an iterator over all bonds.
     *
     * Possibility one:
     * <blockquote><pre>
     * BondIterator bit = bondIterator();
     * Bond bond;
     * while (bit.hasNext())
     * {
     *   bond = bit.nextBond();
     *
     * }
     * </pre></blockquote>
     *
     * Bonds starts with the index 0, which is a difference to atoms
     * which starts with the atom index 1.
     *
     * Possibility two:
     * <blockquote><pre>
     * int bonds = mol.numBonds();
     * Bond bond;
     * for (int i=0; i&lt;bonds; i++)
     * {
     *   bond=mol.getBond(i);
     *
     * }
     * </pre></blockquote>
     *
     * @return    the bond iterator
     * @see #atomIterator()
     * @see #conformerIterator()
     * @see #getRingIterator()
     * @see #genericDataIterator
     * @see #nativeValueIterator()
     */
    BondIterator bondIterator();

    /**
     * Clears molecule.
     *
     * @return    <tt>true</tt> if successfull
     */
    boolean clear();

    /**
     *  Clones molecule without additional data (e.g. descriptor data).
     *
     * @return    the cloned molecule
     */
    Object clone();

    /**
     * Clones this molecule.
     *
     * Note that the atom and bond labels are also stored as features. If you do not clone
     * those features, they must be calculated recalculated for each cloned molecule.
     *
     * @param  cloneFeatures  clones the PairData descriptors if <tt>true</tt>
     * @return            the cloned molecule
     */
    Object clone(boolean cloneFeatures);

    /**
     * Clones this molecule.
     *
     * Note that the atom and bond labels are also stored as features. If you do not clone
     * those features, they must be calculated recalculated for each cloned molecule.
     *
     * @param  cloneFeatures  clones the PairData descriptors if <tt>true</tt>
     * @param  features2clone the features which are cloned independently of the flag
     * @return            the cloned molecule
     */
    Object clone(boolean cloneFeatures, String[] features2clone);

    /**
     * Decrease modification counter.
     *
     * @see #incrementMod()
     * @see #getModificationCounter()
     * @see #beginModify()
     * @see #endModify()
     * @see #endModify(boolean)
     */
    void decrementMod();

    /**
     * Delete atom from molecule.
     *
     * @param  atom  The atom to delete
     * @return       <tt>true</tt> if successfull
     */
    boolean deleteAtom(Atom atom);

    /**
     * Delete the given <tt>Bond</tt> from this molecule.
     *
     * @param  bond  The bond to delete
     * @return       <tt>true</tt> if successfull
     */
    boolean deleteBond(Bond bond);

    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return    Description of the Return Value
     */
    boolean deleteData(String name);

    /**
     *  Delete all data elements which are <tt>equal</tt> to the given <tt>
     *  JOEGenericData</tt> element.
     *
     * @param  genData  the element to delete
     */
    void deleteData(PairData genData);

    /**
     *  Description of the Method
     *
     * @param  dataList  Description of the Parameter
     */
    void deleteData(List dataList);

    /**
     * Deletes hydrogen atom.
     *
     * <p>
     * <blockquote><pre>
     * mol.beginModify();
     * deleteHydrogen(atom );
     * mol.endModify();
     * </pre></blockquote>
     *
     * @param  atom  Hydrogen atom to delete
     * @return       <tt>true</tt> if successfull
     */
    boolean deleteHydrogen(Atom atom);

    /**
     * Delete all hydrogen atoms from molecule.
     *
     * <p>
     * <blockquote><pre>
     * mol.beginModify();
     * deleteHydrogens();
     * mol.endModify();
     * </pre></blockquote>
     *
     * @return    <tt>true</tt> if successfull
     */
    boolean deleteHydrogens();

    /**
     * Delete all hydrogen atoms from given atom.
     *
     * <p>
     * <blockquote><pre>
     * mol.beginModify();
     * deleteHydrogens(atom);
     * mol.endModify();
     * </pre></blockquote>
     *
     * @param  atom  Atom from which hydrogen atoms should be deleted
     * @return    <tt>true</tt> if successfull
     */
    boolean deleteHydrogens(Atom atom);

    /**
     * Delete all non polar hydrogens from molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     *
     * @return    <tt>true</tt> if successfull
     */
    boolean deleteNonPolarHydrogens();

    /**
     *  Description of the Method
     *
     * @param  residue  Description of the Parameter
     * @return          Description of the Return Value
     */
    boolean deleteResidue(Residue residue);

    /**
     * Ends modification of atoms and decrease modification counter.
     * All flags will be deleted.
     *
     * The modification will be  only processed if <tt>getMod()</tt> is 0!!!
     * If you have called beginModify/endModify twice you can not expect
     * that these changes are already available correctly.
     * This fits especially for deleted and added atoms, because endModify
     * updates the atomId's, writes the atom coordinates to the rotamer
     * arrays and checks the aromaticity.
     *
     * @see #endModify(boolean)
     * @see #beginModify()
     * @see #decrementMod()
     * @see #incrementMod()
     * @see #getModificationCounter()
     */
    void endModify();

    /**
     * Ends modification of atoms and decrease modification counter.
     *
     * The modification will be  only processed if <tt>getMod()</tt> is 0!!!
     * If you have called beginModify/endModify twice you can not expect
     * that these changes are already available correctly.
     * This fits especially for deleted and added atoms, because endModify
     * updates the atomId's, writes the atom coordinates to the rotamer
     * arrays and checks the aromaticity.
     *
     * @param  nukeData  if <tt>true</tt> all flags will be deleted
     * @see #endModify()
     * @see #beginModify()
     * @see #decrementMod()
     * @see #incrementMod()
     * @see #getModificationCounter()
     */
    void endModify(boolean nukeLabels);

    void endModify(boolean nukeLabels, boolean correctForPH);

    /**
     * Checks if two molecules are equal, ignoring descriptor values.
     *
     * This method uses the full equality check if the atom and bonds.
     */
    boolean equals(Object obj);

    /**
     * Checks if two molecules are equal, ignoring descriptor values.
     *
     * This method uses the full equality check if the atom and bonds.
     */
    boolean equals(Molecule other);

    /**
     * Returns <tt>true</tt> if this bond exists.
     *
     * @param  bgn  atom index of the start atom
     * @param  end  atom index of the end atom
     * @return <tt>true</tt> if this bond exists
     */
    boolean existsBond(int bgn, int end);

    /**
     *  Locates all atoms for which there exists a path to <tt>second</tt> without
     *  going through <tt>first</tt> children does not include <tt>second</tt> .
     *
     * @param  children  Description of the Parameter
     * @param  first     Description of the Parameter
     * @param  second    Description of the Parameter
     */
    void findChildren(List<Atom> children, int first, int second);

    /**
     *  locates all atoms for which there exists a path to 'second' without going
     *  through 'first' children does not include 'second'
     *
     * @param  children  Description of the Parameter
     * @param  bgn       Description of the Parameter
     * @param  end       Description of the Parameter
     */
    void findChildren(List<Atom> children, Atom bgn, Atom end);

    /**
     *  each vector<int> contains the atom numbers of a contig fragment the
     *  vectors are sorted by size from largest to smallest
     *
     * @param  fagments  Description of the Parameter
     */
    void findLargestFragment(BitVector fagments);

    /**
     * Gets an iterator over the generic data elements of this molecule.
     *
     * There exist a lot of default data types which where defined in
     * {@link JOEDataType}. These data types are used for caching ring
     * searches and storing special data types like comments or virtual bonds.
     * Furthermore there exist the most important data type {@link PairData}
     * for storing descriptor values. Read the {@link JOEDataType} description
     * for details.
     *
     * <p>
     * <ol>
     * <li>For getting all data elements the simple data iterator can be used.<br>
     * <blockquote><pre>
     * GenericDataIterator gdit = mol.genericDataIterator();
     * JOEGenericData genericData;
     * while(gdit.hasNext())
     * {
     *   genericData = gdit.nextGenericData();
     *
     * }
     * </pre></blockquote>
     * </li>
     *
     * <li>For getting all descriptor data elements a slightly modified iterator can be used.<br>
     * <blockquote><pre>
     * GenericDataIterator gdit = mol.genericDataIterator();
     * JOEGenericData genericData;
     * while(gdit.hasNext())
     * {
     *   genericData = gdit.nextGenericData();
     *   if (genericData.getDataType() == JOEDataType.JOE_PAIR_DATA)
     *   {
     *           PairData pairData = (PairData)genericData;
     *
     *   }
     * }
     * </pre></blockquote>
     * </li>
     *
     * <li>For getting all native value descriptors the simple data iterator can be used, also.
     * Because this is a little bit tricky, the {@link #nativeValueIterator()} would be
     * the recommended method for getting these values. Though the complex access
     * will be presented, because it's really instructive for understanding the
     * data access under JOELib.<br>
     * <blockquote><pre>
     * GenericDataIterator gdit = mol.genericDataIterator();
     * JOEGenericData genericData;
     * while(gdit.hasNext())
     * {
     *   genericData = gdit.nextGenericData();
     *   if (genericData.getDataType() == JOEDataType.JOE_PAIR_DATA)
     *   {
     *           PairData pairData = (PairData)genericData;
     *           // data must be parsed to check data type
     *           genericData = mol.getData(pairData.getAttribute(), true);
     *           pairData = (PairData)genericData;
     *             if(JOEHelper.hasInterface(pairData, "NativeValue"))
     *             {
     *        double tmpDbl=((NativeValue) pairData).getDoubleNV();
     *
     *             }
     *   }
     * }
     * </pre></blockquote>
     * </li>
     * </ol>
     *
     * @return    the generic data iterator
     * @see #atomIterator()
     * @see #bondIterator()
     * @see #conformerIterator()
     * @see #getRingIterator()
     * @see #nativeValueIterator()
     * @see #getData(String)
     * @see #getData(String, boolean)
     * @see #addData(JOEGenericData)
     * @see #addData(JOEGenericData, boolean)
     * @see FeatureHelper#getAvailableFeatures(Molecule)
     * @see JOEDataType
     * @see JOEGenericData
     */
    PairDataIterator genericDataIterator();

    /**
     *  Gets an atom of the <tt>Molecule</tt> object Atom index must be between <tt>
     *  1</tt> to <tt>numAtoms()</tt> .
     *
     * @param  idx  Description of the Parameter
     * @return      The atom value
     */
    Atom getAtom(int idx);

    /**
     * @return Returns the atoms.
     */
    List<Atom> getAtoms();

    /**
     * Returns the number of atoms.
     *
     * @return    The number of atoms
     */
    int getAtomsSize();

    /**
     *  Gets a bond of the <tt>Molecule</tt> object. Bond index must be between <tt>
     *  0</tt> to <tt>(numBonds()-1)</tt> .
     *
     * @param  idx  Description of the Parameter
     * @return      The bond value
     */
    Bond getBond(int idx);

    /**
     *  Gets the bond attribute of the <tt>Molecule</tt> object Atom index must be
     *  between <tt>1</tt> to <tt>numAtoms()</tt> .
     *
     * @param  bgn  atom index of the start atom
     * @param  end  atom index of the end atom
     * @return      The bond value
     */
    Bond getBond(int bgn, int end);

    /**
     *  Gets the bond attribute of the <tt>Molecule</tt> object
     *
     * @param  bgn  Description of the Parameter
     * @param  end  Description of the Parameter
     * @return      The bond value
     */
    Bond getBond(Atom bgn, Atom end);

    /**
     * @return Returns the bonds.
     */
    List getBonds();

    /**
     * Returns the number of bonds.
     *
     * @return    The number of bonds
     */
    int getBondsSize();

    /**
     * Returns the data entry with the given name, if multiple data entries
     * exists only the first one is taken.
     * If the data element is a {@link PairData} element with a unparsed
     * String value, the descriptor value will be automatically parsed from
     * a String value to the representing result ({@link FeatureResult}) class.
     * If no data element with this name exists in this molecule, <tt>null</tt>
     * is returned.
     *
     * <p>
     * There exist a lot of default data types which where defined in
     * {@link JOEDataType}. These data types are used for caching ring
     * searches and storing special data types like comments or virtual bonds.
     * Furthermore there exist the most important data type {@link PairData}
     * for storing descriptor values. Read the {@link JOEDataType} description
     * for details.
     *
     * <p>
     * Missing descriptor values can be calculated by using
     * {@link FeatureHelper#featureFrom(Molecule, String)}.<br>
     * Example:
     * <blockquote><pre>
     * DescResult result=null;
     * try
     * {
     *         result=DescriptorHelper.instance().descFromMol(mol, descriptorName);
     * }
     * catch (DescriptorException ex)
     * {
     *         // descriptor can not be calculated
     * }
     * </pre></blockquote>
     * Notice the difference between {@link JOEGenericData} and
     * {@link FeatureResult}. {@link FeatureResult} values can be added to
     * molecules by using {@link PairData}
     * <blockquote><pre>
     * PairData dp = new PairData();
     * dp.setAttribute(descriptorName);
     * dp.setValue(result);
     * mol.addData(dp);
     * </pre></blockquote>
     *
     * @param  name      The data element (descriptor) name
     * @return     <tt>null</tt> if no element with this name exists
     * @see #getData(JOEDataType)
     * @see #getData(String, boolean)
     * @see #genericDataIterator()
     * @see #addData(JOEGenericData)
     * @see #addData(JOEGenericData, boolean)
     * @see JOEDataType
     * @see PairData
     * @see FeatureResult
     * @see BasicDataHolder#getData(String, boolean)
     * @see FeatureHelper#featureFrom(Molecule, String)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult, boolean)
     */
    PairData getData(String name);

    /**
     * Returns the data entry with the given name, if multiple data entries
     * exists only the first one is taken.
     * If the data element is a {@link PairData} the <tt>parse</tt> flag
     * can be used to parse this data elements.
     * If no data element with this name exists in this molecule, <tt>null</tt>
     * is returned.
     *
     * <p>
     * There exist a lot of default data types which where defined in
     * {@link JOEDataType}. These data types are used for caching ring
     * searches and storing special data types like comments or virtual bonds.
     * Furthermore there exist the most important data type {@link PairData}
     * for storing descriptor values. Read the {@link JOEDataType} description
     * for details.
     *
     * <p>
     * Missing descriptor values can be calculated by using
     * {@link FeatureHelper#featureFrom(Molecule, String)}.<br>
     * Example:
     * <blockquote><pre>
     * DescResult result=null;
     * try
     * {
     *         result=DescriptorHelper.instance().descFromMol(mol, descriptorName);
     * }
     * catch (DescriptorException ex)
     * {
     *         // descriptor can not be calculated
     * }
     * </pre></blockquote>
     * Notice the difference between {@link JOEGenericData} and
     * {@link FeatureResult}. {@link FeatureResult} values can be added to
     * molecules by using {@link PairData}
     * <blockquote><pre>
     * PairData dp = new PairData();
     * dp.setAttribute(descriptorName);
     * dp.setValue(result);
     * mol.addData(dp);
     * </pre></blockquote>
     *
     * @param  name       The data element (descriptor) name
     * @param  parse   Parse data element
     * @return     <tt>null</tt> if no element with this name exists
     * @see #getData(JOEDataType)
     * @see #getData(String)
     * @see #genericDataIterator()
     * @see #addData(JOEGenericData)
     * @see #addData(JOEGenericData, boolean)
     * @see JOEDataType
     * @see PairData
     * @see FeatureResult
     * @see BasicDataHolder#getData(String, boolean)
     * @see FeatureHelper#featureFrom(Molecule, String)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult)
     * @see FeatureHelper#featureFrom(Molecule, String, DescResult, boolean)
     */
    PairData getData(String name, boolean parse);

    /**
     * Returns the number of data elements in this molecule.
     *
     * @return    number of data elements in this molecule
     */
    int getDataSize();

    /**
     * Caching mechanism for deprotonated structure.
     *
     * The deprotonated molecular structure is very often used by feature
     * calculation algorithms, because the deprotonation step costs a lot of time
     * this method allows us to access the deprotonated structure, which is chached
     * internally.
     *
     * @return Returns the deprotonated molecular structure.
     */
    Molecule getDeprotonated();

    /**
     *  Gets the energy attribute of the <tt>Molecule</tt> object
     *
     * @return    The energy value
     */
    double getEnergy();

    /**
     *  Gets the firstAtom attribute of the <tt>Molecule</tt> object
     *
     * @return    The firstAtom value
     */
    Atom getFirstAtom();

    /**
     *  Gets the flags attribute of the <tt>Molecule</tt> object
     *
     * @return    The flags value
     * @see       joelib2.molecule.Molecule#HAS_SSSR
     * @see       joelib2.molecule.Molecule#RINGS_ASSIGNED
     * @see       joelib2.molecule.Molecule#AROMATICITY_ASSIGNED
     * @see       joelib2.molecule.Molecule#ATOMTYPES_ASSIGNED
     * @see       joelib2.molecule.Molecule#CHIRALITY_ASSIGNED
     * @see       joelib2.molecule.Molecule#PCHARGE_ASSIGNED
     * @see       joelib2.molecule.Molecule#HYBRID_ASSIGNED
     * @see       joelib2.molecule.Molecule#IMPVAL_ASSIGNED
     * @see       joelib2.molecule.Molecule#KEKULE_ASSIGNED
     * @see       joelib2.molecule.Molecule#CLOSURE_ASSIGNED
     * @see       joelib2.molecule.Molecule#H_ADDED
     * @see       joelib2.molecule.Molecule#PH_CORRECTED
     * @see       joelib2.molecule.Molecule#AROMATICITY_CORRECTED
     * @see       joelib2.molecule.Molecule#IS_CHAIN
     * @see       joelib2.molecule.Molecule#IS_CURRENT_CONFORMER
     */
    int getFlags();

    /**
     * Returns the number of heavy atoms.
     * That is the number of all atoms except hydrogen atoms.
     *
     * @return    The number of heavy atoms
     */
    int getHeavyAtomsNumber();

    /**
     *  Gets the inputType attribute of the <tt>Molecule</tt> object
     *
     * @return    The inputType value
     */
    IOType getInputType();

    /**
     *  Gets the mod attribute of the <tt>Molecule</tt> object
     *
     * @return    The mod value
     */
    int getModificationCounter();

    /**
     *  Gets the outputType attribute of the <tt>Molecule</tt> object
     *
     * @return    The outputType value
     */
    IOType getOutputType();

    String getPartialChargeVendor();

    /**
     *  Gets the residue attribute of the Molecule object
     *
     * @param  idx  Description of the Parameter
     * @return      The residue value
     */
    Residue getResidue(int idx);

    /**
     * Returns the number of residues.
     *
     * @return    The number of residues
     */
    int getResiduesSize();

    /**
     * Gets iterator for the Smallest Set of Smallest Rings (SSSR).
     *
     * <blockquote><pre>
     * RingIterator rit = mol.getRingIterator();
     * JOERing ring;
     * while(rit.hasNext())
     * {
     *   ring = rit.nextRing();
     *
     * }
     * </pre></blockquote>
     *
     * @return     The ring iterator
     * @see #getSSSR()
     * @see #findSSSR()
     * @see #atomIterator()
     * @see #bondIterator()
     * @see #conformerIterator()
     * @see #genericDataIterator
     * @see #nativeValueIterator()
     * @see BasicRingIterator
     * @.cite fig96
     */
    RingIterator getRingIterator();

    /**
     * Returns the number of rotatable bonds.
     *
     * @return    The number of rotatable bonds
     */
    int getRotorsSize();

    /**
     * Gets the Smallest Set of Smallest Rings (SSSR).
     *
     * @return    {@link java.util.Vector} of <tt>JOERing</tt>
     *
     * @see #getRingIterator()
     * @see #findSSSR()
     * @.cite fig96
     */
    List getSSSR();

    /**
     *  Gets the title attribute of the <tt>Molecule</tt> object.
     *
     * @return    The title value
     */
    String getTitle();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    boolean has2D();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    boolean has3D();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    boolean hasAromaticCorrected();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    boolean hasChainsPerceived();

    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return    <tt>true</tt> if the generic attribute/value pair exists
     */
    boolean hasData(String name);

    /**
     * Calculates the hashcode of a molecule using the methods <tt>AbstractDatabase.getHashcode</tt>
     * and <tt>AbstractDatabase.getSMILESHashcode</tt>.
     */
    int hashCode();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    boolean hasHydrogensAdded();

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    boolean hasNonZeroCoords();

    /**
     * Increase modification counter.
     *
     * @see #decrementMod()
     * @see #getModificationCounter()
     * @see #beginModify()
     * @see #endModify()
     * @see #endModify(boolean)
     */
    void incrementMod();

    /**
     *  Description of the Method
     *
     * @param  atom  Description of the Parameter
     * @return       Description of the Return Value
     */
    boolean insertAtom(Atom atom);

    /**
     *  Gets the flag if the automatic calculation of the formal charge of the
     *  atoms is allowed. This is for example used in the PH value correction
     *  method.
     *
     * @return    <tt>true</tt> if the calculation of the formal charge is
     *      allowed.
     */
    boolean isAssignFormalCharge();

    /**
     *  Gets the flag if the automatic calculation of the partial charge of the
     *  atoms is allowed.
     *
     * @return    <tt>true</tt> if the calculation of the partial charge is
     *      allowed.
     * @see joelib2.molecule.charge.GasteigerMarsili
     * @.cite gm78
     */
    boolean isAssignPartialCharge();

    /**
     *  Gets the chiral attribute of the Molecule object
     *
     * @return    The chiral value
     */
    boolean isChiral();

    /**
     *  Gets the correctedForPH attribute of the Molecule object
     *
     * @return    The correctedForPH value
     */
    boolean isCorrectedForPH();

    /**
     * Returns <tt>true</tt> if this molecule contains no atoms.
     *
     * @return   <tt>true</tt> if this molecule contains no atoms
     */
    boolean isEmpty();

    boolean isMoleculeHashing();

    /**
     * Kekulizes the molecule.
     *
     * @return    <tt>true</tt> if successfull
     */
    boolean kekulize();

    /**
     *  Gets an iterator over native descriptor values (<tt>int</tt> or <tt>double</tt>)
     * of this molecule.
     *
     * <blockquote><pre>
     * NativeValueIterator nativeIt = mol.nativeValueIterator();
     * double value;
     * String descName;
     * while (nativeIt.hasNext())
     * {
     *   value = nativeIt.nextDouble();
     *   descName = nativeIt.actualName();
     *
     * }
     * </pre></blockquote>
     *
     * @return    The native value iterator
     * @see #atomIterator()
     * @see #bondIterator()
     * @see #conformerIterator()
     * @see #getRingIterator()
     * @see #genericDataIterator
     */
    NativeValueIterator nativeValueIterator();

    /**
     * Creates only a new atom.
     *
     * @return    the created atom
     * @see #newBond()
     * @see #addAtomClone(Atom)
     * @see #newAtom()
     */
    Atom newAtom();

    /**
     * Creates a new atom and adds it to molecule.
     *
     * @return    the new atom
     * @see #addAtomClone(Atom)
     * @see #newAtom()
     */
    Atom newAtom(boolean add);

    /**
     * Creates only a new bond.
     *
     * @return    the created bond
     * @see #newAtom()
     */
    Bond newBond();

    /**
     * Returns and adds new residue informations for this molecule.
     *
     * @return    The residue informations.
     */
    Residue newResidue();

    int reHash();

    /**
     * @param  renumbered  of type <tt>Atom</tt>
     */
    void renumberAtoms(List<Atom> renumbered);

    /**
     * Reserves a initial capacity of atoms.
     *
     * @param  natoms  The number of atoms to reserve
     */
    void reserveAtoms(int natoms);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    ResidueIterator residueIterator();

    /**
     *  Clones molecule without additional data (e.g. descriptor data).
     *
     * @param  source  Description of the Parameter
     * @return         Description of the Return Value
     */
    Molecule set(final Molecule source);

    /**
     *  Clones molecule.
     * Missing descriptor entries, defined in <tt>descriptors</tt> will be
     * ignored.
     *
     * @param  source     Description of the Parameter
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     * @param  descriptors  descriptors to clone. If <tt>null</tt> all descriptors are cloned
     * @return            The new molecule
     */
    Molecule set(final Molecule source, boolean cloneDesc, String[] descriptors);

    /**
     *  Clones molecule.
     *
     * @param  source     Description of the Parameter
     * @param  cloneDesc  clones the PairData descriptors if <tt>true</tt>
     * @param  descriptors  descriptors to clone. If <tt>null</tt> all descriptors are cloned
     * @param  addDescs  Missing descriptor entries, defined in <tt>descriptors</tt>
     *          will be automatically added to the molecule if <tt>true</tt>
     * @return            The new molecule
     */
    Molecule set(final Molecule source, boolean cloneDesc, String[] descriptors,
        boolean addDescs);

    Molecule setAdd(final Molecule source);

    /**
     *  Sets the aromaticCorrected attribute of the <tt>Molecule</tt> object
     */
    void setAromaticCorrected();

    /**
     *  Sets the flag if the automatic calculation of the formal charge of the
     *  atoms is allowed. This is for example used in the PH value correction
     *  method.
     *
     * @param  assign  <tt>true</tt> if the calculation of the formal charge is
     *      allowed.
     */
    void setAssignFormalCharge(boolean assign);

    /**
     *  Sets the automaticPartialCharge attribute of the <tt>Molecule</tt> object
     *
     * @param  assign  The new automaticPartialCharge value
     */
    void setAssignPartialCharge(boolean assign);

    /**
     *  Sets the chainsPerceived attribute of the <tt>Molecule</tt> object
     */
    void setChainsPerceived();

    /**
     *  Sets the correctedForPH attribute of the <tt>Molecule</tt> object
     */
    void setCorrectedForPH();

    /**
     *  Sets the energy attribute of the <tt>Molecule</tt> object
     *
     * @param  energy  The new energy value
     */
    void setEnergy(double newEnergy);

    /**
     *  Sets the hydrogensAdded attribute of the <tt>Molecule</tt> object
     */
    void setHydrogensAdded();

    /**
     *  Sets the inputType attribute of the <tt>Molecule</tt> object
     *
     * @param  type  The new inputType value
     */
    void setInputType(IOType type);

    /**
     * This requires the canonization of the molecule and is time consuming.
     * @param uniqueHash The uniqueHash to set.
     */
    void setMoleculeHashing(boolean moleculeHashing);

    /**
     *  Sets the outputType attribute of the <tt>Molecule</tt> object
     *
     * @param  type  The new outputType value
     */
    void setOutputType(IOType type);

    void setPartialChargeVendor(String vendor);

    /**
     *  Sets the title attribute of the <tt>Molecule</tt> object
     *
     * @param  title  The new title value
     */
    void setTitle(String newTitle);

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    String toString();

    /**
     *  Description of the Method
     *
     * @param  type  Description of the Parameter
     * @return       Description of the Return Value
     */
    String toString(IOType type);

    /**
     *  Description of the Method
     *
     * @param  writeDesc  Description of the Parameter
     * @return                   Description of the Return Value
     */
    String toString(boolean writeDesc);

    /**
     *  Description of the Method
     *
     * @param  type              Description of the Parameter
     * @param  writeDesc  Description of the Parameter
     * @return                   Description of the Return Value
     */
    String toString(IOType type, boolean writeDesc);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
