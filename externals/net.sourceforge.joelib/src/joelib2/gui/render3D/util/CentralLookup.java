///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: CentralLookup.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:34 $
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
package joelib2.gui.render3D.util;

import java.util.Hashtable;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:34 $
 */
public class CentralLookup
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static CentralLookup lookup = null;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable objectTable;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the CentralLookup object
     */
    CentralLookup()
    {
        objectTable = new Hashtable();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the lookup attribute of the CentralLookup class
     *
     * @return   The lookup value
     */
    public static CentralLookup getLookup()
    {
        if (lookup == null)
        {
            init();
        }

        return lookup;
    }

    /**
     * Adds a feature to the Object attribute of the CentralLookup object
     *
     * @param name    The feature to be added to the Object attribute
     * @param object  The feature to be added to the Object attribute
     */
    public void addObject(String name, Object object)
    {
        objectTable.put(name, object);
    }

    /**
     * Gets the object attribute of the CentralLookup object
     *
     * @param name  Description of the Parameter
     * @return      The object value
     */
    public Object getObject(String name)
    {
        return objectTable.get(name);
    }

    /**
     * Description of the Method
     */
    private static void init()
    {
        lookup = new CentralLookup();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
