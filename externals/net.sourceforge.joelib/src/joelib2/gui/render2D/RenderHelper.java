///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: RenderHelper.java,v $
//Purpose:  Renderer for a 2D layout.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.10 $
//                      $Date: 2005/02/17 16:48:32 $
//                      $Author: wegner $
//Original Author: steinbeck, gzelter, egonw
//Original Version: Copyright (C) 1997-2003
//                  The Chemistry Development Kit (CDK) project
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation; either version 2.1
// of the License, or (at your option) any later version.
// All we ask is that proper credit is given for our work, which includes
// - but is not limited to - adding the above copyright notice to the beginning
// of your source code files, and to any copyright notice that you may distribute
// with programs based on this work.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.gui.render2D;

import joelib2.feature.types.atomlabel.AtomMass;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;

import joelib2.util.iterator.NbrAtomIterator;

import java.awt.Dimension;

import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.apache.log4j.Category;


/**
 * A set of static utility classes for geometric calculations and operations.
 * This class is extensively used, for example, to render and edit molecule.
 *
 * @.author     steinbeck
 * @.author     egonw
 * @.author     wegnerj
 * @.license    LGPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:32 $
 */
public class RenderHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render2D.RenderHelper");

    //~ Methods ////////////////////////////////////////////////////////////////

    /** Determines the normalized vector orthogonal on the vector p1->p2.
     *
     */
    public static Vector2d calculatePerpendicularUnitVector(Point2d p1,
        Point2d p2)
    {
        Vector2d v = new Vector2d();
        v.sub(p2, p1);
        v.normalize();

        // Return the perpendicular vector
        return new Vector2d(-1.0 * v.y, v.x);
    }

    /**
     * Centers the molecule in the given area
     *
     * @param atomCon  molecule to be centered
     * @param areaDim  dimension in which the molecule is to be centered
     */
    public static void center(RenderingAtoms atomCon, Dimension areaDim)
    {
        Dimension molDim = get2DDimension(atomCon);
        int transX = (int) ((areaDim.width - molDim.width) / 2);
        int transY = (int) ((areaDim.height - molDim.height) / 2);
        translateAllPositive(atomCon);
        translate2D(atomCon, new Vector2d(transX, transY));
    }

    /**
     * Gets the coordinates of two points (that represent a bond) and
     * calculates for each the coordinates of two new points that have the given
     * distance vertical to the bond.
     *
     * @param   coords  The coordinates of the two given points of the bond
     *                                        like this [point1x, point1y, point2x, point2y]
     * @param   dist  The vertical distance between the given points and those to be calculated
     * @return     The coordinates of the calculated four points
     */
    public static int[] distanceCalculator(int[] coords, double dist)
    {
        double angle;

        if ((coords[2] - coords[0]) == 0)
        {
            angle = Math.PI / 2;
        }
        else
        {
            angle = Math.atan(((double) coords[3] - (double) coords[1]) /
                    ((double) coords[2] - (double) coords[0]));
        }

        int begin1X = (int) ((Math.cos(angle + (Math.PI / 2)) * dist) +
                coords[0]);
        int begin1Y = (int) ((Math.sin(angle + (Math.PI / 2)) * dist) +
                coords[1]);
        int begin2X = (int) ((Math.cos(angle - (Math.PI / 2)) * dist) +
                coords[0]);
        int begin2Y = (int) ((Math.sin(angle - (Math.PI / 2)) * dist) +
                coords[1]);
        int end1X = (int) ((Math.cos(angle - (Math.PI / 2)) * dist) +
                coords[2]);
        int end1Y = (int) ((Math.sin(angle - (Math.PI / 2)) * dist) +
                coords[3]);
        int end2X = (int) ((Math.cos(angle + (Math.PI / 2)) * dist) +
                coords[2]);
        int end2Y = (int) ((Math.sin(angle + (Math.PI / 2)) * dist) +
                coords[3]);

        int[] newCoords =
            {begin1X, begin1Y, begin2X, begin2Y, end1X, end1Y, end2X, end2Y};

        return newCoords;
    }

    /**
     * Calculates the center of the given atoms and returns it as a Point2d
     *
     * @param   atoms  The vector of the given atoms
     * @return     The center of the given atoms as Point2d
     */
    public static Point2d get2DCenter(List atoms)
    {
        Atom atom;
        double x = 0;
        double y = 0;

        for (int f = 0; f < atoms.size(); f++)
        {
            atom = (Atom) atoms.get(f);

            if ((atom.get3Dx() != 0.0) || (atom.get3Dy() != 0.0))
            {
                x += atom.get3Dx();
                y += atom.get3Dy();
            }
        }

        return new Point2d(x / (double) atoms.size(),
                y / (double) atoms.size());
    }

    /**
     * Calculates the center of mass for the <code>Atom</code>s in the
     * AtomContainer for the 2D coordinates.
     *
     * @param ac        AtomContainer for which the center of mass is calculated
     *
     * @keyword center of mass
     */
    public static Point2d get2DCentreOfMass(RenderingAtoms ac)
    {
        double x = 0.0;
        double y = 0.0;

        double totalmass = 0.0;

        Atom[] atoms = ac.getRenderAtoms();

        for (int i = 0; i < atoms.length; i++)
        {
            Atom a = atoms[i];
            double mass = AtomMass.getDoubleValue(a);
            totalmass += mass;
            x += (mass * a.get3Dx());
            y += (mass * a.get3Dy());
        }

        return new Point2d(x / totalmass, y / totalmass);
    }

    /**
     * Returns the java.awt.Dimension of a molecule
     *
     * @param   molecule of which the dimension should be returned
     * @return The java.awt.Dimension of this molecule
     */
    public static Dimension get2DDimension(RenderingAtoms atomCon)
    {
        double[] minmax = getMinMax(atomCon);
        double maxX = minmax[2];
        double maxY = minmax[3];
        double minX = minmax[0];
        double minY = minmax[1];

        return new Dimension((int) (maxX - minX + 1), (int) (maxY - minY + 1));
    }

    /**
     * Calculates the center of mass for the <code>Atom</code>s in the
     * AtomContainer for the 2D coordinates.
     *
     * @param ac        AtomContainer for which the center of mass is calculated
     *
     * @keyword center of mass
     */
    public static Point3d get3DCentreOfMass(RenderingAtoms ac)
    {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        double totalmass = 0.0;

        Atom[] atoms = ac.getRenderAtoms();

        for (int i = 0; i < atoms.length; i++)
        {
            Atom a = atoms[i];
            double mass = AtomMass.getDoubleValue(a);
            totalmass += mass;
            x += (mass * a.get3Dx());
            y += (mass * a.get3Dy());
            z += (mass * a.get3Dz());
        }

        return new Point3d(x / totalmass, y / totalmass, z / totalmass);
    }

    public static double getAngle(double xDiff, double yDiff)
    {
        double angle = 0;

        //              System.out.println("getAngle->xDiff: " + xDiff);
        //              System.out.println("getAngle->yDiff: " + yDiff);
        if ((xDiff >= 0) && (yDiff >= 0))
        {
            angle = Math.atan(yDiff / xDiff);
        }
        else if ((xDiff < 0) && (yDiff >= 0))
        {
            angle = Math.PI + Math.atan(yDiff / xDiff);
        }
        else if ((xDiff < 0) && (yDiff < 0))
        {
            angle = Math.PI + Math.atan(yDiff / xDiff);
        }
        else if ((xDiff >= 0) && (yDiff < 0))
        {
            angle = (2 * Math.PI) + Math.atan(yDiff / xDiff);
        }

        return angle;
    }

    /**
     * Determines the best alignment for the label of an atom in 2D space.
     * It returns 1 if left aligned, and -1 if right aligned.
     */
    public static int getBestAlignmentForLabel(RenderingAtoms container,
        Atom atom)
    {
        NbrAtomIterator nait = atom.nbrAtomIterator();
        Atom connectedAtom;
        int overallDiffX = 0;

        while (nait.hasNext())
        {
            connectedAtom = nait.nextNbrAtom();
            overallDiffX = overallDiffX +
                (int) (connectedAtom.get3Dx() - atom.get3Dx());
        }

        //System.out.println("label position for atom "+atom.getIdx()+" is "+overallDiffX);
        if (overallDiffX < 0)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Writes the coordinates of the atoms participating the given bond into an array.
     *
     * @param   bond   The given bond
     * @return     The array with the coordinates
     */
    public static int[] getBondCoordinates(Bond bond)
    {
        int beginX = (int) bond.getBegin().get3Dx();
        int endX = (int) bond.getEnd().get3Dx();
        int beginY = (int) bond.getBegin().get3Dy();
        int endY = (int) bond.getEnd().get3Dy();
        int[] coords = {beginX, beginY, endX, endY};

        return coords;
    }

    /**
     * Returns the atom of the given molecule that is closest to the given
     * coordinates.
     *
     * @param   xPosition  The x coordinate
     * @param   yPosition  The y coordinate
     * @param   molecule  The molecule that is searched for the closest atom
     * @return   The atom that is closest to the given coordinates
     */
    public static Atom getClosestAtom(int xPosition, int yPosition,
        RenderingAtoms atomCon)
    {
        Atom closestAtom = null;
        Atom currentAtom;
        double smallestMouseDistance = -1;
        double mouseDistance;
        double atomX;
        double atomY;

        for (int i = 0; i < atomCon.getRenderAtomCount(); i++)
        {
            currentAtom = atomCon.getRenderAtomAtom(i);
            atomX = currentAtom.get3Dx();
            atomY = currentAtom.get3Dy();
            mouseDistance = Math.sqrt(Math.pow(atomX - xPosition, 2) +
                    Math.pow(atomY - yPosition, 2));

            if ((mouseDistance < smallestMouseDistance) ||
                    (smallestMouseDistance == -1))
            {
                smallestMouseDistance = mouseDistance;
                closestAtom = currentAtom;
            }
        }

        return closestAtom;
    }

    /**
     * Returns the minimum and maximum X and Y coordinates of the
     * atoms in the AtomContainer. The output is returned as:
     * <pre>
     *   minmax[0] = minX;
     *   minmax[1] = minY;
     *   minmax[2] = maxX;
     *   minmax[3] = maxY;
     * </pre>
     *
     * @return An four int array as defined above.
     */
    public static double[] getMinMax(RenderingAtoms container)
    {
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (int i = 0; i < container.getRenderAtomCount(); i++)
        {
            Atom atom = container.getRenderAtomAtom(i);

            if ((atom.get3Dx() != 0.0) || (atom.get3Dy() != 0.0))
            {
                if (atom.get3Dx() > maxX)
                {
                    maxX = atom.get3Dx();
                }

                if (atom.get3Dx() < minX)
                {
                    minX = atom.get3Dx();
                }

                if (atom.get3Dy() > maxY)
                {
                    maxY = atom.get3Dy();
                }

                if (atom.get3Dy() < minY)
                {
                    minY = atom.get3Dy();
                }
            }
        }

        double[] minmax = new double[4];
        minmax[0] = minX;
        minmax[1] = minY;
        minmax[2] = maxX;
        minmax[3] = maxY;

        return minmax;
    }

    /**
     * Calculates the normalization factor in order to get an average
     * bond length of 1.5. It takes only into account Bond's with two
     * atoms.
     */
    public static double getNormalizationFactor(RenderingAtoms container)
    {
        List bonds = container.getRenderBonds();
        double bondlength = 0.0;
        double ratio = 0.0;

        /* Desired bond length for storing structures in MDL mol files
           This should probably be set externally (from system wide settings) */
        double desiredBondLength = 1.5;

        // loop over all bonds and determine the mean bond distance
        int counter = 0;

        for (int f = 0; f < bonds.size(); f++)
        {
            // only consider two atom bonds into account
            counter++;

            Atom atom1 = ((Bond) bonds.get(f)).getBegin();
            Atom atom2 = ((Bond) bonds.get(f)).getEnd();

            bondlength += Math.sqrt(
                    Math.pow(atom1.get3Dx() - atom2.get3Dx(), 2) +
                    Math.pow(atom1.get3Dy() - atom2.get3Dy(), 2));
        }

        bondlength = bondlength / counter;
        ratio = desiredBondLength / bondlength;

        return ratio;
    }

    /**
     * Returns the bond of the given molecule that is closest to the given
     * coordinates.
     *
     * @param   xPosition  The x coordinate
     * @param   yPosition  The y coordinate
     * @param   molecule  The molecule that is searched for the closest bond
     * @return   The bond that is closest to the given coordinates
     */

    //  public static Bond getClosestBond(
    //          int xPosition,
    //          int yPosition,
    //          AtomContainer atomCon) {
    //          Point2d bondCenter;
    //          Bond closestBond = null, currentBond;
    //          double smallestMouseDistance = -1,
    //                  mouseDistance,
    //                  bondCenterX,
    //                  bondCenterY;
    //          Vector bonds = atomCon.getBonds();
    //          for (int i = 0; i < bonds.size(); i++) {
    //                  currentBond = (Bond)bonds.get(i);
    //                  bondCenter = get2DCenter(currentBond.getAtomsVector());
    //                  mouseDistance =
    //                          Math.sqrt(
    //                                  Math.pow(bondCenter.x - xPosition, 2)
    //                                          + Math.pow(bondCenter.y - yPosition, 2));
    //                  if (mouseDistance < smallestMouseDistance
    //                          || smallestMouseDistance == -1) {
    //                          smallestMouseDistance = mouseDistance;
    //                          closestBond = currentBond;
    //                  }
    //          }
    //          return closestBond;
    //  }

    /**
     * Sorts a Vector of atoms such that the 2D distances of the
     * atom locations from a given point are smallest for the first
     * atoms in the vector
     *
     * @param   point  The point from which the distances to the atoms are measured
     * @param   atoms  The atoms for which the distances to point are measured
     */

    //  public static void sortBy2DDistance(Atom[] atoms, Point2d point) {
    //          double distance1, distance2;
    //          Atom atom1 = null, atom2 = null;
    //          boolean doneSomething = false;
    //          do {
    //                  doneSomething = false;
    //                  for (int f = 0; f < atoms.length - 1; f++) {
    //                          atom1 = atoms[f];
    //                          atom2 = atoms[f + 1];
    //                          distance1 = point.distance(atom1.getPoint2D());
    //                          distance2 = point.distance(atom2.getPoint2D());
    //                          if (distance2 < distance1) {
    //                                  atoms[f] = atom2;
    //                                  atoms[f + 1] = atom1;
    //                                  doneSomething = true;
    //                          }
    //                  }
    //          } while (doneSomething);
    //  }

    /** Determines the scale factor for displaying a structure loaded from disk in a frame.
      * An average of all bond length values is produced and the structure is scaled
      * such that the resulting bond length divided by the
      * character size equals the current Chemistry Development Kit (CKD)Models bondlengthToCharactersizeRatio
      * setting.
      *
      * @param   ac The AtomContainer for which the ScaleFactor is to be calculated
      * @return  The ScaleFactor with which the AtomContainer must be scaled
     */
    public static double getScaleFactor(RenderingAtoms ac, double bondLength)
    {
        double bondLengthSum = 0;
        Bond bond = null;

        List bonds = ac.getRenderBonds();

        for (int f = 0; f < bonds.size(); f++)
        {
            bond = (Bond) bonds.get(f);
            bondLengthSum += 1.0;
        }

        return bondLength / (bondLengthSum / ac.getRenderBonds().size());
    }

    /** Determines if this AtomContainer contains 2D coordinates.
      *
      * @return  boolean indication that 2D coordinates are available
     */
    public static boolean has2DCoordinates(RenderingAtoms m)
    {
        Atom[] atoms = m.getRenderAtoms();

        for (int i = 0; i < atoms.length; i++)
        {
            if ((atoms[i].get3Dx() == 0.0) && (atoms[i].get3Dy() == 0.0))
            {
                return false;
            }
        }

        return true;
    }

    /** Determines if this Atom contains 2D coordinates.
      *
      * @return  boolean indication that 2D coordinates are available
     */
    public static boolean has2DCoordinates(Atom a)
    {
        if ((a.get3Dx() == 0.0) && (a.get3Dy() == 0.0))
        {
            return false;
        }

        return true;
    }

    /**
     * @param bond
     * @return
     */
    public static boolean has2DCoordinates(Bond bond)
    {
        if ((bond.getBegin().get3Dx() == 0.0) &&
                (bond.getBegin().get3Dy() == 0.0))
        {
            return false;
        }

        if ((bond.getEnd().get3Dx() == 0.0) && (bond.getEnd().get3Dy() == 0.0))
        {
            return false;
        }

        return true;
    }

    /** Determines if this model contains 3D coordinates
      *
      * @return  boolean indication that 3D coordinates are available
     */
    public static boolean has3DCoordinates(RenderingAtoms m)
    {
        boolean hasinfo = true;
        Atom[] atoms = m.getRenderAtoms();

        for (int i = 0; i < atoms.length; i++)
        {
            if ((atoms[i].get3Dx() != 0.0) || (atoms[i].get3Dy() != 0.0) ||
                    (atoms[i].get3Dz() != 0.0))
            {
                hasinfo = false;
            }
        }

        return hasinfo;
    }

    /**
     * Rotates a molecule around a given center by a given angle
     *
     * @param   molecule  The molecule to be rotated
     * @param   center    A point giving the rotation center
     * @param   angle      The angle by which to rotate the molecule
     */
    public static void rotate(RenderingAtoms atomCon, Point2d center,
        double angle)
    {
        Point2d p = null;
        double distance;
        double offsetAngle;
        Atom atom = null;

        for (int i = 0; i < atomCon.getRenderAtomCount(); i++)
        {
            atom = atomCon.getRenderAtomAtom(i);
            p = new Point2d(atom.get3Dx(), atom.get3Dy());
            offsetAngle = getAngle(p.x - center.x, p.y - center.y);
            distance = p.distance(center);
            p.x = center.x + (Math.sin(angle + offsetAngle) * distance);
            p.y = center.y - (Math.cos(angle + offsetAngle) * distance);
            atom.setCoords3D(p.x, p.y, atom.get3Dz());
        }
    }

    /**
     * Multiplies all the coordinates of the atoms of the given molecule with the scalefactor.
     *
     * @param   molecule  The molecule to be scaled
     */
    public static void scaleMolecule(RenderingAtoms atomCon, double scaleFactor)
    {
        Atom atom;

        for (int i = 0; i < atomCon.getRenderAtomCount(); i++)
        {
            atom = atomCon.getRenderAtomAtom(i);

            //if (atom.getX()!=0.0 && atom.getY()!=0.0) {
            atom.setCoords3D(scaleFactor * atom.get3Dx(),
                scaleFactor * atom.get3Dy(), scaleFactor * atom.get3Dz());

            //}
            if (logger.isDebugEnabled())
            {
                logger.debug("scale atom " + atom.getIndex() + " " +
                    atom.getCoords3D());
            }
        }
    }

    /**
     * Scales a molecule such that it fills a given percentage of a given dimension
     *
     * @param   molecule  The molecule to be scaled
     * @param   dim       The dimension to be filled
     * @param   percentage  The percentage of the dimension to be filled
     */
    public static void scaleMolecule(RenderingAtoms atomCon, Dimension areaDim,
        double fillFactor)
    {
        Dimension molDim = get2DDimension(atomCon);
        double widthFactor = (double) areaDim.width / (double) molDim.width;
        double heightFactor = (double) areaDim.height / (double) molDim.height;
        double scaleFactor = Math.min(widthFactor, heightFactor) * fillFactor;
        scaleMolecule(atomCon, scaleFactor);
    }

    /**
     * Translates a molecule from the origin to a new point denoted by a vector.
     *
     * @param atomCon  molecule to be translated
     * @param vector   dimension that represents the translation vector
     */
    public static void translate2D(RenderingAtoms atomCon, Vector2d vector)
    {
        Atom[] atoms = atomCon.getRenderAtoms();

        for (int i = 0; i < atoms.length; i++)
        {
            //if (atoms[i].getX()!=0.0 || atoms[i].getY()!=0.0 ) {
            if (logger.isDebugEnabled())
            {
                logger.debug("translate atom " + atoms[i].getIndex() + " " +
                    atoms[i].getCoords3D());
            }

            atoms[i].setCoords3D(atoms[i].get3Dx() + vector.x,
                atoms[i].get3Dy() + vector.y, atoms[i].get3Dz());

            if (logger.isDebugEnabled())
            {
                logger.debug("   to " + atoms[i].getCoords3D());
            }

            //} else {
            //  logger.warn("Could not translate atom in 2D space");
            //}
        }
    }

    /**
     * Translates the given molecule by the given Vector.
     *
     * @param   molecule  The molecule to be translated
     * @param   transX  translation in x direction
     * @param   transY  translation in y direction
     */
    public static void translate2D(RenderingAtoms atomCon, double transX,
        double transY)
    {
        translate2D(atomCon, new Vector2d(transX, transY));
    }

    /**
     * Translates a molecule from the origin to a new point denoted by a vector.
     *
     * @param atomCon  molecule to be translated
     * @param vector   dimension that represents the translation vector
     */
    public static void translate2DCentreOfMassTo(RenderingAtoms atomCon,
        Point2d p)
    {
        Point2d com = get2DCentreOfMass(atomCon);
        Vector2d translation = new Vector2d(p.x - com.x, p.y - com.y);
        Atom[] atoms = atomCon.getRenderAtoms();

        for (int i = 0; i < atoms.length; i++)
        {
            if ((atoms[i].get3Dx() != 0.0) || (atoms[i].get3Dy() != 0.0))
            {
                atoms[i].setCoords3D(atoms[i].get3Dx() + translation.x,
                    atoms[i].get3Dy() + translation.y, atoms[i].get3Dz());
            }
        }
    }

    /**
     * Adds an automatically calculated offset to the coordinates of all atoms
     * such that all coordinates are positive and the smallest x or y coordinate
     * is exactly zero.
     *
     * @param   molecule for which all the atoms are translated to positive coordinates
     */
    public static void translateAllPositive(RenderingAtoms atomCon)
    {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        Atom[] atoms = atomCon.getRenderAtoms();

        for (int i = 0; i < atoms.length; i++)
        {
            //if (atoms[i].getX()!=0.0 || atoms[i].getY()!=0.0) {
            if (atoms[i].get3Dx() < minX)
            {
                minX = atoms[i].get3Dx();
            }

            if (atoms[i].get3Dy() < minY)
            {
                minY = atoms[i].get3Dy();
            }

            //}
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Translating: minx=" + minX + ", minY=" + minY);
        }

        translate2D(atomCon, minX * -1, minY * -1);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
