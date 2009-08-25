///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ViewerAtoms.java,v $
//  Purpose:  Molecule class for Java3D viewer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:34 $
//            $Author: wegner $
//  Original Author: Jason Plurad (jplurad@tripos.com),
//                   Mike Brusati (brusati@tripos.com)
//                   Zhidong Xie (zxie@tripos.com)
//  Original Version: ftp.tripos.com/pub/java3d/
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
package joelib2.gui.render3D.molecule;

import java.util.Vector;


/**
 * Atom vector class for Java3D viewer.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:34 $
 */
public class ViewerAtoms extends Vector
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Default constructor
     */
    public ViewerAtoms()
    {
        super();
    }

    /**
     * Capacity constructor
     *
     * @param cap  initial capacity of vector
     */
    public ViewerAtoms(int cap)
    {
        super(cap, cap);
    }

    /**
     * Capacity and increment constructor
     *
     * @param cap  initial capacity of vector
     * @param inc  increment factor
     */
    public ViewerAtoms(int cap, int inc)
    {
        super(cap, inc);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Append an atom to the end of vector
     *
     * @param a  atom to be appended
     */
    public final synchronized void append(ViewerAtom a)
    {
        addElement(a);
    }

    /**
     * Returns atom at specified index
     *
     * @param i                                   index of atom
     * @return                                    The atom value
     * @exception ArrayIndexOutOfBoundsException  if index >= capacity()
     */
    public final synchronized ViewerAtom getAtom(int i)
        throws ArrayIndexOutOfBoundsException
    {
        return (ViewerAtom) elementAt(i);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
