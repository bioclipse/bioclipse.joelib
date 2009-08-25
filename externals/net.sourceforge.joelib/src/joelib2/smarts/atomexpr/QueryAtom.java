///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: QueryAtom.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.9 $
//          $Date: 2005/02/17 16:48:39 $
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
package joelib2.smarts.atomexpr;

/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:39 $
 */
public interface QueryAtom
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public final static boolean OLDCODE = false;

    /**
     *  Description of the Field
     */
    public final static int AE_LEAF = 0x01;

    /**
     *  Description of the Field
     */
    public final static int AE_RECUR = 0x02;

    /**
     *  Description of the Field
     */
    public final static int AE_NOT = 0x03;

    /**
     *  Description of the Field
     */
    public final static int AE_ANDHI = 0x04;

    /**
     *  Description of the Field
     */
    public final static int AE_OR = 0x05;

    /**
     *  Description of the Field
     */
    public final static int AE_ANDLO = 0x06;

    /**
     *  Description of the Field
     */
    public final static int AL_CONST = 0x01;

    /**
     *  Description of the Field
     */
    public final static int AL_MASS = 0x02;

    /**
     *  Description of the Field
     */
    public final static int AL_AROM = 0x03;

    /**
     *  Description of the Field
     */
    public final static int AL_ELEM = 0x04;

    /**
     *  Description of the Field
     */
    public final static int AL_HCOUNT = 0x05;

    /**
     * Degree of an atom regarding only heavy atoms, in SMARTS: Q.
     */
    public final static int AL_HEAVY_CONNECT = 0x06;

    /**
     *  Description of the Field
     */
    public final static int AL_NEGATIVE = 0x07;

    /**
     *  Description of the Field
     */
    public final static int AL_POSITIVE = 0x08;

    /**
     *  Description of the Field
     */
    public final static int AL_CONNECT = 0x09;

    /**
     * Degree of an atom (including explicit H), in SMARTS: D.
     */
    public final static int AL_DEGREE = 0x0a;

    /**
     *  Description of the Field
     */
    public final static int AL_IMPLICIT = 0x0b;

    /**
     *  Description of the Field
     */
    public final static int AL_RINGS = 0x0c;

    /**
     *  Description of the Field
     */
    public final static int AL_SIZE = 0x0d;

    /**
     *  Description of the Field
     */
    public final static int AL_VALENCE = 0x0e;

    /**
     *  Description of the Field
     */
    public final static int AL_CHIRAL = 0x0f;

    /**
     *  Description of the Field
     */
    public final static int AL_HYB = 0x10;

    /**
     *  Description of the Field
     */
    public final static int AL_GROUP = 0x11;

    /**
     *  Description of the Field
     */
    public final static int AL_ELECTRONEGATIVE = 0x12;

    /**
     *  Description of the Field
     */
    public final static int AL_CLOCKWISE = 1;

    /**
     *  Description of the Field
     */
    public final static int AL_ANTICLOCKWISE = 2;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the type.
     */
    int getType();

    /**
     * @param type The type to set.
     */
    void setType(int type);

    String toString();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
