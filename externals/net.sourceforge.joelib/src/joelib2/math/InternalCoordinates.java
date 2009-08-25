///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: InternalCoordinates.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.math;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;

import java.util.List;


/**
 * Internal coordinates for three atoms.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:35 $
 */
public class InternalCoordinates implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    public double angle;
    public Atom atom1;
    public Atom atom2;
    public Atom atom3;
    public double distance;
    public double torsion;

    //~ Constructors ///////////////////////////////////////////////////////////

    public InternalCoordinates()
    {
        this(null, null, null);
    }

    public InternalCoordinates(Atom a, Atom b, Atom c)
    {
        atom1 = a;
        atom2 = b;
        atom3 = c;
        distance = angle = torsion = 0.0;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    *
    * @param internalCoords of type <tt>JOEInternalCoord</tt>
    */
    public static void internalToCartesian(
        List<InternalCoordinates> internalCoords, Molecule mol)
    {
        Vector3D norm1 = new BasicVector3D();
        Vector3D norm2 = new BasicVector3D();
        Vector3D vector1 = new BasicVector3D();
        Vector3D vector2 = new BasicVector3D();
        Vector3D vector3 = new BasicVector3D();
        Atom atom = null;

        int index;
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            index = atom.getIndex() - 1;

            InternalCoordinates intCoord = internalCoords.get(index);

            if (index == 0)
            {
                atom.setCoords3D(0.0f, 0.0f, 0.0f);

                continue;
            }
            else if (index == 1)
            {
                vector1.setX3D(-intCoord.distance);
                atom.setCoords3D(vector1);

                continue;
            }
            else if (index == 2)
            {
                vector1.setX3D(-(intCoord.distance * Math.cos(intCoord.angle)));
                vector1.setZ3D(-(intCoord.distance * Math.sin(intCoord.angle)));
                atom.setCoords3D(vector1);

                continue;
            }

            BasicVector3D.sub(vector1, intCoord.atom1.getCoords3D(),
                intCoord.atom2.getCoords3D());
            BasicVector3D.sub(vector2, intCoord.atom1.getCoords3D(),
                intCoord.atom3.getCoords3D());
            BasicVector3D.cross(norm1, vector1, vector2);
            BasicVector3D.cross(norm2, vector1, norm1);
            norm1.normalize();
            norm2.normalize();

            norm1.muling(-Math.sin(intCoord.torsion));
            norm2.muling(Math.cos(intCoord.torsion));
            BasicVector3D.add(vector3, norm1, norm2);
            vector3.normalize();
            vector3.muling(intCoord.distance * Math.sin(intCoord.angle));
            vector1.normalize();
            vector1.muling(intCoord.distance * Math.cos(intCoord.angle));
            BasicVector3D.add(vector2, intCoord.atom1.getCoords3D(), vector3);
            vector2.subing(vector1);

            atom.setCoords3D(vector2);
        }

        // Delete dummy atoms
        ait.reset();

        while (ait.hasNext())
        {
            if (atom.getAtomicNumber() == 0)
            {
                mol.deleteAtom(atom);
            }
        }
    }

    /**
     * @return Returns the angle.
     */
    public double getAngle()
    {
        return angle;
    }

    /**
     * @return Returns the atom1.
     */
    public Atom getAtom1()
    {
        return atom1;
    }

    /**
     * @return Returns the atom2.
     */
    public Atom getAtom2()
    {
        return atom2;
    }

    /**
     * @return Returns the atom3.
     */
    public Atom getAtom3()
    {
        return atom3;
    }

    /**
     * @return Returns the distance.
     */
    public double getDistance()
    {
        return distance;
    }

    /**
     * @return Returns the torsion.
     */
    public double getTorsion()
    {
        return torsion;
    }

    /**
     * @param angle The angle to set.
     */
    public void setAngle(double angle)
    {
        this.angle = angle;
    }

    /**
     * @param atom1 The atom1 to set.
     */
    public void setAtom1(Atom atom1)
    {
        this.atom1 = atom1;
    }

    /**
     * @param atom2 The atom2 to set.
     */
    public void setAtom2(Atom atom2)
    {
        this.atom2 = atom2;
    }

    /**
     * @param atom3 The atom3 to set.
     */
    public void setAtom3(Atom atom3)
    {
        this.atom3 = atom3;
    }

    /**
     * @param distance The distance to set.
     */
    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    /**
     * @param torsion The torsion to set.
     */
    public void setTorsion(double torsion)
    {
        this.torsion = torsion;
    }

    /**
     *
     * @param vic of type <tt>JOEInternalCoord</tt>
     */
    void cartesianToInternal(List vic, Molecule mol)
    {
        double distance;
        double sum;
        Atom atom;
        Atom nbr;
        Atom ref;

        //set reference atoms
        AtomIterator ait = mol.atomIterator();
        AtomIterator aitNbr = mol.atomIterator();
        InternalCoordinates intCoord;
        InternalCoordinates intCoordNbr;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            intCoord = (InternalCoordinates) vic.get(atom.getIndex());

            if (atom.getIndex() == 1)
            {
                continue;
            }
            else if (atom.getIndex() == 2)
            {
                intCoord.atom1 = mol.getAtom(1);

                continue;
            }
            else if (atom.getIndex() == 3)
            {
                intCoord.atom1 = mol.getAtom(2);
                intCoord.atom2 = mol.getAtom(1);

                continue;
            }

            sum = 1.0E10;
            ref = mol.getAtom(1);
            aitNbr.reset();

            while (aitNbr.hasNext() && (ait.getIndex() != aitNbr.getIndex()))
            {
                nbr = aitNbr.nextAtom();
                intCoordNbr = (InternalCoordinates) vic.get(nbr.getIndex());

                if (nbr.getIndex() < 3)
                {
                    continue;
                }

                Vector3D tmp = BasicVector3D.sub(atom.getCoords3D(),
                        nbr.getCoords3D());
                distance = tmp.length_2();

                if ((distance < sum) && (intCoordNbr.atom1 != nbr) &&
                        (intCoordNbr.atom2 != nbr))
                {
                    sum = distance;
                    ref = nbr;
                }
            }

            intCoord.atom1 = ref;
            intCoord.atom2 = ((InternalCoordinates) vic.get(ref.getIndex())).atom1;
            intCoord.atom3 = ((InternalCoordinates) vic.get(ref.getIndex())).atom2;
        }

        //fill in geometries
        int k;
        BasicVector3D v1 = new BasicVector3D();
        BasicVector3D v2 = new BasicVector3D();
        Atom a;
        Atom b;
        Atom c = null;
        InternalCoordinates icK;

        for (k = 2; k <= mol.getAtomsSize(); k++)
        {
            atom = mol.getAtom(k);
            icK = (InternalCoordinates) vic.get(k);
            a = icK.atom1;
            b = icK.atom2;
            c = icK.atom3;

            if (k == 2)
            {
                Vector3D tmp = BasicVector3D.sub(atom.getCoords3D(),
                        a.getCoords3D());
                icK.distance = tmp.length();

                continue;
            }

            BasicVector3D.sub(v1, atom.getCoords3D(), a.getCoords3D());
            BasicVector3D.sub(v2, b.getCoords3D(), a.getCoords3D());
            icK.distance = v1.length();
            icK.angle = BasicVector3D.xyzVectorAngle(v1, v2);

            if (k == 3)
            {
                continue;
            }

            icK.torsion = BasicVector3D.calcTorsionAngle(atom.getCoords3D(),
                    a.getCoords3D(), b.getCoords3D(), c.getCoords3D());
        }

        //check for linear geometries and try to correct if possible
        boolean done;
        double ang;

        for (k = 2; k <= mol.getAtomsSize(); k++)
        {
            icK = (InternalCoordinates) vic.get(k);
            ang = Math.abs(icK.angle);

            if ((ang > 5.0f) && (ang < 175.0f))
            {
                continue;
            }

            atom = mol.getAtom(k);
            done = false;

            ait.reset();

            while (ait.hasNext())
            {
                a = ait.nextAtom();

                if ((a.getIndex() >= k) || done)
                {
                    break;
                }

                AtomIterator ait2 = mol.atomIterator();

                while (ait2.hasNext())
                {
                    b = ait2.nextAtom();

                    if ((b.getIndex() >= a.getIndex()) || done)
                    {
                        break;
                    }

                    BasicVector3D.sub(v1, atom.getCoords3D(), a.getCoords3D());
                    BasicVector3D.sub(v2, b.getCoords3D(), a.getCoords3D());
                    ang = Math.abs(BasicVector3D.xyzVectorAngle(v1, v2));

                    if ((ang < 5.0f) || (ang > 175.0f))
                    {
                        continue;
                    }

                    AtomIterator ait3 = mol.atomIterator();

                    while (ait3.hasNext())
                    {
                        c = ait3.nextAtom();

                        if (c.getIndex() >= atom.getIndex())
                        {
                            break;
                        }

                        if ((c != atom) && (c != a) && (c != b))
                        {
                            break;
                        }
                    }

                    if (c == null)
                    {
                        continue;
                    }

                    icK.atom1 = a;
                    icK.atom2 = b;
                    icK.atom3 = c;
                    icK.distance = v1.length();
                    icK.angle = BasicVector3D.xyzVectorAngle(v1, v2);
                    icK.torsion = BasicVector3D.calcTorsionAngle(atom
                            .getCoords3D(), a.getCoords3D(), b.getCoords3D(),
                            c.getCoords3D());
                    done = true;
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
