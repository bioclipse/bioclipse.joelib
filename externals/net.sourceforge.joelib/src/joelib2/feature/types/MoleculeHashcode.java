///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeHashcode.java,v $
//  Purpose:  Calculates the topological radius.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
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

import joelib2.algo.morgan.Morgan;
import joelib2.algo.morgan.types.BasicTieResolver;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractInt;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureHelper;

import joelib2.molecule.Molecule;

import joelib2.util.database.AbstractDatabase;

import org.apache.log4j.Category;


/**
 * Calculates the topological radius.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:31 $
 */
public class MoleculeHashcode extends AbstractInt
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(MoleculeHashcode.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{AbstractDatabase.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    public MoleculeHashcode()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.IntResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return MoleculeHashcode.class.getName();
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
    public int getIntValue(Molecule mol)
    {
        if (mol.isEmpty())
        {
            logger.warn("Empty molecule '" + mol.getTitle() + "'. " +
                getName() + " was set to 0.");

            return 0;
        }

        int hashCode;

        if (mol.isMoleculeHashing())
        {
            hashCode = mol.hashCode();
        }
        else
        {
            // ensure unique renumbering
            Morgan morgan = new Morgan(new BasicTieResolver());
            Molecule tMol = (Molecule) mol.clone(false);
            morgan.calculate(tMol);

            Molecule rMol = morgan.renumber(tMol);

            // hashcode without cis/trans and chirality informations
            hashCode = AbstractDatabase.getHashcode(mol);

            // hashcode WITH cis/trans and chirality informations
            hashCode = (31 * hashCode) +
                AbstractDatabase.getSMILESHashcode(mol);
        }

        return hashCode;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
