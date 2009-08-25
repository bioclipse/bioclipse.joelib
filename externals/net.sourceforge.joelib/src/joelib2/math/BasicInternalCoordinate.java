///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicInternalCoordinate.java,v $
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


/**
 * Internal coordinates for three atoms.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:35 $
 */
public class BasicInternalCoordinate implements java.io.Serializable,
    InternalCoordinate
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

    public BasicInternalCoordinate()
    {
        this(null, null, null);
    }

    public BasicInternalCoordinate(Atom atom1, Atom atom2, Atom atom3)
    {
        this.atom1 = atom1;
        this.atom2 = atom2;
        this.atom3 = atom3;
        this.distance = this.angle = this.torsion = 0.0;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

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
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
