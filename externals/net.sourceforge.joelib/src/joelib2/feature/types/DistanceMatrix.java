///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DistanceMatrix.java,v $
//  Purpose:  Distance matrix.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:31 $
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
package joelib2.feature.types;

import joelib2.algo.BFS;
import joelib2.algo.BFSResult;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.BasicFeatureDescription;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureFactory;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.ResultFactory;

import joelib2.feature.result.IntMatrixResult;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.util.BasicProperty;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Category;


/**
 *  Calculates the Distance Matrix (shortest paths from each atom to each atom) of a molecule
 *
 * @.author     Jan Bruecker
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:31 $
 */
public class DistanceMatrix implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(DistanceMatrix.class
            .getName());
    private static final Class[] DEPENDENCIES = new Class[]{BFS.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DistanceMatrix object
     */
    public DistanceMatrix()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.IntMatrixResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return DistanceMatrix.class.getName();
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

    public BasicProperty[] acceptedProperties()
    {
        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol) throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  initData                 Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, Map properties)
        throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, properties);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  descResult               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult)
        throws FeatureException
    {
        return calculate(mol, descResult, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  initData                 Description of the Parameter
     * @param  descResult               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult,
        Map properties) throws FeatureException
    {
        if (mol.isEmpty())
        {
            logger.error("Empty molecule '" + mol.getTitle() + "'.");

            return null;
        }

        if (!(descResult instanceof IntMatrixResult))
        {
            logger.error(descInfo.getName() + " result should be of type " +
                IntMatrixResult.class.getName() + " but it's of type " +
                descResult.getClass().toString());
        }

        // check if the init type is correct
        if (!initialize(properties))
        {
            return null;
        }

        int atoms = mol.getAtomsSize();
        Feature[] bfs = new Feature[atoms];

        try
        {
            for (int i = 0; i < atoms; i++)
            {
                bfs[i] = FeatureFactory.getFeature(BFS.getName());

                // System.out.println("Loaded descriptor:\n" + bfs[i].getDescInfo());
            }
        }
        catch (FeatureException ex)
        {
            ex.printStackTrace();

            return null;
        }

        FeatureResult[] resultArr = new FeatureResult[atoms];
        Hashtable[] init = new Hashtable[atoms];
        int[][] matrix = new int[atoms][atoms];
        BFSResult[] resultBFS = new BFSResult[atoms];

        // initialize BFS
        for (int i = 1; i < (atoms + 1); i++)
        {
            Atom startAtom = mol.getAtom(i);
            init[i - 1] = new Hashtable();
            init[i - 1].put(BFS.STARTING_ATOM, startAtom);
        }

        try
        {
            for (int j = 0; j < atoms; j++)
            {
                resultArr[j] = bfs[j].calculate(mol, init[j]);

                // has something weird happen
                if (resultArr[j] == null)
                {
                    logger.error("Distance matrix can't be calculated");

                    return null;
                }
            }
        }
        catch (FeatureException ex)
        {
            ex.printStackTrace();
        }

        for (int l = 0; l < atoms; l++)
        {
            resultBFS[l] = (BFSResult) resultArr[l];
        }

        for (int p = 0; p < atoms; p++)
        {
            for (int k = 0; k < atoms; k++)
            {
                matrix[p][k] = resultBFS[p].getTraverse()[k];
            }
        }

        IntMatrixResult result = (IntMatrixResult) descResult;
        result.value = matrix;

        return result;
    }

    /**
     *  Description of the Method
     */
    public void clear()
    {
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public BasicFeatureInfo getDescInfo()
    {
        return descInfo;
    }

    /**
     *  Sets the descriptionFile attribute of the Descriptor object
     *
     * @param  _descInfo  The new descInfo value
     */

    //  public void setDescInfo(DescriptorInfo _descInfo)
    //  {
    //    descInfo = _descInfo;
    //  }

    /**
     *  Gets the description attribute of the Descriptor object
     *
     * @return    The description value
     */
    public FeatureDescription getDescription()
    {
        return new BasicFeatureDescription(descInfo.getDescriptionFile());
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }

    /**
     *  Description of the Method
     *
     * @param  initData  Description of the Parameter
     */
    public boolean initialize(Map properties)
    {
        return true;
    }

    /**
     * Test the implementation of this descriptor.
     *
     * @return <tt>true</tt> if the implementation is correct
     */
    public boolean testDescriptor()
    {
        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
