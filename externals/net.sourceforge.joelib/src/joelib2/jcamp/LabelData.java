///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: LabelData.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.jcamp;

/**
 * Label/Data entry for JCAMP format.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 * @.cite dl93
 * @.cite dw88
 * @.cite ghhjs91
 * @.cite lhdl94
 * @.cite dhl90
 */
public class LabelData
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public String data;
    public String label;

    //~ Constructors ///////////////////////////////////////////////////////////

    public LabelData()
    {
        label = "";
        data = "";
    }

    public LabelData(String sl, String sd)
    {
        label = sl;
        data = sd;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public final String getData()
    {
        return data;
    }

    public final String getLabel()
    {
        return label;
    }

    public final void setData(String s)
    {
        data = s;
    }

    public final void setLabel(String s)
    {
        label = s;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
