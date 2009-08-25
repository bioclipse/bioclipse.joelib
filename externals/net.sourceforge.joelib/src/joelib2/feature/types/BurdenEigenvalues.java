///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BurdenEigenvalues.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
//            $Date: 2005/02/24 16:58:58 $
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

import jmat.data.Matrix;

import jmat.data.matrixDecompositions.EigenvalueDecomposition;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.BasicFeatureDescription;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.ResultFactory;

import joelib2.feature.result.APropDoubleArrResult;

import joelib2.feature.types.atomlabel.AtomValence;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Burden matrix descriptor (depends on single atom property used).
 *
 * @.author     wegnerj
 * @.wikipedia Characteristic polynomial
 * @.wikipedia Graph theory
 * @.license GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/24 16:58:58 $
 */
public class BurdenEigenvalues implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.12 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/24 16:58:58 $";
    private static Category logger = Category.getInstance(
            BurdenEigenvalues.class);
    public final static String ATOM_PROPERTY = "ATOM_PROPERTY";
    public final static String ATOM_PROPERTY_WEIGHT = "ATOM_PROPERTY_WEIGHT";
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(ATOM_PROPERTY, "java.lang.String",
                "Atom property to use.", true, AtomValence.getName()),
            new BasicProperty(ATOM_PROPERTY_WEIGHT, "java.lang.Double",
                "Atom property weight to use.", true, new Double(1.0)),
        };
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomValence.class, BondInAromaticSystem.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private double atomPropWeight;

    private BasicFeatureInfo descInfo;
    private String propertyName;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the BCUT object
     */
    public BurdenEigenvalues()
    {
        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.APropDoubleArrResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static double[] getBurdenEigenvalues(Molecule mol,
        String propertyName, double weight)
    {
        if ((propertyName == null) || (mol == null))
        {
            return null;
        }

        // remove hydrogens
        // could be a problem if the atom properties can not be calculated by JOELib
        // In this case use: mol.clone(true);
        Molecule hDepletedMol = (Molecule) mol.clone(true,
                new String[]{propertyName});
        hDepletedMol.deleteHydrogens();

        // get atom properties or calculate if not already available
        FeatureResult tmpPropResult;

        try
        {
            tmpPropResult = FeatureHelper.instance().featureFrom(hDepletedMol,
                    propertyName);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());

            return null;
        }

        AtomProperties properties;

        if (tmpPropResult instanceof AtomProperties)
        {
            properties = (AtomProperties) tmpPropResult;
        }
        else
        {
            logger.error("Property '" + propertyName +
                "' must be an atom type for calculating " + getName() +
                " but it's " + tmpPropResult.getClass().getName() + ".");

            return null;
        }

        // calculate burden
        double[] burdenValues = new double[hDepletedMol.getAtomsSize()];
        Matrix burden = new Matrix(hDepletedMol.getAtomsSize(),
                hDepletedMol.getAtomsSize(), 0.001);
        Atom atom;
        int atomIndex;

        AtomIterator ait = hDepletedMol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            atomIndex = atom.getIndex();

            double x = 0;
            BondIterator bit = atom.bondIterator();
            Bond bond;

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                if (BondInAromaticSystem.isAromatic(bond))
                {
                    x = 1.5;
                }
                else
                {
                    x = (double) bond.getBondOrder();
                }

                x *= 0.1;

                Atom nbrAtom = bond.getNeighbor(atom);

                if ((atom.getValence() == 1) || (nbrAtom.getValence() == 1))
                {
                    x += 0.01;
                }

                burden.set(atomIndex - 1, nbrAtom.getIndex() - 1, x);
                burden.set(nbrAtom.getIndex() - 1, atomIndex - 1, x);
            }

            if (Double.isInfinite(properties.getDoubleValue(atomIndex)))
            {
                logger.error("Atom property contains infinity value.");

                return null;
            }

            burden.set(atomIndex - 1, atomIndex - 1,
                properties.getDoubleValue(atomIndex) * weight);

            //System.out.print(" "+properties.getDoubleValue(atomIndex)*weight+" weight:"+weight);
        }

        //System.out.println("burdenMatrix: " + burden.toString());
        EigenvalueDecomposition eigDecomp = null;

        try
        {
            eigDecomp = burden.eig();
        }
        catch (Exception ex)
        {
            logger.error("Eigenvalues could not be calculated.");

            //ex.printStackTrace();
            return null;
        }

        Matrix eigenvalueMatrix = eigDecomp.getD_Real();

        for (int i = 0; i < hDepletedMol.getAtomsSize(); i++)
        {
            burdenValues[i] = eigenvalueMatrix.get(i, i);
        }

        return burdenValues;
    }

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return BurdenEigenvalues.class.getName();
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
    public FeatureResult calculate(Molecule mol, FeatureResult descResult,
        Map properties) throws FeatureException
    {
        APropDoubleArrResult result = null;

        // check if the result type is correct
        if (!(descResult instanceof APropDoubleArrResult))
        {
            logger.error(descInfo.getName() + " result should be of type " +
                APropDoubleArrResult.class.getName() + " but it's of type " +
                descResult.getClass().toString());

            return null;
        }

        // initialize result type, if not already initialized
        else
        {
            result = (APropDoubleArrResult) descResult;
        }

        // check if the init type is correct
        if (!initialize(properties))
        {
            return null;
        }

        double[] burdenValues = getBurdenEigenvalues(mol, propertyName,
                atomPropWeight);

        //    Arrays.sort(burdenValues);
        //    double burden_sort[] = new double[burdenValues.length];
        //    for (int i = 0; i < burdenValues.length; i++)
        //    {
        //    burden_sort[i] = burdenValues[burdenValues.length-i-1];
        //    }
        // save result
        result.value = burdenValues;
        result.atomProperty = propertyName;

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
     * @return            The description value
     */

    //  public void setDescInfo(DescriptorInfo _descInfo)
    //  {
    //    descInfo = _descInfo;
    //  }

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

        String property = (String) PropertyHelper.getProperty(this,
                ATOM_PROPERTY, properties);

        if (property == null)
        {
            // should never happen
            propertyName = AtomValence.getName();
        }
        else
        {
            propertyName = property;
        }

        Double dproperty = (Double) PropertyHelper.getProperty(this,
                ATOM_PROPERTY_WEIGHT, properties);

        if (dproperty == null)
        {
            atomPropWeight = 1.0;
        }
        else
        {
            atomPropWeight = dproperty.doubleValue();
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
