///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicAromaticityTyper.java,v $
//  Purpose:  Aromatic typer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/03/03 07:13:36 $
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
package joelib2.data;

import joelib2.feature.result.AtomDynamicResult;
import joelib2.feature.result.BondDynamicResult;
import joelib2.feature.result.DynamicArrayResult;

import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomInAromaticSystem;
import joelib2.feature.types.atomlabel.AtomInRing;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;
import joelib2.feature.types.bondlabel.BondInRing;
import joelib2.feature.types.bondlabel.BondIsClosure;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.ring.Ring;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.NbrAtomIterator;

import joelib2.util.types.BasicIntInt;
import joelib2.util.types.IntInt;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;


/**
 *  Aromatic typer.
 * The definition file can be defined in the
 * <tt>joelib2.data.JOEAromaticTyper.resourceFile</tt> property in the {@link wsi.ra.tool.BasicPropertyHolder}.
 * The {@link wsi.ra.tool.BasicResourceLoader} loads the <tt>joelib2.properties</tt> file for default.
 *
 * <p>
 * Default:<br>
 * joelib2.data.JOEAromaticTyper.resourceFile=<a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joelib/joelib/src/joelib2/data/plain/aromatic.txt?rev=HEAD&content-type=text/vnd.viewcvs-markup">joelib2/data/plain/aromatic.txt</a>
 *
 * @.author     wegnerj
 * @.wikipedia Aromaticity
 * @.wikipedia Friedrich August Kekulé von Stradonitz
 * @.wikipedia Molecule
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/03/03 07:13:36 $
 * @see wsi.ra.tool.BasicPropertyHolder
 * @see wsi.ra.tool.BasicResourceLoader
 */
