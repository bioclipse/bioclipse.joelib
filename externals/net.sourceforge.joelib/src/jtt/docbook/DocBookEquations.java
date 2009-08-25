///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DocBookEquations.java,v $
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

import jtt.util.Executable;

import wsi.ra.tool.BasicPropertyHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;

import java.util.StringTokenizer;

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
public class DocBookEquations
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(
            "jtt.docbook.DocBookEquations");

    //~ Instance fields ////////////////////////////////////////////////////////

    private String baseDir;
    private String convert;
    private String defaultOutputType = "gif";
    private String dvips;
    private boolean equationBold;
    private boolean equationLarge;
    private String fileExtension;
    private int fontSize;
    private String latex;

    private BasicPropertyHolder propertyHolder;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DocBookEquations() throws Exception
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
        DocBookEquations createEquations;

        try
        {
            createEquations = new DocBookEquations();
            createEquations.apply(args);
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

        String[] files = getFileList();

        //System.out.println("FILES:");
        for (int i = 0; i < files.length; i++)
        {
            //System.out.println(files[i]);
            readFile(baseDir + "/" + files[i]);
        }
    }

    public void createImage(String directory, String file, String equation,
        String outputType)
    {
        //              System.out.println("---");
        //              System.out.println(directory);
        //              System.out.println(file);
        //              System.out.println(equation);
        //              System.out.println("---");
        File fileE = new File(file + "." + outputType);
        System.out.println("file: " + file + "." + outputType + " exists: " +
            fileE.exists());

        //if(fileE.exists())return;
        boolean useAnt = true;

        if (useAnt)
        {
            String[] args = new String[]{file + ".tex"};
            Executable.execute(baseDir + "/" + directory, latex, args);

            args = new String[]{"-E", file + ".dvi", "-o", file + ".eps"};
            Executable.execute(baseDir + "/" + directory, dvips, args);
            args =
                new String[]
                {
                    "-antialias", file + ".eps", file + "." + outputType
                };
            Executable.execute(baseDir + "/" + directory, convert, args);
        }
        else
        {
            String[] args = new String[]{latex, file + ".tex"};
            Executable.execute(args, false);
            args =
                new String[]
                {
                    dvips, "-E",
                    baseDir + "/" + directory + "/" + file + ".dvi",
                    file + ".dvi", "-o",

                    baseDir + "/" + directory + "/" + file + file + ".eps"
                };
            Executable.execute(baseDir + "/" + directory, dvips, args);
            args =
                new String[]
                {
                    convert, "-antialias",

                    baseDir + "/" + directory + "/" + file + ".eps",

                    baseDir + "/" + directory + "/" + file + "." + outputType
                };
            Executable.execute(args, true);
        }
    }

    public void generateLatexFile(String directory, String file,
        String equation)
    {
        //              System.out.println("---");
        //              System.out.println(directory);
        //              System.out.println(file);
        //              System.out.println(equation);
        //              System.out.println("---");
        FileOutputStream os = null;
        PrintStream ps = null;

        try
        {
            os = new FileOutputStream(baseDir + "/" + directory + "/" + file +
                    ".tex");
            ps = new PrintStream(os);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        ps.print("\\documentclass[");
        ps.print(fontSize);
        ps.println("]{article}");
        ps.println("\\pagestyle{empty}\n\\begin{document}");

        if (equationLarge)
        {
            ps.println("\\large");
        }

        ps.println("\\begin{eqnarray}");

        if (equationBold)
        {
            ps.print("\\bf");
        }

        ps.println(equation);
        ps.println("\\end{eqnarray}\n\\end{document}");

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
        String[] includes = {"**\\*." + fileExtension};
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

        if ((value = propertyHolder.getProperty(this, "latex")) == null)
        {
            logger.error("LaTeX executable not defined.");

            return false;
        }
        else
        {
            latex = value;
        }

        if ((value = propertyHolder.getProperty(this, "dvips")) == null)
        {
            logger.error("DviPs executable not defined.");

            return false;
        }
        else
        {
            dvips = value;
        }

        if ((value = propertyHolder.getProperty(this, "convert")) == null)
        {
            logger.error("ImageMagick's convert executable not defined.");

            return false;
        }
        else
        {
            convert = value;
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

        value = BasicPropertyHolder.instance().getProperty(this, "bold");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            equationBold = true;
        }
        else
        {
            equationBold = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this, "large");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            equationLarge = true;
        }
        else
        {
            equationLarge = false;
        }

        fontSize = propertyHolder.getInt(this, "fontSize", 12);

        return true;
    }

    public boolean parseEquation(String equation)
    {
        if (equation.startsWith("<!--"))
        {
            if (equation.endsWith("-->"))
            {
                int index = equation.indexOf(":");

                if (index == -1)
                {
                    logger.error("LATEXEQUATION ':' delimiter not found.");
                    logger.error(
                        "<!-- LATEXEQUATION directory file: equation -->");

                    return false;
                }

                String equ = equation.substring(index + 1,
                        equation.length() - 3);
                StringTokenizer st = new StringTokenizer(equation.substring(4,
                            index), " \t");

                if (st.hasMoreTokens())
                {
                    if (!st.nextToken().equalsIgnoreCase("LATEXEQUATION"))
                    {
                        logger.error(
                            "LATEXEQUATION key word expected as first entry.");
                        logger.error(
                            "<!-- LATEXEQUATION directory file: equation -->");
                    }
                }

                String dir = null;

                if (st.hasMoreTokens())
                {
                    dir = st.nextToken();
                }
                else
                {
                    logger.error("directory is missing.");
                    logger.error(
                        "<!-- LATEXEQUATION directory file: equation -->");
                }

                String file = null;

                if (st.hasMoreTokens())
                {
                    file = st.nextToken();
                }
                else
                {
                    logger.error("file is missing.");
                    logger.error(
                        "<!-- LATEXEQUATION directory file: equation -->");
                }

                generateLatexFile(dir, file, equ);
                createImage(dir, file, equ, defaultOutputType);
            }
            else
            {
                logger.error("LATEXEQUATION should end with -->");

                return false;
            }
        }
        else
        {
            logger.error("LATEXEQUATION should start with <!--");

            return false;
        }

        return true;
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

        //FileInputStream is = null;
        try
        {
            String line;

            //            int index;
            while ((line = lnr.readLine()) != null)
            {
                if (line.indexOf("LATEXEQUATION") != -1)
                {
                    //System.out.println(line);
                    if (!parseEquation(line))
                    {
                        logger.error("in file " + file);
                    }
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
