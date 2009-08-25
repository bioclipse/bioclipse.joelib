///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicBatchFileUtilities.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:43 $
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
import java.io.IOException;


/**
 * Usefull functions for file handling.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:43 $
 */
public class BasicBatchFileUtilities implements BatchFileUtilities
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static BasicBatchFileUtilities fileUtilities;

    //~ Methods ////////////////////////////////////////////////////////////////

    public static synchronized BasicBatchFileUtilities instance()
    {
        if (fileUtilities == null)
        {
            fileUtilities = new BasicBatchFileUtilities();
        }

        return fileUtilities;
    }

    /**
     * Description of the Method
     *
     * @param file             Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public synchronized boolean createLastDirectory(File file)
        throws IOException
    {
        int index = file.getAbsolutePath().lastIndexOf(System.getProperty(
                    "file.separator"));
        String path = null;

        if (index != -1)
        {
            path = file.getAbsolutePath().substring(0, index);

            File directory = new File(path);

            if (directory.exists())
            {
                if (!directory.isDirectory())
                {
                    throw new IOException("" + directory.getAbsolutePath() +
                        " is not a directory.");
                }

                return true;
            }
            else
            {
                if (createLastDirectory(directory))
                {
                    if (!directory.mkdir())
                    {
                        throw new IOException("Can not create directory " +
                            directory.getAbsolutePath());
                    }
                }

                return true;
            }
        }

        return false;
    }

    public synchronized String createNewFileName(String filename)
        throws IOException
    {
        return createNewFileName(filename, 0);
    }

    public synchronized String createNewFileName(String filename,
        int startCounter) throws IOException
    {
        File tempOutputFile = new File(filename);

        if (!tempOutputFile.exists())
        {
            if (!createLastDirectory(tempOutputFile))
            {
                return null;
            }
        }
        else
        {
            if (startCounter != -1)
            {
                int counter = startCounter;

                while (tempOutputFile.exists())
                {
                    tempOutputFile = new File(getNewFilename(
                                tempOutputFile.getAbsolutePath(), counter));
                    counter++;
                }
            }
        }

        return tempOutputFile.getAbsolutePath();
    }

    public synchronized boolean deleteFileName(String filename)
    {
        File file = new File(filename);

        return file.delete();
    }

    /**
     * Creates a new filename from a previous filename with the given counter number.
     * If the given filename contains already a '_123' number entry at the end
     * of the
     *
     * @param filename  The previous filename
     * @param counter   The number of the filename
     * @return          Description of the Return Value
     */
    private String getNewFilename(String filename, int counter)
    {
        // divide filename into name and extension
        int extIndex = filename.lastIndexOf(".");
        String name;
        String extension;

        if (extIndex != -1)
        {
            name = filename.substring(0, extIndex);
            extension = filename.substring(extIndex);
        }
        else
        {
            name = filename;
            extension = "";
        }

        // delete previous number appendix in filename
        // if it exists
        StringBuffer newFilename = new StringBuffer(100);
        int index = name.lastIndexOf("_");

        if (index != -1)
        {
            int i = index + 1;
            int l = name.length();

            while (i < l)
            {
                if (!Character.isDigit(name.charAt(i)))
                {
                    index = -1;

                    break;
                }

                i++;
            }
        }

        // build new filename
        if (index != -1)
        {
            String newF = name.substring(0, index);
            newFilename.append(newF);
        }
        else
        {
            newFilename.append(name);
        }

        newFilename.append('_');
        newFilename.append(counter);
        newFilename.append(extension);

        return newFilename.toString();
    }

    //    /**
    //     * Removes "\.." or "/.." entries from a path.
    //     * This algorithm don't work, if the path begins with "../path" or "..\path"
    //     *
    //     * @param path  the path to prettyfy
    //     * @return      the prettyfied path
    //     */
    //    public String pathPrettyfier(String path)
    //    {
    //        int index;
    //        //System.out.println("start pretty with "+path);
    //        String dummy = new String(path);
    //        while ((index = dummy.indexOf("..")) != -1)
    //        {
    //            if (index - 2 < 0)
    //            {
    //                break;
    //            }
    //            int delimiterIndex = dummy.lastIndexOf("\\", index - 2);
    //            if (delimiterIndex == -1)
    //            {
    //                delimiterIndex = dummy.lastIndexOf("/", index - 2);
    //            }
    //            if (delimiterIndex < 0)
    //            {
    //                break;
    //            }
    //            dummy = new String(dummy.substring(0, delimiterIndex) +
    //            //"#"+
    //            dummy.substring(index + 2));
    //            //System.out.println("pretty "+dummy);
    //            return dummy;
    //        }
    //        return dummy;
    //    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
