///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MathUtils.java,v $
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
package joelib2.gui.render3D.math.util;

/**
 * This class provides some math utility methods.
 *
 * @.author    Mike Brusati
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class MathUtils
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Perform a division of the input integers, and round to the next integer
     * if the divisor is not a even multiple of the dividend.
     *
     * @param dividend  the number to be divided
     * @param divisor   the number by which to divide
     * @return          the result of the division, with possible rounding
     */
    public static int divideAndRound(int dividend, int divisor)
    {
        int result = 0;

        if (divisor != 0)
        {
            result = ((dividend % divisor) == 0) ? (dividend / divisor)
                                                 : ((dividend / divisor) + 1);
        }

        return result;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
