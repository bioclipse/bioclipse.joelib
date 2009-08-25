///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicElementHolder.java,v $
//  Purpose:  Element table.
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

import joelib2.molecule.BasicElement;

import joelib2.util.HelperMethods;

import wsi.ra.tool.BasicPropertyHolder;

import java.awt.Color;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Element table.
 * The definition file can be defined in the
 * <tt>joelib2.data.JOEElementTable.resourceFile</tt> property in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file for default.
 *
 * <p>
 * Default:<br>
 * joelib2.data.JOEElementTable.resourceFile=<a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joelib/joelib/src/joelib2/data/plain/element.txt?rev=HEAD&content-type=text/vnd.viewcvs-markup">joelib2/data/plain/element.txt</a>
 *
 * @.author     wegnerj
 * @.wikipedia Chemical element
 * @.wikipedia Molecule
 * @.wikipedia Atom
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:29 $
 * @see wsi.ra.tool.BasicPropertyHolder
 * @see wsi.ra.tool.BasicResourceLoader
 */
public class BasicElementHolder extends AbstractDataHolder
    implements IdentifierHardDependencies, ElementHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicElementHolder.class.getName());
    private final static String DEFAULT_RESOURCE =
        "joelib2/data/plain/element.txt";
    private static BasicElementHolder etab;
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.3 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:29 $";
    private static final Class[] DEPENDENCIES = new Class[]{};

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  {@link java.util.List} of type <tt>JOEElement</tt>
     */
    private List<BasicElement> elements;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEElementTable object
     */
    private BasicElementHolder()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", DEFAULT_RESOURCE);

        //    resourceFile = "element.txt";
        //    _subdir = "joelib2/data/plain";
        //    //_dataptr  = ElementData;
        elements = new Vector<BasicElement>();

        IdentifierExpertSystem.instance().addHardCodedKernel(this);
        init();
        IdentifierExpertSystem.instance().addSoftCodedKernel(this);

        logger.info("Using element table: " + resourceFile);
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
     * @return   Description of the Return Value
     */
    public static synchronized BasicElementHolder instance()
    {
        if (etab == null)
        {
            etab = new BasicElementHolder();
        }

        return etab;
    }

    /**
     *  Description of the Method
     *
     * @param atomicnum  Description of the Parameter
     * @return           Description of the Return Value
     */
    public double correctedBondRad(int atomicnum)
    {
        return correctedBondRad(atomicnum, 3);
    }

    /**
     *  Description of the Method
     *
     * @param atomicnum  Description of the Parameter
     * @param hyb        Description of the Parameter
     * @return           Description of the Return Value
     */
    public double correctedBondRad(int atomicnum, int hyb)
    {
        // atomic #, hybridization
        double rad;

        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 1.0f;
        }

        rad = elements.get(atomicnum).getRadiusBondOrder();

        if (hyb == 2)
        {
            rad *= 0.95f;
        }
        else if (hyb == 1)
        {
            rad *= 0.90f;
        }

        return rad;
    }

    /**
     *  Description of the Method
     *
     * @param atomicnum  Description of the Parameter
     * @return           Description of the Return Value
     */
    public double correctedVdwRad(int atomicnum)
    {
        return correctedVdwRad(atomicnum, 3);
    }

    /**
     *  Description of the Method
     *
     * @param atomicnum  Description of the Parameter
     * @param hyb        Description of the Parameter
     * @return           Description of the Return Value
     */
    public double correctedVdwRad(int atomicnum, int hyb)
    {
        // atomic #, hybridization
        double rad;

        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 1.95f;
        }

        rad = elements.get(atomicnum).getRadiusVanDerWaals();

        if (hyb == 2)
        {
            rad *= 0.95f;
        }
        else if (hyb == 1)
        {
            rad *= 0.90f;
        }

        return rad;
    }

    /**
     * Gets the atom electronegativity after Allred and Rochow.
     *
     * @return   The allredRochowEN value
     */
    public double getAllredRochowEN(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0;
        }

        return (((BasicElement) elements.get(atomicnum)).getEnAllredRochow());
    }

    /**
     *  Gets the atomicNum attribute of the JOEElementTable object
     *
     * @param sym  Description of the Parameter
     * @return     The atomicNum value
     */
    public int getAtomicNum(final String sym)
    {
        if (!initialized)
        {
            init();
        }

        for (int i = 0; i < elements.size(); i++)
        {
            BasicElement elem = elements.get(i);

            //      System.out.print("symtab "+elem.getSymbol()+" sym "+sym);
            if (sym.equals(elem.getSymbol()))
            {
                //        System.out.println(" "+elem.getAtomicNum());
                return elem.getAtomicNumber();
            }

            //      System.out.println();
        }

        //    System.out.println("0");
        return 0;
    }

    /**
     *  Gets the bORad attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The bORad value
     */
    public double getBORad(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0.0f;
        }

        return elements.get(atomicnum).getRadiusBondOrder();
    }

    /**
     * Gets the color attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The color value
     */
    public Color getColor(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return elements.get(0).getColor();
        }

        return elements.get(atomicnum).getColor();
    }

    /**
     *  Gets the covalentRad attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The covalentRad value
     */
    public double getCovalentRad(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0.0f;
        }

        return elements.get(atomicnum).getRadiusCovalent();
    }

    /**
     * Gets the electronAffinity attribute of the JOEElement object
     *
     * @return   The electronAffinity value
     */
    public double getElectronAffinity(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0;
        }

        return elements.get(atomicnum).getElectronAffinity();
    }

    /**
     * Gets the exteriorElectrons attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The exteriorElectrons value
     */
    public int getExteriorElectrons(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0;
        }

        return elements.get(atomicnum).getExteriorElectrons();
    }

    /**
     *  Gets the mass attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The mass value
     */
    public double getMass(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0.0f;
        }

        return elements.get(atomicnum).getMass();
    }

    /**
     *  Gets the maxBonds attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The maxBonds value
     */
    public int getMaxBonds(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0;
        }

        return elements.get(atomicnum).getMaxBonds();
    }

    /**
     * Gets the atom electronegativity after Pauling.
     *
     * @return   The paulingEN value
     */
    public double getPaulingEN(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0;
        }

        return elements.get(atomicnum).getEnPauling();
    }

    /**
     * Gets the period of the JOEElement object.
     *
     * @return   The electronAffinity value
     */
    public int getPeriod(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0;
        }

        return elements.get(atomicnum).getPeriod();
    }

    /**
     * Release date for this expert system (hard coded).
     *
     * @return Release date for this expert system (hard coded).
     */
    public String getReleaseDateInternal()
    {
        return BasicElementHolder.getReleaseDate();
    }

    /**
     * Release version for this expert system (hard coded).
     *
     * @return Release version for this expert system (hard coded).
     */
    public String getReleaseVersionInternal()
    {
        return BasicElementHolder.getReleaseVersion();
    }

    public double getSandersonEN(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0;
        }

        return elements.get(atomicnum).getEnSanderson();
    }

    /**
     *  Gets the symbol attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The symbol value
     */
    public String getSymbol(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return ("");
        }

        //System.out.println(""+atomicnum+" symbol "+((JOEElement)_element.get(atomicnum)).getSymbol());
        return elements.get(atomicnum).getSymbol();
    }

    /**
     *  Gets the vdwRad attribute of the JOEElementTable object
     *
     * @param atomicnum  Description of the Parameter
     * @return           The vdwRad value
     */
    public double getVdwRad(int atomicnum)
    {
        if (!initialized)
        {
            init();
        }

        if ((atomicnum < 0) || (atomicnum > elements.size()))
        {
            return 0.0f;
        }

        return elements.get(atomicnum).getRadiusVanDerWaals();
    }

    /**
     * Vendor for this expert system (hard coded).
     *
     * @return Vendor for this expert system (hard coded).
     */
    public String getVendorInternal()
    {
        return BasicElementHolder.getVendor();
    }

    /**
     *  Description of the Method
     *
     * @param buffer  Description of the Parameter
     */
    protected void parseLine(String buffer)
    {
        int num;
        int maxbonds;
        String symbol;
        double rBO;
        double rCov;
        double rVdw;
        double mass;
        float r;
        float g;
        float b;
        String exteriorElectrons;
        byte period;
        byte group;
        double enAllredRochow;
        double enPauling;
        double enSanderson;
        double eAffinity;

        // skip comment line (at the top)
        if (!buffer.trim().equals("") && (buffer.charAt(0) != '#'))
        {
            Vector<String> vs = new Vector<String>();

            // of type String
            HelperMethods.tokenize(vs, buffer);

            String tmp;

            try
            {
                // Ignore RGB columns
                num = Integer.parseInt(vs.get(0));
                symbol = vs.get(1);
                rCov = Float.parseFloat(vs.get(2));
                rBO = Float.parseFloat(vs.get(4));
                rVdw = Float.parseFloat(vs.get(5));
                maxbonds = Integer.parseInt(vs.get(6));
                r = Float.parseFloat(vs.get(7));
                g = Float.parseFloat(vs.get(8));
                b = Float.parseFloat(vs.get(9));
                mass = Float.parseFloat(vs.get(10));
                exteriorElectrons = vs.get(11);
                period = Byte.parseByte(vs.get(12));
                tmp = vs.get(13);

                if (tmp.indexOf("Lanthanoids") != -1)
                {
                    group = 50;
                }
                else if (tmp.indexOf("Actinoids") != -1)
                {
                    group = 60;
                }
                else
                {
                    group = Byte.parseByte(tmp);
                }

                enAllredRochow = Float.parseFloat(vs.get(14));
                enPauling = Float.parseFloat(vs.get(15));
                enSanderson = Float.parseFloat(vs.get(16));
                eAffinity = Float.parseFloat(vs.get(17));

                //        System.out.println("Affinity: "+eAffinity);
            }
            catch (NumberFormatException ex)
            {
                logger.error("Error in line: " + buffer);
                logger.error(ex.getMessage());
                logger.error(ex.toString());

                return;
            }

            BasicElement ele = new BasicElement(num, symbol, rCov, rBO, rVdw,
                    maxbonds, new Color(r, g, b), mass, exteriorElectrons,
                    period, group, enAllredRochow, enPauling, enSanderson,
                    eAffinity);
            elements.add(ele);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
