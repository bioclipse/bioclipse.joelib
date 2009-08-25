///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RotorHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/01/26 12:07:23 $
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
package joelib2.rotor;

import joelib2.math.BasicMatrix3D;
import joelib2.math.BasicVector3D;
import joelib2.math.Matrix3D;
import joelib2.math.Vector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.ConformerAtom;
import joelib2.molecule.ConformerMolecule;
import joelib2.molecule.Molecule;

import java.util.List;
import java.util.Vector;


/**
 * Atom representation.
 */
public class RotorHelper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////

    public static synchronized void align(Molecule mol, Atom atom1, Atom atom2,
        Vector3D start, Vector3D end)
    {
        List<Atom> children = new Vector<Atom>();

        // of type int[1]
        //find which atoms to rotate
        mol.findChildren(children, atom1.getIndex(), atom2.getIndex());
        children.add(atom2);

        //find the rotation vector and angle
        Vector3D vector1 = new BasicVector3D();
        Vector3D vector2 = new BasicVector3D();
        Vector3D vector3 = new BasicVector3D();
        vector1 = end.sub(start);
        vector2 = atom2.getCoords3D().sub(atom1.getCoords3D());
        BasicVector3D.cross(vector3, vector1, vector2);

        double angle = BasicVector3D.xyzVectorAngle(vector1, vector2);

        //find the rotation matrix
        Matrix3D rotMatrix = new BasicMatrix3D();
        rotMatrix.rotAboutAxisByAngle(vector3, angle);

        //rotate atoms
        Vector3D vector;
        Atom atom;

        for (int i = 0; i < children.size(); i++)
        {
            atom = children.get(i);
            vector = atom.getCoords3D();
            vector.subing(atom1.getCoords3D());
            vector.muling(rotMatrix);

            //rotate the point
            vector.adding(start);

            //translate the vector
            atom.setCoords3D(vector);
        }

        //set a1 = p1
        atom1.setCoords3D(start);
    }

    /**
     * Sets the torsion attribute of the <tt>Molecule</tt> object
     *
     */
    public static void setTorsion(ConformerMolecule mol, ConformerAtom atom1,
        ConformerAtom atom2, ConformerAtom atom3, ConformerAtom atom4,
        double angle)
    {
        int[] tor = new int[4];
        List atoms = new Vector();
        int[] itmp;
        tor[0] = atom1.getCoordinateIdx();
        tor[1] = atom2.getCoordinateIdx();
        tor[2] = atom3.getCoordinateIdx();
        tor[3] = atom4.getCoordinateIdx();

        mol.findChildren(atoms, atom2.getIndex(), atom3.getIndex());

        int index;

        for (index = 0; index < atoms.size(); index++)
        {
            itmp = (int[]) atoms.get(index);
            itmp[0] = (itmp[0] - 1) * 3;
        }

        double v1x, v1y, v1z;
        double v2x, v2y, v2z;
        double v3x, v3y, v3z;
        double c1x, c1y, c1z;
        double c2x, c2y, c2z;
        double c3x, c3y, c3z;
        double c1mag;
        double c2mag;
        double radang;
        double costheta;
        double[] matrixArray = new double[9];
        double coord3Dx, coord3Dy, coord3Dz;
        double mag;
        double rotang;
        double sin;
        double cos;
        double temp;
        double t3Dx, t3Dy, t3Dz;

        double[] coords3Darr = mol.getCoords3Darr();

        //calculate the torsion angle
        v1x = (double) (coords3Darr[tor[0]] - coords3Darr[tor[1]]);
        v2x = (double) (coords3Darr[tor[1]] - coords3Darr[tor[2]]);
        v1y = (double) (coords3Darr[tor[0] + 1] - coords3Darr[tor[1] + 1]);
        v2y = (double) (coords3Darr[tor[1] + 1] - coords3Darr[tor[2] + 1]);
        v1z = (double) (coords3Darr[tor[0] + 2] - coords3Darr[tor[1] + 2]);
        v2z = (double) (coords3Darr[tor[1] + 2] - coords3Darr[tor[2] + 2]);
        v3x = (double) (coords3Darr[tor[2]] - coords3Darr[tor[3]]);
        v3y = (double) (coords3Darr[tor[2] + 1] - coords3Darr[tor[3] + 1]);
        v3z = (double) (coords3Darr[tor[2] + 2] - coords3Darr[tor[3] + 2]);

        c1x = (v1y * v2z) - (v1z * v2y);
        c2x = (v2y * v3z) - (v2z * v3y);
        c1y = (-v1x * v2z) + (v1z * v2x);
        c2y = (-v2x * v3z) + (v2z * v3x);
        c1z = (v1x * v2y) - (v1y * v2x);
        c2z = (v2x * v3y) - (v2y * v3x);
        c3x = (c1y * c2z) - (c1z * c2y);
        c3y = (-c1x * c2z) + (c1z * c2x);
        c3z = (c1x * c2y) - (c1y * c2x);

        c1mag = (c1x * c1x) + (c1y * c1y) + (c1z * c1z);
        c2mag = (c2x * c2x) + (c2y * c2y) + (c2z * c2z);

        if ((c1mag * c2mag) < 0.01f)
        {
            costheta = 1.0f;
        }

        //avoid div by zero error
        else
        {
            costheta = ((c1x * c2x) + (c1y * c2y) + (c1z * c2z)) /
                (double) (Math.sqrt(c1mag * c2mag));
        }

        if (costheta < -0.999999f)
        {
            costheta = -0.999999f;
        }

        if (costheta > 0.999999f)
        {
            costheta = 0.999999f;
        }

        if (((v2x * c3x) + (v2y * c3y) + (v2z * c3z)) > 0.0f)
        {
            radang = (double) -Math.acos(costheta);
        }
        else
        {
            radang = (double) Math.acos(costheta);
        }

        // now we have the torsion angle (radang) - set up the rot matrix
        //find the difference between current and requested
        rotang = angle - radang;
        sin = (double) Math.sin(rotang);
        cos = (double) Math.cos(rotang);
        temp = 1 - cos;

        //normalize the rotation vector
        mag = (double) Math.sqrt((v2x * v2x) + (v2y * v2y) + (v2z * v2z));
        coord3Dx = v2x / mag;
        coord3Dy = v2y / mag;
        coord3Dz = v2z / mag;

        //set up the rotation matrix
        matrixArray[0] = (temp * coord3Dx * coord3Dx) + cos;
        matrixArray[1] = (temp * coord3Dx * coord3Dy) + (sin * coord3Dz);
        matrixArray[2] = (temp * coord3Dx * coord3Dz) - (sin * coord3Dy);
        matrixArray[3] = (temp * coord3Dx * coord3Dy) - (sin * coord3Dz);
        matrixArray[4] = (temp * coord3Dy * coord3Dy) + cos;
        matrixArray[5] = (temp * coord3Dy * coord3Dz) + (sin * coord3Dx);
        matrixArray[6] = (temp * coord3Dx * coord3Dz) + (sin * coord3Dy);
        matrixArray[7] = (temp * coord3Dy * coord3Dz) - (sin * coord3Dx);
        matrixArray[8] = (temp * coord3Dz * coord3Dz) + cos;

        //
        //now the matrix is set - time to rotate the atoms
        //
        t3Dx = coords3Darr[tor[1]];
        t3Dy = coords3Darr[tor[1] + 1];
        t3Dz = coords3Darr[tor[1] + 2];

        for (int i = 0; i < atoms.size(); i++)
        {
            index = ((int[]) atoms.get(i))[0];
            coords3Darr[index] -= t3Dx;
            coords3Darr[index + 1] -= t3Dy;
            coords3Darr[index + 2] -= t3Dz;
            coord3Dx = (coords3Darr[index] * matrixArray[0]) +
                (coords3Darr[index + 1] * matrixArray[1]) +
                (coords3Darr[index + 2] * matrixArray[2]);
            coord3Dy = (coords3Darr[index] * matrixArray[3]) +
                (coords3Darr[index + 1] * matrixArray[4]) +
                (coords3Darr[index + 2] * matrixArray[5]);
            coord3Dz = (coords3Darr[index] * matrixArray[6]) +
                (coords3Darr[index + 1] * matrixArray[7]) +
                (coords3Darr[index + 2] * matrixArray[8]);
            coords3Darr[index] = coord3Dx;
            coords3Darr[index + 1] = coord3Dy;
            coords3Darr[index + 2] = coord3Dz;
            coords3Darr[index] += t3Dx;
            coords3Darr[index + 1] += t3Dy;
            coords3Darr[index + 2] += t3Dz;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
