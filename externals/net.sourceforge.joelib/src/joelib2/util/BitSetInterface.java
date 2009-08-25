///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: BitSetInterface.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 15, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.3 $
//          $Date: 2005/02/17 16:48:41 $
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
package joelib2.util;

import java.util.BitSet;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.3 $, $Date: 2005/02/17 16:48:41 $
 */
public interface BitSetInterface
{
    //~ Methods ////////////////////////////////////////////////////////////////

    void and(BitSet set);

    void andNot(BitSet set);

    int cardinality();

    void clear();

    void clear(int bitIndex);

    void clear(int fromIndex, int toIndex);

    Object clone();

    boolean equals(Object obj);

    void flip(int bitIndex);

    void flip(int fromIndex, int toIndex);

    boolean get(int bitIndex);

    BitSet get(int fromIndex, int toIndex);

    int hashCode();

    boolean intersects(BitSet set);

    boolean isEmpty();

    int length();

    int nextClearBit(int fromIndex);

    int nextSetBit(int fromIndex);

    void or(BitSet set);

    void set(int bitIndex);

    void set(int bitIndex, boolean value);

    void set(int fromIndex, int toIndex);

    void set(int fromIndex, int toIndex, boolean value);

    int size();

    String toString();

    void xor(BitSet set);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
