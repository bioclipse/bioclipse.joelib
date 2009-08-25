///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicFeatureDescription.java,v $
//  Purpose:  Descriptor helper methods.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.4 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.feature;

import wsi.ra.tool.BasicResourceLoader;

import java.net.URL;


/**
 * Access class to the descriptor descripton files.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.4 $, $Date: 2005/02/17 16:48:29 $
 */
public class BasicFeatureDescription implements FeatureDescription
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private String basePath;
    private String html;
    private String text;
    private String xml;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Initialize the access class to the descriptor descripton files.
     *
     * @param myBasePath
     *            Description of the Parameter
     */
    public BasicFeatureDescription(String myBasePath)
    {
        basePath = myBasePath;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets base file to the descriptor description files.
     *
     * @return base file to the descriptor description files
     */
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Gets the <b>HTML </b> description for the descriptor.
     *
     * @return the <b>HTML </b> description for the descriptor
     */
    public String getHtml()
    {
        String docu = null;

        if (html != null)
        {
            docu = html;
        }
        else
        {
            byte[] bytes = BasicResourceLoader.instance()
                                              .getBytesFromResourceLocation(
                    basePath + ".html");

            if (bytes == null)
            {
                bytes = BasicResourceLoader.instance()
                                           .getBytesFromResourceLocation(
                        basePath + ".htm");
            }

            if (bytes != null)
            {
                html = String.valueOf(bytes);

                docu = html;
            }
        }

        return docu;
    }

    /**
     * Gets the <b>text </b> description for the descriptor.
     *
     * @return the <b>text </b> description for the descriptor
     */
    public String getText()
    {
        String docu = null;

        if (text != null)
        {
            docu = text;
        }
        else
        {
            //    System.out.println("load: "+descriptionFile + ".txt");
            byte[] bytes = BasicResourceLoader.instance()
                                              .getBytesFromResourceLocation(
                    basePath + ".txt");

            if (bytes != null)
            {
                text = String.valueOf(bytes);

                docu = text;
            }
        }

        return docu;
    }

    /**
     * Gets the <b>XML </b> description for the descriptor.
     *
     * @return the <b>XML </b> description for the descriptor
     */
    public String getXml()
    {
        String docu = null;

        if (xml != null)
        {
            docu = xml;
        }
        else
        {
            byte[] bytes = BasicResourceLoader.instance()
                                              .getBytesFromResourceLocation(
                    basePath + ".xml");

            if (bytes != null)
            {
                xml = String.valueOf(bytes);

                docu = xml;
            }
        }

        return docu;
    }

    /**
     * Returns <tt>true</tt> if a <b>HTML </b> description file for the
     * descriptor exists.
     *
     * @return <tt>true</tt> if a <b>HTML </b> description file for the
     *         descriptor exists
     */
    public final boolean hasHtml()
    {
        URL location = ClassLoader.getSystemResource(basePath + ".html");

        if (location == null)
        {
            // try again for web start applications
            location = this.getClass().getClassLoader().getResource(basePath +
                    ".html");
        }

        if (location == null)
        {
            location = ClassLoader.getSystemResource(basePath + ".htm");
        }

        if (location == null)
        {
            // try again for web start applications
            location = this.getClass().getClassLoader().getResource(basePath +
                    ".htm");
        }

        return (location != null);
    }

    /**
     * Returns <tt>true</tt> if a <b>text </b> description file for the
     * descriptor exists.
     *
     * @return <tt>true</tt> if a <b>text </b> description file for the
     *         descriptor exists
     */
    public final boolean hasText()
    {
        URL location = ClassLoader.getSystemResource(basePath + ".txt");

        if (location == null)
        {
            // try again for web start applications
            location = this.getClass().getClassLoader().getResource(basePath +
                    ".txt");
        }

        return (location != null);
    }

    /**
     * Returns <tt>true</tt> if a <b>XML </b> description file for the
     * descriptor exists.
     *
     * @return <tt>true</tt> if a <b>XML </b> description file for the
     *         descriptor exists
     */
    public final boolean hasXml()
    {
        URL location = ClassLoader.getSystemResource(basePath + ".xml");

        if (location == null)
        {
            // try again for web start applications
            location = this.getClass().getClassLoader().getResource(basePath +
                    ".xml");
        }

        return (location != null);
    }

    /**
     * Sets base file to the descriptor description files.
     *
     * @param _docuFile
     *            base file to the descriptor description files
     */
    public void setBasePath(String myBasePath)
    {
        basePath = myBasePath;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
