///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CDOAcceptedObjects.java,v $
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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * List of names (String classes) of objects accepted by CDO.
 *
 * @.author egonw
 * @.author c.steinbeck@uni-koeln.de
 * @.author gezelter@maul.chem.nd.edu
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 */
public class CDOAcceptedObjects
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private List objects;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Constructor.
     */
    public CDOAcceptedObjects()
    {
        objects = new Vector();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds the name of an accepted object.
     *
     * @param object Name of the object
     */
    public void add(String object)
    {
        objects.add(object);
    }

    /**
     * Determine if an object name is contained in this list.
     *
     * @param   object Name of the object to search in the list
     * @return         true if the object is in the list, false otherwise
     */
    public boolean contains(String object)
    {
        return objects.contains(object);
    }

    /**
     * Returns the names in this list as a Enumeration class. Each element in the
     * Enumeration is of type String.
     *
     * @return The names of the accepted objects
     */
    public Iterator elements()
    {
        return objects.iterator();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
