///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicResourceLoader.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner, Gerd Mueller
//  Version:  $Revision: 1.4 $
//            $Date: 2005/06/17 06:31:46 $
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
package wsi.ra.tool;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import java.net.URL;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Category;


/**
 *  Loads resource file from directory OR jar file. Now it is easier possible to
 *  access resource files in a directory structure or a .jar/.zip file.
 *
 * @.author     wegnerj
 * @.author     Robin Friedman, rfriedman@TriadTherapeutics.com
 * @.author     Gerd Mueller
 * @.license GPL
 * @.cvsversion    $Revision: 1.4 $, $Date: 2005/06/17 06:31:46 $
 */
public class BasicResourceLoader implements ResourceLoaderInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            BasicResourceLoader.class.getName());
    private static BasicResourceLoader resourceLoader;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the ResourceLoader object
     */
    private BasicResourceLoader()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicResourceLoader instance()
    {
        if (resourceLoader == null)
        {
            resourceLoader = new BasicResourceLoader();
        }

        return resourceLoader;
    }

    /**
     *  Description of the Method
     *
     * @param  resourceFile  Description of the Parameter
     * @return               Description of the Return Value
     */
    public static List readLines(String resourceFile)
    {
        return readLines(resourceFile, false);
    }

    /**
     *  Description of the Method
     *
     * @param  resourceFile    Description of the Parameter
     * @param  ignoreComments  Description of the Parameter
     * @return                 Description of the Return Value
     */
    public static List readLines(String resourceFile,
        boolean ignoreCommentedLines)
    {
        if (resourceFile == null)
        {
            return null;
        }

        byte[] bytes = BasicResourceLoader.instance()
                                          .getBytesFromResourceLocation(
                resourceFile);

        if (bytes == null)
        {
            return null;
        }

        ByteArrayInputStream sReader = new ByteArrayInputStream(bytes);
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(
                    sReader));

        String line;
        Vector vector = new Vector(100);

        try
        {
            while ((line = lnr.readLine()) != null)
            {
                if (!ignoreCommentedLines)
                {
                    if ((line.trim().length() != 0) && !(line.charAt(0) ==
                                '#'))
                    {
                        vector.add(line);

                        //                System.out.println("ADD:"+line);
                    }
                }
                else
                {
                    vector.add(line);
                }
            }
        }
        catch (IOException ex)
        {
            logger.error(ex.getMessage());
        }

        return vector;
    }

    /**
     *  Gets the byte data from a file at the given resource location.
     *
     * @param  rawResrcLoc  Description of the Parameter
     * @return                   the byte array of file.
     */
    public byte[] getBytesFromResourceLocation(String rawResrcLoc)
    {
        String resourceLocation = rawResrcLoc.replace('\\', '/');

        //System.out.println("Try to get: "+resourceLocation);
        if (resourceLocation == null)
        {
            return null;
        }

        // to avoid hours of debugging non-found-files under linux with
        // some f... special characters at the end which will not be shown
        // at the console output !!!
        resourceLocation = resourceLocation.trim();

        // is a relative path defined ?
        // this can only be possible, if this is a file resource loacation
        if (resourceLocation.startsWith("..") ||
                resourceLocation.startsWith("/") ||
                resourceLocation.startsWith("\\") ||
                ((resourceLocation.length() > 1) &&
                    (resourceLocation.charAt(1) == ':')))
        {
            return getBytesFromFile(resourceLocation);
        }

        InputStream in = this.getClass().getClassLoader()
                             .getSystemResourceAsStream(resourceLocation);

        if (in == null)
        {
            // try again for web start applications
            in = this.getClass().getClassLoader().getResourceAsStream(
                    resourceLocation);
        }

        if (in == null)
        {
            return null;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Stream opened for " + resourceLocation);
        }

        byte[] bytes = getBytesFromStream(in);

        return bytes;
    }

    /**
     *  Gets the byte data from a file contained in a JAR or ZIP file.
     *
     * @param  urlToZipArchive      Description of the Parameter
     * @param  internalArchivePath  Description of the Parameter
     * @return                      the byte array of the file.
     */
    private byte[] getBytesFromArchive(String urlToZipArchive,
        String internalArchivePath)
    {
        URL url = null;
        int size = -1;
        byte[] b = null;

        try
        {
            url = new URL(urlToZipArchive);

            // extracts just sizes only.
            ZipFile zf = new ZipFile(url.getFile());
            Enumeration e = zf.entries();

            while (e.hasMoreElements())
            {
                ZipEntry ze = (ZipEntry) e.nextElement();

                if (ze.getName().equals(internalArchivePath))
                {
                    if (ze.isDirectory())
                    {
                        return null;
                    }

                    // only files with <65536 bytes are allowed
                    if (ze.getSize() > 65536)
                    {
                        System.out.println(
                            "Resource files should be smaller than 65536 bytes...");
                    }

                    size = (int) ze.getSize();
                }
            }

            zf.close();

            FileInputStream fis = new FileInputStream(url.getFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            ZipInputStream zis = new ZipInputStream(bis);
            ZipEntry ze = null;

            while ((ze = zis.getNextEntry()) != null)
            {
                if (ze.getName().equals(internalArchivePath))
                {
                    b = new byte[(int) size];

                    int rb = 0;
                    int chunk = 0;

                    while (((int) size - rb) > 0)
                    {
                        chunk = zis.read(b, rb, (int) size - rb);

                        if (chunk == -1)
                        {
                            break;
                        }

                        rb += chunk;
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());

            return null;
        }

        return b;
    }

    /**
     *  Gets the byte data from a file.
     *
     * @param  fileName  Description of the Parameter
     * @return           the byte array of the file.
     */
    private byte[] getBytesFromFile(String fileName)
    {
        if (fileName.startsWith("/cygdrive/"))
        {
            int length = "/cygdrive/".length();
            fileName = fileName.substring(length, length + 1) + ":" +
                fileName.substring(length + 1);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Trying to get file from " + fileName);
        }

        File file = new File(fileName);
        FileInputStream fis = null;

        try
        {
            fis = new FileInputStream(file);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());

            return null;
        }

        BufferedInputStream bis = new BufferedInputStream(fis);

        // only files with <65536 bytes are allowed
        //if( file.length() > 65536 ) System.out.println("Resource files should be smaller than 65536 bytes...");
        int size = (int) file.length();
        byte[] b = new byte[size];
        int rb = 0;
        int chunk = 0;

        try
        {
            while (((int) size - rb) > 0)
            {
                chunk = bis.read(b, rb, (int) size - rb);

                if (chunk == -1)
                {
                    break;
                }

                rb += chunk;
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());

            return null;
        }

        return b;
    }

    /**
     *  Gets the byte data from a file.
     *
     * @param  fileName  Description of the Parameter
     * @return           the byte array of the file.
     */
    private byte[] getBytesFromStream(InputStream stream)
    {
        if (stream == null)
        {
            return null;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Trying to get file from stream.");
        }

        BufferedInputStream bis = new BufferedInputStream(stream);

        try
        {
            int size = (int) bis.available();
            byte[] b = new byte[size];
            int rb = 0;
            int chunk = 0;

            while (((int) size - rb) > 0)
            {
                chunk = bis.read(b, rb, (int) size - rb);

                if (chunk == -1)
                {
                    break;
                }

                rb += chunk;
            }

            return b;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());

            return null;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
