///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GeometricalDiameter.java,v $
//  Purpose:  Calculates the geometrical diameter.
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

import joelib2.feature.AbstractDouble;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.result.DoubleMatrixResult;

import joelib2.molecule.Molecule;

import org.apache.log4j.Category;


/**
 * Calculates the geometrical diameter.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:31 $
 */
public class GeometricalDiameter extends AbstractDouble
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            GeometricalDiameter.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{GeomDistanceMatrix.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    public GeometricalDiameter()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_GEOMETRICAL, null,
                "joelib2.feature.result.DoubleResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return GeometricalDiameter.class.getName();
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

    /**
     * Gets the defaultAtoms attribute of the NumberOfC object
     *
     * @return   The defaultAtoms value
     */
    public double getDoubleValue(Molecule mol)
    {
        if (mol.isEmpty())
        {
            logger.warn("Empty molecule '" + mol.getTitle() + "'. " +
                GeometricalDiameter.getName() + " was set to 0.");

            return 0.0;
        }

        // get distance matrix or calculate if not already available
        FeatureResult tmpResult = null;
        String geomDistanceMatrixKey = GeomDistanceMatrix.getName();

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol,
                    geomDistanceMatrixKey);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());
            logger.error("Can not calculate distance matrix for " + getName() +
                ".");

            return 0;
        }

        if (!(tmpResult instanceof DoubleMatrixResult))
        {
            logger.error("Needed descriptor '" + geomDistanceMatrixKey +
                "' should be of type " + DoubleMatrixResult.class.getName() +
                ". " + getName() + " can not be calculated.");

            return 0;
        }

        DoubleMatrixResult distResult = (DoubleMatrixResult) tmpResult;
        double[][] distances = distResult.value;

        double geometricalDiameter = -Double.MAX_VALUE;

        for (int i = 0; i < distances.length; i++)
        {
            for (int ii = 0; ii < i; ii++)
            {
                if (geometricalDiameter < distances[i][ii])
                {
                    geometricalDiameter = distances[i][ii];
                }
            }
        }

        return geometricalDiameter;
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
