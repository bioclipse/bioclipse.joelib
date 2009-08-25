///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Set.java,v $
//  Purpose:  Clique detection.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.1 $
//            $Date: 2006/03/03 07:13:24 $
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
package joelib2.algo.clique;



/**
 * Small self-running class used to test the native methods of the 'oelib' interface.
 */
public class Set
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public int[] vertex;
    public int size;

    //~ Constructors ///////////////////////////////////////////////////////////
    public Set(int _size)
    {
        size = 0;
        vertex = new int[_size];
    }

    //~ Methods ////////////////////////////////////////////////////////////////
    static public Set clone(final Set from, Set to)
    {
        to.size = from.size;

        for (int i = 0; i < from.size; i++)
        {
            to.vertex[i] = from.vertex[i];
        }

        return to;
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
