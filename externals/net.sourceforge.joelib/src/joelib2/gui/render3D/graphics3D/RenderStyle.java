///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RenderStyle.java,v $
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

/**
 * Description of the Interface
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public interface RenderStyle
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Description of the Field
     */
    public final static int NONE = -1;

    /**
     * Description of the Field
     */
    public final static int INVISIBLE = 0;

    /**
     * Description of the Field
     */
    public final static int CPK = 1;

    /**
     * Description of the Field
     */
    public final static int BALL_AND_STICK = 2;

    /**
     * Description of the Field
     */
    public final static int STICK = 3;

    /**
     * Description of the Field
     */
    public final static int WIRE = 4;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Sets the style attribute of the RenderStyle object
     *
     * @param style  The new style value
     */
    public void setStyle(int style);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
