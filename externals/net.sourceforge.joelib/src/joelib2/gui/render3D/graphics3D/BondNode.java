///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BondNode.java,v $
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

import joelib2.gui.render3D.molecule.ViewerAtom;
import joelib2.gui.render3D.molecule.ViewerBond;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.picking.PickTool;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class BondNode extends BranchGroup
{
    //~ Static fields/initializers /////////////////////////////////////////////

    final static int STICK_QUALITY = 7;

    //~ Instance fields ////////////////////////////////////////////////////////

    ViewerAtom a1;
    ViewerAtom a2;
    ViewerBond myBond;

    RenderTable rTable = RenderTable.getTable();

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the BondNode object
     *
     * @param b  Description of the Parameter
     */
    protected BondNode(ViewerBond b)
    {
        a1 = b.a1;
        a2 = b.a2;
        createStick(b);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @param b  Description of the Parameter
     * @return   Description of the Return Value
     */
    public static Node createWire(ViewerBond b)
    {
        ViewerAtom a1 = b.a1;
        ViewerAtom a2 = b.a2;

        Group gn = new Group();
        Vector3f middle = getMiddleOfBond(a1, a2);
        float[] vert1 = new float[6];
        vert1[0] = a1.getX();
        vert1[1] = a1.getY();
        vert1[2] = a1.getZ();
        vert1[3] = (float) middle.x;
        vert1[4] = (float) middle.y;
        vert1[5] = (float) middle.z;

        float[] vert2 = new float[6];
        vert2[0] = (float) middle.x;
        vert2[1] = (float) middle.y;
        vert2[2] = (float) middle.z;
        vert2[3] = a2.getX();
        vert2[4] = a2.getY();
        vert2[5] = a2.getZ();

        LineAttributes att = new LineAttributes();
        att.setLineAntialiasingEnable(true);

        float bondScaling = 1.0f;

        if (b.getType() == ViewerBond.DOUBLE)
        {
            bondScaling = 2.0f;
        }
        else if (b.getType() == ViewerBond.AROMATIC)
        {
            bondScaling = 2.0f;
            att.setLinePattern(LineAttributes.PATTERN_USER_DEFINED);
            att.setPatternMask(16383);
        }
        else if (b.getType() == ViewerBond.TRIPLE)
        {
            bondScaling = 3.0f;
        }

        att.setLineWidth(2.5f * bondScaling);

        Appearance app1 = new Appearance();
        Appearance app2 = new Appearance();
        float cInc = 0.25f;
        float[] c = RenderTable.getTable().getRGBFloats(a1);
        ColoringAttributes ca1 = new ColoringAttributes(c[0] + cInc,
                c[1] + cInc, c[2] + cInc, ColoringAttributes.SHADE_FLAT);
        app1.setColoringAttributes(ca1);

        c = RenderTable.getTable().getRGBFloats(a2);

        ColoringAttributes ca2 = new ColoringAttributes(c[0] + cInc,
                c[1] + cInc, c[2] + cInc, ColoringAttributes.SHADE_FLAT);
        app2.setColoringAttributes(ca2);

        app1.setLineAttributes(att);
        app2.setLineAttributes(att);

        LineArray la1 = new LineArray(2,
                LineArray.COORDINATES | LineArray.NORMALS);
        LineArray la2 = new LineArray(2,
                LineArray.COORDINATES | LineArray.NORMALS);
        la1.setCoordinates(0, vert1);
        la1.setNormals(0, vert1);
        la2.setCoordinates(0, vert2);
        la2.setNormals(0, vert2);

        Shape3D s1 = new Shape3D(la1, app1);
        Shape3D s2 = new Shape3D(la2, app2);
        s1.setCapability(s1.ALLOW_APPEARANCE_READ);
        s1.setCapability(s1.ALLOW_APPEARANCE_WRITE);
        s1.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        PickTool.setCapabilities(s1, PickTool.INTERSECT_FULL);
        s2.setCapability(s2.ALLOW_APPEARANCE_READ);
        s2.setCapability(s2.ALLOW_APPEARANCE_WRITE);
        s2.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        PickTool.setCapabilities(s2, PickTool.INTERSECT_FULL);

        gn.addChild(s1);
        gn.addChild(s2);

        b.getParent().pickBondMapping.put(s2, b);
        b.getParent().pickBondMapping.put(s1, b);
        b.shapes.add(s1);
        b.shapes.add(s2);

        return gn;
    }

    /**
     * Description of the Method
     *
     * @param coor   Description of the Parameter
     * @param other  Description of the Parameter
     * @return       Description of the Return Value
     */
    protected static float calcAngle(Vector3f coor, Vector3f other)
    {
        double scalarProduct = (double) coor.dot(other);

        if (scalarProduct == 0.0f)
        {
            //System.out.println("acos "+Math.acos(0.0f));
            return (float) Math.PI / 2.0f;
        }

        scalarProduct = scalarProduct / (coor.length() * other.length());

        return (float) Math.acos(scalarProduct);
    }

    /**
     * Description of the Method
     *
     * @param coor   Description of the Parameter
     * @param other  Description of the Parameter
     * @return       Description of the Return Value
     */
    protected static float calcSign(Vector3f coor, Vector3f other)
    {
        double sp = (double) coor.dot(other);

        if (sp == 0.0f)
        {
            return 0.0f;
        }

        return (float) (sp / Math.abs(sp));
    }

    /**
     * Gets the middleOfBond attribute of the BondNode class
     *
     * @param from  Description of the Parameter
     * @param to    Description of the Parameter
     * @return      The middleOfBond value
     */
    protected static Vector3f getMiddleOfBond(ViewerAtom from, ViewerAtom to)
    {
        float x = (float) (((to.getX() - from.getX()) / 2.0f) + from.getX());
        float y = (float) (((to.getY() - from.getY()) / 2.0f) + from.getY());
        float z = (float) (((to.getZ() - from.getZ()) / 2.0f) + from.getZ());

        return new Vector3f(x, y, z);
    }

    /**
     * Description of the Method
     *
     * @param from  Description of the Parameter
     * @param to    Description of the Parameter
     * @return      Description of the Return Value
     */
    protected float calcDistance(ViewerAtom from, ViewerAtom to)
    {
        double dx = (double) (to.getX() - from.getX());
        double dy = (double) (to.getY() - from.getY());
        double dz = (double) (to.getZ() - from.getZ());

        double dist = (double) Math.sqrt((double) ((dx * dx) + (dy * dy) +
                    (dz * dz)));

        return (float) dist;
    }

    /**
     * Description of the Method
     */
    void createStick(ViewerBond b)
    {
        Vector3f middle = getMiddleOfBond(a1, a2);
        float dist = calcDistance(a1, a2);
        Vector3f rel = new Vector3f((float) (a2.getX() - a1.getX()),
                (float) (a2.getY() - a1.getY()),
                (float) (a2.getZ() - a1.getZ()));

        double xrot = calcAngle(new Vector3f(0.0f, 1.0f, 0.0f), rel);

        Vector3f proj = new Vector3f(rel.x, 0.0f, rel.z);
        float yrot = calcSign(new Vector3f(1.0f, 0.0f, 0.0f), proj) *
            calcAngle(new Vector3f(0.0f, 0.0f, 1.0f), proj);

        //        float bondScaling=0.5f;
        float bondScaling = 1.0f;

        //        if(b.getType()==ViewerBond.DOUBLE) bondScaling=1.0f;
        //        else if(b.getType()==ViewerBond.AROMATIC) bondScaling=1.0f;
        //        else if(b.getType()==ViewerBond.TRIPLE) bondScaling=1.5f;
        Node cyl1 = rTable.getSharedBondGroup(b, a1, bondScaling);
        Node cyl2 = rTable.getSharedBondGroup(b, a2, bondScaling);
        Transform3D rot = new Transform3D();
        rot.rotX(xrot);

        Transform3D t3d1 = new Transform3D();
        t3d1.rotY(yrot);
        t3d1.mul(rot);
        t3d1.setTranslation(new Vector3f(a1.getX(), a1.getY(), a1.getZ()));
        t3d1.setScale(new Vector3d(1.0, (double) (dist / 2.0f), 1.0));

        Transform3D t3d2 = new Transform3D();
        t3d2.rotY(yrot);
        t3d2.mul(rot);
        t3d2.setTranslation(middle);
        t3d2.setScale(new Vector3d(1.0, (double) (dist / 2.0f), 1.0));

        //t3d2.setScale(dist/2.0);
        TransformGroup myTrans = new TransformGroup(t3d1);
        TransformGroup mid = new TransformGroup(t3d2);

        //mid.addChild(cyl2.getShape());
        //myTrans.addChild(cyl1.getShape());
        mid.addChild(cyl2);
        myTrans.addChild(cyl1);

        addChild(myTrans);
        addChild(mid);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
