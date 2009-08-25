///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: CharacteristicPolynomial.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.5 $
//            $Date: 2005/03/03 07:13:36 $
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

import joelib2.feature.types.atomlabel.AtomValence;

import joelib2.molecule.Molecule;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Calculates the coefficients for the characteristic polynomial of a weighted
 * graph.
 *
 * The characteristic polynomial of a graph can be seen as the analytical interpretation of
 * the eigenvalues of the (weighted) adjaceny matrix.
 *
 * @.author wegnerj
 * @.wikipedia Characteristic polynomial
 * @.wikipedia Graph theory
 * @.cite br90
 * @.cite tri92
 * @.license GPL
 * @.cvsversion $Revision: 1.5 $, $Date: 2005/03/03 07:13:36 $
 */
public class CharacteristicPolynomial implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.5 $";
    private static final String RELEASE_DATE = "$Date: 2005/03/03 07:13:36 $";

    private static Category logger = Category.getInstance(
            CharacteristicPolynomial.class.getName());

    public final static String ATOM_PROPERTY = "ATOM_PROPERTY";
    public final static String BOND_PROPERTY = "BOND_PROPERTY";
    public final static String POLYNOMIAL_MINIMUM = "POLYNOMIAL_MINIMUM";
    public final static String POLYNOMIAL_MAXIMUM = "POLYNOMIAL_MAXIMUM";
    public final static String POLYNOMIAL_STEP = "POLYNOMIAL_STEP";
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(BOND_PROPERTY, "java.lang.String",
                "Bond property to use.", true, null),
            new BasicProperty(ATOM_PROPERTY, "java.lang.String",
                "Atom property to use.", true, null),
            new BasicProperty(POLYNOMIAL_MINIMUM, "java.lang.Double",
                "Minimum value of the characteristic polynomial.", true,
                new Double(-2.5)),
            new BasicProperty(POLYNOMIAL_MAXIMUM, "java.lang.Double",
                "Maximum value of the characteristic polynomial.", true,
                new Double(2.5)),
            new BasicProperty(POLYNOMIAL_STEP, "java.lang.Double",
                "Step width of the characteristic polynomial.", true,
                new Double(0.05)),
        };

    private static final Class[] DEPENDENCIES =
        new Class[]{AtomValence.class, DistanceMatrix.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private String atomLabelName;
    private String bondLabelName;

    private BasicFeatureInfo descInfo;
    private double maximum;
    private double minimum;
    private double stepWidth;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Constructor for the GlobalTopologicalChargeIndex object
     */
    public CharacteristicPolynomial()
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
        return CharacteristicPolynomial.class.getName();
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
     * Description of the Method
     *
     * @param mol
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception FeatureException
     *                Description of the Exception
     */
    public FeatureResult calculate(Molecule mol) throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, null);
    }

    /**
     * Description of the Method
     *
     * @param mol
     *            Description of the Parameter
     * @param initData
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception FeatureException
     *                Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, Map properties)
        throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, properties);
    }

    /**
     * Description of the Method
     *
     * @param mol
     *            Description of the Parameter
     * @param descResult
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception FeatureException
     *                Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult)
        throws FeatureException
    {
        return calculate(mol, descResult, null);
    }

    /**
     * Description of the Method
     *
     * @param initData
     *            Description of the Parameter
     * @param descResult
     *            Description of the Parameter
     * @param molOriginal
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception FeatureException
     *                Description of the Exception
     */
    public FeatureResult calculate(Molecule molOriginal,
        FeatureResult descResult, Map properties) throws FeatureException
    {
        AtomDynamicResult result = null;

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

        if (molOriginal.isEmpty())
        {
            return null;
        }

        Molecule mol = null;

        if (atomLabelName != null)
        {
            mol = (Molecule) molOriginal.clone(true,
                    new String[]{atomLabelName});
        }
        else
        {
            mol = (Molecule) molOriginal.clone(false);
        }

        mol.deleteHydrogens();

        DynamicArrayResult coefficients = (DynamicArrayResult) FeatureHelper
            .instance().featureFrom(mol,
                CharacteristicPolynomialCoefficients.getName());
        double[] coeffArr = (double[]) coefficients.getArray();
        int size = 0;

        for (double value = minimum; value <= maximum;
                value = value + stepWidth)
        {
            size++;
        }

        double[] polynomial = (double[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.DOUBLE, size);
        calculatePolynomial(polynomial, coeffArr, minimum, maximum, stepWidth);

        result.setArray(polynomial);

        return result;
    }

    /**
     * Description of the Method
     */
    public void clear()
    {
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public BasicFeatureInfo getDescInfo()
    {
        return descInfo;
    }

    /**
     * Sets the descriptionFile attribute of the Descriptor object
     *
     * Gets the description attribute of the Descriptor object
     *
     * @return The description value
     * @return The description value
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
     * Description of the Method
     *
     * @param initData
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean initialize(Map properties)
    {
        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition or missing property entry.");

            return false;
        }

        atomLabelName = (String) PropertyHelper.getProperty(this, ATOM_PROPERTY,
                properties);
        bondLabelName = (String) PropertyHelper.getProperty(this, BOND_PROPERTY,
                properties);

        Double dproperty = (Double) PropertyHelper.getProperty(this,
                POLYNOMIAL_MINIMUM, properties);

        if (dproperty == null)
        {
            minimum = -2.5;
        }
        else
        {
            minimum = dproperty.doubleValue();
        }

        dproperty = (Double) PropertyHelper.getProperty(this,
                POLYNOMIAL_MAXIMUM, properties);

        if (dproperty == null)
        {
            maximum = 2.5;
        }
        else
        {
            maximum = dproperty.doubleValue();
        }

        dproperty = (Double) PropertyHelper.getProperty(this, POLYNOMIAL_STEP,
                properties);

        if (dproperty == null)
        {
            stepWidth = 0.05;
        }
        else
        {
            stepWidth = dproperty.doubleValue();
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
     * @param polynomial
     * @param coeffArr
     * @param minimum2
     * @param maximum2
     * @param stepWidth2
     */
    private void calculatePolynomial(double[] polynomial, double[] coeffArr,
        double minimum, double maximum, double stepWidth)
    {
        int size = 0;

        for (double value = minimum; value <= maximum;
                value = value + stepWidth)
        {
            // start with c0=1
            double valueSum = Math.pow(value, coeffArr.length);

            // now calculate the rest
            for (int coeff = 0; coeff < coeffArr.length; coeff++)
            {
                valueSum = valueSum -
                    (coeffArr[coeff] *
                        Math.pow(value, coeffArr.length - 1 - coeff));
            }

            polynomial[size] = valueSum;
            size++;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
