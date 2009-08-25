///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DescriptorFilter.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:38 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
///////////////////////////////////////////////////////////////////////////////
package joelib2.process.filter;

import joelib2.molecule.Molecule;

import wsi.ra.tool.BasicResourceLoader;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Interface definition for calling external programs from JOELib.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:38 $
 */
public class DescriptorFilter implements Filter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(DescriptorFilter.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private List descriptorNames;

    private FilterInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorFilter object
     */
    public DescriptorFilter()
    {
    }

    /**
     *  Constructor for the DescriptorFilter object
     *
     * @param  _descNames  Description of the Parameter
     */
    public DescriptorFilter(List _descNames)
    {
        init(_descNames);
    }

    /**
     *  Constructor for the DescriptorFilter object
     *
     * @param  descNamesURL  Description of the Parameter
     */
    public DescriptorFilter(String descNamesURL, boolean _ignoreComments)
    {
        init(descNamesURL, _ignoreComments);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean accept(Molecule mol)
    {
        if (descriptorNames == null)
        {
            logger.warn("Descriptor names not defined in " +
                this.getClass().getName() + ".");

            return false;
        }

        boolean foundAll = true;
        boolean found;
        boolean debug = logger.isDebugEnabled();
        StringBuffer debugSB = null;

        for (int i = 0; i < descriptorNames.size(); i++)
        {
            found = mol.hasData((String) descriptorNames.get(i));

            if (!found)
            {
                //        System.out.println(""+descriptorNames.get(i)+" not found");
                foundAll = false;

                if (debug)
                {
                    if (debugSB == null)
                    {
                        debugSB = new StringBuffer(descriptorNames.size() * 15);
                    }

                    debugSB.append((String) descriptorNames.get(i));
                    debugSB.append(',');
                }
                else
                {
                    break;
                }
            }
        }

        if (debug && !foundAll)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Missing descriptor(s): " + debugSB.toString() +
                    " in " + mol.getTitle());
            }
        }

        //System.out.println("Accept:::"+foundAll);
        return foundAll;
    }

    /**
     *  Gets the processInfo attribute of the DescriptorFilter object
     *
     * @return    The processInfo value
     */
    public FilterInfo getFilterInfo()
    {
        return info;
    }

    /**
     *  Description of the Method
     *
     * @param  _descNames  Description of the Parameter
     */
    public void init(List _descNames)
    {
        if (_descNames == null)
        {
            return;
        }

        //    descriptorNames = _descNames;
        if (_descNames.size() == 0)
        {
            logger.warn("Filter rule is empty in " + this.getClass().getName());
        }

        // use trimmed descriptor names without whitespaces
        descriptorNames = new Vector(_descNames.size());

        for (int i = 0; i < _descNames.size(); i++)
        {
            descriptorNames.add(((String) _descNames.get(i)).trim());
        }
    }

    /**
     *  Description of the Method
     *
     * @param  descNamesURL  Description of the Parameter
     */
    public void init(String descNamesURL, boolean _ignoreComments)
    {
        if (descNamesURL == null)
        {
            return;
        }

        init(BasicResourceLoader.readLines(descNamesURL, _ignoreComments));

        //    byte                  bytes[]    = ResourceLoader.instance().getBytesFromResourceLocation(descNamesURL);
        //    if (bytes == null)
        //    {
        //      logger.error("No descriptor file name found at \"" + descNamesURL + "\".");
        //      return;
        //    }
        //    ByteArrayInputStream  sReader  = new ByteArrayInputStream(bytes);
        //    LineNumberReader      lnr      = new LineNumberReader(new InputStreamReader(sReader));
        //
        //    String                line;
        //    descriptorNames = new Vector(INITIALIZING_SIZE);
        //    try
        //    {
        //      while ((line = lnr.readLine()) != null)
        //      {
        //        descriptorNames.add(line);
        //      }
        //    }
        //    catch (IOException ex)
        //    {
        //      ex.printStackTrace();
        //    }
    }

    /**
     *  Sets the filterInfo attribute of the DescriptorFilter object
     *
     * @param  _info  The new filterInfo value
     */
    public void setFilterInfo(FilterInfo _info)
    {
        info = _info;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
