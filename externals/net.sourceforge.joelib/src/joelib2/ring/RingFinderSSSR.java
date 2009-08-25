///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RingFinderSSSR.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2006/02/22 02:18:22 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.ring;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.BasicFeatureDescription;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.ResultFactory;

import joelib2.feature.types.atomlabel.AtomInRing;
import joelib2.feature.types.bondlabel.BondInRing;
import joelib2.feature.types.bondlabel.BondIsClosure;

import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.fragmentation.ContiguousFragments;

import joelib2.util.BasicBitVector;
import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import joelib2.util.iterator.BasicRingIterator;
import joelib2.util.iterator.BondIterator;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom tree.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2006/02/22 02:18:22 $
 */
public class RingFinderSSSR implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2006/02/22 02:18:22 $";
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AtomInRing.class, BondInRing.class, BondIsClosure.class,
            ContiguousFragments.class
        };
    private static Category logger = Category.getInstance(RingFinderSSSR.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;

    //~ Constructors ///////////////////////////////////////////////////////////

    public RingFinderSSSR()
    {
        String representation = this.getClass().getName();
        descInfo = new BasicFeatureInfo(getName(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, representation,
                "docs/algo/BreadthFirstSearch", null, Rings.class.getName());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return RingFinderSSSR.class.getName();
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

    /* (non-Javadoc)
     * @see joelib2.util.PropertyAcceptor#acceptedProperties()
     */
    public BasicProperty[] acceptedProperties()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#calculate(joelib2.molecule.Molecule)
     */
    public FeatureResult calculate(Molecule mol) throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, null);
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#calculate(joelib2.molecule.Molecule, joelib2.feature.DescResult)
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult)
        throws FeatureException
    {
        return calculate(mol, descResult, null);
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#calculate(joelib2.molecule.Molecule, java.util.Map)
     */
    public FeatureResult calculate(Molecule mol, Map initData)
        throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, initData);
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#calculate(joelib2.molecule.Molecule, joelib2.feature.DescResult, java.util.Map)
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult,
        Map initData) throws FeatureException
    {
        Rings result = null;

        if (mol.isEmpty())
        {
            logger.error("Empty molecule '" + mol.getTitle() + "'.");
        }
        else
        {
            boolean isInitialized = true;

            try
            {
                FeatureHelper.instance().featureFrom(mol, AtomInRing.getName());
                FeatureHelper.instance().featureFrom(mol, BondInRing.getName());
            }
            catch (FeatureException ex)
            {
                logger.error(ex.getMessage());
                logger.error(
                    "Can not calculate atom-in-ring and bond-in-ring informations for calculating " +
                    getName() + ".");
                isInitialized = false;
            }

            if (isInitialized)
            {
                if (!mol.hasData(BondInRing.getName()) ||
                        !mol.hasData(AtomInRing.getName()))
                {
                    throw new FeatureException(
                        "Missing atom-in-ring and bond-in-ring informations.");
                }

                // check if the result type is correct
                if (!(descResult instanceof Rings))
                {
                    logger.error(descInfo.getName() +
                        " result should be of type " + Rings.class.getName() +
                        " but it's of type " +
                        descResult.getClass().toString());
                    //return null;
                }
                else
                {
                    result = (Rings) descResult;

                    if (!mol.hasData(RingFinderSSSR.getName()))
                    {
                        findSSSR(mol, result);
                    }
                }
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#clear()
     */
    public void clear()
    {
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#getDescInfo()
     */
    public BasicFeatureInfo getDescInfo()
    {
        return descInfo;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#getDescription()
     */
    public FeatureDescription getDescription()
    {
        return new BasicFeatureDescription(descInfo.getDescriptionFile());
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#initialize(java.util.Map)
     */
    public boolean initialize(Map initData)
    {
        if (!PropertyHelper.checkProperties(this, initData))
        {
            logger.error(
                "Empty property definition or missing property entry.");

            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.Descriptor#testDescriptor()
     */
    public boolean testDescriptor()
    {
        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    protected static int frerejaque(Molecule mol)
    {
        int frerejaqueNumber = 0;

        //find all continuous graphs in the mol area
        List<int[]> fragments = new Vector<int[]>();
        ContiguousFragments.contiguousFragments(mol, fragments);

        if (fragments.size() != 0)
        {
            if (fragments.size() == 1)
            {
                frerejaqueNumber = (mol.getBondsSize() - mol.getAtomsSize() +
                        1);
            }
            else
            {
                //count up the atoms and bonds belonging to each graph
                Bond bond = null;
                int numatoms;
                int numbonds;
                BasicBitVector frag = new BasicBitVector();
                int[] itmp;

                for (int fragmentIdx = 0; fragmentIdx < fragments.size();
                        fragmentIdx++)
                {
                    itmp = (int[]) fragments.get(fragmentIdx);
                    frag.clear();
                    frag.fromIntArray(itmp);
                    numatoms = itmp.length;
                    numbonds = 0;

                    BondIterator bit = mol.bondIterator();

                    while (bit.hasNext())
                    {
                        bond = bit.nextBond();

                        if (frag.bitIsOn(bond.getBeginIndex()) &&
                                frag.bitIsOn(bond.getEndIndex()))
                        {
                            numbonds++;
                        }
                    }

                    frerejaqueNumber += (numbonds - numatoms + 1);
                }
            }
        }

        return (frerejaqueNumber);
    }

    /**
     * Finds the Smallest Set of Smallest Rings (SSSR).
     *
     * @see #getSSSR()
     * @see #getSSSRIterator(Vector)
     * @todo Find correct reference: Frerejacque, Bull. Soc. Chim. Fr., 5, 1008 (1939)
     * @.cite fig96
     */
    private static synchronized void findSSSR(Molecule mol, Rings rings)
    {
        Ring ring;

        //get frerejaque taking int account multiple possible spanning graphs
        int frjNumber = frerejaque(mol);

        if (logger.isDebugEnabled())
        {
            logger.debug("Frerejaque number of multiple spanning graphs is " +
                frjNumber);
        }

        if (frjNumber != 0)
        {
            List<Ring> ringsList = new Vector<Ring>();
            List<Bond> cbonds = new Vector<Bond>();
            Bond bond;

            //restrict search for rings around closure bonds
            BondIterator bit = mol.bondIterator();

            if (logger.isDebugEnabled())
            {
                logger.debug("Check closure bonds");
            }

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                if (BondIsClosure.isClosure(bond))
                {
                    cbonds.add(bond);
                }
            }

            if (cbonds.size() != 0)
            {
                RingReducer reducer = new RingReducer();

                //search for all rings about closures
                for (int i = 0; i < cbonds.size(); i++)
                {
                    reducer.addRingFromClosure(mol, (Bond) cbonds.get(i), 0);
                }

                //sort ring sizes from smallest to largest
                reducer.sortRings();

                //full ring set - reduce to SSSR set
                reducer.removeRedundant(frjNumber);

                //store the SSSR set
                BasicRingIterator rit = reducer.getRingIterator();
                Ring rtmp;

                while (rit.hasNext())
                {
                    rtmp = rit.nextRing();
                    ring = new BasicRing(rtmp.getAtomIndices(), mol);
                    ringsList.add(ring);
                    //System.out.println("ADDING ring: "+ring);
                }
            }

            rings.setRings(ringsList);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
