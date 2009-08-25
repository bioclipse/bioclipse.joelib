///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.14 $
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

import joelib2.feature.types.atomlabel.AtomInRing;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.bondlabel.BondInRing;

import joelib2.math.BasicVector3D;

import joelib2.molecule.fragmentation.ContiguousFragments;

import joelib2.ring.RingFinderSSSR;

import joelib2.rotor.RotorHelper;

import joelib2.sort.QuickInsertSort;

import joelib2.util.BasicBitVector;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom tree.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.14 $, $Date: 2005/02/17 16:48:36 $
 */
public class MoleculeHelper
{
    // /////////////////////////////////////////////

    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(MoleculeHelper.class
            .getName());

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Centers molecule.
     *
     * @see #center(int)
     */
    public static synchronized void center(ConformerMolecule mol)
    {
        int atomI;
        int size;
        double[] conf;
        double coord3Dx;
        double coord3Dy;
        double coord3Dz;
        double fsize;

        size = mol.getAtomsSize();
        fsize = -1.0f / (double) mol.getAtomsSize();

        for (int confI = 0; confI < mol.getConformers().size(); confI++)
        {
            conf = (double[]) mol.getConformers().get(confI);
            coord3Dx = coord3Dy = coord3Dz = 0.0f;

            for (atomI = 0; atomI < size; atomI++)
            {
                coord3Dx += conf[atomI * 3];
                coord3Dy += conf[(atomI * 3) + 1];
                coord3Dz += conf[(atomI * 3) + 2];
            }

            coord3Dx *= fsize;
            coord3Dy *= fsize;
            coord3Dz *= fsize;

            for (atomI = 0; atomI < size; atomI++)
            {
                conf[atomI * 3] += coord3Dx;
                conf[(atomI * 3) + 1] += coord3Dy;
                conf[(atomI * 3) + 2] += coord3Dz;
            }
        }
    }

    /**
     * Centers conformer.
     *
     * @param nconf
     *            number of the conformer
     * @return the center of the conformer
     * @see #center()
     */
    public static synchronized BasicVector3D center(ConformerMolecule mol,
        int nconf)
    {
        mol.useConformer(nconf);

        Atom atom = null;
        AtomIterator ait = mol.atomIterator();
        double coord3Dx = 0.0f;
        double coord3Dy = 0.0f;
        double coord3Dz = 0.0f;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            coord3Dx += atom.get3Dx();
            coord3Dy += atom.get3Dy();
            coord3Dz += atom.get3Dz();
        }

        double natoms = (double) mol.getAtomsSize();
        coord3Dx /= natoms;
        coord3Dy /= natoms;
        coord3Dz /= natoms;

        BasicVector3D vtmp = new BasicVector3D();
        BasicVector3D vec = new BasicVector3D(coord3Dx, coord3Dy, coord3Dz);

        ait.reset();

        while (ait.hasNext())
        {
            BasicVector3D.sub(vtmp, atom.getCoords3D(), vec);
            atom.setCoords3D(vtmp);
        }

