///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomHybridisation.java,v $
//  Purpose:  Atom mass.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.16 $
//            $Date: 2005/02/17 16:48:31 $
//            $Author: wegner $
//
//  Copyright (c) Dept. Computer Architecture, University of Tuebingen,
//                Germany, 2001-2005
//, 2003-2005
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
package joelib2.feature.types.atomlabel;

import joelib2.data.BasicHybridisationTyper;
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

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomPropertyHelper;

import joelib2.util.BasicProperty;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Atom type (JOELib internal).
 *
 * This atom property stores the JOELib internal atom type, which can be used via the
 * look-up table in {@link joelib2.data.BasicAtomTypeConversionHolder} to export molecules to other
 * formats, like Synyl MOL2, MM2, Tinker, etc.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.16 $, $Date: 2005/02/17 16:48:31 $
 */
public class AtomHybridisation implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.16 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            AtomHybridisation.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{BasicHybridisationTyper.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    public BasicFeatureInfo descInfo;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomType object
     */
    public AtomHybridisation()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                joelib2.feature.result.AtomDynamicResult.class.getName());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static int getIntValue(Atom atom)
    {
        return getIntValue(atom, true);
    }

    public static int getIntValue(Atom atom, boolean ignoreModCounter)
    {
        int hyb = 0;

        try
        {
            hyb = AtomPropertyHelper.getIntAtomProperty(atom, getName(),
                    ignoreModCounter);
        }
        catch (FeatureException e1)
        {
            logger.error(e1.getMessage());
        }

        return hyb;
    }

    public static String getName()
    {
        return AtomHybridisation.class.getName();
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
     *  Returns <tt>true</tt> if this is a ring atom.
     *
     * @return    <tt>true</tt> if this is a ring atom
     */
    public static void setHybridisation(Atom atom, int hyb)
    {
        try
        {
            AtomPropertyHelper.setIntAtomProperty(atom, getName(), hyb);
        }
        catch (FeatureException e)
        {
            logger.error(e.getMessage());
        }
    }

    public BasicProperty[] acceptedProperties()
    {
        return null;
    }

    /**
     * Calculate descriptor for this molecule.
     *
     * @param mol                      molecule for which this descriptor should be calculated
     * @return                         the descriptor calculation result for this molecule
     * @exception FeatureException  descriptor calculation exception
     */
    public FeatureResult calculate(Molecule mol) throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, null);
    }

    /**
     * Calculate descriptor for this molecule.
     *
     * @param mol                      molecule for which this descriptor should be calculated
     * @param initData                 initialization properties
     * @return                         the descriptor calculation result for this molecule
     * @exception FeatureException  descriptor calculation exception
     */
    public FeatureResult calculate(Molecule mol, Map properties)
        throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, properties);
    }

    /**
     * Calculate descriptor for this molecule.
     *
     * It should be faster, if we can can use an already initialized result class,
     * because this must not be get by Java reflection. Ensure that you will clone
     * this result class before you store these results in molecules, or the next molecule will
     * overwrite this result.
     *
     * @param mol                      molecule for which this descriptor should be calculated
     * @param descResult               the descriptor result class in which the result should be stored
     * @return                         the descriptor calculation result for this molecule
     * @exception FeatureException  descriptor calculation exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult)
        throws FeatureException
    {
        return calculate(mol, descResult, null);
    }

    /**
     * Calculate descriptor for this molecule.
     *
     * It should be faster, if we can can use an already initialized result class,
     * because this must not be get by Java reflection. Ensure that you will clone
     * this result class before you store these results in molecules, or the next molecule will
     * overwrite this result.
     *
     * @param mol                      molecule for which this descriptor should be calculated
     * @param descResult               the descriptor result class in which the result should be stored
     * @param initData                 initialization properties
     * @return                         the descriptor calculation result for this molecule
     * @exception FeatureException  descriptor calculation exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult,
        Map properties) throws FeatureException
    {
        AtomDynamicResult result = null;

        // check if the result type is correct
        if (!(descResult instanceof AtomDynamicResult))
        {
            logger.error(descInfo.getName() + " result should be of type " +
                AtomDynamicResult.class.getName() + " but it's of type " +
                descResult.getClass().toString());
        }
        else
        {
            // check if the init type is correct
            if (initialize(properties))
            {
                // initialize result type, if not already initialized
                result = (AtomDynamicResult) descResult;

                // get partial charges for all atoms
                Object array = getAtomPropertiesArray(mol);

                if (array == null)
                {
                    return null;
                }

                result.setArray(array);
                result.addCMLProperty(IdentifierExpertSystem.instance()
                    .getKernelID());
                result.setDataDescription(FeatureHelper.VERSION_IDENTIFIER +
                    " " + String.valueOf(this.hashedDependencyTreeVersion()));
            }
        }

        return result;
    }

    /**
     * Clear descriptor calculation method for a new molecule.
     */
    public void clear()
    {
    }

    public Object getAtomPropertiesArray(Molecule mol)
    {
        int[] hyb = (int[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.INT, mol.getAtomsSize());
        BasicHybridisationTyper.instance().getHybridisation(mol, hyb);

        return hyb;
    }

    /**
     * Gets the descriptor informations for this descriptor.
     *
     * @return   the descriptor information
     */
    public BasicFeatureInfo getDescInfo()
    {
        return descInfo;
    }

    /**
     * Gets the descriptor description.
     *
     * @return   the descriptor description
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
     * Initialize descriptor calculation method for all following molecules.
     *
     * @param initData  initialization properties
     * @return <tt>true</tt> if the initialization was successfull
     */
    public boolean initialize(Map properties)
    {
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
