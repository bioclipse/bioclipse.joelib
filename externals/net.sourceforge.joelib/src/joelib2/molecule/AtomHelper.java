///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
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

import joelib2.feature.types.atomlabel.AtomBondOrderSum;
import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomInRing;
import joelib2.feature.types.atomlabel.AtomIsAlphaBetaUnsaturated;
import joelib2.feature.types.atomlabel.AtomIsCarbon;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.atomlabel.AtomIsOxygen;
import joelib2.feature.types.bondlabel.BondInRing;

import joelib2.math.BasicMatrix3D;
import joelib2.math.BasicVector3D;
import joelib2.math.MathHelper;
import joelib2.math.Matrix3D;
import joelib2.math.Vector3D;

import joelib2.ring.Ring;

import joelib2.util.iterator.BasicRingIterator;
import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.NbrAtomIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom tree.
 *
 * @.author     wegnerj
 * @.wikipedia Atom
 * @.license GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/17 16:48:36 $
 */
public class AtomHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(AtomHelper.class
            .getName());

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adjust attached acyclic bond lengths.
     * @param hyb
     */
    public static void adjustAcyclicBondLengths(Atom atom, int hyb)
    {
        Atom nbr;
        double br1;
        double br2;
        br1 = BasicElementHolder.instance().correctedBondRad(atom
                .getAtomicNumber(), hyb);

        NbrAtomIterator nait = atom.nbrAtomIterator();

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();

            if (!BondInRing.isInRing(nait.actualBond()))
            {
                br2 = BasicElementHolder.instance().correctedBondRad(nbr
                        .getAtomicNumber(), AtomHybridisation.getIntValue(nbr));
                BondHelper.setLength(nait.actualBond(), atom, br1 + br2);
            }
        }
    }

    /**
     *  Apply rotation matrix <tt>m</tt> to the bond between atom <tt>a1</tt> and
     *  <tt>a2</tt> in molecule <tt>mol</tt> .
     *
     * @param  mol  molecule
     * @param  matrix    rotation matrix
     * @param  atom1   atom number one
     * @param  atom2   atom number two
     */
    public static void applyRotMatToBond(Molecule mol, Matrix3D matrix,
        Atom atom1, Atom atom2)
    {
        List<Atom> children = new Vector<Atom>();
        mol.findChildren(children, atom1.getIndex(), atom2.getIndex());
        children.add(atom2);

        Vector3D vector;

        for (int i = 0; i < children.size(); i++)
        {
            vector = children.get(i).getCoords3D();
            vector.subing(atom1.getCoords3D());
            vector.muling(matrix);
            vector.adding(atom1.getCoords3D());
            children.get(i).setCoords3D(vector);
        }
    }

    /**
     * Modifies a hydrogen atom to methyl group with three explicit hydrogens.
     *
     * @return    <tt>true</tt> if successfull
     */
    public static boolean changeHtoMethyl(Atom atomOrig)
    {
        boolean changed = false;

        if (AtomIsHydrogen.isHydrogen(atomOrig))
        {
            Molecule mol = atomOrig.getParent();
            mol.beginModify();
            atomOrig.setAtomicNumber(6);
            atomOrig.setType("C3");
            AtomHybridisation.setHybridisation(atomOrig, 3);

            Atom atom = null;
            Bond bond;
            NbrAtomIterator nait = atomOrig.nbrAtomIterator();

            if (nait.hasNext())
            {
                atom = nait.nextNbrAtom();
                bond = nait.actualBond();

                double br1;
                double br2;
                br1 = BasicElementHolder.instance().correctedBondRad(6, 3);
                br2 = BasicElementHolder.instance().correctedBondRad(atom
                        .getAtomicNumber(),
                        AtomHybridisation.getIntValue(atom));
                BondHelper.setLength(bond, atom, br1 + br2);

                Atom hatom;
                br2 = BasicElementHolder.instance().correctedBondRad(1, 0);

                BasicVector3D vec = new BasicVector3D();

                for (int j = 0; j < 3; j++)
                {
                    hatom = mol.newAtom(true);
                    hatom.setAtomicNumber(1);
                    hatom.setType("H");
                    getNewBondVector3D(atomOrig, vec, br1 + br2);
                    hatom.setCoords3D(vec);
                    mol.addBond(atomOrig.getIndex(), mol.getAtomsSize(), 1);
                }

                mol.endModify();
                changed = true;
            }
            else
            {
                mol.endModify();
            }
        }

        return changed;
    }

    /**
     * @param atom
     */
    public static boolean correctFormalCharge(Atom atom)
    {
        boolean wasCorrected = false;

        // at the moment: correct only carbon
        if (AtomIsCarbon.isCarbon(atom) && !AtomInRing.isInRing(atom))
        {
            if (AtomBondOrderSum.getIntValue(atom) ==
                    BasicElementHolder.instance().getExteriorElectrons(
                        atom.getAtomicNumber()))
            {
                if (atom.getFormalCharge() != 0)
                {
                    logger.warn(atom.getParent().getTitle() +
                        ": Resetting invalid formal charge at atom " +
                        atom.getIndex() +
                        ". Maybe input or protonation model problems?");
                    atom.setFormalCharge(0);
                    wasCorrected = true;
                }
            }
        }

        return wasCorrected;
    }

    /**
     *  Count the bonds of the bond order <tt>order</tt> .
     *
     * @param  order  the bond <tt>order</tt> of the bonds to count
     * @return        the number of the counted bonds with the given bond <tt>
     *      order</tt>
     */
    public static int countBondsOfOrder(Atom atom, int order)
    {
        BondIterator bit = atom.bondIterator();
        Bond bond;
        int count = 0;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (bond.getBondOrder() == order)
            {
                count++;
            }
        }

        return count;
    }

    /**
     *  Gets the newBondVector attribute of the <tt>Atom</tt> object
     *
     * @param  newXYZ       Description of the Parameter
     * @param  length  Description of the Parameter
     * @return         The newBondVector value
     */
    public static boolean getNewBondVector3D(Atom atom, Vector3D newXYZ,
        double length)
    {
        newXYZ.set(BasicVector3D.ZERO);

        boolean state = false;

        if (atom.getValence() == 0)
        {
            state = getNBV3Dvalence0(atom, newXYZ, length);
        }
        else if (atom.getValence() == 1)
        {
            state = getNBV3Dvalence1(atom, newXYZ, length);
        }
        else if (atom.getValence() == 2)
        {
            state = getNBV3Dvalence2(atom, newXYZ, length);
        }
        else if (atom.getValence() == 3)
        {
            state = getNBV3Dvalence3(atom, newXYZ, length);
        }

        return state;
    }

    public static boolean isElementOfGroup(Atom atom, int group)
    {
        boolean isGroup = false;

        if ((group == 8) && (atom.getAtomicNumber() == 2))
        {
            isGroup = true;
        }
        else
        {
            if (group <= 2)
            {
                if ((group == 1) && (atom.getAtomicNumber() == 1))
                {
                    isGroup = true;
                }
                else
                {
                    isGroup = ((atom.getAtomicNumber() == (2 + group)) ||
                            (atom.getAtomicNumber() == (10 + group)) ||
                            (atom.getAtomicNumber() == (18 + group)) ||
                            (atom.getAtomicNumber() == (36 + group)) ||
                            (atom.getAtomicNumber() == (54 + group)) ||
                            (atom.getAtomicNumber() == (86 + group)));
                }
            }
            else
            {
                isGroup = ((atom.getAtomicNumber() == (2 + group)) ||
                        (atom.getAtomicNumber() == (10 + group)) ||
                        (atom.getAtomicNumber() == (28 + group)) ||
                        (atom.getAtomicNumber() == (46 + group)) ||
                        (atom.getAtomicNumber() == (78 + group)) ||
                        (atom.getAtomicNumber() == (110 + group)));
            }
        }

        return isGroup;
    }

    /**
     *  Returns <tt>true</tt> if this is a atom in a ring of given size.
     *
     * @param  size  size of the ring
     * @return       <tt>true</tt> if this is a atom in a ring of given size
     */
    public static boolean isInRingSize(Atom atom, int size)
    {
        List rings = atom.getParent().getSSSR();

        if (rings == null)
        {
            logger.error("No SSSR data available.");

            //      throw new Exception("No SSSR data available.");
        }

        boolean isInRing = false;

        if (AtomInRing.isInRing(atom))
        {
            BasicRingIterator rit = new BasicRingIterator(rings);
            Ring ring;

            while (rit.hasNext())
            {
                ring = rit.nextRing();

                if (ring.isInRing(atom.getIndex()) && (ring.size() == size))
                {
                    isInRing = true;

                    break;
                }
            }
        }

        return isInRing;
    }

    /**
     *  Returns <tt>true</tt> if this atom (first) is with <tt>atom</tt> (fourth)
     *  connected to another two atoms (second, third). E.g. <tt>this</tt> -'other
     *  atom 1'--'other atom 2'-<tt>atom</tt>
     *
     * @param  atom  Description of the Parameter
     * @return     if this atom (first) is with <tt>atom</tt> (fourth) connected
     *      to another two atoms (second, third).
     */
    public static boolean isOneFour(Atom atomOrig, Atom atom)
    {
        Atom atom1 = atomOrig;
        Atom atom2 = atom;
        Bond bond1;
        Bond bond2;

        BondIterator bit1 = atom1.bondIterator();
        BondIterator bit2 = atom2.bondIterator();
        boolean isOneFour = false;

        while (bit1.hasNext())
        {
            bond1 = bit1.nextBond();
            bit2.reset();

            while (bit2.hasNext())
            {
                bond2 = bit2.nextBond();

                if ((bond1.getNeighbor(atom1)).isConnected(
                            bond2.getNeighbor(atom2)))
                {
                    isOneFour = true;

                    break;
                }
            }
        }

        return isOneFour;
    }

    /**
     *  Returns <tt>true</tt> if this atom (first) is with <tt>atom</tt> (third)
     *  connected to another atom (second). E.g. <tt>this</tt> -'other atom'-<tt>
     *  atom</tt>
     *
     * @param  atom  the third atom
     * @return       if this atom (first) is with <tt>atom</tt> (third) connected
     *      to another atom (second)
     */
    public static boolean isOneThree(Atom atomOrig, Atom atom)
    {
        Atom atom1 = atomOrig;
        Atom atom2 = atom;
        Bond bond1;
        Bond bond2;

        BondIterator bit1 = atom1.bondIterator();
        BondIterator bit2 = atom2.bondIterator();
        boolean isOneThree = false;

        while (bit1.hasNext())
        {
            bond1 = bit1.nextBond();
            bit2.reset();

            while (bit2.hasNext())
            {
                bond2 = bit2.nextBond();

                if (bond1.getNeighbor(atom1) == bond2.getNeighbor(atom2))
                {
                    isOneThree = true;

                    break;
                }
            }
        }

        /*
         Vector bonds1, bonds2;
         bonds1 = atom1.getBonds();
         bonds2 = atom2.getBonds();
         for (int i = 0; i < bonds1.length(); i++)
         {
         bond1 = (Bond) bonds1.get(i);
         for (int j = 0; j < bonds2.length(); j++)
         {
         bond2 = (Bond) bonds2.get(j);
         if (bond1.getNbrAtom(atom1) == bond2.getNbrAtom(atom2))
         return (true);
         }
         }
         */
        return isOneThree;
    }

    /**
     * Sets hybridization and geometry of this atom.
     *
     * @param  hyb  The hybridisation of this atom
     * @return      <tt>true</tt> if successfull
     */
    public static boolean setHybAndGeom(Atom atom, int hyb)
    {
        if (checkValence(atom, hyb))
        {
            ProtonationHelper.deleteHydrogens(atom);

            double targetAngle = setTargetAngle(atom, hyb);
            adjustAcyclicBondLengths(atom, hyb);

            if (atom.getValence() > 1)
            {
                List ringNbrs = getRingNbrs(atom);
                List nonRingNbrs = getNonRingNbrs(atom);

                //adjust geometries of heavy atoms according to hybridization
                if (hyb == 1)
                {
                    setGeometryHybSP(atom, nonRingNbrs, targetAngle);
                }
                else if (hyb == 2)
                {
                    setGeometryHybSP2(atom, nonRingNbrs, ringNbrs, targetAngle);
                }
                else if (hyb == 3)
                {
                    setGeometryHybSP3(atom, nonRingNbrs, ringNbrs, targetAngle);
                }
            }

            ProtonationHelper.addHydrogens(atom, hyb);
        }

        return (true);
    }

    public static double smallestBondAngle(Atom atom)
    {
        Atom atom1;
        Atom atom2;
        Vector3D vec1;
        Vector3D vec2;
        double degrees;
        double minDegrees = 360.0;
        Bond bond;

        for (int j = 0; j < atom.getBonds().size(); j++)
        {
            bond = ((Bond) atom.getBonds().get(j));
            atom1 = bond.getNeighbor(atom);

            for (int k = j + 1; k < atom.getBonds().size(); k++)
            {
                if (k >= atom.getBonds().size())
                {
                    break;
                }

                bond = ((Bond) atom.getBonds().get(k));
                atom2 = bond.getNeighbor(atom);

                vec1 = atom1.getCoords3D().sub(atom.getCoords3D());
                vec2 = atom2.getCoords3D().sub(atom.getCoords3D());
                degrees = BasicVector3D.angle(vec1, vec2);

                if (degrees < minDegrees)
                {
                    minDegrees = degrees;
                }
            }
        }

        return minDegrees;
    }

    /**
     * @param hyb
     * @return
     */
    private static boolean checkValence(Atom atom, int hyb)
    {
        boolean success = false;

        if (!((hyb == 0) && (AtomHeavyValence.valence(atom) > 1)))
        {
            if (!((hyb == 1) && (AtomHeavyValence.valence(atom) > 2)))
            {
                if (!((hyb == 2) && (AtomHeavyValence.valence(atom) > 3)))
                {
                    if (!((hyb == 3) && (AtomHeavyValence.valence(atom) > 4)))
                    {
                        success = true;
                    }
                }
            }
        }

        return success;
    }

    /**
     * @param length
     * @return
     */
    private static boolean getNBV3Dvalence0(Atom atom, Vector3D newXYZ,
        double length)
    {
        newXYZ.set(BasicVector3D.XAXIS);
        newXYZ.muling(length);
        newXYZ.adding(atom.getCoords3D());

        return true;
    }

    /**
     * @param newXYZ
     * @param length
     * @return
     */
    private static boolean getNBV3Dvalence1(Atom atomOrig, Vector3D newXYZ,
        double length)
    {
        Atom atom = null;
        Vector3D vtmp = new BasicVector3D();
        Vector3D vec1 = new BasicVector3D();
        Vector3D vec2 = new BasicVector3D();
        NbrAtomIterator nait = atomOrig.nbrAtomIterator();

        if (nait.hasNext())
        {
            atom = nait.nextNbrAtom();
        }

        vtmp = atomOrig.getCoords3D().sub(atom.getCoords3D());

        if ((AtomHybridisation.getIntValue(atomOrig) == 2) ||
                (AtomIsOxygen.isOxygen(atomOrig) &&
                    AtomIsAlphaBetaUnsaturated.isValue(atomOrig)))
        {
            getNBV3Dvalence1Hyb2(atomOrig, newXYZ, vtmp);
        }

        if (AtomHybridisation.getIntValue(atomOrig) == 3)
        {
            BasicVector3D.cross(vec1, vtmp, BasicVector3D.XAXIS);
            BasicVector3D.cross(vec2, vtmp, BasicVector3D.YAXIS);

            if (vec1.length() < vec2.length())
            {
                vec1.set(vec2);
            }

            Matrix3D matrix = new BasicMatrix3D();
            matrix.rotAboutAxisByAngle(vec1, 70.5);
            BasicVector3D.mul(newXYZ, matrix, vtmp);
            newXYZ.normalize();
        }

        if (AtomHybridisation.getIntValue(atomOrig) == 1)
        {
            newXYZ.set(vtmp);
        }

        newXYZ.muling(length);
        newXYZ.adding(atomOrig.getCoords3D());

        return true;
    }

    /**
     * @param newXYZ
     * @param length
     */
    private static void getNBV3Dvalence1Hyb2(Atom atom, Vector3D newXYZ,
        Vector3D vtmp)
    {
        Vector3D vec1 = new BasicVector3D();
        Vector3D vec2 = new BasicVector3D();
        boolean quit = false;
        Atom atom1;
        Atom atom2;
        vec2 = BasicVector3D.ZERO;

        NbrAtomIterator nait1 = atom.nbrAtomIterator();

        while (nait1.hasNext() && !quit)
        {
            atom1 = nait1.nextNbrAtom();

            NbrAtomIterator nait2 = atom1.nbrAtomIterator();

            while (nait2.hasNext() && !quit)
            {
                atom2 = nait2.nextNbrAtom();

                if ((atom1 != null) && (atom2 != null) && (atom2 != atom))
                {
                    BasicVector3D.sub(vec2, atom1.getCoords3D(),
                        atom2.getCoords3D());
                    quit = true;
                }
            }
        }

        if (vec2 == BasicVector3D.ZERO)
        {
            BasicVector3D.cross(vec1, vtmp, BasicVector3D.XAXIS);
            BasicVector3D.cross(vec2, vtmp, BasicVector3D.YAXIS);

            if (vec1.length() < vec2.length())
            {
                vec1.set(vec2);
            }
        }
        else
        {
            BasicVector3D.cross(vec1, vtmp, vec2);
        }

        Matrix3D matrix = new BasicMatrix3D();
        matrix.rotAboutAxisByAngle(vec1, 60.0);
        BasicVector3D.mul(newXYZ, matrix, vtmp);
        newXYZ.normalize();
    }

    /**
     * @param newXYZ
     * @param length
     * @return
     */
    private static boolean getNBV3Dvalence2(Atom atomOrig, Vector3D newXYZ,
        double length)
    {
        Atom atom = null;
        Vector3D vec1 = new BasicVector3D();
        Vector3D vec2 = new BasicVector3D();
        Vector3D vsum = new BasicVector3D();
        Vector3D vnorm = new BasicVector3D();
        NbrAtomIterator nait = atomOrig.nbrAtomIterator();
        boolean state = false;

        if (nait.hasNext())
        {
            atom = nait.nextNbrAtom();
            BasicVector3D.sub(vec1, atomOrig.getCoords3D(), atom.getCoords3D());

            if (nait.hasNext())
            {
                atom = nait.nextNbrAtom();
                BasicVector3D.sub(vec2, atomOrig.getCoords3D(),
                    atom.getCoords3D());
                vec1.normalize();
                vec2.normalize();
                BasicVector3D.add(vsum, vec1, vec2);
                vsum.normalize();

                if (AtomHybridisation.getIntValue(atomOrig) == 2)
                {
                    newXYZ.set(vsum);
                }

                if (AtomHybridisation.getIntValue(atomOrig) == 3)
                {
                    BasicVector3D.cross(vnorm, vec2, vec1);
                    vnorm.normalize();

                    vsum.muling(MathHelper.ONE_OVER_SQRT3);
                    vnorm.muling(MathHelper.SQRT_TWO_THIRDS);

                    BasicVector3D.add(newXYZ, vsum, vnorm);
                }

                newXYZ.muling(length);
                newXYZ.adding(atomOrig.getCoords3D());
                state = true;
            }
        }

        return state;
    }

    /**
     * @param newXYZ
     * @param length
     * @return
     */
    private static boolean getNBV3Dvalence3(Atom atomOrig, Vector3D newXYZ,
        double length)
    {
        Atom atom = null;
        Vector3D vtmp = new BasicVector3D();
        Vector3D vsum = new BasicVector3D();
        NbrAtomIterator nait = atomOrig.nbrAtomIterator();

        while (nait.hasNext())
        {
            atom = nait.nextNbrAtom();
            BasicVector3D.sub(vtmp, atomOrig.getCoords3D(), atom.getCoords3D());
            vtmp.normalize();
            vtmp.diving(3.0);
            vsum.adding(vtmp);
        }

        vsum.normalize();
        newXYZ.set(vsum);
        newXYZ.muling(length);
        newXYZ.adding(atomOrig.getCoords3D());

        return (true);
    }

    /**
     * Find ring atoms.
     *
     * @return
     */
    private static List getNonRingNbrs(Atom atom)
    {
        NbrAtomIterator nait = atom.nbrAtomIterator();
        Atom nbr;
        Vector nonRingNbrs = new Vector();

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();

            if (!BondInRing.isInRing(nait.actualBond()))
            {
                nonRingNbrs.add(nbr);
            }
        }

        return nonRingNbrs;
    }

    /**
     * Find ring atoms.
     *
     * @return
     */
    private static List<Atom> getRingNbrs(Atom atom)
    {
        NbrAtomIterator nait = atom.nbrAtomIterator();
        Atom nbr;
        List<Atom> ringNbrs = new Vector<Atom>();

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();

            if (BondInRing.isInRing(nait.actualBond()))
            {
                ringNbrs.add(nbr);
            }
        }

        return ringNbrs;
    }

    /**
     * @param nonRingNbrs
     */
    private static void setGeometryHybSP(Atom atom, List nonRingNbrs,
        double targetAngle)
    {
        if (nonRingNbrs.size() >= 2)
        {
            double angle;
            BasicMatrix3D matrix = new BasicMatrix3D();
            BasicVector3D vec1 = new BasicVector3D();
            BasicVector3D vec2 = new BasicVector3D();
            BasicVector3D norm = new BasicVector3D();
            Atom atom1 = (Atom) nonRingNbrs.get(0);
            Atom atom2 = (Atom) nonRingNbrs.get(1);
            BasicVector3D.sub(vec1, atom1.getCoords3D(), atom.getCoords3D());
            vec1.normalize();
            BasicVector3D.sub(vec2, atom2.getCoords3D(), atom.getCoords3D());
            vec2.normalize();
            BasicVector3D.cross(norm, vec1, vec2);
            angle = BasicVector3D.xyzVectorAngle(vec1, vec2) - targetAngle;
            matrix.rotAboutAxisByAngle(norm, -angle);
            applyRotMatToBond(atom.getParent(), matrix, atom, atom1);
        }
    }

    /**
     * @param nonRingNbrs
     * @param ringNbrs
     * @param targetAngle
     */
    private static void setGeometryHybSP2(Atom atom, List nonRingNbrs,
        List ringNbrs, double targetAngle)
    {
        if ((ringNbrs.size() >= 2) && (nonRingNbrs.size() >= 1))
        {
            double angle;
            BasicMatrix3D matrix = new BasicMatrix3D();
            BasicVector3D vec1 = new BasicVector3D();
            BasicVector3D vec2 = new BasicVector3D();
            BasicVector3D vec3 = new BasicVector3D();
            BasicVector3D sss = new BasicVector3D();
            BasicVector3D norm = new BasicVector3D();
            Atom atom1 = (Atom) nonRingNbrs.get(0);
            Atom rAtom1 = (Atom) ringNbrs.get(0);
            Atom rAtom2 = (Atom) ringNbrs.get(1);
            BasicVector3D.sub(vec1, rAtom1.getCoords3D(), atom.getCoords3D());
            vec1.normalize();
            BasicVector3D.sub(vec2, rAtom2.getCoords3D(), atom.getCoords3D());
            vec2.normalize();
            BasicVector3D.sub(vec3, atom1.getCoords3D(), atom.getCoords3D());
            BasicVector3D.add(sss, vec1, vec2);
            sss.normalize();
            sss.muling(-1.0f);
            BasicVector3D.cross(norm, sss, vec3);
            angle = BasicVector3D.xyzVectorAngle(sss, vec3);
            matrix.rotAboutAxisByAngle(norm, angle);
            applyRotMatToBond(atom.getParent(), matrix, atom, atom1);
        }
        else
        {
            if (nonRingNbrs.size() >= 2)
            {
                double angle;
                BasicMatrix3D matrix = new BasicMatrix3D();
                BasicVector3D vec1 = new BasicVector3D();
                BasicVector3D vec2 = new BasicVector3D();
                BasicVector3D norm = new BasicVector3D();
                Atom atom1 = (Atom) nonRingNbrs.get(0);
                Atom atom2 = (Atom) nonRingNbrs.get(1);
                BasicVector3D.sub(vec1, atom1.getCoords3D(),
                    atom.getCoords3D());
                vec1.normalize();
                BasicVector3D.sub(vec2, atom2.getCoords3D(),
                    atom.getCoords3D());
                vec2.normalize();
                BasicVector3D.cross(norm, vec1, vec2);
                angle = BasicVector3D.xyzVectorAngle(vec1, vec2) - targetAngle;
                matrix.rotAboutAxisByAngle(norm, -angle);
                applyRotMatToBond(atom.getParent(), matrix, atom, atom1);
            }

            if (nonRingNbrs.size() >= 3)
            {
                double angle;
                BasicMatrix3D matrix = new BasicMatrix3D();
                BasicVector3D vec1 = new BasicVector3D();
                BasicVector3D vec2 = new BasicVector3D();
                BasicVector3D vec3 = new BasicVector3D();
                BasicVector3D sss = new BasicVector3D();
                BasicVector3D norm = new BasicVector3D();
                Atom atom1 = (Atom) nonRingNbrs.get(0);
                Atom atom2 = (Atom) nonRingNbrs.get(1);
                Atom atom3 = (Atom) nonRingNbrs.get(2);
                BasicVector3D.sub(vec1, atom1.getCoords3D(),
                    atom.getCoords3D());
                vec1.normalize();
                BasicVector3D.sub(vec2, atom2.getCoords3D(),
                    atom.getCoords3D());
                vec2.normalize();
                BasicVector3D.sub(vec3, atom3.getCoords3D(),
                    atom.getCoords3D());
                BasicVector3D.add(sss, vec1, vec2);
                sss.normalize();
                sss.muling(-1.0f);
                BasicVector3D.cross(norm, sss, vec3);
                angle = BasicVector3D.xyzVectorAngle(sss, vec3);
                matrix.rotAboutAxisByAngle(norm, angle);
                applyRotMatToBond(atom.getParent(), matrix, atom, atom3);
            }
        }
    }

    /**
     * @param nonRingNbrs
     * @param ringNbrs
     * @param targetAngle
     */
    private static void setGeometryHybSP3(Atom atom, List nonRingNbrs,
        List ringNbrs, double targetAngle)
    {
        if ((ringNbrs.size() >= 3) && (nonRingNbrs.size() >= 1))
        {
            double angle;
            BasicMatrix3D matrix = new BasicMatrix3D();
            BasicVector3D vec1 = new BasicVector3D();
            BasicVector3D vec2 = new BasicVector3D();
            BasicVector3D vec3 = new BasicVector3D();
            BasicVector3D vec4 = new BasicVector3D();
            BasicVector3D sss = new BasicVector3D();
            BasicVector3D norm = new BasicVector3D();
            Atom atom1 = (Atom) nonRingNbrs.get(0);
            Atom rAtom1 = (Atom) ringNbrs.get(0);
            Atom rAtom2 = (Atom) ringNbrs.get(1);
            Atom rAtom3 = (Atom) ringNbrs.get(2);
            BasicVector3D.sub(vec1, rAtom1.getCoords3D(), atom.getCoords3D());
            vec1.normalize();
            BasicVector3D.sub(vec2, rAtom2.getCoords3D(), atom.getCoords3D());
            vec2.normalize();
            BasicVector3D.sub(vec3, rAtom3.getCoords3D(), atom.getCoords3D());
            vec3.normalize();
            BasicVector3D.sub(vec4, atom1.getCoords3D(), atom.getCoords3D());
            BasicVector3D.add(sss, vec1, vec2);
            sss.adding(vec3);
            sss.muling(-1.0f);
            sss.normalize();
            BasicVector3D.cross(norm, sss, vec4);
            angle = BasicVector3D.xyzVectorAngle(sss, vec4);
            matrix.rotAboutAxisByAngle(norm, angle);
            applyRotMatToBond(atom.getParent(), matrix, atom, atom1);
        }
        else if ((ringNbrs.size() == 2) && (nonRingNbrs.size() >= 1))
        {
            double angle;
            BasicMatrix3D matrix = new BasicMatrix3D();
            BasicVector3D vec1 = new BasicVector3D();
            BasicVector3D vec2 = new BasicVector3D();
            BasicVector3D vec3 = new BasicVector3D();
            BasicVector3D vec4 = new BasicVector3D();
            BasicVector3D sss = new BasicVector3D();
            BasicVector3D norm = new BasicVector3D();
            Atom atom1 = (Atom) nonRingNbrs.get(0);
            Atom rAtom1 = (Atom) ringNbrs.get(0);
            Atom rAtom2 = (Atom) ringNbrs.get(1);
            BasicVector3D.sub(vec1, rAtom1.getCoords3D(), atom.getCoords3D());
            vec1.normalize();
            BasicVector3D.sub(vec2, rAtom2.getCoords3D(), atom.getCoords3D());
            vec2.normalize();
            BasicVector3D.sub(vec3, atom1.getCoords3D(), atom.getCoords3D());
            BasicVector3D.add(sss, vec1, vec2);
            sss.muling(-1.0f);
            sss.normalize();
            BasicVector3D.cross(norm, vec1, vec2);
            norm.normalize();
            sss.muling(MathHelper.ONE_OVER_SQRT3);
            norm.muling(MathHelper.SQRT_TWO_THIRDS);
            sss.adding(norm);
            sss.normalize();
            BasicVector3D.cross(norm, sss, vec3);
            angle = BasicVector3D.xyzVectorAngle(sss, vec3);
            matrix.rotAboutAxisByAngle(norm, angle);
            applyRotMatToBond(atom.getParent(), matrix, atom, atom1);

            if (nonRingNbrs.size() >= 2)
            {
                Atom atom2 = (Atom) nonRingNbrs.get(1);
                BasicVector3D.sub(vec1, rAtom1.getCoords3D(),
                    atom.getCoords3D());
                vec1.normalize();
                BasicVector3D.sub(vec2, rAtom2.getCoords3D(),
                    atom.getCoords3D());
                vec2.normalize();
                BasicVector3D.sub(vec3, atom1.getCoords3D(),
                    atom.getCoords3D());
                vec3.normalize();
                BasicVector3D.sub(vec4, atom2.getCoords3D(),
                    atom.getCoords3D());
                BasicVector3D.add(sss, vec1, vec2);
                sss.adding(vec3);
                sss.muling(-1.0f);
                sss.normalize();
                BasicVector3D.cross(norm, sss, vec4);
                angle = BasicVector3D.xyzVectorAngle(sss, vec4);
                matrix.rotAboutAxisByAngle(norm, angle);
                applyRotMatToBond(atom.getParent(), matrix, atom, atom2);
            }
        }
        else
        {
            if (nonRingNbrs.size() >= 2)
            {
                double angle;
                BasicMatrix3D matrix = new BasicMatrix3D();
                BasicVector3D vec1 = new BasicVector3D();
                BasicVector3D vec2 = new BasicVector3D();
                BasicVector3D norm = new BasicVector3D();
                Atom atom1 = (Atom) nonRingNbrs.get(0);
                Atom atom2 = (Atom) nonRingNbrs.get(1);
                BasicVector3D.sub(vec1, atom1.getCoords3D(),
                    atom.getCoords3D());
                vec1.normalize();
                BasicVector3D.sub(vec2, atom2.getCoords3D(),
                    atom.getCoords3D());
                vec2.normalize();
                BasicVector3D.cross(norm, vec1, vec2);
                angle = BasicVector3D.xyzVectorAngle(vec1, vec2) - targetAngle;
                matrix.rotAboutAxisByAngle(norm, -angle);
                applyRotMatToBond(atom.getParent(), matrix, atom, atom1);
            }

            if (nonRingNbrs.size() >= 3)
            {
                double angle;
                BasicMatrix3D matrix = new BasicMatrix3D();
                BasicVector3D vec1 = new BasicVector3D();
                BasicVector3D vec2 = new BasicVector3D();
                BasicVector3D vec3 = new BasicVector3D();
                BasicVector3D sss = new BasicVector3D();
                BasicVector3D norm = new BasicVector3D();
                Atom atom1 = (Atom) nonRingNbrs.get(0);
                Atom atom2 = (Atom) nonRingNbrs.get(1);
                Atom atom3 = (Atom) nonRingNbrs.get(2);
                BasicVector3D.sub(vec1, atom1.getCoords3D(),
                    atom.getCoords3D());
                vec1.normalize();
                BasicVector3D.sub(vec2, atom2.getCoords3D(),
                    atom.getCoords3D());
                vec2.normalize();
                BasicVector3D.sub(vec3, atom3.getCoords3D(),
                    atom.getCoords3D());
                BasicVector3D.add(sss, vec1, vec2);
                sss.muling(-1.0f);
                sss.normalize();
                BasicVector3D.cross(norm, vec1, vec2);
                norm.normalize();
                sss.muling(MathHelper.ONE_OVER_SQRT3);
                norm.muling(MathHelper.SQRT_TWO_THIRDS);
                sss.adding(norm);
                sss.normalize();
                BasicVector3D.cross(norm, sss, vec3);
                angle = BasicVector3D.xyzVectorAngle(sss, vec3);
                matrix.rotAboutAxisByAngle(norm, angle);
                applyRotMatToBond(atom.getParent(), matrix, atom, atom3);
            }
        }
    }

    /**
     * @param hyb
     * @return
     */
    private static double setTargetAngle(Atom atom, int hyb)
    {
        double targetAngle = 0.0;

        if (hyb == 3)
        {
            targetAngle = 109.5;
        }
        else if (hyb == 2)
        {
            targetAngle = 120.0;
        }
        else if (hyb == 1)
        {
            targetAngle = 180.0;
        }

        return targetAngle;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
