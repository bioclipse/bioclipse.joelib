///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DragBehavior.java,v $
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

import java.awt.AWTEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class DragBehavior extends Behavior
{
    //~ Instance fields ////////////////////////////////////////////////////////

    Transform3D modelTrans;
    WakeupOr mouseCriterion;
    WakeupCriterion[] mouseEvents;
    TransformGroup transformGroup;
    Transform3D transformX;
    Transform3D transformY;
    int x;
    double x_angle;
    double x_factor;
    int x_last;
    int y;
    double y_angle;
    double y_factor;
    int y_last;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the DragBehavior object
     *
     * @param transformGroup  Description of the Parameter
     */
    protected DragBehavior(TransformGroup transformGroup)
    {
        super();
        this.transformGroup = transformGroup;
        modelTrans = new Transform3D();
        transformX = new Transform3D();
        transformY = new Transform3D();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     */
    public void initialize()
    {
        x = 0;
        y = 0;
        x_last = 0;
        y_last = 0;
        x_angle = 0;
        y_angle = 0;
        x_factor = .03;
        y_factor = .03;

        mouseEvents = new WakeupCriterion[2];
        mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        mouseCriterion = new WakeupOr(mouseEvents);
        wakeupOn(mouseCriterion);
    }

    /**
     * Description of the Method
     *
     * @param criteria  Description of the Parameter
     */
    public void processStimulus(Enumeration criteria)
    {
        WakeupCriterion wakeup;
        AWTEvent[] event;
        int id;
        int dx;
        int dy;

        while (criteria.hasMoreElements())
        {
            wakeup = (WakeupCriterion) criteria.nextElement();

            if (wakeup instanceof WakeupOnAWTEvent)
            {
                event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();

                for (int i = 0; i < event.length; i++)
                {
                    id = event[i].getID();

                    if (id == MouseEvent.MOUSE_DRAGGED)
                    {
                        int mod = ((MouseEvent) event[i]).getModifiers();
                        System.out.println(mod + " " + InputEvent.BUTTON1_MASK);

                        if (isModifier(mod, InputEvent.BUTTON1_MASK))
                        {
                            System.out.println("button1");
                        }

                        if (isModifier(mod, InputEvent.BUTTON3_MASK))
                        {
                            System.out.println("button3");
                        }

                        x = ((MouseEvent) event[i]).getX();
                        y = ((MouseEvent) event[i]).getY();

                        dx = x - x_last;
                        dy = y - y_last;

                        x_angle = dy * y_factor;
                        y_angle = dx * x_factor;

                        transformX.rotX(x_angle);
                        transformY.rotY(y_angle);
                        modelTrans.mul(transformX, modelTrans);
                        modelTrans.mul(transformY, modelTrans);

                        transformGroup.setTransform(modelTrans);

                        x_last = x;
                        y_last = y;
                    }
                    else if (id == MouseEvent.MOUSE_PRESSED)
                    {
                        x_last = ((MouseEvent) event[i]).getX();
                        y_last = ((MouseEvent) event[i]).getY();
                    }
                }
            }
        }

        wakeupOn(mouseCriterion);
    }

    /**
     * Gets the modifier attribute of the DragBehavior object
     *
     * @param test      Description of the Parameter
     * @param modifier  Description of the Parameter
     * @return          The modifier value
     */
    final boolean isModifier(int test, int modifier)
    {
        return ((test & modifier) == modifier);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
