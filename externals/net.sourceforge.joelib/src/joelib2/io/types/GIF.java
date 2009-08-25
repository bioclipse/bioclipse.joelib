///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GIF.java,v $
//  Purpose:  Reader/Writer for Undefined files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
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
package joelib2.io.types;

import Acme.JPM.Encoders.GifEncoder;

import joelib2.io.BasicImageWriter;

import java.awt.Image;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Writer for a CompuServe Graphics Interchange (GIF) image.
 *
 * @.author     wegnerj
 * @.wikipedia  GIF
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:34 $
 */
public class GIF extends BasicImageWriter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */

    //    private static Category logger = Category.getInstance("joelib2.io.types.GIF");
    private static final String description =
        "CompuServe Graphics Interchange (GIF) image";
    private static final String[] extensions = new String[]{"gif"};

    //~ Methods ////////////////////////////////////////////////////////////////

    public String outputDescription()
    {
        return description;
    }

    public String[] outputFileExtensions()
    {
        return extensions;
    }

    public boolean writeImage(Image image, OutputStream os) throws IOException
    {
        GifEncoder gc = new GifEncoder(image, os, true);
        gc.encode();

        return (true);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
