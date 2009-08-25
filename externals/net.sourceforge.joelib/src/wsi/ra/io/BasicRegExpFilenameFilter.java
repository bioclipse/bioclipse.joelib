///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicRegExpFilenameFilter.java,v $
//  Purpose:  Regular expression filter for files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/01/26 12:07:32 $
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
package wsi.ra.io;

import java.io.File;
import java.io.FilenameFilter;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Regular expression filter for files.
 */
public class BasicRegExpFilenameFilter implements FilenameFilter,
    RegExpFilenameFilter
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Pattern pattern;

    private List skipIfExtension;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicRegExpFilenameFilter(String regExp)
    {
        pattern = Pattern.compile(regExp);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean accept(File dir, String name)
    {
        Matcher m;
        m = pattern.matcher(name);

        if (m.matches())
        {
            if (skipIfExtension != null)
            {
                int size = skipIfExtension.size();

                for (int i = 0; i < size; i++)
                {
                    if (name.endsWith((String) skipIfExtension.get(i)))
                    {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    public void addSkipExtension(String extension)
    {
        if (skipIfExtension == null)
        {
            skipIfExtension = new Vector();
        }

        skipIfExtension.add(extension);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
