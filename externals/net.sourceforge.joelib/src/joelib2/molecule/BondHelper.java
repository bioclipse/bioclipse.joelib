///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BondHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
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

import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;

import joelib2.math.BasicVector3D;
import joelib2.math.Vector3D;

import joelib2.ring.Ring;

import joelib2.util.iterator.BondIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom tree.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:36 $
 */
public class BondHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(BondHelper.class
            .getName());

    /**
     *  Bond order: aromatic bond
     */
    public final static int AROMATIC_BO = 5;

    /**
     *  Bond flag: is wedge bond
     */
    public final static int IS_WEDGE = (1 << 1);

    /**
     *  Bond flag: is hash bond
     */
    public final static int IS_HASH = (1 << 2);

    /**
     *  Bond flag: is torup bond
     */
    public final static int IS_TORUP = (1 << 3);

    /**
     *  Bond flag: is tordown bond
     */
    public final static int IS_TORDOWN = (1 << 4);

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the length of the <tt>Bond</tt> object.
     *
     * @return    The euclidian distance between start and end atom
     */
    public static final double getLength(Bond bond)
    {
        double dist = 0.0;
        double xPos;
        double yPos;
        double zPos;
        Atom begin = bond.getBegin();
        Atom end = bond.getEnd();

        xPos = begin.get3Dx() - end.get3Dx();
        yPos = begin.get3Dy() - end.get3Dy();
        zPos = begin.get3Dz() - end.get3Dz();
        dist = (xPos * xPos) + (yPos * yPos) + (zPos * zPos);

        return Math.sqrt(dist);
    }

    /**
     *  Gets the equibLength attribute of the <tt>Bond</tt> object
     *
     * @return    The equibLength value
     */
    public static double getLengthEquib(Bond bond)
    {
        double length;
        Atom begin;
        Atom end;

        // CorrectedBondRad will always return a # now
        //  if (!correctedBondRad(getBeginAtom(),rad1)) return(0.0);
        //  if (!correctedBondRad(getEndAtom(),rad2))   return(0.0);
        begin = bond.getBegin();
        end = bond.getEnd();
        length =
            BasicElementHolder.instance().correctedBondRad(begin
                .getAtomicNumber(), AtomHybridisation.getIntValue(begin)) +
            BasicElementHolder.instance().correctedBondRad(end
                .getAtomicNumber(), AtomHybridisation.getIntValue(end));

        if (BondInAromaticSystem.isAromatic(bond))
        {
            length *= 0.93f;
        }
        else if (bond.getBondOrder() == 2)
        {
            length *= 0.91f;
        }
        else if (bond.getBondOrder() == 3)
        {
            length *= 0.87f;
        }

        return length;
    }

    public static boolean isOuterBond(Bond baseBond, Bond b)
    {
        List sssRings = baseBond.getParent().getSSSR();

        Ring ring = null;
        boolean ringFound = false;
        Bond bond;

        for (int i = 0; i < sssRings.size(); i++)
        {
            ring = (Ring) sssRings.get(i);

            BondIterator bit = baseBond.getParent().bondIterator();

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                if (ring.isMember(bond))
                {
                    ringFound = true;
                }
            }

            if (ringFound)
            {
                break;
            }
        }

        if (ringFound)
        {
            Vector3D center = new BasicVector3D();
            Vector3D normal1 = new BasicVector3D();
            Vector3D normal2 = new BasicVector3D();
            ring.findCenterAndNormal(center, normal1, normal2);

            double xVec1;
            double yVec1;
            double xVec2;
            double yVec2;
            xVec1 = b.getEnd().get3Dx() - b.getBegin().get3Dx();
            yVec1 = b.getEnd().get3Dy() - b.getBegin().get3Dy();
            xVec2 = center.getX3D() - b.getBegin().get3Dx();
            yVec2 = center.getY3D() - b.getBegin().get3Dy();

            return (((xVec1 * yVec2) - (yVec1 * xVec2)) >= 0); // right side
        }

        return false;
    }

    /**
     *  Sets the length attribute of the <tt>Bond</tt> object
     *
     * @param  fixed   The new length value
     * @param  length  The new length value
     */
    public static void setLength(Bond bond, Atom fixed, double length)
    {
        Molecule mol = fixed.getParent();
        List<Atom> children = new Vector<Atom>();
        int fixedIdx = fixed.getIndex();
        int nbrIdx = bond.getNeighbor(fixed).getIndex();

        mol.findChildren(children, fixedIdx, nbrIdx);
        children.add(mol.getAtom(nbrIdx));

        Vector3D vec1;
        Vector3D vec2;
        vec1 = bond.getNeighbor(fixed).getCoords3D();
        vec2 = fixed.getCoords3D();

        Vector3D vec3 = new BasicVector3D();
        Vector3D vec4 = new BasicVector3D();
        BasicVector3D.sub(vec3, vec1, vec2);
        vec3.normalize();
        vec3.muling(length);
        vec3.adding(vec2);
        BasicVector3D.sub(vec4, vec3, vec1);

        Atom atom;

        for (int index = 0; index < children.size(); index++)
        {
            atom = children.get(index);
            vec1 = atom.getCoords3D();
            vec1.adding(vec4);
            atom.setCoords3D(vec1);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
