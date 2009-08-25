///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IdentifierSoftDefaultSystem.java,v $
//  Purpose:  Descriptor base class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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
package joelib2.data;

import wsi.ra.tool.BasicResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.log4j.Category;


/**
 * Interface for defining a hard coded kernel part.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:29 $
 */
public abstract class IdentifierSoftDefaultSystem
    implements IdentifierSoftDependencies
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            IdentifierSoftDefaultSystem.class.getName());
    private static final String VENDOR = "VENDOR:";
    private static final String RELEASE_VERSION = "RELEASE_VERSION:";
    private static final String RELEASE_DATE = "RELEASE_DATE:";

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Soft coded chemistry kernel initialized.
     */
    protected boolean initialized;

    /**
     *  External soft coded chemistry kernel data resource file.
     */
    protected String resourceFile;
    private int lineCounter;
    private String releaseDateExternal;
    private String releaseVersionExternal;
    private String vendorExternal;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Release date of external (soft coded) data for this expert system.
     *
     * @return Release date of external (soft coded) data for this expert system.
     */
    public String getReleaseDateExternal()
    {
        return releaseDateExternal;
    }

    /**
     * Release version of external (soft coded) data for this expert system.
     *
     * @return Release version of external (soft coded) data for this expert system.
     */
    public String getReleaseVersionExternal()
    {
        return releaseVersionExternal;
    }

    /**
     * Resource for this soft coded expert system.
     *
     * @return Resource for this soft coded expert system.
     */
    public String getResourceExternal()
    {
        return this.resourceFile;
    }

    /**
     * Vendor of external (soft coded) data for this expert system.
     *
     * @return Vendor of external (soft coded) data for this expert system.
     */
    public String getVendorExternal()
    {
        return vendorExternal;
    }

    /**
     *  Description of the Method
     *
     * @param  buffer  Description of the Parameter
     */
    protected abstract void parseLine(String buffer);

    /**
     *  Gets the lineCounter attribute of the JOEGlobalDataBase object
     *
     * @return    The lineCounter value
     */
    protected final int getLineCounter()
    {
        return lineCounter;
    }

    /**
     *  Gets the linesFromBytes attribute of the JOEGlobalDataBase object
     *
     * @param  bytes  Description of the Parameter
     */
    protected synchronized void getLinesFromBytes(byte[] bytes)
        throws RuntimeException, IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        InputStreamReader isr = new InputStreamReader(bais);
        LineNumberReader lnr = new LineNumberReader(isr);
        String nextLine;
        int index = 0;

        for (;;)
        {
            nextLine = lnr.readLine();

            if (nextLine == null)
            {
                break;
            }

            lineCounter = lnr.getLineNumber();

            if (index == 0)
            {
                parseVendor(nextLine);
            }
            else if (index == 1)
            {
                parseReleaseVersion(nextLine);
            }
            else if (index == 2)
            {
                parseReleaseDate(nextLine);
            }
            else
            {
                parseLine(nextLine);
            }

            index++;
        }
    }

    /**
     *  Description of the Method
     */
    protected synchronized void init()
    {
        if (initialized)
        {
            return;
        }

        initialized = true;

        byte[] bytes = BasicResourceLoader.instance()
                                          .getBytesFromResourceLocation(
                resourceFile);

        //    System.out.println("Loaded "+resourceFile+" sucessfull:"+(bytes!=null));
        if (bytes != null)
        {
            try
            {
                getLinesFromBytes(bytes);
            }
            catch (Exception ex)
            {
                logger.error("Problems in loading soft kernel from " +
                    resourceFile + ": " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        else
        {
            logger.error("Unable to open data file '" + resourceFile + "'");

            //throw new Exception("Unable to open data file '" + _filename +"'");
            //System.exit(1);
            initialized = false;
        }
    }

    /**
     * @param nextLine
     */
    private void parseReleaseDate(String nextLine)
    {
        if (!nextLine.startsWith(RELEASE_DATE))
        {
            throw new RuntimeException(
                "External data definition must contain a RELEASE_DATE information, but contains: '" +
                nextLine + "'");
        }
        else
        {
            String tmp = IdentifierExpertSystem.transformCVStag(nextLine
                    .substring(RELEASE_DATE.length()).trim());

            if (releaseDateExternal == null)
            {
                releaseDateExternal = tmp;
            }
            else
            {
                releaseDateExternal = releaseDateExternal + ":" + tmp;
            }
        }
    }

    /**
     * @param nextLine
     */
    private void parseReleaseVersion(String nextLine)
    {
        if (!nextLine.startsWith(RELEASE_VERSION))
        {
            throw new RuntimeException(
                "External data definition must contain a RELEASE_VERSION information, but contains: '" +
                nextLine + "'");
        }
        else
        {
            String tmp = IdentifierExpertSystem.transformCVStag(nextLine
                    .substring(RELEASE_VERSION.length()).trim());

            if (releaseVersionExternal == null)
            {
                releaseVersionExternal = tmp;
            }
            else
            {
                releaseVersionExternal = releaseVersionExternal + ":" + tmp;
            }
        }
    }

    /**
     * @param nextLine
     * @return
     */
    private void parseVendor(String nextLine)
    {
        if (!nextLine.startsWith(VENDOR))
        {
            throw new RuntimeException(
                "External data definition must contain a VENDOR information, but contains: '" +
                nextLine + "'");
        }
        else
        {
            String tmp = nextLine.substring(VENDOR.length()).trim();

            if (vendorExternal == null)
            {
                vendorExternal = tmp;
            }
            else
            {
                vendorExternal = vendorExternal + ":" + tmp;
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
