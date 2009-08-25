///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ViewerBonds.java,v $
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
 * Bond vector class for Java3D viewer.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:34 $
 */
public class ViewerBonds extends Vector
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Default constructor
     */
    public ViewerBonds()
    {
        super();
    }

    /**
     * Capacity constructor
     *
     * @param cap  initial capacity of vector
     */
    public ViewerBonds(int cap)
    {
        super(cap, cap);
    }

    /**
     * Capacity & increment constructor
     *
     * @param cap  initial capacity of vector
     * @param inc  increment factor
     */
    public ViewerBonds(int cap, int inc)
    {
        super(cap, inc);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Append a bond to the end of vector
     *
     * @param a  bond to be appended
     */
    public final synchronized void append(ViewerBond a)
    {
        addElement(a);
    }

    /**
     * Returns bond at specified index
     *
     * @param i                                   Description of the Parameter
     * @return                                    The bond value
     * @exception ArrayIndexOutOfBoundsException  if index >= capacity()
     */
    public final synchronized ViewerBond getBond(int i)
        throws ArrayIndexOutOfBoundsException
    {
        return (ViewerBond) elementAt(i);
    }

    /**
     * Set a bond at specified index; bond at index is replaced
     *
     * @param a  bond to be set
     * @param i  index to place bond
     */
    public final synchronized void set(ViewerBond a, int i)
    {
        setElementAt(a, i);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