        return vec;
    }

    // ////////////////////////////////////////////////////////////////

    public static synchronized void correctFormalCharge(Molecule mol)
    {
        if (mol.isAssignFormalCharge())
        {
            for (int atomIdx = 1; atomIdx <= mol.getAtomsSize(); atomIdx++)
            {
                Atom atom = mol.getAtom(atomIdx);
                AtomHelper.correctFormalCharge(atom);
            }
        }
    }

    /**
     * calculates the graph theoretical distance for every atom and puts it into
     * gtd
     *
     * @param gtd
     *            Description of the Parameter
     * @return The gTDVector value
     */
    public static boolean getGTDVector(Molecule mol, int[] gtd)
    {
        //gtd.clear();
        //gtd.setSize(numAtoms());
        if (gtd.length != mol.getAtomsSize())
        {
            logger.error("gtd must have length of #atoms: " +
                mol.getAtomsSize());
        }

        int gtdcount;

        int natom;
        BasicBitVector used = new BasicBitVector();
        BasicBitVector curr = new BasicBitVector();
        BasicBitVector next = new BasicBitVector();
        Atom atom;
        Atom atom1;
        Bond bond;
        AtomIterator ait = mol.atomIterator();
        next.clear();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            gtdcount = 0;
            used.clear();
            curr.clear();
            used.setBitOn(atom.getIndex());
            curr.setBitOn(atom.getIndex());

            while (!curr.isEmpty())
            {
                next.clear();

                //          for(natom=curr.nextSetBit(0); natom>=0;
                // natom=curr.nextSetBit(natom+1))
                for (natom = curr.nextBit(-1); natom != curr.endBit();
                        natom = curr.nextBit(natom))
                {
                    atom1 = mol.getAtom(natom);

                    BondIterator bit = atom1.bondIterator();

                    while (bit.hasNext())
                    {
                        bond = bit.nextBond();

                        if (!used.bitIsOn(bond.getNeighborIndex(atom1)) &&
                                !curr.bitIsOn(bond.getNeighborIndex(atom1)))
                        {
                            if (!AtomIsHydrogen.isHydrogen(
                                        bond.getNeighbor(atom1)))
                            {
                                next.setBitOn(bond.getNeighborIndex(atom1));
                            }
                        }
                    }
                }

                used.set(next);
                curr.set(next);
                gtdcount++;
            }

            gtd[atom.getIndex() - 1] = gtdcount;
        }

        return true;
    }

    /**
     * Gets the torsion attribute of the <tt>Molecule</tt> object
     *
     * @param atom1idx
     *            Description of the Parameter
     * @param atom2idx
     *            Description of the Parameter
     * @param atom3idx
     *            Description of the Parameter
     * @param atom4idx
     *            Description of the Parameter
     * @return The torsion value
     */
    public static double getTorsion(ConformerMolecule mol, int atom1idx,
        int atom2idx, int atom3idx, int atom4idx)
    {
        return (BasicVector3D.calcTorsionAngle(
                    ((Atom) mol.getAtom(atom1idx - 1)).getCoords3D(),
                    ((Atom) mol.getAtom(atom2idx - 1)).getCoords3D(),
                    ((Atom) mol.getAtom(atom3idx - 1)).getCoords3D(),
                    ((Atom) mol.getAtom(atom4idx - 1)).getCoords3D()));
    }

    /**
     * Gets the torsion attribute of the <tt>Molecule</tt> object
     *
     * @param atom1
     *            Description of the Parameter
     * @param atom2
     *            Description of the Parameter
     * @param atom3
     *            Description of the Parameter
     * @param atom4
     *            Description of the Parameter
     * @return The torsion value
     */
    public static double getTorsion(Molecule mol, Atom atom1, Atom atom2,
        Atom atom3, Atom atom4)
    {
        return (BasicVector3D.calcTorsionAngle(atom1.getCoords3D(),
                    atom2.getCoords3D(), atom3.getCoords3D(),
                    atom4.getCoords3D()));
    }

    /**
     * @param rotArray
     *            of size 9.
     */
    public static void rotate(ConformerMolecule mol, final double[] rotArray)
    {
        for (int i = 0; i < mol.getConformersSize(); i++)
        {
            rotate(mol, rotArray, i);
        }
    }

    /**
     * @param rotMatrix
     *            Description of the Parameter
     */
    public static void rotate(ConformerMolecule mol, final double[][] rotMatrix)
    {
        int column;
        int row;
        int arrIndex;
        double[] rotArray = new double[9];

        for (arrIndex = 0, column = 0; column < 3; column++)
        {
            for (row = 0; row < 3; row++)
            {
                rotArray[arrIndex++] = rotMatrix[column][row];
            }
        }

        for (column = 0; column < mol.getConformersSize(); column++)
        {
            rotate(mol, rotArray, column);
        }
    }

    /**
     * @param rotArray
     *            of size 9.
     * @param nconf
     *            Description of the Parameter
     */
    public static void rotate(ConformerMolecule mol, final double[] rotArray,
        int nconf)
    {
        int atomI;
        int size;
        double coord3Dx;
        double coord3Dy;
        double coord3Dz;
        double[] conf = (nconf == ConformerMolecule.IS_CURRENT_CONFORMER)
            ? mol.getCoords3Darr() : mol.getConformer(nconf);

        size = mol.getAtomsSize();

        for (atomI = 0; atomI < size; atomI++)
        {
            coord3Dx = conf[atomI * 3];
            coord3Dy = conf[(atomI * 3) + 1];
            coord3Dz = conf[(atomI * 3) + 2];
            conf[atomI * 3] = (rotArray[0] * coord3Dx) +
                (rotArray[1] * coord3Dy) + (rotArray[2] * coord3Dz);
            conf[(atomI * 3) + 1] = (rotArray[3] * coord3Dx) +
                (rotArray[4] * coord3Dy) + (rotArray[5] * coord3Dz);
            conf[(atomI * 3) + 2] = (rotArray[6] * coord3Dx) +
                (rotArray[7] * coord3Dy) + (rotArray[8] * coord3Dz);
        }
    }

    /**
     * Sets the torsion attribute of the <tt>Molecule</tt> object
     *
     * @param atom1
     *            The new torsion value
     * @param atom2
     *            The new torsion value
     * @param atom3
     *            The new torsion value
     * @param atom4
     *            The new torsion value
     * @param angle
     *            The new torsion value
     */
    public static void setTorsion(ConformerMolecule mol, ConformerAtom atom1,
        ConformerAtom atom2, ConformerAtom atom3, ConformerAtom atom4,
        double angle)
    {
        RotorHelper.setTorsion(mol, atom1, atom2, atom3, atom4, angle);
    }

    /**
     * Description of the Method
     */
    public static void sortBonds(Molecule mol)
    {
        QuickInsertSort sorting = new QuickInsertSort();
        BondComparator bondComp = new BondComparator();
        sorting.sort(mol.getBonds(), bondComp);
    }

    /**
     * Deletes all atoms except for the largest contiguous fragment.
     *
     * @return <tt>true</tt> if all smaller contiguous fragments were deleted
     * @see #contiguousFragments(Vector)
     */
    public static boolean stripSalts(Molecule mol)
    {
        List<int[]> fragments = new Vector<int[]>();
        int[] max;
        ContiguousFragments.contiguousFragments(mol, fragments);

        boolean saltStripped = false;

        if ((fragments.size() == 0) || (fragments.size() == 1))
        {
            saltStripped = false;
        }
        else
        {
            max = (int[]) fragments.get(0);

            for (int fragmentIdx = 0; fragmentIdx < fragments.size();
                    fragmentIdx++)
            {
                if (max.length < ((int[]) fragments.get(fragmentIdx)).length)
                {
                    max = (int[]) fragments.get(fragmentIdx);
                }
            }

            List<Atom> delatoms = new Vector<Atom>();

            for (int fragmentIdx = 0; fragmentIdx < fragments.size();
                    fragmentIdx++)
            {
                if (fragments.get(fragmentIdx) != max)
                {
                    for (int j = 0;
                            j < ((int[]) fragments.get(fragmentIdx)).length;
                            j++)
                    {
                        delatoms.add(mol.getAtom(
                                ((int[]) fragments.get(fragmentIdx))[j]));

                        //System.out.println("remove " + i + ": atom " + j);
                    }
                }
            }

            if (delatoms.size() != 0)
            {
                // reset ring detection and SSSR
                mol.deleteData(AtomInRing.getName());
                mol.deleteData(BondInRing.getName());
                mol.deleteData(RingFinderSSSR.getName());
                mol.beginModify();

                for (int k = 0; k < delatoms.size(); k++)
                {
                    //System.out.println("strip: delete atom " + ((Atom)
                    // delatoms.get(k)).getIdx()+" atom "+delatoms.get(k));
                    mol.deleteAtom((Atom) delatoms.get(k));
                }

                mol.endModify();
            }

            saltStripped = true;
        }

        return saltStripped;
    }

    /**
     * Description of the Method
     */
    public static void toInertialFrame(ConformerMolecule mol)
    {
        double[] mArray = new double[9];

        for (int i = 0; i < mol.getConformersSize(); i++)
        {
            toInertialFrame(mol, i, mArray);
        }
    }

    /**
     * Description of the Method
     *
     * @param conf
     *            Description of the Parameter
     * @param rmat
     *            Description of the Parameter
     */
    public static void toInertialFrame(ConformerMolecule mol, int conf,
        double[] rmat)
    {
        int atomI;
        int count = 0;
        double coord3Dx;
        double coord3Dy;
        double coord3Dz;
        double[] center = new double[3];
        double[][] mat = new double[3][3];

        mol.useConformer(conf);

        Atom atom = null;
        AtomIterator ait = mol.atomIterator();

        //find center of mass
        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (!AtomIsHydrogen.isHydrogen(atom))
            {
                center[0] += atom.get3Dx();
                center[1] += atom.get3Dy();
                center[2] += atom.get3Dz();
                count++;
            }
        }

        center[0] /= (double) count;
        center[1] /= (double) count;
        center[2] /= (double) count;

        //calculate inertial tensor
        ait.reset();

        while (ait.hasNext())
        {
            if (!AtomIsHydrogen.isHydrogen(atom))
            {
                coord3Dx = atom.get3Dx() - center[0];
                coord3Dy = atom.get3Dy() - center[1];
                coord3Dz = atom.get3Dz() - center[2];

                mat[0][0] += ((coord3Dy * coord3Dy) + (coord3Dz * coord3Dz));
                mat[0][1] -= (coord3Dx * coord3Dy);
                mat[0][2] -= (coord3Dx * coord3Dz);
                mat[1][0] -= (coord3Dx * coord3Dy);
                mat[1][1] += ((coord3Dx * coord3Dx) + (coord3Dz * coord3Dz));
                mat[1][2] -= (coord3Dy * coord3Dz);
                mat[2][0] -= (coord3Dx * coord3Dz);
                mat[2][1] -= (coord3Dy * coord3Dz);
                mat[2][2] += ((coord3Dx * coord3Dx) + (coord3Dy * coord3Dy));
            }
        }

        // find rotation matrix for moment of inertia
        //double v[3][3];
        //jacobi3x3(m,v);
        //XYZVector v1,v2,v3,r1,r2;
        //r1.Set(v[0][0],v[1][0],v[2][0]);
        //r2.Set(v[0][1],v[1][1],v[2][1]);
        //v3 = cross(r1,r2); v3 = v3.normalize();
        //v2 = cross(v3,r1); v2 = v2.normalize();
        //v1 = cross(v2,v3); v1 = v1.normalize();
        //double rmat[9];
        //rmat[0] = v1.x(); rmat[1] = v1.y(); rmat[2] = v1.z();
        //rmat[3] = v2.x(); rmat[4] = v2.y(); rmat[5] = v2.z();
        //rmat[6] = v3.x(); rmat[7] = v3.y(); rmat[8] = v3.z();
        // rotate all coordinates
        double[] internalized = mol.getConformer(conf);

        for (atomI = 0; atomI < mol.getAtomsSize(); atomI++)
        {
            coord3Dx = internalized[atomI * 3] - center[0];
            coord3Dy = internalized[(atomI * 3) + 1] - center[1];
            coord3Dz = internalized[(atomI * 3) + 2] - center[2];
            internalized[atomI * 3] = (coord3Dx * rmat[0]) +
                (coord3Dy * rmat[1]) + (coord3Dz * rmat[2]);
            internalized[(atomI * 3) + 1] = (coord3Dx * rmat[3]) +
                (coord3Dy * rmat[4]) + (coord3Dz * rmat[5]);
            internalized[(atomI * 3) + 2] = (coord3Dx * rmat[6]) +
                (coord3Dy * rmat[7]) + (coord3Dz * rmat[8]);
        }
    }

    /**
     * Description of the Method
     *
     * @param translate
     *            Description of the Parameter
     */
    public static void translate(ConformerMolecule mol,
        final BasicVector3D translate)
    {
        for (int i = 0; i < mol.getConformersSize(); i++)
        {
            translate(mol, translate, i);
        }
    }

    /**
     * Description of the Method
     *
     * @param translation
     *            Description of the Parameter
     * @param nconf
     *            Description of the Parameter
     */
    public static void translate(ConformerMolecule mol,
        final BasicVector3D translation, int nconf)
    {
        int atomI;
        int size;
        double coord3Dx;
        double coord3Dy;
        double coord3Dz;
        double[] conf = (nconf == ConformerMolecule.IS_CURRENT_CONFORMER)
            ? mol.getCoords3Darr() : mol.getConformer(nconf);

        coord3Dx = translation.getX3D();
        coord3Dy = translation.getY3D();
        coord3Dz = translation.getZ3D();
        size = mol.getAtomsSize();

        for (atomI = 0; atomI < size; atomI++)
        {
            conf[atomI * 3] += coord3Dx;
            conf[(atomI * 3) + 1] += coord3Dy;
            conf[(atomI * 3) + 2] += coord3Dz;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
