///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomInDonAcc.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
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
package joelib2.feature.types.atomlabel;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractDynamicAtomProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;

import joelib2.feature.result.DynamicArrayResult;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomPropertyHelper;

import joelib2.smarts.ProgrammableAtomTyper;

import org.apache.log4j.Category;


/**
 * Is this atom an acceptor or donor (acceptor/donor field) for a carbonyl oxygen or amino hydrogen probe.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cite bk02
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/17 16:48:31 $
 * @see joelib2.feature.types.AtomInAcceptor
 * @see joelib2.feature.types.AtomInDonor
 * @see joelib2.feature.types.HBD1
 * @see joelib2.feature.types.HBD2
 * @see joelib2.feature.types.HBA1
 * @see joelib2.feature.types.HBD2
 * @see joelib2.process.filter.RuleOf5Filter
 */
public class AtomInDonAcc extends AbstractDynamicAtomProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.12 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(AtomInDonAcc.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{ProgrammableAtomTyper.class};

    private static ProgrammableAtomTyper patty = new ProgrammableAtomTyper();

    /**
     * Assigned identifier for conjugated atoms.
     */
    private static String assignment = "da";

    static
    {
        // all amino atoms
        patty.addRule("[NH2-C]", assignment);

        // all hydroxy atoms
        patty.addRule("[$([OH]-C)]", assignment);

        // all hydroxy atoms
        patty.addRule("[$([OH]-c)]", assignment);
    }

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public AtomInDonAcc()
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
        return AtomInDonAcc.class.getName();
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
     *  Returns <tt>true</tt> if this is a chiral atom.
     *
     * @return    <tt>true</tt> if this is a chiral atom
     */
    public static boolean isValue(Atom atom)
    {
        boolean isTrue = false;

        try
        {
            isTrue = AtomPropertyHelper.getBooleanAtomProperty(atom, getName());
        }
        catch (FeatureException e1)
        {
            logger.error(e1.getMessage());
        }

        return isTrue;
    }

    public Object getAtomPropertiesArray(Molecule mol)
    {
        int size = mol.getAtomsSize();
        boolean[] donorAcceptor = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, size);

        int[] assignment = patty.assignTypes(mol);

        for (int index = 0; index < assignment.length; index++)
        {
            if (assignment[index] != -1)
            {
                donorAcceptor[index] = true;
            }
        }

        return donorAcceptor;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
