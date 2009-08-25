///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IntrinsicState.java,v $
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
package joelib2.feature.types;

import joelib2.data.BasicElementHolder;
import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractDynamicAtomProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureHelper;

import joelib2.feature.result.DynamicArrayResult;

import joelib2.feature.types.atomlabel.AtomFreeElectronsCount;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;

import java.util.List;

import org.apache.log4j.Category;


/**
 *  Atom valences.
 *
 * @.author     wegnerj
 */
public class IntrinsicState extends AbstractDynamicAtomProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.12 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(IntrinsicState.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{BasicElementHolder.class, AtomFreeElectronsCount.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public IntrinsicState()
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
        return IntrinsicState.class.getName();
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

    public Object getAtomPropertiesArray(Molecule mol)
    {
        Atom atom;
        AtomIterator ait = mol.atomIterator();
        double[] istate = (double[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.DOUBLE, mol.getAtomsSize());
        int i = 0;
        double period;
        double valEl;
        double sigEl;
        BasicElementHolder elements = BasicElementHolder.instance();
        int atomID;
        double t;
        List bonds;
        int freeEl;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            atomID = atom.getAtomicNumber();

            period = elements.getPeriod(atomID);
            bonds = atom.getBonds();
            valEl = 0.0;
            sigEl = (double) bonds.size();

            if (sigEl == 0)
            {
                logger.warn(mol.getTitle() +
                    ": No (sigma) bonds available for atom " + atom.getIndex());
            }

            for (int j = 0; j < bonds.size(); j++)
            {
                valEl += (double) ((Bond) bonds.get(j)).getBondOrder();
            }

            freeEl = AtomFreeElectronsCount.getIntValue(atom);
            valEl += (double) freeEl;

            t = 2 / period;
            istate[i] = ((t * t * valEl) + 1) / sigEl;

            if (logger.isDebugEnabled())
            {
                logger.debug(mol.getTitle() + ": " + "atom " + atom.getIndex() +
                    " period " + period + " free electrons=" + freeEl +
                    " valence electrons=" + valEl + " sigma electrons=" +
                    sigEl + " istate [" + i + "]=" + istate[i]);
            }

            i++;
        }

        return istate;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
