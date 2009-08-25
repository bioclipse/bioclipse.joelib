///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: WeightedBurdenEigenvalues.java,v $
//  Purpose:  Calculates a descriptor.
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

import joelib2.feature.types.bondlabel.BondInAromaticSystem;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * @.author     wegnerj
 */
public class WeightedBurdenEigenvalues implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            WeightedBurdenEigenvalues.class.getName());
    public final static String ATOM_PROPERTY1 = "ATOM_PROPERTY1";
    public final static String ATOM_PROPERTY2 = "ATOM_PROPERTY2";
    public final static String ATOM_PROPERTY3 = "ATOM_PROPERTY3";
    public final static String ATOM_PROPERTY4 = "ATOM_PROPERTY4";
    public final static String ATOM_PROPERTY5 = "ATOM_PROPERTY5";
    public final static String ATOM_PROPERTY6 = "ATOM_PROPERTY6";
    public final static String ATOM_PROPERTY1_WEIGHT = "ATOM_PROPERTY1_WEIGHT";
    public final static String ATOM_PROPERTY2_WEIGHT = "ATOM_PROPERTY2_WEIGHT";
    public final static String ATOM_PROPERTY3_WEIGHT = "ATOM_PROPERTY3_WEIGHT";
    public final static String ATOM_PROPERTY4_WEIGHT = "ATOM_PROPERTY4_WEIGHT";
    public final static String ATOM_PROPERTY5_WEIGHT = "ATOM_PROPERTY5_WEIGHT";
    public final static String ATOM_PROPERTY6_WEIGHT = "ATOM_PROPERTY6_WEIGHT";
    private static final String JAVA_LANG_STRING = "java.lang.String";
    private static final String JAVA_LANG_DOUBLE = "java.lang.Double";
    private static final String NOT_SET = "NOT_SET";
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(ATOM_PROPERTY1, JAVA_LANG_STRING,
                "Atom property one.", true, NOT_SET),
            new BasicProperty(ATOM_PROPERTY2, JAVA_LANG_STRING,
                "Atom property two.", true, NOT_SET),
            new BasicProperty(ATOM_PROPERTY3, JAVA_LANG_STRING,
                "Atom property three.", true, NOT_SET),
            new BasicProperty(ATOM_PROPERTY4, JAVA_LANG_STRING,
                "Atom property four.", true, NOT_SET),
            new BasicProperty(ATOM_PROPERTY5, JAVA_LANG_STRING,
                "Atom property five.", true, NOT_SET),
            new BasicProperty(ATOM_PROPERTY6, JAVA_LANG_STRING,
                "Atom property six.", true, NOT_SET),
            new BasicProperty(ATOM_PROPERTY1_WEIGHT, JAVA_LANG_DOUBLE,
                "Atom property weight one.", true, new Double(0.0)),
            new BasicProperty(ATOM_PROPERTY2_WEIGHT, JAVA_LANG_DOUBLE,
                "Atom property weight two.", true, new Double(0.0)),
            new BasicProperty(ATOM_PROPERTY3_WEIGHT, JAVA_LANG_DOUBLE,
                "Atom property weight three.", true, new Double(0.0)),
            new BasicProperty(ATOM_PROPERTY4_WEIGHT, JAVA_LANG_DOUBLE,
                "Atom property weight four.", true, new Double(0.0)),
            new BasicProperty(ATOM_PROPERTY5_WEIGHT, JAVA_LANG_DOUBLE,
                "Atom property weight five.", true, new Double(0.0)),
            new BasicProperty(ATOM_PROPERTY6_WEIGHT, JAVA_LANG_DOUBLE,
                "Atom property weight six.", true, new Double(0.0)),
        };
    private static final Class[] DEPENDENCIES =
        new Class[]{BondInAromaticSystem.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;
    private String[] propertyNames;
    private double[] weights;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the BCUT object
     */
    public WeightedBurdenEigenvalues()
    {
        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.APropDoubleArrResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static double[] getBurdenEigenvalues(Molecule mol,
        String[] propertyName, double[] weights)
    {
        if ((propertyName == null) || (mol == null))
        {
            return null;
        }

        // remove hydrogens
        // could be a problem if the atom properties can not be calculated by JOELib
        // In this case use: mol.clone(true);
        Molecule hDepletedMol = (Molecule) mol.clone();
        hDepletedMol.deleteHydrogens();

        int propSize = propertyName.length;
        FeatureResult tmpPropResult;
        AtomProperties[] properties = new AtomProperties[propSize];

        for (int i = 0; i < propSize; i++)
        {
            // get atom properties or calculate if not already available
            try
            {
                tmpPropResult = FeatureHelper.instance().featureFrom(
                        hDepletedMol, propertyName[i]);
            }
            catch (FeatureException ex)
            {
                logger.error(ex.toString());

                return null;
            }

            if (tmpPropResult instanceof AtomProperties)
            {
                properties[i] = (AtomProperties) tmpPropResult;
            }
            else
            {
                logger.error("Property '" + propertyName[i] +
                    "' must be an atom type to calculate the burden.");

                return null;
            }
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

            double matrixEntry = 0;
            BondIterator bit = atom.bondIterator();
            Bond bond;

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                if (BondInAromaticSystem.isAromatic(bond))
                {
                    matrixEntry = 1.5;
                }
                else
                {
                    matrixEntry = (double) bond.getBondOrder();
                }

                matrixEntry *= 0.1;

                Atom nbrAtom = bond.getNeighbor(atom);

                if ((atom.getValence() == 1) || (nbrAtom.getValence() == 1))
                {
                    matrixEntry += 0.01;
                }

                burden.set(atomIndex - 1, nbrAtom.getIndex() - 1, matrixEntry);
            }

            // set weighted atom properties
            double weightedProperty = 0.0;

            for (int i = 0; i < propSize; i++)
            {
                //weightedProperty += properties[i].getDoubleValue(atomIndex)*weights[i];
                weightedProperty += (scale(properties[i],
                            properties[i].getDoubleValue(atomIndex),
                            hDepletedMol.getAtomsSize()) * weights[i]);

                //weightedProperty += scale2(properties[i], properties[i].getDoubleValue(atomIndex), hDepletedMol.numAtoms())*weights[i];
            }

            burden.set(atomIndex - 1, atomIndex - 1, weightedProperty);
        }

        //     System.out.println("burdenMatrix: " + burden.toString());
        EigenvalueDecomposition eigDecomp = burden.eig();
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
        return WeightedBurdenEigenvalues.class.getName();
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

    public static double scale(AtomProperties props, double val,
        int numberAtoms)
    {
        double sqsum = 0.0;
        double result = 0.0;

        for (int i = 1; i <= numberAtoms; i++)
        {
            sqsum += (props.getDoubleValue(i) * props.getDoubleValue(i));

            //        System.out.println(" props.getDoubleValue(i):"+ props.getDoubleValue(i));
        }

        if (sqsum != 0.0)
        {
            result = val / sqsum;

            return result;
        }
        else
        {
            logger.error("Error: Sqsum = 0; DIV by 0!!");

            return 0;
        }
    }

    public static double scale2(AtomProperties props, double val,
        int numberAtoms)
    {
        double max = -Double.MAX_VALUE;
        double result;

        for (int i = 1; i >= numberAtoms; i++)
        {
            if (max < props.getDoubleValue(i))
            {
                max = props.getDoubleValue(i);
            }
        }

        result = val / max;

        return result;
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

        double[] burdenValues = getBurdenEigenvalues(mol, propertyNames,
                weights);

        //    Arrays.sort(burdenValues);
        //    double burden_sort[] = new double[burdenValues.length];
        //    for (int i = 0; i < burdenValues.length; i++)
        //    {
        //    burden_sort[i] = burdenValues[burdenValues.length-i-1];
        //    }
        // save result
        result.value = burdenValues;

        // set atom property name(s)
        int size = propertyNames.length;
        StringBuffer sb = new StringBuffer(size * 20);

        for (int i = 0; i < size; i++)
        {
            sb.append(propertyNames[i]);
            sb.append(':');
            sb.append(weights[i]);
            sb.append('_');
        }

        result.atomProperty = sb.toString();
        sb = null;

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

        String property;
        Double weight;
        List<String> tmpProperties = new Vector<String>(6);
        List<Double> tmpWeight = new Vector<Double>(6);

        for (int i = 1; i <= 6; i++)
        {
            property = (String) PropertyHelper.getProperty(this,
                    "ATOM_PROPERTY" + i, properties);

            if ((property == null) || property.equals("NOT_SET"))
            {
                break;
            }
            else
            {
                tmpProperties.add(property);
            }

            weight = (Double) PropertyHelper.getProperty(this,
                    "ATOM_PROPERTY" + i + "_WEIGHT", properties);

            if (weight == null)
            {
                break;
            }
            else
            {
                tmpWeight.add(weight);
            }
        }

        propertyNames = new String[tmpProperties.size()];
        weights = new double[tmpProperties.size()];

        for (int i = 0; i < tmpProperties.size(); i++)
        {
            propertyNames[i] = (String) tmpProperties.get(i);
            weights[i] = ((Double) tmpWeight.get(i)).doubleValue();
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
