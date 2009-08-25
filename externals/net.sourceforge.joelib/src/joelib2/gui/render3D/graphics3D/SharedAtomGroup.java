///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SharedAtomGroup.java,v $
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

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.SharedGroup;
import javax.media.j3d.Switch;


/**
 * SharedAtomGroup.java
 *
 *
 * Created: Sat Nov 28 20:22:12 1998
 *
 * @.author    Stephan Reiling
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class SharedAtomGroup extends SharedGroup implements RenderStyle
{
    //~ Instance fields ////////////////////////////////////////////////////////

    Switch mySwitch;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the SharedAtomGroup object
     *
     * @param a  Description of the Parameter
     */
    public SharedAtomGroup(ViewerAtom a)
    {
        super();
        mySwitch = new Switch(Switch.CHILD_MASK);
        mySwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        addChild(mySwitch);

        RenderTable rTable = RenderTable.getTable();

        Material m = rTable.getMaterial(a);
        Appearance appearance = new Appearance();
        appearance.setMaterial(m);

        // Ball and Stick:
        IcoSphere is = new IcoSphere(rTable.BALL_RADIUS, 1, appearance);
        Node n = (Node) is.getShape();
        mySwitch.addChild(n);
        a.getParent().pickAtomMapping.put(is.getShape(), a);
        a.shapes.add(is.getShape());

        //CPK:
        is = new IcoSphere(rTable.getRadius(a), 2, appearance);
        n = (Node) is.getShape();
        mySwitch.addChild(n);
        a.getParent().pickAtomMapping.put(is.getShape(), a);
        a.shapes.add(is.getShape());

        // STICK:
        is = new IcoSphere(rTable.STICK_RADIUS, 0, appearance);
        n = (Node) is.getShape();
        mySwitch.addChild(n);
        a.getParent().pickAtomMapping.put(is.getShape(), a);
        a.shapes.add(is.getShape());

        mySwitch.setWhichChild(0);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Sets the style attribute of the SharedAtomGroup object
     *
     * @param style  The new style value
     */
    public void setStyle(int style)
    {
        switch (style)
        {
        case RenderStyle.BALL_AND_STICK:
            mySwitch.setWhichChild(0);

            break;

        case RenderStyle.CPK:
            mySwitch.setWhichChild(1);

            break;

        case RenderStyle.STICK:
            mySwitch.setWhichChild(2);

            break;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
