///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ConjElectroTopolState.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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

import joelib2.feature.types.atomlabel.AtomInConjEnvironment;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Conjugated electrotopological state descriptor.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cite wz03
 * @.cite wfz04a
 * @.cite wfz04b
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:31 $
 */
public class ConjElectroTopolState implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            ConjElectroTopolState.class.getName());
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
        new Class[]
        {
            AtomInConjEnvironment.class, APropertyDistanceMatrix.class,
            IntrinsicState.class
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;
    private double influenceOfDistance;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public ConjElectroTopolState()
    {
        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                joelib2.feature.result.AtomDynamicResult.class.getName());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return ConjElectroTopolState.class.getName();
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

        // remove hydrogens
        Molecule mol = (Molecule) molOriginal.clone();
        mol.deleteHydrogens();

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

        // get atom properties or calculate if not already available
        FeatureResult tmpPropResult;
        tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                AtomInConjEnvironment.getName());

        if (!(tmpPropResult instanceof AtomProperties))
        {
            // should never happen
            logger.error("Property '" + AtomInConjEnvironment.getName() +
                "' must be an atom type to calculate the " + getName() + ".");

            return null;
        }

        tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                IntrinsicState.getName());

        AtomProperties istates;

        if (tmpPropResult instanceof AtomProperties)
        {
            istates = (AtomProperties) tmpPropResult;
        }
        else
        {
            // should never happen
            logger.error("Property '" + IntrinsicState.getName() +
                "' must be an atom type to calculate the " + getName() + ".");

            return null;
        }

        tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                ConjugatedTopologicalDistance.getName());

        AtomProperties ctdist;

        if (tmpPropResult instanceof AtomProperties)
        {
            ctdist = (AtomProperties) tmpPropResult;
        }
        else
        {
            // should never happen
            logger.error("Property '" +
                ConjugatedTopologicalDistance.getName() +
                "' must be an atom type to calculate the " + getName() + ".");

            return null;
        }

        // get distance matrix or calculate if not already available
        FeatureResult tmpResult = null;
        String distanceMatrixKey = APropertyDistanceMatrix.getName();

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

        // get electrotopological state index
        int s = mol.getAtomsSize();

        double[] cestates = (double[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.DOUBLE, s);
        double di;
        double k = influenceOfDistance;
        int i_1;
        int j_1;
        double ctd;

        for (int i = 0; i < s; i++)
        {
            i_1 = i + 1;
            di = 0.0;
            ctd = k / ctdist.getDoubleValue(i_1);

            for (int j = 0; j < s; j++)
            {
                j_1 = j + 1;

                // use only connected atoms
                if ((distances[i][j] != 0) &&
                        (distances[i][j] != Integer.MAX_VALUE))
                {
                    di += ((istates.getDoubleValue(i_1) -
                                istates.getDoubleValue(j_1)) /
                            Math.pow((distances[i][j] + 1), ctd));

                    //                            System.out.print(" "+distances[i][j]);
                }
            }

            cestates[i] = istates.getDoubleValue(i_1) + di;

            if (logger.isDebugEnabled())
            {
                logger.debug(" cdist[" + i_1 + "]=" +
                    ctdist.getDoubleValue(i_1) + " istate[" + i_1 + "]=" +
                    istates.getDoubleValue(i_1) + " cestate[" + i_1 + "]=" +
                    cestates[i]);
            }
        }

        // save result
        result.setArray(cestates);

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
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
