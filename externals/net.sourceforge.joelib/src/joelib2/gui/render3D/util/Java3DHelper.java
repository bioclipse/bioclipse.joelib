///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Java3DHelper.java,v $
//  Purpose:  Test class for the Java3D configuration
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Gert Sclep
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:34 $
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
package joelib2.gui.render3D.util;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;


/**
 * Test class for the Java3D configuration. If no Canvas3D-object can be
 * constructed, or the graphics board is too old, or the graphics device isn't
 * properly configured.
 *
 * @.author     gsclep
 * @.license    GPL
 */
public class Java3DHelper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static boolean configOK()
    {
        GraphicsConfigTemplate3D tmpl = new GraphicsConfigTemplate3D();
        GraphicsEnvironment env = GraphicsEnvironment
            .getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getBestConfiguration(tmpl);

        try
        {
            new Canvas3D(config);

            return true;
        }
        catch (Exception exc)
        {
            return false;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
