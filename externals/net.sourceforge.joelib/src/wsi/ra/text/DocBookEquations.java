///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DocBookEquations.java,v $
//  Purpose:  Descriptor base class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:44 $
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
package wsi.ra.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.tools.ant.DirectoryScanner;


/**
 * Interface for defining a decimal formatter.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:44 $
 */
public class DocBookEquations
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
      *  The main program for the TestSmarts class
      *
      * @param  args  The command line arguments
      */
    public static void main(String[] args)
    {
        DocBookEquations createEquations = new DocBookEquations();

        createEquations.apply(args);
        System.exit(0);
    }

    public void apply(String[] args)
    {
        String baseDir = args[0];
        String[] files = getFileList(baseDir);

        System.out.println("FILES:");

        for (int i = 0; i < files.length; i++)
        {
            //System.out.println(files[i]);
            readFile(baseDir + "/" + files[i]);
        }
    }

    public String[] getFileList(String directory)
    {
        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"**\\*.sgml"};
        ds.setIncludes(includes);
        ds.setBasedir(new File(directory));
        ds.setCaseSensitive(true);
        ds.scan();

        return ds.getIncludedFiles();
    }

    public void readFile(String file)
    {
        FileInputStream in = null;
        LineNumberReader lnr = null;

        try
        {
            in = new FileInputStream(file);
            lnr = new LineNumberReader(new InputStreamReader(in));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            String line;

            while ((line = lnr.readLine()) != null)
            {
                if (line.indexOf("LATEXEQUATION") != -1)
                {
                    System.out.println(line);
                }
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     *  Description of the Method
     */
    public void usage()
    {
        StringBuffer sb = new StringBuffer();
        String programName = this.getClass().getName();

        sb.append("\nUsage is : ");
        sb.append("java -cp . ");
        sb.append(programName);
        sb.append(" <args>");
        sb.append(
            "\n\nThis is version $Revision: 1.6 $ ($Date: 2005/02/17 16:48:44 $)\n");

        System.out.println(sb.toString());

        System.exit(0);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
