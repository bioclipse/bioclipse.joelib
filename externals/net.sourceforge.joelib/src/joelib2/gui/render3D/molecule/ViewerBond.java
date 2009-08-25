///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ViewerBond.java,v $
//  Purpose:  Molecule class for Java3D viewer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:34 $
//            $Author: wegner $
//  Original Author: Jason Plurad (jplurad@tripos.com),
//                   Mike Brusati (brusati@tripos.com)
//                   Zhidong Xie (zxie@tripos.com)
//  Original Version: ftp.tripos.com/pub/java3d/
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
package joelib2.gui.render3D.molecule;

import joelib2.feature.types.bondlabel.BondInAromaticSystem;
import joelib2.feature.types.bondlabel.BondIsAmide;

import joelib2.molecule.Bond;

import java.util.LinkedList;


/**
 * Bond class for Java3D viewer.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:34 $
 */
public class ViewerBond
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Single bond type
     */
    public final static int SINGLE = 1;

    /**
     * Double bond type
     */
    public final static int DOUBLE = 2;

    /**
     * Triple bond type
     */
    public final static int TRIPLE = 3;

    /**
     * Aromatic bond type
     */
    public final static int AROMATIC = 4;

    /**
     * Wedge up bond type
     */
    public final static int WEDGE = 5;

    /**
     * Wedge down bond type
     */
    public final static int DASH = 6;

    /**
     * Any bond type
     */
    public final static int ANY = 7;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * From and to atoms, respectively
     */
    public ViewerAtom a1;

    /**
     * From and to atoms, respectively
     */
    public ViewerAtom a2;

    public LinkedList shapes = new LinkedList();
    Bond bond;
    private ViewerMolecule parent;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Constructor without Properties
     *
     * @param from   from atom
     * @param to     to atom
     * @param btype  Description of the Parameter
     */
    public ViewerBond(ViewerMolecule _parent, Bond _bond, ViewerAtom from,
        ViewerAtom to)
    {
        bond = _bond;
        a1 = from;
        a2 = to;
        parent = _parent;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns id of the bond
     *
     * @return   The id value
     */
    public int getId()
    {
        return bond.getIndex();
    }

    public Bond getJOEBond()
    {
        return bond;
    }

    public ViewerMolecule getParent()
    {
        return parent;
    }

    /**
     * Returns bond type
     *
     * @return   The type value
     */
    public int getType()
    {
        int btype = 0;

        if (BondInAromaticSystem.isAromatic(bond))
        {
            btype = ViewerBond.AROMATIC;
        }
        else if (BondIsAmide.isAmide(bond))
        {
            btype = ViewerBond.SINGLE;
        }
        else
        {
            btype = bond.getBondOrder();
        }

        return btype;
    }

    /**
     * Retrun the length of this bond
     *
     * @return   the length of this bond
     */
    public float length()
    {
        float xdiff = a1.getX() - a2.getX();
        float ydiff = a1.getY() - a2.getY();
        float zdiff = a1.getZ() - a2.getZ();

        float lenSquare = (xdiff * xdiff) + (ydiff * ydiff) + (zdiff * zdiff);

        return (float) Math.sqrt(lenSquare);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
