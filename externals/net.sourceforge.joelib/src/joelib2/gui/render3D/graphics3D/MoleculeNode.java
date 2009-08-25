///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeNode.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:33 $
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
package joelib2.gui.render3D.graphics3D;

import joelib2.gui.render3D.molecule.ViewerAtoms;
import joelib2.gui.render3D.molecule.ViewerBonds;
import joelib2.gui.render3D.molecule.ViewerMolecule;

import java.util.List;
import java.util.Vector;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class MoleculeNode extends BranchGroup implements RenderStyle
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private BranchGroup atomGroup;
    private List atoms;
    private BranchGroup bondGroup;
    private List bonds;
    private TransformGroup moleculeTrans;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the MoleculeNode object
     */
    public MoleculeNode()
    {
        super();
        setCapability(BranchGroup.ALLOW_DETACH);
        buildRoot();
    }

    /**
     *Constructor for the MoleculeNode object
     *
     * @param m  Description of the Parameter
     */
    protected MoleculeNode(ViewerMolecule m)
    {
        this(m, RenderStyle.CPK);
    }

    /**
     *Constructor for the MoleculeNode object
     *
     * @param m      Description of the Parameter
     * @param style  Description of the Parameter
     */
    protected MoleculeNode(ViewerMolecule m, int style)
    {
        this();

        if (style != RenderStyle.WIRE)
        {
            atoms = new Vector();
            bonds = new Vector();

            ViewerAtoms v = m.getMyAtoms();
            int s = v.size();

            for (int i = 0; i < s; i++)
            {
                AtomNode a = new AtomNode(v.getAtom(i));
                addAtomNode(a);
            }

            ViewerBonds b = m.getMyBonds();
            s = b.size();

            for (int i = 0; i < s; i++)
            {
                BondNode bn = new BondNode(b.getBond(i));
                addBondNode(bn);
            }
        }
        else
        {
            ViewerBonds b = m.getMyBonds();
            int s = b.size();

            for (int i = 0; i < s; i++)
            {
                bondGroup.addChild(BondNode.createWire(b.getBond(i)));
            }
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds a feature to the AtomNode attribute of the MoleculeNode object
     *
     * @param a  The feature to be added to the AtomNode attribute
     */
    public void addAtomNode(AtomNode a)
    {
        atomGroup.addChild(a);
        atoms.add(a);
    }

    /**
     * Adds a feature to the BondNode attribute of the MoleculeNode object
     *
     * @param b  The feature to be added to the BondNode attribute
     */
    public void addBondNode(BondNode b)
    {
        bondGroup.addChild(b);
        bonds.add(b);
    }

    /**
     * Sets the style attribute of the MoleculeNode object
     *
     * @param style  The new style value
     */
    public void setStyle(int style)
    {
    }

    /**
     * Description of the Method
     */
    void buildRoot()
    {
        //moleculeRoot = new BranchGroup();
        //Transform3D t = new Transform3D();
        //t.set(new Vector3f(0.0f, 0.0f, 20.0f));
        // This will allow to transform individual molecules
        moleculeTrans = new TransformGroup();
        moleculeTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        //BoundingSphere bounds =
        // new BoundingSphere(new Point3d(0.0,0.0,0.0), 500.0);
        // Create the behavior node
        //        DragBehavior behavior = new DragBehavior(moleculeTrans);
        //        behavior.setSchedulingBounds(bounds);
        //        moleculeTrans.addChild(behavior);
        //moleculeRoot.addChild(moleculeTrans);
        addChild(moleculeTrans);

        // currently things are sorted by atoms and bonds
        atomGroup = new BranchGroup();
        bondGroup = new BranchGroup();
        moleculeTrans.addChild(atomGroup);
        moleculeTrans.addChild(bondGroup);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
