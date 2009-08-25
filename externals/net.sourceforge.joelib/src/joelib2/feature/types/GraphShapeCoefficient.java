///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GraphShapeCoefficient.java,v $
//  Purpose:  Calculates the graph shape coefficient.
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

import joelib2.feature.result.IntResult;

import joelib2.molecule.Molecule;

import org.apache.log4j.Category;


/**
 * Calculates the graph shape coefficient.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:31 $
 */
public class GraphShapeCoefficient extends AbstractDouble
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            GraphShapeCoefficient.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{TopologicalDiameter.class, TopologicalRadius.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    public GraphShapeCoefficient()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.DoubleResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return GraphShapeCoefficient.class.getName();
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
        // get topological diameter or calculate if not already available
        FeatureResult tmpResult = null;
        String diameterKey = TopologicalDiameter.getName();

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol, diameterKey);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());
            logger.error("Can not calculate " + diameterKey + " for " +
                getName() + ".");

            return 0;
        }

        if (!(tmpResult instanceof IntResult))
        {
            logger.error("Needed descriptor '" + diameterKey +
                "' should be of type " + IntResult.class.getName() + ". " +
                getName() + " can not be calculated.");

            return 0;
        }

        IntResult diameterResult = (IntResult) tmpResult;
        double diameter = diameterResult.getDoubleNV();

        // get topological radius or calculate if not already available
        //DescResult tmpResult=null;
        String radiusKey = TopologicalRadius.getName();

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol, radiusKey);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());
            logger.error("Can not calculate " + radiusKey + " for " +
                getName() + ".");

            return 0;
        }

        if (!(tmpResult instanceof IntResult))
        {
            logger.error("Needed descriptor '" + radiusKey +
                "' should be of type " + IntResult.class.getName() + ". " +
                getName() + " can not be calculated.");

            return 0;
        }

        IntResult radiusResult = (IntResult) tmpResult;
        double radius = radiusResult.getDoubleNV();

        double shape = (diameter - radius) / radius;

        return shape;
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
