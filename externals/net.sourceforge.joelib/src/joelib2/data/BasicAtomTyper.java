///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicAtomTyper.java,v $
//  Purpose:  Atom typer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:29 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
//
//  Copyright (c) Dept. Computer Architecture, University of Tuebingen,
//                Germany, 2001-2005
//, 2003-2005
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

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;

import joelib2.feature.types.atomlabel.AtomInAromaticSystem;

import joelib2.molecule.Molecule;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.smarts.types.BasicSMARTSPatternString;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom typer based on structural expert rules.
 * The definition file can be defined in the
 * <tt>joelib2.data.JOEAtomTyper.resourceFile</tt> property in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file for default.
 *
 * <p>
 * For assigning atom types using a geometry-based algorithm have a look at {@.cite ml91} and the dot
 * connecting method {@link joelib2.molecule.Molecule#connectTheDots()}.
 *
 * <p>
 * Default:<br>
 * joelib2.data.JOEAtomTyper.resourceFile=<a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joelib/joelib/src/joelib2/data/plain/atomtype.txt?rev=HEAD&content-type=text/vnd.viewcvs-markup">joelib2/data/plain/atomtype.txt</a>
 *
 * @.author     wegnerj
 * @.wikipedia Molecule
 * @.wikipedia Atom
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:29 $
 * @see wsi.ra.tool.BasicPropertyHolder
 * @see wsi.ra.tool.BasicResourceLoader
 */
public class BasicAtomTyper extends AbstractDataHolder
    implements IdentifierHardDependencies, AtomTyper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.7 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:29 $";

    private static Category logger = Category.getInstance(BasicAtomTyper.class
            .getName());
    private static BasicAtomTyper atomtyper;
    protected static final String DEFAULT_RESOURCE =
        "joelib2/data/plain/atomtype.txt";
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            BasicHybridisationTyper.class, BasicSMARTSPatternMatcher.class,
            AtomInAromaticSystem.class
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private List<BasicSMARTSPatternString> externalTypeRule;

    //~ Constructors ///////////////////////////////////////////////////////////

    // of type SMARTSPatternString

    /**
     *  Constructor for the JOEAtomTyper object
     */
    private BasicAtomTyper()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", DEFAULT_RESOURCE);

        externalTypeRule = new Vector<BasicSMARTSPatternString>();

        IdentifierExpertSystem.instance().addHardCodedKernel(this);
        init();
        IdentifierExpertSystem.instance().addSoftCodedKernel(this);

        logger.info("Using atom type model: " + resourceFile);
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
    public static synchronized BasicAtomTyper instance()
    {
        if (atomtyper == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " + BasicAtomTyper.class.getName() +
                    " instance.");
            }

            atomtyper = new BasicAtomTyper();
        }

        return atomtyper;
    }

    public void getAtomTypes(Molecule mol, String[] types)
    {
        int[] itmp;

        if (!initialized)
        {
            init();
        }

        // initialize atoms with element type
        for (int atomI = 0; atomI < mol.getAtomsSize(); atomI++)
        {
            types[atomI] = BasicElementHolder.instance().getSymbol(mol.getAtom(
                        atomI + 1).getAtomicNumber());
        }

        List<int[]> matchList;

        // ensure checked aromaticity
        try
        {
            FeatureHelper.instance().featureFrom(mol,
                AtomInAromaticSystem.getName());
        }
        catch (FeatureException e)
        {
            logger.error(e.getMessage());
        }

        // set more advanced types
        for (int i = 0; i < externalTypeRule.size(); i++)
        {
            BasicSMARTSPatternString ps = externalTypeRule.get(i);

            if (ps.smartsValue.match(mol))
            {
                matchList = ps.smartsValue.getMatches();

                for (int j = 0; j < matchList.size(); j++)
                {
                    itmp = matchList.get(j);
                    mol.getAtom(itmp[0]).setType(ps.stringValue, false);
                    types[itmp[0] - 1] = ps.stringValue;

                    if (logger.isDebugEnabled())
                    {
                        logger.debug(mol.getTitle() + " match " +
                            ps.smartsValue.getSmarts() + " assign " +
                            ps.stringValue + " to atom " + itmp[0]);
                        logger.debug("atom type of atom " + itmp[0] + " " +
                            mol.getAtom(itmp[0]).getType());
                    }
                }
            }
        }
    }

    /**
     * Release date for this expert system (hard coded).
     *
     * @return Release date for this expert system (hard coded).
     */
    public String getReleaseDateInternal()
    {
        return BasicAtomTyper.getReleaseDate();
    }

    /**
     * Release version for this expert system (hard coded).
     *
     * @return Release version for this expert system (hard coded).
     */
    public String getReleaseVersionInternal()
    {
        return BasicAtomTyper.getReleaseVersion();
    }

    /**
     * Vendor for this expert system (hard coded).
     *
     * @return Vendor for this expert system (hard coded).
     */
    public String getVendorInternal()
    {
        return BasicAtomTyper.getVendor();
    }

    public boolean isValidType(String type)
    {
        return BasicAtomTypeConversionHolder.instance().isValidInternalType(
                type);
    }

    /**
     *  Description of the Method
     *
     * @param  buffer  Description of the Parameter
     */
    protected void parseLine(String buffer)
    {
        List<String> vs = new Vector<String>();
        HelperMethods.tokenize(vs, buffer);

        if ((vs.size() != 0) && (vs.size() >= 2))
        {
            if (vs.get(0).charAt(0) != '#')
            {
                SMARTSPatternMatcher sp = new BasicSMARTSPatternMatcher();

                if (sp.init((String) vs.get(0)))
                {
                    externalTypeRule.add(new BasicSMARTSPatternString(sp,
                            (String) vs.get(1)));
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
