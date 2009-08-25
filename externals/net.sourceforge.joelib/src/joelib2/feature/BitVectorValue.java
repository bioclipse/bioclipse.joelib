///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BitVectorValue.java,v $
//  Purpose:  Interface to have a fast method to binary descriptor values.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.feature;

import joelib2.util.BitVector;


/**
 * Interface to have a fast method to getting binary (bit string) descriptor values. This
 * is a better
 * method than checking class names and forces the developer to implement fast
 * accesing methods without class casting methods.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:29 $
 */
public interface BitVectorValue
{
    //~ Methods ////////////////////////////////////////////////////////////////

    BitVector getBinaryValue();

    // that's a problem inBitResult, because the number of max bits is not set !?
    // use simple the highest set bit ???
    // solve that problem later !;-)
    //public void setBinaryValue(JOEBitVec _value);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
