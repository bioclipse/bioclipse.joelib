///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomType.java,v $
//  Purpose:  Atom mass.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.13 $
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
package joelib2.feature.types.atomlabel;

import joelib2.data.BasicAtomTyper;
import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractStringAtomProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;

import joelib2.io.types.cml.ResultCMLProperties;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;
import joelib2.molecule.types.AtomPropertyHelper;

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
 * @.cvsversion    $Revision: 1.13 $, $Date: 2005/02/17 16:48:31 $
 */
public class AtomType extends AbstractStringAtomProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.13 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(AtomType.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{BasicAtomTyper.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomType object
     */
    public AtomType()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.AtomStringResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static String getAtomType(Atom atom)
    {
        String atomType = "";

        try
        {
            atomType = AtomPropertyHelper.getStringAtomProperty(atom,
                    getName());
        }
        catch (FeatureException e1)
        {
            logger.error(e1.getMessage());
        }

        return atomType;
    }

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return AtomType.class.getName();
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
    public static void setAtomType(Atom atom, String type)
    {
        if (atom != null)
        {
            Molecule mol = atom.getParent();

            if (mol != null)
            {
                if (mol.getModificationCounter() == 0)
                {
                    AtomProperties atCache;

                    try
                    {
                        atCache = (AtomProperties) FeatureHelper.instance()
                                                                .featureFrom(
                                atom.getParent(), getName());
                    }
                    catch (FeatureException e1)
                    {
                        throw new RuntimeException(e1.getMessage());
                    }

                    if (atCache != null)
                    {
                        atCache.setStringValue(atom.getIndex(), type);
                    }
                    else
                    {
                        logger.error(
                            "No automatic assigned atom type informations available.");
                    }
                }
                else
                {
                    throw new RuntimeException(
                        "Could not access atom property. Modification counter is not zero.");
                }
            }
        }
    }

    public String[] getStringAtomProperties(Molecule mol,
        ResultCMLProperties cmlProps)
    {
        String[] types = new String[mol.getAtomsSize()];
        BasicAtomTyper.instance().getAtomTypes(mol, types);

        // not required, because we use here the kernel indentifier instead
        //cmlProps.addCMLProperty(new StringString("dictRef","joelib:atomType"));
        return types;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
