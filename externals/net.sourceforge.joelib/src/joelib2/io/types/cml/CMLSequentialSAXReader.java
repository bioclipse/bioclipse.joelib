///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: CMLSequentialSAXReader.java,v $
//  Purpose:  Reader/Writer for CML files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:35 $
//            $Author: wegner $
//  Original Author: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
//  Original Version: Chemical Development Kit,  http://sourceforge.net/projects/cdk
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
package joelib2.io.types.cml;

import joelib2.io.MoleculeCallback;

import joelib2.molecule.Molecule;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Category;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;


/**
 * Sequential reader for Chemical Markup Language (CML) files.
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
public class CMLSequentialSAXReader
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.CMLSequentialSAXReader");

    //~ Instance fields ////////////////////////////////////////////////////////

    private MoleculeFileCDO cdo;
    private String dtdResourceDir;

    private InputStreamReader isr;
    private XMLReader parser;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void initReader(InputStream iStream,
        MoleculeCallback moleculeCallback) throws IOException
    {
        isr = new InputStreamReader(iStream);

        boolean success = false;

        // If Aelfred is not available try Xerces
        if (!success)
        {
            try
            {
                parser = new org.apache.xerces.parsers.SAXParser();
                logger.info("Using Xerces XML parser.");
                success = true;
            }
            catch (Exception e)
            {
                throw new IOException("Could not instantiate any XML parser!");
            }
        }

        cdo = new MoleculeFileCDO();
        cdo.setMoleculeCallback(moleculeCallback);

        try
        {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        }
        catch (SAXException e)
        {
            logger.warn("Cannot deactivate validation.");
        }

        parser.setContentHandler(new CMLHandler((CDOInterface) cdo));
        parser.setEntityResolver(new CMLResolver());
        parser.setErrorHandler(new CMLErrorHandler());
    }

    public synchronized boolean read(Molecule mol) throws IOException
    {
        cdo.setMolecule(mol);

        try
        {
            parser.parse(new InputSource(isr));
        }
        catch (IOException e)
        {
            logger.warn("IOException: " + e.toString());
        }
        catch (SAXParseException saxe)
        {
            SAXParseException spe = (SAXParseException) saxe;
            String error = "Found well-formedness error in line " +
                spe.getLineNumber();
            logger.error(error);
        }
        catch (SAXException saxe)
        {
            logger.warn("SAXException: " + saxe.getClass().getName());
            logger.warn(saxe.toString());
            saxe.printStackTrace();
        }

        return false;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
