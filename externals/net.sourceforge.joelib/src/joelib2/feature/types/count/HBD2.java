///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: HBD2.java,v $
//  Purpose:  Number of Hydrogen Bond Donors (HBD).
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:32 $
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
package joelib2.feature.types.count;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractSMARTSCounter;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureHelper;

import joelib2.smarts.ProgrammableAtomTyper;

import org.apache.log4j.Category;


/**
 * Number of Hydrogen Bond Donors (HBD).
 * This number is often used for drug-like and lead-like filters.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cite lldf01
 * @.cite odtl01
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:32 $
 * @see joelib2.feature.types.HBD1
 * @see joelib2.feature.types.HBD2
 * @see joelib2.feature.types.HBA1
 * @see joelib2.process.filter.RuleOf5Filter
 * @see joelib2.feature.types.AtomInDonor
 * @see joelib2.feature.types.AtomInAcceptor
 * @see joelib2.feature.types.AtomInDonAcc
 */
public class HBD2 extends AbstractSMARTSCounter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:32 $";
    private static Category logger = Category.getInstance(HBD2.class.getName());
    public final static String DEFAULT =
        "[$([O;H1,-&!$(*-N=O)]),$([S;H1&X2,-&X1]),$([#7;H;!$(*(S(=O)=O)C(F)(F)F);!$(n1nnnc1);!$(n1nncn1)]),$([#7;-])]";
    private static final Class[] DEPENDENCIES =
        new Class[]{ProgrammableAtomTyper.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    public HBD2()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName() +
                "with SMARTS pattern: " + DEFAULT);
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES,
                "joelib2.feature.StringInit",
                "joelib2.feature.result.IntResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return HBD2.class.getName();
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

    public String getDefaultSMARTS()
    {
        return DEFAULT;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
