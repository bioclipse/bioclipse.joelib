///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: FeatureDescription.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.8 $
//          $Date: 2005/02/17 16:48:29 $
//          $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                     Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                     2003,2004,2005
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
package joelib2.feature;

/**
 *
 * @.author       wegner
 * @.license      GPL
 * @.cvsversion   $Revision: 1.8 $, $Date: 2005/02/17 16:48:29 $
 */
public interface FeatureDescription
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets base file to the descriptor description files.
     *
     * @return base file to the descriptor description files
     */
    String getBasePath();

    /**
     * Gets the <b>HTML </b> description for the descriptor.
     *
     * @return the <b>HTML </b> description for the descriptor
     */
    String getHtml();

    /**
     * Gets the <b>text </b> description for the descriptor.
     *
     * @return the <b>text </b> description for the descriptor
     */
    String getText();

    /**
     * Gets the <b>XML </b> description for the descriptor.
     *
     * @return the <b>XML </b> description for the descriptor
     */
    String getXml();

    /**
     * Returns <tt>true</tt> if a <b>HTML </b> description file for the
     * descriptor exists.
     *
     * @return <tt>true</tt> if a <b>HTML </b> description file for the
     *         descriptor exists
     */
    boolean hasHtml();

    /**
     * Returns <tt>true</tt> if a <b>text </b> description file for the
     * descriptor exists.
     *
     * @return <tt>true</tt> if a <b>text </b> description file for the
     *         descriptor exists
     */
    boolean hasText();

    /**
     * Returns <tt>true</tt> if a <b>XML </b> description file for the
     * descriptor exists.
     *
     * @return <tt>true</tt> if a <b>XML </b> description file for the
     *         descriptor exists
     */
    boolean hasXml();

    /**
     * Sets base file to the descriptor description files.
     *
     * @param _docuFile
     *            base file to the descriptor description files
     */
    void setBasePath(String myBasePath);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
