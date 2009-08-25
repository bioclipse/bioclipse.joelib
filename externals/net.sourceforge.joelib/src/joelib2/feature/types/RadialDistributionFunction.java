///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RadialDistributionFunction.java,v $
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

import joelib2.feature.result.APropDoubleArrResult;
import joelib2.feature.result.DoubleMatrixResult;

import joelib2.feature.types.atomlabel.AtomPartialCharge;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Category;


/**
 * Radial Basis Function (RDF).
 *
 * @.author     wegnerj
 */
public class RadialDistributionFunction implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            RadialDistributionFunction.class.getName());

    public final static String ATOM_PROPERTY = "ATOM_PROPERTY";
    public final static String MIN_SPHERICAL_VOLUME = "MIN_SPHERICAL_VOLUME";
    public final static String MAX_SPHERICAL_VOLUME = "MAX_SPHERICAL_VOLUME";
    public final static String SPHERICAL_VOLUME_RESOLUTION =
        "SPHERICAL_VOLUME_RESOLUTION";
    public final static String SMOOTHING_FACTOR = "SMOOTHING_FACTOR";
    public final static String REMOVE_HYDROGENS = "REMOVE_HYDROGENS";
    public final static BasicProperty ATOM_PROPERTY_PROPERTY =
        new BasicProperty(ATOM_PROPERTY, "java.lang.String",
            "Atom property to use.", true, "Gasteiger_Marsili");
    public final static BasicProperty MIN_SPHERICAL_VOLUME_PROPERTY =
        new BasicProperty(MIN_SPHERICAL_VOLUME, "java.lang.Double",
            "Minimum spherical volume radius.", true, new Double(0.2));
    public final static BasicProperty MAX_SPHERICAL_VOLUME_PROPERTY =
        new BasicProperty(MAX_SPHERICAL_VOLUME, "java.lang.Double",
            "Maximum spherical volume radius.", true, new Double(10.0));
    public final static BasicProperty SPHERICAL_VOLUME_RESOLUTION_PROPERTY =
        new BasicProperty(SPHERICAL_VOLUME_RESOLUTION, "java.lang.Double",
            "Resolution to use for the spherical volume radius.", true,
            new Double(0.2));
    public final static BasicProperty SMOOTHING_FACTOR_PROPERTY =
        new BasicProperty(SMOOTHING_FACTOR, "java.lang.Double",
            "Smoothing parameter for the interatomic distances.", true,
            new Double(0.2));
    public final static BasicProperty REMOVE_HYDROGENS_PROPERTY =
        new BasicProperty(REMOVE_HYDROGENS, "java.lang.Boolean",
            "Remove hydrogens before calculating radial basis methodName.",
            true, Boolean.TRUE);
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            ATOM_PROPERTY_PROPERTY, MIN_SPHERICAL_VOLUME_PROPERTY,
            MAX_SPHERICAL_VOLUME_PROPERTY,
            SPHERICAL_VOLUME_RESOLUTION_PROPERTY, SMOOTHING_FACTOR_PROPERTY,
            REMOVE_HYDROGENS_PROPERTY
        };
    private static final Class[] DEPENDENCIES =
        new Class[]{GeomDistanceMatrix.class, AtomPartialCharge.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;
    private double maxSphericalVolume = 10.0;
    private double minSphericalVolume = 0.2;
    private String propertyName;
    private boolean removeHydrogens = true;
    private double smoothingFactorB = 25;
    private double sphericalVolumeResolution = 0.2;

    //~ Constructors ///////////////////////////////////////////////////////////

    public RadialDistributionFunction()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.APropDoubleArrResult");

        BasicPropertyHolder pHolder = BasicPropertyHolder.instance();
        Properties prop = pHolder.getProperties();
        String className = this.getClass().getName();
        double valueD;

        valueD = BasicPropertyHolder.getDouble(prop,
                className + ".minSphericalVolume", 0.1, 50, 0.2);

        if (!Double.isNaN(valueD))
        {
            minSphericalVolume = valueD;
            MIN_SPHERICAL_VOLUME_PROPERTY.setDefaultProperty(new Double(
                    minSphericalVolume));

            if (logger.isDebugEnabled())
            {
                logger.debug("Set minSphericalVolume=" + minSphericalVolume);
            }
        }

        valueD = BasicPropertyHolder.getDouble(prop,
                className + ".maxSphericalVolume", minSphericalVolume, 100, 10);

        if (!Double.isNaN(valueD))
        {
            maxSphericalVolume = valueD;
            MAX_SPHERICAL_VOLUME_PROPERTY.setDefaultProperty(new Double(
                    maxSphericalVolume));

            if (logger.isDebugEnabled())
            {
                logger.debug("Set maxSphericalVolume=" + maxSphericalVolume);
            }
        }

        valueD = BasicPropertyHolder.getDouble(prop,
                className + ".sphericalVolumeResolution", 0.01, 0.5, 0.2);

        if (!Double.isNaN(valueD))
        {
            sphericalVolumeResolution = valueD;
            SPHERICAL_VOLUME_RESOLUTION_PROPERTY.setDefaultProperty(new Double(
                    sphericalVolumeResolution));

            if (logger.isDebugEnabled())
            {
                logger.debug("Set sphericalVolumeResolution=" +
                    sphericalVolumeResolution);
            }
        }

        valueD = BasicPropertyHolder.getDouble(prop,
                className + ".smoothingFactor", 1.0, 100000.0, 25.0);

        if (!Double.isNaN(valueD))
        {
            smoothingFactorB = valueD;
            SMOOTHING_FACTOR_PROPERTY.setDefaultProperty(new Double(
                    smoothingFactorB));

            if (logger.isDebugEnabled())
            {
                logger.debug("Set smoothingFactor=" + smoothingFactorB);
            }
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return RadialDistributionFunction.class.getName();
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

        if (molOriginal.isEmpty())
        {
            result.value = new double[1];
            result.atomProperty = propertyName;
            logger.warn("Empty molecule '" + molOriginal.getTitle() + "'. " +
                getName() + " was set to ac[0]=0.");

            return result;
        }

        Molecule mol = null;

        if (removeHydrogens)
        {
            mol = (Molecule) molOriginal.clone(true,
                    new String[]{propertyName});
            mol.deleteHydrogens();
        }
        else
        {
            mol = molOriginal;
        }

        //System.out.print("propertyName: "+propertyName);
        // get distance matrix or calculate if not already available
        FeatureResult tmpResult;
        String distanceMatrixKey = GeomDistanceMatrix.getName();
        tmpResult = FeatureHelper.instance().featureFrom(mol,
                distanceMatrixKey);

        if (!(tmpResult instanceof DoubleMatrixResult))
        {
            logger.error("Needed descriptor '" + distanceMatrixKey +
                "' should be of type " + DoubleMatrixResult.class.getName() +
                ". " + getName() + " can not be calculated.");

            return null;
        }

        DoubleMatrixResult distResult = (DoubleMatrixResult) tmpResult;
        double[][] distances = distResult.value;

        //              double maxDistance = -Double.MAX_VALUE;
        //              for (int i = 0; i < distances.length; i++)
        //              {
        //                      for (int ii = 0; ii < i; ii++)
        //                      {
        //                              if (maxDistance < distances[i][ii])
        //                                      maxDistance = distances[i][ii];
        //                      }
        //              }
        //              System.out.println("Max distance: "+maxDistance);
        // get number of intervals
        int intervals = (int) ((maxSphericalVolume - minSphericalVolume) /
                sphericalVolumeResolution);

        // get atom properties or calculate if not already available
        FeatureResult tmpPropResult;
        tmpPropResult = FeatureHelper.instance().featureFrom(mol, propertyName);

        //System.out.println("Use atom property: "+propertyName);
        AtomProperties atomProperties;

        if (tmpPropResult instanceof AtomProperties)
        {
            atomProperties = (AtomProperties) tmpPropResult;
        }
        else
        {
            logger.error("Property '" + propertyName +
                "' must be an atom type to calculate the " + getName() + ".");

            return null;
        }

        // calculate radial basis methodName (RDF)
        int atoms = mol.getAtomsSize();
        int atoms_1 = mol.getAtomsSize() - 1;
        double[] rdfValues = new double[intervals + 1];
        double aPropWeights;
        double eTerm;
        int index = 0;
        double tmpRDF = 0.0;

        //     System.out.println("atoms:"+mol.numAtoms()+" distances:"+distances.length);
        for (double r = minSphericalVolume; r <= maxSphericalVolume;
                r += sphericalVolumeResolution)
        {
            tmpRDF = 0.0;

            for (int i = 0; i < atoms_1; i++)
            {
                for (int j = i + 1; j < atoms; j++)
                {
                    aPropWeights = atomProperties.getDoubleValue(i + 1) *
                        atomProperties.getDoubleValue(j + 1);

                    eTerm = (r - distances[i][j]);
                    eTerm *= eTerm;
                    tmpRDF += (aPropWeights *
                            Math.exp(-smoothingFactorB * eTerm));
                }
            }

            rdfValues[index] = tmpRDF;

            //                  scalingFactor += tmpRDF * tmpRDF;
            index++;
        }

        // this SCALING makes sense for Kohonen networks, but is not
        // necessary before we know what we want to do with these
        // descriptors
        //              scalingFactor = 1 / Math.sqrt(scalingFactor);
        //              for (int i = 0; i < rdfValues.length; i++)
        //              {
        //                      rdfValues[i] *= scalingFactor;
        //              }
        //System.out.println("");
        //              for (int i = 0; i < rdfValues; i++) {
        //                      System.out.print(" "+rdfValues[i]);
        //              }
        //              System.out.println("");
        // save result
        //System.out.println("Calculated "+propertyName+":RDF[0]="+rdfValues[0]+" use[0]:"+atomProperties.getDoubleValue(1));
        result.value = rdfValues;
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

        String propertyS = (String) PropertyHelper.getProperty(this,
                ATOM_PROPERTY, properties);

        if (propertyS == null)
        {
            propertyName = AtomPartialCharge.getName();
        }
        else
        {
            propertyName = propertyS;
        }

        //System.out.println("propertyName:"+propertyName);
        Double propertyD = (Double) PropertyHelper.getProperty(this,
                MIN_SPHERICAL_VOLUME, properties);

        if (propertyD != null)
        {
            minSphericalVolume = propertyD.doubleValue();
        }

        propertyD = (Double) PropertyHelper.getProperty(this,
                MAX_SPHERICAL_VOLUME, properties);

        if (propertyD != null)
        {
            maxSphericalVolume = propertyD.doubleValue();
        }

        propertyD = (Double) PropertyHelper.getProperty(this,
                SPHERICAL_VOLUME_RESOLUTION, properties);

        if (propertyD != null)
        {
            sphericalVolumeResolution = propertyD.doubleValue();
        }

        propertyD = (Double) PropertyHelper.getProperty(this, SMOOTHING_FACTOR,
                properties);

        if (propertyD != null)
        {
            smoothingFactorB = propertyD.doubleValue();
        }

        //System.out.println("Use smoothing ="+smoothingFactorB);
        Boolean propertyB = (Boolean) PropertyHelper.getProperty(this,
                REMOVE_HYDROGENS, properties);

        if (propertyD != null)
        {
            removeHydrogens = propertyB.booleanValue();
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
