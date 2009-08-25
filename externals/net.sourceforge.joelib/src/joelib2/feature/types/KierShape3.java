///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: KierShape3.java,v $
//  Purpose:  Calculates the Kier Shape for paths with length three.
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
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.NbrAtomIterator;

import org.apache.log4j.Category;


/**
 *  Calculates the Kier Shape for paths with length three.
 *
 * @.author    Jan Bruecker
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:31 $
 */
public class KierShape3 extends AbstractDouble
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(KierShape3.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomIsHydrogen.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape3 object
     */
    public KierShape3()
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
        return KierShape3.class.getName();
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
     * Gets the doubleValue attribute of the KierShape3 object
     *
     * @param mol  Description of the Parameter
     * @return     The doubleValue value
     */
    public double getDoubleValue(Molecule mol)
    {
        double nodes = 0;
        double paths = 0;
        AtomIterator ait = mol.atomIterator();
        NbrAtomIterator nbrait;
        NbrAtomIterator nbrait2;
        NbrAtomIterator nbrait3;
        Atom atom;
        Atom nbrAtom;
        Atom nbrAtom2;
        Atom nbrAtom3;
        double kier;

        while (ait.hasNext())
        {
            //Iterates over all nodes
            atom = ait.nextAtom();

            //"node" is the current node of the Iteration
            if (!AtomIsHydrogen.isHydrogen(atom))
            {
                //Graph should be H-Atom depleted
                nodes++;
                nbrait = atom.nbrAtomIterator();

                while (nbrait.hasNext())
                {
                    //Iterates over all edges of the current "node"
                    nbrAtom = nbrait.nextNbrAtom();

                    if (!AtomIsHydrogen.isHydrogen(nbrAtom))
                    {
                        nbrait2 = nbrAtom.nbrAtomIterator();

                        while (nbrait2.hasNext())
                        {
                            nbrAtom2 = nbrait2.nextNbrAtom();

                            if ((!AtomIsHydrogen.isHydrogen(nbrAtom2)) &&
                                    (nbrAtom2.getIndex() != atom.getIndex()))
                            {
                                nbrait3 = nbrAtom2.nbrAtomIterator();

                                while (nbrait3.hasNext())
                                {
                                    nbrAtom3 = nbrait3.nextNbrAtom();

                                    if ((!AtomIsHydrogen.isHydrogen(nbrAtom3)) &&
                                            (nbrAtom3.getIndex() !=
                                                nbrAtom.getIndex()))
                                    {
                                        paths++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        paths = paths / 2;

        //each path has been counted twice, so divide by two
        //System.out.println("Kier 3 paths: " +paths +"\n Knoten: " +nodes);
        if (paths > 0)
        {
            if ((nodes % 2) == 0)
            {
                kier = (((nodes - 3) * ((nodes - 2) * (nodes - 2))) /
                        (paths * paths));
            }
            else
            {
                kier = (((nodes - 1) * ((nodes - 3) * (nodes - 3))) /
                        (paths * paths));
            }
        }
        else
        {
            return 0.0;
        }

        return kier;
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
