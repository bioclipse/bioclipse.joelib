///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DocBookMolecules.java,v $
//  Purpose:  Descriptor base class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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

import Acme.JPM.Encoders.GifEncoder;

import joelib2.gui.render2D.Mol2Image;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Molecule;

import wsi.ra.tool.BasicPropertyHolder;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Category;
import org.apache.tools.ant.DirectoryScanner;


/**
 * Interface for defining a decimal formatter.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:42 $
 */
public class DocBookMolecules
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(
            "jtt.docbook.DocBookMolecules");

    //~ Instance fields ////////////////////////////////////////////////////////

    private String baseDir;
    private String fileExtension;
    private BasicPropertyHolder propertyHolder;

    private Hashtable<String, String> summary;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DocBookMolecules() throws Exception
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
        DocBookMolecules createMolStructs;

        try
        {
            createMolStructs = new DocBookMolecules();
            createMolStructs.apply(args);
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

        //System.out.println("basedir "+baseDir);
        String[] files = getFileList();

        //System.out.println("FILES:");
        summary = new Hashtable<String, String>(files.length);

        for (int i = 0; i < files.length; i++)
        {
            //System.out.println(files[i]);
            readFile(baseDir + "/" + files[i]);
        }

        try
        {
            // build summary file
            PrintStream ps = new PrintStream(new FileOutputStream(
                        baseDir + "/" + args[1] + ".sgml"));
            Enumeration enumeration = summary.keys();
            String mol;
            String[] mols = new String[summary.size()];
            int index = 0;

            while (enumeration.hasMoreElements())
            {
                mol = (String) enumeration.nextElement();

                if (mol != null)
                {
                    mols[index] = mol;
                }
                else
                {
                    mols[index] = "";
                }

                index++;
            }

            Arrays.sort(mols);

            ps.println(
                "<appendix id=\"appendix.molecules\">\n<title>Summary of molecular structures</title>\n<?dbhtml filename='" +
                args[1] + ".html'>");

            /*ps.println("<table frame=\"all\">");
            ps.println(
                "<title>Summary of molecular structures</title>\n");
            ps.println("  <tgroup cols=\"2\">\n  <colspec colname=\"molecularstructure\">\n  <colspec colname=\"options\">\n");
            ps.println(
                "  <thead>\n    <row>\n      <entry>Molecular structure</entry>\n      <entry>Options</entry>\n    </row>\n  </thead>\n");
            ps.println("  <tbody>");

            for (int i = 0; i < mols.length; i++)
            {
                ps.println("<row>\n<entry><mediaobject>\n<imageobject>");
                ps.println("<imagedata fileref=\"" + mols[i] + ".gif" +
                    "\" format=\"GIF\" align=\"center\">");
                ps.println("</imageobject>\n</mediaobject>\n</entry>");
                ps.println("<entry><![CDATA[" + mols[i] + ": " +
                    summary.get(mols[i]) + "]]>");
                ps.println("</entry>\n</row>");
            }

            ps.println("  </tbody>\n </tgroup>\n</table>");*/
            for (int i = 0; i < mols.length; i++)
            {
                ps.println("<para><mediaobject>\n<imageobject>");
                ps.println("<imagedata fileref=\"" + mols[i] + ".gif" +
                    "\" format=\"GIF\" align=\"center\">");
                ps.println("</imageobject>\n</mediaobject>\n");
                ps.println("<![CDATA[" + mols[i] + ": " + summary.get(mols[i]) +
                    "]]>");
                ps.println("</para>");
            }

            ps.println("</appendix>");

            ps.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void createGIFimage(String structFile, String imgOutFile,
        String visOptions)
    {
        //System.out.println("structFile="+structFile+" imgOutFile="+imgOutFile+" visOptions="+visOptions);
        BasicIOType ioType = BasicIOTypeHolder.instance().filenameToType(
                structFile);
        Molecule mol = null;

        if (!summary.containsKey(imgOutFile))
        {
            summary.put(imgOutFile, visOptions);
        }

        try
        {
            mol = MoleculeFileHelper.loadMolFromFile(baseDir + "/" + structFile,
                    ioType.getName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (MoleculeIOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Create image file for " + mol);

        BufferedImage image;
        image = Mol2Image.instance().mol2image(mol,
                Mol2Image.parseOptions(visOptions));

        //      Vector opts = new Vector();
        //              JHM.tokenize(opts, visOptions);
        //        if (opts.size() != 7)
        //        {
        //            logger.error("Image options for " + structFile +
        //                " must be: width height smarts arrows orthoLines conjRings labels");
        //            logger.error("Using no options for " + structFile);
        //            image = Mol2Image.instance().mol2image(mol, mol.getTitle());
        //        }
        //        else
        //        {
        //            Mol2Image.instance().setDefaultWidth(Integer.parseInt(
        //                    (String) opts.get(0)));
        //            Mol2Image.instance().setDefaultHeight(Integer.parseInt(
        //                    (String) opts.get(1)));
        //
        //            String smartsS = (String) opts.get(2);
        //            JOESmartsPattern smarts = null;
        //
        //            smarts = new JOESmartsPattern();
        //
        //            if (!smartsS.equals(";"))
        //            {
        //                if (!smarts.init(smartsS))
        //                {
        //                    logger.error("Invalid SMARTS pattern: " + smartsS);
        //                    smarts = null;
        //                }
        //            }
        //
        //            String arrows = (String) opts.get(3);
        //            String oLines = (String) opts.get(4);
        //            String conjRing = (String) opts.get(5);
        //            String labels = (String) opts.get(6);
        //            image = Mol2Image.instance().mol2image(mol, mol.getTitle(), null,
        //                    arrows, oLines, conjRing, labels);
        //        }
        //              String title,JOESmartsPattern smarts, String eTransfer, String retroSynth,
        //                              String conjRing, String labels
        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(baseDir + "/" + imgOutFile + ".gif");
        }
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }

        GifEncoder gc;

        try
        {
            gc = new GifEncoder(image, fos, true);
            gc.encode();
        }
        catch (IOException e2)
        {
            e2.printStackTrace();
        }

        try
        {
            fos.close();
        }
        catch (IOException e3)
        {
            e3.printStackTrace();
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

        if ((value = propertyHolder.getProperty(this, "fileExtension")) == null)
        {
            logger.error("File extension not defined.");

            return false;
        }
        else
        {
            fileExtension = value;
        }

        return true;
    }

    public boolean parseMolStruct(String struct)
    {
        //System.out.println("parse "+struct);
        if (struct.indexOf("<!--") != -1)
        {
            if (struct.lastIndexOf("-->") != -1)
            {
                int index = struct.indexOf(":");

                if (index == -1)
                {
                    logger.error("MOLECULARSTRUCTURE ':' delimiter not found.");
                    logger.error(
                        "<!-- MOLECULARSTRUCTURE structureFile outputImageFile: options -->");

                    return false;
                }

                String visOptions = struct.substring(index + 1,
                        struct.lastIndexOf("-->"));

                StringTokenizer st = new StringTokenizer(struct.substring(
                            struct.indexOf("<!--") + 4, index), " \t");

                if (st.hasMoreTokens())
                {
                    if (!st.nextToken().equalsIgnoreCase("MOLECULARSTRUCTURE"))
                    {
                        logger.error(
                            "MOLECULARSTRUCTURE key word expected as first entry.");
                        logger.error(
                            "<!-- MOLECULARSTRUCTURE structureFile outputImageFile: options -->");
                        logger.error(struct);
                    }
                }

                String structFile = null;

                if (st.hasMoreTokens())
                {
                    structFile = st.nextToken();
                }
                else
                {
                    logger.error("Structure file is missing.");
                    logger.error(
                        "<!-- MOLECULARSTRUCTURE structureFile outputImageFile: options -->");
                    logger.error(struct);
                }

                String imgOutFile = null;

                if (st.hasMoreTokens())
                {
                    imgOutFile = st.nextToken();
                }
                else
                {
                    logger.error("Image output file is missing.");
                    logger.error(
                        "<!-- MOLECULARSTRUCTURE directory file: equation -->");
                    logger.error(struct);
                }

                createGIFimage(structFile, imgOutFile, visOptions);
            }
            else
            {
                logger.error("MOLECULARSTRUCTURE should end with -->");
                logger.error(struct);

                return false;
            }
        }
        else
        {
            logger.error("MOLECULARSTRUCTURE should start with <!--");
            logger.error(struct);

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
                if (line.indexOf("MOLECULARSTRUCTURE") != -1)
                {
                    //System.out.println(line);
                    if (!parseMolStruct(line))
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
        sb.append(" <baseDIR>");
        sb.append(" <summaryFile>");
        sb.append(
            "\n\nThis is version $Revision: 1.9 $ ($Date: 2005/02/17 16:48:42 $)\n");

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
