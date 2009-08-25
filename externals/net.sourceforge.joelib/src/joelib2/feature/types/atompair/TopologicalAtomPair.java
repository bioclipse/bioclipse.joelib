///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: TopologicalAtomPair.java,v $
//  Purpose:  Atom pair descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:32 $
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
package joelib2.feature.types.atompair;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.BasicFeatureDescription;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.ResultFactory;

import joelib2.feature.result.IntMatrixResult;

import joelib2.feature.types.DistanceMatrix;
import joelib2.feature.types.atomlabel.AtomType;
import joelib2.feature.types.atomlabel.AtomValence;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Category;


/**
 * Calculates topological atom type pair (depends on atom properties used).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:32 $
 */
public class TopologicalAtomPair implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:32 $";
    private static Category logger = Category.getInstance(
            TopologicalAtomPair.class.getName());
    public final static String ATOM_PROPERTIES_NOMINAL =
        "ATOM_PROPERTIES_NOMINAL";
    public final static String ATOM_PROPERTIES_NUMERIC =
        "ATOM_PROPERTIES_NUMERIC";
    private static final Class[] DEPENDENCIES =
        new Class[]{DistanceMatrix.class, AtomType.class, AtomValence.class};

    // define default atom properties:
    //////////////////////////////////
    //  Electronegativity_pauling
    //  Electrogeometrical_state_index
    //  Conjugated_topological_distance
    //  Graph_potentials
    //  Conjugated_electrotopological_state_index
    //  Atom_mass
    //  Atom_van_der_waals_volume
    //  Atom_in_conjugated_environment
    //  Atom_valence
    //  Intrinsic_state
    //  Electrotopological_state_index
    //  Electron_affinity
    //  Gasteiger_Marsili
    //  public final static String DEFAULT_ATOM_PROPERTIES[] = new String[] { "Atom_valence", "Gasteiger_Marsili", "Intrinsic_state"};
    public final static String[] DEFAULT_ATOM_PROPERTIES_NUMERIC =
        new String[]{AtomValence.getName()};
    public final static String[] DEFAULT_ATOM_PROPERTIES_NOMINAL =
        new String[]{AtomType.getName()};
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(ATOM_PROPERTIES_NOMINAL, "[Ljava.lang.String;",
                "Nominal atom properties to define atom pair atom types.", true,
                DEFAULT_ATOM_PROPERTIES_NOMINAL),
            new BasicProperty(ATOM_PROPERTIES_NUMERIC, "[Ljava.lang.String;",
                "Numeric atom properties to define atom pair atom types.", true,
                DEFAULT_ATOM_PROPERTIES_NUMERIC),
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;

    // store informations for caching properties
    private Molecule molCache;
    private Molecule molCacheDeprotonated;
    private AtomProperties[] nominalAPCache;
    private String[] nominalAPNames;
    private AtomProperties[] numericAPCache;
    private String[] numericAPNames;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public TopologicalAtomPair()
    {
        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.types.atompair.AtomPairResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return TopologicalAtomPair.class.getName();
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
        AtomPairResult result = null;

        // check if the result type is correct
        if (!(descResult instanceof AtomPairResult))
        {
            logger.error(descInfo.getName() + " result should be of type " +
                AtomPairResult.class.getName() + " but it's of type " +
                descResult.getClass().toString());

            return null;
        }

        // initialize result type, if not already initialized
        else
        {
            result = (AtomPairResult) descResult;
        }

        // check if the init type is correct
        if (!initialize(properties))
        {
            return null;
        }

        // TODO : implement synchronized molecule cache (singleton class) with
        // TODO : protonated and deprotonated molecules !!!
        Molecule mol = null;
        AtomProperties[] nominalAP;
        AtomProperties[] numericAP;

        if (molCache != molOriginal)
        {
            // remove hydrogens
            mol = (Molecule) molOriginal.clone();
            mol.deleteHydrogens();

            // get atom properties or calculate if not already available
            FeatureResult tmpPropResult;
            nominalAP = new AtomProperties[nominalAPNames.length];

            for (int i = 0; i < nominalAPNames.length; i++)
            {
                tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                        nominalAPNames[i]);

                if (tmpPropResult instanceof AtomProperties)
                {
                    nominalAP[i] = (AtomProperties) tmpPropResult;
                }
                else
                {
                    // should never happen
                    logger.error("Property '" + nominalAPNames[i] +
                        "' must be an atom type to calculate the " + getName() +
                        ".");

                    return null;
                }
            }

            numericAP = new AtomProperties[numericAPNames.length];

            for (int i = 0; i < numericAPNames.length; i++)
            {
                tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                        numericAPNames[i]);

                if (tmpPropResult instanceof AtomProperties)
                {
                    numericAP[i] = (AtomProperties) tmpPropResult;
                }
                else
                {
                    // should never happen
                    logger.error("Property '" + numericAPNames[i] +
                        "' must be an atom type to calculate the " + getName() +
                        ".");

                    return null;
                }
            }
        }
        else
        {
            mol = this.molCacheDeprotonated;
            nominalAP = nominalAPCache;
            numericAP = numericAPCache;
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

        // calculate atom pair descriptor !!!
        AtomPair atomPair = null;
        Hashtable atomPairs = new Hashtable();
        int[] ia;

        for (int i = 0; i < distances.length; i++)
        {
            // visit only one triangle of the symmetric distance matrix
            for (int j = i; j < distances.length; j++)
            {
                atomPair = new AtomPair(nominalAP, numericAP,
                        mol.getAtom(i + 1), mol.getAtom(j + 1),
                        distances[i][j]);

                if (atomPairs.containsKey(atomPair))
                {
                    // increment atom pair counter
                    ia = (int[]) atomPairs.get(atomPair);
                    ia[0]++;
                }
                else
                {
                    // add new atom pair counter
                    atomPairs.put(atomPair, new int[]{1});

                    //                                  System.out.println("put "+atomPair);
                }

                //System.out.println(atomPair+" has hash code: "+atomPair.hashCode()+" stored:"+atomPairs.containsKey(atomPair));
                //System.out.println("-----------------------------------");
            }
        }

        // store informations
        result.atomPairs = atomPairs;
        result.nominalAP = nominalAPNames;
        result.numericAP = numericAPNames;

        // cache molecules and atom properties
        molCache = molOriginal;
        molCacheDeprotonated = mol;
        nominalAPCache = nominalAP;
        numericAPCache = numericAP;

        //System.out.println(atomPairs);
        //              System.out.println(result);
        return result;
    }

    /**
     *  Description of the Method
     */
    public void clear()
    {
        numericAPNames = null;
        nominalAPNames = null;
        molCache = null;
        numericAPCache = null;
        nominalAPCache = null;
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

        String[] property = (String[]) PropertyHelper.getProperty(this,
                ATOM_PROPERTIES_NOMINAL, properties);

        if (property == null)
        {
            nominalAPNames = DEFAULT_ATOM_PROPERTIES_NOMINAL;
        }
        else
        {
            nominalAPNames = property;
        }

        property = (String[]) PropertyHelper.getProperty(this,
                ATOM_PROPERTIES_NUMERIC, properties);

        if (property == null)
        {
            numericAPNames = DEFAULT_ATOM_PROPERTIES_NUMERIC;
        }
        else
        {
            numericAPNames = property;
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
