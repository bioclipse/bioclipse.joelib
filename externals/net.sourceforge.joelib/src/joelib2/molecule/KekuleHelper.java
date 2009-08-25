///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: KekuleHelper.java,v $
//Purpose:  Atom representation.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.19 $
//        $Date: 2005/03/03 07:13:51 $
//        $Author: wegner $
//Original Author: ???, OpenEye Scientific Software
//Original Version: babel 2.0a1
//
//Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                       U.S.A., 1999,2000,2001
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                       Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                       2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.molecule;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomInAromaticSystem;
import joelib2.feature.types.atomlabel.AtomIsCarbon;
import joelib2.feature.types.atomlabel.AtomIsNitrogen;
import joelib2.feature.types.atomlabel.AtomIsOxygen;
import joelib2.feature.types.atomlabel.AtomIsSulfur;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;
import joelib2.feature.types.bondlabel.BondInRing;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BasicAtomIterator;
import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.NbrAtomIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
* Helper methods to kekulize molecules.
*
* @.author     wegnerj
* @.cite br90
* @.wikipedia Aromaticity
* @.wikipedia Friedrich August Kekulé von Stradonitz
* @.license GPL
* @.cvsversion    $Revision: 1.19 $, $Date: 2005/03/03 07:13:51 $
*/
public class KekuleHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.19 $";
    private static final String RELEASE_DATE = "$Date: 2005/03/03 07:13:51 $";
    private static Category logger = Category.getInstance(KekuleHelper.class
            .getName());
    public static final int KEKULE_SINGLE = 1;
    public static final int KEKULE_DOUBLE = 2;
    public static final int KEKULE_TRIPLE = 3;
    public static final int KEKULE_POTENTIAL_AROMATIC = BondHelper.AROMATIC_BO;
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AtomHeavyValence.class, AtomImplicitValence.class,
            AtomInAromaticSystem.class, AtomIsCarbon.class,
            AtomIsNitrogen.class, AtomIsOxygen.class, AtomIsSulfur.class,
            BondInRing.class, BasicSMARTSPatternMatcher.class
        };

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
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static boolean perceiveKekuleBonds(Molecule mol, int[] kekuleType)
    {
        if (((BasicConformerMolecule) mol).isOccuredKekulizationError())
        {
            return false;
        }

        // for simple carbon cycles, we can check at first for the
        // Koenig-Theorem
        // see Bonchev/Rouvray, Chemical Graph Theory: Introduction
        // and Fundamentals Gordon and Breach Science Publishers, 1990
        // This is not possible for Hetero-Rings, because we might have addtional
        // electrons at the hetero atoms.

        boolean[] aromaticBObonds = getBondsWithAromaticBO(mol, kekuleType);

        if (aromaticBObonds == null)
        {
            return true;
        }

        int[] maxValence = getMaximumValence(mol, aromaticBObonds);

        // ensure that aromatictiy is already assigned, because we are now
        // playing with bond orders
        // This will cause wrong aromaticity assignments !
        // What a recursive dependency mess !!!
        if (mol.getAtomsSize() > 0)
        {
            AtomHybridisation.getIntValue(mol.getAtom(1));
            AtomImplicitValence.getImplicitValence(mol.getAtom(1));
            AtomInAromaticSystem.isValue(mol.getAtom(1));
        }

        boolean result = checkAllConjugatedSystems(mol, kekuleType, maxValence);

        if (!result)
        {
            logger.warn("Unable to to kekulize molecule " + mol.getTitle() +
                " (#atoms=" + mol.getAtomsSize() + ")");

            if (mol instanceof BasicConformerMolecule)
            {
                ((BasicConformerMolecule) mol).setOccuredKekulizationError(
                    true);
            }
        }

        //  Now delete wrongly detected aromaticity, because
        // it was estimated before getting the kekule mode
        // What a recursive dependency mess !!!
        mol.deleteData(AtomInAromaticSystem.getName());
        mol.deleteData(BondInAromaticSystem.getName());

        if (mol.getAtomsSize() > 0)
        {
            AtomInAromaticSystem.isValue(mol.getAtom(1));
        }

        return result;
    }

    private static int atomValenceSum(Atom atom, int[] kekuleType)
    {
        int count = AtomImplicitValence.getImplicitValence(atom);
        Bond bond;
        BondIterator bit = atom.bondIterator();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (kekuleType[bond.getIndex()] == KekuleHelper.KEKULE_DOUBLE)
            {
                count++;
            }
        }

        return count;
    }

    /**
     * @param mol
     * @param kekuleType
     * @param aromaticBObonds
     * @param maxValence
     * @return
     */
    private static boolean checkAllConjugatedSystems(Molecule mol,
        int[] kekuleType, int[] maxValence)
    {
        boolean result = true;
        boolean[] used = new boolean[mol.getAtomsSize() + 1];
        Vector<Atom> conjugatedSystem = new Vector<Atom>();
        Vector<Atom> curr = new Vector<Atom>();
        Vector<Atom> next = new Vector<Atom>();
        AtomIterator ait = mol.atomIterator();
        Atom atom;
        Atom nbr;
        int index = 0;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (hasNextAromaticBObond(atom) && !used[atom.getIndex()])
            {
                conjugatedSystem.clear();
                curr.clear();
                conjugatedSystem.add(atom);
                curr.add(atom);
                used[atom.getIndex()] = true;

                while (curr.size() != 0)
                {
                    next.clear();

                    for (int k = 0; k < curr.size(); k++)
                    {
                        Atom tmp = (Atom) curr.get(k);
                        NbrAtomIterator nait = tmp.nbrAtomIterator();

                        while (nait.hasNext())
                        {
                            nbr = nait.nextNbrAtom();

                            if (hasNextAromaticBObond(nbr) &&
                                    !used[nbr.getIndex()])
                            {
                                used[nbr.getIndex()] = true;
                                next.add(nbr);
                                conjugatedSystem.add(nbr);
                            }
                        }
                    }

                    curr.clear();

                    for (int nn = 0; nn < next.size(); nn++)
                    {
                        curr.add(next.get(nn));
                    }
                }

                if (logger.isDebugEnabled())
                {
                    StringBuffer buffer = new StringBuffer(50);

                    for (int i = 0; i < conjugatedSystem.size(); i++)
                    {
                        buffer.append(conjugatedSystem.get(i).getIndex());
                        buffer.append(' ');
                    }

                    logger.debug("conjugated atoms (round " + index + "): " +
                        buffer.toString());
                }

                //try it first without protonating aromatic nitrogens
                boolean noProtSuccess = expandKekule(mol, conjugatedSystem,
                        new BasicAtomIterator(conjugatedSystem), maxValence,
                        false, kekuleType, 0);

                if (noProtSuccess)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Deprotonated kekulization successfull: " +
                            noProtSuccess);
                        //break;
                    }
                }

                if (!noProtSuccess)
                {
                    if (!expandKekule(mol, conjugatedSystem,
                                new BasicAtomIterator(conjugatedSystem),
                                maxValence, true, kekuleType, 0))
                    {
                        result = false;
                    }
                }

                //                for (int i = 0; i < kekuleType.length; i++) {
                //                    System.out.println("kekulization ("+(index++)+"): kekuleType "+i+" "+kekuleType[i]);
                //                }
                index++;
            }
        }

        return result;
    }

    /**
     * @param atom
     * @param mol
     * @param conjugatedSystem
     * @param clonedAtomIter
     * @param maxValence
     * @param protonateNOS
     * @param kekuleType
     * @return
     */
    private static boolean checkIfChargedAtom(Atom atom, Molecule mol,
        List<Atom> conjugatedSystem, BasicAtomIterator clonedAtomIter,
        int[] maxValence, boolean protonateNOS, int[] kekuleType,
        List<Bond> aromaticBObonds, int depth)
    {
        boolean trycharge = false;

        if (protonateNOS && (atom.getFormalCharge() == 0))
        {
            if (AtomIsNitrogen.isNitrogen(atom) &&
                    (AtomHeavyValence.valence(atom) == 3))
            {
                trycharge = true;
            }

            if (AtomIsOxygen.isOxygen(atom) &&
                    (AtomHeavyValence.valence(atom) == 2))
            {
                trycharge = true;
            }

            if (AtomIsSulfur.isSulfur(atom) &&
                    (AtomHeavyValence.valence(atom) == 2))
            {
                trycharge = true;
            }
        }

        if (trycharge)
        {
            Bond bond;
            Atom nbr;
            maxValence[atom.getIndex()]++;
            atom.setFormalCharge(1);

            for (int j = 0; j < aromaticBObonds.size(); j++)
            {
                bond = (Bond) aromaticBObonds.get(j);
                nbr = bond.getNeighbor(atom);

                if (getCurrentValence(nbr, kekuleType) <=
                        maxValence[nbr.getIndex()])
                {
                    kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_DOUBLE;
                    bond.setBondOrder(2);
                    clonedAtomIter.incrementIndex();

                    if (expandKekule(mol, conjugatedSystem, clonedAtomIter,
                                maxValence, protonateNOS, kekuleType, depth++))
                    {
                        return true;
                    }

                    clonedAtomIter.decrementIndex();
                    kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_SINGLE;
                    bond.setBondOrder(1);
                }
            }

            maxValence[atom.getIndex()]--;
            atom.setFormalCharge(0);
        }

        return false;
    }

    /**
     *
     */
    private static void correctAmidineAndGuanidine(Molecule mol,
        int[] kekuleType)
    {
        Bond b1;
        Bond b2;
        Atom a1;
        Atom a2;
        Atom a3;
        List mlist;
        int[] itmp;

        //amidene and guanidine
        SMARTSPatternMatcher amidene = new BasicSMARTSPatternMatcher();
        amidene.init("[nD1]c([nD1])*");

        if (amidene.match(mol))
        {
            mlist = amidene.getMatchesUnique();

            for (int i = 0; i < mlist.size(); i++)
            {
                itmp = (int[]) mlist.get(i);
                a1 = mol.getAtom(itmp[0]);
                a2 = mol.getAtom(itmp[1]);
                a3 = mol.getAtom(itmp[2]);

                b1 = a2.getBond(a1);
                b2 = a2.getBond(a3);

                if ((b1 == null) || (b2 == null))
                {
                    continue;
                }

                kekuleType[b1.getIndex()] = KekuleHelper.KEKULE_DOUBLE;
                kekuleType[b2.getIndex()] = KekuleHelper.KEKULE_SINGLE;
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     */
    private static void correctBadResonanceForm(Molecule mol, int[] kekuleType)
    {
        correctCarboxylicAcid(mol, kekuleType);
        correctPhosphonicAcid(mol, kekuleType);
        correctAmidineAndGuanidine(mol, kekuleType);
    }

    /**
     * @param mol
     * @param kekuleType
     */
    private static void correctCarboxylicAcid(Molecule mol, int[] kekuleType)
    {
        Bond b1;
        Bond b2;
        Atom a1;
        Atom a2;
        Atom a3;
        List mlist;
        int[] itmp;

        SMARTSPatternMatcher acid = new BasicSMARTSPatternMatcher();
        acid.init("[oD1]c[oD1]");

        if (acid.match(mol))
        {
            mlist = acid.getMatchesUnique();

            for (int i = 0; i < mlist.size(); i++)
            {
                itmp = (int[]) mlist.get(i);
                a1 = mol.getAtom(itmp[0]);
                a2 = mol.getAtom(itmp[1]);
                a3 = mol.getAtom(itmp[2]);
                b1 = a2.getBond(a1);
                b2 = a2.getBond(a3);

                if ((b1 == null) || (b2 == null))
                {
                    continue;
                }

                kekuleType[b1.getIndex()] = KekuleHelper.KEKULE_DOUBLE;
                kekuleType[b2.getIndex()] = KekuleHelper.KEKULE_SINGLE;
            }
        }
    }

    /**
     * @param mol
     * @param kekuleType
     */
    private static void correctPhosphonicAcid(Molecule mol, int[] kekuleType)
    {
        Bond b1;
        Bond b2;
        Bond b3;
        Atom a1;
        Atom a2;
        Atom a3;
        Atom a4;
        List mlist;
        int[] itmp;

        //phosphonic acid
        SMARTSPatternMatcher phosphate = new BasicSMARTSPatternMatcher();
        phosphate.init("[p]([oD1])([oD1])([oD1])[#6,#8]");

        if (phosphate.match(mol))
        {
            mlist = phosphate.getMatchesUnique();

            for (int i = 0; i < mlist.size(); i++)
            {
                itmp = (int[]) mlist.get(i);
                a1 = mol.getAtom(itmp[0]);
                a2 = mol.getAtom(itmp[1]);
                a3 = mol.getAtom(itmp[2]);
                a4 = mol.getAtom(itmp[3]);
                b1 = a1.getBond(a2);
                b2 = a1.getBond(a3);
                b3 = a1.getBond(a4);

                if ((b1 == null) || (b2 == null) || (b3 == null))
                {
                    continue;
                }

                kekuleType[b1.getIndex()] = KekuleHelper.KEKULE_DOUBLE;
                kekuleType[b2.getIndex()] = KekuleHelper.KEKULE_SINGLE;
                kekuleType[b3.getIndex()] = KekuleHelper.KEKULE_SINGLE;
            }
        }
    }

    /**
     * @param  conjugatedSystem          {@link java.util.Vector} of <tt>Atom</tt>
     * @param  mol         Description of the Parameter
     * @param  i           Description of the Parameter
     * @param  maxValence        Description of the Parameter
     * @param  protonateNOS  Description of the Parameter
     * @return             Description of the Return Value
     */
    private static boolean expandKekule(Molecule mol,
        List<Atom> conjugatedSystem, BasicAtomIterator atomIter,
        int[] maxValence, boolean protonateNOS, int[] kekuleType, int depth)
    {
        boolean kekulizationFinished = false;
        BasicAtomIterator clonedAtomIter = (BasicAtomIterator) atomIter.clone();

        // are more atoms in the conjugated system available?
        if (!clonedAtomIter.hasNext())
        {
            kekulizationFinished = idealValenceAchieved(conjugatedSystem,
                    maxValence, kekuleType);
        }
        else
        {
            Atom atom = (Atom) clonedAtomIter.actual();

            if (hasNextAromaticBObond(atom) == false)
            {
                clonedAtomIter.incrementIndex();

                return (expandKekule(mol, conjugatedSystem, clonedAtomIter,
                            maxValence, protonateNOS, kekuleType, depth++));
            }
            else
            {
                //store list of attached aromatic atoms
                List<Bond> aromaticBObonds = new Vector<Bond>();
                getAromaticBObonds(aromaticBObonds, atom, kekuleType);

                int currentValence = getCurrentValence(atom, kekuleType);
                boolean fullValence = (currentValence >=
                        maxValence[atom.getIndex()]);

                if (logger.isDebugEnabled())
                {
                    logger.debug("Depth " + depth + ": Atom " +
                        atom.getIndex() + " has full valence " + fullValence +
                        " (current>=maximum valence) (" + currentValence +
                        ">=" + maxValence[atom.getIndex()] + ")");
                }

                //System.out.println("atom="+atom.getIndex()+"fullValence="+fullValence+" current="+getCurrentValence(atom, kekuleType)+" maxValence="+maxValence[atom.getIndex()]);
                if (fullValence)
                {
                    clonedAtomIter.incrementIndex();

                    if (expandKekule(mol, conjugatedSystem, clonedAtomIter,
                                maxValence, protonateNOS, kekuleType, depth++))
                    {
                        kekulizationFinished = true;
                    }

                    if (!kekulizationFinished)
                    {
                        clonedAtomIter.decrementIndex();

                        if (checkIfChargedAtom(atom, mol, conjugatedSystem,
                                    clonedAtomIter, maxValence, protonateNOS,
                                    kekuleType, aromaticBObonds, depth++))
                        {
                            kekulizationFinished = true;
                        }

                        if (!kekulizationFinished)
                        {
                            if (protonateNOS)
                            {
                                if (protonateNOS(atom, mol, conjugatedSystem,
                                            clonedAtomIter, maxValence,
                                            kekuleType, depth))
                                {
                                    kekulizationFinished = true;
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (setConjugatedKekuleBO(atom, mol, conjugatedSystem,
                                clonedAtomIter, maxValence, protonateNOS,
                                kekuleType, aromaticBObonds, depth))
                    {
                        kekulizationFinished = true;
                    }

                    if (!kekulizationFinished)
                    {
                        if (protonateNOS)
                        {
                            if (protonateNOS(atom, mol, conjugatedSystem,
                                        clonedAtomIter, maxValence, kekuleType,
                                        depth))
                            {
                                kekulizationFinished = true;
                            }
                        }
                    }
                }

                if (!kekulizationFinished)
                {
                    //failed to find a valid solution - reset attached bonds
                    resetAromaticBObonds(aromaticBObonds, kekuleType);
                }
            }
        }

        return kekulizationFinished;
    }

    /**
     * @param aromaticBObonds
     */
    private static void getAromaticBObonds(List<Bond> aromaticBObonds,
        Atom atom, int[] kekuleType)
    {
        //store list of attached aromatic atoms
        Bond bond;
        NbrAtomIterator nait = atom.nbrAtomIterator();
        StringBuffer buffer = null;

        while (nait.hasNext())
        {
            nait.nextNbrAtom();
            bond = nait.actualBond();

            if (bond.getBondOrder() == BondHelper.AROMATIC_BO)
            {
                aromaticBObonds.add(bond);
                bond.setBondOrder(1);
                kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_SINGLE;

                if (logger.isDebugEnabled())
                {
                    if (buffer == null)
                    {
                        buffer = new StringBuffer(20);
                    }

                    buffer.append('(');
                    buffer.append(bond.getBeginIndex());
                    buffer.append(',');
                    buffer.append(bond.getEndIndex());
                    buffer.append(')');
                    buffer.append(' ');
                }
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Bonds with aromatic BOND_ORDER: " +
                buffer.toString());
        }
    }

    /**
     * @param mol
     * @param kekuleType
     * @return
     */
    private static boolean[] getBondsWithAromaticBO(Molecule mol,
        int[] kekuleType)
    {
        Bond bond;
        boolean done = true;
        boolean badResonanceForm = false;
        boolean[] varo = new boolean[mol.getAtomsSize() + 1];
        BondIterator bit = mol.bondIterator();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            switch (bond.getBondOrder())
            {
            case 2:
                kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_DOUBLE;

                break;

            case 3:
                kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_TRIPLE;

                break;

            case BondHelper.AROMATIC_BO:
                kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_SINGLE;

                if (BondInRing.isInRing(bond))
                {
                    varo[bond.getBeginIndex()] = true;
                    varo[bond.getEndIndex()] = true;
                    done = false;
                }
                else
                {
                    badResonanceForm = true;
                }

                break;

            default:
                kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_SINGLE;

                break;
            }
        }

        if (logger.isDebugEnabled())
        {
            for (int i = 1; i < varo.length; i++)
            {
                if (varo[i])
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("kekulization: aromatic atom " + i);
                    }
                }
            }
        }

        if (badResonanceForm)
        {
            correctBadResonanceForm(mol, kekuleType);

            if (logger.isDebugEnabled())
            {
                for (int i = 0; i < kekuleType.length; i++)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("kekulization: corrected bad resonance " +
                            kekuleType[i] + " for bond " + i);
                    }
                }
            }
        }

        if (done)
        {
            return null;
        }
        else
        {
            return varo;
        }
    }

    /**
     *  Gets the currentValence attribute of the JHM class
     *
     * @param  atom  Description of the Parameter
     * @return       The currentValence value
     */
    private static int getCurrentValence(Atom atom, int[] kekuleType)
    {
        int count = AtomImplicitValence.getImplicitValence(atom);
        Bond bond;
        BondIterator bit = atom.bondIterator();
        StringBuffer buffer = null;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (kekuleType[bond.getIndex()] == KekuleHelper.KEKULE_DOUBLE)
            {
                count++;
            }
            else if (kekuleType[bond.getIndex()] == KekuleHelper.KEKULE_TRIPLE)
            {
                count += 2;
            }

            if (logger.isDebugEnabled())
            {
                if (buffer == null)
                {
                    buffer = new StringBuffer(50);
                }

                buffer.append('(');
                buffer.append(bond.getBeginIndex());
                buffer.append(',');
                buffer.append(bond.getEndIndex());
                buffer.append(')');
                buffer.append('=');
                buffer.append(kekuleType[bond.getIndex()]);
                buffer.append(' ');
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Atom " + atom.getIndex() + " has implicite valence " +
                AtomImplicitValence.getImplicitValence(atom) +
                ", current kekule valence is " + count + " with kekule bonds " +
                buffer);
        }

        return (count);
    }

    /**
     * @param mol
     * @param aromaticBObonds
     * @return
     */
    private static int[] getMaximumValence(Molecule mol,
        boolean[] aromaticBObonds)
    {
        Atom atom;
        Atom nbr;
        int[] maxv = new int[mol.getAtomsSize() + 1];
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (aromaticBObonds[atom.getIndex()])
            {
                switch (atom.getAtomicNumber())
                {
                case 6:
                    maxv[atom.getIndex()] = 4;

                    break;

                case 8:
                case 16:
                case 34:
                case 52:
                    maxv[atom.getIndex()] = 2;

                    break;

                case 7:
                case 15:
                case 33:
                    maxv[atom.getIndex()] = 3;

                    break;
                }

                //correct valence for formal charges
                if (AtomIsCarbon.isCarbon(atom))
                {
                    maxv[atom.getIndex()] -= Math.abs(atom.getFormalCharge());
                }
                else
                {
                    maxv[atom.getIndex()] += atom.getFormalCharge();
                }

                if (AtomIsNitrogen.isNitrogen(atom) ||
                        AtomIsSulfur.isSulfur(atom))
                {
                    NbrAtomIterator nait = atom.nbrAtomIterator();

                    while (nait.hasNext())
                    {
                        nbr = nait.nextNbrAtom();

                        if (AtomIsOxygen.isOxygen(nbr) &&
                                (nait.actualBond().getBondOrder() == 2))
                        {
                            maxv[atom.getIndex()] += 2;
                        }
                    }
                }
            }
        }

        //        if(logger.isDebugEnabled()){
        //            for (int i = 1; i < maxv.length; i++) {
        //                   logger.debug("maximum valence of atom "+i+" is "+maxv[i]);
        //           }
        //        }
        return maxv;
    }

    /**
     * @return
     */
    private static boolean hasNextAromaticBObond(Atom atom)
    {
        //jump to next atom in list if current atom doesn't have any attached
        //aromatic bonds
        Bond bond;
        BondIterator bit = atom.bondIterator();
        boolean found = false;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (bond.getBondOrder() == BondHelper.AROMATIC_BO)
            {
                found = true;

                break;
            }
        }

        //if(logger.isDebugEnabled())logger.debug("Atom "+atom.getIndex()+" has bond with AROMATIC_BO "+found);

        return found;
    }

    /**
     * @param conjugatedSystem
     * @return
     */
    private static boolean idealValenceAchieved(List<Atom> conjugatedSystem,
        int[] maxValence, int[] kekuleType)
    {
        //check to see that the ideal valence has been achieved for all atoms
        Atom atom;
        BasicAtomIterator ait = new BasicAtomIterator(conjugatedSystem);

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            //let erroneously aromatic carboxylates pass
            if (AtomIsOxygen.isOxygen(atom) && (atom.getValence() == 1))
            {
                continue;
            }

            if (getCurrentValence(atom, kekuleType) !=
                    maxValence[atom.getIndex()])
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Ideal valence not achieved for atom " +
                            atom.getIndex());
                }
                return false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Ideal valence achieved.");
        }

        return true;
    }

    /**
     * @param  visit  {@link java.util.Vector} of <tt>int[0]</tt>
     * @param  ival   {@link java.util.Vector} of <tt>int[0]</tt>
     * @param  atom   Description of the Parameter
     * @param  depth  Description of the Parameter
     * @return        Description of the Return Value
     */
    private static boolean kekulePropagate(Atom atom, List visit, List ival,
        int depth, int[] kekuleType)
    {
        int count = 0;
        Bond bond;
        BondIterator bit = atom.bondIterator();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (((int[]) visit.get(bond.getIndex()))[0] == 0)
            {
                count++;
            }
        }

        if (count == 0)
        {
            return (atomValenceSum(atom, kekuleType) ==
                    ((int[]) ival.get(atom.getIndex()))[0]);
        }

        boolean result = true;
        Atom nbr;
        NbrAtomIterator nait = atom.nbrAtomIterator();

        //        System.out.println("kekule: atomvalencesum "+atomValenceSum(atom,kekuleType)+">="+((int[]) ival.get(               atom.getIndex()))[0]);
        if (atomValenceSum(atom, kekuleType) >=
                ((int[]) ival.get(atom.getIndex()))[0])
        {
            while (nait.hasNext())
            {
                nbr = nait.nextNbrAtom();
                bond = nait.actualBond();

                if (AtomInAromaticSystem.isValue(nbr) &&
                        (((int[]) visit.get(bond.getIndex()))[0] == 0))
                {
                    ((int[]) visit.get(bond.getIndex()))[0] = depth;
                    kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_SINGLE;
                    result = kekulePropagate(nbr, visit, ival, depth,
                            kekuleType);

                    if (result)
                    {
                        break;
                    }
                }
            }
        }
        else if (count == 1)
        {
            while (nait.hasNext())
            {
                nbr = nait.nextNbrAtom();
                bond = nait.actualBond();

                if (AtomInAromaticSystem.isValue(nbr) &&
                        (((int[]) visit.get(bond.getIndex()))[0] == 0))
                {
                    ((int[]) visit.get(bond.getIndex()))[0] = depth;
                    kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_DOUBLE;
                    result = kekulePropagate(nbr, visit, ival, depth,
                            kekuleType);

                    //break;
                    if (result)
                    {
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * @param atom
     * @param mol
     * @param conjugatedSystem
     * @param clonedAtomIter
     * @param maxValence
     * @param protonateNOS
     * @param kekuleType
     * @return
     */
    private static boolean protonateNOS(Atom atom, Molecule mol,
        List<Atom> conjugatedSystem, BasicAtomIterator clonedAtomIter,
        int[] maxValence, int[] kekuleType, int depth)
    {
        boolean protonateNOS = true;

        if (AtomIsNitrogen.isNitrogen(atom) && (atom.getFormalCharge() == 0) &&
                (AtomImplicitValence.getImplicitValence(atom) == 2))
        {
            //try protonating the nitrogen
            AtomImplicitValence.incrementImplicitValence(atom);
            clonedAtomIter.incrementIndex();

            if (expandKekule(mol, conjugatedSystem, clonedAtomIter, maxValence,
                        protonateNOS, kekuleType, depth++))
            {
                return true;
            }

            clonedAtomIter.decrementIndex();
            AtomImplicitValence.decrementImplicitValence(atom);
        }

        return false;
    }

    /**
     * @param aromaticBObonds
     */
    private static void resetAromaticBObonds(List<Bond> aromaticBObonds,
        int[] kekuleType)
    {
        //failed to find a valid solution - reset attached bonds
        Bond bond;

        for (int j = 0; j < aromaticBObonds.size(); j++)
        {
            bond = (Bond) aromaticBObonds.get(j);
            kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_POTENTIAL_AROMATIC;
            bond.setBondOrder(BondHelper.AROMATIC_BO);
        }
    }

    /**
     * @param atom
     * @param mol
     * @param conjugatedSystem
     * @param clonedAtomIter
     * @param maxValence
     * @param protonateNOS
     * @param kekuleType
     * @param aromaticBObonds
     * @return
     */
    private static boolean setConjugatedKekuleBO(Atom atom, Molecule mol,
        List<Atom> conjugatedSystem, BasicAtomIterator clonedAtomIter,
        int[] maxValence, boolean protonateNOS, int[] kekuleType,
        List aromaticBObonds, int depth)
    {
        Bond bond;
        Atom nbr;

        for (int j = 0; j < aromaticBObonds.size(); j++)
        {
            bond = (Bond) aromaticBObonds.get(j);
            nbr = bond.getNeighbor(atom);

            //                System.out.println("getCurrentValence(nbr) < maxv[nbr.getIndex()]-->"+getCurrentValence(nbr,kekuleType)+"<"+maxv[nbr.getIndex()]);
            if (getCurrentValence(nbr, kekuleType) <=
                    maxValence[nbr.getIndex()])
            {
                kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_DOUBLE;
                bond.setBondOrder(2);
                clonedAtomIter.incrementIndex();

                if (expandKekule(mol, conjugatedSystem, clonedAtomIter,
                            maxValence, protonateNOS, kekuleType, depth++))
                {
                    return true;
                }

                clonedAtomIter.decrementIndex();
                kekuleType[bond.getIndex()] = KekuleHelper.KEKULE_SINGLE;
                bond.setBondOrder(1);
            }
        }

        return false;
    }
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
