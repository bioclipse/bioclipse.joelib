///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolDescCounter.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:30 $
//            $Author: wegner $
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
package joelib2.feature.data;

import joelib2.feature.NativeValue;

import joelib2.io.BasicIOType;
import joelib2.io.BasicReader;
import joelib2.io.IOType;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.PairData;

import joelib2.util.iterator.PairDataIterator;

import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.BasicResourceLoader;

import java.io.FileInputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Category;


/**
 *  Counts the number of descriptors and molecules in a molecule file.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:30 $
 */
public class MolDescCounter implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(MolDescCounter.class
            .getName());

    /**
     *  Description of the Field
     */
    public final static int ALL_DESCRIPTORS = 0;

    /**
     *  Description of the Field
     */
    public final static int NATIVE_DESCRIPTORS = 1;

    //~ Instance fields ////////////////////////////////////////////////////////

    private List desc2ignore;

    private Hashtable descriptors;
    private String identifierValue;
    private int moleculeCounter;
    private String moleculeIdentifier;
    private Hashtable moleculeIdentifiers;
    private Hashtable moleculeNames;
    private int store = NATIVE_DESCRIPTORS;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntResult object
     */
    public MolDescCounter()
    {
        this(100, 50);
    }

    /**
     *  Constructor for the MolDescCounter object
     *
     * @param  _molSize   Description of the Parameter
     * @param  _descSize  Description of the Parameter
     */
    public MolDescCounter(int _molSize, int _descSize)
    {
        moleculeNames = new Hashtable(_molSize);
        moleculeIdentifiers = new Hashtable(_molSize);
        descriptors = new Hashtable(_descSize);
        moleculeCounter = 0;

        // load descriptors which should be ignored
        String value;

        if ((value = BasicPropertyHolder.instance().getProperty(this,
                            "descriptors2ignore")) != null)
        {
            List tmpVec = BasicResourceLoader.readLines(value);

            if (tmpVec == null)
            {
                logger.error("File with descriptor names to ignore not found.");
            }

            desc2ignore = tmpVec;
        }
    }

    /**
     *  Constructor for the MolDescCounter object
     *
     * @param  _inType  Description of the Parameter
     * @param  _inFile  Description of the Parameter
     */
    public MolDescCounter(IOType _inType, String _inFile) throws Exception
    {
        this(1000, 500);
        count(_inType, _inFile);
    }

    /**
     *  Constructor for the MolDescCounter object
     *
     * @param  _molSize   Description of the Parameter
     * @param  _descSize  Description of the Parameter
     * @param  _inType    Description of the Parameter
     * @param  _inFile    Description of the Parameter
     */
    public MolDescCounter(int _molSize, int _descSize, BasicIOType _inType,
        String _inFile) throws Exception
    {
        this(_molSize, _descSize);
        count(_inType, _inFile);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Enumeration availableDescriptors()
    {
        return descriptors.keys();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Enumeration availableMolIdentifiers()
    {
        return moleculeIdentifiers.keys();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Enumeration availableMolNames()
    {
        return moleculeNames.keys();
    }

    /**
     *  Description of the Method
     */
    public void clear()
    {
        moleculeNames.clear();
        moleculeIdentifiers.clear();
        descriptors.clear();
        moleculeCounter = 0;
    }

    /**
     *  Description of the Method
     *
     * @param  inType         Description of the Parameter
     * @param  inFile         Description of the Parameter
     * @return                Description of the Return Value
     * @exception  Exception  Description of the Exception
     */
    public int count(IOType inType, String inFile) throws Exception
    {
        logger.info("Count descriptors in " + inFile + ".");

        Integer integer;

        BasicReader reader = null;

        //System.out.println("inType:"+inType);
        try
        {
            reader = new BasicReader(new FileInputStream(inFile), inType);
        }
        catch (Exception ex)
        {
            throw ex;

            //      return -1;
        }

        Molecule mol = new BasicConformerMolecule(inType, inType);

        for (;;)
        {
            try
            {
                if (!reader.readNext(mol))
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                logger.error(ex.toString());

                return -1;
            }

            identifierValue = null;
            countDescriptors(mol);

            integer = new Integer(moleculeCounter);

            //      System.out.println("mol.getTitle():"+mol.getTitle()+" integer:"+integer+" moleculeNames:"+moleculeNames);
            if (mol.getTitle() != null)
            {
                moleculeNames.put(mol.getTitle(), integer);
            }

            //      System.out.println("identifierValue:"+identifierValue+" integer:"+integer+" moleculeNames:"+moleculeNames);
            if (identifierValue != null)
            {
                moleculeNames.put(identifierValue, integer);
            }

            moleculeCounter++;

            //      System.out.println("moleculeCounter:"+moleculeCounter);
        }

        //reader.close();
        reader = null;

        return moleculeCounter;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public int countDescriptors(Molecule mol)
    {
        PairDataIterator gdit = mol.genericDataIterator();
        String descriptor;
        PairData pairData;

        //    String               ignoreDesc   = PropertyHolder.instance().getProperties().getProperty("joelib2.feature.MolDescCounter.ignoreDescriptor", "Entry_Number");
        // count descriptors
        int counter = 0;
        boolean ignoreDesc = false;

        while (gdit.hasNext())
        {
            pairData = gdit.nextPairData();
            descriptor = pairData.getKey();

            // ignore descriptors in list
            if (desc2ignore != null)
            {
                ignoreDesc = false;

                for (int i = 0; i < desc2ignore.size(); i++)
                {
                    if (descriptor.equals((String) desc2ignore.get(i)))
                    {
                        ignoreDesc = true;

                        break;
                    }
                }

                if (ignoreDesc)
                {
                    continue;
                }
            }

            // get unparsed identifier
            if (descriptor.equals(moleculeIdentifier))
            {
                identifierValue = (String) pairData.getKeyValue();
            }

            // parse data, if possible
            pairData = mol.getData(descriptor, true);

            if (store == NATIVE_DESCRIPTORS)
            {
                //          System.out.println("data:"+data.getClass().getName());
                if (pairData instanceof NativeValue)
                {
                    //            System.out.println("native");
                    if (!descriptors.containsKey(descriptor))
                    {
                        descriptors.put(descriptor, "");
                        counter++;

                        //              System.out.println("add native: "+counter);
                    }
                }

                //          System.out.println("value:"+data.getValue());
            }
            else if (store == ALL_DESCRIPTORS)
            {
                descriptors.put(descriptor, "");
                counter++;
            }
        }

        //System.out.println("counter:"+counter);
        return counter;
    }

    /**
     *  Gets the molIdentifier attribute of the MolDescCounter object
     *
     * @return    The molIdentifier value
     */
    public String getMolIdentifier()
    {
        return moleculeIdentifier;
    }

    /**
     *  Gets the storeDescriptors attribute of the MolDescCounter object
     *
     * @return    The storeDescriptors value
     */
    public int getStoreDescriptors()
    {
        return store;
    }

    /**
     *  Gets the numberDescriptors attribute of the MolDescCounter object
     *
     * @return    The numberDescriptors value
     */
    public int numberDescriptors()
    {
        return descriptors.size();
    }

    /**
     *  Gets the numberMolecules attribute of the MolDescCounter object
     *
     * @return    The numberMolecules value
     */
    public int numberMolecules()
    {
        return moleculeCounter;
    }

    /**
     *  Sets the molIdentifier attribute of the MolDescCounter object
     *
     * @param  _moleculeIdentifier  The new molIdentifier value
     */
    public void setMolIdentifier(String _moleculeIdentifier)
    {
        moleculeIdentifier = _moleculeIdentifier;
    }

    /**
     *  Sets the storeDescriptors attribute of the MolDescCounter object
     *
     * @param  _store  The new storeDescriptors value
     */
    public void setStoreDescriptors(int _store)
    {
        store = _store;
    }
}

/*-------------------------------------------------------------------------*
 * END
 *-------------------------------------------------------------------------*/

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
