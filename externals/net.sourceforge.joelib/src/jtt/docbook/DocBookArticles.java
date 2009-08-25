///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DocBookArticles.java,v $
//  Purpose:  Descriptor base class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:42 $
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
package jtt.docbook;

import wsi.ra.tool.BasicPropertyHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Category;
import org.apache.tools.ant.DirectoryScanner;


/*==========================================================================*
 * IMPORTS
 *==========================================================================  */
/*==========================================================================*
 * INTERFACE DECLARATION
 *==========================================================================  */

/**
 * Interface for defining a decimal formatter.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:42 $
 */
public class DocBookArticles
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(
            "jtt.docbook.DocBookArticles");

    //~ Instance fields ////////////////////////////////////////////////////////

    private String baseDir;
    private String dtd;
    private String fileExtension;
    private String outputDir;

    private BasicPropertyHolder propertyHolder;
    private String version;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DocBookArticles() throws Exception
    {
        propertyHolder = BasicPropertyHolder.instance();

        if (!loadParameters())
        {
            throw new Exception("Could not get all parameters.");
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
      *  The main program for the TestSmarts class
      *
      * @param  args  The command line arguments
      */
    public static void main(String[] args)
    {
        DocBookArticles createArticles;

        try
        {
            createArticles = new DocBookArticles();
            createArticles.apply(args);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    public void apply(String[] args)
    {
        baseDir = args[0];
        outputDir = args[1];

        String[] files = getFileList();

        //System.out.println("FILES:");
        int index;

        for (int i = 0; i < files.length; i++)
        {
            //System.out.println(files[i]);
            index = files[i].indexOf(".");
            generateArticleFile("..", files[i].substring(0, index), args[2]);

            //createArticle(files[i].substring(0,index));
        }
    }

    public void createArticle(String name)
    {
        try
        {
            Jade jade = new Jade();

            //System.out.println("execute jade on "+outputDir + "/" +  name + "."+fileExtension);
            jade.execute(outputDir, "sgml",

                //outputDir + "/" +
                name + "." + fileExtension);
            jade.execute(outputDir, "rtf",

                //outputDir + "/" +
                name + "." + fileExtension);

            //jade.execute(outputDir,"xml",
            //outputDir + "/" +  name + "."+fileExtension);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void generateArticleFile(String directory, String name,
        String bibliography)
    {
        //              System.out.println(name);
        //              System.out.println("---");
        FileOutputStream os = null;
        PrintStream ps = null;

        try
        {
            os = new FileOutputStream(outputDir + "/" + name + "." +
                    fileExtension);
            ps = new PrintStream(os);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        ps.print("<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook ");
        ps.print(version);
        ps.print("//EN\" \"");
        ps.print(dtd);
        ps.println("\" [");
        ps.print("  <!ENTITY ");
        ps.print(name);
        ps.print(" SYSTEM \"");
        ps.print(directory);
        ps.print('/');
        ps.print(name);
        ps.print('.');
        ps.print(fileExtension);
        ps.println("\">");

        if (bibliography != null)
        {
            ps.print("  <!ENTITY bibliography SYSTEM \"");
            ps.print(bibliography);
            ps.println("\">");
        }

        ps.println("]>");
        ps.println("<article>");

        ps.print("  <?dbhtml filename='");
        ps.print(name);
        ps.println(".html' output-dir='.'>");
        ps.print("  <sect1 id=\"joelib2.descriptor");

        //ps.print('.');
        //ps.print(name);
        ps.println("\">");
        ps.println("  <title>Descriptor</title>");
        ps.print("  &");
        ps.print(name);
        ps.println(";");
        ps.println("  </sect1>");

        if (bibliography != null)
        {
            ps.println("  &bibliography;");
        }

        ps.println("</article>");

        try
        {
            ps.close();
            os.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String[] getFileList()
    {
        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = {"*." + fileExtension};
        ds.setIncludes(includes);

        //System.out.println("BASEDIR: " + baseDir);
        ds.setBasedir(new File(baseDir));
        ds.setCaseSensitive(true);
        ds.scan();

        return ds.getIncludedFiles();
    }

    /**
      *  Description of the Method
      *
      * @return    Description of the Return Value
      */
    public boolean loadParameters() throws Exception
    {
        String value;

        if ((value = propertyHolder.getProperty(this, "version")) == null)
        {
            logger.error("DocBook version not defined.");

            return false;
        }
        else
        {
            version = value;
        }

        if ((value = propertyHolder.getProperty(this, "fileExtension")) == null)
        {
            logger.error("File extension not defined.");

            return false;
        }
        else
        {
            fileExtension = value;
        }

        if ((value = propertyHolder.getProperty(this, "DTD")) == null)
        {
            logger.error("File extension not defined.");

            return false;
        }
        else
        {
            dtd = value;
        }

        return true;
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
            "\n\nThis is version $Revision: 1.7 $ ($Date: 2005/02/17 16:48:42 $)\n");

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
