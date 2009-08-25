///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CDOInterface.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//                      egonw@sci.kun.nl, wegner@users.sourceforge.net
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
 * Chemical Markup Language (CML) interface definition.
 *
 * @.author egonw
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:35 $
 */
public interface CDOInterface
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * The next procedure must be implemented by each CDO and
     * return a CDOAcceptedObjects class with the names of the
     * objects that can be handled.
     **/
    public CDOAcceptedObjects acceptObjects();

    /**
     * Called just after XML parsing has ended.
     */
    public void endDocument();

    /**
     * End the process of adding a new object to the CDO of a certain type.
     *
     * @param objectType  Type of the object being added.
     */
    public void endObject(String objectType);

    /**
     * Sets a property for this document.
     *
     * @param type  Type of the property.
     * @param value Value of the property.
     */
    public void setDocumentProperty(String type, Object value);

    /**
     * Sets a property of the object being added.
     *
     * @param objectType          Type of the object being added.
     * @param propertyType        Type of the property being set.
     * @param propertyValue       Value of the property being set.
     */
    public void setObjectProperty(String objectType, String propertyType,
        Object propertyValue);

    /**
     * Called just before XML parsing is started.
     */
    public void startDocument();

    /**
     * Start the process of adding a new object to the CDO of a certain type.
     *
     * @param objectType  Type of the object being added.
     */
    public void startObject(String objectType);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
