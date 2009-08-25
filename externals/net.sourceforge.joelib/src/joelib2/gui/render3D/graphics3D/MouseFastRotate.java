///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MouseFastRotate.java,v $
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

import java.awt.event.MouseEvent;

import javax.media.j3d.TransformGroup;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;


/**
 * MouseFastRotate.java
 *
 *
 * @.author    Stephan Reiling
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class MouseFastRotate extends MouseRotate
{
    //~ Instance fields ////////////////////////////////////////////////////////

    MolecularScene myScene;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the MouseFastRotate object
     *
     * @param transformGroup  Description of the Parameter
     * @param scene           Description of the Parameter
     */
    public MouseFastRotate(TransformGroup transformGroup, MolecularScene scene)
    {
        super(transformGroup);
        myScene = scene;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @param evt  Description of the Parameter
     */
    public void processMouseEvent(MouseEvent evt)
    {
        if (evt.getID() == MouseEvent.MOUSE_PRESSED)
        {
            myScene.setFast();
        }
        else if (evt.getID() == MouseEvent.MOUSE_RELEASED)
        {
            myScene.setNice();
        }

        //           else if (evt.getID() == MouseEvent.MOUSE_MOVED) {
        //           // Process mouse move event
        //           }
        super.processMouseEvent(evt);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
