/**
*  Filename: $RCSfile: ExternalHelper.java,v $
*  Purpose:  Some helper methods for calling external programs.
*  Language: Java
*  Compiler: JDK 1.2
*  Authors:  Fred Rapp, Joerg Kurt Wegner
*  Version:  $Revision: 1.5 $
*            $Date: 2005/02/17 16:48:29 $
*            $Author: wegner $
*  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
*/
package joelib2.ext;

import org.apache.log4j.Category;


/**
 * Some helper methods for calling external programs.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.5 $, $Date: 2005/02/17 16:48:29 $
 */
public class ExternalHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /** Obtain a suitable logger. */
    private static Category logger = Category.getInstance(
            "joelib2.ext.ExternalHelper");
    public static final String OS_WINDOWS = "windows";
    public static final String OS_LINUX = "linux";
    public static final String OS_SOLARIS = "solaris";

    //~ Constructors ///////////////////////////////////////////////////////////

    /** Don't let anyone instantiate this class */
    private ExternalHelper()
    {
        logger.info("Operating system is: " + getOperationSystemName());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns the name of the operation system.
     *
     *   @todo maybe move this method to a more common class */
    public static String getOperationSystemName()
    {
        String osName = System.getProperty("os.name");

        // determine name of operation system and convert it into lower caps without blanks
        if (osName.indexOf("Windows") != -1)
        {
            osName = OS_WINDOWS;
        }
        else if (osName.indexOf("Linux") != -1)
        {
            osName = OS_LINUX;
        }
        else if (osName.indexOf("Solaris") != -1)
        {
            osName = OS_SOLARIS;
        }

        return osName;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
