///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ConformerAtom.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.7 $
//          $Date: 2005/02/17 16:48:36 $
//          $Author: wegner $
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
package joelib2.molecule;

/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.7 $, $Date: 2005/02/17 16:48:36 $
 */
public interface ConformerAtom extends Atom
{
    //~ Methods ////////////////////////////////////////////////////////////////

    void clearCoords3Darr();

    int getCoordinateIdx();

    /**
     *  Gets the coordinate attribute of the <tt>Atom</tt> object
     *
     * @return    The coordinate value
     */
    double[] getCoords3Darr();

    /**
     *  Sets the vector of the <tt>Atom</tt> object. Copies the x,y and z
     *  values from the coordinate array into the <tt>XYZVector</tt> .
     */
    void setCoords3D();

    /**
     *  Sets the coordPtr attribute of the <tt>Atom</tt> object
     *
     * @param  coordsArray  The new coordPtr value
     */
    void setCoords3Darr(double[] coordsArray);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
