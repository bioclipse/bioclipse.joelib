///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomNode.java,v $
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

//import com.sun.j3d.utils.geometry.Sphere;
//import com.sun.j3d.utils.geometry.ColorCube;
import joelib2.gui.render3D.molecule.ViewerAtom;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import javax.vecmath.Vector3f;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class AtomNode extends BranchGroup
{
    //~ Instance fields ////////////////////////////////////////////////////////

    ViewerAtom myAtom;

    Transform3D myLoc;
    TransformGroup myTrans;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the AtomNode object
     *
     * @param a  Description of the Parameter
     */
    protected AtomNode(ViewerAtom a)
    {
        super();
        myAtom = a;

        //System.out.println("Node atom");
        myLoc = new Transform3D();
        myLoc.set(new Vector3f((float) a.getX(), (float) a.getY(),
                (float) a.getZ()));
        myTrans = new TransformGroup(myLoc);

        addChild(myTrans);

        Node node = RenderTable.getTable().getSharedAtomGroup(myAtom);

        //node.setCapability(Node.ALLOW_PICKABLE_READ);
        //node.setCapability(Node.ALLOW_PICKABLE_WRITE);
        //node.setCapability(Node.ENABLE_PICK_REPORTING);
        myTrans.addChild(node);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Sets the coor attribute of the AtomNode object
     *
     * @param x  The new coor value
     * @param y  The new coor value
     * @param z  The new coor value
     */
    void setCoor(float x, float y, float z)
    {
        myLoc.set(new Vector3f(x, y, z));
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
