///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ElectrotopologicalState.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
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

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.BasicFeatureDescription;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.ResultFactory;

import joelib2.feature.result.AtomDynamicResult;
import joelib2.feature.result.DynamicArrayResult;
import joelib2.feature.result.IntMatrixResult;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Electrotolpogical state descriptor (ESTATE).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:31 $
 */
public class ElectrotopologicalState implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.11 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            ElectrotopologicalState.class);
    public final static String DISTANCE_INFLUENCE = "DISTANCE_INFLUENCE";
    public final static double DEFAULT_DISTANCE_INFLUENCE = 2.0;
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(DISTANCE_INFLUENCE, "java.lang.Double",
                "Influence of distance (default=2).", true,
                new Double(DEFAULT_DISTANCE_INFLUENCE)),
        };
    private static final Class[] DEPENDENCIES =
        new Class[]{IntrinsicState.class, DistanceMatrix.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;
    private double influenceOfDistance;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public ElectrotopologicalState()
    {
        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_GEOMETRICAL, null,
                joelib2.feature.result.AtomDynamicResult.class.getName());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return ElectrotopologicalState.class.getName();
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
        return ACCEPTED_PROPERTIES;
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
    public FeatureResult calculate(Molecule molOriginal,
        FeatureResult descResult, Map properties) throws FeatureException
    {
        AtomDynamicResult result = null;

        Molecule mol = molOriginal;

        // remove hydrogens
        //Molecule mol = (Molecule) molOriginal.clone();
        //mol.deleteHydrogens();

        // check if the result type is correct
        if (!(descResult instanceof AtomDynamicResult))
        {
            logger.error(descInfo.getName() + " result should be of type " +
                AtomDynamicResult.class.getName() + " but it's of type " +
                descResult.getClass().toString());

            return null;
        }

        // initialize result type, if not already initialized
        else
        {
            result = (AtomDynamicResult) descResult;
        }

        // check if the init type is correct
        if (!initialize(properties))
        {
            return null;
        }

        // get distance matrix or calculate if not already available
        FeatureResult tmpResult = null;
        String distanceMatrixKey = DistanceMatrix.getName();

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol,
                    distanceMatrixKey);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());
            logger.error("Can not calculate distance matrix for " + getName() +
                ".");

            return null;
        }

        if (!(tmpResult instanceof IntMatrixResult))
        {
            logger.error("Needed descriptor '" + distanceMatrixKey +
                "' should be of type " + IntMatrixResult.class.getName() +
                ". " + getName() + " can not be calculated.");

            return null;
        }

        IntMatrixResult distResult = (IntMatrixResult) tmpResult;
        int[][] distances = distResult.value;

        // get atom properties or calculate if not already available
        AtomProperties atomProperties = getIntrinsicState(mol);

        // get electrotopological state index
        int size = mol.getAtomsSize();

        if (size != atomProperties.getSize())
        {
            // try to recalculate
            mol.deleteData(IntrinsicState.getName());
            atomProperties = getIntrinsicState(mol);

            if (size != atomProperties.getSize())
            {
                logger.error(mol.getTitle() + " has " + size +
                    " atoms, but the IntrinsicState uses " +
                    atomProperties.getSize() + " atoms.");

                return null;
            }
        }

        double[] estates = (double[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.DOUBLE, size);
        double di;
        double k = influenceOfDistance;
        int i_1;
        int j_1;

        for (int i = 0; i < size; i++)
        {
            i_1 = i + 1;
            di = 0.0;

            for (int j = 0; j < size; j++)
            {
                j_1 = j + 1;

                di += ((atomProperties.getDoubleValue(i_1) -
                            atomProperties.getDoubleValue(j_1)) /
                        (Math.pow(distances[i][j] + 1, k)));

                //                              System.out.println("distance="+distances[i][j]);
            }

            estates[i] = atomProperties.getDoubleValue(i_1) + di;

            if (logger.isDebugEnabled())
            {
                logger.debug("istate[" + i_1 + "]=" +
                    atomProperties.getDoubleValue(i_1) + " estate[" + i_1 +
                    "]=" + estates[i]);
            }
        }

        // save result
        result.setArray(estates);

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
     *  Gets the description attribute of the Descriptor object
     *
     * @return            The description value
     * @return            The description value
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
     * @return           Description of the Return Value
     */
    public boolean initialize(Map properties)
    {
        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition or missing property entry.");

            return false;
        }

        Double property = (Double) PropertyHelper.getProperty(this,
                DISTANCE_INFLUENCE, properties);

        if (property == null)
        {
            influenceOfDistance = DEFAULT_DISTANCE_INFLUENCE;
        }
        else
        {
            influenceOfDistance = property.doubleValue();
        }

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

    /**
     * @param mol
     * @return
     * @throws FeatureException
     */
    private AtomProperties getIntrinsicState(Molecule mol)
        throws FeatureException
    {
        FeatureResult tmpPropResult;
        tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                IntrinsicState.getName());

        AtomProperties atomProperties = null;

        if (tmpPropResult instanceof AtomProperties)
        {
            atomProperties = (AtomProperties) tmpPropResult;
        }
        else
        {
            // should never happen
            logger.error("Property '" + IntrinsicState.getName() +
                "' must be an atom type to calculate the " + getName() + ".");
        }

        return atomProperties;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
