/*
 * Created on Jan 15, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.math;

import joelib2.molecule.Atom;


/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface InternalCoordinate
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the angle.
     */
    double getAngle();

    /**
     * @return Returns the atom1.
     */
    Atom getAtom1();

    /**
     * @return Returns the atom2.
     */
    Atom getAtom2();

    /**
     * @return Returns the atom3.
     */
    Atom getAtom3();

    /**
     * @return Returns the distance.
     */
    double getDistance();

    /**
     * @return Returns the torsion.
     */
    double getTorsion();

    /**
     * @param angle The angle to set.
     */
    void setAngle(double angle);

    /**
     * @param atom1 The atom1 to set.
     */
    void setAtom1(Atom atom1);

    /**
     * @param atom2 The atom2 to set.
     */
    void setAtom2(Atom atom2);

    /**
     * @param atom3 The atom3 to set.
     */
    void setAtom3(Atom atom3);

    /**
     * @param distance The distance to set.
     */
    void setDistance(double distance);

    /**
     * @param torsion The torsion to set.
     */
    void setTorsion(double torsion);
}
