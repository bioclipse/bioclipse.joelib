///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SymCoordinates.java,v $
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

/**
 * SymmetryElement.
 *
 * @.author     Serguei Patchkovskii
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:35 $
 */
public class SymCoordinates
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final int DIMENSION = 3;

    //~ Instance fields ////////////////////////////////////////////////////////

    public double[] coord = new double[DIMENSION];

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomIntInt object
     *
     */
    public SymCoordinates()
    {
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
