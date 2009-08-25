///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ToolTipInfo.java,v $
//  Purpose:  Tool tip information for chart visualisations.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
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
package joelib2.gui.util;

/**
 * Tool tip information for chart visualisations.
 *
 * @.author     wegnerj
 *     21. März 2002
 */
public interface ToolTipInfo
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the tool tip text. Returns HTML code <b>without</b> the &lt;html> and
     *  &lt;/html> tokens.
     *
     * @param  x  the x coordinate in a chart visualisation
     * @param  y  the y coordinate in a chart visualisation
     * @return    the HTML tool tip text
     */
    public String getHTMLInfo(double x, double y);

    /**
     *  Gets the information type for this tool tip, e.g "infrared data"
     *
     * @return    the information type
     */
    public String getType();
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
