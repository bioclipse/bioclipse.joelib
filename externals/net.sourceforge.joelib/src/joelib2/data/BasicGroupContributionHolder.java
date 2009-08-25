///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicGroupContributionHolder.java,v $
//  Purpose:  Holds group contribution lists for different models (e.g. logP, MR, PSA).
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner, Stephen Jelfs
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:29 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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

import joelib2.algo.contribution.BasicGroupContributions;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.BasicResourceLoader;

import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Holds group contribution lists for different models (e.g. logP, MR, PSA).
 *
 * @.author     wegnerj
 * @.author  Stephen Jelfs
 * @.wikipedia Molecule
 * @.wikipedia QSAR
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:29 $
 * @see BasicPropertyHolder
 * @see BasicResourceLoader
 * @.cite ers00
 * @.cite wc99
 */
public class BasicGroupContributionHolder extends IdentifierSoftDefaultSystem
    implements IdentifierHardDependencies, GroupContributionHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            BasicGroupContributionHolder.class.getName());

    //  private final static String FILE_SEP_CHAR =System.getProperty("file.separator");
    private static BasicGroupContributionHolder instance;
    private static final String DEFAULT_RESOURCE_DIR = "joelib2/data/plain";
    private static final String DEFAULT_CONTRIBUTION = ".contributions";
    private static final String DEFAULT_MODELS = "LogP MR PSA";
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.3 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:29 $";
    private static final Class[] DEPENDENCIES = new Class[]{};

    //~ Instance fields ////////////////////////////////////////////////////////

    protected String _contribExt;
    protected byte[] _dataptr;
    protected boolean _init;
    protected String _models;
    protected String _resourceDir;
    private BasicGroupContributions actualGC;
    private Hashtable contributions;
    private boolean heavyAtoms;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEGroupContribution object
     */
    private BasicGroupContributionHolder()
    {
        _init = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        _resourceDir = prop.getProperty(this.getClass().getName() +
                ".resourceDir", DEFAULT_RESOURCE_DIR);
        _contribExt = prop.getProperty(this.getClass().getName() +
                ".contributionExtension", DEFAULT_CONTRIBUTION);
        _models = prop.getProperty(this.getClass().getName() + ".models",
                DEFAULT_MODELS);

        IdentifierExpertSystem.instance().addHardCodedKernel(this);
        init();
        IdentifierExpertSystem.instance().addSoftCodedKernel(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized BasicGroupContributionHolder instance()
    {
        if (instance == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " +
                    BasicGroupContributionHolder.class.getName() +
                    " instance.");
            }

            instance = new BasicGroupContributionHolder();
        }

        return instance;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    public BasicGroupContributions getGroupContributions(String model)
    {
        if (!_init)
        {
            init();
        }

        BasicGroupContributions tmp;
        tmp = (BasicGroupContributions) contributions.get(model);

        if (tmp == null)
        {
            logger.error("Group contribution model " + model +
                " does not exist.");
        }

        return tmp;
    }

    /**
     * Release date for this expert system (hard coded).
     *
     * @return Release date for this expert system (hard coded).
     */
    public String getReleaseDateInternal()
    {
        return BasicGroupContributionHolder.getReleaseDate();
    }

    /**
     * Release version for this expert system (hard coded).
     *
     * @return Release version for this expert system (hard coded).
     */
    public String getReleaseVersionInternal()
    {
        return BasicGroupContributionHolder.getReleaseVersion();
    }

    /**
     * Vendor for this expert system (hard coded).
     *
     * @return Vendor for this expert system (hard coded).
     */
    public String getVendorInternal()
    {
        return BasicGroupContributionHolder.getVendor();
    }

    /**
     *  Description of the Method
     */
    protected void finalize() throws Throwable
    {
        contributions.clear();
        contributions = null;
        super.finalize();
    }

    /**
     *  Description of the Method
     */
    protected synchronized void init()
    {
        if (_init)
        {
            return;
        }

        _init = true;

        Vector vs = new Vector();
        byte[] bytes = null;

        HelperMethods.tokenize(vs, _models);

        if (vs.size() == 0)
        {
            logger.error("No group contribution models defined.");

            return;
        }

        contributions = new Hashtable(vs.size());

        String filename;

        for (int i = 0; i < vs.size(); i++)
        {
            filename = _resourceDir + "/" + (String) vs.get(i) + _contribExt;
            bytes = BasicResourceLoader.instance().getBytesFromResourceLocation(
                    filename);

            if (resourceFile == null)
            {
                resourceFile = filename;
            }
            else
            {
                resourceFile = resourceFile + ":" + filename;
            }

            actualGC = new BasicGroupContributions((String) vs.get(i));
            heavyAtoms = true;

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
                }
            }
            else
            {
                logger.error("Unable to open data file '" + filename + "'");

                //throw new Exception("Unable to open data file '" + _filename +"'");
                System.exit(1);
            }

            contributions.put((String) vs.get(i), actualGC);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  buffer  Description of the Parameter
     */
    protected void parseLine(String buffer)
    {
        // skip comments and blanks
        if (buffer.startsWith("#") || buffer.startsWith("\n"))
        {
            return;
        }

        // check for heavy/hydrogen atom switch
        if (buffer.startsWith(";hydrogen"))
        {
            heavyAtoms = false;

            return;
        }
        else if (buffer.startsWith(";heavy"))
        {
            heavyAtoms = true;

            return;
        }

        // extract smarts and value tokens
        StringTokenizer tokens = new StringTokenizer(buffer);

        if (tokens.countTokens() < 2)
        {
            return;
        }

        // parse smarts
        SMARTSPatternMatcher smarts = new BasicSMARTSPatternMatcher();
        smarts.init(tokens.nextToken());

        Double value = new Double(tokens.nextToken());

        // store values for atom or hydrogen
        if (heavyAtoms)
        {
            actualGC.atomSmarts.add(smarts);
            actualGC.atomContributions.add(value);
        }
        else
        {
            actualGC.hydrogenSmarts.add(smarts);
            actualGC.hydrogenContributions.add(value);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
