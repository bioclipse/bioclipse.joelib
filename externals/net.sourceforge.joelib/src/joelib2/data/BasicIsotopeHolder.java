///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicIsotopeHolder.java,v $
//  Purpose:  Isotope element table.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
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

import joelib2.util.HelperMethods;

import joelib2.util.types.BasicDoubleInt;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Isotope element table.
 * The definition file can be defined in the
 * <tt>joelib2.data.JOEIsotopeTable.resourceFile</tt> property in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file for default.
 *
 * <p>
 * Default:<br>
 * joelib2.data.JOEIsotopeTable.resourceFile=<a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joelib/joelib/src/joelib2/data/plain/isotope.txt?rev=HEAD&content-type=text/vnd.viewcvs-markup">joelib2/data/plain/isotope.txt</a>
 *
 * @.author     wegnerj
 * @.wikipedia Isotope
 * @.wikipedia Molecule
 * @.wikipedia Atom
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:29 $
 */
public class BasicIsotopeHolder extends AbstractDataHolder
    implements IdentifierHardDependencies, IsotopeHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicIsotopeHolder.class.getName());
    private static BasicIsotopeHolder isotopeTable;
    protected static final String DEFAULT_RESOURCE =
        "joelib2/data/plain/isotope.txt";
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.3 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:29 $";
    private static final Class[] DEPENDENCIES = new Class[]{};

    //~ Instance fields ////////////////////////////////////////////////////////

    private List<Vector<BasicDoubleInt>> isotopes;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOETypeTable object
     */
    private BasicIsotopeHolder()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", DEFAULT_RESOURCE);

        isotopes = new Vector<Vector<BasicDoubleInt>>();

        IdentifierExpertSystem.instance().addHardCodedKernel(this);
        init();
        IdentifierExpertSystem.instance().addSoftCodedKernel(this);

        logger.info("Using isotope table: " + resourceFile);
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
    public static synchronized BasicIsotopeHolder instance()
    {
        if (isotopeTable == null)
        {
            isotopeTable = new BasicIsotopeHolder();
        }

        return isotopeTable;
    }

    /**
     * Return the exact masss of the isotope.
     * (or by default, the most abundant isotope)
     */
    public double getExactMass(int atomicNum)
    {
        return getExactMass(atomicNum, 0);
    }

    /**
     * Return the exact masss of the isotope.
     * (or by default, the most abundant isotope)
     */
    public double getExactMass(int atomicNum, int isotope)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicNum > isotopes.size()) || (atomicNum < 0))
        {
            return 0.0;
        }

        int iso;
        List<BasicDoubleInt> elements = isotopes.get(atomicNum);
        BasicDoubleInt entry;

        for (iso = 0; iso < elements.size(); iso++)
        {
            entry = elements.get(iso);

            if (isotope == entry.intValue)
            {
                return entry.doubleValue;
            }
        }

        return 0.0;
    }

    /**
     * Release date for this expert system (hard coded).
     *
     * @return Release date for this expert system (hard coded).
     */
    public String getReleaseDateInternal()
    {
        return BasicIsotopeHolder.getReleaseDate();
    }

    /**
     * Release version for this expert system (hard coded).
     *
     * @return Release version for this expert system (hard coded).
     */
    public String getReleaseVersionInternal()
    {
        return BasicIsotopeHolder.getReleaseVersion();
    }

    /**
     * Vendor for this expert system (hard coded).
     *
     * @return Vendor for this expert system (hard coded).
     */
    public String getVendorInternal()
    {
        return BasicIsotopeHolder.getVendor();
    }

    /**
     *  Description of the Method
     *
     * @param  buffer  Description of the Parameter
     */
    protected void parseLine(String buffer)
    {
        Vector<String> vs = new Vector<String>();
        int atomicNumber;
        Vector<BasicDoubleInt> row = new Vector<BasicDoubleInt>();
        BasicDoubleInt di;

        // skip comment line (at the top)
        if (!buffer.trim().equals("") && (buffer.charAt(0) != '#'))
        {
            HelperMethods.tokenize(vs, buffer);

            if (vs.size() > 3) // atomic number, 0, most abundant mass (...)
            {
                //atomicNum = Integer.parseInt((String) vs.get(0));
                for (atomicNumber = 1; atomicNumber < (vs.size() - 1);
                        atomicNumber += 2) // make sure i+1 still exists
                {
                    di = new BasicDoubleInt();
                    di.intValue = Integer.parseInt(vs.get(atomicNumber));
                    di.doubleValue = Double.parseDouble(vs.get(
                                atomicNumber + 1));
                    row.add(di);
                }

                isotopes.add(row);
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
