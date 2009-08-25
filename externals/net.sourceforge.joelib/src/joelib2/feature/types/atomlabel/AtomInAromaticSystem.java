///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomInAromaticSystem.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.13 $
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
package joelib2.feature.types.atomlabel;

import joelib2.data.BasicAromaticityTyper;
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
import joelib2.feature.result.BondDynamicResult;

import joelib2.feature.types.bondlabel.BondInAromaticSystem;

import joelib2.molecule.Atom;
import joelib2.molecule.BondHelper;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicProperty;

import java.util.Map;
import java.util.zip.DataFormatException;

import org.apache.log4j.Category;


/**
 * Is this atom part of an aromatic system.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.13 $, $Date: 2005/03/03 07:13:36 $
 */
public class AtomInAromaticSystem implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.13 $";
    private static final String RELEASE_DATE = "$Date: 2005/03/03 07:13:36 $";
    private static Category logger = Category.getInstance(
            AtomInAromaticSystem.class.getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{BasicAromaticityTyper.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    public BasicFeatureInfo descInfo;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public AtomInAromaticSystem()
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

    public static String getName()
    {
        return AtomInAromaticSystem.class.getName();
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
    public static boolean isValue(Atom atom)
    {
        boolean isAromatic = false;
        Molecule mol = atom.getParent();

        if (atom.getParent().getModificationCounter() == 0)
        {
            if (!mol.hasData(AtomInAromaticSystem.getName()) /* ||
                                                                 !mol.hasData(BondInAromaticSystem.getName())*/)
            {
                //System.out.println(" aromAtoms:"+mol.hasData(AtomInAromaticSystem.getName())+" aromBonds:"+mol.hasData(BondInAromaticSystem.getName()));
                //System.out.println("calculate aromaticity");
                assignAromaticity(mol);
            }

            //System.out.println(""+getName()+" "+mol.getData(getName())+" "+mol.getData(getName()).getClass().getName());
            PairData pairData = mol.getData(getName());

            if (pairData != null)
            {
                AtomProperties aIsArom = null;

                if (pairData instanceof AtomProperties)
                {
                    aIsArom = (AtomProperties) mol.getData(getName());
                }
                else if (pairData.getKeyValue() instanceof AtomProperties)
                {
                    aIsArom = (AtomProperties) pairData.getKeyValue();
                }

                try
                {
                    if (aIsArom.getIntValue(atom.getIndex()) != 0)
                    {
                        isAromatic = true;
                    }
                }
                catch (DataFormatException e)
                {
                    logger.error(e.getMessage());
                }
            }
            else
            {
                logger.error("No aromatic informations available.");
            }
        }
        else
        {
            throw new RuntimeException(
                "Could not access atom property. Modification counter is not zero.");
        }

        return isAromatic || atom.hasBondOfOrder(BondHelper.AROMATIC_BO);
    }

    /**
     *  Returns <tt>true</tt> if this is a ring atom.
     *
     * @return    <tt>true</tt> if this is a ring atom
     */
    public static void setValue(Atom atom, boolean isAromatic)
    {
        if (atom != null)
        {
            Molecule mol = atom.getParent();

            if (mol.getModificationCounter() == 0)
            {
                if (!mol.hasData(AtomInAromaticSystem.getName()))
                {
                    assignAromaticity(mol);
                }

                AtomProperties aIsArom = (AtomProperties) mol.getData(
                        AtomInAromaticSystem.getName());

                if (aIsArom != null)
                {
                    if (isAromatic)
                    {
                        aIsArom.setIntValue(atom.getIndex(), 1);
                    }
                    else
                    {
                        aIsArom.setIntValue(atom.getIndex(), 0);
                    }
                }
                else
                {
                    logger.error(
                        "No automatic aromatic informations available.");
                }
            }
            else
            {
                throw new RuntimeException(
                    "Could not access atom property. Modification counter is not zero.");
            }
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

                if (!mol.hasData(getName()))
                {
                    BondDynamicResult aromBonds = new BondDynamicResult();
                    BasicAromaticityTyper.instance().assignAromaticFlags(mol,
                        result, aromBonds);
                    aromBonds.addCMLProperty(IdentifierExpertSystem.instance()
                        .getKernelID());
                    aromBonds.setDataDescription(
                        FeatureHelper.VERSION_IDENTIFIER + " " +
                        String.valueOf(
                            IdentifierExpertSystem.getDependencyTreeHash(
                                BondInAromaticSystem.getName())));
                    mol.addData(aromBonds);
                }

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

    private static void assignAromaticity(Molecule mol)
    {
        AtomDynamicResult aromAtoms = new AtomDynamicResult();
        BondDynamicResult aromBonds = new BondDynamicResult();
        BasicAromaticityTyper.instance().assignAromaticFlags(mol, aromAtoms,
            aromBonds);

        aromAtoms.addCMLProperty(IdentifierExpertSystem.instance()
            .getKernelID());
        aromAtoms.setDataDescription(FeatureHelper.VERSION_IDENTIFIER + " " +
            String.valueOf(
                IdentifierExpertSystem.getDependencyTreeHash(
                    AtomInAromaticSystem.getName())));
        aromBonds.addCMLProperty(IdentifierExpertSystem.instance()
            .getKernelID());
        aromBonds.setDataDescription(FeatureHelper.VERSION_IDENTIFIER + " " +
            String.valueOf(
                IdentifierExpertSystem.getDependencyTreeHash(
                    BondInAromaticSystem.getName())));

        mol.addData(aromAtoms);
        mol.addData(aromBonds);
        //System.out.println("after calculation aromAtoms:"+mol.hasData(AtomInAromaticSystem.getName())+" aromBonds:"+mol.hasData(BondInAromaticSystem.getName()));

        //System.out.println("Aromatic atoms (in "+getName()+"): "+aromAtoms);
        //System.out.println("Aromatic bonds (in "+getName()+"): "+aromBonds);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
