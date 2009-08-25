///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CMLStack.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//                      egonw@sci.kun.nl, wegner@users.sourceforge.net
//Version:  $Revision: 1.6 $
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
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public License
//as published by the Free Software Foundation; either version 2.1
//of the License, or (at your option) any later version.
//All we ask is that proper credit is given for our work, which includes
//- but is not limited to - adding the above copyright notice to the beginning
//of your source code files, and to any copyright notice that you may distribute
//with programs based on this work.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.io.types.cml;

/**
 * Low weigth alternative to Sun's Stack class.
 *
 * @.author egonw
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 */
public class CMLStack
{
    //~ Instance fields ////////////////////////////////////////////////////////

    int sp = 0;

    String[] stack = new String[64];

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns the last added entry.
     *
     * @see #pop()
     */
    public String current()
    {
        if (sp > 0)
        {
            return stack[sp - 1];
        }
        else
        {
            return "";
        }
    }

    /**
     * Retrieves and deletes to last added entry.
     *
     * @see #current()
     */
    public String pop()
    {
        return stack[--sp];
    }

    /**
     * Adds an entry to the stack.
     */
    public void push(String item)
    {
        if (sp == stack.length)
        {
            String[] temp = new String[2 * sp];
            System.arraycopy(stack, 0, temp, 0, sp);
            stack = temp;
        }

        stack[sp++] = item;
    }

    /**
     * Returns a String representation of the stack.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("/");

        for (int i = 0; i < sp; ++i)
        {
            sb.append(stack[i]);
            sb.append("/");
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
