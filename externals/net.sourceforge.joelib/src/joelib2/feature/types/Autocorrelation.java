///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Autocorrelation.java,v $
//  Purpose:  Moreau-Broto autocorrelation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2005/02/24 16:58:57 $
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
import joelib2.feature.result.IntMatrixResult;

import joelib2.feature.types.atomlabel.AtomPartialCharge;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Moreau-Broto autocorrelation.
 *
 * @.author     wegnerj
 * @.wikipedia Autocorrelation
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/24 16:58:57 $
 * @.cite bmv84
 */
public class Autocorrelation implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.11 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/24 16:58:57 $";
    private static Category logger = Category.getInstance(Autocorrelation.class
            .getName());
    public final static String ATOM_PROPERTY = "ATOM_PROPERTY";
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(ATOM_PROPERTY, "java.lang.String",
                "Atom property to use.", true, AtomPartialCharge.getName()),
        };
    private static final Class[] DEPENDENCIES =
        new Class[]{DistanceMatrix.class, AtomPartialCharge.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;
    private String propertyName;

    //~ Constructors ///////////////////////////////////////////////////////////

    public Autocorrelation()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.APropDoubleArrResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return Autocorrelation.class.getName();
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

        Molecule mol = (Molecule) molOriginal.clone(true,
                new String[]{propertyName});
        mol.deleteHydrogens();

        //System.out.print("propertyName: "+propertyName);
        // get distance matrix or calculate if not already available
        FeatureResult tmpResult;
        String distanceMatrixKey = DistanceMatrix.getName();
        tmpResult = FeatureHelper.instance().featureFrom(mol,
                distanceMatrixKey);

        if (!(tmpResult instanceof IntMatrixResult))
        {
            logger.error("Needed descriptor '" + distanceMatrixKey +
                "' should be of type " + IntMatrixResult.class.getName() +
                ". " + getName() + " can not be calculated.");

            return null;
        }

        IntMatrixResult distResult = (IntMatrixResult) tmpResult;
        int[][] distances = distResult.value;

        // get maximum distance value
        int maxDistance = -Integer.MAX_VALUE;

        for (int i = 0; i < distances.length; i++)
        {
            for (int ii = 0; ii < i; ii++)
            {
                if (maxDistance < distances[i][ii])
                {
                    maxDistance = distances[i][ii];
                }
            }
        }

        // get atom properties or calculate if not already available
        FeatureResult tmpPropResult;
        tmpPropResult = FeatureHelper.instance().featureFrom(mol, propertyName);

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

        // calculate autocorrelation
        //              System.out.println("maxDistance:"+maxDistance);
        if (maxDistance < 0)
        {
            logger.warn("Possibly invalid molecule or only one atom in " +
                mol.getTitle());
            maxDistance = 0;

            //return null;
        }

        if (maxDistance == Integer.MAX_VALUE)
        {
            logger.warn("Some atoms which where never visited by BFS in " +
                mol.getTitle());

            //maxDistance=0;
            return null;
        }

        double[] acValues = new double[maxDistance + 1];
        double value;

        //     System.out.println("atoms:"+mol.numAtoms()+" distances:"+distances.length);
        for (int i = 0; i < distances.length; i++)
        {
            for (int ii = 0; ii <= i; ii++)
            {
                value = atomProperties.getDoubleValue(i + 1) *
                    atomProperties.getDoubleValue(ii + 1);
                acValues[distances[i][ii]] += value;
                acValues[distances[ii][i]] += value;
            }

            //System.out.print(" "+atomProperties.getDoubleValue(i + 1));
        }

        //System.out.println("");
        //              for (int i = 0; i < acValues.length; i++) {
        //                      System.out.print(" "+acValues[i]);
        //              }
        //              System.out.println("");
        // save result
        result.value = acValues;
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

        String property = (String) PropertyHelper.getProperty(this,
                ATOM_PROPERTY, properties);

        if (property == null)
        {
            propertyName = AtomPartialCharge.getName();
        }
        else
        {
            propertyName = property;
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
