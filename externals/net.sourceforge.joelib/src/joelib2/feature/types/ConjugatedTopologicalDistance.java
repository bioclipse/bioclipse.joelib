///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ConjugatedTopologicalDistance.java,v $
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

import joelib2.feature.AbstractDynamicAtomProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.result.DynamicArrayResult;
import joelib2.feature.result.IntMatrixResult;

import joelib2.feature.types.atomlabel.AtomInConjEnvironment;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import org.apache.log4j.Category;


/**
 * Conjugated topological distance.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cite wz03
 * @.cite wfz04a
 * @.cite wfz04b
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:31 $
 */
public class ConjugatedTopologicalDistance extends AbstractDynamicAtomProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            ConjugatedTopologicalDistance.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomInConjEnvironment.class, APropertyDistanceMatrix.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
    *  Constructor for the KierShape1 object
    */
    public ConjugatedTopologicalDistance()
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
        return ConjugatedTopologicalDistance.class.getName();
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

    public Object getAtomPropertiesArray(Molecule molOriginal)
    {
        // get atom properties or calculate if not already available
        FeatureResult tmpPropResult;

        // remove hydrogens
        Molecule mol = (Molecule) molOriginal.clone();
        mol.deleteHydrogens();

        try
        {
            tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                    AtomInConjEnvironment.getName());
        }
        catch (FeatureException e)
        {
            return null;
        }

        AtomProperties atomProperties;

        if (tmpPropResult instanceof AtomProperties)
        {
            atomProperties = (AtomProperties) tmpPropResult;
        }
        else
        {
            // should never happen
            logger.error("Property '" + AtomInConjEnvironment.getName() +
                "' must be an atom type to calculate the " + getName() + ".");

            return null;
        }

        // get distance matrix or calculate if not already available
        FeatureResult tmpResult = null;

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol,
                    APropertyDistanceMatrix.getName());
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
            logger.error("Needed descriptor '" +
                APropertyDistanceMatrix.getName() + "' should be of type " +
                IntMatrixResult.class.getName() + ". " + getName() +
                " can not be calculated.");

            return null;
        }

        IntMatrixResult distResult = (IntMatrixResult) tmpResult;
        int[][] distances = distResult.value;

        // get electrotopological state index
        int s = mol.getAtomsSize();
        int[] cdist = (int[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.INT, s);

        int i_1;
        int tmp = 0;

        for (int i = 0; i < s; i++)
        {
            i_1 = i + 1;

            try
            {
                tmp = ((AtomProperties) atomProperties).getIntValue(i_1);
            }
            catch (Exception ex)
            {
                // zip Exception ???
                // i don't get it ... this should NEVER be happen
                ex.printStackTrace();
            }

            cdist[i] = 1;

            for (int j = 0; j < s; j++)
            {
                if ((distances[i][j] == 0) ||
                        (distances[i][j] == Integer.MAX_VALUE))
                {
                    distances[i][j] = 1;
                }

                if (tmp == 1)
                {
                    if (cdist[i] < distances[i][j])
                    {
                        cdist[i] = distances[i][j];
                    }
                }
            }

            if (logger.isDebugEnabled())
            {
                logger.debug("conjugated[" + i_1 + "]=" + tmp + " cdist[" +
                    i_1 + "]=" + cdist[i]);
            }
        }

        // save result
        return cdist;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
