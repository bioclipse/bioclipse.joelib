///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: QueryBond.java,v $
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
package joelib2.smarts.bondexpr;

/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:39 $
 */
public interface QueryBond
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public final static boolean FOO = false;

    /**
     *  Description of the Field
     */
    public final static boolean ORIG = false;

    //public static final boolean UNUSED    = false;

    /**
     *  Description of the Field
     */
    public final static int BE_LEAF = 0x01;

    /**
     *  Description of the Field
     */
    public final static int BE_ANDHI = 0x02;

    /**
     *  Description of the Field
     */
    public final static int BE_ANDLO = 0x03;

    /**
     *  Description of the Field
     */
    public final static int BE_NOT = 0x04;

    /**
     *  Description of the Field
     */
    public final static int BE_OR = 0x05;

    /**
     *  Description of the Field
     */
    public final static int BL_CONST = 0x01;

    /**
     *  Description of the Field
     */
    public final static int BL_TYPE = 0x02;

    /**
     *  Description of the Field
     */
    public final static int BT_SINGLE = 0x01;

    /**
     *  Description of the Field
     */
    public final static int BT_DOUBLE = 0x02;

    /**
     *  Description of the Field
     */
    public final static int BT_TRIPLE = 0x03;

    /**
     *  Description of the Field
     */
    public final static int BT_AROM = 0x04;

    /**
     *  Description of the Field
     */
    public final static int BT_UP = 0x05;

    /**
     *  Description of the Field
     */
    public final static int BT_DOWN = 0x06;

    /**
     *  Description of the Field
     */
    public final static int BT_UPUNSPEC = 0x07;

    /**
     *  Description of the Field
     */
    public final static int BT_DOWNUNSPEC = 0x08;

    /**
     *  Description of the Field
     */
    public final static int BT_RING = 0x09;

    /**
     *  Description of the Field
     */
    public final static int BF_NONRINGUNSPEC = 0x0001;

    /**
     *  Description of the Field
     */
    public final static int BF_NONRINGDOWN = 0x0002;

    /**
     *  Description of the Field
     */
    public final static int BF_NONRINGUP = 0x0004;

    /**
     *  Description of the Field
     */
    public final static int BF_NONRINGDOUBLE = 0x0008;

    /**
     *  Description of the Field
     */
    public final static int BF_NONRINGTRIPLE = 0x0010;

    /**
     *  Description of the Field
     */
    public final static int BF_RINGUNSPEC = 0x0020;

    /**
     *  Description of the Field
     */
    public final static int BF_RINGDOWN = 0x0040;

    /**
     *  Description of the Field
     */
    public final static int BF_RINGUP = 0x0080;

    /**
     *  Description of the Field
     */
    public final static int BF_RINGAROM = 0x0100;

    /**
     *  Description of the Field
     */
    public final static int BF_RINGDOUBLE = 0x0200;

    /**
     *  Description of the Field
     */
    public final static int BF_RINGTRIPLE = 0x0400;

    /**
     *  Description of the Field
     */
    public final static int BS_ALL = 0x07FF;

    /**
     *  Description of the Field
     */
    public final static int BS_SINGLE = 0x00E7;

    /**
     *  Description of the Field
     */
    public final static int BS_DOUBLE = 0x0208;

    /**
     *  Description of the Field
     */
    public final static int BS_TRIPLE = 0x0410;

    /**
     *  Description of the Field
     */
    public final static int BS_AROM = 0x0100;

    /**
     *  Description of the Field
     */
    public final static int BS_UP = 0x0084;

    /**
     *  Description of the Field
     */
    public final static int BS_DOWN = 0x0042;

    /**
     *  Description of the Field
     */
    public final static int BS_UPUNSPEC = 0x00A5;

    /**
     *  Description of the Field
     */
    public final static int BS_DOWNUNSPEC = 0x0063;

    /**
     *  Description of the Field
     */
    public final static int BS_RING = 0x07E0;

    /**
     *  Description of the Field
     */
    public final static int BS_DEFAULT = 0x01E7;

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
