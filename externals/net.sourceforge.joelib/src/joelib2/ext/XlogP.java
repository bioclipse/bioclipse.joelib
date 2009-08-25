///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: XlogP.java,v $
//  Purpose:  Calls corina to create 3D structures.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.1 $
//            $Date: 2006/03/26 10:36:44 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
///////////////////////////////////////////////////////////////////////////////
package joelib2.ext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import joelib2.ext.External;
import joelib2.ext.ExternalHelper;
import joelib2.ext.ExternalInfo;
import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.process.MoleculeProcessException;
import joelib2.process.ProcessInfo;
import joelib2.util.HelperMethods;
import joelib2.molecule.Molecule;
import joelib2.molecule.types.BasicPairData;
import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import org.apache.log4j.Category;

import wsi.ra.io.BasicBatchFileUtilities;


/**
 * Calls petra to get molecule properties.
 *
 * @author     wegnerj
 */
public class XlogP implements External
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
    *  Obtain a suitable logger.
    */
    private static Category logger = Category.getInstance(
            "joelib2.ext.XlogP");
    public final static String FILE = "FILE";
    public final static String XLOGP = "XLOGP";
    public final static String ADD = "ADD";
    private final static BasicProperty[] ACCEPTED_PROPERTIES = new BasicProperty[]
        {
            new BasicProperty(FILE, "java.lang.String",
                "Full path to the MOL2 molecule file.", true),
            new BasicProperty(XLOGP, "java.lang.Double",
                "Calculated XlogP value.", true),
            new BasicProperty(ADD, "java.lang.Boolean",
                "Flag if the value should be added to molecule.", true,
                Boolean.TRUE),
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private ExternalInfo info;
    private boolean tmpFileCreated;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
    *  Constructor for the Corina object
    */
    public XlogP()
    {
        tmpFileCreated = false;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    *  Gets the descriptionFile attribute of the Corina object
    *
    * @return    The descriptionFile value
    */
    public String getDescriptionFile()
    {
        return info.getDescriptionFile();
    }

    /**
    *  Sets the externalInfo attribute of the Corina object
    *
    * @param  _info  The new externalInfo value
    */
    public void setExternalInfo(ExternalInfo _info)
    {
        info = _info;
    }

    /**
    *  Gets the externalInfo attribute of the Corina object
    *
    * @return    The externalInfo value
    */
    public ExternalInfo getExternalInfo()
    {
        return info;
    }

    /**
    *  Sets the processInfo attribute of the ProcessPipe object
    *
    * @param  _info  The new processInfo value
    */
    public void setProcessInfo(ProcessInfo _info)
    {
        info.setName(_info.getName());
        info.setRepresentation(_info.getRepresentation());
        info.setDescriptionFile(_info.getDescriptionFile());
    }

    /**
    *  Gets the processInfo attribute of the ProcessPipe object
    *
    * @return    The processInfo value
    */
    public ProcessInfo getProcessInfo()
    {
        return (ProcessInfo) info;
    }

    /**
    *  Gets the thisOSsupported attribute of the Corina object
    *
    * @return    The thisOSsupported value
    */
    public boolean isThisOSsupported()
    {
        if (ExternalHelper.getOperationSystemName().equals(ExternalHelper.OS_LINUX) ||
                ExternalHelper.getOperationSystemName().equals(ExternalHelper.OS_WINDOWS) /*||
            ExternalHelper.getOperationSystemName().equals( ExternalHelper.OS_SOLARIS )*/)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public BasicProperty[] acceptedProperties()
    {
        return ACCEPTED_PROPERTIES;
    }

    public boolean clear()
    {
        tmpFileCreated = false;

        return true;
    }

    /**
    *  Description of the Method
    *
    * @param  mol  Description of the Parameter
    * @return      Description of the Return Value
    */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException
    {
        System.out.println("XLogP");

        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition for process or missing property entry.");

            return false;
        }

        String filename = getFileName(mol, properties);

        if (filename == null)
        {
            return false;
        }

        // generate executable command line
        List argsVector = getExternalInfo().getArguments();

        if ((argsVector == null) || (argsVector.size() == 0))
        {
            logger.error("External " + this.getClass().getName() +
                " not properly defined. See " + getDescriptionFile());

            return false;
        }

        String[] args = new String[argsVector.size() + 2];
        args[0] = getExternalInfo().getExecutable();

        for (int i = 0; i < argsVector.size(); i++)
        {
            args[i + 1] = (String) argsVector.get(i);
        }

        args[argsVector.size() + 1] = filename;

        // execute XlogP process
        Process process;
        StringBuffer buffer = new StringBuffer(1000);
        double xLogPvalue = 0.0;

        try
        {
            //        	for (int i = 0; i < args.length; i++)
            //			{
            //				System.out.println("Execute XLogP: "+args[i]);
            //			}
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(args);

            // set input pipe
            BufferedReader in = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));

            // set output pipe
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                        process.getOutputStream()));

            // wait for extern process termination
            //System.out.println("Wait for XLogP ");
            process.waitFor();

            //process.destroy();
            // get input pipe data
            String nextLine = null;

            //System.out.println("Parse XLogP ");
            while ((nextLine = in.readLine()) != null)
            {
                //System.out.println("XlogP: "+nextLine);
                buffer.append(nextLine + HelperMethods.eol);

                if (nextLine.charAt(0) == 'L')
                {
                    int index = nextLine.indexOf("LogP =");
                    xLogPvalue = Double.parseDouble(nextLine.substring(index +
                                6, index + 16));
                }

                //        System.out.println("XLOGP:"+xLogPvalue);
            }
        }
         catch (Exception e)
        {
            logger.error("Could not start executable: " + args[0]);
            e.printStackTrace();

            return false;
        }

        // delete molecule file only if it is temporary
        if (tmpFileCreated)
        {
            BasicBatchFileUtilities.instance().deleteFileName(filename);
        }

        Double xLogP = new Double(xLogPvalue);
        PropertyHelper.setProperty(XLOGP, properties, xLogP);

        Boolean addFlag = (Boolean) PropertyHelper.getProperty(this, ADD,
                properties);

        if (addFlag != null)
        {
            if (addFlag.booleanValue())
            {
                BasicPairData dp = new BasicPairData();
                dp.setKey("XlogP");
                dp.setKeyValue(xLogP.toString());
                mol.addData(dp);
            }
        }

        return true;
    }

    private String getFileName(Molecule mol, Map properties)
        throws MoleculeProcessException
    {
        // get plain file name
        String filename = (String) PropertyHelper.getProperty(this, FILE,
                properties);
        String fullFilename = filename;

        if (filename == null)
        {
            tmpFileCreated = true;
            filename = "joelib-molecule.mol2";

            // create full path to file and create file
            fullFilename = HelperMethods.getTempFileBase() + filename;

            try
            {
                fullFilename = BasicBatchFileUtilities.instance().createNewFileName(fullFilename);
            }
             catch (Exception ex)
            {
                logger.error(ex.toString());

                return null;
            }

            // get io type and create molecule file
            BasicIOType ioType = BasicIOTypeHolder.instance().getIOType("MOL2");

            if (HelperMethods.moleculeToFile(fullFilename, mol, ioType, true) == false)
            {
                return null;
            }

            //      // store created file name in properties
            //      properties.put("FILE", fullFilename);
            //      if (logger.isDebugEnabled())
            //      {
            //        logger.debug("Filename  '" + fullFilename + "' created.");
            //      }
        }

        return fullFilename;
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
