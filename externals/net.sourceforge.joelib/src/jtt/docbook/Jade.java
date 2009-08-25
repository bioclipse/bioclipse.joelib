///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Jade.java,v $
//  Purpose:  Calls corina to create 3D structures.
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
///////////////////////////////////////////////////////////////////////////////
package jtt.docbook;

import jtt.util.Executable;

import wsi.ra.tool.BasicPropertyHolder;

import org.apache.log4j.Category;


/**
 *  Calls corina to create 3D structures.
 *
 * @.author     wegnerj
 */
public class Jade
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance("jtt.docbook.Jade");

    //~ Instance fields ////////////////////////////////////////////////////////

    //private String sgmlCatalogFiles;
    private boolean createIndex;
    private String dssslHTML;
    private String dssslRTF;
    private String jade;

    private BasicPropertyHolder propertyHolder;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Corina object
     */
    public Jade() throws Exception
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
        Jade jade;

        try
        {
            jade = new Jade();
            jade.execute(args[0], args[1], args[2]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    /**
     * @param string
     */
    public void execute(String dir, String outputType, String sgmlFile)
    {
        String dsssl = null;

        if (outputType.equalsIgnoreCase("RTF"))
        {
            dsssl = dssslRTF;
        }
        else if (outputType.equalsIgnoreCase("HTML"))
        {
            dsssl = dssslHTML;
        }

        String[] args;

        if (dsssl == null)
        {
            if (createIndex)
            {
                args =
                    new String[]
                    {
                        "-t", outputType, "-V", "html-index", sgmlFile
                    };
            }
            else
            {
                args = new String[]{"-t", outputType, sgmlFile};
            }
        }
        else
        {
            if (createIndex)
            {
                args =
                    new String[]
                    {
                        "-t", outputType, "-d", dsssl, "-V", "html-index",
                        sgmlFile
                    };
            }
            else
            {
                args = new String[]{"-t", outputType, "-d", dsssl, sgmlFile};
            }
        }

        Executable.execute(dir, jade, args);

        //Executable.execute(args, true);
    }

    /*-------------------------------------------------------------------------*
     * public static methods
     *-------------------------------------------------------------------------   */

    /**
      *  Description of the Method
      *
      * @return    Description of the Return Value
      */
    public boolean loadParameters() throws Exception
    {
        String value;

        if ((value = propertyHolder.getProperty(this, "jade")) == null)
        {
            logger.error("jade executable not defined.");

            return false;
        }
        else
        {
            jade = value;
        }

        if ((value = propertyHolder.getProperty(this, "DSSSL.rtf")) == null)
        {
            logger.error("DSSSL definition for RTF not defined.");

            return false;
        }
        else
        {
            dssslRTF = value;
        }

        if ((value = propertyHolder.getProperty(this, "DSSSL.html")) == null)
        {
            logger.error("DSSSL definition for HTML not defined.");

            return false;
        }
        else
        {
            dssslHTML = value;
        }

        //        if ((value = propertyHolder.getProperty(this, "SGML.catalg.files")) == null)
        //        {
        //            logger.error("SGML catalog files not defined.");
        //
        //            return false;
        //        }
        //        else
        //        {
        //            sgmlCatalogFiles = value;
        //        }
        value = BasicPropertyHolder.instance().getProperty(this, "index");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            createIndex = true;
        }
        else
        {
            createIndex = false;
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
