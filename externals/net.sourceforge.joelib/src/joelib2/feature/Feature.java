///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Feature.java,v $
//  Purpose:  Descriptor base class.
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

import joelib2.molecule.Molecule;

import joelib2.util.PropertyAcceptor;

import java.util.Map;


/**
 * Interface for defining descriptors, which can be calculated by using JOELib.
 *
 * For 'knowing' descriptor values which can not be calculated, have a look at the
 * {@link joelib2.feature.ResultFactory}  object.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:29 $
 */
public interface Feature extends PropertyAcceptor
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Calculate descriptor for this molecule.
     *
     * @param mol                      molecule for which this descriptor should be calculated
     * @return                         the descriptor calculation result for this molecule
     * @exception FeatureException  descriptor calculation exception
     */
    FeatureResult calculate(Molecule mol) throws FeatureException;

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
    FeatureResult calculate(Molecule mol, FeatureResult descResult)
        throws FeatureException;

    /**
     * Calculate descriptor for this molecule.
     *
     * @param mol                      molecule for which this descriptor should be calculated
     * @param initData                 initialization properties
     * @return                         the descriptor calculation result for this molecule
     * @exception FeatureException  descriptor calculation exception
     */
    FeatureResult calculate(Molecule mol, Map initData) throws FeatureException;

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
    FeatureResult calculate(Molecule mol, FeatureResult descResult,
        Map initData) throws FeatureException;

    /**
     * Clear descriptor calculation method for a new molecule.
     */
    void clear();

    /**
     * Gets the descriptor informations for this descriptor.
     *
     * @return   the descriptor information
     */
    BasicFeatureInfo getDescInfo();

    /**
     * Gets the descriptor description.
     *
     * @return   the descriptor description
     */
    FeatureDescription getDescription();

    /**
     * Gets the hashed dependency tree version number.
     */
    int hashedDependencyTreeVersion();

    /**
     * Initialize descriptor calculation method for all following molecules.
     *
     * @param initData  initialization properties
     * @return <tt>true</tt> if the initialization was successfull
     */
    boolean initialize(Map initData);

    /**
     * Test the implementation of this descriptor.
     *
     * @return <tt>true</tt> if the implementation is correct
     */
    boolean testDescriptor();
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
