///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ConformerMolecule.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.10 $
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

import joelib2.molecule.types.Pose;

import joelib2.util.iterator.ConformerIterator;

import java.util.List;


/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.10 $, $Date: 2005/02/17 16:48:36 $
 */
public interface ConformerMolecule extends Molecule
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Molecule flag: current conformer.
     */
    final static int IS_CURRENT_CONFORMER = (1 << 5);

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds conformer coordinates to this molecule.
     *
     * @param  conformer  The conformer coordinates
     */
    void addConformer(double[] newConformer);

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     */
    void addPose(Pose pose);

    /**
     * Gets an iterator over all conformers.
     *
     * <blockquote><pre>
     * ConformerIterator cit = mol.conformerIterator();
     * double conformer[];
     * while (cit.hasNext())
     * {
     *   conformer = cit.nextConformer();
     *
     * }
     * </pre></blockquote>
     *
     * @return    the conformer iterator
     * @see #atomIterator()
     * @see #bondIterator()
     * @see #getRingIterator()
     * @see #genericDataIterator
     * @see #nativeValueIterator()
     */
    ConformerIterator conformerIterator();

    /**
     * Copies given conformer to this molecule.
     *
     * @param  src    the conformer
     * @param  idx  the index of the conformer
     */
    void copyConformer(double[] src, int idx);

    /**
     * Copies given conformer to this molecule.
     *
     * @param  src    the conformer
     * @param  idx  the index of the conformer
     */
    void copyConformer(float[] src, int idx);

    /**
     * Returns the number of the current pose.
     * @return The number of the current pose.  If no poses are present 0 is returned.
     */
    int currentPoseIndex();

    /**
     * Delete conformer from molecule.
     *
     * @param  idx  The conformer number
     */
    void deleteConformer(int idx);

    double[] getActualPose3D();

    /**
     *  Gets the conformer attribute of the Molecule object
     *
     * @param  index  Description of the Parameter
     * @return    The conformer value
     */
    double[] getConformer(int index);

    /**
     * Returns a {@link java.util.Vector} of all conformer coordinates (<tt>double[]</tt> values).
     *
     * @return    The conformers coordinates
     */
    List getConformers();

    /**
     * Returns the number of conformers of this molecule.
     *
     * @return    The number of conformers of this molecule
     */
    int getConformersSize();

    /**
     * Gets the coordinate array of this molecule.
     *
     * @return    The coordinates array
     */
    double[] getCoords3Darr();

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     */
    Pose getPose(int index);

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     *
     */
    void getPoseCoordinates(int poseI, double[] xyz);

    List getPoses();

    int getPosesSize();

    void setActualPose3D(double[] poseArr);

    /**
     *  Sets the conformer attribute of the Molecule object
     *
     * @param  index  The new conformer value
     */
    void setConformer(int index, double[] conformer);

    /**
     *  Sets the conformers attribute of the Molecule object
     *
     * @param  conformers  The new conformers value (v is of type double[])
     */
    void setConformers(List<double[]> newConfs);

    void setCoords3Darr(double[] c3Darr);

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     *
     */
    void setPose(int poseI);

    /**
     *  Deletes all pose information for the <tt>Molecule</tt>.
     */
    void setPoses(List<Pose> newPoses);

    /**
     *  Sets the conformer attribute of the Molecule object
     *
     * @param  index  The new conformer value
     */
    void useConformer(int index);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
