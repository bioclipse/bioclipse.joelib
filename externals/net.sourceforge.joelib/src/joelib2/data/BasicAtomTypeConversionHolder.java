///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicAtomTypeConversionHolder.java,v $
//  Purpose:  Type table.
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

import wsi.ra.tool.BasicPropertyHolder;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Type table to map internal atom types to atom types of other file formats.
 * The definition file can be defined in the
 * <tt>joelib2.data.JOETypeTable.resourceFile</tt> property in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file for default.
 *
 * <p>
 * Default:<br>
 * joelib2.data.JOETypeTable.resourceFile=<a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joelib/joelib/src/joelib2/data/plain/types.txt?rev=HEAD&content-type=text/vnd.viewcvs-markup">joelib2/data/plain/types.txt</a>
 *
 * @.author     wegnerj
 * @.wikipedia Molecule
 * @.wikipedia Atom
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:29 $
 */
public class BasicAtomTypeConversionHolder extends AbstractDataHolder
    implements IdentifierHardDependencies, AtomTypeConversionHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicAtomTypeConversionHolder.class.getName());
    private static BasicAtomTypeConversionHolder typeTable;
    protected static final String DEFAULT_RESOURCE =
        "joelib2/data/plain/types.txt";
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.3 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:29 $";

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  of type <tt>String</tt>
     */
    private List<String> _colnames;

    /**
     *  of type <tt>String</tt>-{@link java.util.Vector}
     */
    private List<Vector<String>> _table;
    private int fromTypeIndex;

    private Hashtable internal;
    private int internalIndex = 0;
    private boolean nextLineIsHeader = false;
    private int numberOfRows;
    private boolean readConversionTable = false;
    private int toTypeIndex;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOETypeTable object
     */
    private BasicAtomTypeConversionHolder()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", DEFAULT_RESOURCE);

        //    _linecount  = 0;
        fromTypeIndex = toTypeIndex = -1;

        _colnames = new Vector<String>();
        _table = new Vector<Vector<String>>();

        IdentifierExpertSystem.instance().addHardCodedKernel(this);
        init();
        IdentifierExpertSystem.instance().addSoftCodedKernel(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

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
    public static synchronized BasicAtomTypeConversionHolder instance()
    {
        if (typeTable == null)
        {
            typeTable = new BasicAtomTypeConversionHolder();
        }

        return typeTable;
    }

    /**
     * Release date for this expert system (hard coded).
     *
     * @return Release date for this expert system (hard coded).
     */
    public String getReleaseDateInternal()
    {
        return BasicAtomTypeConversionHolder.getReleaseDate();
    }

    /**
     * Release version for this expert system (hard coded).
     *
     * @return Release version for this expert system (hard coded).
     */
    public String getReleaseVersionInternal()
    {
        return BasicAtomTypeConversionHolder.getReleaseVersion();
    }

    /**
     * Vendor for this expert system (hard coded).
     *
     * @return Vendor for this expert system (hard coded).
     */
    public String getVendorInternal()
    {
        return BasicAtomTypeConversionHolder.getVendor();
    }

    /**
     *  Sets the fromType attribute of the JOETypeTable object
     *
     * @param  from  The new fromType value
     * @return       Description of the Return Value
     */
    public boolean setFromType(String from)
    {
        if (!initialized)
        {
            init();
        }

        String tmp = from;

        for (int i = 0; i < _colnames.size(); i++)
        {
            if (tmp.equals(_colnames.get(i)))
            {
                fromTypeIndex = i;

                return true;
            }
        }

        //throw new Exception("Requested type column not found");
        logger.error("Requested type column not found");

        return false;
    }

    /**
     *  Sets the toType attribute of the JOETypeTable object
     *
     * @param  to  The new toType value
     * @return     Description of the Return Value
     */
    public boolean setToType(String to)
    {
        if (!initialized)
        {
            init();
        }

        String tmp = to;

        for (int i = 0; i < _colnames.size(); i++)
        {
            if (tmp.equals((String) _colnames.get(i)))
            {
                toTypeIndex = i;

                return true;
            }
        }

        //throw new Exception("Requested type column not found");
        logger.error("Requested type column not found");

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  from  Description of the Parameter
     * @return       Description of the Return Value
     */
    public String translate(char[] from)
    {
        if (!initialized)
        {
            init();
        }

        String to = translate(from);

        return to;
    }

    /**
     *  Description of the Method
     *
     * @param  from  Description of the Parameter
     * @return       Description of the Return Value
     */
    public String translate(String from)
    {
        //    System.out.println("from:"+from);
        if (!initialized)
        {
            init();
        }

        if ((from == null) || from.equals(""))
        {
            logger.error("Empty atom type can not be translated !");

            return null;
        }

        //    String  to=null;
        for (int i = 0; i < _table.size(); i++)
        {
            Vector v = (Vector) _table.get(i);

            //      System.out.println("check: "+((String) v.get(_from)));
            if ((v.size() > fromTypeIndex) &&
                    ((String) v.get(fromTypeIndex)).equals(from))
            {
                return (String) v.get(toTypeIndex);
            }
        }

        logger.error("Atom type (" + from + ") can not be found! Check '" +
            BasicPropertyHolder.instance().getProperties().getProperty(
                BasicAtomTypeConversionHolder.class.getName() + ".resourceFile",
                DEFAULT_RESOURCE) + "' and '" +
            BasicPropertyHolder.instance().getProperties().getProperty(
                BasicAtomTyper.class.getName() + ".resourceFile",
                BasicAtomTyper.DEFAULT_RESOURCE) + "' definition files.");

        return null;
    }

    protected boolean isValidInternalType(String type)
    {
        if (internal == null)
        {
            internal = new Hashtable(_table.size());

            for (int i = 0; i < _table.size(); i++)
            {
                String internalType = _table.get(i).get(internalIndex);
                internal.put(internalType, _table.get(i));
            }
        }

        return internal.containsKey(type);
    }

    /**
     *  Description of the Method
     *
     * @param  buffer  Description of the Parameter
     */
    protected void parseLine(String buffer)
    {
        if ((buffer.trim().length() != 0) && (buffer.charAt(0) != '#'))
        {
            List<String> vs = new Vector<String>();
            HelperMethods.tokenize(vs, buffer);

            if (vs.size() == 2)
            {
                //System.out.println(vs.size()+"--> "+buffer);
                numberOfRows = Integer.parseInt((String) vs.get(1));
                nextLineIsHeader = true;
            }
            else if (nextLineIsHeader)
            {
                nextLineIsHeader = false;
                readConversionTable = true;
                HelperMethods.tokenize(_colnames, buffer);
                //for (int i = 0; i < _colnames.size(); i++) {
                //    System.out.println(_colnames.get(i));
                //}
            }
            else if (readConversionTable)
            {
                // skip empty lines
                if (buffer.trim().equals(""))
                {
                    return;
                }

                // store all types
                Vector<String> vc = new Vector<String>();
                HelperMethods.tokenize(vc, buffer);

                //      System.out.println("vc.size "+vc.size() +"_nrows"+_nrows);
                if (vc.size() == numberOfRows)
                {
                    _table.add(vc);
                }
                else
                {
                    logger.error("Wrong number of rows " + vc.size() + " (" +
                        numberOfRows + " expected) in " + resourceFile +
                        " in line " + getLineCounter() + ":\n" + buffer);
                }
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
