///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ZagrebIndex2.java,v $
//  Purpose:  Calculates the Zagreb Group Index 2.
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
import joelib2.feature.FeatureHelper;

import joelib2.feature.types.atomlabel.AtomIsHydrogen;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.BondIterator;

import org.apache.log4j.Category;


/**
 *  Calculates the Zagreb Group Index 2.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:31 $
 */
public class ZagrebIndex2 extends AbstractDouble
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(ZagrebIndex2.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomIsHydrogen.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the ZagrebIndex2 object
     */
    public ZagrebIndex2()
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
        return ZagrebIndex2.class.getName();
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

    public double getDoubleValue(Molecule mol)
    {
        double counter = 0;
        double atomDegree1;
        double atomDegree2;
        Atom node1;
        Atom node2;
        Bond bond;
        BondIterator bit = mol.bondIterator();

        while (bit.hasNext())
        {
            bond = bit.nextBond();
            node1 = bond.getBegin();
            node2 = bond.getEnd();

            // Graph must be H Atom depleted
            if ((!AtomIsHydrogen.isHydrogen(node1)) &&
                    (!AtomIsHydrogen.isHydrogen(node2)))
            {
                atomDegree1 = node1.getBonds().size();
                atomDegree2 = node2.getBonds().size();
                counter += (atomDegree1 * atomDegree2);
            }
        }

        return counter;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
