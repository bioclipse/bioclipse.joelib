///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ProtonationHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.17 $
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

import joelib2.data.BasicElementHolder;
import joelib2.data.BasicProtonationModel;
import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.atomlabel.AtomIsNitrogen;
import joelib2.feature.types.atomlabel.AtomIsNonPolarHydrogen;
import joelib2.feature.types.atomlabel.AtomIsOxygen;
import joelib2.feature.types.atomlabel.AtomIsPhosphorus;
import joelib2.feature.types.atomlabel.AtomIsSulfur;
import joelib2.feature.types.atomlabel.AtomPartialCharge;
import joelib2.feature.types.atomlabel.AtomType;

import joelib2.math.BasicVector3D;

import joelib2.util.BasicBitVector;
import joelib2.util.BitVector;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.NbrAtomIterator;

import joelib2.util.types.BasicAtomInt;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom tree.
 *
 * @.author     wegnerj
 * @.wikipedia  Protonation
 * @.wikipedia  Deprotonation
 * @.wikipedia  PH
 * @.license GPL
 * @.cvsversion    $Revision: 1.17 $, $Date: 2005/02/17 16:48:36 $
 */
public class ProtonationHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.17 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:36 $";
    private static Category logger = Category.getInstance(
            ProtonationHelper.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            BasicElementHolder.class, BasicProtonationModel.class,
            AtomHeavyValence.class, AtomHybridisation.class,
            AtomImplicitValence.class, AtomIsHydrogen.class,
            AtomIsNitrogen.class, AtomIsNonPolarHydrogen.class,
            AtomIsOxygen.class, AtomIsPhosphorus.class, AtomIsSulfur.class,
            AtomPartialCharge.class, AtomType.class
        };

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void addHydrogens(Atom aToAdd, int hyb)
    {
        //              add hydrogens back to atom
        Molecule mol = aToAdd.getParent();
        int impval = AtomImplicitValence.getImplicitValence(aToAdd, hyb);
        int hcount = impval - AtomHeavyValence.valence(aToAdd);

        if (hcount != 0)
        {
            int index;
            BasicVector3D vec = new BasicVector3D();
            Atom atom;
            double brsum =
                BasicElementHolder.instance().correctedBondRad(1, 0) +
                BasicElementHolder.instance().correctedBondRad(aToAdd
                    .getAtomicNumber(), AtomHybridisation.getIntValue(aToAdd));
            AtomHybridisation.setHybridisation(aToAdd, hyb);
            mol.beginModify();

            for (index = 0; index < hcount; index++)
            {
                AtomHelper.getNewBondVector3D(aToAdd, vec, brsum);
                atom = mol.newAtom(true);
                atom.setAtomicNumber(1);
                atom.setType("H");
                atom.setCoords3D(vec);
                mol.addBond(atom.getIndex(), aToAdd.getIndex(), 1);
            }

            mol.endModify();
        }
    }

    /**
     * Adds hydrogens atoms to the given atom.
     * The pH value will not be corrected.
     *
     * @param  atom  The atom to which the hydogens should be added
     * @return               <tt>true</tt> if successfull
     */
    public static synchronized boolean addHydrogens(Molecule mol, Atom atom)
    {
        boolean added = false;

        if (atom.getParent() != mol)
        {
            logger.error(
                "Hydrogens can only added if atom is part of the parent molecule. Check atom object (cloned?).");
        }
        else
        {
            Atom hydrogen;
            int hcount;
            int count = 0;
            List<BasicAtomInt> hydrogens2add = new Vector<BasicAtomInt>();

            hcount = AtomImplicitValence.getImplicitValence(atom) -
                atom.getValence();

            if (hcount < 0)
            {
                hcount = 0;
            }

            if (hcount != 0)
            {
                hydrogens2add.add(new BasicAtomInt(atom, hcount));
                count += hcount;
            }

            if (count == 0)
            {
                return true;
            }

            //realloc memory in coordinate arrays for new hydroges
            double[] tmpf;
            double[] iter;

            if (mol instanceof ConformerMolecule)
            {
                for (int j = 0;
                        j < ((ConformerMolecule) mol).getConformers().size();
                        j++)
                {
                    iter = ((ConformerMolecule) mol).getConformer(j);
                    tmpf = new double[((mol.getAtomsSize() + count) * 3) + 10];
                    System.arraycopy(iter, 0, tmpf, 0, mol.getAtomsSize() * 3);
                    ((ConformerMolecule) mol).getConformers().set(j, tmpf);
                    iter = null;
                }

                //Just delete the pose coordinate array.  It will automatically
                //be reallocated when SetPose(int) is called.
                if (((ConformerMolecule) mol).getActualPose3D() != null)
                {
                    ((ConformerMolecule) mol).setActualPose3D(null);
                }
            }

            mol.beginModify();

            int hydrogenIndex;
            int confIndex;
            BasicVector3D v = new BasicVector3D();
            BasicAtomInt atomHCount;
            double hbrad = BasicElementHolder.instance().correctedBondRad(1, 0);

            for (int addIndex = 0; addIndex < hydrogens2add.size(); addIndex++)
            {
                atomHCount = (BasicAtomInt) hydrogens2add.get(addIndex);
                atom = atomHCount.atom;

                double bondlen = hbrad +
                    BasicElementHolder.instance().correctedBondRad(atom
                        .getAtomicNumber(), AtomHybridisation.getIntValue(atom));

                for (hydrogenIndex = 0; hydrogenIndex < atomHCount.intValue;
                        hydrogenIndex++)
                {
                    if (mol instanceof ConformerMolecule)
                    {
                        for (confIndex = 0;
                                confIndex <
                                ((ConformerMolecule) mol).getConformersSize();
                                confIndex++)
                        {
                            ((ConformerMolecule) mol).useConformer(confIndex);
                            AtomHelper.getNewBondVector3D(atom, v, bondlen);
                            ((ConformerMolecule) mol).getCoords3Darr()[(mol
                                .getAtomsSize()) * 3] = v.getX3D();
                            ((ConformerMolecule) mol).getCoords3Darr()[((mol
                                    .getAtomsSize()) * 3) + 1] = v.getY3D();
                            ((ConformerMolecule) mol).getCoords3Darr()[((mol
                                    .getAtomsSize()) * 3) + 2] = v.getZ3D();
                        }
                    }

                    hydrogen = mol.newAtom(true);
                    hydrogen.setType("H");
                    hydrogen.setAtomicNumber(1);
                    mol.addBond(atom.getIndex(), hydrogen.getIndex(), 1);
                    ((ConformerAtom) hydrogen).setCoords3Darr(
                        ((ConformerMolecule) mol).getCoords3Darr());
                }
            }

            mol.endModify();

            if (mol instanceof ConformerMolecule)
            {
                ((ConformerMolecule) mol).useConformer(0);
            }

            //reset atom type and partial charge flags
            // already nuked in end-modify method
            //mol.deleteData(AtomPartialCharge.getName());
            //mol.deleteData(AtomType.getName());
            added = true;
        }

        return added;
    }

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
     * @see BasicProtonationModel
     * @see #endModify()
     * @see #endModify(boolean)
     */
    public static synchronized boolean addHydrogens(Molecule mol,
        boolean polaronly, boolean correctForPH, boolean useCoordV)
    {
        if (mol.hasHydrogensAdded())
        {
            return true;
        }

        mol.setHydrogensAdded();

        Atom atom;
        Atom hydrogen;
        int hcount;
        int count = 0;
        List<BasicAtomInt> vhadd = new Vector<BasicAtomInt>();
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (polaronly &&
                    !(AtomIsNitrogen.isNitrogen(atom) ||
                        AtomIsOxygen.isOxygen(atom) ||
                        AtomIsSulfur.isSulfur(atom) ||
                        AtomIsPhosphorus.isPhosphorus(atom)))
            {
                continue;
            }

            hcount = AtomImplicitValence.getImplicitValence(atom) -
                atom.getValence();

            if (hcount < 0)
            {
                hcount = 0;
            }

            if (hcount != 0)
            {
                vhadd.add(new BasicAtomInt(atom, hcount));
                count += hcount;
            }
        }

        if (count == 0)
        {
            return true;
        }

        boolean hasCoords = mol.hasNonZeroCoords();

        //realloc memory in coordinate arrays for new hydrogens
        double[] tmpf = null;
        double[] iterf;

        if (mol instanceof ConformerMolecule)
        {
            int size = ((ConformerMolecule) mol).getConformers().size();

            for (int j = 0; j < size; j++)
            {
                iterf = ((ConformerMolecule) mol).getConformer(j);
                tmpf = new double[(mol.getAtomsSize() + count) * 3];

                //_c=coords3Darr
                //System.out.println("j"+j+" _c"+_c+" length"+_c.length+" (numAtoms() + count) * 3:"+((numAtoms() + count) * 3));
                //System.out.println("numAtoms():"+numAtoms()+" count:"+ count+" iterf.length:"+iterf.length+" tmpf.length:"+tmpf.length);
                if (hasCoords)
                {
                    System.arraycopy(iterf, 0, tmpf, 0, iterf.length);
                }

                ((ConformerMolecule) mol).setConformer(j, tmpf);
                iterf = null;
            }

            //Just delete the pose coordinate array.  It will automatically
            //be reallocated when SetPose(int) is called.
            if (((ConformerMolecule) mol).getActualPose3D() != null)
            {
                ((ConformerMolecule) mol).setActualPose3D(null);
            }
        }

        mol.beginModify();

        int m;
        BasicVector3D v = new BasicVector3D();
        double hbrad = BasicElementHolder.instance().correctedBondRad(1, 0);

        for (int k = 0; k < vhadd.size(); k++)
        {
            BasicAtomInt atomHCount = (BasicAtomInt) vhadd.get(k);
            atom = atomHCount.atom;

            double bondlen = hbrad +
                BasicElementHolder.instance().correctedBondRad(atom
                    .getAtomicNumber(), AtomHybridisation.getIntValue(atom));

            for (m = 0; m < atomHCount.intValue; m++)
            {
                //System.out.println("numConformers():"+numConformers()+"atomHCount.hCount:"+atomHCount.hCount);
                //remove this line
                if (useCoordV == true)
                {
                    AtomHelper.getNewBondVector3D(atom, v, bondlen);
                }

                // bug in this code
                // the memory allocated from endModify for _c is to small
                // the solution would be to allocate (numAtoms() + count) * 3
                // eventually over a global _hcount member variable
                // but lets think about it ...
                //_c=coords3Darr
                //                for (n = 0; n < numConformers(); n++)
                //                {
                //                    setConformer(n);
                //                    if (hasCoords)
                //                    {
                //                        atom.getNewBondVector(v, bondlen);
                //                        System.out.println("n"+n+" _c"+_c+" length"+_c.length+" numAtoms()*3:"+(numAtoms()*3));
                //                       // _c[(numAtoms()) * 3] = v.x();
                //                       // _c[(numAtoms()) * 3 + 1] = v.y();
                //                       // _c[(numAtoms()) * 3 + 2] = v.z();
                //                    }
                //                    else
                //                    {
                //                      v.set(0.0,0.0,0.0);
                //                       // _c[(numAtoms()) * 3] = 0.0;
                //                       // _c[(numAtoms()) * 3 + 1] = 0.0;
                //                       // _c[(numAtoms()) * 3 + 2] = 0.0;
                //                    }
                //                }
                hydrogen = mol.newAtom(true);
                hydrogen.setType("H");
                hydrogen.setAtomicNumber(1);
                hydrogen.setCoords3D(v.getX3D(), v.getY3D(), v.getZ3D());
                mol.addBond(atom.getIndex(), hydrogen.getIndex(), 1);

                //h.setCoordPtr(_c);
            }
        }

        mol.endModify();

        if (mol instanceof ConformerMolecule)
        {
            ((ConformerMolecule) mol).useConformer(0);
        }

        //reset atom type and partial charge flags
        mol.deleteData(AtomPartialCharge.getName());
        mol.deleteData(AtomType.getName());

        return true;
    }

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
    public static boolean deleteHydrogen(Molecule mol, Atom atom)
    {
        boolean deleted = false;

        if (atom.getParent() != mol)
        {
            logger.error(
                "Atom can only be deleted in parent molecule. Check atom object (cloned?).");
        }
        else
        {
            List<Bond> bonds2delete = new Vector<Bond>();
            NbrAtomIterator nait = atom.nbrAtomIterator();

            while (nait.hasNext())
            {
                nait.nextNbrAtom();
                bonds2delete.add(nait.actualBond());
            }

            mol.incrementMod();

            for (int bondIdx = 0; bondIdx < bonds2delete.size(); bondIdx++)
            {
                mol.deleteBond((Bond) bonds2delete.get(bondIdx));
            }

            mol.decrementMod();

            int idx = 0;

            if (mol instanceof ConformerMolecule)
            {
                if (atom.getIndex() != (int) mol.getAtomsSize())
                {
                    idx = ((ConformerAtom) atom).getCoordinateIdx();

                    int size = mol.getAtomsSize() - atom.getIndex();

                    //Update conformer coordinates
                    for (int k = 0;
                            k <
                            ((ConformerMolecule) mol).getConformers().size();
                            k++)
                    {
                        System.arraycopy((double[]) ((ConformerMolecule) mol)
                            .getConformers().get(k), idx + 3,
                            (double[]) ((ConformerMolecule) mol).getConformers()
                            .get(k), idx, 3 * size);
                    }

                    //Update pose coordinates if necessary
                    if (((ConformerMolecule) mol).getActualPose3D() != null)
                    {
                        System.arraycopy(((ConformerMolecule) mol)
                            .getActualPose3D(), idx + 3,
                            ((ConformerMolecule) mol).getActualPose3D(), idx,
                            3 * size);
                    }
                }
            }

            //System.out.println("_atom.size():"+_atom.size()+" idx:"+atom.getIdx());
            mol.getAtoms().remove(atom.getIndex() - 1);

            //reset all the indices to the atoms
            Atom tmp;
            AtomIterator ait = mol.atomIterator();
            idx = 1;

            while (ait.hasNext())
            {
                tmp = ait.nextAtom();
                tmp.setIndex(idx);
                idx++;
            }

            deleted = true;
        }

        return deleted;
    }

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
    public static synchronized boolean deleteHydrogens(Molecule mol)
    {
        Atom atom = null;
        Atom nbr = null;
        List<Atom> delatoms = new Vector<Atom>();
        List<Atom> atoms2add = new Vector<Atom>();
        AtomIterator ait = mol.atomIterator();

        //if(logger.isDebugEnabled())logger.debug("Start removing H atoms"+this.numAtoms());
        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (AtomIsHydrogen.isHydrogen(atom))
            {
                delatoms.add(atom);
            }
        }

        if (delatoms.size() == 0)
        {
            return true;
        }

        mol.beginModify();

        //find bonds to delete
        List<Bond> bonds2delete = new Vector<Bond>();
        ait.reset();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            //      System.out.println("atom element symbol:" + JOEElementTable.instance().getSymbol(atom.getAtomicNum()));
            if (!AtomIsHydrogen.isHydrogen(atom))
            {
                NbrAtomIterator nait = atom.nbrAtomIterator();

                while (nait.hasNext())
                {
                    nbr = nait.nextNbrAtom();

                    //          System.out.println("nbr element symbol:" + JOEElementTable.instance().getSymbol(nbr.getAtomicNum()));
                    //          System.out.println("is H: "+nbr.isHydrogen());
                    if (AtomIsHydrogen.isHydrogen(nbr))
                    {
                        bonds2delete.add(nait.actualBond());
                    }
                }
            }
        }

        mol.incrementMod();

        for (int j = 0; j < bonds2delete.size(); j++)
        {
            mol.deleteBond((Bond) bonds2delete.get(j));
        }

        //delete bonds
        mol.decrementMod();

        int idx1 = 0;

        int idx2 = 0;
        ait.reset();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (!AtomIsHydrogen.isHydrogen(atom))
            {
                if (mol instanceof ConformerMolecule)
                {
                    //Update conformer coordinates
                    for (int k = 0;
                            k <
                            ((ConformerMolecule) mol).getConformers().size();
                            k++)
                    {
                        System.arraycopy((double[]) ((ConformerMolecule) mol)
                            .getConformers().get(k), idx1 * 3,
                            (double[]) ((ConformerMolecule) mol).getConformers()
                            .get(k), idx2 * 3, 3);
                    }
                }

                //        //Update pose coordinates if necessary
                //        if (actualPose3D != null)
                //        {
                //          System.arraycopy(actualPose3D, idx1 * 3, actualPose3D, idx2 * 3, 3);
                //        }
                idx2++;
                atoms2add.add(atom);
            }

            idx1++;
        }

        mol.getAtoms().clear();

        for (int i = 0; i < atoms2add.size(); i++)
        {
            mol.getAtoms().add(atoms2add.get(i));
        }

        //reset all the indices to the atoms
        ait.reset();
        idx1 = 1;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            atom.setIndex(idx1);

            //System.out.println("RESET to:"+idx1);
            idx1++;
        }

        if (logger.isDebugEnabled())
        {
            //check atom neighbours
            ait.reset();

            int max = mol.getAtomsSize();
            NbrAtomIterator nbrIterator;

            while (ait.hasNext())
            {
                atom = ait.nextAtom();

                if (nbr.getIndex() > max)
                {
                    logger.error("Atom " + atom.getIndex() +
                        " index should not be greater " + max + ".");
                }

                nbrIterator = atom.nbrAtomIterator();

                while (nbrIterator.hasNext())
                {
                    nbr = nbrIterator.nextNbrAtom();

                    //System.out.println("Molecule:Atom ("+atom.getIdx()+") neighbour index: "+nbr.getIdx());
                    if (nbr.getIndex() > max)
                    {
                        logger.error("Atom " + nbr.getIndex() +
                            " should not exist(" + max + ").");
                    }
                }
            }
            //System.out.println("");
        }

        mol.endModify(true);

        return true;
    }

    /**
     * @param delatm
     */
    public static void deleteHydrogens(Atom atom)
    {
        Molecule mol = atom.getParent();
        Atom nbr;
        List<Atom> atom2delete = new Vector<Atom>();
        NbrAtomIterator nait = atom.nbrAtomIterator();

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();

            if (AtomIsHydrogen.isHydrogen(nbr))
            {
                atom2delete.add(nbr);
            }
        }

        //delete attached hydrogens
        mol.incrementMod();

        for (int j = 0; j < atom2delete.size(); j++)
        {
            mol.deleteAtom((Atom) atom2delete.get(j));
        }

        mol.decrementMod();
    }

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
    public static synchronized boolean deleteHydrogens(Molecule mol, Atom atom)
    {
        boolean deleted = false;

        if (atom.getParent() != mol)
        {
            logger.error(
                "Atom can only be deleted in parent molecule. Check atom object (cloned?).");
        }
        else
        {
            mol.beginModify();

            Atom nbr;
            BitVector a2deleteSet = new BasicBitVector();
            List<Atom> atom2delete = new Vector<Atom>();
            NbrAtomIterator nait = atom.nbrAtomIterator();

            while (nait.hasNext())
            {
                nbr = nait.nextNbrAtom();

                if (AtomIsHydrogen.isHydrogen(nbr))
                {
                    atom2delete.add(nbr);
                    a2deleteSet.set(nbr.getIndex());
                }
            }

            if (atom2delete.size() == 0)
            {
                return true;
            }

            mol.incrementMod();

            int idx1 = 0;
            int idx2 = 0;
            AtomIterator ait = mol.atomIterator();

            while (ait.hasNext())
            {
                nbr = ait.nextAtom();

                if (!a2deleteSet.get(nbr.getIndex()))
                {
                    if (mol instanceof ConformerMolecule)
                    {
                        //Update conformer coordinates
                        for (int k = 0;
                                k <
                                ((ConformerMolecule) mol).getConformers().size();
                                k++)
                        {
                            System.arraycopy((double[]) ((ConformerMolecule) mol)
                                .getConformers().get(k), idx1 * 3,
                                (double[]) ((ConformerMolecule) mol)
                                .getConformers().get(k), idx2 * 3, 3);
                        }

                        //Update pose coordinates if necessary
                        if (((ConformerMolecule) mol).getActualPose3D() != null)
                        {
                            System.arraycopy(((ConformerMolecule) mol)
                                .getActualPose3D(), idx1 * 3,
                                ((ConformerMolecule) mol).getActualPose3D(),
                                idx2 * 3, 3);
                        }
                    }

                    idx2++;
                }

                idx1++;
            }

            for (int i = 0; i < atom2delete.size(); i++)
            {
                mol.deleteAtom((Atom) atom2delete.get(i));
            }

            mol.decrementMod();
            mol.endModify();
            deleted = true;
        }

        return deleted;
    }

    /**
     * Delete all non polar hydrogens from molecule.
     * All hydrogens in neighbourhood to N,O,P or S are treated as polar.
     *
     * @return    <tt>true</tt> if successfull
     */
    public static synchronized boolean deleteNonPolarHydrogens(Molecule mol)
    {
        Atom atom;
        List<Atom> delatoms = new Vector<Atom>();
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (AtomIsNonPolarHydrogen.isNonPolarHydrogen(atom))
            {
                delatoms.add(atom);
            }
        }

        if (delatoms.size() == 0)
        {
            return (true);
        }

        mol.beginModify();

        for (int i = 0; i < delatoms.size(); i++)
        {
            mol.deleteAtom((Atom) delatoms.get(i));
        }

        mol.endModify(true);

        return true;
    }

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
