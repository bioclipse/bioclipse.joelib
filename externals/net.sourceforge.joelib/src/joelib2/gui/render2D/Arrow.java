///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Arrow.java,v $
//Purpose:  Renderer for a 2D layout.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.6 $
//                      $Date: 2005/02/17 16:48:32 $
//                      $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.gui.render2D;

/**
 * Holding single arrow informations.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:32 $
 */
public class Arrow extends FromToAtoms
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public boolean alignRight;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Parse entries which start with a non-digit.
     *
     * @param optionEntry
     * @return
     */
    public boolean parseOption(String optionEntry)
    {
        char firstChar = optionEntry.charAt(0);

        if (firstChar == 'r')
        {
            alignRight = true;
        }
        else if (firstChar == 'l')
        {
            alignRight = false;
        }

        return true;
    }

    public String toString()
    {
        if ((from == null) || (to == null))
        {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < from.length; i++)
        {
            sb.append(from[i]);
            sb.append(',');
        }

        if (alignRight)
        {
            sb.append('r');
        }
        else
        {
            sb.append('l');
        }

        sb.append(',');

        for (int i = 0; i < to.length; i++)
        {
            sb.append(to[i]);

            if (i < (to.length - 1))
            {
                sb.append(',');
            }
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
