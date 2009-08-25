///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Petra.java,v $
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import joelib2.ext.SimpleExternalProcess;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;
import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;
import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;
import joelib2.process.MoleculeProcessException;
import joelib2.util.HelperMethods;
import joelib2.util.iterator.PairDataIterator;

import org.apache.log4j.Category;


/**
 * Calls petra to get molecule properties.
 *
 * @author     wegnerj
 */
public class Petra extends SimpleExternalProcess
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.ext.Petra");

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean overwriteProp;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Corina object
     */
    public Petra()
    {
        overwriteProp = true;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Sets the overwrite attribute of the Petra object
     *
     * @param  _flag  The new overwrite value
     */
    public void setOverwrite(boolean _flag)
    {
        overwriteProp = _flag;
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
        if (!super.process(mol, properties))
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

        String[] args = new String[argsVector.size() + 1];
        args[0] = getExternalInfo().getExecutable();

        for (int i = 0; i < argsVector.size(); i++)
        {
            args[i + 1] = (String) argsVector.get(i);
        }

        // get molecule string
        String molString = toMolString(mol);

        if (molString == null)
        {
            logger.error("Molecule not writeable");

            return false;
        }

        // save old pairdata
        Vector data = new Vector(20);
        PairDataIterator gdit = mol.genericDataIterator();
        PairData pairData;
        while (gdit.hasNext())
        {
            pairData = gdit.nextPairData();

                data.add(pairData);
        }

        // execute petra
        Process process;
        StringBuffer buffer = new StringBuffer(1000);

        try
        {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(args);

            // set input pipe
            BufferedReader in = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));

            // set output pipe
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                        process.getOutputStream()));
            out.write(molString, 0, molString.length());
            out.close();

            // wait for extern process termination
            process.waitFor();

            // get input pipe data
            String nextLine = null;

            while ((nextLine = in.readLine()) != null)
            {
                buffer.append(nextLine + HelperMethods.eol);

                //        System.out.println("PETRA: "+nextLine);
            }
        }
         catch (Exception e)
        {
            logger.error("Could not start executable: " + args[0]);
            e.printStackTrace();

            return false;
        }

        // getting new molecule
        Molecule tmpMol = new BasicConformerMolecule(mol);

        // create backup
        IOType inType = mol.getInputType();
        IOType outType = mol.getOutputType();
        mol.clear();

        ByteArrayInputStream sReader = new ByteArrayInputStream(buffer.toString()
                                                                      .getBytes());

        // get molecule loader
        MoleculeFileIO loader = null;

        try
        {
            loader = MoleculeFileHelper.getMolReader(sReader,
                    BasicIOTypeHolder.instance().getIOType("CTX"));
        }
         catch (IOException ex)
        {
            ex.printStackTrace();
        }
         catch (MoleculeIOException ex)
        {
            ex.printStackTrace();
        }

        if (!loader.readable())
        {
            // should never happen
            logger.error(inType.getRepresentation() + " is not readable.");
            logger.error("You're invited to write one !;-)");

            return false;
        }

        // load molecules and restore old data
        mol.setInputType(inType);
        mol.setOutputType(outType);

        boolean success = true;

        try
        {
            success = loader.read(mol);

            if (!success)
            {
                mol.set(tmpMol);

                return false;
            }
        }
         catch (IOException ex)
        {
            ex.printStackTrace();
            mol.set(tmpMol);

            return false;
        }
         catch (MoleculeIOException ex)
        {
            ex.printStackTrace();
            mol.set(tmpMol);

            return false;
        }

        if (mol.isEmpty())
        {
            logger.error("No molecule after " + this.getClass().getName() +
                " execution loaded.");
            mol.set(tmpMol);

            return false;
        }

        // restore old descriptor data
        for (int i = 0; i < data.size(); i++)
        {
            pairData = (BasicPairData)data.get(i);

            if (overwriteProp)
            {
                String attribute = pairData.getKey();

                if (!mol.hasData(attribute))
                {
                    mol.addData((BasicPairData) data.get(i));
                }
            }
            else
            {
                mol.addData((BasicPairData) data.get(i));
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private String toMolString(Molecule mol)
    {
        return mol.toString(BasicIOTypeHolder.instance().getIOType("CTX"));
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
