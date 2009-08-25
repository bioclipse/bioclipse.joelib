///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: PointGroups.java,v $
//  Purpose:  Brute force symmetry analyzer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Original author: (C) 1996, 2003 S. Patchkovskii, Serguei.Patchkovskii@sympatico.ca
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:35 $
//            $Author: wegner $
//
// Copyright Symmetry:       S. Patchkovskii, 1996,2000,2003
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.math.symmetry;

import java.util.Hashtable;
import java.util.Map;


/**
 * PointGroups.
 *
 * @.author     Serguei Patchkovskii
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:35 $
 */
public class PointGroups
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public final static PointGroup[] defaultPointGroups =
        {
            new PointGroup("C1", "", true),
            new PointGroup("Cs", "(sigma) ", true),
            new PointGroup("Ci", "(i) ", true),
            new PointGroup("C2", "(C2) ", true),
            new PointGroup("C3", "(C3) ", true),
            new PointGroup("C4", "(C4) (C2) ", true),
            new PointGroup("C5", "(C5) ", true),
            new PointGroup("C6", "(C6) (C3) (C2) ", true),
            new PointGroup("C7", "(C7) ", true),
            new PointGroup("C8", "(C8) (C4) (C2) ", true),
            new PointGroup("D2", "3*(C2) ", true),
            new PointGroup("D3", "(C3) 3*(C2) ", true),
            new PointGroup("D4", "(C4) 5*(C2) ", true),
            new PointGroup("D5", "(C5) 5*(C2) ", true),
            new PointGroup("D6", "(C6) (C3) 7*(C2) ", true),
            new PointGroup("D7", "(C7) 7*(C2) ", true),
            new PointGroup("D8", "(C8) (C4) 9*(C2) ", true),
            new PointGroup("C2v", "(C2) 2*(sigma) ", true),
            new PointGroup("C3v", "(C3) 3*(sigma) ", true),
            new PointGroup("C4v", "(C4) (C2) 4*(sigma) ", true),
            new PointGroup("C5v", "(C5) 5*(sigma) ", true),
            new PointGroup("C6v", "(C6) (C3) (C2) 6*(sigma) ", true),
            new PointGroup("C7v", "(C7) 7*(sigma) ", true),
            new PointGroup("C8v", "(C8) (C4) (C2) 8*(sigma) ", true),
            new PointGroup("C2h", "(i) (C2) (sigma) ", true),
            new PointGroup("C3h", "(C3) (S3) (sigma) ", true),
            new PointGroup("C4h", "(i) (C4) (C2) (S4) (sigma) ", true),
            new PointGroup("C5h", "(C5) (S5) (sigma) ", true),
            new PointGroup("C6h", "(i) (C6) (C3) (C2) (S6) (S3) (sigma) ", true),
            new PointGroup("C7h", "(C7) (S7) (sigma) ", true),
            new PointGroup("C8h", "(i) (C8) (C4) (C2) (S8) (S4) (sigma) ", true),
            new PointGroup("D2h", "(i) 3*(C2) 3*(sigma) ", true),
            new PointGroup("D3h", "(C3) 3*(C2) (S3) 4*(sigma) ", true),
            new PointGroup("D4h", "(i) (C4) 5*(C2) (S4) 5*(sigma) ", true),
            new PointGroup("D5h", "(C5) 5*(C2) (S5) 6*(sigma) ", true),
            new PointGroup("D6h", "(i) (C6) (C3) 7*(C2) (S6) (S3) 7*(sigma) ",
                true),
            new PointGroup("D7h", "(C7) 7*(C2) (S7) 8*(sigma) ", true),
            new PointGroup("D8h", "(i) (C8) (C4) 9*(C2) (S8) (S4) 9*(sigma) ",
                true), new PointGroup("D2d", "3*(C2) (S4) 2*(sigma) ", true),
            new PointGroup("D3d", "(i) (C3) 3*(C2) (S6) 3*(sigma) ", true),
            new PointGroup("D4d", "(C4) 5*(C2) (S8) 4*(sigma) ", true),
            new PointGroup("D5d", "(i) (C5) 5*(C2) (S10) 5*(sigma) ", true),
            new PointGroup("D6d", "(C6) (C3) 7*(C2) (S12) (S4) 6*(sigma) ",
                true),
            new PointGroup("D7d", "(i) (C7) 7*(C2) (S14) 7*(sigma) ", true),
            new PointGroup("D8d", "(C8) (C4) 9*(C2) (S16) 8*(sigma) ", true),
            new PointGroup("S4", "(C2) (S4) ", true),
            new PointGroup("S6", "(i) (C3) (S6) ", true),
            new PointGroup("S8", "(C4) (C2) (S8) ", true),
            new PointGroup("T", "4*(C3) 3*(C2) ", true),
            new PointGroup("Th", "(i) 4*(C3) 3*(C2) 4*(S6) 3*(sigma) ", true),
            new PointGroup("Td", "4*(C3) 3*(C2) 3*(S4) 6*(sigma) ", true),
            new PointGroup("O", "3*(C4) 4*(C3) 9*(C2) ", true),
            new PointGroup("Oh",
                "(i) 3*(C4) 4*(C3) 9*(C2) 4*(S6) 3*(S4) 9*(sigma) ", true),
            new PointGroup("Cinfv", "(Cinf) (sigma) ", true),
            new PointGroup("Dinfh", "(i) (Cinf) (C2) 2*(sigma) ", true),
            new PointGroup("I", "6*(C5) 10*(C3) 15*(C2) ", true),
            new PointGroup("Ih",
                "(i) 6*(C5) 10*(C3) 15*(C2) 6*(S10) 10*(S6) 15*(sigma) ", true),
            new PointGroup("Kh", "(i) (Cinf) (sigma) ", true)
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private Map<String, PointGroup> pointGroups;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomIntInt object
     *
     */
    public PointGroups()
    {
        pointGroups = new Hashtable<String, PointGroup>(
                defaultPointGroups.length);

        for (int i = 0; i < defaultPointGroups.length; i++)
        {
            pointGroups.put(defaultPointGroups[i].getGroupName(),
                defaultPointGroups[i]);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
