///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AbstractAtomsCounter.java,v $
//  Purpose:  Zagreb Group Index 1 - Calculator.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.feature;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.result.IntResult;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import joelib2.util.iterator.AtomIterator;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Abstract descriptor class for counting the number of atoms.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.8 $, $Date: 2005/02/17 16:48:29 $
 */
public abstract class AbstractAtomsCounter implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // /////////////////////////////////////////////
    private static Category logger = Category.getInstance(
            AbstractAtomsCounter.class.getName());
    public final static String ATOM_NUMBERS = "ATOM_NUMBERS";
    private final static BasicProperty[] PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(ATOM_NUMBERS, "int[]", "Atom numbers to count.",
                true),
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    public BasicFeatureInfo descInfo;
    private int[] atoms2count;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the default atoms to calculate.
     *
     * @return the default atoms to calculate
     */
    public abstract int[] getDefaultAtoms();

    /**
     * Return the accepted properties.
     *
     * @return the accepted properties
     */
    public BasicProperty[] acceptedProperties()
    {
        return PROPERTIES;
    }

    /**
     * Calculate descriptor for this molecule.
     *
     * @param mol
     *            molecule for which this descriptor should be calculated
     * @return the descriptor calculation result for this molecule
     * @exception FeatureException
     *                descriptor calculation exception
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
     * @param mol
     *            molecule for which this descriptor should be calculated
     * @param initData
     *            initialization properties
     * @return the descriptor calculation result for this molecule
     * @exception FeatureException
     *                descriptor calculation exception
     *
     * @see #ATOM_NUMBERS
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
     * It should be faster, if we can can use an already initialized result
     * class, because this must not be get by Java reflection. Ensure that you
     * will clone this result class before you store these results in molecules,
     * or the next molecule will overwrite this result.
     *
     * @param mol
     *            molecule for which this descriptor should be calculated
     * @param descResult
     *            the descriptor result class in which the result should be
     *            stored
     * @return the descriptor calculation result for this molecule
     * @exception FeatureException
     *                descriptor calculation exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult)
        throws FeatureException
    {
        return calculate(mol, descResult, null);
    }

    /**
     * Calculate descriptor for this molecule.
     *
     * It should be faster, if we can can use an already initialized result
     * class, because this must not be get by Java reflection. Ensure that you
     * will clone this result class before you store these results in molecules,
     * or the next molecule will overwrite this result.
     *
     * @param mol
     *            molecule for which this descriptor should be calculated
     * @param descResult
     *            the descriptor result class in which the result should be
     *            stored
     * @param initData
     *            initialization properties
     * @return the descriptor calculation result for this molecule
     * @exception FeatureException
     *                descriptor calculation exception
     *
     * @see #ATOM_NUMBERS
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult,
        Map properties) throws FeatureException
    {
        // check if the result type is ok
        if (!(descResult instanceof IntResult))
        {
            logger.error(descInfo.getName() + " result should be of type " +
                IntResult.class.getName() + " but it's of type " +
                descResult.getClass().toString());
        }

        IntResult result = null;

        // check if the properties are ok
        if (initialize(properties))
        {
            // find all atoms of the given list in the molecule
            int counter = 0;
            Atom atom;
            AtomIterator ait = mol.atomIterator();
            int atomicNum;

            while (ait.hasNext())
            {
                atom = ait.nextAtom();
                atomicNum = atom.getAtomicNumber();

                for (int arrIdx = 0; arrIdx < atoms2count.length; arrIdx++)
                {
                    if (atomicNum == atoms2count[arrIdx])
                    {
                        counter++;

                        break;
                    }
                }
            }

            result = (IntResult) descResult;
            result.setInt(counter);
            result.addCMLProperty(IdentifierExpertSystem.instance()
                .getKernelID());
        }

        return result;
    }

    /**
     * Clear descriptor calculation method for a new molecule.
     */
    public void clear()
    {
        atoms2count = getDefaultAtoms();

        if (atoms2count == null)
        {
            logger.error("No atomic numbers defined in " +
                AbstractAtomsCounter.class.getName());
        }
    }

    /**
     * @return Returns the atms2count.
     */
    public int[] getAtoms2count()
    {
        return atoms2count;
    }

    /**
     * Gets the descriptor informations for this descriptor.
     *
     * @return the descriptor information
     */
    public BasicFeatureInfo getDescInfo()
    {
        return descInfo;
    }

    /**
     * Gets the descriptor description.
     *
     * @return the descriptor description
     */
    public FeatureDescription getDescription()
    {
        return new BasicFeatureDescription(descInfo.getDescriptionFile());
    }

    /**
     * Initialize descriptor calculation method for all following molecules.
     *
     * @param initData
     *            initialization properties
     * @return <tt>true</tt> if the initialization was successfull
     *
     * @see #ATOM_NUMBERS
     */
    public boolean initialize(Map properties)
    {
        boolean allFine = false;

        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition or missing property entry.");
        }
        else
        {
            int[] atoms = (int[]) PropertyHelper.getProperty(this, ATOM_NUMBERS,
                    properties);

            if (atoms == null)
            {
                atoms2count = getDefaultAtoms();

                if (atoms2count == null)
                {
                    logger.error("No atomic numbers defined in " +
                        AbstractAtomsCounter.class.getName());
                }
            }
            else
            {
                atoms2count = atoms;
            }

            allFine = true;
        }

        return allFine;
    }

    /**
     * @param atms2count The atms2count to set.
     */
    public void setAtoms2count(int[] atms2count)
    {
        this.atoms2count = atms2count;
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
