///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: XMLSpecialCharacter.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.7 $
//                      $Date: 2005/02/17 16:48:35 $
//                      $Author: wegner $
//
//Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
package joelib2.io.types.cml;

/**
 * Checks and converts XML special characters.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class XMLSpecialCharacter
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Converts plain strings to XML formatted string entries.
     *
     * Converts special characters to XML special characters for single line entries <b>or</b>
     * changes multiple line entries to a XML core data element.
     *
     * @param the plain string
     * @return the XML compatible string
     */
    public static String convertPlain2XML(String xmlEntry)
    {
        char amp = '&';
        int q;
        StringBuffer sb = new StringBuffer(xmlEntry);

        // store multiple line string entries as core data elements
        if (sb.toString().indexOf("\n") != -1)
        {
            sb.insert(0, "<![CDATA[");
            sb.append("]]>");

            return sb.toString();
        }

        //conversion of '&'
        q = -1;

        while ((q = sb.toString().indexOf("&", (q + 1))) != -1)
        {
            sb.insert((q + 1), "amp;");
        }

        //conversion of '<'
        while ((q = sb.toString().indexOf("<")) != -1)
        {
            sb.setCharAt(q, amp);
            sb.insert((q + 1), "lt;");
        }

        //conversion of '>'
        while ((q = sb.toString().indexOf(">")) != -1)
        {
            sb.setCharAt(q, amp);
            sb.insert((q + 1), "gt;");
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
