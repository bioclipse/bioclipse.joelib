///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DistanceCalculation.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
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
package joelib2.process.types;

import joelib2.io.BasicIOType;

import joelib2.math.similarity.BasicFeatureMetrics;

import joelib2.molecule.Molecule;

import joelib2.process.BasicProcess;
import joelib2.process.MoleculeProcessException;

import joelib2.util.BasicProperty;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;


/**
 *  Calling processor classes if the filter rule fits.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:38 $
 */
public class DistanceCalculation extends BasicProcess
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.process.types.DistanceCalculation");

    //~ Instance fields ////////////////////////////////////////////////////////

    //  private final static  JOEProperty[]  ACCEPTED_PROPERTIES    = new JOEProperty[]{
    //      new JOEProperty("NUMBER_OF_BINS", "java.lang.Integer", "Number of bins to create.", true),
    //      };
    private BasicFeatureMetrics comparison;
    private double[] distanceValues;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescSelectionWriter object
     */
    public DistanceCalculation()
    {
        clear();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean clear()
    {
        return true;
    }

    public double[] getDistanceValues()
    {
        return distanceValues;
    }

    public final List getTargetMols()
    {
        return comparison.getTargetMols();
    }

    /**
     * Description of the Method
     *
     * @param _statistic  Description of the Parameter
     */
    public void init(BasicFeatureMetrics _comparison)
    {
        comparison = _comparison;
    }

    public void init(Molecule target, String descriptor) throws Exception
    {
        logger.info("Checking target file for comparison.");
        comparison = new BasicFeatureMetrics(target);
        comparison.setComparisonDescriptor(descriptor);
    }

    public void init(Molecule target, String[] descriptors) throws Exception
    {
        logger.info("Checking target file for comparison.");
        comparison = new BasicFeatureMetrics(target);
        comparison.setComparisonDescriptors(descriptors);
    }

    /**
     *  Description of the Method
     *
     * @param inType         Description of the Parameter
     * @param inStream       Description of the Parameter
     * @param _numberOfBins  Description of the Parameter
     * @exception Exception  Description of the Exception
     */
    public void init(BasicIOType inType, String targetFile, String descriptor)
        throws Exception
    {
        logger.info("Checking target file for comparison.");
        comparison = new BasicFeatureMetrics(inType, targetFile);
        comparison.setComparisonDescriptor(descriptor);
    }

    public void init(BasicIOType inType, String targetFile,
        String[] descriptors) throws Exception
    {
        logger.info("Checking target file for comparison.");
        comparison = new BasicFeatureMetrics(inType, targetFile);
        comparison.setComparisonDescriptors(descriptors);
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public BasicProperty[] neededProperties()
    {
        //    return ACCEPTED_PROPERTIES;
        return null;
    }

    /**
     *  Description of the Method
     *
     * @param mol                      Description of the Parameter
     * @param properties               Description of the Parameter
     * @return                         Description of the Return Value
     * @exception MoleculeProcessException  Description of the Exception
     */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException
    {
        if (comparison == null)
        {
            return false;
        }

        try
        {
            super.process(mol, properties);
        }
        catch (MoleculeProcessException e)
        {
            throw new MoleculeProcessException("Properties for " +
                this.getClass().getName() + " not correct.");
        }

        distanceValues = comparison.compare(mol, null, distanceValues);

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