public class BasicAromaticityTyper extends AbstractDataHolder
    implements IdentifierHardDependencies, AromaticityTyper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(
            BasicAromaticityTyper.class.getName());
    private static BasicAromaticityTyper aromtyper;
    private final static String DEFAULT_RESOURCE =
        "joelib2/data/plain/aromatic.txt";
    private final static boolean DEFAULT_AVOID_INNER_RING_FLAG = true;
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.7 $";
    private static final String RELEASE_DATE = "$Date: 2005/03/03 07:13:36 $";
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            BasicSMARTSPatternMatcher.class, AtomHybridisation.class,
            AtomImplicitValence.class, AtomInRing.class, AtomIsHydrogen.class,
            BondInRing.class, BondIsClosure.class
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean[] isRoot;
    private boolean[] isVisited;

    private List<BasicIntInt> minMaxElectrons;
    private BasicIntInt[] numberOfElectrons;

    /**
     * Potentially aromatic atoms.
     */
    private boolean[] potentiallyAromatic;
    private List<SMARTSPatternMatcher> smarts;
    private boolean useAromaticityModel = true;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Initializes the aromatic typer factory class.
     */
    private BasicAromaticityTyper()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", DEFAULT_RESOURCE);

        minMaxElectrons = new Vector<BasicIntInt>();
        potentiallyAromatic = null;
        isVisited = null;
        isRoot = null;
        numberOfElectrons = null;

        smarts = new Vector<SMARTSPatternMatcher>();

        IdentifierExpertSystem.instance().addHardCodedKernel(this);
        init();
        IdentifierExpertSystem.instance().addSoftCodedKernel(this);

        String isTrue = prop.getProperty(this.getClass().getName() +
                ".useAromaticityModel", "true");

        if (isTrue.equalsIgnoreCase("true"))
        {
            useAromaticityModel = true;
        }
        else
        {
            useAromaticityModel = false;
        }

        if (useAromaticityModel)
        {
            logger.info("Using aromaticity model: " + resourceFile);
        }
        else
        {
            logger.info("Aromaticity model is switched OFF.");
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
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
     * Gets an instance of this aromatic typer factory class.
     *
     * @return    the instance of this aromatic typer factory class
     */
    public static synchronized BasicAromaticityTyper instance()
    {
        if (aromtyper == null)
        {
            aromtyper = new BasicAromaticityTyper();
        }

        return aromtyper;
    }

    /**
     * Assign the aromaticity flag to atoms and bonds.
     *
     * 3 rings will be excluded.
     * Please remember that the aromaticity typer JOEAromaticTyper.assignAromaticFlags(Molecule)
     * assign ONLY aromaticity flags and NOT the internal aromatic bond order Bond.JOE_AROMATIC_BOND_ORDER.
     *
     * @param  mol  the molecule
     */
    public void assignAromaticFlags(Molecule mol, AtomDynamicResult atoms,
        BondDynamicResult bonds)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Starting aromatic flag assignment.");
        }

        if (useAromaticityModel)
        {
            if (!initialized)
            {
                init();
            }

            int size = mol.getAtomsSize() + 1;
            potentiallyAromatic = new boolean[size];
            isRoot = new boolean[size];
            isVisited = new boolean[size];
            numberOfElectrons = new BasicIntInt[size];

            for (int atomIdx = 0; atomIdx < size; atomIdx++)
            {
                numberOfElectrons[atomIdx] = new BasicIntInt();

                //            if(i>0 && i<=mol.getAtomsSize())
                //            potentiallyAromatic[i]=mol.getAtom(i).hasBondOfOrder(BondHelper.AROMATIC_BO);
            }

            markPotentiallyAromatic(mol);

            //        printPotentialAromatic("Marked ");
            //sanity check - exclude all 4 substituted atoms and sp centers
            sanityCheck(mol);

            //        printPotentialAromatic("Sanity ");
            //propagate potentially aromatic atoms
            propagatePotArom(mol);

            //        printPotentialAromatic("Propagate ");
            // select root atom
            selectRootAtoms(mol, DEFAULT_AVOID_INNER_RING_FLAG);

            //remove 3 membered rings from consideration
            excludeSmallRing(mol);
        }

        int bondsSize = mol.getBondsSize();
        boolean[] aromBondsArr = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, bondsSize);
        int atomsSize = mol.getAtomsSize();
        boolean[] aromAtomsArr = (boolean[]) DynamicArrayResult.getNewArray(
                DynamicArrayResult.BOOLEAN, atomsSize);

        if (useAromaticityModel)
        {
            //loop over root atoms and look for 5-6 membered aromatic rings
            checkAromaticity(mol, aromAtomsArr, aromBondsArr);
        }
        else
        {
            // use simply the aromatic bond order assignment
            getBondOrderAromaticity(mol, aromAtomsArr, aromBondsArr);
        }

        if (atoms != null)
        {
            atoms.setKey(AtomInAromaticSystem.getName());
            atoms.setKeyValue(atoms);
            atoms.setArray(aromAtomsArr);
        }

        if (bonds != null)
        {
            bonds.setArray(aromBondsArr);
            bonds.setKey(BondInAromaticSystem.getName());
            bonds.setKeyValue(bonds);
        }

        if (logger.isDebugEnabled())
        {
            debugAssignment(mol, aromAtomsArr, aromBondsArr);
        }
    }

    /**
     * Release date for this expert system (hard coded).
     *
     * @return Release date for this expert system (hard coded).
     */
    public String getReleaseDateInternal()
    {
        return BasicAromaticityTyper.getReleaseDate();
    }

    /**
     * Release version for this expert system (hard coded).
     *
     * @return Release version for this expert system (hard coded).
     */
    public String getReleaseVersionInternal()
    {
        return BasicAromaticityTyper.getReleaseVersion();
    }

    /**
     * Vendor for this expert system (hard coded).
     *
     * @return Vendor for this expert system (hard coded).
     */
    public String getVendorInternal()
    {
        return BasicAromaticityTyper.getVendor();
    }

    /**
    * Parses the atom typer definition line to initialize the aromatic typer.
    *
    * @param  buffer  the atom typer definition line
    */
    protected void parseLine(String buffer)
    {
        SMARTSPatternMatcher smartsPattern;

        if (buffer.trim().equals("") || (buffer.charAt(0) == '#'))
        {
            return;
        }

        List<String> tokenized = new Vector<String>();
        HelperMethods.tokenize(tokenized, buffer);

        if ((tokenized.size() != 0) && (tokenized.size() == 3))
        {
            String tmp = (String) tokenized.get(0);
            smartsPattern = new BasicSMARTSPatternMatcher();

            if (smartsPattern.init(tmp))
            {
                smarts.add(smartsPattern);

                //minimum and maximum number of electrons
                int minElectrons = Integer.parseInt((String) tokenized.get(1));
                int maxElectrons = Integer.parseInt((String) tokenized.get(2));
                minMaxElectrons.add(new BasicIntInt(minElectrons,
                        maxElectrons));
            }
            else
            {
                smartsPattern = null;
            }
        }
    }

    /**
     * @param mol
     */
    private void checkAromaticity(Molecule mol, boolean[] aromAtoms,
        boolean[] aromBonds)
    {
        AtomIterator ait = mol.atomIterator();
        Atom atom;
        ait.reset();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            // check only for root atoms
            if (isRoot[atom.getIndex()])
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Check aromaticity for root atom " +
                        atom.getIndex());
                }

                checkAromaticity(atom, 6, aromAtoms, aromBonds);
            }
        }

        ait.reset();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            // check only for root atoms
            if (isRoot[atom.getIndex()])
            {
                checkAromaticity(atom, 20, aromAtoms, aromBonds);
            }
        }
    }

    /**
     * Check aromaticity starting from the root atom.
     *
     * @param  atom   the root atom
     * @param  depth  the search depth, e.g. 6 or 20 for typical aromatic systems
     *
     * @see @see #selectRootAtoms(Molecule, boolean}
     */
    private void checkAromaticity(Atom atom, int depth, boolean[] aromAtoms,
        boolean[] aromBonds)
    {
        Atom nbr;
        Bond bond;
        BasicIntInt erange = null;
        NbrAtomIterator nait = atom.nbrAtomIterator();

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();
            bond = nait.actualBond();

            if (BondInRing.isInRing(bond) && !aromBonds[bond.getIndex()])
            {
                erange = numberOfElectrons[atom.getIndex()];

                if (traverseCycle(atom, nbr, bond, erange, depth - 1, aromAtoms,
                            aromBonds))
                {
                    aromAtoms[atom.getIndex() - 1] = true;
                    aromBonds[bond.getIndex()] = true;

                    //bond.setBondOrderAromatic();
                }
            }
        }
        //System.out.println("check depth:"+depth+" atom "+atom.getIndex()+" potAr:"+potentiallyAromatic[atom.getIndex()]+" minEl:"+(numberOfElectrons[atom.getIndex()]).intValue1+" maxEl:"+(numberOfElectrons[atom.getIndex()]).intValue2+" aromatic? "+aromAtoms[atom.getIndex()-1]+" erange: "+erange);
    }

    /**
     * @param mol
     */
    private void debugAssignment(Molecule mol, boolean[] aromAtoms,
        boolean[] aromBonds)
    {
        AtomIterator ait = mol.atomIterator();
        BondIterator bit = mol.bondIterator();
        Bond bond;
        Atom atom;
        StringBuffer debugBuffer = new StringBuffer();
        debugBuffer.append("aromatic-atoms:");
        ait.reset();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (aromAtoms[atom.getIndex() - 1])
            {
                debugBuffer.append(" " + atom.getIndex());
            }
        }

        debugBuffer.append("\naromatic-bonds:");
        bit.reset();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (aromBonds[bond.getIndex()])
            {
                debugBuffer.append(" " + bond.getIndex());
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(debugBuffer.toString());
        }
    }

    /**
     * Remove 3 membered rings from consideration.
     *
     * @param  mol  the molecule
     */
    private void excludeSmallRing(Molecule mol)
    {
        Atom atom;
        Atom nbr1;
        Atom nbr2;

        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (isRoot[atom.getIndex()])
            {
                NbrAtomIterator nait1 = atom.nbrAtomIterator();

                while (nait1.hasNext())
                {
                    nbr1 = nait1.nextNbrAtom();

                    if (BondInRing.isInRing(nait1.actualBond()) &&
                            potentiallyAromatic[nbr1.getIndex()])
                    {
                        NbrAtomIterator nait2 = nbr1.nbrAtomIterator();

                        while (nait2.hasNext())
                        {
                            nbr2 = nait2.nextNbrAtom();

                            if ((nbr2 != atom) &&
                                    BondInRing.isInRing(nait2.actualBond()) &&
                                    potentiallyAromatic[nbr2.getIndex()])
                            {
                                if (atom.isConnected(nbr2))
                                {
                                    isRoot[atom.getIndex()] = false;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param mol
     * @param aromAtomsArr
     * @param aromBondsArr
     */
    private void getBondOrderAromaticity(Molecule mol, boolean[] aromAtomsArr,
        boolean[] aromBondsArr)
    {
        for (int bondIdx = 0; bondIdx < mol.getBondsSize(); bondIdx++)
        {
            Bond bond = mol.getBond(bondIdx);

            if (bond.isBondOrderAromatic())
            {
                aromBondsArr[bondIdx] = true;
                aromAtomsArr[bond.getBeginIndex() - 1] = true;
                aromAtomsArr[bond.getEndIndex() - 1] = true;
            }
        }
    }

    /**
     * @param potentiallyAromatic2
     */
    private void markPotentiallyAromatic(Molecule mol)
    {
        int idx;
        int[] itmp;
        int k;

        //mark atoms as potentially aromatic
        for (idx = 0, k = 0; k < smarts.size(); k++, idx++)
        {
            SMARTSPatternMatcher tmpSP = smarts.get(k);

            //System.out.println("markPotentiallyAromatic "+tmpSP.getSMARTS()+" idx "+idx+tmpSP);
            if (tmpSP.match(mol))
            {
                List<int[]> matchList = tmpSP.getMatches();

                //        System.out.print("mlist:");
                for (int m = 0; m < matchList.size(); m++)
                {
                    itmp = matchList.get(m);

                    //                          System.out.println("get ("+idx+")"+(itmp[0]));
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("pot. aromatic " + itmp[0] + " " +
                            tmpSP.getSmarts());
                    }

                    //System.out.println(tmpSP.getSMARTS()+" "+minMaxElectrons.get(idx));
                    potentiallyAromatic[itmp[0]] = true;

                    BasicIntInt iiMinMaxE = minMaxElectrons.get(idx);
                    BasicIntInt iiElectrons = numberOfElectrons[itmp[0]];
                    iiElectrons.intValue1 = iiMinMaxE.intValue1;
                    iiElectrons.intValue2 = iiMinMaxE.intValue2;
                }

                //        System.out.println("");
            }
        }

        if (logger.isDebugEnabled())
        {
            StringBuffer debugBuffer = new StringBuffer();
            debugBuffer.append("potentiallyAromatic:");

            for (int i = 0; i < potentiallyAromatic.length; i++)
            {
                debugBuffer.append(" " + potentiallyAromatic[i]);
            }

            logger.debug(debugBuffer.toString());
        }
    }

    /**
     * @param string
     */
    private void printPotentialAromatic(String string)
    {
        for (int i = 0; i < potentiallyAromatic.length; i++)
        {
            System.out.println(string + " pot. aromatic" + i + "=" +
                potentiallyAromatic[i]);
        }
    }

    /**
         * @param mol
         */
    private void propagatePotArom(Molecule mol)
    {
        AtomIterator ait = mol.atomIterator();
        Atom atom;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (potentiallyAromatic[atom.getIndex()])
            {
                propagatePotArom(atom);
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  atom  Description of the Parameter
     */
    private void propagatePotArom(Atom atom)
    {
        int count = 0;

        // count potentially aromatic neighbour atoms of
        // this atoms which are included in a ring
        Atom nbr = null;
        NbrAtomIterator nait = atom.nbrAtomIterator();

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();

            if (BondInRing.isInRing(nait.actualBond()) &&
                    potentiallyAromatic[nbr.getIndex()])
            {
                count++;
            }
        }

        //System.out.println("atom: "+atom.getIdx()+" counter: "+count);
        if (count < 2)
        {
            potentiallyAromatic[atom.getIndex()] = false;

            if (logger.isDebugEnabled())
            {
                if (potentiallyAromatic[atom.getIndex()] == true)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Remove potentially aromatic atom " +
                            atom.getIndex() +
                            ", less than 2 aromatic neighbours.");
                    }
                }
            }

            if (count == 1)
            {
                nait.reset();

                while (nait.hasNext())
                {
                    nbr = nait.nextNbrAtom();

                    if (BondInRing.isInRing(nait.actualBond()) &&
                            potentiallyAromatic[nbr.getIndex()])
                    {
                        propagatePotArom(nbr);
                    }
                }
            }
        }
    }

    /**
     * @param mol
     * @param potentiallyAromatic2
     */
    private void sanityCheck(Molecule mol)
    {
        Atom atom;
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (AtomImplicitValence.getImplicitValence(atom) > 3)
            {
                if (logger.isDebugEnabled())
                {
                    if (potentiallyAromatic[atom.getIndex()] == true)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Remove potentially aromatic atom " +
                                atom.getIndex() + ", BO>3.");
                        }
                    }
                }

                potentiallyAromatic[atom.getIndex()] = false;

                continue;
            }

            switch (atom.getAtomicNumber())
            {
            //phosphorus and sulfur may be initially typed as sp3
            case 6:
            case 7:
            case 8:

                if (AtomHybridisation.getIntValue(atom) != 2)
                {
                    if (logger.isDebugEnabled())
                    {
                        if (potentiallyAromatic[atom.getIndex()] == true)
                        {
                            if (logger.isDebugEnabled())
                            {
                                logger.debug(
                                    "Remove potentially aromatic atom " +
                                    atom.getIndex() + ", Hyb!=2.");
                            }
                        }
                    }

                    potentiallyAromatic[atom.getIndex()] = false;
                }

                break;
            }
        }
    }

    /**
     * Select the root atoms for traversing atoms in rings.
     *
     * Picking only the begin atom of a closure bond can cause
     * difficulties when the selected atom is an inner atom
     * with three neighbour ring atoms. Why ? Because this atom
     * can get trapped by the other atoms when determining aromaticity,
     * because a simple visited flag is used in the
     * {@link #traverseCycle(Atom, Atom, Bond, IntInt, int)}
     *
     * @param mol the molecule
     * @param avoidInnerRingAtoms inner closure ring atoms with more than 2 neighbours will be avoided
     *
     * @see #traverseCycle(Atom, Atom, Bond, IntInt, int)
     */
    private void selectRootAtoms(Molecule mol, boolean avoidInnerRingAtoms)
    {
        BondIterator bit = mol.bondIterator();
        List sssRings = mol.getSSSR();
        Bond bond;
        int rootAtom;
        NbrAtomIterator nait;
        Atom nbrAtom;
        int ringNbrs;
        int heavyNbrs;
        Ring ring;
        int[] ringIndices;
        int newRoot = -1;
        Vector tmpRootAtoms = new Vector();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (BondIsClosure.isClosure(bond))
            {
                rootAtom = bond.getBeginIndex();
                tmpRootAtoms.add(new Integer(rootAtom));
            }
        }

        bit.reset();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (BondIsClosure.isClosure(bond))
            {
                // BASIC APPROACH
                // pick begin atom at closure bond
                // this is really greedy, isn't it !;-)
                rootAtom = bond.getBeginIndex();
                isRoot[rootAtom] = true;

                // EXTENDED APPROACH
                if (avoidInnerRingAtoms)
                {
                    // count the number of neighbour ring atoms
                    nait = mol.getAtom(rootAtom).nbrAtomIterator();
                    ringNbrs = heavyNbrs = 0;

                    while (nait.hasNext())
                    {
                        nbrAtom = nait.nextNbrAtom();

                        if (!AtomIsHydrogen.isHydrogen(nbrAtom))
                        {
                            heavyNbrs++;

                            if (AtomInRing.isInRing(nbrAtom))
                            {
                                ringNbrs++;
                            }
                        }
                    }

                    // if this atom has more than 2 neighbour
                    // ring atoms, we could get trapped later
                    // when traversing the cycles, which
                    // can cause aromaticity false detection
                    newRoot = -1;

                    if (ringNbrs > 2)
                    {
                        //try to find an other root atom
                        for (int i = 0; i < sssRings.size(); i++)
                        {
                            ring = (Ring) sssRings.get(i);
                            ringIndices = ring.getAtomIndices();

                            //System.out.println("ring: "+i+" "+ring);
                            boolean checkThisRing = false;
                            int rootAtomNumber = 0;
                            int idx = 0;

                            // avoiding two root atoms in one ring !
                            for (int rootIdx = 0; rootIdx < tmpRootAtoms.size();
                                    rootIdx++)
                            {
                                idx = ((Integer) tmpRootAtoms.get(rootIdx))
                                    .intValue();

                                if (ring.isInRing(idx))
                                {
                                    rootAtomNumber++;

                                    if (rootAtomNumber >= 2)
                                    {
                                        break;
                                    }
                                }
                            }

                            if (rootAtomNumber < 2)
                            {
                                for (int ringAtomIdx = 0;
                                        ringAtomIdx < ringIndices.length;
                                        ringAtomIdx++)
                                {
                                    // find critical ring
                                    if (ringIndices[ringAtomIdx] == rootAtom)
                                    {
                                        checkThisRing = true;
                                    }
                                    else
                                    {
                                        // second root atom in this ring ?
                                        if (isRoot[ringIndices[ringAtomIdx]] ==
                                                true)
                                        {
                                            // when there is a second root
                                            // atom this ring can not be
                                            // used for getting an other
                                            // root atom
                                            checkThisRing = false;

                                            break;
                                        }
                                    }
                                }
                            }

                            // check ring for getting an other
                            // root atom to aromaticity typer avoid
                            // problems
                            if (checkThisRing)
                            {
                                // check if we can find another root
                                // atom
                                for (int ringAtomIdx = 0;
                                        ringAtomIdx < ringIndices.length;
                                        ringAtomIdx++)
                                {
                                    //System.out.println("ring "+i+"atom "+mol.getAtom(tmp[m]).getIdx());
                                    nait = mol.getAtom(ringIndices[ringAtomIdx])
                                              .nbrAtomIterator();
                                    ringNbrs = heavyNbrs = 0;

                                    while (nait.hasNext())
                                    {
                                        nbrAtom = nait.nextNbrAtom();

                                        if (!AtomIsHydrogen.isHydrogen(nbrAtom))
                                        {
                                            heavyNbrs++;

                                            if (AtomInRing.isInRing(nbrAtom))
                                            {
                                                ringNbrs++;
                                            }
                                        }
                                    }

                                    // if the number of neighboured heavy atoms is also
                                    // the number of neighboured ring atoms, the aromaticity
                                    // typer could be stuck in a local traversing trap
                                    if ((ringNbrs <= 2) &&
                                            ring.isInRing(
                                                mol.getAtom(
                                                    ringIndices[ringAtomIdx])
                                                .getIndex()))
                                    {
                                        newRoot = ringIndices[ringAtomIdx];
                                    }
                                }
                            }
                        }

                        if ((newRoot != -1) && (rootAtom != newRoot))
                        {
                            // unset root atom
                            isRoot[rootAtom] = false;

                            // pick new root atom
                            isRoot[newRoot] = true;
                        }
                        else
                        {
                            if (logger.isEnabledFor(Priority.WARN))
                            {
                                logger.warn("Root atom " + rootAtom +
                                    " could cause false aromaticity detection in " +
                                    mol.getTitle());
                            }
                        }
                    }
                }

                if (isRoot[rootAtom] == true)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Set root atom (simple) " + rootAtom);
                    }
                }
                else
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Set root atom (ext.) " + newRoot);
                    }
                }
            }
        }
    }

    /**
     * Traverse cycles to assign aromaticity flags to the atoms and bonds starting from a root atom.
     *
     * It's important that the root atoms are not trapped by further neighboured ring atoms.
     * See {@link #selectRootAtoms(Molecule, boolean}} for details.
     *
     * @param  root   the root atom from which we will start
     * @param  atom   the actual atom which will be checked
     * @param  prev   previous atom
     * @param  electrons     minimal and maximal number of electrons
     * @param  depth  depth of the search, e.g. 6 or 20 for typical aromatic systems
     * @return        <tt>true</tt> if the actual visited atom is aromatic
     *
     * @see #selectRootAtoms(Molecule, boolean}
     */
    private boolean traverseCycle(Atom root, Atom atom, Bond prev,
        BasicIntInt electrons, int depth, boolean[] aromAtoms,
        boolean[] aromBonds)
    {
        if (atom == root)
        {
            for (int elNum = electrons.intValue1; elNum <= electrons.intValue2;
                    elNum++)
            {
                if (((elNum % 4) == 2) && (elNum > 2))
                {
                    return true;
                }
            }

            return false;
        }

        if ((depth == 0) || !potentiallyAromatic[atom.getIndex()] ||
                isVisited[atom.getIndex()])
        {
            return false;
        }

        boolean result = false;
        depth--;
        electrons.intValue1 += (numberOfElectrons[atom.getIndex()]).intValue1;
        electrons.intValue2 += (numberOfElectrons[atom.getIndex()]).intValue2;
        isVisited[atom.getIndex()] = true;

        Atom nbr;
        Bond bond;
        NbrAtomIterator nait = atom.nbrAtomIterator();

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();
            bond = nait.actualBond();

            if ((bond != prev) && BondInRing.isInRing(bond) &&
                    potentiallyAromatic[nbr.getIndex()])
            {
                if (traverseCycle(root, nbr, bond, electrons, depth, aromAtoms,
                            aromBonds))
                {
                    result = true;
                    aromBonds[bond.getIndex()] = true;
                }
            }
        }

        isVisited[atom.getIndex()] = false;

        if (result)
        {
            aromAtoms[atom.getIndex() - 1] = true;
        }

        electrons.intValue1 -= (numberOfElectrons[atom.getIndex()]).intValue1;
        electrons.intValue2 -= (numberOfElectrons[atom.getIndex()]).intValue2;

        if (logger.isDebugEnabled())
        {
            System.out.println("electron contribution: " + atom.getIndex() +
                " electrons " + electrons + " elAtom:" +
                numberOfElectrons[atom.getIndex()] + " aromatic:" +
                aromAtoms[atom.getIndex() - 1]);
        }

        return result;
    }
    
    /**
     * @return Returns the useAromaticityModel.
     */
    public boolean isUseAromaticityModel() {
        return useAromaticityModel;
    }
    /**
     * @param useAromaticityModel The useAromaticityModel to set.
     */
    public void setUseAromaticityModel(boolean useAromaticityModel) {
        this.useAromaticityModel = useAromaticityModel;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
